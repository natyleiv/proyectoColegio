package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

// Aporte de un estudiante después de una clase.
// Ya NO copia el nombre del estudiante ni el título de la bitácora: guarda las claves
// foráneas (bitacoraId, estudianteId) y los nombres se obtienen con un JOIN.
// La retroalimentación del docente está en su propia tabla (Retroalimentacion).
@Entity(
    tableName = "registros",
    foreignKeys = [
        ForeignKey(
            entity = Bitacora::class,
            parentColumns = ["id"],
            childColumns = ["bitacoraId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["estudianteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["bitacoraId"]),
        Index(value = ["estudianteId"])
    ]
)
data class Registro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bitacoraId: Int,
    val estudianteId: Int,
    val fechaClase: String,
    val contenido: String,
    val actividad: String,
    val aprendizaje: String,
    val dudas: String = "",
    val fotoUri: String = "",
    val enlace: String = "",
    val estadoEnvio: String = EstadoEnvio.BORRADOR,
    val fechaCreacion: Long,
    val fechaEnvio: Long = 0L
)
