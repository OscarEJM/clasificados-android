package com.univalle.clasificados

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import com.univalle.clasificados.data.SesionEnFotos
import okhttp3.OkHttpClient

/**
 * Clase Application de Clasificados: se ejecuta una vez al abrir la app, antes de cualquier pantalla.
 * Aquí se configuran los servicios que deben quedar listos desde el inicio.
 *
 * También le da a Coil el cargador de imágenes de la app (HU-16): uno que envía la sesión del
 * usuario al pedir las fotos de los anuncios, porque el bucket de Supabase es privado ([SesionEnFotos]).
 */
class ClasificadosApp : Application(), SingletonImageLoader.Factory {

    /** Configura Firestore antes de que cualquier repositorio lo use. */
    override fun onCreate() {
        super.onCreate()
        // RNF-06: Firestore guarda su caché solo en memoria; en el dispositivo solo se guarda el tema.
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            .build()
    }

    /** Cargador de imágenes de Coil que usan todas las pantallas (fotos con sesión, HU-16 · RNF-15). */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context as Context)
            .components {
                add(OkHttpNetworkFetcherFactory(callFactory = { OkHttpClient.Builder().addInterceptor(SesionEnFotos()).build() }))
            }
            .build()
}
