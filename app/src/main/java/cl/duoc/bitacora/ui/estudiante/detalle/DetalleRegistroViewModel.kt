package cl.duoc.bitacora.ui.estudiante.detalle

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.RegistroRepository
import cl.duoc.bitacora.data.RegistroDetalle
import cl.duoc.bitacora.util.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleRegistroUiState(
    val registro: RegistroDetalle? = null,
    val cargado: Boolean = false
)

class DetalleRegistroViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = RegistroRepository(database.registroDao())

    private val _uiState = MutableStateFlow(DetalleRegistroUiState())
    val uiState: StateFlow<DetalleRegistroUiState> = _uiState.asStateFlow()

    init {
        // Si el registro no es de este estudiante, vuelve null (sin acceso)
        viewModelScope.launch {
            try {
                val registro = repository.obtenerDeEstudiante(
                    Sesion.registroId,
                    Sesion.usuario?.id ?: 0
                )
                _uiState.update { it.copy(registro = registro, cargado = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(cargado = true) }
                Sesion.error("No se pudo cargar el registro.")
            }
        }
    }
}
