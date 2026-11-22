package cl.duoc.bitacora.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Pruebas de las reglas de validación de los formularios (Validaciones.kt).
// Cada función devuelve true si el dato es válido y false si no.
class ValidacionesTest {

    // ---------- Correo ----------
    @Test
    fun validarCorreo_correoInstitucionalValido_retornaTrue() {
        // ARRANGE
        val correo = "docente1@escuela.cl"
        // ACT
        val resultado = validarCorreo(correo)
        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarCorreo_vacio_retornaFalse() {
        assertFalse(validarCorreo(""))
    }

    @Test
    fun validarCorreo_sinArroba_retornaFalse() {
        assertFalse(validarCorreo("docente1escuela.cl"))
    }

    @Test
    fun validarCorreo_noTerminaEnPuntoCl_retornaFalse() {
        assertFalse(validarCorreo("docente1@escuela.com"))
    }

    // ---------- Contraseña (mínimo 8 caracteres) ----------
    @Test
    fun validarPassword_sieteCaracteres_retornaFalse() {
        assertFalse(validarPassword("1234567"))
    }

    @Test
    fun validarPassword_ochoCaracteres_retornaTrue() {
        assertTrue(validarPassword("12345678"))
    }

    // ---------- Campo obligatorio ----------
    @Test
    fun validarCampoObligatorio_vacioOSoloEspacios_retornaFalse() {
        assertFalse(validarCampoObligatorio(""))
        assertFalse(validarCampoObligatorio("   "))
    }

    @Test
    fun validarCampoObligatorio_conTexto_retornaTrue() {
        assertTrue(validarCampoObligatorio("Fracciones"))
    }

    // ---------- Largo mínimo (ej: título de 5 caracteres) ----------
    @Test
    fun validarLargoMinimo_menosDelMinimoAunqueTengaEspacios_retornaFalse() {
        // "  Mate  " sin espacios queda "Mate" (4 letras)
        assertFalse(validarLargoMinimo("  Mate  ", 5))
    }

    @Test
    fun validarLargoMinimo_justoElMinimo_retornaTrue() {
        assertTrue(validarLargoMinimo("Notas", 5))
    }

    // ---------- Mes (1 a 12) ----------
    @Test
    fun validarMes_limitesValidos_retornaTrue() {
        assertTrue(validarMes("1"))
        assertTrue(validarMes("12"))
        assertTrue(validarMes(" 5 "))
    }

    @Test
    fun validarMes_fueraDeRango_retornaFalse() {
        assertFalse(validarMes("0"))
        assertFalse(validarMes("13"))
    }

    @Test
    fun validarMes_noNumerico_retornaFalse() {
        assertFalse(validarMes("octubre"))
    }

    // ---------- Año (2025 a 2100) ----------
    @Test
    fun validarAnio_limitesValidos_retornaTrue() {
        assertTrue(validarAnio("2025"))
        assertTrue(validarAnio("2100"))
    }

    @Test
    fun validarAnio_fueraDeRango_retornaFalse() {
        assertFalse(validarAnio("2024"))
        assertFalse(validarAnio("2101"))
    }

    @Test
    fun validarAnio_noNumerico_retornaFalse() {
        assertFalse(validarAnio("dos mil"))
    }

    // ---------- Asignación (curso y asignatura) ----------
    @Test
    fun validarAsignacionSeleccionada_cero_retornaFalse() {
        assertFalse(validarAsignacionSeleccionada(0))
    }

    @Test
    fun validarAsignacionSeleccionada_idValido_retornaTrue() {
        assertTrue(validarAsignacionSeleccionada(1))
    }

    // ---------- Fecha de la clase (dd/MM/yyyy, existe y no es futura) ----------
    @Test
    fun validarFechaClase_fechaPasadaValida_retornaTrue() {
        assertTrue(validarFechaClase("05/10/2025"))
    }

    @Test
    fun validarFechaClase_fechaFutura_retornaFalse() {
        assertFalse(validarFechaClase("01/01/2099"))
    }

    @Test
    fun validarFechaClase_fechaQueNoExiste_retornaFalse() {
        // 31 de febrero no existe
        assertFalse(validarFechaClase("31/02/2026"))
    }

    @Test
    fun validarFechaClase_formatoIncorrecto_retornaFalse() {
        assertFalse(validarFechaClase("5/10/2026"))      // largo distinto de 10
        assertFalse(validarFechaClase("aa/bb/cccc"))     // largo 10, pero no es fecha
    }

    // ---------- Enlace (opcional, http:// o https://) ----------
    @Test
    fun validarEnlace_vacio_retornaTrue() {
        assertTrue(validarEnlace(""))
    }

    @Test
    fun validarEnlace_conHttpOHttps_retornaTrue() {
        assertTrue(validarEnlace("http://www.duoc.cl"))
        assertTrue(validarEnlace("https://www.duoc.cl"))
    }

    @Test
    fun validarEnlace_sinProtocolo_retornaFalse() {
        assertFalse(validarEnlace("www.duoc.cl"))
    }

    // ---------- Fecha dentro del mes de la bitácora (regla de negocio) ----------
    @Test
    fun validarFechaEnMes_mismoMesYAnio_retornaTrue() {
        assertTrue(validarFechaEnMes("20/10/2026", 10, 2026))
    }

    @Test
    fun validarFechaEnMes_otroMes_retornaFalse() {
        assertFalse(validarFechaEnMes("20/11/2026", 10, 2026))
    }

    @Test
    fun validarFechaEnMes_otroAnio_retornaFalse() {
        assertFalse(validarFechaEnMes("20/10/2025", 10, 2026))
    }

    @Test
    fun validarFechaEnMes_formatoIncorrecto_retornaFalse() {
        assertFalse(validarFechaEnMes("5/10/2026", 10, 2026))    // largo distinto de 10
        assertFalse(validarFechaEnMes("20-10-2026", 10, 2026))   // no usa "/"
    }
}