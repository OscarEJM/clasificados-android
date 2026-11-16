package com.univalle.clasificados.data.repository

import com.univalle.clasificados.data.SupabaseCliente
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType

/**
 * Una foto lista para subir (HU-14).
 * @property bytes contenido del archivo, tal como está en el teléfono.
 * @property tipo tipo del archivo: «image/jpeg» o «image/png».
 */
class FotoParaSubir(val bytes: ByteArray, val tipo: String)

/** Acceso a las fotos de los anuncios en Supabase Storage (bucket «anuncios-fotos», RNF-05). */
class FotosRepository(
    private val storage: Storage = SupabaseCliente.cliente.storage
) {

    private val bucket get() = storage.from(BUCKET)

    /**
     * Ruta de la foto número [numero] (desde 1) de un anuncio: `{autorId}/{idAnuncio}/{n}.jpg`
     * (sección 1.8). Las PNG conservan su formato real aunque la ruta diga «.jpg»: Supabase
     * guarda el tipo del archivo y el navegador o Coil la muestran bien (aprobado por Oscar).
     */
    fun ruta(autorId: String, anuncioId: String, numero: Int) = "$autorId/$anuncioId/$numero.jpg"

    /**
     * Sube las fotos de un anuncio en orden y devuelve sus URL públicas (HU-14 · RF-26). Las
     * reglas de Supabase solo dejan subir dentro de la carpeta del propio usuario (RNF-15).
     *
     * Si una foto falla, borra las que ya alcanzaron a subir y lanza el error, para no dejar
     * fotos sueltas de un anuncio que no se creó.
     */
    suspend fun subirFotosAnuncio(autorId: String, anuncioId: String, fotos: List<FotoParaSubir>): List<String> {
        val subidas = mutableListOf<String>()
        try {
            fotos.forEachIndexed { indice, foto ->
                val ruta = ruta(autorId, anuncioId, indice + 1)
                bucket.upload(ruta, foto.bytes) {
                    upsert = false
                    contentType = ContentType.parse(foto.tipo)
                }
                subidas += ruta
            }
        } catch (e: Exception) {
            borrar(subidas)
            throw e
        }
        return subidas.map { bucket.publicUrl(it) }
    }

    /**
     * Borra las fotos de un anuncio que no se pudo guardar (HU-14) y, más adelante, las de un
     * anuncio eliminado (HU-20 · RN-16). Si el borrado falla (por ejemplo sin internet), no se
     * interrumpe el resto: lo importante es avisar al usuario del primer error.
     */
    suspend fun borrarFotosAnuncio(autorId: String, anuncioId: String, cantidad: Int) =
        borrar((1..cantidad).map { ruta(autorId, anuncioId, it) })

    private suspend fun borrar(rutas: List<String>) {
        if (rutas.isEmpty()) return
        runCatching { bucket.delete(rutas) }
    }

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
