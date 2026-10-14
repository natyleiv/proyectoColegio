package cl.duoc.bitacora.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

// Restricción de tamaño para las evidencias (el caso pide limitar tipos y tamaños)
const val TAMANO_MAXIMO_BYTES = 5L * 1024 * 1024   // 5 MB

private fun carpetaEvidencias(context: Context): File {
    val carpeta = File(context.filesDir, "evidencias")
    carpeta.mkdirs()
    return carpeta
}

private fun uriDeArchivo(context: Context, archivo: File): Uri {
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        archivo
    )
}

// CÁMARA (clase 06): crea un archivo vacío y devuelve la URI donde la cámara guardará la foto
fun crearUriFotografia(context: Context): Uri {
    val archivo = File.createTempFile("foto_", ".jpg", carpetaEvidencias(context))
    return uriDeArchivo(context, archivo)
}

// GALERÍA: la URI que entrega el Photo Picker es temporal, así que COPIAMOS la imagen
// a la carpeta privada de la app para poder verla después. Devuelve null si falla
// o si la imagen pesa más de 5 MB.
fun copiarImagenAlmacenamientoApp(context: Context, uri: Uri): Uri? {
    return try {
        val destino = File.createTempFile("evidencia_", ".jpg", carpetaEvidencias(context))
        val entrada = context.contentResolver.openInputStream(uri)
        if (entrada == null) {
            destino.delete()
            null
        } else {
            entrada.use { origen ->
                destino.outputStream().use { salida ->
                    origen.copyTo(salida)
                }
            }
            if (destino.length() > TAMANO_MAXIMO_BYTES) {
                destino.delete()
                null
            } else {
                uriDeArchivo(context, destino)
            }
        }
    } catch (e: Exception) {
        null
    }
}
