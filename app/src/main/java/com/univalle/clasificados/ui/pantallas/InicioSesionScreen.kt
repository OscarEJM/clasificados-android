package com.univalle.clasificados.ui.pantallas

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.viewmodel.LoginViewModel

/**
 * Pantalla 01 · Inicio de sesión. Por ahora es un marcador con su título, al que llega la app
 * cuando no hay sesión (HU-03). En HU-05 · Iniciar sesión se reemplaza por la pantalla de Figma.
 * Lee el estado de [LoginViewModel] con collectAsStateWithLifecycle(), siguiendo MVVM.
 */
@Composable
fun InicioSesionScreen(viewModel: LoginViewModel = viewModel()) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    PantallaMarcador(titulo = stringResource(estado.titulo))
}
