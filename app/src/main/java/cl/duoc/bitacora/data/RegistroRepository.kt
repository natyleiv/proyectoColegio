package cl.duoc.bitacora.data

import cl.duoc.bitacora.data.remote.BitacorasApi
import cl.duoc.bitacora.data.remote.MapeoRemoto
import cl.duoc.bitacora.data.remote.RetroalimentacionRemota
import cl.duoc.bitacora.data.remote.RetrofitClient
import cl.duoc.bitacora.data.remote.comprobarRespuesta
import cl.duoc.bitacora.data.remote.comprobarSinCuerpo
import cl.duoc.bitacora.model.EstadoEnvio
import cl.duoc.bitacora.model.Registro
import kotlinx.coroutines.flow.Flow


// Los registros y comentarios viven en ms-bitacoras. Room guarda una copia local que las pantallas observan.
// Escribir: primero el backend; si responde bien, se guarda en Room con el id del servidor.
class RegistroRepository(
    private val registroDao: RegistroDao,
    private val api: BitacorasApi = RetrofitClient.bitacorasApi
) {

    // ---------- Sincronizar (backend -> Room) ----------

    // Historial del estudiante (borradores y enviados) con sus comentarios
    suspend fun sincronizarDeEstudiante(estudianteId: Int) {
        val registros = api.obtenerRegistrosDeEstudiante(estudianteId).map { MapeoRemoto.aRegistro(it) }
        guardarConRetroalimentacion(registros)
    }

    // Aportes enviados de una bitácora (vista del docente) con sus comentarios
    suspend fun sincronizarDeBitacora(bitacoraId: Int) {
        val registros = api.obtenerRegistrosEnviados(bitacoraId).map { MapeoRemoto.aRegistro(it) }
        guardarConRetroalimentacion(registros)
    }

    // Guarda los registros y, para cada uno ENVIADO, trae su comentario si ya fue revisado
    private suspend fun guardarConRetroalimentacion(registros: List<Registro>) {
        registroDao.guardarRegistros(registros)
        for (registro in registros) {
            if (registro.estadoEnvio == EstadoEnvio.ENVIADO) {
                val respuesta = api.obtenerRetroalimentacion(registro.id)
                val retro = respuesta.body()
                // 200 = tiene comentario · 204 = aún sin revisar (body null) -> no se guarda nada
                if (respuesta.isSuccessful && retro != null) {
                    registroDao.guardarRetroalimentaciones(listOf(MapeoRemoto.aRetroalimentacion(retro)))
                }
            }
        }
    }

    // ---------- Escribir (primero el backend) ----------

    suspend fun guardarNuevo(registro: Registro) {
        val respuesta = api.crearRegistro(MapeoRemoto.aRegistroRemoto(registro))
        registroDao.guardarRegistros(listOf(MapeoRemoto.aRegistro(comprobarRespuesta(respuesta))))
    }

    // Editar un borrador (y si viene como ENVIADO, también lo envía)
    suspend fun actualizar(registro: Registro) {
        val respuesta = api.actualizarRegistro(registro.id, MapeoRemoto.aRegistroRemoto(registro))
        registroDao.guardarRegistros(listOf(MapeoRemoto.aRegistro(comprobarRespuesta(respuesta))))
    }

    // Elimina un borrador del estudiante (lo enviado no se puede borrar: queda para el docente)
    suspend fun borrar(id: Int, estudianteId: Int) {
        val registro = registroDao.obtenerCrudoDeEstudiante(id, estudianteId).firstOrNull()
        if (registro == null || registro.estadoEnvio != EstadoEnvio.BORRADOR) {
            throw IllegalStateException("El registro no se puede eliminar")
        }
        comprobarSinCuerpo(api.borrarRegistro(id))
        registroDao.borrarRegistro(id)
    }

    // Guarda la retroalimentación (el backend la crea o la actualiza).
    // Si el registro no es de una bitácora de este docente, lanza error.
    suspend fun revisar(registroId: Int, comentario: String, docenteId: Int) {
        if (registroDao.contarRevisables(registroId, docenteId) == 0) {
            throw IllegalStateException("El registro no se puede revisar")
        }
        val datos = RetroalimentacionRemota(docenteId = docenteId, comentario = comentario)
        val guardada = comprobarRespuesta(api.guardarRetroalimentacion(registroId, datos))
        registroDao.guardarRetroalimentaciones(listOf(MapeoRemoto.aRetroalimentacion(guardada)))
    }

    // ---------- Leer (desde Room) ----------

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

    fun contarPendientesDeDocente(docenteId: Int): Flow<Int> {
        return registroDao.contarPendientesDeDocente(docenteId)
    }
}