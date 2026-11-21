package cl.duoc.bitacora.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta "Berry + Ink": frambuesa (principal), rosa suave (contenedores), tinta (texto) y menta
val Tinta = Color(0xFF24202D)          // texto principal
val Berry = Color(0xFFA83D72)          // color principal: botones, títulos, íconos
val RosaSuave = Color(0xFFF3DFE9)      // contenedores y tarjetas destacadas
val BlancoCalido = Color(0xFFFFFDFB)   // fondo de las pantallas
val GrisMuted = Color(0xFF6D6475)      // texto secundario
val Menta = Color(0xFF43A786)          // acento verde
val MentaSuave = Color(0xFFDDF3EA)
val MentaOscuro = Color(0xFF0F3D2E)
val GrisSuave = Color(0xFFF1EDF3)      // contenedor secundario
val BordeRosa = Color(0xFFB9A9B4)      // bordes de botones y campos
val BordeRosaSuave = Color(0xFFEBDCE4) // divisores y bordes muy suaves

// Colores para identificar estados (el caso pide diferenciarlos visualmente)
val ColorPendiente = Color(0xFFFFE082)   // amarillo: enviado, pendiente de revisión
val ColorRevisado = Color(0xFFA5D6A7)    // verde: revisado

// Etiquetas de estado: cada una con su fondo pastel y su texto del mismo tono, más oscuro
val FondoActiva = Color(0xFFE3F5EA)      // verde: bitácora activa y registro revisado
val TextoActiva = Color(0xFF2E7D5B)
val FondoPendiente = Color(0xFFFFF1D6)   // ámbar: pendiente de revisión
val TextoPendiente = Color(0xFF9A6410)
val FondoBorrador = Color(0xFFE9E2FA)    // lila: borrador
val TextoBorrador = Color(0xFF6B4DB8)
val FondoCerrada = Color(0xFFFCE3E3)     // rosado rojizo: bitácora cerrada
val TextoCerrada = Color(0xFFB94056)
val FondoEnviado = RosaSuave             // rosa de la paleta: registro enviado
val TextoEnviado = Color(0xFF8C2F5E)

// Colores SEMÁNTICOS centralizados (todos los mensajes y estados de la app salen de aquí):
//   éxito = verde · advertencia = ámbar · error = rojo
// Versión fuerte (fondo de mensajes Snackbar con texto blanco)
val ColorExito = Color(0xFF2E7D32)
val ColorAdvertencia = Color(0xFFF9A825)
val ColorError = Color(0xFFC62828)
// Versión suave (etiquetas de estado, con texto oscuro)
val ColorExitoSuave = ColorRevisado
val ColorAdvertenciaSuave = ColorPendiente
val ColorErrorSuave = Color(0xFFEF9A9A)
// Color del texto sobre cada fondo semántico
val TextoSobreExito = Color(0xFFFFFFFF)
val TextoSobreError = Color(0xFFFFFFFF)
val TextoSobreAdvertencia = Color(0xFF1B1B1B)

// Texto oscuro que se usa sobre las etiquetas de estado de colores suaves
val TextoSobreEtiqueta = Color(0xFF1B1B1B)

// Fondo de tarjetas en modo claro: rosa casi blanco (reemplaza el lila por defecto de Material 3)
val TarjetaRosa = Color(0xFFF8EEF3)

// Paleta para modo oscuro (mismos tonos, versión clara para que se lean sobre fondo oscuro)
val BerryOscuro = Color(0xFFF2A6C9)
val BerryContenedorOscuro = Color(0xFF6B2A4A)
val GrisMutedOscuro = Color(0xFFC9BFD0)
val GrisContenedorOscuro = Color(0xFF3A3340)
val MentaClara = Color(0xFF7FD3B1)
val MentaContenedorOscuro = Color(0xFF1C4D3C)
val FondoOscuro = Color(0xFF1A161D)
val TarjetaOscura = Color(0xFF2B2530)
val TextoPrincipalOscuro = Color(0xFFF1EAF0)
val BordeOscuro = Color(0xFF8F8596)
