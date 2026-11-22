package cl.duoc.bitacora.ui.estudiante.historial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.RegistroRepository
import cl.duoc.bitacora.data.RegistroDetalle
import cl.duoc.bitacora.util.Sesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import cl.duoc.bitacora.data.remote.mensajeDeError
import kotlinx.coroutines.launch

class HistorialViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = RegistroRepository(database.registroDao())

    // Al abrir el historial se descargan los registros del estudiante desde ms-bitacoras
    init {
        viewModelScope.launch {
            try {
                repository.sincronizarDeEstudiante(Sesion.usuario?.id ?: 0)
            } catch (e: Exception) {
                Sesion.error(mensajeDeError(e, "No se pudo actualizar tu historial."))
            }
        }
    }

    // Solo los registros del estudiante que inició sesión (nunca los de sus compañeros)
    val registros: StateFlow<List<RegistroDetalle>> =
        repository
            .obtenerPorEstudiante(Sesion.usuario?.id ?: 0)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
