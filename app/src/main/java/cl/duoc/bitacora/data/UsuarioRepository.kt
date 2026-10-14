package cl.duoc.bitacora.data

import cl.duoc.bitacora.model.Asignacion
import cl.duoc.bitacora.model.Usuario
import kotlinx.coroutines.flow.Flow

// DEFINIMOS EL CÓMO LO VAMOS A HACER
class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val asignacionDao: AsignacionDao
) {
    // Devuelve el usuario si el correo existe y la contraseña coincide; si no, null
    suspend fun iniciarSesion(correo: String, password: String): Usuario? {
        val usuario = usuarioDao.buscarPorCorreo(correo.trim().lowercase())
        return if (usuario != null && usuario.password == password) usuario else null
    }

    fun obtenerAsignaciones(docenteId: Int): Flow<List<Asignacion>> {
        return asignacionDao.obtenerPorDocente(docenteId)
    }

    fun obtenerEstudiantesDeBitacora(bitacoraId: Int, docenteId: Int): Flow<List<Usuario>> {
        return usuarioDao.obtenerEstudiantesDeBitacora(bitacoraId, docenteId)
    }
}
