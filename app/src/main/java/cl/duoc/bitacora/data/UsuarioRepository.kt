package cl.duoc.bitacora.data

import cl.duoc.bitacora.data.remote.LoginRemoto
import cl.duoc.bitacora.data.remote.MapeoRemoto
import cl.duoc.bitacora.data.remote.RetrofitClient
import cl.duoc.bitacora.data.remote.UsuariosApi
import cl.duoc.bitacora.model.Usuario
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

// DEFINIMOS EL CÓMO LO VAMOS A HACER
// El login y los datos base (cursos, asignaturas, usuarios y asignaciones) vienen de ms-usuarios.
// Room guarda una copia local para que las pantallas puedan armar los nombres con JOIN.
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val asignacionDao: AsignacionDao,
    private val catalogoDao: CatalogoDao,
    // Se recibe en el constructor (como en el ejemplo del profesor) para poder
    // reemplazarla por una API falsa en las pruebas unitarias
    private val api: UsuariosApi = RetrofitClient.usuariosApi
) {

    // Valida el correo y la contraseña en ms-usuarios.
    // Devuelve el usuario si son correctos, o null si el backend responde 401.
    // Si el servidor no responde, lanza una excepción (la maneja el ViewModel).
    suspend fun iniciarSesion(correo: String, password: String): Usuario? {
        val respuesta = api.login(LoginRemoto(correo.trim().lowercase(), password))
        if (respuesta.code() == 401) return null            // correo o contraseña incorrectos
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
        val remoto = respuesta.body() ?: return null
        sincronizarDatosBase()
        return MapeoRemoto.aUsuario(remoto)
    }

    // Descarga cursos, asignaturas, usuarios y asignaciones y los guarda en Room.
    // El orden importa por las claves foráneas: primero las tablas "padre".
    suspend fun sincronizarDatosBase() {
        catalogoDao.guardarCursos(api.obtenerCursos().map { MapeoRemoto.aCurso(it) })
        catalogoDao.guardarAsignaturas(api.obtenerAsignaturas().map { MapeoRemoto.aAsignatura(it) })
        usuarioDao.guardarUsuarios(api.obtenerUsuarios().map { MapeoRemoto.aUsuario(it) })
        asignacionDao.guardarAsignaciones(api.obtenerAsignaciones().map { MapeoRemoto.aAsignacion(it) })
    }

    fun obtenerAsignaciones(docenteId: Int): Flow<List<AsignacionDetalle>> {
        return asignacionDao.obtenerPorDocente(docenteId)
    }

    fun obtenerEstudiantesDeBitacora(bitacoraId: Int, docenteId: Int): Flow<List<Usuario>> {
        return usuarioDao.obtenerEstudiantesDeBitacora(bitacoraId, docenteId)
    }

    // Nombre del curso (ej: "5°A") a partir de su id
    suspend fun nombreCurso(cursoId: Int): String {
        return catalogoDao.nombreCurso(cursoId) ?: ""
    }
}
