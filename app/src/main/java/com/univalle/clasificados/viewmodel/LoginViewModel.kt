package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import com.univalle.clasificados.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel de la pantalla 01 · Inicio de sesión. Por ahora solo expone el título del marcador;
 * en HU-05 · Iniciar sesión se agregan los campos, las validaciones y el inicio de sesión.
 */
class LoginViewModel : ViewModel() {
    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(MarcadorUiState(titulo = R.string.inicio_sesion_titulo))

    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<MarcadorUiState> = _estado.asStateFlow()
}
