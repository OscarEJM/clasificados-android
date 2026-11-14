package com.univalle.clasificados.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.univalle.clasificados.data.repository.PreferenciasRepository
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel de toda la app, a nivel de MainActivity (HU-11 · Cambiar entre tema claro y oscuro,
 * RF-31, RF-32). Expone el tema elegido como [StateFlow]; MainActivity se lo pasa a
 * ClasificadosTheme, y al cambiar Compose redibuja toda la app sin reiniciarla.
 *
 * `@JvmOverloads` crea también el constructor que solo recibe [Application], que es el que usa
 * Android para crear el ViewModel.
 */
class TemaViewModel @JvmOverloads constructor(
    aplicacion: Application,
    preferencias: PreferenciasRepository = PreferenciasRepository.obtener(aplicacion)
) : AndroidViewModel(aplicacion) {

    /** `true` si la app se debe ver en modo oscuro. Ya tiene el valor guardado desde el inicio. */
    val modoOscuro: StateFlow<Boolean> = preferencias.modoOscuro
}
