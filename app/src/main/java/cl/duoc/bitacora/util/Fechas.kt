package cl.duoc.bitacora.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
