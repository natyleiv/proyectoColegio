package cl.duoc.bitacora.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.util.formatearFecha
import coil3.compose.AsyncImage

// Muestra el contenido de un registro (solo lectura). Lo usan el docente y el estudiante.
@Composable
fun RegistroContenido(registro: Registro) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = registro.bitacoraTitulo,
            style = MaterialTheme.typography.titleLarge
        )
        Text("Estudiante: ${registro.estudianteNombre}")
        EtiquetasRegistro(registro)

        DatoRegistro("Fecha de la clase", registro.fechaClase)
        DatoRegistro("Contenido trabajado", registro.contenido)
        DatoRegistro("Actividad realizada", registro.actividad)
        DatoRegistro("Lo que aprendí", registro.aprendizaje)
        if (registro.dudas.isNotBlank()) {
            DatoRegistro("Dudas o dificultades", registro.dudas)
        }
        if (registro.enlace.isNotBlank()) {
            DatoRegistro("Enlace", registro.enlace)
        }

        // La foto aparece con animación (clase 06: AnimatedVisibility)
        AnimatedVisibility(visible = registro.fotoUri.isNotBlank()) {
            AsyncImage(
                model = registro.fotoUri,
                contentDescription = "Evidencia fotográfica del registro",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        // Retroalimentación del docente
        if (registro.comentarioDocente.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Comentario del docente",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(registro.comentarioDocente)
                    if (registro.fechaRetro != 0L) {
                        Text(
                            text = "Fecha: ${formatearFecha(registro.fechaRetro)}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DatoRegistro(titulo: String, valor: String) {
    Column {
        Text(
            text = titulo,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(valor.ifBlank { "-" })
    }
}
