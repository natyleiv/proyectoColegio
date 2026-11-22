package cl.duoc.bitacora.data.remote

import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response
import java.io.IOException

// Pruebas del manejo de respuestas del backend.
// Las respuestas HTTP se arman "a mano" con Response.success / Response.error:
// así se simula lo que respondería el microservicio, sin red ni backend encendido.
class RespuestasTest {

    // ---------- comprobarRespuesta ----------
    @Test
    fun comprobarRespuesta_exitoConCuerpo_devuelveElCuerpo() {
        // ARRANGE: el backend respondió 200/201 con la bitácora creada
        val respuesta = Response.success(BitacoraRemota(id = 5, titulo = "Bitácora creada"))
        // ACT
        val cuerpo = comprobarRespuesta(respuesta)
        // ASSERT
        assertEquals(5, cuerpo.id)
        assertEquals("Bitácora creada", cuerpo.titulo)
    }

    @Test
    fun comprobarRespuesta_exitoSinCuerpo_lanzaErrorServidor() {
        val respuesta = Response.success<BitacoraRemota>(null)

        val error = assertThrows(ErrorServidor::class.java) { comprobarRespuesta(respuesta) }

        assertEquals("El servidor respondió sin datos", error.message)
    }

    @Test
    fun comprobarRespuesta_error409_lanzaErrorConElMensajeDelBackend() {
        // El mismo texto que responde BitacoraController cuando la bitácora está duplicada
        val mensaje = "Ya existe una bitácora de este mes para el curso y asignatura elegidos"
        val respuesta = Response.error<BitacoraRemota>(409, mensaje.toResponseBody())

        val error = assertThrows(ErrorServidor::class.java) { comprobarRespuesta(respuesta) }

        assertEquals(mensaje, error.message)
    }

    @Test
    fun comprobarRespuesta_errorSinTexto_usaElCodigoHttp() {
        // ResponseEntity.notFound().build() responde 404 sin cuerpo
        val respuesta = Response.error<BitacoraRemota>(404, "".toResponseBody())

        val error = assertThrows(ErrorServidor::class.java) { comprobarRespuesta(respuesta) }

        assertEquals("Error del servidor (404)", error.message)
    }

    // ---------- comprobarSinCuerpo (DELETE) ----------
    @Test
    fun comprobarSinCuerpo_exito_noLanzaError() {
        // 204 No Content: si no lanza excepción, la prueba pasa
        comprobarSinCuerpo(Response.success<Void>(null))
    }

    @Test
    fun comprobarSinCuerpo_error409_lanzaErrorConElMensajeDelBackend() {
        val mensaje = "Solo se puede eliminar una bitácora en borrador."
        val respuesta = Response.error<Void>(409, mensaje.toResponseBody())

        val error = assertThrows(ErrorServidor::class.java) { comprobarSinCuerpo(respuesta) }

        assertEquals(mensaje, error.message)
    }

    // ---------- mensajeDeError ----------
    @Test
    fun mensajeDeError_errorServidor_muestraElMensajeDelBackend() {
        val mensaje = mensajeDeError(ErrorServidor("Esta bitácora ya no se puede editar."), "Error genérico")

        assertEquals("Esta bitácora ya no se puede editar.", mensaje)
    }

    @Test
    fun mensajeDeError_sinConexion_muestraMensajeDeConexion() {
        val mensaje = mensajeDeError(IOException("Failed to connect"), "Error genérico")

        assertEquals(
            "No se pudo conectar con el servidor. Revisa que los microservicios estén encendidos.",
            mensaje
        )
    }

    @Test
    fun mensajeDeError_otroError_muestraElMensajePorDefecto() {
        val mensaje = mensajeDeError(IllegalStateException("La bitácora no es de este docente"), "Error genérico")

        assertEquals("Error genérico", mensaje)
    }
}