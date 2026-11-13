package com.univalle.clasificados.ui.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.R
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.componentes.Buscador
import com.univalle.clasificados.ui.componentes.EspacioBotonPublicar
import com.univalle.clasificados.ui.componentes.TarjetaCategoria
import com.univalle.clasificados.ui.componentes.TarjetaMasCategorias
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.viewmodel.InicioViewModel

/**
 * Pantalla 03 · Inicio de Figma (HU-06 · Explorar categorías: RF-10 y RNF-11).
 * Muestra «Clasificados» con su lema, el buscador, el título «Categorías» con «Ver todas» y una
 * cuadrícula de dos por renglón con las 5 categorías principales y «Más categorías».
 * Las tarjetas solo tienen ícono y nombre. Al final deja [EspacioBotonPublicar] libre.
 *
 * @param alAbrirCategoria abre el listado de la categoría tocada.
 * @param alVerTodas «Más categorías» y «Ver todas»: abren la pantalla 04 (RF-11).
 */
@Composable
fun InicioScreen(
    alAbrirCategoria: (Categoria) -> Unit,
    alVerTodas: () -> Unit,
    viewModel: InicioViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val colores = MaterialTheme.colorScheme

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = EspacioBotonPublicar),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Encabezado, buscador y título de sección ocupan las dos columnas, separados 20 dp (Figma).
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Column(
                    modifier = Modifier.padding(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = colores.primary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.bienvenida_lema),
                        color = ClasificadosTheme.colores.textoSecundario,
                        fontSize = 14.sp
                    )
                }
                Buscador(
                    valor = estado.textoBusqueda,
                    alCambiar = viewModel::alCambiarBusqueda,
                    ejemplo = stringResource(R.string.inicio_buscador)
                )
                // La fila mide 48 dp por la zona táctil de «Ver todas» (12 dp sobre y bajo el texto):
                // con 8 dp arriba el texto queda a 20 dp del buscador, como en Figma.
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.inicio_categorias),
                        color = colores.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    // Zona táctil de 48 dp (RNF-12) alrededor del texto «Ver todas».
                    Box(
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clickable(role = Role.Button, onClick = alVerTodas),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = stringResource(R.string.inicio_ver_todas),
                            color = colores.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        items(estado.categorias, key = { it.id }) { categoria ->
            TarjetaCategoria(categoria = categoria, alTocar = { alAbrirCategoria(categoria) })
        }
        item(key = "mas_categorias") {
            TarjetaMasCategorias(
                nombre = stringResource(R.string.categorias_mas),
                alTocar = alVerTodas
            )
        }
    }
}
