package cl.duoc.bitacora.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

// Pruebas de las funciones de fechas (Fechas.kt) que usan los formularios y el calendario
class FechasTest {

    // ---------- Nombre del mes ----------
    @Test
    fun nombreMes_mesesLimite_retornaNombre() {
        assertEquals("Enero", nombreMes(1))
        assertEquals("Octubre", nombreMes(10))
        assertEquals("Diciembre", nombreMes(12))
    }

    @Test
    fun nombreMes_fueraDeRango_retornaTextoGenerico() {
        assertEquals("Mes 0", nombreMes(0))
        assertEquals("Mes 13", nombreMes(13))
    }

    // ---------- Calendario: ida y vuelta entre texto y milisegundos ----------
    @Test
    fun primerDiaParaCalendario_octubre2026_esElDiaUno() {
        // ACT
        val millis = primerDiaParaCalendario(10, 2026)
        // ASSERT
        assertEquals("01/10/2026", formatearFechaCalendario(millis))
        assertEquals(10, mesDeCalendario(millis))
        assertEquals(2026, anioDeCalendario(millis))
    }

    @Test
    fun parsearFechaCalendario_fechaValida_vuelveAlMismoTexto() {
        // ARRANGE
        val texto = "20/10/2026"
        // ACT
        val millis = parsearFechaCalendario(texto)
        // ASSERT: al volver a formatear se obtiene la misma fecha
        assertEquals(texto, formatearFechaCalendario(millis!!))
    }

    @Test
    fun parsearFechaCalendario_fechaQueNoExiste_retornaNull() {
        assertNull(parsearFechaCalendario("31/02/2026"))
    }

    @Test
    fun parsearFechaCalendario_formatoIncorrecto_retornaNull() {
        assertNull(parsearFechaCalendario("2026"))          // largo distinto de 10
        assertNull(parsearFechaCalendario("aa/bb/cccc"))    // largo 10, pero no es fecha
    }

    // ---------- "Hoy" ----------
    @Test
    fun hoyParaCalendario_correspondeALaFechaDeHoy() {
        assertEquals(fechaHoy(), formatearFechaCalendario(hoyParaCalendario()))
    }

    @Test
    fun formatearFecha_milisegundosActuales_coincideConFechaHoy() {
        assertEquals(fechaHoy(), formatearFecha(System.currentTimeMillis()))
    }

    @Test
    fun anioActual_coincideConElCalendarioDelSistema() {
        assertEquals(Calendar.getInstance().get(Calendar.YEAR), anioActual())
    }
}