package cl.duoc.bitacora.ui.estudiante.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora.ui.componentes.EtiquetaBitacora
import cl.duoc.bitacora.ui.componentes.MensajeSesion
import cl.duoc.bitacora.ui.componentes.PantallaBase
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.nombreMes
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Inbox
import cl.duoc.bitacora.ui.componentes.EstadoVacio
import cl.duoc.bitacora.ui.componentes.TarjetaBienvenida
import cl.duoc.bitacora.ui.componentes.TituloSeccion

@Composable
fun EstudianteInicioScreen(
    onNuevoRegistro: (Int) -> Unit,
    onVerHistorial: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: EstudianteInicioViewModel = viewModel()
) {
    val bitacoras by viewModel.bitacoras.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MensajeSesion(snackbarHostState)

    PantallaBase(
        titulo = "Mis bitácoras",
        snackbarHostState = snackbarHostState,
        acciones = {
            IconButton(onClick = { onCerrarSesion() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Cerrar sesión"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TarjetaBienvenida(
                    nombre = Sesion.usuario?.nombre ?: "",
                    detalle = "Curso: ${Sesion.usuario?.curso ?: ""}",
                    icono = Icons.Filled.Face
                )
            }
            item {
                OutlinedButton(
                    onClick = { onVerHistorial() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("  Mi historial de aportes")
                }
            }
            item {
                TituloSeccion(
                    texto = "Bitácoras activas",
                    icono = Icons.Filled.Book
                )
            }
            if (bitacoras.isEmpty()) {
                item {
                    EstadoVacio(
                        texto = "Tu docente aún no ha publicado\nbitácoras para tu curso.",
                        icono = Icons.Filled.Inbox
                    )
                }
            }
            items(bitacoras) { bitacora ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = bitacora.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("${bitacora.asignatura} · ${nombreMes(bitacora.mes)} ${bitacora.anio}")
                        EtiquetaBitacora(bitacora.estado)
                        Text("Propósito: ${bitacora.proposito}")
                        Button(
                            onClick = { onNuevoRegistro(bitacora.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("  Agregar registro de clase")
                        }
                    }
                }
            }
        }
    }
}
