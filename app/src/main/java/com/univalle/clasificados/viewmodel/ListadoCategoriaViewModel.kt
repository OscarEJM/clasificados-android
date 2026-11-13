package com.univalle.clasificados.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.navegacion.Rutas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Estado del listado de una categoría (pantalla 05).
 * @property categoria la categoría tocada; `null` si el id que llegó no existe.
 */
data class ListadoCategoriaUiState(
    val categoria: Categoria? = null
)

/**
 * ViewModel del listado de una categoría. En HU-06 solo lee qué categoría se tocó, a partir del
 * argumento de la navegación, para mostrar el marcador. En HU-07 carga los anuncios (RF-12).
 */
class ListadoCategoriaViewModel(estadoGuardado: SavedStateHandle) : ViewModel() {
    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<ListadoCategoriaUiState> = MutableStateFlow(
        ListadoCategoriaUiState(Categoria.desdeId(estadoGuardado.get<String>(Rutas.ARG_CATEGORIA)))
    ).asStateFlow()
}
