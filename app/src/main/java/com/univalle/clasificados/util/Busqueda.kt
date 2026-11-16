package com.univalle.clasificados.util

import com.univalle.clasificados.model.Anuncio

/**
 * Búsqueda por título (HU-15 · Buscar anuncios por título; RN-14, RF-16, RF-17). Funciones puras,
 * sin nada de Android, para probarlas con JUnit.
 *
 * Firestore no puede buscar «contiene» dentro de un texto, así que los anuncios se traen y se
 * filtran aquí: solo cuenta el título (nunca la descripción), sin distinguir mayúsculas de
 * minúsculas y sin los espacios del principio y del final de lo buscado.
 */
object Busqueda {

    /** Texto buscado listo para comparar: sin espacios al principio ni al final y en minúsculas. */
    fun normalizar(texto: String): String = texto.trim().lowercase()

    /** `true` si el título del anuncio contiene el texto buscado (RN-14). */
    fun coincide(anuncio: Anuncio, texto: String): Boolean {
        val buscado = normalizar(texto)
        val titulo = anuncio.tituloMinusculas.ifEmpty { anuncio.titulo.lowercase() }
        return titulo.contains(buscado)
    }

    /**
     * Los anuncios cuyo título contiene [texto], en el mismo orden en que llegaron (más recientes
     * primero, RN-13). Con el texto vacío o solo espacios devuelve todos.
     */
    fun filtrar(anuncios: List<Anuncio>, texto: String): List<Anuncio> =
        if (normalizar(texto).isEmpty()) anuncios else anuncios.filter { coincide(it, texto) }
}
