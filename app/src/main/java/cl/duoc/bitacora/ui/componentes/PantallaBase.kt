package cl.duoc.bitacora.ui.componentes

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = botonFlotante,
        content = contenido
    )
}

// Muestra en un Snackbar el mensaje de confirmación guardado en Sesion.mensaje
// (por ejemplo "Registro enviado") y luego lo limpia.
@Composable
fun MensajeSesion(snackbarHostState: SnackbarHostState) {
    val mensaje = cl.duoc.bitacora.util.Sesion.mensaje
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            snackbarHostState.showSnackbar(mensaje)
            cl.duoc.bitacora.util.Sesion.mensaje = null
        }
    }
}
