package com.univalle.clasificados.util

import com.univalle.clasificados.model.Anuncio
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias de la búsqueda por título (HU-15 · Buscar anuncios por título; RN-14,
 * RF-16, RF-17). Se ejecutan sin emulador: ./gradlew testDebugUnitTest
 */
class BusquedaTest {

    private fun anuncio(id: String, titulo: String, descripcion: String = "") =
        Anuncio(id = id, titulo = titulo, tituloMinusculas = titulo.lowercase(), descripcion = descripcion)

    private val bici = anuncio("1", "Bicicleta de ruta talla M")
    private val electrica = anuncio("2", "Bicicleta eléctrica de segunda")
    private val cuadro = anuncio("3", "Cuadro decorativo para sala", "Pintado con acuarela")

    /** Criterio 2: «BICI» encuentra «bicicleta», sin importar mayúsculas y minúsculas. */
    @Test
    fun noDistingueMayusculas() {
        assertEquals(listOf(bici, electrica), Busqueda.filtrar(listOf(bici, electrica, cuadro), "BICI"))
    }

    /** Criterio 3: una palabra que solo está en la descripción no encuentra nada. */
    @Test
    fun soloBuscaEnElTitulo() {
        assertEquals(emptyList<Anuncio>(), Busqueda.filtrar(listOf(bici, cuadro), "acuarela"))
    }

    /** Los espacios al principio y al final no cuentan; sin texto se ven todos, en su orden. */
    @Test
    fun ignoraEspaciosYTextoVacio() {
        assertEquals(listOf(bici), Busqueda.filtrar(listOf(bici, cuadro), "  ruta  "))
        assertEquals(listOf(bici, electrica, cuadro), Busqueda.filtrar(listOf(bici, electrica, cuadro), "   "))
    }
}
