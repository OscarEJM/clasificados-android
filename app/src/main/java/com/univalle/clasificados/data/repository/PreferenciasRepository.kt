package com.univalle.clasificados.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Preferencia de tema claro/oscuro guardada en el teléfono con SharedPreferences (HU-11 · Cambiar
 * entre tema claro y oscuro, RF-32, RNF-06, RN-17). Es lo **único** que el código de la app guarda
 * en el dispositivo: un archivo propio ([ARCHIVO]) con una sola clave ([CLAVE_MODO_OSCURO]).
 * (La sesión iniciada la guarda Firebase por su cuenta, HU-03.)
 *
 * Hay una sola instancia para toda la app ([obtener]), así el tema que cambia Ajustes es el mismo
 * que dibuja MainActivity. El valor se lee al crear la instancia, antes de la primera pantalla,
 * para que no se vea un parpadeo del tema contrario.
 */
class PreferenciasRepository private constructor(private val preferencias: SharedPreferences) {

    /** Valor actual de la preferencia; por defecto `false` (tema claro, RN-17). */
    private val _modoOscuro = MutableStateFlow(preferencias.getBoolean(CLAVE_MODO_OSCURO, false))

    /** `true` si el usuario eligió el modo oscuro. Avisa cada vez que cambia. */
    val modoOscuro: StateFlow<Boolean> = _modoOscuro.asStateFlow()

    /** Guarda la preferencia en el teléfono y avisa a toda la app para que cambie de tema (RF-31). */
    fun cambiarModoOscuro(activo: Boolean) {
        preferencias.edit().putBoolean(CLAVE_MODO_OSCURO, activo).apply()
        _modoOscuro.value = activo
    }

    companion object {
        /** Nombre del archivo de SharedPreferences de la app. */
        const val ARCHIVO = "preferencias_clasificados"

        /** Clave de la preferencia de tema (sección 1.10 de la guía). */
        const val CLAVE_MODO_OSCURO = "modo_oscuro"

        @Volatile
        private var instancia: PreferenciasRepository? = null

        /**
         * Solo para las pruebas automáticas (criterio 3 de HU-11): olvida la instancia para que la
         * siguiente vuelva a leer el archivo, como pasa al instalar la app por primera vez.
         */
        @VisibleForTesting
        fun olvidarInstancia() {
            instancia = null
        }

        /** Devuelve la única instancia de la app, creándola la primera vez. */
        fun obtener(contexto: Context): PreferenciasRepository =
            instancia ?: synchronized(this) {
                instancia ?: PreferenciasRepository(
                    contexto.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
                ).also { instancia = it }
            }
    }
}
