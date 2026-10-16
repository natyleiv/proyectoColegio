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
import cl.duoc.bitacora.ui.theme.ColorAdvertenciaSuave
import cl.duoc.bitacora.ui.theme.ColorBorrador
import cl.duoc.bitacora.ui.theme.ColorCerrada
import cl.duoc.bitacora.ui.theme.ColorExitoSuave
import cl.duoc.bitacora.ui.theme.ColorPublicada
import cl.duoc.bitacora.ui.theme.TextoSobreEtiqueta

// Etiqueta de color. Si el estado cambia, el color cambia con una animación (animateColorAsState).
@Composable
fun EtiquetaEstado(texto: String, colorObjetivo: Color) {
    val color by animateColorAsState(
        targetValue = colorObjetivo,
        animationSpec = tween(durationMillis = 600),
        label = "Color de la etiqueta"
    )
    Surface(
        color = color,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = texto,
            color = TextoSobreEtiqueta,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

// Etiqueta según el estado de una bitácora
@Composable
fun EtiquetaBitacora(estado: String) {
    when (estado) {
        EstadoBitacora.PUBLICADA -> EtiquetaEstado("Activa", ColorPublicada)
        EstadoBitacora.CERRADA -> EtiquetaEstado("Cerrada", ColorCerrada)
        else -> EtiquetaEstado("Borrador", ColorBorrador)
    }
}

// Etiquetas de un registro: borrador / enviado + pendiente / revisado
@Composable
fun EtiquetasRegistro(registro: RegistroDetalle) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (registro.estadoEnvio == EstadoEnvio.BORRADOR) {
            EtiquetaEstado("Borrador", ColorBorrador)
        } else {
            EtiquetaEstado("Enviado", ColorPublicada)
            if (registro.estadoRevision == EstadoRevision.REVISADO) {
                EtiquetaEstado("Revisado", ColorExitoSuave)
            } else {
                EtiquetaEstado("Pendiente de revisión", ColorAdvertenciaSuave)
            }
        }
    }
}
