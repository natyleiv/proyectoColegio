package cl.duoc.bitacora.ui.docente.revision

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora.ui.componentes.CampoFormulario
import cl.duoc.bitacora.ui.componentes.MensajeSesion
import cl.duoc.bitacora.ui.componentes.PantallaBase
import cl.duoc.bitacora.ui.componentes.RegistroContenido

@Composable
fun RevisarRegistroScreen(
    onVolver: () -> Unit,
    viewModel: RevisarRegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MensajeSesion(snackbarHostState)

    LaunchedEffect(uiState.guardado) {
        if (uiState.guardado) onVolver()
    }

    PantallaBase(
        titulo = "Revisar aporte",
        snackbarHostState = snackbarHostState,
        onVolver = onVolver
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val registro = uiState.registro
            if (!uiState.cargado) {
                Text("Cargando...")
            } else if (registro == null) {
                Text("No tienes acceso a este aporte.")
            } else {
                RegistroContenido(registro)

                CampoFormulario(
                    valor = uiState.comentario,
                    onValorChange = { viewModel.cambiarComentario(it) },
                    etiqueta = "Retroalimentación para el estudiante",
                    icono = Icons.Filled.Edit,
                    hayError = uiState.errorComentario,
                    mensajeError = "Escriba un comentario de al menos 5 caracteres",
                    lineas = 3
                )

                Button(
                    onClick = { viewModel.guardarRevision() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar comentario y marcar como revisado")
                }
            }
        }
    }
}
