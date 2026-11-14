package com.univalle.clasificados.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias del formato de precios que muestran las tarjetas de anuncio
 * (HU-07 · RN-09, RN-21, RNF-13). Se ejecutan sin emulador: ./gradlew testDebugUnitTest
 */
class FormatoPrecioTest {

    /** Punto como separador de miles y «$» pegado al número. */
    @Test
    fun separaMilesConPunto() {
        assertEquals("$1.200.000", FormatoPrecio.formatear(1_200_000))
        assertEquals("$450.000", FormatoPrecio.formatear(450_000))
        assertEquals("$999", FormatoPrecio.formatear(999))
    }

    /** Un anuncio gratis se muestra «$0». */
    @Test
    fun gratisEsCeroPesos() {
        assertEquals("$0", FormatoPrecio.formatear(0))
    }
}
