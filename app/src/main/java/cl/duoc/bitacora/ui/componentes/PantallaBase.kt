package cl.duoc.bitacora.ui.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora.ui.theme.ColorAdvertencia
import cl.duoc.bitacora.ui.theme.ColorError
import cl.duoc.bitacora.ui.theme.ColorExito
import cl.duoc.bitacora.ui.theme.TextoSobreAdvertencia
import cl.duoc.bitacora.ui.theme.TextoSobreError
import cl.duoc.bitacora.ui.theme.TextoSobreExito
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.TipoMensaje

// Ancho máximo del contenido: en tablets o con el teléfono horizontal el contenido
// queda centrado y legible en vez de estirarse por toda la pantalla (diseño adaptable).
private val AnchoMaximoContenido = 720.dp

// Estructura base de todas las pantallas (clase 03): Scaffold + TopAppBar.
// Si onVolver es null (pantalla de inicio) no se muestra la flecha de retorno.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBase(
    titulo: String,
    snackbarHostState: SnackbarHostState,
    onVolver: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {},
    botonFlotante: @Composable () -> Unit = {},
    contenido: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                title = { Text(titulo) },
                navigationIcon = {
                    if (onVolver != null) {
                        IconButton(onClick = { onVolver() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                },
                actions = acciones
            )
        },
        snackbarHost = {
            // Snackbar con color según el tipo de mensaje (éxito / advertencia / error)
            SnackbarHost(snackbarHostState) { datos ->
                val visuales = datos.visuals
                val tipo = if (visuales is MensajeVisual) visuales.tipo else TipoMensaje.EXITO
                val fondo = when (tipo) {
                    TipoMensaje.ERROR -> ColorError
                    TipoMensaje.ADVERTENCIA -> ColorAdvertencia
                    TipoMensaje.EXITO -> ColorExito
                }
                val texto = when (tipo) {
                    TipoMensaje.ERROR -> TextoSobreError
                    TipoMensaje.ADVERTENCIA -> TextoSobreAdvertencia
                    TipoMensaje.EXITO -> TextoSobreExito
                }
                Snackbar(
                    snackbarData = datos,
                    containerColor = fondo,
                    contentColor = texto,
                    dismissActionContentColor = texto
                )
            }
        },
        floatingActionButton = botonFlotante
    ) { innerPadding ->
        // Caja exterior: ocupa todo y centra. Caja interior: limita el ancho máximo.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .widthIn(max = AnchoMaximoContenido)
                    .fillMaxSize()
            ) {
                contenido(innerPadding)
            }
        }
    }
}

// Datos del Snackbar: el texto y su tipo (para pintarlo del color correcto)
class MensajeVisual(
    override val message: String,
    val tipo: TipoMensaje
) : SnackbarVisuals {
    override val actionLabel: String? = null
    override val withDismissAction: Boolean = true
    override val duration: SnackbarDuration = SnackbarDuration.Long
}

// Muestra en un Snackbar el mensaje guardado en Sesion.mensaje (éxito, advertencia o error)
// y luego lo limpia.
@Composable
fun MensajeSesion(snackbarHostState: SnackbarHostState) {
    val mensaje = Sesion.mensaje
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            val tipo = Sesion.tipoMensaje
            // Se limpia DESPUÉS de mostrarlo (si se limpiara antes, el efecto se cancelaría)
            snackbarHostState.showSnackbar(MensajeVisual(mensaje, tipo))
            Sesion.mensaje = null
            Sesion.tipoMensaje = TipoMensaje.EXITO
        }
    }
}
