package com.univalle.clasificados.ui.componentes

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/** Forma de la tarjeta de Figma: esquinas de 20 dp. */
private val FormaTarjeta = RoundedCornerShape(20.dp)

/**
 * Tarjeta de categoría de Figma (pantallas 03 y 04, HU-06 · RNF-11): 120 dp de alto, borde,
 * ícono de 28 dp sobre un fondo de color de 56 dp y el nombre debajo, **sin descripción**.
 * Ocupa todo el ancho de su columna, así la cuadrícula queda de dos por renglón.
 *
 * @param nombre texto bajo el ícono.
 * @param icono ícono de Figma.
 * @param fondoIcono color del cuadro detrás del ícono.
 * @param colorIcono color del ícono.
 * @param alTocar se llama al tocar la tarjeta.
 */
@Composable
fun TarjetaCategoria(
    nombre: String,
    @DrawableRes icono: Int,
    fondoIcono: Color,
    colorIcono: Color,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(FormaTarjeta)
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, ClasificadosTheme.colores.borde, FormaTarjeta)
            .clickable(role = Role.Button, onClick = alTocar)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(fondoIcono, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icono),
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            text = nombre,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Tarjeta de una [Categoria] con sus colores de Figma. En modo claro usa el fondo y el ícono de
 * la tabla de la sección 1.4; en modo oscuro, el color del ícono al 18 % de fondo y el ícono aclarado.
 */
@Composable
fun TarjetaCategoria(
    categoria: Categoria,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val oscuro = ClasificadosTheme.colores.oscuro
    TarjetaCategoria(
        nombre = categoria.nombre,
        icono = categoria.icono,
        fondoIcono = if (oscuro) Color(categoria.iconoClaro).copy(alpha = 0.18f) else Color(categoria.fondoClaro),
        colorIcono = Color(if (oscuro) categoria.iconoOscuro else categoria.iconoClaro),
        alTocar = alTocar,
        modifier = modifier
    )
}

/**
 * Tarjeta «Más categorías» de la pantalla 03: ícono de cuadrícula gris sobre fondo
 * superficie-variante. Abre la pantalla 04 · Todas las categorías (RF-11).
 */
@Composable
fun TarjetaMasCategorias(
    nombre: String,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier
) {
    TarjetaCategoria(
        nombre = nombre,
        icono = R.drawable.ic_categorias_tarjeta,
        fondoIcono = MaterialTheme.colorScheme.surfaceVariant,
        colorIcono = ClasificadosTheme.colores.iconoNeutro,
        alTocar = alTocar,
        modifier = modifier
    )
}
