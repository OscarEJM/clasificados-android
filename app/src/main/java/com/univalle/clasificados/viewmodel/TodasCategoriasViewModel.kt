package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import com.univalle.clasificados.model.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado de la pantalla 04 · Todas las categorías.
 * @property categorias las 13 categorías en el orden de la guía (sección 1.4).
 */
data class TodasCategoriasUiState(
    val categorias: List<Categoria> = Categoria.entries
)

/**
 * ViewModel de la pantalla 04 · Todas las categorías (HU-06 · Explorar categorías, RF-11).
 * Expone el estado en un [StateFlow] que la pantalla lee con collectAsStateWithLifecycle().
 */
class TodasCategoriasViewModel : ViewModel() {
    /** Estado de solo lectura: la lista fija no cambia mientras se ve la pantalla. */
    val estado: StateFlow<TodasCategoriasUiState> = MutableStateFlow(TodasCategoriasUiState()).asStateFlow()
}
