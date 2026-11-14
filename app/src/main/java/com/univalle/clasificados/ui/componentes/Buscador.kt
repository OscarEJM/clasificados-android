package com.univalle.clasificados.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/**
 * Buscador de Figma (componente «Buscador» de la pantalla 03): barra de 48 dp, gris
 * superficie-variante, esquinas de 24 dp, lupa de 20 dp y el texto de ejemplo.
 * En HU-06 solo guarda lo que se escribe; la búsqueda por título se conecta en HU-15 (RF-16).
 *
 * @param valor texto escrito.
 * @param alCambiar se llama con cada cambio del texto.
 * @param ejemplo texto gris que se ve mientras el buscador está vacío.
 */
@Composable
fun Buscador(
    valor: String,
    alCambiar: (String) -> Unit,
    ejemplo: String,
    modifier: Modifier = Modifier
) {
    val secundario = ClasificadosTheme.colores.textoSecundario
    BasicTextField(
        value = valor,
        onValueChange = alCambiar,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        decorationBox = { campo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_buscar),
                    contentDescription = null,
                    tint = secundario,
                    modifier = Modifier.size(20.dp)
                )
                Box(modifier = Modifier.weight(1f)) {
                    if (valor.isEmpty()) Text(text = ejemplo, color = secundario, fontSize = 14.sp)
                    campo()
                }
            }
        }
    )
}
