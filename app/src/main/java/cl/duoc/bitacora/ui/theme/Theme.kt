package cl.duoc.bitacora.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
private val DarkColorScheme = darkColorScheme(
    primary = AzulPrimarioOscuro,
    onPrimary = AzulOscuro,
    primaryContainer = AzulContenedorOscuro,
    onPrimaryContainer = AzulClaro,
    secondary = NaranjaAcentoOscuro,
    secondaryContainer = NaranjaContenedorOscuro,
    onSecondaryContainer = NaranjaClaro,
    tertiary = VerdeAcentoOscuro,
    background = FondoOscuro,
    surface = FondoOscuro,
    surfaceVariant = TarjetaOscura,
    surfaceContainerHighest = TarjetaOscura
)

private val LightColorScheme = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulOscuro,
    secondary = NaranjaAcento,
    secondaryContainer = NaranjaClaro,
    onSecondaryContainer = NaranjaOscuro,
    tertiary = VerdeAcento,
    background = FondoSuave,
    surface = FondoSuave,
    // Fondo de las Card y de los recuadros grises: azul muy claro en vez del lila por defecto
    surfaceVariant = AzulTarjeta,
    surfaceContainerHighest = AzulTarjeta
)

// Esquinas más redondeadas en tarjetas, botones y campos (aspecto más amigable)
private val FormasRedondeadas = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun BitacoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = FormasRedondeadas,
        content = content
    )
}
