package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

// Una pregunta orientadora de una bitácora. Una bitácora tiene MUCHAS preguntas
// (relación 1 a N), por eso van en una tabla aparte y no dentro de un solo texto.
@Entity(
    tableName = "preguntas_orientadoras",
    foreignKeys = [
        ForeignKey(
            entity = Bitacora::class,
            parentColumns = ["id"],
            childColumns = ["bitacoraId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["bitacoraId"])]
)
data class PreguntaOrientadora(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bitacoraId: Int,
    val orden: Int,
    val texto: String
)
