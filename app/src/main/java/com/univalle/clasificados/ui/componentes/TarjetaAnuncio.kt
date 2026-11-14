package com.univalle.clasificados.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import com.univalle.clasificados.R
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.util.FormatoPrecio

/** Forma de la tarjeta de anuncio de Figma: esquinas de 16 dp. */
private val FormaTarjeta = RoundedCornerShape(16.dp)

/**
 * Tarjeta de anuncio de Figma (pantalla 05, HU-07 · RF-12): primera foto de 112 dp de alto,
 * título de máximo dos líneas (con «…» si es más largo), precio en pesos colombianos (RN-21)
 * y ciudad con el ícono de ubicación. Ocupa el ancho de su columna (dos por renglón, RNF-11).
 *
 * Mientras la foto carga, o si el anuncio no tiene foto o falla la descarga, se ve el ícono de
 * imagen de Figma sobre el gris superficie-variante.
 *
 * @param anuncio datos que se muestran.
 * @param alTocar se llama al tocar la tarjeta (el detalle se abre en HU-08).
 */
@Composable
fun TarjetaAnuncio(
    anuncio: Anuncio,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    val secundario = ClasificadosTheme.colores.textoSecundario
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(FormaTarjeta)
            .background(colores.surface)
            .border(1.dp, ClasificadosTheme.colores.borde, FormaTarjeta)
            .clickable(role = Role.Button, onClick = alTocar)
    ) {
        SubcomposeAsyncImage(
            model = anuncio.fotos.firstOrNull(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .background(colores.surfaceVariant),
            loading = { IconoSinFoto() },
            error = { IconoSinFoto() }
        )
        Column(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = anuncio.titulo,
                color = colores.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = FormatoPrecio.formatear(anuncio.precio),
                color = colores.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_ubicacion),
                    contentDescription = null,
                    tint = secundario,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = anuncio.ciudad,
                    color = secundario,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Ícono de imagen de Figma (32 dp) centrado: se ve mientras no hay foto que mostrar. */
@Composable
private fun IconoSinFoto() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().height(112.dp)) {
        Icon(
            painter = painterResource(R.drawable.ic_imagen),
            contentDescription = null,
            tint = ClasificadosTheme.colores.textoSecundario,
            modifier = Modifier.size(32.dp)
        )
    }
}
