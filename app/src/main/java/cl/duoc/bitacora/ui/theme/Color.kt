package cl.duoc.bitacora.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta alegre: azul vivo, naranja cálido y verde
val AzulPrimario = Color(0xFF2979FF)
val AzulClaro = Color(0xFFDCE8FF)
val AzulOscuro = Color(0xFF0B2A66)
val NaranjaAcento = Color(0xFFFF8F00)
val NaranjaClaro = Color(0xFFFFE0B2)
val NaranjaOscuro = Color(0xFF3E2700)
val VerdeAcento = Color(0xFF43A047)
val FondoSuave = Color(0xFFF7F9FF)

// Colores para identificar estados (el caso pide diferenciarlos visualmente)
val ColorBorrador = Color(0xFFE0E0E0)    // gris: borrador
val ColorPendiente = Color(0xFFFFE082)   // amarillo: enviado, pendiente de revisión
val ColorRevisado = Color(0xFFA5D6A7)    // verde: revisado
val ColorPublicada = Color(0xFF4FC3F7)   // celeste intenso: bitácora activa / registro enviado
val ColorCerrada = Color(0xFFBDBDBD)     // gris oscuro: bitácora cerrada

// Fondo de tarjetas en modo claro (reemplaza el lila por defecto de Material 3)
val AzulTarjeta = Color(0xFFD3E0FA)

// Paleta para modo oscuro (mismos tonos, versión clara para que se lean sobre fondo oscuro)
val AzulPrimarioOscuro = Color(0xFFAFC6FF)
val AzulContenedorOscuro = Color(0xFF1B3F8A)
val NaranjaAcentoOscuro = Color(0xFFFFB866)
val NaranjaContenedorOscuro = Color(0xFF5A3A00)
val VerdeAcentoOscuro = Color(0xFF81C784)
val FondoOscuro = Color(0xFF111318)
val TarjetaOscura = Color(0xFF262B36)