package cl.duoc.bitacora.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Reglas de validación. NO saben nada de la interfaz: solo responden "¿es válido?" (Boolean).
// Las llaman los ViewModel (clase 04: Validaciones.kt).

fun validarCorreo(correo: String): Boolean {
    return correo.isNotBlank() &&
            correo.contains("@") &&
            correo.endsWith(".cl")
}

fun validarPassword(password: String): Boolean {
    return password.length >= 8
}

fun validarCampoObligatorio(texto: String): Boolean {
    return texto.isNotBlank()
}

// Para textos que deben tener un mínimo de caracteres (ej: título)
fun validarLargoMinimo(texto: String, minimo: Int): Boolean {
    return texto.trim().length >= minimo
}

fun validarMes(mes: String): Boolean {
    val numero = mes.trim().toIntOrNull()
    return numero != null && numero in 1..12
}

fun validarAnio(anio: String): Boolean {
    val numero = anio.trim().toIntOrNull()
    return numero != null && numero in 2025..2100
}

fun validarAsignacionSeleccionada(asignacionId: Int): Boolean {
    return asignacionId != 0
}

// Fecha de la clase en formato dd/MM/yyyy, que exista y que no sea futura
fun validarFechaClase(fecha: String): Boolean {
    if (fecha.length != 10) return false
    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        formato.isLenient = false          // rechaza fechas como 31/02/2026
        val fechaClase = formato.parse(fecha) ?: return false
        !fechaClase.after(Date())
    } catch (e: Exception) {
        false
    }
}

// El enlace es opcional: si viene vacío es válido; si viene, debe partir con http:// o https://
fun validarEnlace(enlace: String): Boolean {
    return enlace.isBlank() ||
            enlace.startsWith("http://") ||
            enlace.startsWith("https://")
}
