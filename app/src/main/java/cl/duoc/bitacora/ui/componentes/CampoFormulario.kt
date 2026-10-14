package cl.duoc.bitacora.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

// Campo de formulario reutilizable. Cumple la pauta: ícono del campo, ícono de error,
// borde rojo (isError) y mensaje claro bajo el campo (supportingText), todo por campo.
@Composable
fun CampoFormulario(
    valor: String,
    onValorChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    hayError: Boolean,
    mensajeError: String,
    modifier: Modifier = Modifier,
    esPassword: Boolean = false,
    lineas: Int = 1,
    teclado: KeyboardType = KeyboardType.Text,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(etiqueta) },
        placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
        leadingIcon = {
            Icon(imageVector = icono, contentDescription = null)
        },
        trailingIcon = {
            if (hayError) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = "Campo con error",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        },
        isError = hayError,
        supportingText = {
            // El mensaje de error aparece con una animación
            AnimatedVisibility(visible = hayError) {
                Text(mensajeError)
            }
        },
        visualTransformation = if (esPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = teclado),
        singleLine = lineas == 1,
        minLines = lineas,
        modifier = modifier.fillMaxWidth()
    )
}
