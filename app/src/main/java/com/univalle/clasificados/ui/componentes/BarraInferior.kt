package com.univalle.clasificados.ui.componentes

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.navegacion.Rutas
import com.univalle.clasificados.ui.theme.ClasificadosTheme

private data class Pestana(
    val ruta: String,
    @StringRes val etiqueta: Int,
    val icono: ImageVector
)

// Orden fijo de la guía (sección 1.9): Inicio, Publicar, Mis anuncios, Ajustes.
private val pestanas = listOf(
    Pestana(Rutas.INICIO, R.string.pestana_inicio, Icons.Filled.Home),
    Pestana(Rutas.PUBLICAR, R.string.pestana_publicar, Icons.Filled.AddCircle),
    Pestana(Rutas.MIS_ANUNCIOS, R.string.pestana_mis_anuncios, Icons.AutoMirrored.Filled.List),
    Pestana(Rutas.AJUSTES, R.string.pestana_ajustes, Icons.Filled.Settings)
)

@Composable
fun BarraInferior(
    rutaActual: String?,
    alSeleccionar: (String) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        pestanas.forEach { pestana ->
            val esPublicar = pestana.ruta == Rutas.PUBLICAR
            val colores = if (esPublicar) {
                // «Publicar» siempre se ve en verde.
                val verde = ClasificadosTheme.colores.publicar
                NavigationBarItemDefaults.colors(
                    selectedIconColor = verde,
                    selectedTextColor = verde,
                    unselectedIconColor = verde,
                    unselectedTextColor = verde,
                    indicatorColor = ClasificadosTheme.colores.primarioSuave
                )
            } else {
                NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = ClasificadosTheme.colores.textoSecundario,
                    unselectedTextColor = ClasificadosTheme.colores.textoSecundario,
                    indicatorColor = ClasificadosTheme.colores.primarioSuave
                )
            }
            NavigationBarItem(
                selected = rutaActual == pestana.ruta,
                onClick = { alSeleccionar(pestana.ruta) },
                icon = { Icon(pestana.icono, contentDescription = null) },
                label = { Text(stringResource(pestana.etiqueta)) },
                colors = colores
            )
        }
    }
}
