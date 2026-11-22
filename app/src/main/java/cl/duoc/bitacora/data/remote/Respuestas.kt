package cl.duoc.bitacora.data.remote

import retrofit2.Response
import java.io.IOException

// Error que responde el backend (400, 404, 409...), con el mensaje que mandó
// (por ejemplo: "Ya existe una bitácora de este mes para el curso y asignatura elegidos")
class ErrorServidor(mensaje: String) : Exception(mensaje)

// Igual que comprobarRespuesta del ejemplo del profesor, pero además devuelve el cuerpo.
// <T> significa "cualquier tipo": sirve para Response<BitacoraRemota>, Response<RegistroRemoto>, etc.
fun <T> comprobarRespuesta(respuesta: Response<T>): T {
    if (!respuesta.isSuccessful) throw ErrorServidor(leerMensaje(respuesta))
    return respuesta.body() ?: throw ErrorServidor("El servidor respondió sin datos")
}

// Para respuestas sin cuerpo (DELETE responde 204 No Content)
fun comprobarSinCuerpo(respuesta: Response<Void>) {
    if (!respuesta.isSuccessful) throw ErrorServidor(leerMensaje(respuesta))
}

// El texto que mandó el backend; si no mandó nada, el código HTTP
private fun leerMensaje(respuesta: Response<*>): String {
    val texto = respuesta.errorBody()?.string()
    return if (texto.isNullOrBlank()) "Error del servidor (${respuesta.code()})" else texto
}

// Mensaje para mostrar al usuario según el tipo de error
fun mensajeDeError(e: Exception, porDefecto: String): String {
    return when (e) {
        is ErrorServidor -> e.message ?: porDefecto
        is IOException -> "No se pudo conectar con el servidor. Revisa que los microservicios estén encendidos."
        else -> porDefecto
    }
}