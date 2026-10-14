package cl.duoc.bitacora.ui.docente.inicio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.BitacoraRepository
import cl.duoc.bitacora.data.UsuarioRepository
import cl.duoc.bitacora.model.Asignacion
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.util.Sesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DocenteInicioViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val bitacoraRepository = BitacoraRepository(database.bitacoraDao())
    private val usuarioRepository = UsuarioRepository(database.usuarioDao(), database.asignacionDao())

    // Solo se consulta lo que pertenece al docente que inició sesión
    private val docenteId = Sesion.usuario?.id ?: 0

    val bitacoras: StateFlow<List<Bitacora>> =
        bitacoraRepository
            .obtenerPorDocente(docenteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val asignaciones: StateFlow<List<Asignacion>> =
        usuarioRepository
            .obtenerAsignaciones(docenteId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
}
