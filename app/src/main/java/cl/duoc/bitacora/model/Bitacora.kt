package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Bitácora mensual creada por un docente para un curso y una asignatura.
// Las fechas se guardan como texto "dd/MM/yyyy" para mantenerlo simple.
@Entity(tableName = "bitacoras")
data class Bitacora(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val proposito: String,
    val instrucciones: String,
    val preguntas: String,          // una pregunta orientadora por línea
    val curso: String,
    val asignatura: String,
    val mes: Int,
    val anio: Int,
    val docenteId: Int,
    val estado: String = EstadoBitacora.BORRADOR,
    val fechaPublicacion: String = "",
    val fechaCierre: String = ""
)
