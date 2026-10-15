package cl.duoc.bitacora.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.bitacora.model.Bitacora
import kotlinx.coroutines.flow.Flow

@Dao
interface BitacoraDao {

    @Insert
    suspend fun insertar(bitacora: Bitacora)

    // Solo las bitácoras del docente que inició sesión
    @Query("SELECT * FROM bitacoras WHERE docenteId = :docenteId ORDER BY anio DESC, mes DESC, id DESC")
    fun obtenerPorDocente(docenteId: Int): Flow<List<Bitacora>>

    // Devuelve una lista: vacía si la bitácora no existe o no es de este docente
    @Query("SELECT * FROM bitacoras WHERE id = :id AND docenteId = :docenteId")
    fun obtenerParaDocente(id: Int, docenteId: Int): Flow<List<Bitacora>>

    // Solo las bitácoras publicadas del curso del estudiante
    @Query("SELECT * FROM bitacoras WHERE curso = :curso AND estado = 'PUBLICADA' ORDER BY anio DESC, mes DESC, id DESC")
    fun obtenerPublicadasPorCurso(curso: String): Flow<List<Bitacora>>

    @Query("SELECT * FROM bitacoras WHERE id = :id AND curso = :curso AND estado = 'PUBLICADA'")
    suspend fun obtenerPublicadaParaEstudiante(id: Int, curso: String): Bitacora?

    // Solo el docente dueño puede publicar y solo desde BORRADOR
    @Query("UPDATE bitacoras SET estado = 'PUBLICADA', fechaPublicacion = :fecha WHERE id = :id AND docenteId = :docenteId AND estado = 'BORRADOR'")
    suspend fun publicar(id: Int, docenteId: Int, fecha: String)

    // Solo el docente dueño puede cerrar y solo desde PUBLICADA
    @Query("UPDATE bitacoras SET estado = 'CERRADA', fechaCierre = :fecha WHERE id = :id AND docenteId = :docenteId AND estado = 'PUBLICADA'")
    suspend fun cerrar(id: Int, docenteId: Int, fecha: String)
}
