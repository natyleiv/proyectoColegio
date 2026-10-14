package cl.duoc.bitacora.ui.estudiante.historial

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora.model.EstadoEnvio
import cl.duoc.bitacora.ui.componentes.EtiquetasRegistro
import cl.duoc.bitacora.ui.componentes.MensajeSesion
import cl.duoc.bitacora.ui.componentes.PantallaBase
import cl.duoc.bitacora.util.Sesion

@Composable
fun HistorialScreen(
    onVolver: () -> Unit,
    onEditarBorrador: () -> Unit,
    onVerRegistro: () -> Unit,
    viewModel: HistorialViewModel = viewModel()
) {
    val registros by viewModel.registros.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MensajeSesion(snackbarHostState)

    PantallaBase(
        titulo = "Mi historial",
        snackbarHostState = snackbarHostState,
        onVolver = onVolver
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (registros.isEmpty()) {
                item { Text("Aún no tienes registros. Agrega uno desde una bitácora activa.") }
            }
            items(registros) { registro ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Sesion.registroId = registro.id
                            if (registro.estadoEnvio == EstadoEnvio.BORRADOR) {
                                // Un borrador se puede seguir editando
                                Sesion.bitacoraId = registro.bitacoraId
                                onEditarBorrador()
                            } else {
                                onVerRegistro()
                            }
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = registro.bitacoraTitulo,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Clase del ${registro.fechaClase}: ${registro.contenido}")
                        EtiquetasRegistro(registro)
                    }
                }
            }
        }
    }
}
