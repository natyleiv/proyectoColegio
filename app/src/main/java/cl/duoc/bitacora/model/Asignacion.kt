package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Relación docente - curso - asignatura (ej: Ana Rojas enseña Matemática en 5°A).
// Sirve para que un docente solo cree bitácoras de lo que tiene asignado.
@Entity(tableName = "asignaciones")
data class Asignacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val docenteId: Int,
    val curso: String,
    val asignatura: String
)
