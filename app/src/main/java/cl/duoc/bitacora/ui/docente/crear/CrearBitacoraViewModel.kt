package cl.duoc.bitacora.ui.docente.crear

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.BitacoraRepository
import cl.duoc.bitacora.data.UsuarioRepository
import cl.duoc.bitacora.data.AsignacionDetalle
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.anioDeCalendario
import cl.duoc.bitacora.util.mesDeCalendario
import cl.duoc.bitacora.util.validarAnio
import cl.duoc.bitacora.util.validarAsignacionSeleccionada
import cl.duoc.bitacora.util.validarCampoObligatorio
import cl.duoc.bitacora.util.validarLargoMinimo
import cl.duoc.bitacora.util.validarMes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
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
    private val usuarioRepository = UsuarioRepository(database.usuarioDao(), database.asignacionDao(), database.catalogoDao())

    private val docenteId = Sesion.usuario?.id ?: 0

    // Modo editar: se decide al abrir la pantalla (ver Navegacion) y no cambia mientras dura
    private val modoEdicion = Sesion.modoEdicion
    private val bitacoraEditadaId = Sesion.bitacoraId

    private val _uiState = MutableStateFlow(CrearBitacoraUiState(modoEdicion = modoEdicion))
    val uiState: StateFlow<CrearBitacoraUiState> = _uiState.asStateFlow()

    init {
        // Si se está editando, carga los datos actuales de la bitácora en el formulario
        if (modoEdicion) {
            viewModelScope.launch {
                try {
                    val encontradas = bitacoraRepository
                        .obtenerParaDocente(bitacoraEditadaId, docenteId)
                        .first()
                    val actual = encontradas.firstOrNull()
                    if (actual == null || actual.estado != EstadoBitacora.BORRADOR) {
                        // No existe, no es de este docente o ya fue publicada
                        Sesion.error("Esta bitácora ya no se puede editar.")
                        _uiState.update { it.copy(guardada = true) }
                    } else {
                        val preguntas = bitacoraRepository.obtenerPreguntasUnaVez(actual.id)
                        _uiState.update {
                            it.copy(
                                asignacionId = actual.asignacionId,
                                titulo = actual.titulo,
                                proposito = actual.proposito,
                                instrucciones = actual.instrucciones,
                                preguntas = preguntas.joinToString("\n") { p -> p.texto },
                                mes = actual.mes.toString(),
                                anio = actual.anio.toString()
                            )
                        }
                    }
                } catch (e: Exception) {
                    Sesion.error("No se pudo cargar la bitácora.")
                    _uiState.update { it.copy(guardada = true) }
                }
            }
        }
    }

    // Solo se ofrecen los cursos/asignaturas asignados a este docente
    val asignaciones: StateFlow<List<AsignacionDetalle>> =
        usuarioRepository
            .obtenerAsignaciones(docenteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Cada cambio guarda el valor y, si el campo tenía error, lo quita apenas sea válido
    fun cambiarAsignacion(id: Int) {
        _uiState.update {
            it.copy(
                asignacionId = id,
                errorAsignacion = it.errorAsignacion && !validarAsignacionSeleccionada(id),
                errorDuplicada = false
            )
        }
    }

    fun cambiarTitulo(valor: String) {
        _uiState.update {
            it.copy(titulo = valor, errorTitulo = it.errorTitulo && !validarLargoMinimo(valor, 5))
        }
    }

    fun cambiarProposito(valor: String) {
        _uiState.update {
            it.copy(proposito = valor, errorProposito = it.errorProposito && !validarCampoObligatorio(valor))
        }
    }

    fun cambiarInstrucciones(valor: String) {
        _uiState.update {
            it.copy(instrucciones = valor, errorInstrucciones = it.errorInstrucciones && !validarCampoObligatorio(valor))
        }
    }

    fun cambiarPreguntas(valor: String) {
        _uiState.update {
            it.copy(preguntas = valor, errorPreguntas = it.errorPreguntas && !validarCampoObligatorio(valor))
        }
    }

    // El calendario entrega el día elegido (milisegundos UTC); de ahí se toman mes y año
    fun elegirMesAnio(millisUtc: Long) {
        val mesElegido = mesDeCalendario(millisUtc)
        val anioElegido = anioDeCalendario(millisUtc)
        _uiState.update {
            it.copy(
                mes = mesElegido.toString(),
                anio = anioElegido.toString(),
                errorMes = false,
                errorAnio = false,
                errorDuplicada = false
            )
        }
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

            // Una pregunta por línea; las líneas vacías se ignoran
            val preguntas = estado.preguntas
                .lines()
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            viewModelScope.launch {
                // Si falla la base de datos se muestra un mensaje de ERROR (no se cierra la app)
                try {
                    // Regla de negocio: una sola bitácora por curso, asignatura, mes y año
                    val idExcluir = if (modoEdicion) bitacoraEditadaId else 0
                    val duplicada = bitacoraRepository.existeDuplicada(
                        asignacion.id,
                        estado.mes.trim().toInt(),
                        estado.anio.trim().toInt(),
                        idExcluir
                    )
                    if (duplicada) {
                        _uiState.update { it.copy(errorDuplicada = true) }
                        return@launch
                    }

                    if (modoEdicion) {
                        bitacoraRepository.editar(
                            id = bitacoraEditadaId,
                            docenteId = docenteId,
                            asignacionId = asignacion.id,
                            titulo = estado.titulo.trim(),
                            proposito = estado.proposito.trim(),
                            instrucciones = estado.instrucciones.trim(),
                            mes = estado.mes.trim().toInt(),
                            anio = estado.anio.trim().toInt(),
                            preguntas = preguntas
                        )
                        Sesion.exito("Cambios guardados en la bitácora.")
                    } else {
                        val bitacora = Bitacora(
                            asignacionId = asignacion.id,
                            titulo = estado.titulo.trim(),
                            proposito = estado.proposito.trim(),
                            instrucciones = estado.instrucciones.trim(),
                            mes = estado.mes.trim().toInt(),
                            anio = estado.anio.trim().toInt(),
                            estado = EstadoBitacora.BORRADOR
                        )
                        bitacoraRepository.guardar(bitacora, preguntas)
                        Sesion.exito("Bitácora creada como borrador. Ábrela para publicarla.")
                    }
                    _uiState.update { it.copy(guardada = true) }
                } catch (e: Exception) {
                    Sesion.error("No se pudo guardar la bitácora. Intenta nuevamente.")
                }
            }
        }
    }
}
