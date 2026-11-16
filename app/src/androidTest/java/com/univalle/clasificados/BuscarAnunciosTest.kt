package com.univalle.clasificados

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.pantallas.TARJETA_ANUNCIO
import com.univalle.clasificados.util.Busqueda
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-15 · Buscar anuncios por título (RF-16, RF-17, RF-15, RN-14) con la app completa
 * y Firestore real. Antes de cada prueba se crean los anuncios de prueba de HU-07, HU-08 y HU-15
 * con la cuenta prueba@ de local.properties; se quedan en la base para probar a mano.
 *
 * Lo que se espera se calcula leyendo Firestore directamente, así las pruebas siguen sirviendo
 * cuando haya anuncios reales con las mismas palabras.
 */
@RunWith(AndroidJUnit4::class)
class BuscarAnunciosTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val auth = FirebaseAuth.getInstance()
    private lateinit var escenario: ActivityScenario<MainActivity>

    @Before
    fun iniciarSesionYCrearAnuncios() = runBlocking {
        val argumentos = InstrumentationRegistry.getArguments()
        val correo = argumentos.getString("correoPrueba").orEmpty()
        val contrasena = argumentos.getString("contrasenaPrueba").orEmpty()
        assumeTrue("Falta PRUEBA_CORREO o PRUEBA_CONTRASENA en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        val usuario = auth.signInWithEmailAndPassword(correo, contrasena).await().user!!
        AnunciosDePrueba.crear(autorId = usuario.uid, correo = correo)
        AnunciosDePrueba.crearHu15(autorId = usuario.uid, correo = correo)
        escenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun cerrar() {
        if (::escenario.isInitialized) escenario.close()
        auth.signOut()
    }

    /**
     * Criterio 1: desde Inicio, «bici» encuentra los anuncios de Deportes y de Vehículos, dos por
     * renglón, con «N anuncios encontrados» igual a los títulos que la contienen.
     */
    @Test
    fun desdeInicio_encuentraEnDosCategorias_dosPorRenglon() {
        buscarDesdeInicio("bici")
        esperarTexto(AnunciosDePrueba.TITULO_BICI_DEPORTES)
        esperarTexto(AnunciosDePrueba.TITULO_BICI_VEHICULOS)
        val esperados = coincidenciasEnFirestore("bici")
        esperarTexto("$esperados anuncios encontrados")

        // Dos por renglón: las dos primeras tarjetas están a la misma altura, una al lado de la otra.
        val tarjetas = compose.onAllNodesWithTag(TARJETA_ANUNCIO).fetchSemanticsNodes()
        assertTrue(tarjetas.size >= 2)
        assertEquals(tarjetas[0].boundsInRoot.top, tarjetas[1].boundsInRoot.top, 1f)
        assertTrue(tarjetas[1].boundsInRoot.left > tarjetas[0].boundsInRoot.right)
    }

    /** Criterio 2: «BICI» en mayúsculas encuentra «Bicicleta de ruta talla M». */
    @Test
    fun noDistingueMayusculas() {
        buscarDesdeInicio("  BICI  ")
        esperarTexto(AnunciosDePrueba.TITULO_BICI_DEPORTES)
    }

    /** Criterio 3: una palabra que solo está en una descripción no encuentra nada. */
    @Test
    fun palabraSoloEnDescripcion_noApareceNada() {
        assumeTrue("Algún título real ya contiene la palabra", coincidenciasEnFirestore(AnunciosDePrueba.PALABRA_SOLO_DESCRIPCION) == 0)
        buscarDesdeInicio(AnunciosDePrueba.PALABRA_SOLO_DESCRIPCION)
        esperarTexto("No se encontraron anuncios")
        esperarQueNoEste(AnunciosDePrueba.TITULO_PINTURA)
    }

    /**
     * Criterio 4: dentro de Empleos, buscar «Apartamento» (solo existe en Vivienda) no lo
     * muestra; en Vivienda sí aparece.
     */
    @Test
    fun dentroDeUnaCategoria_soloBuscaEnElla() {
        abrirCategoria(Categoria.EMPLEOS)
        esperarTexto("Auxiliar contable medio tiempo")
        escribirEnBuscador("Apartamento")
        esperarTexto("No se encontraron anuncios")
        esperarQueNoEste("Apartamento de 2 habitaciones cerca a la Univalle")

        // Al borrar el texto vuelve la lista completa de Empleos.
        escribirEnBuscador("")
        esperarTexto("Auxiliar contable medio tiempo")

        compose.onNode(hasText("Inicio") and hasClickAction()).performSemanticsAction(SemanticsActions.OnClick)
        abrirCategoria(Categoria.VIVIENDA)
        escribirEnBuscador("apartamento")
        esperarTexto("Apartamento de 2 habitaciones cerca a la Univalle")
        val enVivienda = runBlocking {
            Busqueda.filtrar(AnunciosRepository().anunciosDeCategoria(Categoria.VIVIENDA.id), "apartamento").size
        }
        esperarTexto("$enVivienda anuncios encontrados")
    }

    /** Criterio 5: sin coincidencias se ve «No se encontraron anuncios». */
    @Test
    fun sinCoincidencias_muestraMensaje() {
        buscarDesdeInicio("zzqxw sin resultados")
        esperarTexto("No se encontraron anuncios")
    }

    // --- Ayudas ---

    /** Escribe en el buscador de Inicio y toca la lupa del teclado; espera la pantalla 03b. */
    private fun buscarDesdeInicio(texto: String) {
        esperarTexto("Ver todas")
        escribirEnBuscador(texto)
        compose.onNode(hasSetTextAction()).performImeAction()
        esperarTexto("Resultados de búsqueda")
    }

    /** Reemplaza el texto del buscador de la pantalla visible (el único campo editable). */
    private fun escribirEnBuscador(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasSetTextAction()).fetchSemanticsNodes().size == 1
        }
        compose.onNode(hasSetTextAction()).performTextReplacement(texto)
    }

    /** Desde Inicio, toca la categoría y espera su listado. */
    private fun abrirCategoria(categoria: Categoria) {
        esperarTexto("Ver todas")
        compose.onNode(hasText(categoria.nombre) and hasClickAction()).performSemanticsAction(SemanticsActions.OnClick)
        esperarTexto("anuncios encontrados", substring = true)
    }

    /** Cuántos anuncios de Firestore tienen [texto] en el título, como lo hace la app. */
    private fun coincidenciasEnFirestore(texto: String): Int = runBlocking {
        Busqueda.filtrar(AnunciosRepository().todosLosAnuncios(), texto).size
    }

    /** Espera a que aparezca un texto en pantalla. */
    private fun esperarTexto(texto: String, substring: Boolean = false) {
        compose.waitUntil(timeoutMillis = 15_000) {
            compose.onAllNodesWithText(texto, substring = substring).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Comprueba que un texto no esté en pantalla. */
    private fun esperarQueNoEste(texto: String) {
        assertTrue(compose.onAllNodesWithText(texto).fetchSemanticsNodes().isEmpty())
    }
}
