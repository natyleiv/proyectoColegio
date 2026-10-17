package cl.duoc.bitacora.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.PreguntaOrientadora
import kotlinx.coroutines.flow.Flow

@Dao
interface BitacoraDao {

    @Insert
    suspend fun insertar(bitacora: Bitacora)

    @Insert
    suspend fun insertarPregunta(pregunta: PreguntaOrientadora)

    // Siguiente id libre (se asigna a mano para poder crear las preguntas con su bitacoraId)
    @Query("SELECT COALESCE(MAX(id), 0) + 1 FROM bitacoras")
    suspend fun siguienteId(): Int

    // Parte común de los SELECT con JOIN (se repite en cada consulta)
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

    // Solo el docente dueño puede publicar y solo desde BORRADOR
    @Query(
        """
        UPDATE bitacoras SET estado = 'PUBLICADA', fechaPublicacion = :fecha
        WHERE id = :id AND estado = 'BORRADOR'
          AND asignacionId IN (SELECT id FROM asignaciones WHERE docenteId = :docenteId)
        """
    )
    suspend fun publicar(id: Int, docenteId: Int, fecha: String)

    // Solo el docente dueño puede cerrar y solo desde PUBLICADA
    @Query(
        """
        UPDATE bitacoras SET estado = 'CERRADA', fechaCierre = :fecha
        WHERE id = :id AND estado = 'PUBLICADA'
          AND asignacionId IN (SELECT id FROM asignaciones WHERE docenteId = :docenteId)
        """
    )
    suspend fun cerrar(id: Int, docenteId: Int, fecha: String)

    // EDITAR: solo el docente dueño y solo mientras la bitácora sea BORRADOR
    @Query(
        """
        UPDATE bitacoras
        SET asignacionId = :asignacionId, titulo = :titulo, proposito = :proposito,
            instrucciones = :instrucciones, mes = :mes, anio = :anio
        WHERE id = :id AND estado = 'BORRADOR'
          AND asignacionId IN (SELECT id FROM asignaciones WHERE docenteId = :docenteId)
          AND :asignacionId IN (SELECT id FROM asignaciones WHERE docenteId = :docenteId)
        """
    )
    suspend fun editar(
        id: Int, docenteId: Int, asignacionId: Int, titulo: String,
        proposito: String, instrucciones: String, mes: Int, anio: Int
    )

    // Cuántas bitácoras en BORRADOR de este docente tienen ese id (0 = no se puede editar)
    @Query(
        """
        SELECT COUNT(*) FROM bitacoras b
        INNER JOIN asignaciones a ON a.id = b.asignacionId
        WHERE b.id = :id AND a.docenteId = :docenteId AND b.estado = 'BORRADOR'
        """
    )
    suspend fun contarEditables(id: Int, docenteId: Int): Int

    // Al editar se reemplazan las preguntas (se borran y se vuelven a crear)
    @Query("DELETE FROM preguntas_orientadoras WHERE bitacoraId = :bitacoraId")
    suspend fun borrarPreguntas(bitacoraId: Int)


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
