package com.univalle.clasificados.data.repository

import com.univalle.clasificados.data.SupabaseCliente
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType

/** Acceso a las fotos de los anuncios en Supabase Storage (bucket «anuncios-fotos»). */
class FotosRepository(
    private val storage: Storage = SupabaseCliente.cliente.storage
) {

    private val bucket get() = storage.from(BUCKET)

    // --- Funciones de prueba de conexión (HU-02). Escriben y leen en la carpeta «pruebas/». ---

    /** Sube una imagen PNG de prueba y devuelve su URL pública. */
    suspend fun subirPrueba(imagenPng: ByteArray): String {
        bucket.upload(RUTA_PRUEBA, imagenPng) {
            upsert = true
            contentType = ContentType.Image.PNG
        }
        return bucket.publicUrl(RUTA_PRUEBA)
    }

    /** Descarga la imagen de prueba desde su URL pública. */
    suspend fun leerPrueba(): ByteArray = bucket.downloadPublic(RUTA_PRUEBA)

    companion object {
        const val BUCKET = "anuncios-fotos"
        private const val RUTA_PRUEBA = "pruebas/prueba.png"
    }
}
