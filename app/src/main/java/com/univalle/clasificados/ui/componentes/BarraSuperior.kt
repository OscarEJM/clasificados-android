package com.univalle.clasificados.ui.componentes

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R

/**
 * Acción opcional a la derecha de la [BarraSuperior], por ejemplo el ícono de filtros de la
 * pantalla 05 (HU-07).
 *
 * @property icono ícono de Figma de 24 dp.
 * @property descripcion texto para lectores de pantalla y pruebas.
 * @property alTocar se llama al tocar el ícono.
 */
data class AccionBarra(
    @DrawableRes val icono: Int,
    val descripcion: String,
    val alTocar: () -> Unit
)

/**
 * Barra superior con flecha atrás y título, igual a la de la pantalla 02 · Registro de Figma
 * (56 dp, título de 18 sp). Se usa en la pantalla 04 (HU-06) y en el listado de una categoría
 * (pantalla 05, HU-07), que agrega a la derecha el ícono de filtros. Los íconos tienen zona
 * táctil de 48 dp (RNF-12).
 *
 * @param titulo texto de la barra; si es muy largo se corta con «…».
 * @param alVolver se llama al tocar la flecha.
 * @param accion ícono opcional a la derecha.
 */
@Composable
fun BarraSuperior(
    titulo: String,
    alVolver: () -> Unit,
    modifier: Modifier = Modifier,
    accion: AccionBarra? = null
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
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (accion != null) {
            IconButton(onClick = accion.alTocar) {
                Icon(
                    painter = painterResource(accion.icono),
                    contentDescription = accion.descripcion,
                    tint = colores.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
