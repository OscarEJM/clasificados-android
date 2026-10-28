package com.univalle.clasificados.data

import com.google.firebase.auth.FirebaseAuth
import com.univalle.clasificados.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.tasks.await

/**
 * Cliente único de Supabase. Supabase reconoce al usuario de Firebase (autenticación de terceros):
 * cada petición lleva el token de Firebase del usuario con sesión. Sin sesión se usa la llave pública.
 */
object SupabaseCliente {

    val cliente: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_KEY
        ) {
            accessToken = {
                FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token
            }
            install(Storage)
        }
    }
}
