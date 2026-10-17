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
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.School
import androidx.compose.ui.Alignment
import cl.duoc.bitacora.ui.componentes.EstadoVacio
import cl.duoc.bitacora.ui.componentes.TarjetaBienvenida
import cl.duoc.bitacora.ui.componentes.TituloSeccion
import androidx.compose.material.icons.filled.BarChart
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.ui.componentes.IndicadorResumen

@Composable
fun DocenteInicioScreen(
    onCrearBitacora: () -> Unit,
    onAbrirBitacora: (Int) -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: DocenteInicioViewModel = viewModel()
) {
    val bitacoras by viewModel.bitacoras.collectAsStateWithLifecycle()
    val asignaciones by viewModel.asignaciones.collectAsStateWithLifecycle()
    val pendientes by viewModel.pendientes.collectAsStateWithLifecycle()
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TarjetaBienvenida(
                    nombre = Sesion.usuario?.nombre ?: "",
                    detalle = "Crea y revisa las bitácoras de tus cursos",
                    icono = Icons.Filled.School
                )
            }


            // Resumen (dashboard): estado general de las bitácoras del docente
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TituloSeccion(
                            texto = "Resumen",
                            icono = Icons.Filled.BarChart
                        )
                        // weight(1f): las tres columnas ocupan el mismo ancho (PPT 02)
                        Row(modifier = Modifier.fillMaxWidth()) {
                            IndicadorResumen(
                                valor = bitacoras.count { it.estado == EstadoBitacora.PUBLICADA },
                                etiqueta = "Activas",
                                modifier = Modifier.weight(1f)
                            )
                            IndicadorResumen(
                                valor = bitacoras.count { it.estado == EstadoBitacora.BORRADOR },
                                etiqueta = "Borradores",
                                modifier = Modifier.weight(1f)
                            )
                            IndicadorResumen(
                                valor = pendientes,
                                etiqueta = "Aportes por revisar",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            // Cursos y asignaturas asignadas al docente
            item {
                TituloSeccion(
                    texto = "Mis cursos y asignaturas",
                    icono = Icons.Filled.Groups
                )
            }
            items(asignaciones) { asignacion ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Class,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("${asignacion.curso} - ${asignacion.asignatura}")
                }
            }

            item {
                TituloSeccion(
                    texto = "Bitácoras mensuales",
                    icono = Icons.Filled.Book
                )
            }
            if (bitacoras.isEmpty()) {
                item {
                    EstadoVacio(
                        texto = "Aún no has creado bitácoras.\nUsa el botón \"Nueva bitácora\".",
                        icono = Icons.Filled.Book
                    )
                }
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
