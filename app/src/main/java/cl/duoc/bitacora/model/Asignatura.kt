package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Catálogo de asignaturas (ej: "Matemática")
@Entity(tableName = "asignaturas")
data class Asignatura(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String
)
