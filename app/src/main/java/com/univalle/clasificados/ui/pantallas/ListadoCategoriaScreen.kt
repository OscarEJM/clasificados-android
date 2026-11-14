package com.univalle.clasificados.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.componentes.AccionBarra
import com.univalle.clasificados.ui.componentes.BarraSuperior
import com.univalle.clasificados.ui.componentes.Buscador
import com.univalle.clasificados.ui.componentes.EspacioBotonPublicar
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
 * tarjeta todavía no hace nada: el detalle es de HU-08.
 *
 * Otros estados: circulito azul mientras carga, la pantalla 05b «No se encontraron anuncios»
 * si no hay anuncios, y el mensaje de RN-18 centrado si no se pudieron traer (hasta HU-21).
 *
 * @param alVolver flecha atrás: regresa a la pantalla anterior (03 o 04).
 */
@Composable
fun ListadoCategoriaScreen(
    alVolver: () -> Unit,
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
            CargaAnuncios.Cargando -> Cargando(Modifier.weight(1f))
            CargaAnuncios.Vacio -> SinResultados(Modifier.weight(1f))
            CargaAnuncios.Error -> SinConexion(Modifier.weight(1f))
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
                            alTocar = {}, // El detalle del anuncio es de HU-08.
                            modifier = Modifier.testTag(TARJETA_ANUNCIO)
                        )
                    }
                }
            }
        }
    }
}

/** Circulito azul en el centro mientras llegan los anuncios (aprobado por Oscar). */
@Composable
private fun Cargando(modifier: Modifier) {
    val descripcion = stringResource(R.string.listado_cargando)
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics { contentDescription = descripcion }
        )
    }
}

/**
 * Estado vacío de la pantalla 05b (RF-15): círculo gris de 80 dp con la lupa de 36 dp y
 * «No se encontraron anuncios» en 16 sp seminegrita, centrados en el espacio libre.
 */
@Composable
private fun SinResultados(modifier: Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_buscar),
                contentDescription = null,
                tint = ClasificadosTheme.colores.textoSecundario,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = stringResource(R.string.listado_vacio),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Mensaje sin conexión de RN-18, centrado (acordado con Oscar hasta HU-21, pantalla 05c). */
@Composable
private fun SinConexion(modifier: Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.sin_conexion),
            color = ClasificadosTheme.colores.textoSecundario,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
