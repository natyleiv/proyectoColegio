package cl.duoc.bitacora.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints de ms-bitacoras (puerto 8082). Las rutas son las mismas del README del backend.
// Las lecturas devuelven el dato directo; las escrituras devuelven Response<...>
// para poder revisar el código (201, 400, 409...) y leer el mensaje de error.
interface BitacorasApi {

    // ---------- Bitácoras ----------
    @GET("bitacoras")
    suspend fun obtenerBitacoras(): List<BitacoraRemota>

    @GET("bitacoras/asignacion/{asignacionId}")
    suspend fun obtenerBitacorasDeAsignacion(@Path("asignacionId") asignacionId: Int): List<BitacoraRemota>

    @POST("bitacoras")
    suspend fun crearBitacora(@Body bitacora: BitacoraRemota): Response<BitacoraRemota>

    @PUT("bitacoras/{id}")
    suspend fun actualizarBitacora(@Path("id") id: Int, @Body bitacora: BitacoraRemota): Response<BitacoraRemota>

    @PATCH("bitacoras/{id}/publicar")
    suspend fun publicarBitacora(@Path("id") id: Int): Response<BitacoraRemota>

    @PATCH("bitacoras/{id}/cerrar")
    suspend fun cerrarBitacora(@Path("id") id: Int): Response<BitacoraRemota>

    @DELETE("bitacoras/{id}")
    suspend fun borrarBitacora(@Path("id") id: Int): Response<Void>

    // ---------- Preguntas orientadoras ----------
    @GET("bitacoras/{bitacoraId}/preguntas")
    suspend fun obtenerPreguntas(@Path("bitacoraId") bitacoraId: Int): List<PreguntaRemota>

    @PUT("bitacoras/{bitacoraId}/preguntas")
    suspend fun reemplazarPreguntas(
        @Path("bitacoraId") bitacoraId: Int,
        @Body preguntas: List<PreguntaRemota>
    ): Response<List<PreguntaRemota>>

    // ---------- Registros ----------
    @GET("registros/bitacora/{bitacoraId}")
    suspend fun obtenerRegistrosEnviados(@Path("bitacoraId") bitacoraId: Int): List<RegistroRemoto>

    @GET("registros/estudiante/{estudianteId}")
    suspend fun obtenerRegistrosDeEstudiante(@Path("estudianteId") estudianteId: Int): List<RegistroRemoto>

    @POST("registros")
    suspend fun crearRegistro(@Body registro: RegistroRemoto): Response<RegistroRemoto>

    @PUT("registros/{id}")
    suspend fun actualizarRegistro(@Path("id") id: Int, @Body registro: RegistroRemoto): Response<RegistroRemoto>

    @DELETE("registros/{id}")
    suspend fun borrarRegistro(@Path("id") id: Int): Response<Void>

    // ---------- Retroalimentación ----------
    // 200 = ya revisado (trae el comentario) · 204 = todavía sin revisar (cuerpo vacío)
    @GET("registros/{registroId}/retroalimentacion")
    suspend fun obtenerRetroalimentacion(@Path("registroId") registroId: Int): Response<RetroalimentacionRemota>

    @PUT("registros/{registroId}/retroalimentacion")
    suspend fun guardarRetroalimentacion(
        @Path("registroId") registroId: Int,
        @Body retro: RetroalimentacionRemota
    ): Response<RetroalimentacionRemota>
}