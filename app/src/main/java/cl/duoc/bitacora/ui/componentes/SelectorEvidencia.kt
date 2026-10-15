package cl.duoc.bitacora.ui.componentes

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora.util.crearUriFotografia
import coil3.compose.AsyncImage
import androidx.compose.animation.Crossfade

// RECURSOS NATIVOS (clase 06):
//  1) Photo Picker (galería) con PickVisualMedia
//  2) Cámara con TakePicture + FileProvider
// Ambos terminan entregando una URI que se muestra con Coil (AsyncImage).
@Composable
fun SelectorEvidencia(
    fotoUri: String,
    mensajeError: String,
    onGaleria: (Uri) -> Unit,
    onFotoTomada: (Uri) -> Unit,
    onQuitar: () -> Unit
) {
    val context = LocalContext.current

    // Foto que la cámara está tomando (se guarda aunque se rote la pantalla)
    var uriPendiente by rememberSaveable { mutableStateOf<Uri?>(null) }

    val selectorFotos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        // Si el usuario cancela, uri es null y no cambiamos nada
        if (uri != null) onGaleria(uri)
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        val uri = uriPendiente
        if (exito && uri != null) {
            onFotoTomada(uri)
        }
        uriPendiente = null
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Evidencia (opcional)",
            style = MaterialTheme.typography.titleMedium
        )
        // Mensaje que cambia con un desvanecido cuando se adjunta o se quita la foto (PPT 06: Crossfade)
        Crossfade(
            targetState = fotoUri.isNotBlank(),
            label = "Estado de la evidencia"
        ) { hayFoto ->
            Text(
                text = if (hayFoto) "¡Foto adjunta!" else "Aún no adjuntas una foto",
                color = if (hayFoto) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = {
                    selectorFotos.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Icon(
                    Icons.Filled.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text("  Galería")
            }
            Button(
                onClick = {
                    try {
                        val nuevaUri = crearUriFotografia(context)
                        uriPendiente = nuevaUri
                        camaraLauncher.launch(nuevaUri)
                    } catch (e: Exception) {
                        uriPendiente = null
                        e.printStackTrace()
                    }
                }
            ) {
                Icon(
                    Icons.Filled.PhotoCamera,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text("  Cámara")
            }
        }

        // Mensaje si la imagen no se pudo adjuntar (ej: pesa más de 5 MB)
        AnimatedVisibility(visible = mensajeError.isNotBlank()) {
            Text(text = mensajeError, color = MaterialTheme.colorScheme.error)
        }

        // Vista previa con animación y botón para quitar la foto
        AnimatedVisibility(
            visible = fotoUri.isNotBlank(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column {
                AsyncImage(
                    model = fotoUri,
                    contentDescription = "Fotografía seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                TextButton(onClick = { onQuitar() }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text("  Quitar foto")
                }
            }
        }
    }
}
