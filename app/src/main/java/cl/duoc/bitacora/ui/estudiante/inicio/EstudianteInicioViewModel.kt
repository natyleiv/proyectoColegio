package cl.duoc.bitacora.ui.estudiante.inicio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.BitacoraRepository
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.util.Sesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class EstudianteInicioViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = BitacoraRepository(database.bitacoraDao())

    private val curso = Sesion.usuario?.curso ?: ""

    // Solo bitácoras PUBLICADAS del curso del estudiante
    val bitacoras: StateFlow<List<Bitacora>> =
        repository
            .obtenerPublicadasPorCurso(curso)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
