package cl.duoc.bitacora.ui.docente.revision

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.RegistroRepository
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.validarLargoMinimo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RevisarRegistroViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = RegistroRepository(database.registroDao())

    private val docenteId = Sesion.usuario?.id ?: 0

    private val _uiState = MutableStateFlow(RevisarRegistroUiState())
    val uiState: StateFlow<RevisarRegistroUiState> = _uiState.asStateFlow()

    init {
        // Si el registro no es de una bitácora de este docente, vuelve null (sin acceso)
        viewModelScope.launch {
            try {
                val registro = repository.obtenerParaDocente(Sesion.registroId, docenteId)
                _uiState.update {
                    it.copy(
                        registro = registro,
                        cargado = true,
                        comentario = registro?.comentarioDocente ?: ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargado = true) }
                Sesion.error("No se pudo cargar el aporte.")
            }
        }
    }

    fun cambiarComentario(valor: String) {
        _uiState.update {
            it.copy(comentario = valor, errorComentario = it.errorComentario && !validarLargoMinimo(valor, 5))
        }
    }

    // Guarda la retroalimentación y marca el aporte como revisado
    fun guardarRevision() {
        val estado = _uiState.value
        val registro = estado.registro ?: return
        val errorComentario = !validarLargoMinimo(estado.comentario, 5)

        _uiState.update { it.copy(errorComentario = errorComentario) }

        if (!errorComentario) {
            viewModelScope.launch {
                // Si algo falla (sin acceso, error de base de datos) se muestra un mensaje de ERROR
                try {
                    repository.revisar(registro.id, estado.comentario.trim(), docenteId)
                    Sesion.exito("Aporte marcado como revisado.")
                    _uiState.update { it.copy(guardado = true) }
                } catch (e: Exception) {
                    Sesion.error("No se pudo guardar la revisión. Intenta nuevamente.")
                }
            }
        }
    }
}
