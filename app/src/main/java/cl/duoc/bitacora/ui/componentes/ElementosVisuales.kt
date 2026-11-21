package cl.duoc.bitacora.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

// Tarjeta de saludo al inicio de cada perfil: ícono en un círculo + nombre + detalle
@Composable
fun TarjetaBienvenida(
    nombre: String,
    detalle: String,
    icono: ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            // Rosa pastel de la paleta (primaryContainer en ui/theme)
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(32.dp)
                )
            }
            Column {
                Text(
                    text = "Hola, $nombre",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = detalle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

// Título de sección con ícono, en el color principal de la app
@Composable
fun TituloSeccion(
    texto: String,
    icono: ImageVector
) {
    Row(
        modifier = Modifier.padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// Mensaje cuando una lista está vacía: ícono grande + texto centrado
@Composable
fun EstadoVacio(
    texto: String,
    icono: ImageVector
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(56.dp)
        )
        Text(
            text = texto,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Un número destacado con su etiqueta debajo (para tarjetas de resumen)
@Composable
fun IndicadorResumen(
    valor: Int,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = valor.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center
        )
    }
}

// Botón que muestra u oculta contenido con una animación (AnimatedVisibility).
// Sirve para no llenar la tarjeta de texto: el detalle aparece solo cuando el usuario lo pide.
@Composable
fun DetalleDesplegable(
    textoVer: String = "Ver detalles",
    textoOcultar: String = "Ocultar detalles",
    contenido: @Composable () -> Unit
) {
    var abierto by rememberSaveable { mutableStateOf(false) }
    Column {
        TextButton(
            onClick = { abierto = !abierto },
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            Text(if (abierto) textoOcultar else textoVer)
            Icon(
                imageVector = if (abierto) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null
            )
        }
        AnimatedVisibility(visible = abierto) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                contenido()
            }
        }
    }
}

// Bloque con título y texto. Si es "destacado" se pinta con el rosa suave de la paleta.
@Composable
fun BloqueTexto(
    titulo: String,
    texto: String,
    destacado: Boolean = false
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        // Sin destacar, el fondo es transparente para dejar ver el color de la tarjeta
        color = if (destacado) MaterialTheme.colorScheme.primaryContainer
        else Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(if (destacado) 12.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = texto,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
