package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

// Tabla "usuarios": docentes y estudiantes FICTICIOS (el caso prohíbe datos reales).
// cursoId es la CLAVE FORÁNEA al curso del estudiante; en un docente queda null,
// porque sus cursos vienen de la tabla "asignaciones".
@Entity(
    tableName = "usuarios",
    foreignKeys = [
        ForeignKey(
            entity = Curso::class,
            parentColumns = ["id"],
            childColumns = ["cursoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["correo"], unique = true),
        Index(value = ["cursoId"])
    ]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val password: String,
    val perfil: String,
    val cursoId: Int? = null
)
