package cl.duoc.bitacora

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import cl.duoc.bitacora.ui.navegacion.Navegacion
import cl.duoc.bitacora.ui.theme.BitacoraTheme
import cl.duoc.bitacora.util.crearCanalNotificaciones

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        crearCanalNotificaciones(this)
        setContent {
            BitacoraTheme {
                // Mismo patrón que la galería y la cámara (PPT 06), pero pidiendo un permiso
                val permisoNotificaciones = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { }

                // Al abrir la app se pide el permiso una vez (solo existe desde Android 13)
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                // Cada pantalla trae su propio Scaffold (clase 03)
                Navegacion()
            }
        }
    }
}
