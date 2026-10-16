package cl.duoc.bitacora.data

import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.PreguntaOrientadora
import kotlinx.coroutines.flow.Flow

class BitacoraRepository(private val bitacoraDao: BitacoraDao) {

    // Guarda la bitácora y sus preguntas orientadoras (una fila por pregunta)
    suspend fun guardar(bitacora: Bitacora, preguntas: List<String>) {
        val id = bitacoraDao.siguienteId()
        bitacoraDao.insertar(bitacora.copy(id = id))
        guardarPreguntas(id, preguntas)
    }

    // Edita una bitácora en BORRADOR del docente. Si no corresponde, lanza error
    // (la pantalla lo muestra como mensaje de error).
    suspend fun editar(
        id: Int, docenteId: Int, asignacionId: Int, titulo: String,
        proposito: String, instrucciones: String, mes: Int, anio: Int,
        preguntas: List<String>
    ) {
        if (bitacoraDao.contarEditables(id, docenteId) == 0) {
            throw IllegalStateException("La bitácora no se puede editar")
        }
        bitacoraDao.editar(id, docenteId, asignacionId, titulo, proposito, instrucciones, mes, anio)
        bitacoraDao.borrarPreguntas(id)
        guardarPreguntas(id, preguntas)
    }

    private suspend fun guardarPreguntas(bitacoraId: Int, preguntas: List<String>) {
        preguntas.forEachIndexed { indice, texto ->
            bitacoraDao.insertarPregunta(
                PreguntaOrientadora(bitacoraId = bitacoraId, orden = indice + 1, texto = texto)
            )
        }
    }

    suspend fun publicar(id: Int, docenteId: Int, fecha: String) {
        bitacoraDao.publicar(id, docenteId, fecha)
    }

    suspend fun cerrar(id: Int, docenteId: Int, fecha: String) {
        bitacoraDao.cerrar(id, docenteId, fecha)
    }

    fun obtenerPorDocente(docenteId: Int): Flow<List<BitacoraDetalle>> {
        return bitacoraDao.obtenerPorDocente(docenteId)
    }

    fun obtenerParaDocente(id: Int, docenteId: Int): Flow<List<BitacoraDetalle>> {
        return bitacoraDao.obtenerParaDocente(id, docenteId)
    }

    fun obtenerPublicadasPorCurso(cursoId: Int): Flow<List<BitacoraDetalle>> {
        return bitacoraDao.obtenerPublicadasPorCurso(cursoId)
    }

    // null si la bitácora no existe, no está publicada o no es del curso del estudiante
    suspend fun obtenerPublicadaParaEstudiante(id: Int, cursoId: Int): BitacoraDetalle? {
        return bitacoraDao.obtenerPublicadaParaEstudiante(id, cursoId).firstOrNull()
    }

    fun obtenerPreguntas(bitacoraId: Int): Flow<List<PreguntaOrientadora>> {
        return bitacoraDao.obtenerPreguntas(bitacoraId)
    }

    suspend fun obtenerPreguntasUnaVez(bitacoraId: Int): List<PreguntaOrientadora> {
        return bitacoraDao.obtenerPreguntasUnaVez(bitacoraId)
    }
}
