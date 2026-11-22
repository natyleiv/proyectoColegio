package cl.duoc.bitacora.data

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.model.Retroalimentacion
import kotlinx.coroutines.flow.Flow

// Room guarda una copia local de los registros y comentarios de ms-bitacoras.
// Las escrituras van primero al backend; aquí solo se guarda lo que el backend aceptó.
@Dao
interface RegistroDao {

    // ---------- Guardar lo que llega del backend (inserta o actualiza, sin borrar) ----------
    @Upsert
    suspend fun guardarRegistros(registros: List<Registro>)

    @Upsert
    suspend fun guardarRetroalimentaciones(retros: List<Retroalimentacion>)

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

    // Control de acceso: cuántos registros ENVIADOS de este docente tienen ese id
    // (0 = no puede revisarlo). El backend no sabe qué docente inició sesión.
    @Query(
        """
        SELECT COUNT(*) FROM registros r
        INNER JOIN bitacoras b ON b.id = r.bitacoraId
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        WHERE r.id = :id AND a.docenteId = :docenteId AND r.estadoEnvio = 'ENVIADO'
        """
    )
    suspend fun contarRevisables(id: Int, docenteId: Int): Int

    // Aportes ENVIADOS sin retroalimentación, en TODAS las bitácoras del docente (resumen del inicio)
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