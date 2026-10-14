package cl.duoc.bitacora.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cl.duoc.bitacora.model.Perfil
import cl.duoc.bitacora.model.Usuario

// Guarda "quién inició sesión" y qué elemento se está viendo, para pasarlo entre pantallas
// sin usar argumentos de navegación (no vistos en clase).
// El control de acceso real se hace igual en los DAO (consultas filtradas por docente/estudiante).
object Sesion {
    var usuario: Usuario? = null
    var bitacoraId: Int = 0
    var registroId: Int = 0

    // Mensaje de confirmación que muestra la pantalla siguiente en un Snackbar
    var mensaje: String? by mutableStateOf(null)

    fun esDocente(): Boolean = usuario?.perfil == Perfil.DOCENTE
    fun esEstudiante(): Boolean = usuario?.perfil == Perfil.ESTUDIANTE

    fun cerrar() {
        usuario = null
        bitacoraId = 0
        registroId = 0
        mensaje = null
    }
}
