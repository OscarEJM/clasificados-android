package com.univalle.clasificados.ui.componentes

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.navegacion.Rutas
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/**
 * Datos de una pestaña de la barra inferior.
 * @property ruta ruta de navegación que abre la pestaña.
 * @property etiqueta texto visible (recurso de strings.xml).
 * @property icono ícono vectorial exportado de Figma.
 */
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
 * Barra inferior según el componente «Barra inferior» de Figma (pantalla 03).
 * Usa ShortNavigationBar de Material 3 (64 dp de alto, indicador de 56 × 32 dp) con borde superior.
 * «Publicar» es un elemento propio porque en Figma su píldora verde se ve siempre.
 */
@Composable
fun BarraInferior(
    rutaActual: String?,
    alSeleccionar: (String) -> Unit
) {
    val colorBorde = ClasificadosTheme.colores.borde
    ShortNavigationBar(
        modifier = Modifier.drawWithContent {
            drawContent()
            drawLine(
                color = colorBorde,
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                strokeWidth = 1.dp.toPx()
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        pestanas.forEach { pestana ->
            val seleccionada = rutaActual == pestana.ruta
            if (pestana.ruta == Rutas.PUBLICAR) {
                ElementoPublicar(
                    pestana = pestana,
                    seleccionada = seleccionada,
                    alTocar = { alSeleccionar(pestana.ruta) }
                )
            } else {
                ShortNavigationBarItem(
                    selected = seleccionada,
                    onClick = { alSeleccionar(pestana.ruta) },
                    icon = {
                        Icon(
                            painter = painterResource(pestana.icono),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(pestana.etiqueta),
                            fontSize = 12.sp,
                            fontWeight = if (seleccionada) FontWeight.SemiBold else FontWeight.Medium,
                            maxLines = 1
                        )
                    },
                    colors = ShortNavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        selectedIndicatorColor = ClasificadosTheme.colores.primarioSuave,
                        unselectedIconColor = ClasificadosTheme.colores.textoSecundario,
                        unselectedTextColor = ClasificadosTheme.colores.textoSecundario
                    )
                )
            }
        }
    }
}

/** Pestaña «Publicar»: píldora verde de 56 × 32 dp con «+» blanco y texto en color de texto (Figma). */
@Composable
private fun ElementoPublicar(
    pestana: Pestana,
    seleccionada: Boolean,
    alTocar: () -> Unit
) {
    Column(
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .selectable(selected = seleccionada, onClick = alTocar, role = Role.Tab)
            .padding(top = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(ClasificadosTheme.colores.publicar),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(pestana.icono),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(pestana.etiqueta),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}
