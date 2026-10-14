package cl.duoc.bitacora.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import cl.duoc.bitacora.model.Registro
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Insert
    suspend fun insertar(registro: Registro)

    @Update
    suspend fun actualizar(registro: Registro)

    // ---- Estudiante: solo ve SUS registros ----
    @Query("SELECT * FROM registros WHERE estudianteId = :estudianteId ORDER BY fechaCreacion DESC")
    fun obtenerPorEstudiante(estudianteId: Int): Flow<List<Registro>>

    @Query("SELECT * FROM registros WHERE id = :id AND estudianteId = :estudianteId")
    suspend fun obtenerDeEstudiante(id: Int, estudianteId: Int): Registro?

    // ---- Docente: solo registros ENVIADOS de bitácoras que le pertenecen ----
    @Query(
        """
        SELECT r.* FROM registros r
        INNER JOIN bitacoras b ON r.bitacoraId = b.id
        WHERE b.id = :bitacoraId AND b.docenteId = :docenteId AND r.estadoEnvio = 'ENVIADO'
        ORDER BY r.fechaEnvio DESC
        """
    )
    fun obtenerEnviadosParaDocente(bitacoraId: Int, docenteId: Int): Flow<List<Registro>>

    @Query(
        """
        SELECT r.* FROM registros r
        INNER JOIN bitacoras b ON r.bitacoraId = b.id
        WHERE r.id = :id AND b.docenteId = :docenteId AND r.estadoEnvio = 'ENVIADO'
        """
    )
    suspend fun obtenerParaDocente(id: Int, docenteId: Int): Registro?

    // Guarda la retroalimentación y marca el aporte como revisado
    @Query(
        """
        UPDATE registros
        SET comentarioDocente = :comentario, estadoRevision = 'REVISADO',
            fechaRetro = :fecha, docenteRevisorId = :docenteId
        WHERE id = :id AND estadoEnvio = 'ENVIADO'
          AND bitacoraId IN (SELECT id FROM bitacoras WHERE docenteId = :docenteId)
        """
    )
    suspend fun revisar(id: Int, comentario: String, fecha: Long, docenteId: Int)
}
