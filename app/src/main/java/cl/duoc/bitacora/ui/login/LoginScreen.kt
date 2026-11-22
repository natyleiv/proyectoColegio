package cl.duoc.bitacora.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora.model.Perfil
import cl.duoc.bitacora.ui.componentes.CampoFormulario
import cl.duoc.bitacora.ui.componentes.MensajeSesion
import cl.duoc.bitacora.ui.componentes.PantallaBase
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
        animationSpec = tween(durationMillis = 500),
        label = "Color del botón ingresar"
    )

    PantallaBase(
        titulo = "Mi Bitácora",
        snackbarHostState = snackbarHostState
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado: ícono dentro de un círculo de color y mensaje de bienvenida
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(44.dp)
                )
            }
            Text(
                text = "¡Hola! Bienvenido/a",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Tu espacio para aprender y registrar tus avances.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CampoFormulario(
                valor = uiState.correo,
                onValorChange = { viewModel.cambiarCorreo(it) },
                etiqueta = "Correo electrónico",
                icono = Icons.Filled.Email,
                hayError = uiState.errorCorreo,
                mensajeError = "Ingrese un correo válido terminado en .cl",
                teclado = KeyboardType.Email,
                placeholder = "Ingresa tu correo",
                etiquetaArriba = true
            )

            CampoFormulario(
                valor = uiState.password,
                onValorChange = { viewModel.cambiarPassword(it) },
                etiqueta = "Contraseña",
                icono = Icons.Filled.Lock,
                hayError = uiState.errorPassword,
                mensajeError = "Debe contener al menos 8 caracteres",
                esPassword = true,
                placeholder = "Ingresa tu contraseña",
                etiquetaArriba = true
            )

            // Error de credenciales con animación
            AnimatedVisibility(visible = uiState.errorCredenciales) {
                Text(
                    text = "Correo o contraseña incorrectos",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Mientras se espera la respuesta de ms-usuarios el botón se desactiva
            // y cambia su texto (estado de carga visible)
            Button(
                onClick = { viewModel.ingresar() },
                enabled = !uiState.cargando,
                colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (uiState.cargando) "Ingresando…" else "Ingresar",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Ayuda para probar la app: dos accesos rápidos con cuentas ficticias.
            // Cada botón completa el correo y la contraseña de la cuenta de prueba e ingresa.
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "¿Necesitas probar la aplicación?",
                        style = MaterialTheme.typography.titleSmall
                    )
                    AccesoPrueba(
                        texto = "Ingresar como docente",
                        icono = Icons.Filled.School,
                        habilitado = !uiState.cargando,
                        onClick = {
                            viewModel.cambiarCorreo("docente1@escuela.cl")
                            viewModel.cambiarPassword("docente123")
                            viewModel.ingresar()
                        }
                    )
                    AccesoPrueba(
                        texto = "Ingresar como estudiante",
                        Icons.AutoMirrored.Filled.MenuBook,
                        habilitado = !uiState.cargando,
                        onClick = {
                            viewModel.cambiarCorreo("alumno1@escuela.cl")
                            viewModel.cambiarPassword("alumno123")
                            viewModel.ingresar()
                        }
                    )
                }
            }
        }
    }
}

// Botón de acceso rápido con cuenta de prueba: ícono, texto y flecha
@Composable
private fun AccesoPrueba(
    texto: String,
    icono: ImageVector,
    habilitado: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = habilitado,
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(imageVector = icono, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = texto, modifier = Modifier.weight(1f))
        Icons.AutoMirrored.Filled.KeyboardArrowRight
    }
}