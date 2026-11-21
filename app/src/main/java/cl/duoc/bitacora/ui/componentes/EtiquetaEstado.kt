package cl.duoc.bitacora.ui.componentes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.model.EstadoEnvio
import cl.duoc.bitacora.model.EstadoRevision
import cl.duoc.bitacora.data.RegistroDetalle
import cl.duoc.bitacora.ui.theme.FondoActiva
import cl.duoc.bitacora.ui.theme.FondoBorrador
import cl.duoc.bitacora.ui.theme.FondoCerrada
import cl.duoc.bitacora.ui.theme.FondoEnviado
import cl.duoc.bitacora.ui.theme.FondoPendiente
import cl.duoc.bitacora.ui.theme.TextoActiva
import cl.duoc.bitacora.ui.theme.TextoBorrador
import cl.duoc.bitacora.ui.theme.TextoCerrada
import cl.duoc.bitacora.ui.theme.TextoEnviado
import cl.duoc.bitacora.ui.theme.TextoPendiente

// Etiqueta de color con fondo pastel y texto del mismo tono.
// Si el estado cambia, los colores cambian con una animación (animateColorAsState).
@Composable
fun EtiquetaEstado(texto: String, fondoObjetivo: Color, textoObjetivo: Color) {
    val fondo by animateColorAsState(
        targetValue = fondoObjetivo,
        animationSpec = tween(durationMillis = 600),
        label = "Fondo de la etiqueta"
    )
    val colorTexto by animateColorAsState(
        targetValue = textoObjetivo,
        animationSpec = tween(durationMillis = 600),
        label = "Texto de la etiqueta"
    )
    Surface(
        color = fondo,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = texto,
            color = colorTexto,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

// Etiqueta según el estado de una bitácora
@Composable
fun EtiquetaBitacora(estado: String) {
    when (estado) {
        EstadoBitacora.PUBLICADA -> EtiquetaEstado("Activa", FondoActiva, TextoActiva)
        EstadoBitacora.CERRADA -> EtiquetaEstado("Cerrada", FondoCerrada, TextoCerrada)
        else -> EtiquetaEstado("Borrador", FondoBorrador, TextoBorrador)
    }
}

// Etiquetas de un registro: borrador / enviado + pendiente / revisado
@Composable
fun EtiquetasRegistro(registro: RegistroDetalle) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (registro.estadoEnvio == EstadoEnvio.BORRADOR) {
            EtiquetaEstado("Borrador", FondoBorrador, TextoBorrador)
        } else {
            EtiquetaEstado("Enviado", FondoEnviado, TextoEnviado)
            if (registro.estadoRevision == EstadoRevision.REVISADO) {
                EtiquetaEstado("Revisado", FondoActiva, TextoActiva)
            } else {
                EtiquetaEstado("Pendiente de revisión", FondoPendiente, TextoPendiente)
            }
        }
    }
}
