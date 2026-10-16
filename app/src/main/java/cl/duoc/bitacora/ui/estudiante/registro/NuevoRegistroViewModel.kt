package cl.duoc.bitacora.ui.estudiante.registro

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.BitacoraRepository
import cl.duoc.bitacora.data.RegistroRepository
import cl.duoc.bitacora.model.EstadoEnvio
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.copiarImagenAlmacenamientoApp
import cl.duoc.bitacora.util.formatearFechaCalendario
import cl.duoc.bitacora.util.validarCampoObligatorio
import cl.duoc.bitacora.util.validarEnlace
import cl.duoc.bitacora.util.validarFechaClase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NuevoRegistroViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val bitacoraRepository = BitacoraRepository(database.bitacoraDao())
    private val registroRepository = RegistroRepository(database.registroDao())

    private val estudiante = Sesion.usuario
    private val bitacoraId = Sesion.bitacoraId
    private val registroId = Sesion.registroId     // 0 = registro nuevo; otro = editar borrador

    private var fechaCreacionOriginal = System.currentTimeMillis()

    private val _uiState = MutableStateFlow(NuevoRegistroUiState())
    val uiState: StateFlow<NuevoRegistroUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                cargarDatos()
            } catch (e: Exception) {
                _uiState.update { it.copy(cargado = true, sinAcceso = true) }
                Sesion.error("No se pudo cargar el formulario.")
            }
        }
    }

    // Verifica que la bitácora esté publicada y sea del curso del estudiante.
    // Si se está editando un borrador, carga sus datos.
    private suspend fun cargarDatos() {
        if (estudiante == null) {
            _uiState.update { it.copy(cargado = true, sinAcceso = true) }
            return
        }
        val bitacora = bitacoraRepository.obtenerPublicadaParaEstudiante(bitacoraId, estudiante.cursoId ?: 0)
        if (bitacora == null) {
            _uiState.update { it.copy(cargado = true, sinAcceso = true) }
            return
        }
        // Las preguntas orientadoras están en su propia tabla: se muestran numeradas
        val textoPreguntas = bitacoraRepository
            .obtenerPreguntasUnaVez(bitacora.id)
            .joinToString("\n") { "${it.orden}. ${it.texto}" }

        if (registroId != 0) {
            val borrador = registroRepository.obtenerCrudoDeEstudiante(registroId, estudiante.id)
            if (borrador == null || borrador.estadoEnvio != EstadoEnvio.BORRADOR) {
                _uiState.update { it.copy(cargado = true, sinAcceso = true) }
                return
            }
            fechaCreacionOriginal = borrador.fechaCreacion
            _uiState.update {
                it.copy(
                    bitacoraTitulo = bitacora.titulo,
                    instrucciones = bitacora.instrucciones,
                    preguntas = textoPreguntas,
                    cargado = true,
                    fechaClase = borrador.fechaClase,
                    contenido = borrador.contenido,
                    actividad = borrador.actividad,
                    aprendizaje = borrador.aprendizaje,
                    dudas = borrador.dudas,
                    enlace = borrador.enlace,
                    fotoUri = borrador.fotoUri
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    bitacoraTitulo = bitacora.titulo,
                    instrucciones = bitacora.instrucciones,
                    preguntas = textoPreguntas,
                    cargado = true
                )
            }
        }
    }

    // La fecha se elige en el calendario (milisegundos UTC) y se guarda como "dd/MM/yyyy"
    fun elegirFecha(millisUtc: Long) {
        val texto = formatearFechaCalendario(millisUtc)
        _uiState.update {
            it.copy(fechaClase = texto, errorFecha = it.errorFecha && !validarFechaClase(texto))
        }
    }

    fun cambiarContenido(valor: String) {
        _uiState.update {
            it.copy(contenido = valor, errorContenido = it.errorContenido && !validarCampoObligatorio(valor))
        }
    }

    fun cambiarActividad(valor: String) {
        _uiState.update {
            it.copy(actividad = valor, errorActividad = it.errorActividad && !validarCampoObligatorio(valor))
        }
    }

    fun cambiarAprendizaje(valor: String) {
        _uiState.update {
            it.copy(aprendizaje = valor, errorAprendizaje = it.errorAprendizaje && !validarCampoObligatorio(valor))
        }
    }

    fun cambiarDudas(valor: String) {
        _uiState.update { it.copy(dudas = valor) }
    }

    fun cambiarEnlace(valor: String) {
        _uiState.update {
            it.copy(enlace = valor, errorEnlace = it.errorEnlace && !validarEnlace(valor))
        }
    }

    // ---- Evidencias ----
    // Foto de la cámara: la URI ya apunta a un archivo propio de la app
    fun fotoTomada(uri: Uri) {
        _uiState.update { it.copy(fotoUri = uri.toString(), errorFoto = "") }
    }

    // Foto de la galería: se copia a la carpeta de la app (valida el tamaño máximo)
    fun fotoDeGaleria(uri: Uri) {
        viewModelScope.launch {
            val copia = withContext(Dispatchers.IO) {
                copiarImagenAlmacenamientoApp(getApplication<Application>(), uri)
            }
            if (copia == null) {
                _uiState.update { it.copy(errorFoto = "No se pudo adjuntar la imagen (máximo 5 MB).") }
            } else {
                _uiState.update { it.copy(fotoUri = copia.toString(), errorFoto = "") }
            }
        }
    }

    fun quitarFoto() {
        _uiState.update { it.copy(fotoUri = "", errorFoto = "") }
    }

    // ---- Acciones ----
    // Borrador: se exige solo fecha válida y contenido, para poder guardar a medias
    fun guardarBorrador() {
        val estado = _uiState.value
        val errorFecha = !validarFechaClase(estado.fechaClase)
        val errorContenido = !validarCampoObligatorio(estado.contenido)
        val errorEnlace = !validarEnlace(estado.enlace)

        _uiState.update {
            it.copy(errorFecha = errorFecha, errorContenido = errorContenido, errorEnlace = errorEnlace)
        }

        if (!errorFecha && !errorContenido && !errorEnlace) {
            guardar(enviar = false)
        }
    }

    // Enviar: todos los campos obligatorios deben ser válidos
    fun enviar() {
        val estado = _uiState.value
        val errorFecha = !validarFechaClase(estado.fechaClase)
        val errorContenido = !validarCampoObligatorio(estado.contenido)
        val errorActividad = !validarCampoObligatorio(estado.actividad)
        val errorAprendizaje = !validarCampoObligatorio(estado.aprendizaje)
        val errorEnlace = !validarEnlace(estado.enlace)

        _uiState.update {
            it.copy(
                errorFecha = errorFecha,
                errorContenido = errorContenido,
                errorActividad = errorActividad,
                errorAprendizaje = errorAprendizaje,
                errorEnlace = errorEnlace
            )
        }

        val formularioValido =
            !errorFecha &&
                    !errorContenido &&
                    !errorActividad &&
                    !errorAprendizaje &&
                    !errorEnlace

        if (formularioValido) {
            guardar(enviar = true)
        }
    }

    private fun guardar(enviar: Boolean) {
        val estado = _uiState.value
        val alumno = estudiante ?: return

        viewModelScope.launch {
            // Si algo falla se muestra un mensaje de ERROR en vez de cerrar la app
            try {
                // Se vuelve a comprobar que la bitácora siga activa (el docente pudo cerrarla)
                val bitacora = bitacoraRepository.obtenerPublicadaParaEstudiante(bitacoraId, alumno.cursoId ?: 0)
                if (bitacora == null) {
                    _uiState.update { it.copy(sinAcceso = true) }
                    Sesion.error("La bitácora ya no está activa. No se guardó el registro.")
                    return@launch
                }

                val ahora = System.currentTimeMillis()
                val registro = Registro(
                    id = registroId,
                    bitacoraId = bitacora.id,
                    estudianteId = alumno.id,
                    fechaClase = estado.fechaClase.trim(),
                    contenido = estado.contenido.trim(),
                    actividad = estado.actividad.trim(),
                    aprendizaje = estado.aprendizaje.trim(),
                    dudas = estado.dudas.trim(),
                    fotoUri = estado.fotoUri,
                    enlace = estado.enlace.trim(),
                    estadoEnvio = if (enviar) EstadoEnvio.ENVIADO else EstadoEnvio.BORRADOR,
                    fechaCreacion = fechaCreacionOriginal,
                    fechaEnvio = if (enviar) ahora else 0L
                )

                if (registroId == 0) {
                    registroRepository.guardarNuevo(registro)
                } else {
                    registroRepository.actualizar(registro)
                }

                if (enviar) {
                    Sesion.exito("Registro enviado a tu docente.")
                } else {
                    // Borrador = aviso (ámbar): todavía falta enviarlo
                    Sesion.advertencia("Borrador guardado. Recuerda enviarlo a tu docente.")
                }
                _uiState.update { it.copy(terminado = true) }
            } catch (e: Exception) {
                Sesion.error("No se pudo guardar el registro. Intenta nuevamente.")
            }
        }
    }
}
