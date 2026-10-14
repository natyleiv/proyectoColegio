package cl.duoc.bitacora.ui.docente.crear

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import cl.duoc.bitacora.ui.componentes.CampoFormulario
import cl.duoc.bitacora.ui.componentes.PantallaBase

@Composable
fun CrearBitacoraScreen(
    onVolver: () -> Unit,
    viewModel: CrearBitacoraViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val asignaciones by viewModel.asignaciones.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Al guardar correctamente volvemos a la pantalla anterior
    LaunchedEffect(uiState.guardada) {
        if (uiState.guardada) onVolver()
    }

    PantallaBase(
        titulo = "Nueva bitácora",
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
            Text(
                text = "Curso y asignatura",
                style = MaterialTheme.typography.titleMedium
            )
            asignaciones.forEach { asignacion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.cambiarAsignacion(asignacion.id) }
                ) {
                    RadioButton(
                        selected = uiState.asignacionId == asignacion.id,
                        onClick = { viewModel.cambiarAsignacion(asignacion.id) }
                    )
                    Text("${asignacion.curso} - ${asignacion.asignatura}")
                }
            }
            AnimatedVisibility(visible = uiState.errorAsignacion) {
                Text(
                    text = "Seleccione un curso y asignatura",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoFormulario(
                    valor = uiState.mes,
                    onValorChange = { viewModel.cambiarMes(it) },
                    etiqueta = "Mes (1-12)",
                    icono = Icons.Filled.DateRange,
                    hayError = uiState.errorMes,
                    mensajeError = "Mes entre 1 y 12",
                    teclado = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
                CampoFormulario(
                    valor = uiState.anio,
                    onValorChange = { viewModel.cambiarAnio(it) },
                    etiqueta = "Año",
                    icono = Icons.Filled.DateRange,
                    hayError = uiState.errorAnio,
                    mensajeError = "Año válido (ej: 2026)",
                    teclado = KeyboardType.Number,
                    modifier = Modifier.weight(1f)
                )
            }

            CampoFormulario(
                valor = uiState.titulo,
                onValorChange = { viewModel.cambiarTitulo(it) },
                etiqueta = "Título",
                icono = Icons.Filled.Edit,
                hayError = uiState.errorTitulo,
                mensajeError = "El título debe tener al menos 5 caracteres"
            )

            CampoFormulario(
                valor = uiState.proposito,
                onValorChange = { viewModel.cambiarProposito(it) },
                etiqueta = "Propósito general",
                icono = Icons.Filled.Star,
                hayError = uiState.errorProposito,
                mensajeError = "El propósito es obligatorio",
                lineas = 2
            )

            CampoFormulario(
                valor = uiState.instrucciones,
                onValorChange = { viewModel.cambiarInstrucciones(it) },
                etiqueta = "Instrucciones",
                icono = Icons.Filled.Info,
                hayError = uiState.errorInstrucciones,
                mensajeError = "Las instrucciones son obligatorias",
                lineas = 3
            )

            CampoFormulario(
                valor = uiState.preguntas,
                onValorChange = { viewModel.cambiarPreguntas(it) },
                etiqueta = "Preguntas orientadoras",
                icono = Icons.Filled.Check,
                hayError = uiState.errorPreguntas,
                mensajeError = "Escriba al menos una pregunta (una por línea)",
                lineas = 4,
                placeholder = "Una pregunta por línea"
            )

            Button(
                onClick = { viewModel.guardar() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar bitácora")
            }
        }
    }
}
