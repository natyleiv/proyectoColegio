package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Aporte de un estudiante después de una clase. Incluye la retroalimentación del docente
// (comentario, fecha y revisor) para no crear otra tabla en este avance.
// estudianteNombre y bitacoraTitulo se copian para poder mostrarlos sin hacer más consultas.
@Entity(tableName = "registros")
data class Registro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bitacoraId: Int,
    val bitacoraTitulo: String,
    val estudianteId: Int,
    val estudianteNombre: String,
    val fechaClase: String,
    val contenido: String,
    val actividad: String,
    val aprendizaje: String,
    val dudas: String = "",
    val fotoUri: String = "",
    val enlace: String = "",
    val estadoEnvio: String = EstadoEnvio.BORRADOR,
    val estadoRevision: String = EstadoRevision.PENDIENTE,
    val fechaCreacion: Long,
    val fechaEnvio: Long = 0L,
    val comentarioDocente: String = "",
    val docenteRevisorId: Int = 0,
    val fechaRetro: Long = 0L
)
