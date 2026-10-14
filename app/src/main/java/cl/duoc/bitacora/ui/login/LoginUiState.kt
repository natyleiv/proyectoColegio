package cl.duoc.bitacora.ui.login

// "Fotografía" del estado de la pantalla de login (clase 05: UiState)
data class LoginUiState(
    val correo: String = "",
    val password: String = "",
    val errorCorreo: Boolean = false,
    val errorPassword: Boolean = false,
    val errorCredenciales: Boolean = false,
    // Perfil con el que se ingresó; la pantalla lo usa para navegar al inicio correcto
    val perfilIngresado: String? = null
)
