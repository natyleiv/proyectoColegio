package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey

// Bitácora mensual. Ya NO guarda docente, curso ni asignatura como texto:
// guarda solo asignacionId (clave foránea) y de ahí se obtienen los tres datos.
// Las preguntas orientadoras están en su propia tabla (PreguntaOrientadora).
// Las fechas se guardan como texto "dd/MM/yyyy" para mantenerlo simple.
@Entity(
    tableName = "bitacoras",
    foreignKeys = [
        ForeignKey(
            entity = Asignacion::class,
            parentColumns = ["id"],
            childColumns = ["asignacionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["asignacionId"])]
)
data class Bitacora(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val asignacionId: Int,
    val titulo: String,
    val proposito: String,
    val instrucciones: String,
    val mes: Int,
    val anio: Int,
    val estado: String = EstadoBitacora.BORRADOR,
    val fechaPublicacion: String = "",
    val fechaCierre: String = ""
)
