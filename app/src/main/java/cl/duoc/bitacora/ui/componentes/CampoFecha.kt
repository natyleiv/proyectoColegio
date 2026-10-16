package cl.duoc.bitacora.ui.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cl.duoc.bitacora.util.hoyParaCalendario

// Campo de fecha que NO se escribe a mano: al tocarlo se abre un calendario (DatePicker de Material 3).
// - textoMostrado: lo que se ve dentro del campo (ej: "05/10/2026" o "Octubre 2026")
// - fechaInicial: fecha (milisegundos UTC) que aparece marcada al abrir; null = hoy
// - onFechaElegida: recibe los milisegundos UTC del día elegido
// - bloquearFuturas: si es true, los días posteriores a hoy no se pueden elegir
// - anios: rango de años que ofrece el selector de año del calendario
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoFecha(
    textoMostrado: String,
    etiqueta: String,
    hayError: Boolean,
    mensajeError: String,
    onFechaElegida: (Long) -> Unit,
    modifier: Modifier = Modifier,
    fechaInicial: Long? = null,
    bloquearFuturas: Boolean = false,
    anios: IntRange = 2025..2100
) {
    var mostrarCalendario by remember { mutableStateOf(false) }

    // El campo es de solo lectura; una capa transparente encima recibe el toque
    // (así el teclado nunca aparece y toda el área del campo abre el calendario).
    Box(modifier = modifier.fillMaxWidth()) {
        CampoFormulario(
            valor = textoMostrado,
            onValorChange = { },
            etiqueta = etiqueta,
            icono = Icons.Filled.DateRange,
            hayError = hayError,
            mensajeError = mensajeError,
            soloLectura = true,
            placeholder = "Toca para elegir en el calendario"
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { mostrarCalendario = true }
        )
    }

    if (mostrarCalendario) {
        val hoy = hoyParaCalendario()
        val estado = rememberDatePickerState(
            initialSelectedDateMillis = fechaInicial,
            initialDisplayedMonthMillis = fechaInicial ?: hoy,
            yearRange = anios,
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    return !bloquearFuturas || utcTimeMillis <= hoy
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year in anios
                }
            }
        )
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val elegida = estado.selectedDateMillis
                        if (elegida != null) onFechaElegida(elegida)
                        mostrarCalendario = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estado)
        }
    }
}
