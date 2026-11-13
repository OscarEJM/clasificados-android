package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import com.univalle.clasificados.model.Categoria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Estado de la pantalla 03 · Inicio.
 * @property textoBusqueda lo escrito en el buscador (la búsqueda se conecta en HU-15).
 * @property categorias las 5 categorías principales que se muestran antes de «Más categorías».
 */
data class InicioUiState(
    val textoBusqueda: String = "",
    val categorias: List<Categoria> = Categoria.PRINCIPALES
)

/**
 * ViewModel de la pantalla 03 · Inicio (HU-06 · Explorar categorías, RF-10).
 * Expone el estado en un [StateFlow] que la pantalla lee con collectAsStateWithLifecycle().
 */
class InicioViewModel : ViewModel() {
    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(InicioUiState())

    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<InicioUiState> = _estado.asStateFlow()

    /** El usuario escribió en el buscador: se guarda el texto. Todavía no busca (HU-15). */
    fun alCambiarBusqueda(valor: String) = _estado.update { it.copy(textoBusqueda = valor) }
}
