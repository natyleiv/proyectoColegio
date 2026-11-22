package cl.duoc.bitacora.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Endpoints de ms-usuarios (puerto 8081)
interface UsuariosApi {

    // 200 = datos del usuario · 401 = correo o contraseña incorrectos
    @POST("login")
    suspend fun login(@Body datos: LoginRemoto): Response<UsuarioRemoto>

    @GET("cursos")
    suspend fun obtenerCursos(): List<CursoRemoto>

    @GET("asignaturas")
    suspend fun obtenerAsignaturas(): List<AsignaturaRemota>

    @GET("usuarios")
    suspend fun obtenerUsuarios(): List<UsuarioRemoto>

    @GET("asignaciones")
    suspend fun obtenerAsignaciones(): List<AsignacionRemota>
}