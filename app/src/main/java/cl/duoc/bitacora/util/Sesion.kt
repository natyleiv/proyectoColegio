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

    // true cuando el docente abre el formulario de bitácora para EDITAR (usa bitacoraId)
    var modoEdicion: Boolean = false

    // Mensaje que muestra la pantalla actual en un Snackbar (éxito, advertencia o error).
    // El color del mensaje depende de "tipoMensaje" (colores semánticos en ui/theme).
    var mensaje: String? by mutableStateOf(null)
    var tipoMensaje: TipoMensaje = TipoMensaje.EXITO

    // Atajos: se escribe el tipo ANTES que el texto (el texto es lo que dispara el Snackbar)
    fun exito(texto: String) {
        tipoMensaje = TipoMensaje.EXITO
        mensaje = texto
    }

    fun advertencia(texto: String) {
        tipoMensaje = TipoMensaje.ADVERTENCIA
        mensaje = texto
    }

    fun error(texto: String) {
        tipoMensaje = TipoMensaje.ERROR
        mensaje = texto
    }

    fun esDocente(): Boolean = usuario?.perfil == Perfil.DOCENTE
    fun esEstudiante(): Boolean = usuario?.perfil == Perfil.ESTUDIANTE

    fun cerrar() {
        usuario = null
        bitacoraId = 0
        registroId = 0
        modoEdicion = false
        mensaje = null
        tipoMensaje = TipoMensaje.EXITO
    }
}

// Tipos de mensaje para el Snackbar
enum class TipoMensaje { EXITO, ADVERTENCIA, ERROR }
