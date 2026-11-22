package cl.duoc.bitacora.data

import cl.duoc.bitacora.data.remote.AsignacionRemota
import cl.duoc.bitacora.data.remote.AsignaturaRemota
import cl.duoc.bitacora.data.remote.CursoRemoto
import cl.duoc.bitacora.data.remote.LoginRemoto
import cl.duoc.bitacora.data.remote.UsuarioRemoto
import cl.duoc.bitacora.data.remote.UsuariosApi
import cl.duoc.bitacora.model.Curso
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
import retrofit2.HttpException
import retrofit2.Response

// Pruebas del login y la sincronización de datos base.
// Se SIMULAN (mock) la API de ms-usuarios y los DAO de Room:
// no se necesita el backend encendido ni una base de datos real.
class UsuarioRepositoryTest {

    private lateinit var api: UsuariosApi
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var asignacionDao: AsignacionDao
    private lateinit var catalogoDao: CatalogoDao
    private lateinit var repositorio: UsuarioRepository

    // Se ejecuta antes de CADA prueba: mocks nuevos para que una prueba no afecte a otra.
    // relaxed = true: los DAO aceptan cualquier llamada sin tener que programarla
    @Before
    fun preparar() {
        api = mockk()
        usuarioDao = mockk(relaxed = true)
        asignacionDao = mockk(relaxed = true)
        catalogoDao = mockk(relaxed = true)
        repositorio = UsuarioRepository(usuarioDao, asignacionDao, catalogoDao, api)
    }

    // Programa las respuestas de los 4 endpoints que se descargan después del login
    private fun simularDatosBase() {
        coEvery { api.obtenerCursos() } returns listOf(CursoRemoto(1, "5°A"))
        coEvery { api.obtenerAsignaturas() } returns listOf(AsignaturaRemota(1, "Matemática"))
        coEvery { api.obtenerUsuarios() } returns listOf(
            UsuarioRemoto(1, "Profesor Christian Acuña", "docente1@escuela.cl", "DOCENTE", null)
        )
        coEvery { api.obtenerAsignaciones() } returns listOf(AsignacionRemota(1, 1, 1, 1))
    }

    @Test
    fun iniciarSesion_credencialesCorrectas_devuelveUsuarioYGuardaDatosBase() = runTest {
        // ARRANGE: el backend responde 200 con el docente
        coEvery { api.login(any()) } returns Response.success(
            UsuarioRemoto(1, "Profesor Christian Acuña", "docente1@escuela.cl", "DOCENTE", null)
        )
        simularDatosBase()

        // ACT: el correo viene con espacios y mayúsculas
        val usuario = repositorio.iniciarSesion("  Docente1@Escuela.cl ", "docente123")

        // ASSERT
        assertEquals(1, usuario?.id)
        assertEquals("DOCENTE", usuario?.perfil)
        // Se envió el correo limpio (sin espacios y en minúsculas)
        coVerify { api.login(LoginRemoto("docente1@escuela.cl", "docente123")) }
        // Se guardaron en Room los datos base descargados
        coVerify { catalogoDao.guardarCursos(listOf(Curso(1, "5°A"))) }
        coVerify { catalogoDao.guardarAsignaturas(any()) }
        coVerify { usuarioDao.guardarUsuarios(match { it.size == 1 && it[0].password == "" }) }
        coVerify { asignacionDao.guardarAsignaciones(match { it[0].docenteId == 1 }) }
    }

    @Test
    fun iniciarSesion_credencialesIncorrectas401_devuelveNullYNoDescargaNada() = runTest {
        coEvery { api.login(any()) } returns
                Response.error(401, "Correo o contraseña incorrectos".toResponseBody())

        val usuario = repositorio.iniciarSesion("docente1@escuela.cl", "mala")

        assertNull(usuario)
        coVerify(exactly = 0) { api.obtenerCursos() }
        coVerify(exactly = 0) { usuarioDao.guardarUsuarios(any()) }
    }

    @Test
    fun iniciarSesion_respuestaSinDatos_devuelveNull() = runTest {
        coEvery { api.login(any()) } returns Response.success<UsuarioRemoto>(null)

        assertNull(repositorio.iniciarSesion("docente1@escuela.cl", "docente123"))
    }

    @Test
    fun iniciarSesion_errorDelServidor500_lanzaHttpException() = runTest {
        coEvery { api.login(any()) } returns Response.error(500, "".toResponseBody())

        val error = runCatching { repositorio.iniciarSesion("docente1@escuela.cl", "docente123") }
            .exceptionOrNull()

        assertTrue(error is HttpException)
    }

    @Test
    fun obtenerAsignacionesYEstudiantes_leenDesdeRoom() {
        val asignaciones = flowOf(emptyList<AsignacionDetalle>())
        val estudiantes = flowOf(emptyList<cl.duoc.bitacora.model.Usuario>())
        every { asignacionDao.obtenerPorDocente(1) } returns asignaciones
        every { usuarioDao.obtenerEstudiantesDeBitacora(1, 1) } returns estudiantes

        assertSame(asignaciones, repositorio.obtenerAsignaciones(1))
        assertSame(estudiantes, repositorio.obtenerEstudiantesDeBitacora(1, 1))
    }

    @Test
    fun nombreCurso_existe_devuelveElNombre() = runTest {
        coEvery { catalogoDao.nombreCurso(1) } returns "5°A"

        assertEquals("5°A", repositorio.nombreCurso(1))
    }

    @Test
    fun nombreCurso_noExiste_devuelveTextoVacio() = runTest {
        coEvery { catalogoDao.nombreCurso(99) } returns null

        assertEquals("", repositorio.nombreCurso(99))
    }
}