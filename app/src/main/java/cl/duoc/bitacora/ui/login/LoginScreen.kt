package cl.duoc.bitacora.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora.model.Perfil
import cl.duoc.bitacora.ui.componentes.CampoFormulario
import cl.duoc.bitacora.ui.componentes.MensajeSesion
import cl.duoc.bitacora.ui.componentes.PantallaBase

@Composable
fun LoginScreen(
    onLoginDocente: () -> Unit,
    onLoginEstudiante: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    // Observa el estado del ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MensajeSesion(snackbarHostState)

    // Cuando el login es correcto, navegamos según el perfil
    LaunchedEffect(uiState.perfilIngresado) {
        when (uiState.perfilIngresado) {
            Perfil.DOCENTE -> onLoginDocente()
            Perfil.ESTUDIANTE -> onLoginEstudiante()
        }
        if (uiState.perfilIngresado != null) viewModel.navegacionRealizada()
    }

    // El botón cambia de color con animación cuando ya hay datos escritos
    val hayDatos = uiState.correo.isNotBlank() && uiState.password.isNotBlank()
    val colorBoton by animateColorAsState(
        targetValue = if (hayDatos) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        animationSpec = tween(durationMillis = 500),
        label = "Color del botón ingresar"
    )

    PantallaBase(
        titulo = "Bitácora Escolar",
        snackbarHostState = snackbarHostState
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Iniciar sesión",
                style = MaterialTheme.typography.headlineMedium
            )

            CampoFormulario(
                valor = uiState.correo,
                onValorChange = { viewModel.cambiarCorreo(it) },
                etiqueta = "Correo",
                icono = Icons.Filled.Email,
                hayError = uiState.errorCorreo,
                mensajeError = "Ingrese un correo válido terminado en .cl",
                teclado = KeyboardType.Email
            )

            CampoFormulario(
                valor = uiState.password,
                onValorChange = { viewModel.cambiarPassword(it) },
                etiqueta = "Contraseña",
                icono = Icons.Filled.Lock,
                hayError = uiState.errorPassword,
                mensajeError = "Debe contener al menos 8 caracteres",
                esPassword = true
            )

            // Error de credenciales con animación
            AnimatedVisibility(visible = uiState.errorCredenciales) {
                Text(
                    text = "Correo o contraseña incorrectos",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = { viewModel.ingresar() },
                colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            // Ayuda para probar la app: cuentas ficticias
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Cuentas de prueba (datos ficticios)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Docente: docente1@escuela.cl / docente123")
                    Text("Estudiante: alumno1@escuela.cl / alumno123")
                }
            }
        }
    }
}
