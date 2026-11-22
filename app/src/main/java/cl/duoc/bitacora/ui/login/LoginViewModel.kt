package cl.duoc.bitacora.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora.data.AppDatabase
import cl.duoc.bitacora.data.UsuarioRepository
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.validarCorreo
import cl.duoc.bitacora.util.validarPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = UsuarioRepository(database.usuarioDao(), database.asignacionDao(), database.catalogoDao())

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()



    fun cambiarCorreo(nuevoCorreo: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(
                correo = nuevoCorreo,
                // Si el campo estaba con error, se quita apenas el correo sea válido
                errorCorreo = estadoActual.errorCorreo && !validarCorreo(nuevoCorreo.trim()),
                errorCredenciales = false
            )
        }
    }

    fun cambiarPassword(nuevoPassword: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(
                password = nuevoPassword,
                errorPassword = estadoActual.errorPassword && !validarPassword(nuevoPassword),
                errorCredenciales = false
            )
        }
    }

    fun ingresar() {
        val estado = _uiState.value
        if (estado.cargando) return   // evita enviar dos veces si se toca el botón repetido

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
            _uiState.update { it.copy(cargando = true) }
            viewModelScope.launch {
                try {
                    val usuario = repository.iniciarSesion(estado.correo, estado.password)
                    if (usuario == null) {
                        _uiState.update { estadoActual ->
                            estadoActual.copy(errorCredenciales = true, cargando = false)
                        }
                    } else {
                        Sesion.usuario = usuario
                        _uiState.update { estadoActual ->
                            estadoActual.copy(perfilIngresado = usuario.perfil, cargando = false)
                        }
                    }
                } catch (e: IOException) {
                    // El servidor no respondió (microservicio apagado o sin red)
                    _uiState.update { it.copy(cargando = false) }
                    Sesion.error("No se pudo conectar con el servidor. Revisa que los microservicios estén encendidos.")
                } catch (e: Exception) {
                    _uiState.update { it.copy(cargando = false) }
                    Sesion.error("Error al iniciar sesión. Intenta nuevamente.")
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
