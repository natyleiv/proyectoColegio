package cl.duoc.bitacora.ui.estudiante.historial

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.RegistroRepository
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.util.Sesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HistorialViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = RegistroRepository(database.registroDao())

    // Solo los registros del estudiante que inició sesión (nunca los de sus compañeros)
    val registros: StateFlow<List<Registro>> =
        repository
            .obtenerPorEstudiante(Sesion.usuario?.id ?: 0)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
