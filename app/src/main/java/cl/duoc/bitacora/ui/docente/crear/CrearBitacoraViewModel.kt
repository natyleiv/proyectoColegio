package cl.duoc.bitacora.ui.docente.crear

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.BitacoraRepository
import cl.duoc.bitacora.data.UsuarioRepository
import cl.duoc.bitacora.model.Asignacion
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.validarAnio
import cl.duoc.bitacora.util.validarAsignacionSeleccionada
import cl.duoc.bitacora.util.validarCampoObligatorio
import cl.duoc.bitacora.util.validarLargoMinimo
import cl.duoc.bitacora.util.validarMes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrearBitacoraViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val bitacoraRepository = BitacoraRepository(database.bitacoraDao())
    private val usuarioRepository = UsuarioRepository(database.usuarioDao(), database.asignacionDao())

    private val docenteId = Sesion.usuario?.id ?: 0

    private val _uiState = MutableStateFlow(CrearBitacoraUiState())
    val uiState: StateFlow<CrearBitacoraUiState> = _uiState.asStateFlow()

    // Solo se ofrecen los cursos/asignaturas asignados a este docente
    val asignaciones: StateFlow<List<Asignacion>> =
        usuarioRepository
            .obtenerAsignaciones(docenteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun cambiarAsignacion(id: Int) {
        _uiState.update { it.copy(asignacionId = id) }
    }

    fun cambiarTitulo(valor: String) {
        _uiState.update { it.copy(titulo = valor) }
    }

    fun cambiarProposito(valor: String) {
        _uiState.update { it.copy(proposito = valor) }
    }

    fun cambiarInstrucciones(valor: String) {
        _uiState.update { it.copy(instrucciones = valor) }
    }

    fun cambiarPreguntas(valor: String) {
        _uiState.update { it.copy(preguntas = valor) }
    }

    fun cambiarMes(valor: String) {
        _uiState.update { it.copy(mes = valor) }
    }

    fun cambiarAnio(valor: String) {
        _uiState.update { it.copy(anio = valor) }
    }

    // Valida cada campo y, si todo está bien, guarda la bitácora como BORRADOR
    fun guardar() {
        val estado = _uiState.value
        val errorAsignacion = !validarAsignacionSeleccionada(estado.asignacionId)
        val errorTitulo = !validarLargoMinimo(estado.titulo, 5)
        val errorProposito = !validarCampoObligatorio(estado.proposito)
        val errorInstrucciones = !validarCampoObligatorio(estado.instrucciones)
        val errorPreguntas = !validarCampoObligatorio(estado.preguntas)
        val errorMes = !validarMes(estado.mes)
        val errorAnio = !validarAnio(estado.anio)

        _uiState.update {
            it.copy(
                errorAsignacion = errorAsignacion,
                errorTitulo = errorTitulo,
                errorProposito = errorProposito,
                errorInstrucciones = errorInstrucciones,
                errorPreguntas = errorPreguntas,
                errorMes = errorMes,
                errorAnio = errorAnio
            )
        }

        val formularioValido =
            !errorAsignacion &&
                    !errorTitulo &&
                    !errorProposito &&
                    !errorInstrucciones &&
                    !errorPreguntas &&
                    !errorMes &&
                    !errorAnio

        if (formularioValido) {
            // Control de acceso: la asignación debe ser una de las del docente
            val asignacion = asignaciones.value.firstOrNull { it.id == estado.asignacionId }
            if (asignacion == null) {
                _uiState.update { it.copy(errorAsignacion = true) }
                return
            }

            val bitacora = Bitacora(
                titulo = estado.titulo.trim(),
                proposito = estado.proposito.trim(),
                instrucciones = estado.instrucciones.trim(),
                preguntas = estado.preguntas.trim(),
                curso = asignacion.curso,
                asignatura = asignacion.asignatura,
                mes = estado.mes.trim().toInt(),
                anio = estado.anio.trim().toInt(),
                docenteId = docenteId,
                estado = EstadoBitacora.BORRADOR
            )
            viewModelScope.launch {
                bitacoraRepository.guardar(bitacora)
                Sesion.mensaje = "Bitácora creada como borrador. Ábrela para publicarla."
                _uiState.update { it.copy(guardada = true) }
            }
        }
    }
}
