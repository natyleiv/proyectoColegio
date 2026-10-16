package cl.duoc.bitacora.ui.estudiante.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora.model.EstadoRevision
import cl.duoc.bitacora.ui.componentes.MensajeSesion
import cl.duoc.bitacora.ui.componentes.PantallaBase
import cl.duoc.bitacora.ui.componentes.RegistroContenido

@Composable
fun DetalleRegistroScreen(
    onVolver: () -> Unit,
    viewModel: DetalleRegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MensajeSesion(snackbarHostState)

    PantallaBase(
        titulo = "Mi registro",
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
                Text("No tienes acceso a este registro.")
            } else {
                RegistroContenido(registro)
                if (registro.estadoRevision == EstadoRevision.PENDIENTE) {
                    Text("Tu docente aún no revisa este aporte.")
                }
            }
        }
    }
}
