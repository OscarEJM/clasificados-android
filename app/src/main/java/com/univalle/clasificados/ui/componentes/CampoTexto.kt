package com.univalle.clasificados.ui.componentes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/** Forma de la caja del campo: esquinas de 12 dp, como el componente «Campo de texto» de Figma. */
private val FormaCaja = RoundedCornerShape(12.dp)

/**
 * Componente «Campo de texto» de Figma: etiqueta arriba (13 sp), caja de 48 dp con borde y
 * esquinas de 12 dp, ícono opcional a la derecha y debajo la ayuda o el error (12 sp).
 * Usa la caja con borde de Material 3 (la misma de OutlinedTextField) con las medidas de Figma.
 * Con error, el borde y el mensaje se ven en el color *error* del tema (diseño aprobado por Oscar).
 *
 * @param etiqueta texto encima de la caja, por ejemplo «Nombre completo».
 * @param ejemplo texto gris que se ve cuando la caja está vacía.
 * @param icono ícono de Figma a la derecha (correo, teléfono); se ignora si [esContrasena].
 * @param error mensaje de error; si no es `null` reemplaza a [ayuda].
 * @param ayuda texto de ayuda bajo la caja, por ejemplo «Mínimo 6 caracteres».
 * @param esContrasena oculta el texto y muestra el ojo para verlo (azul cuando está visible).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    alCambiar: (String) -> Unit,
    modifier: Modifier = Modifier,
    ejemplo: String = "",
    @DrawableRes icono: Int? = null,
    error: String? = null,
    ayuda: String? = null,
    esContrasena: Boolean = false,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    habilitado: Boolean = true
) {
    val colores = MaterialTheme.colorScheme
    val extra = ClasificadosTheme.colores
    var contrasenaVisible by rememberSaveable { mutableStateOf(false) }
    val interaccion = remember { MutableInteractionSource() }
    val transformacion =
        if (esContrasena && !contrasenaVisible) PasswordVisualTransformation() else VisualTransformation.None
    val coloresCaja = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = colores.surface,
        unfocusedContainerColor = colores.surface,
        disabledContainerColor = colores.surface,
        errorContainerColor = colores.surface,
        focusedBorderColor = colores.primary,
        unfocusedBorderColor = extra.borde,
        errorBorderColor = colores.error,
        focusedPlaceholderColor = extra.textoSecundario,
        unfocusedPlaceholderColor = extra.textoSecundario,
        errorPlaceholderColor = extra.textoSecundario
    )

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = etiqueta, color = colores.onSurface, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        BasicTextField(
            value = valor,
            onValueChange = alCambiar,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            enabled = habilitado,
            singleLine = true,
            textStyle = TextStyle(color = colores.onSurface, fontSize = 15.sp),
            cursorBrush = SolidColor(colores.primary),
            visualTransformation = transformacion,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (esContrasena) KeyboardType.Password else tipoTeclado
            ),
            interactionSource = interaccion,
            decorationBox = { campo ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = valor,
                    innerTextField = campo,
                    enabled = habilitado,
                    singleLine = true,
                    visualTransformation = transformacion,
                    interactionSource = interaccion,
                    isError = error != null,
                    placeholder = { Text(ejemplo, fontSize = 15.sp) },
                    trailingIcon = when {
                        esContrasena -> {
                            {
                                IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_ojo),
                                        contentDescription = stringResource(
                                            if (contrasenaVisible) R.string.accion_ocultar_contrasena
                                            else R.string.accion_mostrar_contrasena
                                        ),
                                        tint = if (contrasenaVisible) colores.primary else extra.textoSecundario,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        icono != null -> {
                            {
                                Icon(
                                    painter = painterResource(icono),
                                    contentDescription = null,
                                    tint = extra.textoSecundario,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        else -> null
                    },
                    colors = coloresCaja,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = habilitado,
                            isError = error != null,
                            interactionSource = interaccion,
                            colors = coloresCaja,
                            shape = FormaCaja,
                            focusedBorderThickness = 1.dp,
                            unfocusedBorderThickness = 1.dp
                        )
                    }
                )
            }
        )
        val textoAbajo = error ?: ayuda
        if (textoAbajo != null) {
            Text(
                text = textoAbajo,
                color = if (error != null) colores.error else extra.textoSecundario,
                fontSize = 12.sp
            )
        }
    }
}
