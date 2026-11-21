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
    primary = BerryOscuro,
    onPrimary = Tinta,
    primaryContainer = BerryContenedorOscuro,
    onPrimaryContainer = RosaSuave,
    secondary = GrisMutedOscuro,
    onSecondary = Tinta,
    secondaryContainer = GrisContenedorOscuro,
    onSecondaryContainer = TextoPrincipalOscuro,
    tertiary = MentaClara,
    onTertiary = Tinta,
    tertiaryContainer = MentaContenedorOscuro,
    onTertiaryContainer = MentaSuave,
    background = FondoOscuro,
    onBackground = TextoPrincipalOscuro,
    surface = FondoOscuro,
    onSurface = TextoPrincipalOscuro,
    surfaceVariant = TarjetaOscura,
    onSurfaceVariant = GrisMutedOscuro,
    outline = BordeOscuro,
    outlineVariant = GrisContenedorOscuro,
    surfaceContainerHighest = TarjetaOscura
)

private val LightColorScheme = lightColorScheme(
    primary = Berry,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = RosaSuave,
    onPrimaryContainer = Tinta,
    secondary = GrisMuted,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = GrisSuave,
    onSecondaryContainer = Tinta,
    tertiary = Menta,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = MentaSuave,
    onTertiaryContainer = MentaOscuro,
    background = BlancoCalido,
    onBackground = Tinta,
    surface = BlancoCalido,
    onSurface = Tinta,
    // Fondo de las Card y de los recuadros grises: rosa casi blanco en vez del lila por defecto
    surfaceVariant = TarjetaRosa,
    onSurfaceVariant = GrisMuted,
    outline = BordeRosa,
    outlineVariant = BordeRosaSuave,
    surfaceContainerHighest = TarjetaRosa
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
