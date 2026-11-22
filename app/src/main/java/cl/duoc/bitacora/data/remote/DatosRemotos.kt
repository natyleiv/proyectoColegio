package cl.duoc.bitacora.data.remote

import cl.duoc.bitacora.model.Asignacion
import cl.duoc.bitacora.model.Asignatura
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.Curso
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.model.EstadoEnvio
import cl.duoc.bitacora.model.PreguntaOrientadora
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.model.Retroalimentacion
import cl.duoc.bitacora.model.Usuario

// Cómo viaja cada dato por la red (el JSON de los microservicios).
// Todos los campos aceptan null (?), porque el backend a veces manda null
// (por ejemplo fechaCierre o dudas). Al pasarlos al modelo de la app
// se reemplazan por un valor por defecto, así la app nunca recibe un null inesperado.

// ======================= ms-bitacoras =======================

data class BitacoraRemota(
    val id: Int? = null,
    val asignacionId: Int? = null,
    val titulo: String? = null,
    val proposito: String? = null,
    val instrucciones: String? = null,
    val mes: Int? = null,
    val anio: Int? = null,
    val estado: String? = null,
    val fechaPublicacion: String? = null,
    val fechaCierre: String? = null
)

data class PreguntaRemota(
    val id: Int? = null,
    val bitacoraId: Int? = null,
    val orden: Int? = null,
    val texto: String? = null
)

data class RegistroRemoto(
    val id: Int? = null,
    val bitacoraId: Int? = null,
    val estudianteId: Int? = null,
    val fechaClase: String? = null,
    val contenido: String? = null,
    val actividad: String? = null,
    val aprendizaje: String? = null,
    val dudas: String? = null,
    val fotoUri: String? = null,
    val enlace: String? = null,
    val estadoEnvio: String? = null,
    val fechaCreacion: Long? = null,
    val fechaEnvio: Long? = null
)

data class RetroalimentacionRemota(
    val id: Int? = null,
    val registroId: Int? = null,
    val docenteId: Int? = null,
    val comentario: String? = null,
    val fecha: Long? = null
)

// ======================= ms-usuarios =======================

// Lo que se envía al iniciar sesión
data class LoginRemoto(
    val correo: String,
    val password: String
)

// El backend nunca devuelve la contraseña, por eso no está aquí
data class UsuarioRemoto(
    val id: Int? = null,
    val nombre: String? = null,
    val correo: String? = null,
    val perfil: String? = null,
    val cursoId: Int? = null
)

data class CursoRemoto(
    val id: Int? = null,
    val nombre: String? = null
)

data class AsignaturaRemota(
    val id: Int? = null,
    val nombre: String? = null
)

data class AsignacionRemota(
    val id: Int? = null,
    val docenteId: Int? = null,
    val cursoId: Int? = null,
    val asignaturaId: Int? = null
)

// ======================= Conversiones =======================
// Entre lo que llega por la red y los modelos de la app (Room)
object MapeoRemoto {

    // ---------- Bitácora ----------
    fun aBitacora(remota: BitacoraRemota): Bitacora {
        return Bitacora(
            id = remota.id ?: 0,
            asignacionId = remota.asignacionId ?: 0,
            titulo = remota.titulo ?: "",
            proposito = remota.proposito ?: "",
            instrucciones = remota.instrucciones ?: "",
            mes = remota.mes ?: 1,
            anio = remota.anio ?: 2026,
            estado = remota.estado ?: EstadoBitacora.BORRADOR,
            fechaPublicacion = remota.fechaPublicacion ?: "",
            fechaCierre = remota.fechaCierre ?: ""
        )
    }

    // Al crear, el id va en null para que lo asigne el backend
    fun aBitacoraRemota(bitacora: Bitacora): BitacoraRemota {
        return BitacoraRemota(
            id = if (bitacora.id == 0) null else bitacora.id,
            asignacionId = bitacora.asignacionId,
            titulo = bitacora.titulo,
            proposito = bitacora.proposito,
            instrucciones = bitacora.instrucciones,
            mes = bitacora.mes,
            anio = bitacora.anio,
            estado = bitacora.estado
        )
    }

    // ---------- Pregunta orientadora ----------
    fun aPregunta(remota: PreguntaRemota): PreguntaOrientadora {
        return PreguntaOrientadora(
            id = remota.id ?: 0,
            bitacoraId = remota.bitacoraId ?: 0,
            orden = remota.orden ?: 0,
            texto = remota.texto ?: ""
        )
    }

    // ---------- Registro ----------
    fun aRegistro(remoto: RegistroRemoto): Registro {
        return Registro(
            id = remoto.id ?: 0,
            bitacoraId = remoto.bitacoraId ?: 0,
            estudianteId = remoto.estudianteId ?: 0,
            fechaClase = remoto.fechaClase ?: "",
            contenido = remoto.contenido ?: "",
            actividad = remoto.actividad ?: "",
            aprendizaje = remoto.aprendizaje ?: "",
            dudas = remoto.dudas ?: "",
            fotoUri = remoto.fotoUri ?: "",
            enlace = remoto.enlace ?: "",
            estadoEnvio = remoto.estadoEnvio ?: EstadoEnvio.BORRADOR,
            fechaCreacion = remoto.fechaCreacion ?: 0L,
            fechaEnvio = remoto.fechaEnvio ?: 0L
        )
    }

    fun aRegistroRemoto(registro: Registro): RegistroRemoto {
        return RegistroRemoto(
            id = if (registro.id == 0) null else registro.id,
            bitacoraId = registro.bitacoraId,
            estudianteId = registro.estudianteId,
            fechaClase = registro.fechaClase,
            contenido = registro.contenido,
            actividad = registro.actividad,
            aprendizaje = registro.aprendizaje,
            dudas = registro.dudas,
            fotoUri = registro.fotoUri,
            enlace = registro.enlace,
            estadoEnvio = registro.estadoEnvio
        )
    }

    // ---------- Retroalimentación ----------
    fun aRetroalimentacion(remota: RetroalimentacionRemota): Retroalimentacion {
        return Retroalimentacion(
            id = remota.id ?: 0,
            registroId = remota.registroId ?: 0,
            docenteId = remota.docenteId ?: 0,
            comentario = remota.comentario ?: "",
            fecha = remota.fecha ?: 0L
        )
    }

    // ---------- ms-usuarios ----------
    // La contraseña queda vacía: el login se valida en el backend, no en el teléfono
    fun aUsuario(remoto: UsuarioRemoto): Usuario {
        return Usuario(
            id = remoto.id ?: 0,
            nombre = remoto.nombre ?: "",
            correo = remoto.correo ?: "",
            password = "",
            perfil = remoto.perfil ?: "",
            cursoId = remoto.cursoId
        )
    }

    fun aCurso(remoto: CursoRemoto): Curso {
        return Curso(id = remoto.id ?: 0, nombre = remoto.nombre ?: "")
    }

    fun aAsignatura(remota: AsignaturaRemota): Asignatura {
        return Asignatura(id = remota.id ?: 0, nombre = remota.nombre ?: "")
    }

    fun aAsignacion(remota: AsignacionRemota): Asignacion {
        return Asignacion(
            id = remota.id ?: 0,
            docenteId = remota.docenteId ?: 0,
            cursoId = remota.cursoId ?: 0,
            asignaturaId = remota.asignaturaId ?: 0
        )
    }
}