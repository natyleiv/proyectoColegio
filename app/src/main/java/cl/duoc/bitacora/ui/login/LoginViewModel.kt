package cl.duoc.bitacora.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.UsuarioRepository
import cl.duoc.bitacora.data.cargarDatosDemo
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.validarCorreo
import cl.duoc.bitacora.util.validarPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = UsuarioRepository(database.usuarioDao(), database.asignacionDao())

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Primera ejecución: carga usuarios y bitácoras ficticias de prueba
        viewModelScope.launch {
            cargarDatosDemo(database)
        }
    }

    fun cambiarCorreo(nuevoCorreo: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(correo = nuevoCorreo, errorCredenciales = false)
        }
    }

    fun cambiarPassword(nuevoPassword: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(password = nuevoPassword, errorCredenciales = false)
        }
    }

    fun ingresar() {
        val estado = _uiState.value
        val errorCorreo = !validarCorreo(estado.correo.trim())
        val errorPassword = !validarPassword(estado.password)

        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorCorreo = errorCorreo,
                errorPassword = errorPassword,
                errorCredenciales = false
            )
        }

        if (!errorCorreo && !errorPassword) {
            viewModelScope.launch {
                val usuario = repository.iniciarSesion(estado.correo, estado.password)
                if (usuario == null) {
                    _uiState.update { estadoActual ->
                        estadoActual.copy(errorCredenciales = true)
                    }
                } else {
                    Sesion.usuario = usuario
                    _uiState.update { estadoActual ->
                        estadoActual.copy(perfilIngresado = usuario.perfil)
                    }
                }
            }
        }
    }

    fun navegacionRealizada() {
        _uiState.update { estadoActual ->
            estadoActual.copy(perfilIngresado = null)
        }
    }
}
