package com.univalle.clasificados.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.R
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.componentes.BarraSuperior
import com.univalle.clasificados.ui.componentes.EspacioBotonPublicar
import com.univalle.clasificados.ui.componentes.TarjetaCategoria
import com.univalle.clasificados.viewmodel.TodasCategoriasViewModel

/** Etiqueta de la cuadrícula para las pruebas (permite desplazarla hasta una categoría). */
const val CUADRICULA_TODAS_CATEGORIAS = "cuadricula_todas_categorias"

/**
 * Pantalla 04 · Todas las categorías de Figma (HU-06 · Explorar categorías: RF-11 y RNF-11).
 * Muestra las 13 categorías en tarjetas de dos por renglón, con ícono y nombre, en una cuadrícula
 * que se desplaza. Al final deja [EspacioBotonPublicar] libre.
 *
 * @param alAbrirCategoria abre el listado de la categoría tocada.
 * @param alVolver flecha atrás: regresa a Inicio.
 */
@Composable
fun TodasCategoriasScreen(
    alAbrirCategoria: (Categoria) -> Unit,
    alVolver: () -> Unit,
    viewModel: TodasCategoriasViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(titulo = stringResource(R.string.todas_categorias_titulo), alVolver = alVolver)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize().testTag(CUADRICULA_TODAS_CATEGORIAS),
            // Medidas de Figma (04): 16 dp a los lados y 8 dp arriba.
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = EspacioBotonPublicar),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(estado.categorias, key = { it.id }) { categoria ->
                TarjetaCategoria(categoria = categoria, alTocar = { alAbrirCategoria(categoria) })
            }
        }
    }
}
