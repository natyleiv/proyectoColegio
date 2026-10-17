package cl.duoc.bitacora.data

import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.model.Retroalimentacion
import kotlinx.coroutines.flow.Flow

class RegistroRepository(private val registroDao: RegistroDao) {

    suspend fun guardarNuevo(registro: Registro) {
        registroDao.insertar(registro)
    }

    suspend fun actualizar(registro: Registro) {
        registroDao.actualizar(registro)
    }

    fun obtenerPorEstudiante(estudianteId: Int): Flow<List<RegistroDetalle>> {
        return registroDao.obtenerPorEstudiante(estudianteId)
    }

    // Registro con nombres resueltos (para mostrar el detalle)
    suspend fun obtenerDeEstudiante(id: Int, estudianteId: Int): RegistroDetalle? {
        return registroDao.obtenerDeEstudiante(id, estudianteId).firstOrNull()
    }

    // Registro "crudo" de la tabla (para cargar un borrador en el formulario y reenviarlo)
    suspend fun obtenerCrudoDeEstudiante(id: Int, estudianteId: Int): Registro? {
        return registroDao.obtenerCrudoDeEstudiante(id, estudianteId).firstOrNull()
    }

    fun obtenerEnviadosParaDocente(bitacoraId: Int, docenteId: Int): Flow<List<RegistroDetalle>> {
        return registroDao.obtenerEnviadosParaDocente(bitacoraId, docenteId)
    }

    suspend fun obtenerParaDocente(id: Int, docenteId: Int): RegistroDetalle? {
        return registroDao.obtenerParaDocente(id, docenteId).firstOrNull()
    }

    // Guarda la retroalimentación (crea la fila o actualiza la existente).
    // Si el registro no es de una bitácora de este docente, lanza error.
    suspend fun revisar(registroId: Int, comentario: String, docenteId: Int) {
        if (registroDao.contarRevisables(registroId, docenteId) == 0) {
            throw IllegalStateException("El registro no se puede revisar")
        }
        val ahora = System.currentTimeMillis()
        if (registroDao.contarRetroalimentaciones(registroId) == 0) {
            registroDao.insertarRetroalimentacion(
                Retroalimentacion(
                    registroId = registroId,
                    docenteId = docenteId,
                    comentario = comentario,
                    fecha = ahora
                )
            )
        } else {
            registroDao.actualizarRetroalimentacion(registroId, comentario, ahora, docenteId)
        }
    }

    fun contarPendientesDeDocente(docenteId: Int): Flow<Int> {
        return registroDao.contarPendientesDeDocente(docenteId)
    }
}
