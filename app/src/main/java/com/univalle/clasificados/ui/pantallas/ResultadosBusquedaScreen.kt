package com.univalle.clasificados.ui.pantallas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.R
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.viewmodel.ResultadosBusquedaViewModel

/**
 * Pantalla 03b · Resultados de búsqueda (HU-15 · Buscar anuncios por título; RF-16, RF-15).
 *
 * Se abre al tocar la lupa del teclado en el buscador de Inicio. En Figma es igual a la pantalla
 * 05 con el título «Resultados de búsqueda», así que usa el mismo [ListadoAnuncios]: el buscador
 * con lo buscado (se puede cambiar y buscar otra vez con la lupa), «N anuncios encontrados» y los
 * anuncios de todas las categorías cuyo título coincide, en dos por renglón y del más reciente al
 * más antiguo. Sin coincidencias se ve la 05b «No se encontraron anuncios».
 *
 * @param alVolver flecha atrás: regresa a Inicio.
 * @param alAbrirAnuncio abre la pantalla 07 · Detalle del anuncio tocado.
 */
@Composable
fun ResultadosBusquedaScreen(
    alVolver: () -> Unit,
    alAbrirAnuncio: (Anuncio) -> Unit,
    viewModel: ResultadosBusquedaViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    ListadoAnuncios(
        titulo = stringResource(R.string.busqueda_titulo),
        textoBusqueda = estado.texto,
        alCambiarBusqueda = viewModel::alCambiarTexto,
        carga = estado.carga,
        alVolver = alVolver,
        alAbrirAnuncio = alAbrirAnuncio,
        alBuscar = viewModel::buscar
    )
}
