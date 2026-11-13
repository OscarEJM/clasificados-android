package com.univalle.clasificados.ui.componentes

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R

/**
 * Barra superior con flecha atrás y título, igual a la de la pantalla 02 · Registro de Figma
 * (56 dp, título de 18 sp). Se usa en la pantalla 04 y en el marcador del listado (HU-06).
 * La flecha tiene zona táctil de 48 dp (RNF-12).
 *
 * @param titulo texto de la barra.
 * @param alVolver se llama al tocar la flecha.
 */
@Composable
fun BarraSuperior(
    titulo: String,
    alVolver: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    Row(
        modifier = modifier.fillMaxWidth().height(56.dp).padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = alVolver) {
            Icon(
                painter = painterResource(R.drawable.ic_atras),
                contentDescription = stringResource(R.string.accion_volver),
                tint = colores.onSurface,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = titulo,
            color = colores.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
