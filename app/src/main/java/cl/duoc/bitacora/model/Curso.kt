package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Catálogo de cursos (ej: "5°A"). El nombre se escribe UNA sola vez aquí y las demás
// tablas lo referencian por id (así se cumple la 3FN: no se repite texto).
@Entity(tableName = "cursos")
data class Curso(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String
)
