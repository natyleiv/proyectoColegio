package cl.duoc.bitacora.data

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.PreguntaOrientadora
import kotlinx.coroutines.flow.Flow

// Room guarda una copia local de las bitácoras de ms-bitacoras.
// Las escrituras van primero al backend; aquí solo se guarda lo que el backend aceptó.
@Dao
interface BitacoraDao {

    // ---------- Guardar lo que llega del backend ----------
    // Inserta o actualiza sin borrar (así no se dispara el CASCADE sobre los registros)
    @Upsert
    suspend fun guardarBitacoras(bitacoras: List<Bitacora>)

    @Upsert
    suspend fun guardarPreguntas(preguntas: List<PreguntaOrientadora>)

    // Quita de Room las bitácoras que ya no existen en el servidor
    @Query("DELETE FROM bitacoras WHERE id NOT IN (:ids)")
    suspend fun borrarLasQueNoEstan(ids: List<Int>)

    // Al reemplazar las preguntas se borran las anteriores
    @Query("DELETE FROM preguntas_orientadoras WHERE bitacoraId = :bitacoraId")
    suspend fun borrarPreguntas(bitacoraId: Int)

    // ---------- Consultas para las pantallas ----------
    // bitacoras b -> asignaciones a (docente) -> cursos c / asignaturas s

    // Solo las bitácoras del docente que inició sesión
    @Query(
        """
        SELECT b.id AS id, b.asignacionId AS asignacionId, b.titulo AS titulo,
               b.proposito AS proposito, b.instrucciones AS instrucciones,
               b.mes AS mes, b.anio AS anio, b.estado AS estado,
               b.fechaPublicacion AS fechaPublicacion, b.fechaCierre AS fechaCierre,
               a.docenteId AS docenteId, a.cursoId AS cursoId,
               c.nombre AS curso, s.nombre AS asignatura
        FROM bitacoras b
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        INNER JOIN cursos c ON c.id = a.cursoId
        INNER JOIN asignaturas s ON s.id = a.asignaturaId
        WHERE a.docenteId = :docenteId
        ORDER BY b.anio DESC, b.mes DESC, b.id DESC
        """
    )
    fun obtenerPorDocente(docenteId: Int): Flow<List<BitacoraDetalle>>

    // Devuelve una lista: vacía si la bitácora no existe o no es de este docente
    @Query(
        """
        SELECT b.id AS id, b.asignacionId AS asignacionId, b.titulo AS titulo,
               b.proposito AS proposito, b.instrucciones AS instrucciones,
               b.mes AS mes, b.anio AS anio, b.estado AS estado,
               b.fechaPublicacion AS fechaPublicacion, b.fechaCierre AS fechaCierre,
               a.docenteId AS docenteId, a.cursoId AS cursoId,
               c.nombre AS curso, s.nombre AS asignatura
        FROM bitacoras b
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        INNER JOIN cursos c ON c.id = a.cursoId
        INNER JOIN asignaturas s ON s.id = a.asignaturaId
        WHERE b.id = :id AND a.docenteId = :docenteId
        """
    )
    fun obtenerParaDocente(id: Int, docenteId: Int): Flow<List<BitacoraDetalle>>

    // Solo las bitácoras publicadas del curso del estudiante
    @Query(
        """
        SELECT b.id AS id, b.asignacionId AS asignacionId, b.titulo AS titulo,
               b.proposito AS proposito, b.instrucciones AS instrucciones,
               b.mes AS mes, b.anio AS anio, b.estado AS estado,
               b.fechaPublicacion AS fechaPublicacion, b.fechaCierre AS fechaCierre,
               a.docenteId AS docenteId, a.cursoId AS cursoId,
               c.nombre AS curso, s.nombre AS asignatura
        FROM bitacoras b
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        INNER JOIN cursos c ON c.id = a.cursoId
        INNER JOIN asignaturas s ON s.id = a.asignaturaId
        WHERE a.cursoId = :cursoId AND b.estado = 'PUBLICADA'
        ORDER BY b.anio DESC, b.mes DESC, b.id DESC
        """
    )
    fun obtenerPublicadasPorCurso(cursoId: Int): Flow<List<BitacoraDetalle>>

    // Una bitácora publicada del curso del estudiante (lista vacía si no corresponde)
    @Query(
        """
        SELECT b.id AS id, b.asignacionId AS asignacionId, b.titulo AS titulo,
               b.proposito AS proposito, b.instrucciones AS instrucciones,
               b.mes AS mes, b.anio AS anio, b.estado AS estado,
               b.fechaPublicacion AS fechaPublicacion, b.fechaCierre AS fechaCierre,
               a.docenteId AS docenteId, a.cursoId AS cursoId,
               c.nombre AS curso, s.nombre AS asignatura
        FROM bitacoras b
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        INNER JOIN cursos c ON c.id = a.cursoId
        INNER JOIN asignaturas s ON s.id = a.asignaturaId
        WHERE b.id = :id AND a.cursoId = :cursoId AND b.estado = 'PUBLICADA'
        """
    )
    suspend fun obtenerPublicadaParaEstudiante(id: Int, cursoId: Int): List<BitacoraDetalle>

    // Preguntas orientadoras de una bitácora, en orden
    @Query("SELECT * FROM preguntas_orientadoras WHERE bitacoraId = :bitacoraId ORDER BY orden")
    fun obtenerPreguntas(bitacoraId: Int): Flow<List<PreguntaOrientadora>>

    @Query("SELECT * FROM preguntas_orientadoras WHERE bitacoraId = :bitacoraId ORDER BY orden")
    suspend fun obtenerPreguntasUnaVez(bitacoraId: Int): List<PreguntaOrientadora>

    // ---------- Control de acceso (el backend no sabe qué docente inició sesión) ----------

    // Cuántas bitácoras de este docente tienen ese id (0 = no es suya)
    @Query(
        """
        SELECT COUNT(*) FROM bitacoras b
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        WHERE b.id = :id AND a.docenteId = :docenteId
        """
    )
    suspend fun contarDelDocente(id: Int, docenteId: Int): Int

    // Cuántas bitácoras en BORRADOR de este docente tienen ese id (0 = no se puede editar)
    @Query(
        """
        SELECT COUNT(*) FROM bitacoras b
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        WHERE b.id = :id AND a.docenteId = :docenteId AND b.estado = 'BORRADOR'
        """
    )
    suspend fun contarEditables(id: Int, docenteId: Int): Int

    // Cuenta bitácoras de la misma asignación (curso + asignatura), mes y año.
    // idExcluir: al EDITAR se excluye la propia bitácora (al crear se pasa 0).
    @Query(
        """
        SELECT COUNT(*) FROM bitacoras
        WHERE asignacionId = :asignacionId AND mes = :mes AND anio = :anio AND id != :idExcluir
        """
    )
    suspend fun contarDuplicadas(asignacionId: Int, mes: Int, anio: Int, idExcluir: Int): Int
}