package cl.duoc.bitacora.ui.estudiante.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora.ui.componentes.CampoFecha
import cl.duoc.bitacora.ui.componentes.CampoFormulario
import cl.duoc.bitacora.ui.componentes.MensajeSesion
import cl.duoc.bitacora.ui.componentes.PantallaBase
import cl.duoc.bitacora.ui.componentes.SelectorEvidencia
import cl.duoc.bitacora.util.anioActual
import cl.duoc.bitacora.util.parsearFechaCalendario

@Composable
fun NuevoRegistroScreen(
    onVolver: () -> Unit,
    viewModel: NuevoRegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    MensajeSesion(snackbarHostState)

    // Al guardar o enviar volvemos a la pantalla anterior
    LaunchedEffect(uiState.terminado) {
        if (uiState.terminado) onVolver()
    }

    PantallaBase(
        titulo = "Registro de clase",
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
            if (!uiState.cargado) {
                Text("Cargando...")
            } else if (uiState.sinAcceso) {
                Text("Esta bitácora no está disponible para ti o ya fue cerrada.")
            } else {
                Text(
                    text = uiState.bitacoraTitulo,
                    style = MaterialTheme.typography.titleLarge
                )

                // Instrucciones y preguntas orientadoras del docente
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Instrucciones: ${uiState.instrucciones}")
                        Text(
                            text = "Preguntas orientadoras:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(uiState.preguntas)
                    }
                }

                // La fecha de la clase se elige en el calendario (no se escribe).
                // Los días futuros están bloqueados.
                CampoFecha(
                    textoMostrado = uiState.fechaClase,
                    etiqueta = "Fecha de la clase",
                    hayError = uiState.errorFecha,
                    mensajeError = "Elija un día de ${uiState.mesBitacoraTexto}, hasta la fecha de hoy",
                    onFechaElegida = { viewModel.elegirFecha(it) },
                    fechaInicial = parsearFechaCalendario(uiState.fechaClase),
                    bloquearFuturas = true,
                    anios = 2025..maxOf(2025, anioActual())
                )

                CampoFormulario(
                    valor = uiState.contenido,
                    onValorChange = { viewModel.cambiarContenido(it) },
                    etiqueta = "Contenido trabajado",
                    icono = Icons.Filled.Create,
                    hayError = uiState.errorContenido,
                    mensajeError = "El contenido trabajado es obligatorio",
                    lineas = 2
                )

                CampoFormulario(
                    valor = uiState.actividad,
                    onValorChange = { viewModel.cambiarActividad(it) },
                    etiqueta = "Actividad realizada",
                    icono = Icons.Filled.Star,
                    hayError = uiState.errorActividad,
                    mensajeError = "Describe la actividad que realizaste",
                    lineas = 2
                )

                CampoFormulario(
                    valor = uiState.aprendizaje,
                    onValorChange = { viewModel.cambiarAprendizaje(it) },
                    etiqueta = "¿Qué aprendí?",
                    icono = Icons.Filled.Lightbulb,
                    hayError = uiState.errorAprendizaje,
                    mensajeError = "Cuéntanos qué aprendiste",
                    lineas = 3
                )

                CampoFormulario(
                    valor = uiState.dudas,
                    onValorChange = { viewModel.cambiarDudas(it) },
                    etiqueta = "Dudas o dificultades (opcional)",
                    icono = Icons.Filled.Warning,
                    hayError = false,
                    mensajeError = "",
                    lineas = 2
                )

                CampoFormulario(
                    valor = uiState.enlace,
                    onValorChange = { viewModel.cambiarEnlace(it) },
                    etiqueta = "Enlace relacionado (opcional)",
                    icono = Icons.Filled.Link,
                    hayError = uiState.errorEnlace,
                    mensajeError = "El enlace debe comenzar con http:// o https://",
                    teclado = KeyboardType.Uri
                )

                SelectorEvidencia(
                    fotoUri = uiState.fotoUri,
                    mensajeError = uiState.errorFoto,
                    onGaleria = { viewModel.fotoDeGaleria(it) },
                    onFotoTomada = { viewModel.fotoTomada(it) },
                    onQuitar = { viewModel.quitarFoto() }
                )

                OutlinedButton(
                    onClick = { viewModel.guardarBorrador() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar borrador")
                }

                Button(
                    onClick = { viewModel.enviar() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Text("  Enviar al docente")
                }
            }
        }
    }
}
