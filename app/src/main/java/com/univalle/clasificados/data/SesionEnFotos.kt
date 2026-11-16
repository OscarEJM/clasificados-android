package com.univalle.clasificados.data

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.univalle.clasificados.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * Hace que las fotos de los anuncios se carguen con la sesión del usuario (HU-16 · Reglas de
 * seguridad de los datos; RNF-15).
 *
 * El bucket «anuncios-fotos» de Supabase es privado: solo un usuario con sesión puede ver las
 * fotos. Los anuncios guardan el enlace público de cada foto (`…/object/public/…`, HU-14); este
 * interceptor de las peticiones de Coil lo cambia por el enlace con sesión
 * (`…/object/authenticated/…`) y le agrega el pase (token) de Firebase del usuario. Así no hay que
 * cambiar ningún anuncio guardado. Las demás imágenes pasan sin cambios.
 *
 * Coil hace las peticiones fuera del hilo de la pantalla, por eso se puede esperar el pase aquí.
 */
class SesionEnFotos : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val peticion = chain.request()
        val url = peticion.url.toString()
        if (!url.startsWith(BuildConfig.SUPABASE_URL) || RUTA_PUBLICA !in url) return chain.proceed(peticion)

        val pase = runCatching {
            FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.let { Tasks.await(it, 15, TimeUnit.SECONDS).token }
        }.getOrNull()
        val conSesion = peticion.newBuilder()
            .url(url.replace(RUTA_PUBLICA, RUTA_CON_SESION))
            .header("apikey", BuildConfig.SUPABASE_KEY)
            .apply { if (pase != null) header("Authorization", "Bearer $pase") }
            .build()
        return chain.proceed(conSesion)
    }

    private companion object {
        const val RUTA_PUBLICA = "/storage/v1/object/public/"
        const val RUTA_CON_SESION = "/storage/v1/object/authenticated/"
    }
}
