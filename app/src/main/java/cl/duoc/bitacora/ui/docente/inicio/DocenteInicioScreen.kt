package cl.duoc.bitacora.ui.docente.inicio

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import cl.duoc.bitacora.ui.componentes.EtiquetaBitacora
import cl.duoc.bitacora.ui.componentes.MensajeSesion
import cl.duoc.bitacora.ui.componentes.PantallaBase
import cl.duoc.bitacora.util.Sesion
import cl.duoc.bitacora.util.nombreMes

@Composable
fun DocenteInicioScreen(
    onCrearBitacora: () -> Unit,
    onAbrirBitacora: (Int) -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: DocenteInicioViewModel = viewModel()
) {
    val bitacoras by viewModel.bitacoras.collectAsStateWithLifecycle()
    val asignaciones by viewModel.asignaciones.collectAsStateWithLifecycle()
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
        },
        botonFlotante = {
            ExtendedFloatingActionButton(
                onClick = { onCrearBitacora() },
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nueva bitácora") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Hola, ${Sesion.usuario?.nombre ?: ""}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            // Cursos y asignaturas asignadas al docente
            item {
                Text(
                    text = "Mis cursos y asignaturas",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            items(asignaciones) { asignacion ->
                Text("• ${asignacion.curso} - ${asignacion.asignatura}")
            }

            item {
                Text(
                    text = "Bitácoras mensuales",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (bitacoras.isEmpty()) {
                item { Text("Aún no has creado bitácoras. Usa el botón \"Nueva bitácora\".") }
            }
            items(bitacoras) { bitacora ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAbrirBitacora(bitacora.id) }
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = bitacora.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("${bitacora.curso} · ${bitacora.asignatura}")
                        Row {
                            Text("${nombreMes(bitacora.mes)} ${bitacora.anio}   ")
                            EtiquetaBitacora(bitacora.estado)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }  // espacio para que el botón flotante no tape el último elemento
        }
    }
}
