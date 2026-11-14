package com.univalle.clasificados.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.navegacion.Rutas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Lo que muestra el listado de una categoría (pantalla 05, HU-07).
 */
sealed interface CargaAnuncios {
    /** Se están pidiendo los anuncios a Firestore: circulito azul en el centro. */
    data object Cargando : CargaAnuncios

    /** Llegaron anuncios: cuadrícula con «N anuncios encontrados» (RF-12). */
    data class ConResultados(val anuncios: List<Anuncio>) : CargaAnuncios

    /** La categoría no tiene anuncios: estado de la pantalla 05b (RF-15). */
    data object Vacio : CargaAnuncios

    /** No se pudieron traer: mensaje de RN-18 centrado (acordado con Oscar hasta HU-21). */
    data object Error : CargaAnuncios
}

/**
 * Estado de la pantalla 05 · Anuncios de categoría.
 * @property categoria la categoría tocada; `null` si el id que llegó no existe.
 * @property textoBusqueda lo escrito en el buscador (la búsqueda dentro de la categoría es de HU-15).
 * @property carga si se está cargando, si hay anuncios, si está vacía o si hubo error.
 */
data class ListadoCategoriaUiState(
    val categoria: Categoria? = null,
    val textoBusqueda: String = "",
    val carga: CargaAnuncios = CargaAnuncios.Cargando
)

/**
 * ViewModel del listado de una categoría (HU-07 · Ver los anuncios de una categoría, RF-12, RF-13,
 * RF-15, RNF-08). Lee la categoría tocada del argumento de la navegación y pide sus anuncios a
 * [AnunciosRepository], del más reciente al más antiguo.
 *
 * `@JvmOverloads` crea también el constructor que solo recibe [SavedStateHandle], que es el que
 * usa Android para crear el ViewModel desde la navegación.
 */
class ListadoCategoriaViewModel @JvmOverloads constructor(
    estadoGuardado: SavedStateHandle,
    private val repositorio: AnunciosRepository = AnunciosRepository()
) : ViewModel() {

    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(
        ListadoCategoriaUiState(Categoria.desdeId(estadoGuardado.get<String>(Rutas.ARG_CATEGORIA)))
    )

    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<ListadoCategoriaUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    /** Pide los anuncios de la categoría. Cualquier fallo se muestra como falta de conexión. */
    fun cargar() {
        val categoria = _estado.value.categoria ?: run {
            _estado.update { it.copy(carga = CargaAnuncios.Vacio) }
            return
        }
        _estado.update { it.copy(carga = CargaAnuncios.Cargando) }
        viewModelScope.launch {
            val carga = try {
                val anuncios = repositorio.anunciosDeCategoria(categoria.id)
                if (anuncios.isEmpty()) CargaAnuncios.Vacio else CargaAnuncios.ConResultados(anuncios)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // En el registro queda la causa real (por ejemplo, el enlace para crear el índice).
                Log.e(ETIQUETA, "No se pudieron cargar los anuncios de ${categoria.id}", e)
                CargaAnuncios.Error
            }
            _estado.update { it.copy(carga = carga) }
        }
    }

    /** El usuario escribió en el buscador: se guarda el texto. Todavía no busca (HU-15). */
    fun alCambiarBusqueda(valor: String) = _estado.update { it.copy(textoBusqueda = valor) }

    private companion object {
        const val ETIQUETA = "ListadoCategoria"
    }
}
