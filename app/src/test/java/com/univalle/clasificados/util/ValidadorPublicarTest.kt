package com.univalle.clasificados.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias de las validaciones de los pasos 1 y 2 de Publicar (HU-12 · RF-22, RF-23,
 * RN-08, RN-09). Se ejecutan sin emulador: ./gradlew testDebugUnitTest
 */
class ValidadorPublicarTest {

    /** Paso 1: sin categoría aparece el mensaje de la sección 1.5. */
    @Test
    fun categoria() {
        assertEquals("Por favor selecciona una categoría para continuar", Validador.validarCategoria(null))
        assertNull(Validador.validarCategoria("empleos"))
    }

    /** Título: obligatorio y de al menos 10 caracteres (sin contar espacios de los bordes). */
    @Test
    fun titulo() {
        assertEquals("El título es obligatorio", Validador.validarTitulo(""))
        assertEquals("El título es obligatorio", Validador.validarTitulo("   "))
        assertEquals("El título debe tener al menos 10 caracteres", Validador.validarTitulo("Apto"))
        assertEquals("El título debe tener al menos 10 caracteres", Validador.validarTitulo("  123456789  "))
        assertNull(Validador.validarTitulo("1234567890"))
    }

    /** Descripción: obligatoria y de al menos 20 caracteres. */
    @Test
    fun descripcion() {
        assertEquals("La descripción es obligatoria", Validador.validarDescripcion(""))
        assertEquals("La descripción debe tener al menos 20 caracteres", Validador.validarDescripcion("Muy corta"))
        assertNull(Validador.validarDescripcion("Veinte caracteres ok"))
    }

    /** Precio: obligatorio, entero ≥ 0; el 0 es válido (gratis). */
    @Test
    fun precio() {
        assertEquals("Indica un precio o escribe '0' si es gratis", Validador.validarPrecio(""))
        assertEquals("Indica un precio o escribe '0' si es gratis", Validador.validarPrecio("1.5"))
        assertNull(Validador.validarPrecio("0"))
        assertNull(Validador.validarPrecio("1200000"))
    }

    /** El campo de precio solo deja dígitos y como máximo 10. */
    @Test
    fun limpiarPrecio() {
        assertEquals("12000", Validador.limpiarPrecio("12.000"))
        assertEquals("15", Validador.limpiarPrecio("-1,5 abc"))
        assertEquals("1234567890", Validador.limpiarPrecio("123456789012"))
    }
}
