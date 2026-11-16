package com.univalle.clasificados.util

import com.univalle.clasificados.model.Ciudad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias del paso 4 de Publicar (HU-14 · Publicar anuncio: ubicación, contacto y
 * confirmación; RF-25, RN-11) y del catálogo de ciudades (sección 1.4). Se ejecutan sin
 * emulador: ./gradlew testDebugUnitTest
 */
class ValidadorContactoTest {

    /** Sin ciudad aparece el mensaje de la sección 1.5; con ciudad no hay error. */
    @Test
    fun ciudadObligatoria() {
        assertEquals(Validador.CIUDAD_OBLIGATORIA, Validador.validarCiudad(null))
        assertEquals(Validador.CIUDAD_OBLIGATORIA, Validador.validarCiudad(" "))
        assertNull(Validador.validarCiudad("Tuluá"))
    }

    /** Correo vacío, sin «@» o sin extensión: cada caso con su mensaje; uno válido no tiene error. */
    @Test
    fun correoDeContacto() {
        assertEquals(Validador.CORREO_CONTACTO_OBLIGATORIO, Validador.validarCorreoContacto(""))
        assertEquals(Validador.CORREO_CONTACTO_OBLIGATORIO, Validador.validarCorreoContacto("   "))
        assertEquals(Validador.CORREO_CONTACTO_INVALIDO, Validador.validarCorreoContacto("ana.gomez.email.com"))
        assertEquals(Validador.CORREO_CONTACTO_INVALIDO, Validador.validarCorreoContacto("ana@email"))
        assertNull(Validador.validarCorreoContacto("ana.gomez@email.com"))
        assertNull(Validador.validarCorreoContacto(" ana@email.co "))
    }

    /** Las 10 ciudades de la sección 1.4, en su orden. */
    @Test
    fun diezCiudadesEnOrden() {
        assertEquals(
            listOf(
                "Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena",
                "Bucaramanga", "Pereira", "Manizales", "Pasto", "Tuluá"
            ),
            Ciudad.entries.map { it.nombre }
        )
    }
}
