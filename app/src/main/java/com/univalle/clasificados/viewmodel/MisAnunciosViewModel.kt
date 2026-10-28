package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import com.univalle.clasificados.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel de la pestaña Mis anuncios (pantalla 13 de Figma).
 * Expone el estado de la pantalla en un [StateFlow]; por ahora solo el título del marcador.
 * En HU-18 · Mis anuncios se agrega la lista de anuncios del usuario.
 */
class MisAnunciosViewModel : ViewModel() {
    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(MarcadorUiState(titulo = R.string.pestana_mis_anuncios))

    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<MarcadorUiState> = _estado.asStateFlow()
}
