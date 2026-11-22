package cl.duoc.bitacora.ui.login

import cl.duoc.bitacora.data.UsuarioRepository
import cl.duoc.bitacora.model.Perfil
import cl.duoc.bitacora.model.Usuario
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.TipoMensaje
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

// Pruebas del ViewModel del login: estado inicial, validaciones del formulario,
// estados de carga, éxito y error. El repositorio se SIMULA con MockK,
// así que no se necesita base de datos ni backend.
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    // viewModelScope usa el hilo principal de Android (Dispatchers.Main), que no existe en la JVM.
    // Se reemplaza por un dispatcher de prueba que nosotros controlamos (igual que el profesor).
    private val dispatcher = StandardTestDispatcher()

    private lateinit var repositorio: UsuarioRepository
    private lateinit var viewModel: LoginViewModel

    private val docente = Usuario(
        id = 1, nombre = "Profesor Christian Acuña", correo = "docente1@escuela.cl",
        password = "", perfil = Perfil.DOCENTE
    )

    @Before
    fun preparar() {
        Dispatchers.setMain(dispatcher)
        Sesion.cerrar()                     // la sesión es global: se limpia antes de cada prueba
        repositorio = mockk()
        viewModel = LoginViewModel(mockk(relaxed = true), repositorio)
    }

    @After
    fun finalizar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    // Ejecuta las corrutinas pendientes (lo que hay dentro de viewModelScope.launch)
    private fun esperar() = dispatcher.scheduler.advanceUntilIdle()

    private fun escribirCredencialesValidas() {
        viewModel.cambiarCorreo("docente1@escuela.cl")
        viewModel.cambiarPassword("docente123")
    }

    // ---------- Estado inicial ----------
    @Test
    fun estadoInicial_camposVaciosYSinErrores() {
        val estado = viewModel.uiState.value

        assertEquals("", estado.correo)
        assertEquals("", estado.password)
        assertFalse(estado.errorCorreo)
        assertFalse(estado.errorPassword)
        assertFalse(estado.errorCredenciales)
        assertFalse(estado.cargando)
        assertNull(estado.perfilIngresado)
    }

    @Test
    fun cambiarCorreoYPassword_actualizanElEstado() {
        escribirCredencialesValidas()

        assertEquals("docente1@escuela.cl", viewModel.uiState.value.correo)
        assertEquals("docente123", viewModel.uiState.value.password)
    }

    // ---------- Validaciones (no llaman al backend) ----------
    @Test
    fun ingresar_correoInvalido_marcaErrorYNoLlamaAlRepositorio() {
        viewModel.cambiarCorreo("docente1")              // sin @ ni .cl
        viewModel.cambiarPassword("docente123")

        viewModel.ingresar()
        esperar()

        assertTrue(viewModel.uiState.value.errorCorreo)
        assertFalse(viewModel.uiState.value.cargando)
        coVerify(exactly = 0) { repositorio.iniciarSesion(any(), any()) }
    }

    @Test
    fun ingresar_passwordCorta_marcaErrorYNoLlamaAlRepositorio() {
        viewModel.cambiarCorreo("docente1@escuela.cl")
        viewModel.cambiarPassword("123")

        viewModel.ingresar()
        esperar()

        assertTrue(viewModel.uiState.value.errorPassword)
        coVerify(exactly = 0) { repositorio.iniciarSesion(any(), any()) }
    }

    @Test
    fun corregirLosCampos_quitaLosErroresApenasSonValidos() {
        // ARRANGE: primero se intenta entrar con los dos campos vacíos
        viewModel.ingresar()
        assertTrue(viewModel.uiState.value.errorCorreo)
        assertTrue(viewModel.uiState.value.errorPassword)

        // ACT: el usuario corrige los campos
        escribirCredencialesValidas()

        // ASSERT: los errores desaparecen sin volver a tocar "Ingresar"
        assertFalse(viewModel.uiState.value.errorCorreo)
        assertFalse(viewModel.uiState.value.errorPassword)
    }

    // ---------- Respuestas del repositorio ----------
    @Test
    fun ingresar_credencialesCorrectas_muestraCargandoYLuegoGuardaLaSesion() {
        coEvery { repositorio.iniciarSesion("docente1@escuela.cl", "docente123") } returns docente
        escribirCredencialesValidas()

        viewModel.ingresar()
        // Mientras espera la respuesta: estado de carga
        assertTrue(viewModel.uiState.value.cargando)

        esperar()

        // Después de la respuesta: éxito
        val estado = viewModel.uiState.value
        assertFalse(estado.cargando)
        assertEquals(Perfil.DOCENTE, estado.perfilIngresado)
        assertEquals(docente, Sesion.usuario)
    }

    @Test
    fun ingresar_credencialesIncorrectas_marcaErrorDeCredenciales() {
        coEvery { repositorio.iniciarSesion(any(), any()) } returns null
        escribirCredencialesValidas()

        viewModel.ingresar()
        esperar()

        assertTrue(viewModel.uiState.value.errorCredenciales)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(Sesion.usuario)
    }

    @Test
    fun cambiarCorreo_despuesDeCredencialesIncorrectas_quitaEseError() {
        coEvery { repositorio.iniciarSesion(any(), any()) } returns null
        escribirCredencialesValidas()
        viewModel.ingresar()
        esperar()

        viewModel.cambiarCorreo("docente2@escuela.cl")

        assertFalse(viewModel.uiState.value.errorCredenciales)
    }

    @Test
    fun ingresar_sinConexion_muestraMensajeDeConexion() {
        coEvery { repositorio.iniciarSesion(any(), any()) } throws IOException("Failed to connect")
        escribirCredencialesValidas()

        viewModel.ingresar()
        esperar()

        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(TipoMensaje.ERROR, Sesion.tipoMensaje)
        assertEquals(
            "No se pudo conectar con el servidor. Revisa que los microservicios estén encendidos.",
            Sesion.mensaje
        )
    }

    @Test
    fun ingresar_errorInesperado_muestraMensajeGenerico() {
        coEvery { repositorio.iniciarSesion(any(), any()) } throws RuntimeException("Error 500")
        escribirCredencialesValidas()

        viewModel.ingresar()
        esperar()

        assertFalse(viewModel.uiState.value.cargando)
        assertEquals("Error al iniciar sesión. Intenta nuevamente.", Sesion.mensaje)
    }

    @Test
    fun ingresar_tocarDosVecesMientrasCarga_soloEnviaUnaVez() {
        coEvery { repositorio.iniciarSesion(any(), any()) } returns docente
        escribirCredencialesValidas()

        viewModel.ingresar()
        viewModel.ingresar()      // segundo toque mientras está cargando
        esperar()

        coVerify(exactly = 1) { repositorio.iniciarSesion(any(), any()) }
    }

    @Test
    fun navegacionRealizada_limpiaElPerfilIngresado() {
        coEvery { repositorio.iniciarSesion(any(), any()) } returns docente
        escribirCredencialesValidas()
        viewModel.ingresar()
        esperar()

        viewModel.navegacionRealizada()

        assertNull(viewModel.uiState.value.perfilIngresado)
    }
}