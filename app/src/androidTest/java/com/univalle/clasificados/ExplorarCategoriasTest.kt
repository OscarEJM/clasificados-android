package com.univalle.clasificados

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.pantallas.CUADRICULA_TODAS_CATEGORIAS
import com.univalle.clasificados.ui.pantallas.InicioScreen
import com.univalle.clasificados.ui.pantallas.TodasCategoriasScreen
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-06 · Explorar categorías sobre las pantallas 03 · Inicio y 04 · Todas las
 * categorías, sin Firebase. La navegación real (con sesión) está en NavegacionCategoriasTest.
 */
@RunWith(AndroidJUnit4::class)
class ExplorarCategoriasTest {

    @get:Rule
    val compose = createComposeRule()

    /**
     * Criterio 1: Inicio muestra el buscador y seis tarjetas en tres renglones de dos:
     * Empleos, Vivienda, Servicios, Comunidad, Vehículos y «Más categorías».
     */
    @Test
    fun inicio_muestraBuscadorYSeisTarjetasDosPorRenglon() {
        compose.setContent { ClasificadosTheme { InicioScreen(alAbrirCategoria = {}, alVerTodas = {}) } }

        compose.onNode(hasSetTextAction()).assertExists()
        compose.onNodeWithText("¿Qué estás buscando hoy?").assertExists()
        val nombres = listOf("Empleos", "Vivienda", "Servicios", "Comunidad", "Vehículos", "Más categorías")
        nombres.forEach { compose.onNodeWithText(it).assertExists() }
        compose.onNodeWithText("Tecnología").assertDoesNotExist()

        // Dos por renglón: cada pareja está a la misma altura y en columnas distintas.
        nombres.chunked(2).forEach { (izquierda, derecha) ->
            val a = compose.onNodeWithText(izquierda).getBoundsInRoot()
            val b = compose.onNodeWithText(derecha).getBoundsInRoot()
            assertEquals(a.top, b.top)
            assertTrue(a.left < b.left)
        }
        val renglon1 = compose.onNodeWithText("Empleos").getBoundsInRoot().top
        val renglon2 = compose.onNodeWithText("Servicios").getBoundsInRoot().top
        assertNotEquals(renglon1, renglon2)
    }

    /** Criterio 2: «Más categorías» y «Ver todas» abren la pantalla de todas las categorías. */
    @Test
    fun inicio_masCategoriasYVerTodas_abrenTodas() {
        var veces = 0
        compose.setContent { ClasificadosTheme { InicioScreen(alAbrirCategoria = {}, alVerTodas = { veces++ }) } }

        tocar("Más categorías")
        tocar("Ver todas")
        assertEquals(2, veces)
    }

    /** Criterio 2: la pantalla 04 muestra las 13 categorías, dos por renglón. */
    @Test
    fun todas_muestraLasTreceDosPorRenglon() {
        compose.setContent { ClasificadosTheme { TodasCategoriasScreen(alAbrirCategoria = {}, alVolver = {}) } }

        val nombres = Categoria.entries.map { it.nombre }
        nombres.chunked(2).forEach { pareja ->
            compose.onNodeWithTag(CUADRICULA_TODAS_CATEGORIAS).performScrollToNode(hasText(pareja.first()))
            pareja.forEach { compose.onNodeWithText(it).assertExists() }
            if (pareja.size == 2) {
                val a = compose.onNodeWithText(pareja[0]).getBoundsInRoot()
                val b = compose.onNodeWithText(pareja[1]).getBoundsInRoot()
                assertEquals(a.top, b.top)
                assertTrue(a.left < b.left)
            }
        }
    }

    /** Criterio 3: tocar una categoría en Inicio pide abrir el listado de esa categoría. */
    @Test
    fun inicio_tocarCategoria_abreSuListado() {
        var tocada: Categoria? = null
        compose.setContent { ClasificadosTheme { InicioScreen(alAbrirCategoria = { tocada = it }, alVerTodas = {}) } }

        tocar("Vivienda")
        assertEquals(Categoria.VIVIENDA, tocada)
    }

    /** Criterio 3: tocar una categoría en la pantalla 04 pide abrir el listado de esa categoría. */
    @Test
    fun todas_tocarCategoria_abreSuListado() {
        var tocada: Categoria? = null
        compose.setContent { ClasificadosTheme { TodasCategoriasScreen(alAbrirCategoria = { tocada = it }, alVolver = {}) } }

        compose.onNodeWithTag(CUADRICULA_TODAS_CATEGORIAS).performScrollToNode(hasText("Deportes"))
        tocar("Deportes")
        assertEquals(Categoria.DEPORTES, tocada)
    }

    /** Toca el elemento con ese texto (o su tarjeta, que es el elemento que se puede tocar). */
    private fun tocar(texto: String) {
        compose.onNode(hasText(texto) and hasClickAction(), useUnmergedTree = false)
            .performSemanticsAction(SemanticsActions.OnClick)
    }
}
