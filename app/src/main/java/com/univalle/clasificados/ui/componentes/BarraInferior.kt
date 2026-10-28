package com.univalle.clasificados.ui.componentes

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.navegacion.Rutas
import com.univalle.clasificados.ui.theme.ClasificadosTheme

private data class Pestana(
    val ruta: String,
    @StringRes val etiqueta: Int,
    @DrawableRes val icono: Int
)

// Orden fijo de la guía (sección 1.9): Inicio, Publicar, Mis anuncios, Ajustes.
private val pestanas = listOf(
    Pestana(Rutas.INICIO, R.string.pestana_inicio, R.drawable.ic_inicio),
    Pestana(Rutas.PUBLICAR, R.string.pestana_publicar, R.drawable.ic_mas),
    Pestana(Rutas.MIS_ANUNCIOS, R.string.pestana_mis_anuncios, R.drawable.ic_mis_anuncios),
    Pestana(Rutas.AJUSTES, R.string.pestana_ajustes, R.drawable.ic_ajustes)
)

/**
 * Barra inferior según el componente «Barra inferior» de Figma (pantalla 03):
 * borde superior, pestañas de 64 dp e indicador de 56 × 32 dp. «Publicar» siempre va en verde.
 */
@Composable
fun BarraInferior(
    rutaActual: String?,
    alSeleccionar: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        HorizontalDivider(thickness = 1.dp, color = ClasificadosTheme.colores.borde)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .selectableGroup()
        ) {
            pestanas.forEach { pestana ->
                ElementoBarra(
                    pestana = pestana,
                    seleccionada = rutaActual == pestana.ruta,
                    alTocar = { alSeleccionar(pestana.ruta) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ElementoBarra(
    pestana: Pestana,
    seleccionada: Boolean,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val esPublicar = pestana.ruta == Rutas.PUBLICAR
    val colores = ClasificadosTheme.colores

    val fondoIndicador = when {
        esPublicar -> colores.publicar
        seleccionada -> colores.primarioSuave
        else -> Color.Transparent
    }
    val colorIcono = when {
        esPublicar -> MaterialTheme.colorScheme.onPrimary
        seleccionada -> MaterialTheme.colorScheme.primary
        else -> colores.textoSecundario
    }
    val colorTexto = when {
        esPublicar -> MaterialTheme.colorScheme.onSurface
        seleccionada -> MaterialTheme.colorScheme.primary
        else -> colores.textoSecundario
    }
    val pesoTexto = if (esPublicar || seleccionada) FontWeight.SemiBold else FontWeight.Medium

    Column(
        modifier = modifier
            .fillMaxHeight()
            .selectable(selected = seleccionada, onClick = alTocar, role = Role.Tab)
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(fondoIndicador),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(pestana.icono),
                contentDescription = null,
                tint = colorIcono,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(pestana.etiqueta),
            color = colorTexto,
            fontSize = 12.sp,
            fontWeight = pesoTexto,
            maxLines = 1
        )
    }
}
