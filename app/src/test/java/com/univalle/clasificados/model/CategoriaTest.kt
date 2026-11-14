package com.univalle.clasificados.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Pruebas unitarias del catálogo fijo de categorías (HU-06, sección 1.4 de la guía).
 * Se ejecutan sin emulador: ./gradlew testDebugUnitTest
 */
class CategoriaTest {

    /** Son exactamente 13 y van en el orden de la sección 1.4. */
    @Test
    fun trece_categorias_en_el_orden_de_la_guia() {
        assertEquals(
            listOf(
                "Empleos", "Vivienda", "Servicios", "Comunidad", "Vehículos", "Tecnología",
                "Electrónica", "Mascotas", "Educación", "Salud", "Eventos", "Moda", "Deportes"
            ),
            Categoria.entries.map { it.nombre }
        )
    }

    /** Inicio muestra las 5 primeras (RF-10). */
    @Test
    fun principales_son_las_cinco_primeras() {
        assertEquals(
            listOf(Categoria.EMPLEOS, Categoria.VIVIENDA, Categoria.SERVICIOS, Categoria.COMUNIDAD, Categoria.VEHICULOS),
            Categoria.PRINCIPALES
        )
    }

    /** El id que viaja en la navegación devuelve la misma categoría; uno desconocido da null. */
    @Test
    fun desdeId_encuentra_la_categoria() {
        Categoria.entries.forEach { assertEquals(it, Categoria.desdeId(it.id)) }
        assertNull(Categoria.desdeId("no-existe"))
        assertNull(Categoria.desdeId(null))
    }
}
