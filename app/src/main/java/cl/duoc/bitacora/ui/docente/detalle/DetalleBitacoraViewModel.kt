package cl.duoc.bitacora.ui.docente.detalle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.BitacoraRepository
import cl.duoc.bitacora.data.RegistroRepository
import cl.duoc.bitacora.data.UsuarioRepository
import cl.duoc.bitacora.data.BitacoraDetalle
import cl.duoc.bitacora.model.PreguntaOrientadora
import cl.duoc.bitacora.model.EstadoRevision
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.fechaHoy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetalleBitacoraViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val bitacoraRepository = BitacoraRepository(database.bitacoraDao())
    private val registroRepository = RegistroRepository(database.registroDao())
    private val usuarioRepository = UsuarioRepository(database.usuarioDao(), database.asignacionDao(), database.catalogoDao())

    private val bitacoraId = Sesion.bitacoraId
    private val docenteId = Sesion.usuario?.id ?: 0

    // null = todavía cargando · lista vacía = sin acceso · lista con 1 = la bitácora
    val bitacora: StateFlow<List<BitacoraDetalle>?> =
        bitacoraRepository
            .obtenerParaDocente(bitacoraId, docenteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )

    // Preguntas orientadoras (tabla aparte, una fila por pregunta)
    val preguntas: StateFlow<List<PreguntaOrientadora>> =
        bitacoraRepository
            .obtenerPreguntas(bitacoraId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val registros =
        registroRepository.obtenerEnviadosParaDocente(bitacoraId, docenteId)

    private val estudiantes =
        usuarioRepository.obtenerEstudiantesDeBitacora(bitacoraId, docenteId)

    private val filtroEstado = MutableStateFlow(FILTRO_TODOS)
    private val busqueda = MutableStateFlow("")

    // Une las 4 fuentes (registros, estudiantes, filtro y búsqueda) en un solo estado
    val uiState: StateFlow<DetalleBitacoraUiState> =
        combine(registros, estudiantes, filtroEstado, busqueda) { regs, ests, filtro, texto ->
            val idsQueParticiparon = regs.map { it.estudianteId }.toSet()
            val sinParticipar = ests
                .filter { it.id !in idsQueParticiparon }
                .map { it.nombre }

            val porEstado = when (filtro) {
                FILTRO_PENDIENTES -> regs.filter { it.estadoRevision == EstadoRevision.PENDIENTE }
                FILTRO_REVISADOS -> regs.filter { it.estadoRevision == EstadoRevision.REVISADO }
                else -> regs
            }
            val textoLimpio = texto.trim()
            val filtrados = if (textoLimpio.isEmpty()) {
                porEstado
            } else {
                porEstado.filter {
                    it.estudianteNombre.contains(textoLimpio, ignoreCase = true) ||
                            it.fechaClase.contains(textoLimpio)
                }
            }

            DetalleBitacoraUiState(
                registros = filtrados,
                filtroEstado = filtro,
                busqueda = texto,
                totalEstudiantes = ests.size,
                participaron = idsQueParticiparon.size,
                sinParticipacion = sinParticipar,
                totalAportes = regs.size,
                revisados = regs.count { it.estadoRevision == EstadoRevision.REVISADO },
                pendientes = regs.count { it.estadoRevision == EstadoRevision.PENDIENTE }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DetalleBitacoraUiState()
        )

    fun cambiarFiltro(nuevoFiltro: String) {
        filtroEstado.update { nuevoFiltro }
    }

    fun cambiarBusqueda(texto: String) {
        busqueda.update { texto }
    }

    fun publicar() {
        viewModelScope.launch {
            try {
                bitacoraRepository.publicar(bitacoraId, docenteId, fechaHoy())
                Sesion.exito("Bitácora publicada: los estudiantes ya pueden participar.")
            } catch (e: Exception) {
                Sesion.error("No se pudo publicar la bitácora. Intenta nuevamente.")
            }
        }
    }

    fun cerrar() {
        viewModelScope.launch {
            try {
                bitacoraRepository.cerrar(bitacoraId, docenteId, fechaHoy())
                Sesion.advertencia("Bitácora cerrada: ya no se reciben más aportes.")
            } catch (e: Exception) {
                Sesion.error("No se pudo cerrar la bitácora. Intenta nuevamente.")
            }
        }
    }
}
