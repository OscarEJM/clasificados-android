package com.univalle.clasificados.ui.componentes

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.ShortNavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.navegacion.Rutas
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/**
 * Espacio libre que deja **al final** todo contenido con scroll para que el botón «Publicar»
 * no tape el último elemento (sección 1.9 de la guía). El botón con su texto ocupa unos 95 dp
 * sobre la barra. Solo se usa al final de las listas: arriba la pantalla usa todo el alto.
 */
val EspacioBotonPublicar: Dp = 96.dp

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

// Orden fijo de la guía (sección 1.9): Inicio, Mis anuncios, Ajustes. «Publicar» es el botón flotante.
private val pestanas = listOf(
    Pestana(Rutas.INICIO, R.string.pestana_inicio, R.drawable.ic_inicio),
    Pestana(Rutas.MIS_ANUNCIOS, R.string.pestana_mis_anuncios, R.drawable.ic_mis_anuncios),
    Pestana(Rutas.AJUSTES, R.string.pestana_ajustes, R.drawable.ic_ajustes)
)

/**
 * Barra inferior según el componente «Barra inferior» de Figma (pantalla 03): tres pestañas del
 * mismo ancho con borde superior. Usa ShortNavigationBar de Material 3 (64 dp de alto, indicador
 * de 56 × 32 dp). El botón «Publicar» va aparte, en [BotonPublicar].
 *
 * @param rutaActual ruta de la pantalla visible, para marcar la pestaña activa.
 * @param alSeleccionar se llama con la ruta de la pestaña tocada.
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

/**
 * Botón circular «Publicar» de Figma: círculo verde de 60 dp con aro de 5 dp color superficie,
 * sombra verde suave, ícono «+» blanco de 28 dp y el texto «Publicar» debajo (12 sp, negrita).
 * Flota sobre la barra, centrado en la pantalla (encima de «Mis anuncios»).
 *
 * @param alTocar abre la pantalla Publicar.
 */
@Composable
fun BotonPublicar(alTocar: () -> Unit) {
    val verde = ClasificadosTheme.colores.publicar
    val etiqueta = stringResource(R.string.pestana_publicar)
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClickLabel = etiqueta, role = Role.Button, onClick = alTocar),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Aro de 5 dp: círculo de 70 dp color superficie con el círculo verde de 60 dp encima.
        Box(
            modifier = Modifier
                .size(70.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = CircleShape,
                        ambientColor = verde.copy(alpha = 0.35f),
                        spotColor = verde.copy(alpha = 0.35f)
                    )
                    .background(verde, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_mas),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}
