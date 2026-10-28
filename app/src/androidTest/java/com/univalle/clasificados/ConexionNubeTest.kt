package com.univalle.clasificados

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MemoryCacheSettings
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de la configuración de la nube (HU-02). Se ejecutan en el emulador:
 * ./gradlew connectedDebugAndroidTest
 *
 * Las pruebas de escritura en Firestore y de subida a Supabase se usaron para verificar los
 * criterios 3 y 4 de HU-02 con permisos temporales sin sesión. Al cerrar la historia esos permisos
 * se quitaron (ahora todo exige sesión), así que esas pruebas se retiraron.
 */
@RunWith(AndroidJUnit4::class)
class ConexionNubeTest {

    /** Criterio 5: la caché de Firestore está configurada solo en memoria (RNF-06). */
    @Test
    fun firestoreUsaCacheSoloEnMemoria() {
        val ajustes = FirebaseFirestore.getInstance().firestoreSettings
        assertTrue(ajustes.cacheSettings is MemoryCacheSettings)
    }
}
