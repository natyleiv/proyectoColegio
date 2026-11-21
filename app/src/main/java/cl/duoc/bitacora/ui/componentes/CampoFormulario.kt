package cl.duoc.bitacora.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

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
    placeholder: String = "",
    soloLectura: Boolean = false,
    // Si es true, el título va sobre el campo (más limpio) y no como etiqueta flotante
    etiquetaArriba: Boolean = false
) {
    // Para los campos de contraseña: el ojo muestra u oculta lo escrito
    var verPassword by remember { mutableStateOf(false) }

    // Etiqueta flotante dentro del campo (solo cuando el título no va arriba)
    val etiquetaFlotante: (@Composable () -> Unit)? =
        if (etiquetaArriba) null else { { Text(etiqueta) } }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (etiquetaArriba) {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            label = etiquetaFlotante,
            placeholder = { if (placeholder.isNotEmpty()) Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (hayError) {
                    Icon(
                        imageVector = Icons.Filled.Error,
                        contentDescription = "Campo con error",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else if (esPassword) {
                    IconButton(onClick = { verPassword = !verPassword }) {
                        Icon(
                            imageVector = if (verPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (verPassword) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                }
            },
            isError = hayError,
            supportingText = {
                // El mensaje de error aparece con una animación
                AnimatedVisibility(visible = hayError) {
                    Text(mensajeError)
                }
            },
            visualTransformation = if (esPassword && !verPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = teclado),
            readOnly = soloLectura,
            singleLine = lineas == 1,
            minLines = lineas,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
