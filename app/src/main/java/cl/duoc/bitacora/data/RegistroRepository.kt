package cl.duoc.bitacora.data

import cl.duoc.bitacora.model.Registro
import kotlinx.coroutines.flow.Flow

class RegistroRepository(private val registroDao: RegistroDao) {

    suspend fun guardarNuevo(registro: Registro) {
        registroDao.insertar(registro)
    }

    suspend fun actualizar(registro: Registro) {
        registroDao.actualizar(registro)
    }

    fun obtenerPorEstudiante(estudianteId: Int): Flow<List<Registro>> {
        return registroDao.obtenerPorEstudiante(estudianteId)
    }

    suspend fun obtenerDeEstudiante(id: Int, estudianteId: Int): Registro? {
        return registroDao.obtenerDeEstudiante(id, estudianteId)
    }

    fun obtenerEnviadosParaDocente(bitacoraId: Int, docenteId: Int): Flow<List<Registro>> {
        return registroDao.obtenerEnviadosParaDocente(bitacoraId, docenteId)
    }

    suspend fun obtenerParaDocente(id: Int, docenteId: Int): Registro? {
        return registroDao.obtenerParaDocente(id, docenteId)
    }

    suspend fun revisar(id: Int, comentario: String, docenteId: Int) {
        registroDao.revisar(id, comentario, System.currentTimeMillis(), docenteId)
    }
}
