package com.univalle.clasificados.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas unitarias de las reglas de las fotos del paso 3 de Publicar (HU-13 · RF-24, RNF-09,
 * RN-10). Se ejecutan sin emulador: ./gradlew testDebugUnitTest
 */
class ValidadorFotosTest {

    private val megabyte = 1024L * 1024

    /** JPG y PNG de hasta 5 MB son válidas, incluido el límite exacto. */
    @Test
    fun jpgYPngHasta5MB() {
        assertTrue(Validador.esFotoValida("image/jpeg", 2 * megabyte))
        assertTrue(Validador.esFotoValida("image/png", 5 * megabyte))
        assertTrue(Validador.esFotoValida("IMAGE/JPEG", 1))
    }

    /** Más de 5 MB, otro formato o datos desconocidos: no se agrega. */
    @Test
    fun rechazaGrandesOtrosFormatosYDesconocidos() {
        assertFalse(Validador.esFotoValida("image/jpeg", 5 * megabyte + 1))
        assertFalse(Validador.esFotoValida("image/gif", megabyte))
        assertFalse(Validador.esFotoValida("image/webp", megabyte))
        assertFalse(Validador.esFotoValida(null, megabyte))
        assertFalse(Validador.esFotoValida("image/png", null))
        assertFalse(Validador.esFotoValida("image/png", 0))
    }

    /** Sin fotos no se puede continuar; con una o más, sí. */
    @Test
    fun cantidadMinima() {
        assertEquals("Agrega al menos 1 foto", Validador.validarCantidadFotos(0))
        assertNull(Validador.validarCantidadFotos(1))
        assertNull(Validador.validarCantidadFotos(5))
    }
}
