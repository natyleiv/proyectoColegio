package cl.duoc.bitacora.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Formatea milisegundos como "dd/MM/yyyy"
fun formatearFecha(millis: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.US).format(Date(millis))
}

fun fechaHoy(): String {
    return formatearFecha(System.currentTimeMillis())
}

fun nombreMes(mes: Int): String {
    val meses = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )
    return if (mes in 1..12) meses[mes - 1] else "Mes $mes"
}

// ---------------- Calendario (DatePicker de Material 3) ----------------
// El DatePicker trabaja con milisegundos en hora UTC (medianoche UTC del día elegido).
// Por eso usamos formatos en UTC para que el día mostrado sea exactamente el elegido.

// Convierte los milisegundos del calendario a "dd/MM/yyyy"
fun formatearFechaCalendario(millisUtc: Long): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    formato.timeZone = TimeZone.getTimeZone("UTC")
    return formato.format(Date(millisUtc))
}

// Convierte "dd/MM/yyyy" a milisegundos UTC (para dejar marcada la fecha en el calendario).
// Devuelve null si el texto no es una fecha válida.
fun parsearFechaCalendario(texto: String): Long? {
    if (texto.length != 10) return null
    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        formato.timeZone = TimeZone.getTimeZone("UTC")
        formato.isLenient = false
        formato.parse(texto)?.time
    } catch (e: Exception) {
        null
    }
}

// "Hoy" (según la fecha local del teléfono) expresado como medianoche UTC.
// Sirve para bloquear en el calendario los días futuros.
fun hoyParaCalendario(): Long {
    val local = Calendar.getInstance()
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utc.clear()
    utc.set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
    return utc.timeInMillis
}

// Mes (1-12) de una fecha del calendario
fun mesDeCalendario(millisUtc: Long): Int {
    val c = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    c.timeInMillis = millisUtc
    return c.get(Calendar.MONTH) + 1
}

// Año de una fecha del calendario
fun anioDeCalendario(millisUtc: Long): Int {
    val c = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    c.timeInMillis = millisUtc
    return c.get(Calendar.YEAR)
}

// Medianoche UTC del día 1 de un mes/año (para abrir el calendario en ese mes)
fun primerDiaParaCalendario(mes: Int, anio: Int): Long {
    val c = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    c.clear()
    c.set(anio, mes - 1, 1)
    return c.timeInMillis
}

// Año actual (para limitar el calendario de la fecha de clase)
fun anioActual(): Int {
    return Calendar.getInstance().get(Calendar.YEAR)
}
