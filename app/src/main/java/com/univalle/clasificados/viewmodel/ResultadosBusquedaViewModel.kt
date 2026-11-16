package com.univalle.clasificados.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.ui.navegacion.Rutas
import com.univalle.clasificados.util.Busqueda
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla 03b · Resultados de búsqueda.
 * @property texto lo escrito en el buscador de la 03b; empieza con lo buscado en Inicio.
 * @property carga circulito, resultados, «No se encontraron anuncios» (RF-15) o sin conexión.
 */
data class ResultadosBusquedaUiState(
    val texto: String = "",
    val carga: CargaAnuncios = CargaAnuncios.Cargando
)

/**
 * ViewModel de la pantalla 03b (HU-15 · Buscar anuncios por título; RF-16, RF-15, RN-14).
 * Lee lo buscado del argumento de la navegación, trae todos los anuncios (más recientes primero,
 * RN-13) y se queda solo con los que tienen el texto en el título, sin distinguir mayúsculas.
 *
 * `@JvmOverloads` crea también el constructor que solo recibe [SavedStateHandle], que es el que
 * usa Android para crear el ViewModel desde la navegación.
 */
class ResultadosBusquedaViewModel @JvmOverloads constructor(
    estadoGuardado: SavedStateHandle,
    private val repositorio: AnunciosRepository = AnunciosRepository()
) : ViewModel() {

    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(
        ResultadosBusquedaUiState(texto = estadoGuardado.get<String>(Rutas.ARG_TEXTO).orEmpty())
    )

    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<ResultadosBusquedaUiState> = _estado.asStateFlow()

    init {
        buscar()
    }

    /** El usuario cambió el texto del buscador de la 03b; se busca de nuevo al tocar la lupa. */
    fun alCambiarTexto(valor: String) = _estado.update { it.copy(texto = valor) }

    /**
     * Trae los anuncios y los filtra por título. Cualquier fallo se muestra como falta de conexión
     * (RN-18). Con el buscador vacío o solo espacios no hace nada (aprobado por Oscar).
     */
    fun buscar() {
        val texto = _estado.value.texto
        if (Busqueda.normalizar(texto).isEmpty()) return
        _estado.update { it.copy(carga = CargaAnuncios.Cargando) }
        viewModelScope.launch {
            val carga = try {
                val encontrados = Busqueda.filtrar(repositorio.todosLosAnuncios(), texto)
                if (encontrados.isEmpty()) CargaAnuncios.Vacio else CargaAnuncios.ConResultados(encontrados)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(ETIQUETA, "No se pudo buscar", e)
                CargaAnuncios.Error
            }
            _estado.update { it.copy(carga = carga) }
        }
    }

    private companion object {
        const val ETIQUETA = "ResultadosBusqueda"
    }
}
