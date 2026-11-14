package com.univalle.clasificados.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.R
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.ui.componentes.AccionBarra
import com.univalle.clasificados.ui.componentes.BarraSuperior
import com.univalle.clasificados.ui.componentes.Buscador
import com.univalle.clasificados.ui.componentes.EspacioBotonPublicar
import com.univalle.clasificados.ui.componentes.EstadoCargando
import com.univalle.clasificados.ui.componentes.EstadoSinConexion
import com.univalle.clasificados.ui.componentes.EstadoSinResultados
import com.univalle.clasificados.ui.componentes.TarjetaAnuncio
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.viewmodel.CargaAnuncios
import com.univalle.clasificados.viewmodel.ListadoCategoriaViewModel

/** Etiqueta de la cuadrícula de anuncios, para las pruebas automáticas (HU-07). */
const val CUADRICULA_ANUNCIOS = "cuadricula_anuncios"

/** Etiqueta de cada tarjeta de anuncio, para contar las tarjetas en las pruebas (HU-07). */
const val TARJETA_ANUNCIO = "tarjeta_anuncio"

/**
 * Pantalla 05 · Anuncios de categoría (HU-07 · Ver los anuncios de una categoría, RF-12, RF-15).
 *
 * Arriba: flecha atrás, nombre de la categoría y el ícono de filtros (se ve, pero los filtros
 * llegan en HU-17). Debajo: el buscador (se escribe, pero busca desde HU-15), «N anuncios
 * encontrados» y los anuncios en dos por renglón, del más reciente al más antiguo. Tocar una
 * tarjeta abre su detalle (HU-08).
 *
 * Otros estados: circulito azul mientras carga, la pantalla 05b «No se encontraron anuncios»
 * si no hay anuncios, y el mensaje de RN-18 centrado si no se pudieron traer (hasta HU-21).
 *
 * @param alVolver flecha atrás: regresa a la pantalla anterior (03 o 04).
 * @param alAbrirAnuncio se llama con el anuncio tocado para abrir la pantalla 07 (HU-08).
 */
@Composable
fun ListadoCategoriaScreen(
    alVolver: () -> Unit,
    alAbrirAnuncio: (Anuncio) -> Unit,
    viewModel: ListadoCategoriaViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(
            titulo = estado.categoria?.nombre.orEmpty(),
            alVolver = alVolver,
            accion = AccionBarra(
                icono = R.drawable.ic_filtros,
                descripcion = stringResource(R.string.accion_filtros),
                alTocar = {} // Los filtros se conectan en HU-17 (acordado con Oscar).
            )
        )
        Buscador(
            valor = estado.textoBusqueda,
            alCambiar = viewModel::alCambiarBusqueda,
            ejemplo = stringResource(R.string.inicio_buscador),
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, end = 16.dp)
        )
        when (val carga = estado.carga) {
            CargaAnuncios.Cargando ->
                EstadoCargando(stringResource(R.string.listado_cargando), Modifier.weight(1f))
            CargaAnuncios.Vacio -> EstadoSinResultados(Modifier.weight(1f))
            CargaAnuncios.Error -> EstadoSinConexion(Modifier.weight(1f))
            is CargaAnuncios.ConResultados -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(
                        start = 16.dp, top = 16.dp, end = 16.dp, bottom = EspacioBotonPublicar
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f).testTag(CUADRICULA_ANUNCIOS)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = stringResource(R.string.listado_contador, carga.anuncios.size),
                            color = ClasificadosTheme.colores.textoSecundario,
                            fontSize = 13.sp,
                            // Con el espacio de 12 de la cuadrícula suma los 16 dp de Figma.
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                    items(carga.anuncios, key = { it.id }) { anuncio ->
                        TarjetaAnuncio(
                            anuncio = anuncio,
                            alTocar = { alAbrirAnuncio(anuncio) },
                            modifier = Modifier.testTag(TARJETA_ANUNCIO)
                        )
                    }
                }
            }
        }
    }
}
