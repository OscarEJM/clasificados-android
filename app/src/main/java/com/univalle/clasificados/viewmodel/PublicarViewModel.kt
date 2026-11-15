package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.clasificados.data.repository.AuthRepository
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.util.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Borrador del anuncio mientras se llena el formulario de Publicar (HU-12). Vive solo en memoria:
 * en el teléfono no se guarda nada más que el tema (RN-17).
 *
 * @property categoria la categoría elegida en el paso 1; `null` si todavía no se eligió.
 * @property precio lo escrito en el campo de precio, solo dígitos (se convierte a número al guardar).
 */
data class BorradorAnuncio(
    val categoria: Categoria? = null,
    val titulo: String = "",
    val descripcion: String = "",
    val precio: String = ""
)

/**
 * Estado del formulario de cuatro pasos (RF-21).
 * @property paso paso actual, de 1 a 4. En HU-12 existen el 1 y el 2; el 3 es un marcador hasta HU-13.
 * @property errorCategoria mensaje del paso 1, o `null`.
 */
data class PublicarUiState(
    val paso: Int = 1,
    val borrador: BorradorAnuncio = BorradorAnuncio(),
    val errorCategoria: String? = null,
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorPrecio: String? = null
) {
    /** Contador vivo del título: «Mínimo 10 caracteres · N escritos» (sección 1.5). */
    val contadorTitulo: String
        get() = "Mínimo ${Validador.MINIMO_TITULO} caracteres · ${borrador.titulo.length} escritos"
}

/**
 * ViewModel del formulario de Publicar, compartido por los cuatro pasos (HU-12 · Publicar anuncio:
 * pasos de categoría y detalles; RF-21, RF-22, RF-23).
 *
 * Guarda en memoria el borrador del anuncio, así «Volver» no pierde datos. Lo crea la actividad
 * (no la pantalla), de modo que el borrador se conserva si el usuario sale de Publicar y vuelve,
 * mientras la app esté abierta (acordado con Oscar). Si cambia la cuenta con sesión, el borrador
 * se borra para que otra persona no vea lo que se estaba escribiendo.
 *
 * Los errores aparecen al tocar «Continuar» y el de cada campo se borra al escribir en él, igual
 * que en Registro (acordado con Oscar).
 */
class PublicarViewModel @JvmOverloads constructor(
    autenticacion: AuthRepository = AuthRepository()
) : ViewModel() {

    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(PublicarUiState())

    /** Estado de solo lectura que las pantallas observan con collectAsStateWithLifecycle(). */
    val estado: StateFlow<PublicarUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            // El primer valor es la cuenta actual; a partir del segundo, la cuenta cambió.
            autenticacion.uidActual.distinctUntilChanged().drop(1).collect { reiniciar() }
        }
    }

    /** Paso 1: el usuario tocó una categoría; queda resaltada y se borra el error. */
    fun elegirCategoria(categoria: Categoria) =
        _estado.update { it.copy(borrador = it.borrador.copy(categoria = categoria), errorCategoria = null) }

    /** Paso 2: cambia el título. El contador se actualiza en vivo. */
    fun alCambiarTitulo(valor: String) =
        _estado.update { it.copy(borrador = it.borrador.copy(titulo = valor), errorTitulo = null) }

    /** Paso 2: cambia la descripción. */
    fun alCambiarDescripcion(valor: String) =
        _estado.update { it.copy(borrador = it.borrador.copy(descripcion = valor), errorDescripcion = null) }

    /** Paso 2: cambia el precio. Solo se aceptan dígitos (RF-23), hasta 10. */
    fun alCambiarPrecio(valor: String) =
        _estado.update {
            it.copy(borrador = it.borrador.copy(precio = Validador.limpiarPrecio(valor)), errorPrecio = null)
        }

    /** «Continuar»: valida el paso actual y solo avanza si es válido (RF-21). */
    fun continuar() = _estado.update { actual ->
        val borrador = actual.borrador
        when (actual.paso) {
            1 -> {
                val error = Validador.validarCategoria(borrador.categoria?.id)
                if (error == null) actual.copy(paso = 2) else actual.copy(errorCategoria = error)
            }
            2 -> {
                val conErrores = actual.copy(
                    errorTitulo = Validador.validarTitulo(borrador.titulo),
                    errorDescripcion = Validador.validarDescripcion(borrador.descripcion),
                    errorPrecio = Validador.validarPrecio(borrador.precio)
                )
                val valido = conErrores.errorTitulo == null &&
                    conErrores.errorDescripcion == null && conErrores.errorPrecio == null
                if (valido) conErrores.copy(paso = 3) else conErrores
            }
            else -> actual // Los pasos 3 y 4 llegan en HU-13 y HU-14.
        }
    }

    /**
     * «Volver»: regresa al paso anterior conservando los datos (RF-21).
     * @return `false` si ya estaba en el paso 1, para que la pantalla cierre Publicar.
     */
    fun volver(): Boolean {
        if (_estado.value.paso <= 1) return false
        _estado.update { it.copy(paso = it.paso - 1) }
        return true
    }

    /** Borra el borrador y vuelve al paso 1. */
    fun reiniciar() {
        _estado.value = PublicarUiState()
    }
}
