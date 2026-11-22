package cl.duoc.bitacora.data

import cl.duoc.bitacora.data.remote.BitacoraRemota
import cl.duoc.bitacora.data.remote.BitacorasApi
import cl.duoc.bitacora.data.remote.ErrorServidor
import cl.duoc.bitacora.data.remote.PreguntaRemota
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.model.PreguntaOrientadora
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

// Pruebas de las bitácoras: el repository debe escribir PRIMERO en el backend
// y solo si responde bien, guardar en Room. Se simulan la API y el DAO.
class BitacoraRepositoryTest {

    private lateinit var api: BitacorasApi
    private lateinit var dao: BitacoraDao
    private lateinit var repositorio: BitacoraRepository

    @Before
    fun preparar() {
        api = mockk()
        dao = mockk(relaxed = true)
        repositorio = BitacoraRepository(dao, api)
    }

    private fun bitacoraNueva() = Bitacora(
        asignacionId = 2, titulo = "Bitácora de Diciembre - Lenguaje",
        proposito = "Registrar lo aprendido", instrucciones = "Escribe después de cada clase",
        mes = 12, anio = 2026
    )

    // ---------- Sincronizar ----------
    @Test
    fun sincronizar_guardaBitacorasYPreguntasDelBackend() = runTest {
        // ARRANGE: el backend tiene 2 bitácoras; solo la 1 tiene preguntas
        coEvery { api.obtenerBitacoras() } returns listOf(
            BitacoraRemota(id = 1, asignacionId = 1, titulo = "Matemática"),
            BitacoraRemota(id = 2, asignacionId = 3, titulo = "Historia")
        )
        coEvery { api.obtenerPreguntas(1) } returns
                listOf(PreguntaRemota(id = 1, bitacoraId = 1, orden = 1, texto = "¿Qué vimos hoy?"))
        coEvery { api.obtenerPreguntas(2) } returns emptyList()

        // ACT
        repositorio.sincronizar()

        // ASSERT
        coVerify { dao.guardarBitacoras(match { it.size == 2 && it[0].id == 1 && it[1].id == 2 }) }
        coVerify { dao.borrarLasQueNoEstan(listOf(1, 2)) }   // Room queda igual al servidor
        coVerify { dao.borrarPreguntas(1) }
        coVerify { dao.guardarPreguntas(match { it.size == 1 && it[0].texto == "¿Qué vimos hoy?" }) }
        coVerify { dao.borrarPreguntas(2) }
    }

    // ---------- Crear ----------
    @Test
    fun guardar_backendLaCrea_seGuardaEnRoomConElIdDelServidor() = runTest {
        coEvery { api.crearBitacora(any()) } returns Response.success(
            BitacoraRemota(id = 5, asignacionId = 2, titulo = "Bitácora de Diciembre - Lenguaje",
                mes = 12, anio = 2026, estado = EstadoBitacora.BORRADOR)
        )
        coEvery { api.reemplazarPreguntas(5, any()) } returns Response.success(
            listOf(PreguntaRemota(id = 10, bitacoraId = 5, orden = 1, texto = "¿Qué leímos?"))
        )

        repositorio.guardar(bitacoraNueva(), listOf("¿Qué leímos?"))

        // Al backend se envió sin id (lo asigna el servidor)
        coVerify { api.crearBitacora(match { it.id == null && it.asignacionId == 2 }) }
        coVerify { api.reemplazarPreguntas(5, listOf(PreguntaRemota(texto = "¿Qué leímos?"))) }
        // En Room se guardó con el id 5 que devolvió el backend
        coVerify { dao.guardarBitacoras(match { it[0].id == 5 }) }
        coVerify { dao.guardarPreguntas(match { it[0].id == 10 && it[0].bitacoraId == 5 }) }
    }

    @Test
    fun guardar_backendResponde409Duplicada_lanzaErrorYNoGuardaEnRoom() = runTest {
        val mensaje = "Ya existe una bitácora de este mes para el curso y asignatura elegidos"
        coEvery { api.crearBitacora(any()) } returns Response.error(409, mensaje.toResponseBody())

        val error = runCatching { repositorio.guardar(bitacoraNueva(), listOf("¿Qué leímos?")) }
            .exceptionOrNull()

        assertTrue(error is ErrorServidor)
        assertEquals(mensaje, error?.message)
        coVerify(exactly = 0) { dao.guardarBitacoras(any()) }
    }

    // ---------- Editar ----------
    @Test
    fun editar_noEsBorradorDelDocente_lanzaErrorYNoLlamaAlBackend() = runTest {
        coEvery { dao.contarEditables(3, 1) } returns 0

        val error = runCatching {
            repositorio.editar(3, 1, 2, "Título", "p", "i", 11, 2026, listOf("¿?"))
        }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
        coVerify(exactly = 0) { api.actualizarBitacora(any(), any()) }
    }

    @Test
    fun editar_borradorDelDocente_actualizaEnBackendYRoom() = runTest {
        coEvery { dao.contarEditables(3, 1) } returns 1
        coEvery { api.actualizarBitacora(3, any()) } returns
                Response.success(BitacoraRemota(id = 3, asignacionId = 2, titulo = "Título nuevo"))
        coEvery { api.reemplazarPreguntas(3, any()) } returns Response.success(emptyList())

        repositorio.editar(3, 1, 2, "Título nuevo", "p", "i", 11, 2026, listOf("¿Qué leímos?"))

        coVerify { api.actualizarBitacora(3, match { it.titulo == "Título nuevo" }) }
        coVerify { dao.guardarBitacoras(match { it[0].titulo == "Título nuevo" }) }
        coVerify { dao.borrarPreguntas(3) }
    }

    // ---------- Publicar y cerrar ----------
    @Test
    fun publicar_bitacoraDeOtroDocente_lanzaErrorYNoLlamaAlBackend() = runTest {
        coEvery { dao.contarDelDocente(2, 1) } returns 0

        val error = runCatching { repositorio.publicar(2, 1) }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
        coVerify(exactly = 0) { api.publicarBitacora(any()) }
    }

    @Test
    fun publicar_bitacoraDelDocente_guardaElEstadoQueDevuelveElBackend() = runTest {
        coEvery { dao.contarDelDocente(3, 1) } returns 1
        coEvery { api.publicarBitacora(3) } returns Response.success(
            BitacoraRemota(id = 3, estado = EstadoBitacora.PUBLICADA, fechaPublicacion = "21/11/2026")
        )

        repositorio.publicar(3, 1)

        coVerify {
            dao.guardarBitacoras(match {
                it[0].estado == EstadoBitacora.PUBLICADA && it[0].fechaPublicacion == "21/11/2026"
            })
        }
    }

    @Test
    fun cerrar_bitacoraDeOtroDocente_lanzaErrorYNoLlamaAlBackend() = runTest {
        coEvery { dao.contarDelDocente(2, 1) } returns 0

        val error = runCatching { repositorio.cerrar(2, 1) }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
        coVerify(exactly = 0) { api.cerrarBitacora(any()) }
    }

    @Test
    fun cerrar_bitacoraDelDocente_guardaElEstadoCerrada() = runTest {
        coEvery { dao.contarDelDocente(1, 1) } returns 1
        coEvery { api.cerrarBitacora(1) } returns Response.success(
            BitacoraRemota(id = 1, estado = EstadoBitacora.CERRADA, fechaCierre = "21/11/2026")
        )

        repositorio.cerrar(1, 1)

        coVerify { dao.guardarBitacoras(match { it[0].estado == EstadoBitacora.CERRADA }) }
    }

    // ---------- Eliminar ----------
    @Test
    fun borrar_noEsBorradorDelDocente_lanzaErrorYNoLlamaAlBackend() = runTest {
        coEvery { dao.contarEditables(1, 1) } returns 0

        val error = runCatching { repositorio.borrar(1, 1) }.exceptionOrNull()

        assertTrue(error is IllegalStateException)
        coVerify(exactly = 0) { api.borrarBitacora(any()) }
    }

    @Test
    fun borrar_borradorDelDocente_eliminaEnBackendYEnRoom() = runTest {
        coEvery { dao.contarEditables(4, 1) } returns 1
        coEvery { api.borrarBitacora(4) } returns Response.success<Void>(null)

        repositorio.borrar(4, 1)

        coVerify { api.borrarBitacora(4) }
        coVerify { dao.borrarBitacora(4) }
    }

    @Test
    fun borrar_backendRechaza409_noBorraDeRoom() = runTest {
        coEvery { dao.contarEditables(4, 1) } returns 1
        coEvery { api.borrarBitacora(4) } returns
                Response.error(409, "Solo se puede eliminar una bitácora en borrador.".toResponseBody())

        val error = runCatching { repositorio.borrar(4, 1) }.exceptionOrNull()

        assertTrue(error is ErrorServidor)
        coVerify(exactly = 0) { dao.borrarBitacora(any()) }
    }

    // ---------- Lecturas (desde Room) ----------
    @Test
    fun lecturasConFlow_devuelvenLoQueEntregaRoom() {
        val porDocente = flowOf(emptyList<BitacoraDetalle>())
        val paraDocente = flowOf(emptyList<BitacoraDetalle>())
        val publicadas = flowOf(emptyList<BitacoraDetalle>())
        val preguntas = flowOf(emptyList<PreguntaOrientadora>())
        every { dao.obtenerPorDocente(1) } returns porDocente
        every { dao.obtenerParaDocente(3, 1) } returns paraDocente
        every { dao.obtenerPublicadasPorCurso(1) } returns publicadas
        every { dao.obtenerPreguntas(3) } returns preguntas

        assertSame(porDocente, repositorio.obtenerPorDocente(1))
        assertSame(paraDocente, repositorio.obtenerParaDocente(3, 1))
        assertSame(publicadas, repositorio.obtenerPublicadasPorCurso(1))
        assertSame(preguntas, repositorio.obtenerPreguntas(3))
    }

    @Test
    fun obtenerPublicadaParaEstudiante_existe_devuelveLaBitacora() = runTest {
        val detalle = mockk<BitacoraDetalle>()
        coEvery { dao.obtenerPublicadaParaEstudiante(1, 1) } returns listOf(detalle)

        assertSame(detalle, repositorio.obtenerPublicadaParaEstudiante(1, 1))
    }

    @Test
    fun obtenerPublicadaParaEstudiante_noCorresponde_devuelveNull() = runTest {
        coEvery { dao.obtenerPublicadaParaEstudiante(2, 1) } returns emptyList()

        assertNull(repositorio.obtenerPublicadaParaEstudiante(2, 1))
    }

    @Test
    fun obtenerPreguntasUnaVez_devuelveLasDeRoom() = runTest {
        val lista = listOf(PreguntaOrientadora(1, 1, 1, "¿Qué vimos hoy?"))
        coEvery { dao.obtenerPreguntasUnaVez(1) } returns lista

        assertEquals(lista, repositorio.obtenerPreguntasUnaVez(1))
    }

    @Test
    fun existeDuplicada_hayOtraDelMismoMes_retornaTrue() = runTest {
        coEvery { dao.contarDuplicadas(2, 11, 2026, 0) } returns 1

        assertTrue(repositorio.existeDuplicada(2, 11, 2026, 0))
    }

    @Test
    fun existeDuplicada_noHayOtra_retornaFalse() = runTest {
        coEvery { dao.contarDuplicadas(2, 12, 2026, 0) } returns 0

        assertFalse(repositorio.existeDuplicada(2, 12, 2026, 0))
    }
}