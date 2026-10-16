package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

// Relación docente - curso - asignatura (ej: Ana Rojas enseña Matemática en 5°A).
// Es la tabla que une a las otras tres (3 claves foráneas).
@Entity(
    tableName = "asignaciones",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["docenteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Curso::class,
            parentColumns = ["id"],
            childColumns = ["cursoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Asignatura::class,
            parentColumns = ["id"],
            childColumns = ["asignaturaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["docenteId", "cursoId", "asignaturaId"], unique = true),
        Index(value = ["cursoId"]),
        Index(value = ["asignaturaId"])
    ]
)
data class Asignacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val docenteId: Int,
    val cursoId: Int,
    val asignaturaId: Int
)
