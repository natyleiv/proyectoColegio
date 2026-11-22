package cl.duoc.bitacora.data

import cl.duoc.bitacora.data.remote.BitacorasApi
import cl.duoc.bitacora.data.remote.MapeoRemoto
import cl.duoc.bitacora.data.remote.PreguntaRemota
import cl.duoc.bitacora.data.remote.RetrofitClient
import cl.duoc.bitacora.data.remote.comprobarRespuesta
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.PreguntaOrientadora
import kotlinx.coroutines.flow.Flow

// Las bitácoras viven en ms-bitacoras. Room guarda una copia local que las pantallas observan.
// Escribir: primero el backend; si responde bien, se guarda en Room con el id del servidor.
class BitacoraRepository(
    private val bitacoraDao: BitacoraDao,
    private val api: BitacorasApi = RetrofitClient.bitacorasApi
) {

    // Descarga todas las bitácoras y sus preguntas, y deja Room igual al servidor
    suspend fun sincronizar() {
        val bitacoras = api.obtenerBitacoras().map { MapeoRemoto.aBitacora(it) }
        bitacoraDao.guardarBitacoras(bitacoras)
        bitacoraDao.borrarLasQueNoEstan(bitacoras.map { it.id })
        for (bitacora in bitacoras) {
            val preguntas = api.obtenerPreguntas(bitacora.id).map { MapeoRemoto.aPregunta(it) }
            bitacoraDao.borrarPreguntas(bitacora.id)
            bitacoraDao.guardarPreguntas(preguntas)
        }
    }

    // Crea la bitácora (el backend la deja en BORRADOR) y luego sus preguntas
    suspend fun guardar(bitacora: Bitacora, preguntas: List<String>) {
        val respuesta = api.crearBitacora(MapeoRemoto.aBitacoraRemota(bitacora))
        val creada = MapeoRemoto.aBitacora(comprobarRespuesta(respuesta))
        bitacoraDao.guardarBitacoras(listOf(creada))
        guardarPreguntas(creada.id, preguntas)
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
        val datos = Bitacora(
            id = id, asignacionId = asignacionId, titulo = titulo, proposito = proposito,
            instrucciones = instrucciones, mes = mes, anio = anio
        )
        val respuesta = api.actualizarBitacora(id, MapeoRemoto.aBitacoraRemota(datos))
        bitacoraDao.guardarBitacoras(listOf(MapeoRemoto.aBitacora(comprobarRespuesta(respuesta))))
        guardarPreguntas(id, preguntas)
    }

    // Reemplaza las preguntas en el backend y guarda en Room las que devolvió (con sus ids)
    private suspend fun guardarPreguntas(bitacoraId: Int, preguntas: List<String>) {
        val enviadas = preguntas.map { PreguntaRemota(texto = it) }
        val guardadas = comprobarRespuesta(api.reemplazarPreguntas(bitacoraId, enviadas))
        bitacoraDao.borrarPreguntas(bitacoraId)
        bitacoraDao.guardarPreguntas(guardadas.map { MapeoRemoto.aPregunta(it) })
    }

    // La fecha de publicación y de cierre ahora la pone el backend
    suspend fun publicar(id: Int, docenteId: Int) {
        if (bitacoraDao.contarDelDocente(id, docenteId) == 0) {
            throw IllegalStateException("La bitácora no es de este docente")
        }
        val publicada = comprobarRespuesta(api.publicarBitacora(id))
        bitacoraDao.guardarBitacoras(listOf(MapeoRemoto.aBitacora(publicada)))
    }

    suspend fun cerrar(id: Int, docenteId: Int) {
        if (bitacoraDao.contarDelDocente(id, docenteId) == 0) {
            throw IllegalStateException("La bitácora no es de este docente")
        }
        val cerrada = comprobarRespuesta(api.cerrarBitacora(id))
        bitacoraDao.guardarBitacoras(listOf(MapeoRemoto.aBitacora(cerrada)))
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

    // true si ya existe OTRA bitácora del mismo curso, asignatura, mes y año
    suspend fun existeDuplicada(asignacionId: Int, mes: Int, anio: Int, idExcluir: Int): Boolean {
        return bitacoraDao.contarDuplicadas(asignacionId, mes, anio, idExcluir) > 0
    }
}