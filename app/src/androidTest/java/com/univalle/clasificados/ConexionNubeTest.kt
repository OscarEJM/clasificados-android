package com.univalle.clasificados

import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MemoryCacheSettings
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.data.repository.FotosRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

/**
 * Prueba de conexión de HU-02. Se ejecuta en el emulador con internet:
 * ./gradlew connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class ConexionNubeTest {

    /** Criterio 5: la caché de Firestore está configurada solo en memoria (RNF-06). */
    @Test
    fun firestoreUsaCacheSoloEnMemoria() {
        val ajustes = FirebaseFirestore.getInstance().firestoreSettings
        assertTrue(ajustes.cacheSettings is MemoryCacheSettings)
    }

    /** Criterio 3: la app escribe un documento en la colección «pruebas» y lo vuelve a leer. */
    @Test
    fun escribeYLeeDocumentoDePruebaEnFirestore() = runBlocking {
        val repositorio = AnunciosRepository()
        val id = repositorio.escribirPrueba()
        assertEquals(AnunciosRepository.MENSAJE_PRUEBA, repositorio.leerPrueba(id))
    }

    /** Criterio 4: la app sube una imagen PNG al bucket «anuncios-fotos» y la vuelve a descargar. */
    @Test
    fun subeYLeeImagenDePruebaEnSupabase() = runBlocking {
        val imagen = crearImagenPng()
        val repositorio = FotosRepository()
        val url = repositorio.subirPrueba(imagen)
        assertTrue(url.contains(FotosRepository.BUCKET))
        assertEquals(imagen.size, repositorio.leerPrueba().size)
    }

    /** Imagen PNG de 64 × 64 px con el color primario de la app. */
    private fun crearImagenPng(): ByteArray {
        val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(Color.parseColor("#2563EB"))
        return ByteArrayOutputStream().use { salida ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
            salida.toByteArray()
        }
    }
}
