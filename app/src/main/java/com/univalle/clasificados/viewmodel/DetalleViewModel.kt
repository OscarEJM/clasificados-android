package com.univalle.clasificados.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.ui.navegacion.Rutas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Lo que muestra la pantalla 07 · Detalle del anuncio (HU-08).
 */
sealed interface EstadoDetalle {
    /** Se está pidiendo el anuncio a Firestore: circulito azul en el centro. */
    data object Cargando : EstadoDetalle

    /** Llegó el anuncio: se muestran todos sus datos (RF-14). */
    data class Listo(val anuncio: Anuncio) : EstadoDetalle

    /** El anuncio ya no existe: «No se encontraron anuncios» (acordado con Oscar). */
    data object NoEncontrado : EstadoDetalle

    /** No se pudo traer: mensaje de RN-18 centrado (acordado con Oscar hasta HU-21). */
    data object Error : EstadoDetalle
}

/**
 * ViewModel de la pantalla 07 · Detalle del anuncio (HU-08 · Ver el detalle de un anuncio, RF-14).
 * Lee el id del anuncio que llegó en la navegación y lo pide a [AnunciosRepository].
 *
 * `@JvmOverloads` crea también el constructor que solo recibe [SavedStateHandle], que es el que
 * usa Android para crear el ViewModel desde la navegación.
 */
class DetalleViewModel @JvmOverloads constructor(
    estadoGuardado: SavedStateHandle,
    private val repositorio: AnunciosRepository = AnunciosRepository()
) : ViewModel() {

    /** Id del anuncio que se abrió. */
    private val anuncioId: String? = estadoGuardado.get<String>(Rutas.ARG_ANUNCIO)

    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow<EstadoDetalle>(EstadoDetalle.Cargando)

    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<EstadoDetalle> = _estado.asStateFlow()

    init {
        cargar()
    }

    /** Pide el anuncio. Cualquier fallo se muestra como falta de conexión. */
    private fun cargar() {
        val id = anuncioId ?: run {
            _estado.value = EstadoDetalle.NoEncontrado
            return
        }
        viewModelScope.launch {
            _estado.value = try {
                repositorio.anuncio(id)?.let { EstadoDetalle.Listo(it) } ?: EstadoDetalle.NoEncontrado
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(ETIQUETA, "No se pudo cargar el anuncio $id", e)
                EstadoDetalle.Error
            }
        }
    }

    private companion object {
        const val ETIQUETA = "DetalleAnuncio"
    }
}
