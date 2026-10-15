package cl.duoc.bitacora.ui.docente.detalle

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.ui.componentes.EtiquetaBitacora
import cl.duoc.bitacora.ui.componentes.EtiquetasRegistro
import cl.duoc.bitacora.ui.componentes.MensajeSesion
import cl.duoc.bitacora.ui.componentes.PantallaBase
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.nombreMes
import androidx.compose.animation.Crossfade
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Lock
import cl.duoc.bitacora.ui.componentes.EstadoVacio
import cl.duoc.bitacora.ui.componentes.TituloSeccion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleBitacoraScreen(
    onVolver: () -> Unit,
    onAbrirRegistro: () -> Unit,
    viewModel: DetalleBitacoraViewModel = viewModel()
) {
    val bitacora by viewModel.bitacora.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MensajeSesion(snackbarHostState)

    // La barra de participación se llena con una animación
    val fraccionParticipacion = if (uiState.totalEstudiantes == 0) 0f
    else uiState.participaron.toFloat() / uiState.totalEstudiantes.toFloat()
    val progreso by animateFloatAsState(
        targetValue = fraccionParticipacion,
        animationSpec = tween(durationMillis = 800),
        label = "Progreso de participación"
    )

    PantallaBase(
        titulo = "Detalle de bitácora",
        snackbarHostState = snackbarHostState,
        onVolver = onVolver
    ) { innerPadding ->
        val resultado = bitacora

        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (resultado == null) {
                // Room todavía no responde
                item { Text("Cargando...") }
            } else if (resultado.isEmpty()) {
                // No existe o no pertenece a este docente
                item {
                    EstadoVacio(
                        texto = "No tienes acceso a esta bitácora.",
                        icono = Icons.Filled.Lock
                    )
                }
            } else {
                val bitacoraActual = resultado.first()
                // ---- Datos de la bitácora ----
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = bitacoraActual.titulo,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text("${bitacoraActual.curso} · ${bitacoraActual.asignatura}")
                            Text("${nombreMes(bitacoraActual.mes)} ${bitacoraActual.anio}")
                            EtiquetaBitacora(bitacoraActual.estado)
                            Text("Propósito: ${bitacoraActual.proposito}")
                            Text("Instrucciones: ${bitacoraActual.instrucciones}")
                            Text("Preguntas orientadoras:")
                            Text(bitacoraActual.preguntas)
                            if (bitacoraActual.fechaPublicacion.isNotBlank()) {
                                Text("Publicada el ${bitacoraActual.fechaPublicacion}")
                            }
                            if (bitacoraActual.fechaCierre.isNotBlank()) {
                                Text("Cerrada el ${bitacoraActual.fechaCierre}")
                            }
                        }
                    }
                }

                // ---- Publicar / cerrar ----
                // El botón cambia con un desvanecido según el estado de la bitácora (PPT 06: Crossfade)
                item {
                    Crossfade(
                        targetState = bitacoraActual.estado,
                        label = "Acción según estado de la bitácora"
                    ) { estado ->
                        when (estado) {
                            EstadoBitacora.BORRADOR -> Button(
                                onClick = { viewModel.publicar() },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Publicar bitácora") }

                            EstadoBitacora.PUBLICADA -> OutlinedButton(
                                onClick = { viewModel.cerrar() },
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("Cerrar bitácora") }

                            else -> Text("Esta bitácora está cerrada.")
                        }
                    }
                }

                // ---- Indicadores de participación ----
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            TituloSeccion(
                                texto = "Indicadores de participación",
                                icono = Icons.Filled.BarChart
                            )
                            Text("Participaron ${uiState.participaron} de ${uiState.totalEstudiantes} estudiantes")
                            LinearProgressIndicator(
                                progress = { progreso },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text("Aportes enviados: ${uiState.totalAportes}")
                            Text("Revisados: ${uiState.revisados}  ·  Pendientes: ${uiState.pendientes}")
                            if (uiState.sinParticipacion.isNotEmpty()) {
                                Text(
                                    text = "Sin participación:",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                uiState.sinParticipacion.forEach { nombre ->
                                    Text("• $nombre")
                                }
                            }
                        }
                    }
                }

                // ---- Filtros ----
                item {
                    TituloSeccion(
                        texto = "Aportes recibidos",
                        icono = Icons.Filled.Inbox
                    )
                }
                item {
                    OutlinedTextField(
                        value = uiState.busqueda,
                        onValueChange = { viewModel.cambiarBusqueda(it) },
                        label = { Text("Buscar por estudiante o fecha (dd/mm/aaaa)") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = uiState.filtroEstado == FILTRO_TODOS,
                            onClick = { viewModel.cambiarFiltro(FILTRO_TODOS) },
                            label = { Text("Todos") }
                        )
                        FilterChip(
                            selected = uiState.filtroEstado == FILTRO_PENDIENTES,
                            onClick = { viewModel.cambiarFiltro(FILTRO_PENDIENTES) },
                            label = { Text("Pendientes") }
                        )
                        FilterChip(
                            selected = uiState.filtroEstado == FILTRO_REVISADOS,
                            onClick = { viewModel.cambiarFiltro(FILTRO_REVISADOS) },
                            label = { Text("Revisados") }
                        )
                    }
                }

                if (uiState.registros.isEmpty()) {
                    item { Text("No hay aportes que coincidan.") }
                }
                items(uiState.registros) { registro ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Sesion.registroId = registro.id
                                onAbrirRegistro()
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = registro.estudianteNombre,
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
}
