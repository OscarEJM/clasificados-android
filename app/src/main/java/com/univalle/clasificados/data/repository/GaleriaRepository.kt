package com.univalle.clasificados.data.repository

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns

/**
 * Datos de una foto elegida en la galería que hacen falta para revisarla (HU-13 · RF-24).
 * @property tipo tipo del archivo, por ejemplo «image/jpeg»; `null` si Android no lo sabe.
 * @property tamanoBytes tamaño del archivo; `null` si Android no lo sabe.
 */
data class InfoFoto(val tipo: String?, val tamanoBytes: Long?)

/**
 * Lee el tipo y el tamaño de las fotos que el usuario elige en la galería del teléfono, sin
 * copiarlas ni guardarlas (HU-13 · Publicar anuncio: agregar fotos). La foto se sube a Supabase
 * Storage hasta que se publica el anuncio (HU-14).
 */
class GaleriaRepository(private val contenido: ContentResolver) {

    /**
     * Tipo (ContentResolver.getType) y tamaño (OpenableColumns.SIZE) de la foto.
     * Si Android no da alguno de los dos, queda en `null` y la foto se rechaza.
     */
    fun info(uri: Uri): InfoFoto {
        val tipo = runCatching { contenido.getType(uri) }.getOrNull()
        val tamano = runCatching {
            contenido.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
                val columna = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (columna >= 0 && cursor.moveToFirst() && !cursor.isNull(columna)) cursor.getLong(columna) else null
            }
        }.getOrNull()
        return InfoFoto(tipo, tamano)
    }

    /**
     * Contenido de la foto para subirla a Supabase al publicar (HU-14 · RF-26). Lee la foto
     * original del teléfono, sin cambiarla. Se llama fuera del hilo de la pantalla.
     * @throws java.io.IOException si la foto ya no existe en el teléfono.
     */
    fun leer(uri: Uri): ByteArray =
        contenido.openInputStream(uri)?.use { it.readBytes() }
            ?: throw java.io.IOException("No se pudo leer la foto $uri")
}
