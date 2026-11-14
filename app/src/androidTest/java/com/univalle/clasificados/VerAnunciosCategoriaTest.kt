package com.univalle.clasificados

import android.os.SystemClock
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.pantallas.TARJETA_ANUNCIO
import com.univalle.clasificados.util.FormatoPrecio
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
 * Pruebas de HU-07 · Ver los anuncios de una categoría (RF-12, RF-13, RF-15, RNF-08), con la app
 * completa y Firestore real. Antes de cada prueba se crean los [AnunciosDePrueba] con el usuario
 * de prueba de local.properties (PRUEBA_CORREO y PRUEBA_CONTRASENA); si no está, se saltan.
 *
 * Lo que se espera se calcula leyendo Firestore directamente (sin pasar por la app), así las
 * pruebas siguen sirviendo cuando haya anuncios reales en las categorías.
 *
 * El criterio 4 (anuncio de otro usuario en otro dispositivo) lo prueba Oscar a mano: inicia
 * sesión con la segunda cuenta y ve los anuncios que publicó el usuario de prueba.
 */
@RunWith(AndroidJUnit4::class)
class VerAnunciosCategoriaTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val auth = FirebaseAuth.getInstance()

    @Before
    fun iniciarSesionYCrearAnuncios() = runBlocking {
        val argumentos = InstrumentationRegistry.getArguments()
        val correo = argumentos.getString("correoPrueba").orEmpty()
        val contrasena = argumentos.getString("contrasenaPrueba").orEmpty()
        assumeTrue("Falta PRUEBA_CORREO o PRUEBA_CONTRASENA en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        val usuario = auth.signInWithEmailAndPassword(correo, contrasena).await().user!!
        AnunciosDePrueba.crear(autorId = usuario.uid, correo = correo)
    }

    @After
    fun cerrarSesion() = auth.signOut()

    /**
     * Criterios 1 y 2: tarjetas de dos por renglón con foto, título, precio y ciudad, y
     * «N anuncios encontrados» con N igual al número de tarjetas.
     */
    @Test
    fun tarjetasDosPorRenglon_conContadorCorrecto() {
        val esperados = anunciosEnFirestore(Categoria.EMPLEOS)
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirCategoria(Categoria.EMPLEOS)
            esperarTexto("${esperados.size} anuncios encontrados")

            val tarjetas = compose.onAllNodesWithTag(TARJETA_ANUNCIO).fetchSemanticsNodes()
            assertEquals("Cada anuncio es una tarjeta", esperados.size, tarjetas.size)

            // Dos por renglón: las dos primeras tarjetas comparten renglón y la tercera baja.
            val (primera, segunda, tercera) = tarjetas.map { it.boundsInRoot }
            assertEquals(primera.top, segunda.top, 1f)
            assertTrue("La segunda va a la derecha", segunda.left > primera.right)
            assertTrue("La tercera va en otro renglón", tercera.top > primera.bottom)

            // La primera tarjeta muestra título, precio y ciudad del anuncio.
            val textos = textosDe(tarjetas.first())
            val anuncio = esperados.first()
            assertEquals(listOf(anuncio.titulo, FormatoPrecio.formatear(anuncio.precio), anuncio.ciudad), textos)
        }
    }

    /** Criterio 3: el anuncio más reciente aparece primero (RN-13). */
    @Test
    fun masRecientePrimero() {
        val esperados = anunciosEnFirestore(Categoria.VIVIENDA)
        val masReciente = esperados.maxBy { it.fechaPublicacion!! }
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirCategoria(Categoria.VIVIENDA)
            esperarTexto("${esperados.size} anuncios encontrados")
            val primera = compose.onAllNodesWithTag(TARJETA_ANUNCIO).fetchSemanticsNodes().first()
            assertEquals(masReciente.titulo, textosDe(primera).first())
        }
    }

    /** Un anuncio gratis se muestra «$0» (RN-09). */
    @Test
    fun anuncioGratis_muestraCeroPesos() {
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirCategoria(Categoria.VIVIENDA)
            esperarTexto("$0")
        }
    }

    /** Criterio 5: una categoría sin anuncios muestra «No se encontraron anuncios» (RF-15). */
    @Test
    fun categoriaVacia_muestraMensaje() {
        val categoria = AnunciosDePrueba.CATEGORIA_VACIA
        assumeTrue("La categoría de prueba ya tiene anuncios reales", anunciosEnFirestore(categoria).isEmpty())
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Ver todas")
            tocar("Ver todas")
            esperarTexto("Todas las categorías")
            tocar(categoria.nombre)
            esperarTexto("No se encontraron anuncios", tiempo = 10_000)
            assertEquals(0, compose.onAllNodesWithTag(TARJETA_ANUNCIO).fetchSemanticsNodes().size)
        }
    }

    /** Criterio 6: las tarjetas aparecen en 5 segundos o menos desde que se toca la categoría (RNF-08). */
    @Test
    fun tarjetasEnCincoSegundosOMenos() {
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Ver todas")
            val inicio = SystemClock.elapsedRealtime()
            tocar(Categoria.EMPLEOS.nombre)
            compose.waitUntil(timeoutMillis = 5_000) {
                compose.onAllNodesWithTag(TARJETA_ANUNCIO).fetchSemanticsNodes().isNotEmpty()
            }
            val segundos = (SystemClock.elapsedRealtime() - inicio) / 1000.0
            assertTrue("Tardó $segundos s", segundos <= 5.0)
        }
    }

    /** Lee de Firestore los anuncios de una categoría, del más reciente al más antiguo. */
    private fun anunciosEnFirestore(categoria: Categoria): List<Anuncio> = runBlocking {
        FirebaseFirestore.getInstance().collection(AnunciosRepository.COLECCION_ANUNCIOS)
            .whereEqualTo(AnunciosRepository.CAMPO_CATEGORIA, categoria.id)
            .get(Source.SERVER).await()
            .toObjects(Anuncio::class.java)
            .sortedByDescending { it.fechaPublicacion }
    }

    /** Textos de una tarjeta en orden: título, precio y ciudad. */
    private fun textosDe(tarjeta: androidx.compose.ui.semantics.SemanticsNode): List<String> =
        tarjeta.config.getOrNull(SemanticsProperties.Text).orEmpty().map { it.text }

    /** Desde Inicio, toca la categoría y espera a que se vea su listado. */
    private fun abrirCategoria(categoria: Categoria) {
        esperarTexto("Ver todas")
        tocar(categoria.nombre)
        esperarQueDesaparezca("Ver todas")
    }

    /** Toca el elemento con ese texto o descripción. */
    private fun tocar(texto: String) {
        val conTexto = hasText(texto) and hasClickAction()
        val conDescripcion = hasContentDescription(texto) and hasClickAction()
        compose.onNode(conTexto or conDescripcion).performSemanticsAction(SemanticsActions.OnClick)
    }

    /** Espera a que aparezca un texto exacto en pantalla. */
    private fun esperarTexto(texto: String, tiempo: Long = 10_000) {
        compose.waitUntil(timeoutMillis = tiempo) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Espera a que un texto deje de estar en pantalla. */
    private fun esperarQueDesaparezca(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isEmpty()
        }
    }
}
