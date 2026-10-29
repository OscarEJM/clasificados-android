package com.univalle.clasificados.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Acceso a la sesión de Firebase Authentication (HU-03). Es la única clase que consulta a
 * Firebase si hay un usuario con sesión; las pantallas lo hacen a través de los ViewModels.
 * Firebase guarda la sesión por sí solo, así que la app no guarda nada en el dispositivo (RNF-06).
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    /**
     * Emite `true` cuando hay un usuario con sesión y `false` cuando no lo hay. Emite el estado
     * actual apenas alguien empieza a escuchar y vuelve a emitir cada vez que la sesión cambia
     * (por ejemplo al iniciar o cerrar sesión).
     */
    val sesionActiva: Flow<Boolean> = callbackFlow {
        val oyente = FirebaseAuth.AuthStateListener { trySend(it.currentUser != null) }
        auth.addAuthStateListener(oyente)
        awaitClose { auth.removeAuthStateListener(oyente) }
    }
}
