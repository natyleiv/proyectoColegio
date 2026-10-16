package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

// Comentario del docente sobre un registro. Es una relación 1 a 1 con Registro
// (registroId es único). Si existe una fila aquí, el registro está REVISADO;
// si no existe, está PENDIENTE (así el estado no se guarda dos veces).
@Entity(
    tableName = "retroalimentaciones",
    foreignKeys = [
        ForeignKey(
            entity = Registro::class,
            parentColumns = ["id"],
            childColumns = ["registroId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["docenteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["registroId"], unique = true),
        Index(value = ["docenteId"])
    ]
)
data class Retroalimentacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val registroId: Int,
    val docenteId: Int,
    val comentario: String,
    val fecha: Long
)
