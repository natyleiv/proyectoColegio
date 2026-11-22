package cl.duoc.bitacora.data

import cl.duoc.bitacora.data.remote.BitacorasApi
import cl.duoc.bitacora.data.remote.ErrorServidor
import cl.duoc.bitacora.data.remote.RegistroRemoto
import cl.duoc.bitacora.data.remote.RetroalimentacionRemota
import cl.duoc.bitacora.model.EstadoEnvio
import cl.duoc.bitacora.model.Registro
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

// Pruebas de los registros del estudiante y la retroalimentación del docente.
// Se simulan la API de ms-bitacoras y el DAO de Room.
class RegistroRepositoryTest {

    private lateinit var api: BitacorasApi
    private lateinit var dao: RegistroDao
    private lateinit var repositorio: RegistroRepository

    @Before
    fun preparar() {
        api = mockk()
        dao = mockk(relaxed = true)
        repositorio = RegistroRepository(dao, api)
    }

    private fun registro(id: Int, estado: String) = Registro(
        id = id, bitacoraId = 1, estudianteId = 3, fechaClase = "20/10/2026",
        contenido = "Comparación de fracciones", actividad = "Guía en parejas",
        aprendizaje = "Denominador común", estadoEnvio = estado, fechaCreacion = 0L
    )

    // ---------- Sincronizar ----------
    @Test
    fun sincronizarDeEstudiante_guardaRegistrosYSoloBuscaComentarioDeLosEnviados() = runTest {
        // ARRANGE: un registro enviado (revisado) y un borrador
        coEvery { api.obtenerRegistrosDeEstudiante(3) } returns listOf(
            RegistroRemoto(id = 1, bitacoraId = 1, estudianteId = 3, estadoEnvio = EstadoEnvio.ENVIADO),
            RegistroRemoto(id = 3, bitacoraId = 1, estudianteId = 3, estadoEnvio = EstadoEnvio.BORRADOR)
        )
        coEvery { api.obtenerRetroalimentacion(1) } returns Response.success(
            RetroalimentacionRemota(id = 1, registroId = 1, docenteId = 1, comentario = "Muy buen trabajo", fecha = 1L)
        )

        // ACT
        repositorio.sincronizarDeEstudiante(3)

        // ASSERT
        coVerify { dao.guardarRegistros(match { it.size == 2 }) }
        coVerify { dao.guardarRetroalimentaciones(match { it[0].comentario == "Muy buen trabajo" }) }
        // Un borrador nunca tiene comentario: no se consulta
        coVerify(exactly = 0) { api.obtenerRetroalimentacion(3) }
    }

    @Test
    fun sincronizarDeBitacora_enviadoSinRevisar204_noGuardaComentario() = runTest {
        coEvery { api.obtenerRegistrosEnviados(1) } returns listOf(
            RegistroRemoto(id = 3, bitacoraId = 1, estudianteId = 3, estadoEnvio = EstadoEnvio.ENVIADO)
        )
        // 204 No Content: todavía nadie lo revisa (cuerpo vacío)
        coEvery { api.obtenerRetroalimentacion(3) } returns Response.success<RetroalimentacionRemota>(null)

        repositorio.sincronizarDeBitacora(1)

        coVerify { dao.guardarRegistros(match { it[0].id == 3 }) }
        coVerify(exactly = 0) { dao.guardarRetroalimentaciones(any()) }
    }

    // ---------- Crear y editar ----------
    @Test
    fun guardarNuevo_backendLoCrea_seGuardaEnRoomConElIdDelServidor() = runTest {
        coEvery { api.crearRegistro(any()) } returns Response.success(
            RegistroRemoto(id = 4, bitacoraId = 1, estudianteId = 3, estadoEnvio = EstadoEnvio.BORRADOR)
        )

        repositorio.guardarNuevo(registro(0, EstadoEnvio.BORRADOR))

        coVerify { api.crearRegistro(match { it.id == null && it.estudianteId == 3 }) }
        coVerify { dao.guardarRegistros(match { it[0].id == 4 }) }
    }

    @Test
    fun guardarNuevo_backendRechaza400_lanzaErrorConSuMensajeYNoGuarda() = runTest {
        val mensaje = "Describe la actividad que realizaste"
        coEvery { api.crearRegistro(any()) } returns Response.error(400, mensaje.toResponseBody())

        val error = runCatching { repositorio.guardarNuevo(registro(0, EstadoEnvio.ENVIADO)) }
            .exceptionOrNull()

        assertTrue(error is ErrorServidor)
        assertEquals(mensaje, error?.message)
        coVerify(exactly = 0) { dao.guardarRegistros(any()) }
    }

    @Test
    fun actualizar_borradorQueSeEnvia_guardaLoQueDevuelveElBackend() = runTest {
        coEvery { api.actualizarRegistro(3, any()) } returns Response.success(
            RegistroRemoto(id = 3, bitacoraId = 1, estudianteId = 3, estadoEnvio = EstadoEnvio.ENVIADO)
        )

        repositorio.actualizar(registro(3, EstadoEnvio.ENVIADO))

        coVerify { api.actualizarRegistro(3, match { it.estadoEnvio == EstadoEnvio.ENVIADO }) }
        coVerify { dao.guardarRegistros(match { it[0].estadoEnvio == EstadoEnvio.ENVIADO }) }
    }

    // ---------- Retroalimentación ----------
    @Test
    fun revisar_registroQueNoEsDelDocente_lanzaErrorYNoLlamaAlBackend() = runTest {
        coEvery { dao.contarRevisables(3, 2) } returns 0

        val error = runCatching { repositorio.revisar(3, "Buen avance", 2) }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
        coVerify(exactly = 0) { api.guardarRetroalimentacion(any(), any()) }
    }

    @Test
    fun revisar_registroDelDocente_guardaElComentarioEnBackendYRoom() = runTest {
        coEvery { dao.contarRevisables(3, 1) } returns 1
        coEvery { api.guardarRetroalimentacion(3, any()) } returns Response.success(
            RetroalimentacionRemota(id = 2, registroId = 3, docenteId = 1, comentario = "Buen avance", fecha = 1L)
        )

        repositorio.revisar(3, "Buen avance", 1)

        coVerify {
            api.guardarRetroalimentacion(3, RetroalimentacionRemota(docenteId = 1, comentario = "Buen avance"))
        }
        coVerify { dao.guardarRetroalimentaciones(match { it[0].id == 2 && it[0].registroId == 3 }) }
    }

    // ---------- Eliminar ----------
    @Test
    fun borrar_borradorDelEstudiante_eliminaEnBackendYEnRoom() = runTest {
        coEvery { dao.obtenerCrudoDeEstudiante(5, 3) } returns listOf(registro(5, EstadoEnvio.BORRADOR))
        coEvery { api.borrarRegistro(5) } returns Response.success<Void>(null)

        repositorio.borrar(5, 3)

        coVerify { api.borrarRegistro(5) }
        coVerify { dao.borrarRegistro(5) }
    }

    @Test
    fun borrar_registroYaEnviado_lanzaErrorYNoLlamaAlBackend() = runTest {
        coEvery { dao.obtenerCrudoDeEstudiante(1, 3) } returns listOf(registro(1, EstadoEnvio.ENVIADO))

        val error = runCatching { repositorio.borrar(1, 3) }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
        coVerify(exactly = 0) { api.borrarRegistro(any()) }
    }

    @Test
    fun borrar_registroQueNoEsDelEstudiante_lanzaError() = runTest {
        coEvery { dao.obtenerCrudoDeEstudiante(1, 4) } returns emptyList()

        val error = runCatching { repositorio.borrar(1, 4) }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
        coVerify(exactly = 0) { api.borrarRegistro(any()) }
    }

    // ---------- Lecturas (desde Room) ----------
    @Test
    fun lecturasConFlow_devuelvenLoQueEntregaRoom() {
        val porEstudiante = flowOf(emptyList<RegistroDetalle>())
        val enviados = flowOf(emptyList<RegistroDetalle>())
        val pendientes = flowOf(1)
        every { dao.obtenerPorEstudiante(3) } returns porEstudiante
        every { dao.obtenerEnviadosParaDocente(1, 1) } returns enviados
        every { dao.contarPendientesDeDocente(1) } returns pendientes

        assertSame(porEstudiante, repositorio.obtenerPorEstudiante(3))
        assertSame(enviados, repositorio.obtenerEnviadosParaDocente(1, 1))
        assertSame(pendientes, repositorio.contarPendientesDeDocente(1))
    }

    @Test
    fun obtenerDeEstudiante_existe_devuelveElDetalle() = runTest {
        val detalle = mockk<RegistroDetalle>()
        coEvery { dao.obtenerDeEstudiante(1, 3) } returns listOf(detalle)

        assertSame(detalle, repositorio.obtenerDeEstudiante(1, 3))
    }

    @Test
    fun obtenerDeEstudiante_noEsSuyo_devuelveNull() = runTest {
        coEvery { dao.obtenerDeEstudiante(1, 4) } returns emptyList()

        assertNull(repositorio.obtenerDeEstudiante(1, 4))
    }

    @Test
    fun obtenerCrudoDeEstudiante_devuelveElRegistroORetornaNull() = runTest {
        coEvery { dao.obtenerCrudoDeEstudiante(3, 3) } returns listOf(registro(3, EstadoEnvio.BORRADOR))
        coEvery { dao.obtenerCrudoDeEstudiante(9, 3) } returns emptyList()

        assertEquals(3, repositorio.obtenerCrudoDeEstudiante(3, 3)?.id)
        assertNull(repositorio.obtenerCrudoDeEstudiante(9, 3))
    }

    @Test
    fun obtenerParaDocente_devuelveElDetalleORetornaNull() = runTest {
        val detalle = mockk<RegistroDetalle>()
        coEvery { dao.obtenerParaDocente(3, 1) } returns listOf(detalle)
        coEvery { dao.obtenerParaDocente(3, 2) } returns emptyList()

        assertSame(detalle, repositorio.obtenerParaDocente(3, 1))
        assertNull(repositorio.obtenerParaDocente(3, 2))
    }
}