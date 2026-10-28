package com.univalle.clasificados.ui.pantallas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.viewmodel.MisAnunciosViewModel

@Composable
fun MisAnunciosScreen(viewModel: MisAnunciosViewModel = viewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    PantallaMarcador(titulo = stringResource(estado.titulo))
}
