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

    // Asignaciones del docente con el nombre del curso y de la asignatura (JOIN)
    @Query(
        """
        SELECT a.id AS id, a.cursoId AS cursoId, c.nombre AS curso,
               a.asignaturaId AS asignaturaId, s.nombre AS asignatura
        FROM asignaciones a
        INNER JOIN cursos c ON c.id = a.cursoId
        INNER JOIN asignaturas s ON s.id = a.asignaturaId
        WHERE a.docenteId = :docenteId
        ORDER BY c.nombre, s.nombre
        """
    )
    fun obtenerPorDocente(docenteId: Int): Flow<List<AsignacionDetalle>>
}
