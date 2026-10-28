package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import com.univalle.clasificados.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel de la pantalla Inicio (pantalla 03 de Figma).
 * Expone el estado de la pantalla en un [StateFlow]; por ahora solo el título del marcador.
 * En HU-06 · Explorar categorías se agregan el buscador y las categorías.
 */
class InicioViewModel : ViewModel() {
    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(MarcadorUiState(titulo = R.string.pestana_inicio))
    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<MarcadorUiState> = _estado.asStateFlow()
}
