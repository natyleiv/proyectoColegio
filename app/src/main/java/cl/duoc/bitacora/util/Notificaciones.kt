package cl.duoc.bitacora.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

// Id del canal por donde salen los avisos de la app
const val CANAL_AVISOS = "avisos_bitacora"

// Desde Android 8 toda notificación debe pertenecer a un canal.
// HIGH hace que el aviso aparezca encima de la pantalla.
fun crearCanalNotificaciones(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val canal = NotificationChannel(
            CANAL_AVISOS,
            "Avisos de la bitácora",
            NotificationManager.IMPORTANCE_HIGH
        )
        canal.description = "Confirmaciones de registros y bitácoras"
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(canal)
    }
}

// Muestra una notificación del sistema. Si el usuario no dio permiso, no hace nada.
fun mostrarNotificacion(context: Context, titulo: String, texto: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        return
    }

    val notificacion = NotificationCompat.Builder(context, CANAL_AVISOS)
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle(titulo)
        .setContentText(texto)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    // Cada notificación usa un id distinto para que no se reemplacen entre sí
    NotificationManagerCompat.from(context)
        .notify(System.currentTimeMillis().toInt(), notificacion)
}