package cl.duoc.bitacora.ui.estudiante.inicio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.BitacoraRepository
import cl.duoc.bitacora.data.BitacoraDetalle
import cl.duoc.bitacora.data.UsuarioRepository
import cl.duoc.bitacora.util.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstudianteInicioViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = BitacoraRepository(database.bitacoraDao())

    private val usuarioRepository = UsuarioRepository(database.usuarioDao(), database.asignacionDao(), database.catalogoDao())

    // El estudiante guarda solo el id de su curso (clave foránea); el nombre se busca aparte
    private val cursoId = Sesion.usuario?.cursoId ?: 0

    private val _nombreCurso = MutableStateFlow("")
    val nombreCurso: StateFlow<String> = _nombreCurso.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _nombreCurso.value = usuarioRepository.nombreCurso(cursoId)
            } catch (e: Exception) {
                Sesion.error("No se pudo cargar tu curso.")
            }
        }
    }

    // Solo bitácoras PUBLICADAS del curso del estudiante
    val bitacoras: StateFlow<List<BitacoraDetalle>> =
        repository
            .obtenerPublicadasPorCurso(cursoId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
