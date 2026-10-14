package cl.duoc.bitacora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.bitacora.ui.navegacion.Navegacion
import cl.duoc.bitacora.ui.theme.BitacoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BitacoraTheme {
                // Cada pantalla trae su propio Scaffold (clase 03)
                Navegacion()
            }
        }
    }
}
