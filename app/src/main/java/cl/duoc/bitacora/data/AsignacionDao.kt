package cl.duoc.bitacora.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.bitacora.model.Asignacion
import kotlinx.coroutines.flow.Flow

@Dao
interface AsignacionDao {

    @Insert
    suspend fun insertar(asignacion: Asignacion)

    @Query("SELECT * FROM asignaciones WHERE docenteId = :docenteId ORDER BY curso, asignatura")
    fun obtenerPorDocente(docenteId: Int): Flow<List<Asignacion>>
}
