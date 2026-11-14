package com.univalle.clasificados.util

/**
 * Iniciales del avatar de la tarjeta de perfil en Ajustes (HU-09 · Ver mi perfil en Ajustes, RF-33).
 * Función pura, sin Android, para probarla con JUnit.
 *
 * Regla aprobada por Oscar el 14/11/2026:
 * - la primera letra de las dos primeras palabras del nombre, en mayúscula
 *   («Oscar Eduardo Jimenez» → «OE», «Ana Gómez» → «AG»);
 * - si el nombre tiene una sola palabra, una sola letra («Oscar» → «O»);
 * - si no hay nombre guardado, la primera letra del correo («prueba@…» → «P»).
 */
object Iniciales {

    /**
     * @param nombre nombre completo del perfil; puede venir vacío.
     * @param correo correo de la sesión, para cuando no hay nombre.
     */
    fun de(nombre: String, correo: String): String {
        val palabras = nombre.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        val letras = if (palabras.isNotEmpty()) {
            palabras.take(2).map { it.first() }
        } else {
            listOfNotNull(correo.trim().firstOrNull())
        }
        return letras.joinToString("").uppercase()
    }
}
