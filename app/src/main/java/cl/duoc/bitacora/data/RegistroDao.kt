package cl.duoc.bitacora.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.model.Retroalimentacion
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroDao {

    @Insert
    suspend fun insertar(registro: Registro)

    @Update
    suspend fun actualizar(registro: Registro)

    @Insert
    suspend fun insertarRetroalimentacion(retro: Retroalimentacion)

    // Registro "crudo" (para editar un borrador del estudiante)
    @Query("SELECT * FROM registros WHERE id = :id AND estudianteId = :estudianteId")
    suspend fun obtenerCrudoDeEstudiante(id: Int, estudianteId: Int): List<Registro>

    // ---- Estudiante: solo ve SUS registros ----
    @Query(
        """
        SELECT r.id AS id, r.bitacoraId AS bitacoraId, r.estudianteId AS estudianteId,
               r.fechaClase AS fechaClase, r.contenido AS contenido, r.actividad AS actividad,
               r.aprendizaje AS aprendizaje, r.dudas AS dudas, r.fotoUri AS fotoUri,
               r.enlace AS enlace, r.estadoEnvio AS estadoEnvio,
               r.fechaCreacion AS fechaCreacion, r.fechaEnvio AS fechaEnvio,
               b.titulo AS bitacoraTitulo, u.nombre AS estudianteNombre,
               COALESCE(t.comentario, '') AS comentarioDocente,
               COALESCE(t.fecha, 0) AS fechaRetro,
               CASE WHEN t.id IS NULL THEN 'PENDIENTE' ELSE 'REVISADO' END AS estadoRevision
        FROM registros r
        INNER JOIN bitacoras b ON b.id = r.bitacoraId
        INNER JOIN usuarios u ON u.id = r.estudianteId
        LEFT JOIN retroalimentaciones t ON t.registroId = r.id
        WHERE r.estudianteId = :estudianteId
        ORDER BY r.fechaCreacion DESC
        """
    )
    fun obtenerPorEstudiante(estudianteId: Int): Flow<List<RegistroDetalle>>

    @Query(
        """
        SELECT r.id AS id, r.bitacoraId AS bitacoraId, r.estudianteId AS estudianteId,
               r.fechaClase AS fechaClase, r.contenido AS contenido, r.actividad AS actividad,
               r.aprendizaje AS aprendizaje, r.dudas AS dudas, r.fotoUri AS fotoUri,
               r.enlace AS enlace, r.estadoEnvio AS estadoEnvio,
               r.fechaCreacion AS fechaCreacion, r.fechaEnvio AS fechaEnvio,
               b.titulo AS bitacoraTitulo, u.nombre AS estudianteNombre,
               COALESCE(t.comentario, '') AS comentarioDocente,
               COALESCE(t.fecha, 0) AS fechaRetro,
               CASE WHEN t.id IS NULL THEN 'PENDIENTE' ELSE 'REVISADO' END AS estadoRevision
        FROM registros r
        INNER JOIN bitacoras b ON b.id = r.bitacoraId
        INNER JOIN usuarios u ON u.id = r.estudianteId
        LEFT JOIN retroalimentaciones t ON t.registroId = r.id
        WHERE r.id = :id AND r.estudianteId = :estudianteId
        """
    )
    suspend fun obtenerDeEstudiante(id: Int, estudianteId: Int): List<RegistroDetalle>

    // ---- Docente: solo registros ENVIADOS de bitácoras que le pertenecen ----
    @Query(
        """
        SELECT r.id AS id, r.bitacoraId AS bitacoraId, r.estudianteId AS estudianteId,
               r.fechaClase AS fechaClase, r.contenido AS contenido, r.actividad AS actividad,
               r.aprendizaje AS aprendizaje, r.dudas AS dudas, r.fotoUri AS fotoUri,
               r.enlace AS enlace, r.estadoEnvio AS estadoEnvio,
               r.fechaCreacion AS fechaCreacion, r.fechaEnvio AS fechaEnvio,
               b.titulo AS bitacoraTitulo, u.nombre AS estudianteNombre,
               COALESCE(t.comentario, '') AS comentarioDocente,
               COALESCE(t.fecha, 0) AS fechaRetro,
               CASE WHEN t.id IS NULL THEN 'PENDIENTE' ELSE 'REVISADO' END AS estadoRevision
        FROM registros r
        INNER JOIN bitacoras b ON b.id = r.bitacoraId
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        INNER JOIN usuarios u ON u.id = r.estudianteId
        LEFT JOIN retroalimentaciones t ON t.registroId = r.id
        WHERE b.id = :bitacoraId AND a.docenteId = :docenteId AND r.estadoEnvio = 'ENVIADO'
        ORDER BY r.fechaEnvio DESC
        """
    )
    fun obtenerEnviadosParaDocente(bitacoraId: Int, docenteId: Int): Flow<List<RegistroDetalle>>

    @Query(
        """
        SELECT r.id AS id, r.bitacoraId AS bitacoraId, r.estudianteId AS estudianteId,
               r.fechaClase AS fechaClase, r.contenido AS contenido, r.actividad AS actividad,
               r.aprendizaje AS aprendizaje, r.dudas AS dudas, r.fotoUri AS fotoUri,
               r.enlace AS enlace, r.estadoEnvio AS estadoEnvio,
               r.fechaCreacion AS fechaCreacion, r.fechaEnvio AS fechaEnvio,
               b.titulo AS bitacoraTitulo, u.nombre AS estudianteNombre,
               COALESCE(t.comentario, '') AS comentarioDocente,
               COALESCE(t.fecha, 0) AS fechaRetro,
               CASE WHEN t.id IS NULL THEN 'PENDIENTE' ELSE 'REVISADO' END AS estadoRevision
        FROM registros r
        INNER JOIN bitacoras b ON b.id = r.bitacoraId
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        INNER JOIN usuarios u ON u.id = r.estudianteId
        LEFT JOIN retroalimentaciones t ON t.registroId = r.id
        WHERE r.id = :id AND a.docenteId = :docenteId AND r.estadoEnvio = 'ENVIADO'
        """
    )
    suspend fun obtenerParaDocente(id: Int, docenteId: Int): List<RegistroDetalle>

    // Cuántos registros ENVIADOS de este docente tienen ese id (0 = no puede revisarlo)
    @Query(
        """
        SELECT COUNT(*) FROM registros r
        INNER JOIN bitacoras b ON b.id = r.bitacoraId
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        WHERE r.id = :id AND a.docenteId = :docenteId AND r.estadoEnvio = 'ENVIADO'
        """
    )
    suspend fun contarRevisables(id: Int, docenteId: Int): Int

    // Si el registro ya tenía retroalimentación, se actualiza el comentario
    @Query(
        """
        UPDATE retroalimentaciones SET comentario = :comentario, fecha = :fecha, docenteId = :docenteId
        WHERE registroId = :registroId
        """
    )
    suspend fun actualizarRetroalimentacion(registroId: Int, comentario: String, fecha: Long, docenteId: Int)

    @Query("SELECT COUNT(*) FROM retroalimentaciones WHERE registroId = :registroId")
    suspend fun contarRetroalimentaciones(registroId: Int): Int

    // Aportes ENVIADOS sin retroalimentación, en TODAS las bitácoras del docente (para el resumen del inicio)
    @Query(
        """
        SELECT COUNT(*) FROM registros r
        INNER JOIN bitacoras b ON b.id = r.bitacoraId
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        LEFT JOIN retroalimentaciones t ON t.registroId = r.id
        WHERE a.docenteId = :docenteId AND r.estadoEnvio = 'ENVIADO' AND t.id IS NULL
        """
    )
    fun contarPendientesDeDocente(docenteId: Int): Flow<Int>
}
