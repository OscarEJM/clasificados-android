package com.univalle.clasificados

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings

/**
 * Clase Application de Clasificados: se ejecuta una vez al abrir la app, antes de cualquier pantalla.
 * Aquí se configuran los servicios que deben quedar listos desde el inicio.
 */
class ClasificadosApp : Application() {

    /** Configura Firestore antes de que cualquier repositorio lo use. */
    override fun onCreate() {
        super.onCreate()
        // RNF-06: Firestore guarda su caché solo en memoria; en el dispositivo solo se guarda el tema.
        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            .build()
    }
}
