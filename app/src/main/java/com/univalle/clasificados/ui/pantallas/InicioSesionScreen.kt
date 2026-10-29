package com.univalle.clasificados.ui.pantallas

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.componentes.EnlaceInferior
import com.univalle.clasificados.viewmodel.LoginViewModel

/**
 * Pantalla 01 · Inicio de sesión. Por ahora es un marcador con su título, al que llega la app
 * cuando no hay sesión (HU-03), y el enlace «¿No tienes cuenta? Regístrate» de la 01 de Figma
 * para llegar a Registro (HU-04). En HU-05 · Iniciar sesión se construye la pantalla completa.
 * Lee el estado de [LoginViewModel] con collectAsStateWithLifecycle(), siguiendo MVVM.
 *
 * @param alRegistrarse abre la pantalla 02 · Registro.
 */
@Composable
fun InicioSesionScreen(
    alRegistrarse: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize()) {
        PantallaMarcador(titulo = stringResource(estado.titulo))
        EnlaceInferior(
            pregunta = stringResource(R.string.inicio_sesion_sin_cuenta),
            accion = stringResource(R.string.inicio_sesion_registrate),
            alTocar = alRegistrarse,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}
