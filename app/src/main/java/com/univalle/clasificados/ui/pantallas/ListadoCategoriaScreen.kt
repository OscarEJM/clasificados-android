package com.univalle.clasificados.ui.pantallas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.ui.componentes.BarraSuperior
import com.univalle.clasificados.viewmodel.ListadoCategoriaViewModel

/**
 * Marcador del listado de una categoría (pantalla 05), acordado con Oscar para HU-06: muestra
 * la flecha atrás y el nombre de la categoría tocada. HU-07 lo reemplaza por el listado real (RF-12).
 *
 * @param alVolver flecha atrás: regresa a la pantalla anterior (03 o 04).
 */
@Composable
fun ListadoCategoriaScreen(
    alVolver: () -> Unit,
    viewModel: ListadoCategoriaViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val nombre = estado.categoria?.nombre.orEmpty()

    Column(modifier = Modifier.fillMaxSize()) {
        BarraSuperior(titulo = nombre, alVolver = alVolver)
        PantallaMarcador(titulo = nombre, modifier = Modifier.weight(1f))
    }
}
