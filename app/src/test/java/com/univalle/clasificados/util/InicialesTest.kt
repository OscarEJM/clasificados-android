package com.univalle.clasificados.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias de las iniciales del avatar en Ajustes (HU-09 · RF-33).
 * Se ejecutan sin emulador: ./gradlew testDebugUnitTest
 */
class InicialesTest {

    /** Primera letra de las dos primeras palabras, en mayúscula. */
    @Test
    fun dosPrimerasPalabras() {
        assertEquals("OE", Iniciales.de("Oscar Eduardo Jimenez", "o@x.com"))
        assertEquals("AG", Iniciales.de("Ana Gómez", "a@x.com"))
        assertEquals("ÁL", Iniciales.de("  álvaro   lópez ", "a@x.com"))
    }

    /** Con una sola palabra, una sola letra. */
    @Test
    fun unaPalabra() = assertEquals("O", Iniciales.de("Oscar", "o@x.com"))

    /** Sin nombre guardado, la primera letra del correo. */
    @Test
    fun sinNombreUsaElCorreo() {
        assertEquals("P", Iniciales.de("", "prueba@clasificados.com"))
        assertEquals("P", Iniciales.de("   ", "prueba@clasificados.com"))
    }
}
