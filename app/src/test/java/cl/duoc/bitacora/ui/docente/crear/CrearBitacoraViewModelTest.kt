package cl.duoc.bitacora.ui.docente.crear

import cl.duoc.bitacora.data.AsignacionDetalle
import cl.duoc.bitacora.data.BitacoraDetalle
import cl.duoc.bitacora.data.BitacoraRepository
import cl.duoc.bitacora.data.UsuarioRepository
import cl.duoc.bitacora.data.remote.ErrorServidor
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.model.Perfil
import cl.duoc.bitacora.model.PreguntaOrientadora
import cl.duoc.bitacora.model.Usuario
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.TipoMensaje
import cl.duoc.bitacora.util.primerDiaParaCalendario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Pruebas del formulario para crear y editar bitácoras.
// Los dos repositorios se SIMULAN con MockK: sin base de datos ni backend.
@OptIn(ExperimentalCoroutinesApi::class)
class CrearBitacoraViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    private lateinit var bitacoraRepository: BitacoraRepository
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var viewModel: CrearBitacoraViewModel
    private var recolector: Job? = null

    // Las asignaciones del docente 1 (Christian): 5°A Matemática y 5°A Lenguaje
    private val asignacionesDelDocente = listOf(
        AsignacionDetalle(id = 1, cursoId = 1, curso = "5°A", asignaturaId = 1, asignatura = "Matemática"),
        AsignacionDetalle(id = 2, cursoId = 1, curso = "5°A", asignaturaId = 2, asignatura = "Lenguaje")
    )

    @Before
    fun preparar() {
        Dispatchers.setMain(dispatcher)
        Sesion.cerrar()
        Sesion.usuario = Usuario(1, "Profesor Christian Acuña", "docente1@escuela.cl", "", Perfil.DOCENTE)
        bitacoraRepository = mockk()
        usuarioRepository = mockk()
        every { usuarioRepository.obtenerAsignaciones(1) } returns flowOf(asignacionesDelDocente)
    }

    @After
    fun finalizar() {
        recolector?.cancel()
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    private fun esperar() = dispatcher.scheduler.advanceUntilIdle()

    // Crea el ViewModel. Va en una función (y no en @Before) porque el modo crear/editar
    // se lee de Sesion al construirlo, y algunas pruebas lo cambian antes.
    private fun crearViewModel() {
        viewModel = CrearBitacoraViewModel(mockk(relaxed = true), bitacoraRepository, usuarioRepository)
        // "asignaciones" solo se carga mientras alguien la observa (como la pantalla):
        // aquí se observa para que guardar() pueda revisar las asignaciones del docente
        recolector = CoroutineScope(dispatcher).launch { viewModel.asignaciones.collect {} }
        esperar()
    }

    // Llena el formulario con datos válidos: 5°A Lenguaje, diciembre 2026
    private fun llenarFormularioValido() {
        viewModel.cambiarAsignacion(2)
        viewModel.cambiarTitulo("  Bitácora de Diciembre - Lenguaje  ")
        viewModel.cambiarProposito("Registrar lo aprendido en Lenguaje.")
        viewModel.cambiarInstrucciones("Escribe después de cada clase.")
        viewModel.cambiarPreguntas("¿Qué leímos?\n\n   ¿Qué palabra nueva aprendiste?  ")
        viewModel.elegirMesAnio(primerDiaParaCalendario(12, 2026))
    }

    // ================= Modo crear =================

    @Test
    fun estadoInicial_modoCrearConCamposVaciosYSinErrores() {
        crearViewModel()
        val estado = viewModel.uiState.value

        assertFalse(estado.modoEdicion)
        assertEquals("", estado.titulo)
        assertEquals("", estado.mes)
        assertFalse(estado.errorTitulo)
        assertFalse(estado.guardada)
        assertEquals(asignacionesDelDocente, viewModel.asignaciones.value)
    }

    @Test
    fun elegirMesAnio_tomaElMesYAnioDelCalendario() {
        crearViewModel()

        viewModel.elegirMesAnio(primerDiaParaCalendario(12, 2026))

        assertEquals("12", viewModel.uiState.value.mes)
        assertEquals("2026", viewModel.uiState.value.anio)
    }

    @Test
    fun guardar_formularioVacio_marcaTodosLosErroresYNoGuarda() {
        crearViewModel()

        viewModel.guardar()
        esperar()

        val estado = viewModel.uiState.value
        assertTrue(estado.errorAsignacion)
        assertTrue(estado.errorTitulo)
        assertTrue(estado.errorProposito)
        assertTrue(estado.errorInstrucciones)
        assertTrue(estado.errorPreguntas)
        assertTrue(estado.errorMes)
        assertTrue(estado.errorAnio)
        assertFalse(estado.guardada)
        coVerify(exactly = 0) { bitacoraRepository.guardar(any(), any()) }
    }

    @Test
    fun corregirLosCampos_quitaLosErroresApenasSonValidos() {
        crearViewModel()
        viewModel.guardar()                       // todos los campos quedan con error

        viewModel.cambiarTitulo("Mate")            // 4 letras: sigue con error
        assertTrue(viewModel.uiState.value.errorTitulo)

        viewModel.cambiarTitulo("Matemática")      // ya es válido
        viewModel.cambiarAsignacion(1)
        viewModel.cambiarProposito("Propósito")
        viewModel.cambiarInstrucciones("Instrucciones")
        viewModel.cambiarPreguntas("¿Qué vimos?")
        viewModel.elegirMesAnio(primerDiaParaCalendario(12, 2026))

        val estado = viewModel.uiState.value
        assertFalse(estado.errorTitulo)
        assertFalse(estado.errorAsignacion)
        assertFalse(estado.errorProposito)
        assertFalse(estado.errorInstrucciones)
        assertFalse(estado.errorPreguntas)
        assertFalse(estado.errorMes)
        assertFalse(estado.errorAnio)
    }

    @Test
    fun guardar_asignacionQueNoEsDelDocente_marcaErrorYNoGuarda() {
        crearViewModel()
        llenarFormularioValido()
        viewModel.cambiarAsignacion(3)             // 6°B Historia es de Luis, no de Christian

        viewModel.guardar()
        esperar()

        assertTrue(viewModel.uiState.value.errorAsignacion)
        coVerify(exactly = 0) { bitacoraRepository.guardar(any(), any()) }
    }

    @Test
    fun guardar_bitacoraDuplicada_marcaErrorYNoGuarda() {
        crearViewModel()
        coEvery { bitacoraRepository.existeDuplicada(2, 12, 2026, 0) } returns true
        llenarFormularioValido()

        viewModel.guardar()
        esperar()

        assertTrue(viewModel.uiState.value.errorDuplicada)
        assertFalse(viewModel.uiState.value.guardada)
        coVerify(exactly = 0) { bitacoraRepository.guardar(any(), any()) }
    }

    @Test
    fun guardar_formularioValido_creaBorradorConTextosYPreguntasLimpios() {
        crearViewModel()
        coEvery { bitacoraRepository.existeDuplicada(2, 12, 2026, 0) } returns false
        coEvery { bitacoraRepository.guardar(any(), any()) } returns Unit
        llenarFormularioValido()

        viewModel.guardar()
        esperar()

        // Se guarda en BORRADOR, sin espacios sobrantes y una pregunta por línea (sin líneas vacías)
        coVerify {
            bitacoraRepository.guardar(
                match {
                    it.asignacionId == 2 && it.titulo == "Bitácora de Diciembre - Lenguaje" &&
                            it.mes == 12 && it.anio == 2026 && it.estado == EstadoBitacora.BORRADOR
                },
                listOf("¿Qué leímos?", "¿Qué palabra nueva aprendiste?")
            )
        }
        assertTrue(viewModel.uiState.value.guardada)
        assertEquals(TipoMensaje.EXITO, Sesion.tipoMensaje)
        assertEquals("Bitácora creada como borrador. Ábrela para publicarla.", Sesion.mensaje)
    }

    @Test
    fun guardar_backendRechaza_muestraElMensajeDelBackend() {
        crearViewModel()
        coEvery { bitacoraRepository.existeDuplicada(any(), any(), any(), any()) } returns false
        coEvery { bitacoraRepository.guardar(any(), any()) } throws
                ErrorServidor("Ya existe una bitácora de este mes para el curso y asignatura elegidos")
        llenarFormularioValido()

        viewModel.guardar()
        esperar()

        assertFalse(viewModel.uiState.value.guardada)
        assertEquals(TipoMensaje.ERROR, Sesion.tipoMensaje)
        assertEquals("Ya existe una bitácora de este mes para el curso y asignatura elegidos", Sesion.mensaje)
    }

    // ================= Modo editar =================

    private fun bitacoraDetalle(estado: String) = BitacoraDetalle(
        id = 3, asignacionId = 2, titulo = "Bitácora de Noviembre - Lenguaje",
        proposito = "Registrar lo aprendido", instrucciones = "Escribe después de cada clase",
        mes = 11, anio = 2026, estado = estado, fechaPublicacion = "", fechaCierre = "",
        docenteId = 1, cursoId = 1, curso = "5°A", asignatura = "Lenguaje"
    )

    private fun activarModoEdicion() {
        Sesion.modoEdicion = true
        Sesion.bitacoraId = 3
    }

    @Test
    fun modoEdicion_borrador_cargaLosDatosEnElFormulario() {
        activarModoEdicion()
        every { bitacoraRepository.obtenerParaDocente(3, 1) } returns
                flowOf(listOf(bitacoraDetalle(EstadoBitacora.BORRADOR)))
        coEvery { bitacoraRepository.obtenerPreguntasUnaVez(3) } returns listOf(
            PreguntaOrientadora(1, 3, 1, "¿Qué leímos?"),
            PreguntaOrientadora(2, 3, 2, "¿Qué palabra nueva aprendiste?")
        )

        crearViewModel()

        val estado = viewModel.uiState.value
        assertTrue(estado.modoEdicion)
        assertEquals(2, estado.asignacionId)
        assertEquals("Bitácora de Noviembre - Lenguaje", estado.titulo)
        assertEquals("¿Qué leímos?\n¿Qué palabra nueva aprendiste?", estado.preguntas)
        assertEquals("11", estado.mes)
        assertEquals("2026", estado.anio)
    }

    @Test
    fun modoEdicion_bitacoraYaPublicada_noSePuedeEditar() {
        activarModoEdicion()
        every { bitacoraRepository.obtenerParaDocente(3, 1) } returns
                flowOf(listOf(bitacoraDetalle(EstadoBitacora.PUBLICADA)))

        crearViewModel()

        assertTrue(viewModel.uiState.value.guardada)     // la pantalla vuelve atrás
        assertEquals("Esta bitácora ya no se puede editar.", Sesion.mensaje)
    }

    @Test
    fun modoEdicion_errorAlCargar_muestraMensajeYVuelveAtras() {
        activarModoEdicion()
        every { bitacoraRepository.obtenerParaDocente(3, 1) } throws RuntimeException("Room no responde")

        crearViewModel()

        assertTrue(viewModel.uiState.value.guardada)
        assertEquals("No se pudo cargar la bitácora.", Sesion.mensaje)
    }

    @Test
    fun modoEdicion_guardar_editaLaMismaBitacoraExcluyendolaDeLaRevisionDeDuplicadas() {
        activarModoEdicion()
        every { bitacoraRepository.obtenerParaDocente(3, 1) } returns
                flowOf(listOf(bitacoraDetalle(EstadoBitacora.BORRADOR)))
        coEvery { bitacoraRepository.obtenerPreguntasUnaVez(3) } returns
                listOf(PreguntaOrientadora(1, 3, 1, "¿Qué leímos?"))
        // Al editar, la revisión de duplicadas excluye la propia bitácora (idExcluir = 3)
        coEvery { bitacoraRepository.existeDuplicada(2, 11, 2026, 3) } returns false
        coEvery { bitacoraRepository.editar(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns Unit
        crearViewModel()

        viewModel.cambiarTitulo("Bitácora de Noviembre - Lenguaje y Comprensión Lectora")
        viewModel.guardar()
        esperar()

        coVerify {
            bitacoraRepository.editar(
                3, 1, 2, "Bitácora de Noviembre - Lenguaje y Comprensión Lectora",
                "Registrar lo aprendido", "Escribe después de cada clase", 11, 2026, listOf("¿Qué leímos?")
            )
        }
        coVerify(exactly = 0) { bitacoraRepository.guardar(any(), any()) }
        assertTrue(viewModel.uiState.value.guardada)
        assertEquals("Cambios guardados en la bitácora.", Sesion.mensaje)
    }
}