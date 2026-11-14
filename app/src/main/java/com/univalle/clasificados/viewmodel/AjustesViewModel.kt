package com.univalle.clasificados.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.clasificados.data.repository.AuthRepository
import com.univalle.clasificados.util.Iniciales
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/**
 * Lo que muestra la tarjeta de perfil de la pantalla 16 · Ajustes (HU-09).
 */
sealed interface EstadoPerfil {
    /** Se está pidiendo el nombre a Firestore: circulito azul en lugar de la tarjeta. */
    data object Cargando : EstadoPerfil

    /**
     * Datos de la cuenta con sesión (RF-33).
     * @property nombre nombre del perfil; vacío si la cuenta no tiene perfil guardado.
     * @property correo correo de la sesión.
     * @property iniciales letras del avatar (ver [Iniciales]).
     */
    data class Listo(val nombre: String, val correo: String, val iniciales: String) : EstadoPerfil

    /** No se pudo traer el nombre: mensaje de RN-18 en lugar de la tarjeta (acordado con Oscar). */
    data object Error : EstadoPerfil
}

/**
 * ViewModel de la pantalla 16 · Ajustes (HU-09 · Ver mi perfil en Ajustes, RF-33).
 *
 * Escucha qué cuenta tiene la sesión y, cada vez que cambia, lee de nuevo su perfil: el correo
 * sale de Firebase Authentication y el nombre de `usuarios/{uid}` en Firestore. Así, al cerrar
 * sesión y entrar con otra cuenta, Ajustes muestra los datos de la nueva (criterio 2).
 * También cierra la sesión (HU-10). El modo oscuro se conecta en HU-11.
 */
class AjustesViewModel @JvmOverloads constructor(
    private val repositorio: AuthRepository = AuthRepository()
) : ViewModel() {

    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _perfil = MutableStateFlow<EstadoPerfil>(EstadoPerfil.Cargando)

    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val perfil: StateFlow<EstadoPerfil> = _perfil.asStateFlow()

    init {
        viewModelScope.launch {
            // collectLatest: si la cuenta cambia mientras se lee la anterior, esa lectura se descarta.
            repositorio.uidActual.distinctUntilChanged().collectLatest { uid ->
                if (uid == null) {
                    // Sin sesión: la navegación lleva a Inicio de sesión (RF-09).
                    _perfil.value = EstadoPerfil.Cargando
                } else {
                    cargar(uid)
                }
            }
        }
    }

    /**
     * Cierra la sesión (HU-10 · Cerrar sesión, RF-08). No navega: al quedar sin sesión, la regla
     * de HU-03 lleva a Inicio de sesión y borra la pila, así «atrás» no vuelve a las pantallas
     * internas.
     */
    fun cerrarSesion() = repositorio.cerrarSesion()

    /** Lee el perfil de la cuenta [uid]. Cualquier fallo se muestra como falta de conexión. */
    private suspend fun cargar(uid: String) {
        _perfil.value = EstadoPerfil.Cargando
        _perfil.value = try {
            val correo = repositorio.correoActual()
            val nombre = repositorio.perfil(uid)?.nombre.orEmpty()
            EstadoPerfil.Listo(nombre = nombre, correo = correo, iniciales = Iniciales.de(nombre, correo))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(ETIQUETA, "No se pudo cargar el perfil", e)
            EstadoPerfil.Error
        }
    }

    private companion object {
        const val ETIQUETA = "Ajustes"
    }
}
