package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.clasificados.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado de la sesión que usa la navegación para decidir a qué pantalla ir (HU-03). */
enum class EstadoSesion {
    /** Se está mostrando la bienvenida mientras se revisa la sesión. */
    VERIFICANDO,

    /** Hay un usuario con sesión: puede entrar a las pantallas privadas. */
    CON_SESION,

    /** No hay sesión: solo se permiten Bienvenida, Inicio de sesión y Registro (RF-09). */
    SIN_SESION
}

/**
 * ViewModel de la sesión, compartido por toda la app (RF-01, RF-02 y RF-09).
 * El estado sigue en [EstadoSesion.VERIFICANDO] hasta que se cumplan dos cosas: la pantalla
 * 00 · Bienvenida lleva al menos [TIEMPO_MINIMO_BIENVENIDA_MS] en pantalla y Firebase ya
 * respondió si hay sesión. Después sigue escuchando la sesión, para que la navegación saque al
 * usuario si la sesión se cierra.
 */
class SesionViewModel(
    authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    /** `true` cuando la bienvenida ya cumplió su tiempo mínimo en pantalla. */
    private val bienvenidaCumplida = MutableStateFlow(false)

    /** `null` mientras Firebase no responde; luego `true` o `false` según haya sesión. */
    private val haySesion = MutableStateFlow<Boolean?>(null)

    /** Evita empezar el conteo de la bienvenida dos veces. */
    private var conteoIniciado = false

    /** Estado de solo lectura que la navegación observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<EstadoSesion> = combine(bienvenidaCumplida, haySesion) { cumplida, sesion ->
        when {
            !cumplida || sesion == null -> EstadoSesion.VERIFICANDO
            sesion -> EstadoSesion.CON_SESION
            else -> EstadoSesion.SIN_SESION
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, EstadoSesion.VERIFICANDO)

    init {
        viewModelScope.launch {
            authRepository.sesionActiva.collect { haySesion.value = it }
        }
    }

    /**
     * Empieza a contar el tiempo mínimo de la bienvenida. Lo llama la navegación cuando la
     * pantalla 00 ya se ve, así el arranque de Android no se come ese tiempo.
     */
    fun contarTiempoBienvenida() {
        if (conteoIniciado) return
        conteoIniciado = true
        viewModelScope.launch {
            delay(TIEMPO_MINIMO_BIENVENIDA_MS)
            bienvenidaCumplida.value = true
        }
    }

    companion object {
        /** Tiempo mínimo que se ve la bienvenida, acordado con Oscar: 1 segundo. */
        const val TIEMPO_MINIMO_BIENVENIDA_MS = 1_000L
    }
}
