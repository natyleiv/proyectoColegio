package cl.duoc.bitacora.data

import cl.duoc.bitacora.model.Bitacora
import kotlinx.coroutines.flow.Flow

class BitacoraRepository(private val bitacoraDao: BitacoraDao) {

    suspend fun guardar(bitacora: Bitacora) {
        bitacoraDao.insertar(bitacora)
    }

    suspend fun publicar(id: Int, docenteId: Int, fecha: String) {
        bitacoraDao.publicar(id, docenteId, fecha)
    }

    suspend fun cerrar(id: Int, docenteId: Int, fecha: String) {
        bitacoraDao.cerrar(id, docenteId, fecha)
    }

    fun obtenerPorDocente(docenteId: Int): Flow<List<Bitacora>> {
        return bitacoraDao.obtenerPorDocente(docenteId)
    }

    fun obtenerParaDocente(id: Int, docenteId: Int): Flow<List<Bitacora>> {
        return bitacoraDao.obtenerParaDocente(id, docenteId)
    }

    fun obtenerPublicadasPorCurso(curso: String): Flow<List<Bitacora>> {
        return bitacoraDao.obtenerPublicadasPorCurso(curso)
    }

    suspend fun obtenerPublicadaParaEstudiante(id: Int, curso: String): Bitacora? {
        return bitacoraDao.obtenerPublicadaParaEstudiante(id, curso)
    }
}
