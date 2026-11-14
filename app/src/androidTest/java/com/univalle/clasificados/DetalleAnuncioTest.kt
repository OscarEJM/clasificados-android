package com.univalle.clasificados

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.pantallas.GALERIA_FOTOS
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-08 · Ver el detalle de un anuncio (RF-14), con la app completa y Firestore real.
 * Antes de cada prueba se crean los [AnunciosDePrueba] con el usuario de prueba de
 * local.properties (PRUEBA_CORREO y PRUEBA_CONTRASENA); si no está, se saltan.
 */
@RunWith(AndroidJUnit4::class)
class DetalleAnuncioTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val auth = FirebaseAuth.getInstance()

    /** Correo del usuario de prueba: es el correo de contacto de los anuncios de prueba. */
    private var correo = ""

    @Before
    fun iniciarSesionYCrearAnuncios() = runBlocking {
        val argumentos = InstrumentationRegistry.getArguments()
        correo = argumentos.getString("correoPrueba").orEmpty()
        val contrasena = argumentos.getString("contrasenaPrueba").orEmpty()
        assumeTrue("Falta PRUEBA_CORREO o PRUEBA_CONTRASENA en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        val usuario = auth.signInWithEmailAndPassword(correo, contrasena).await().user!!
        AnunciosDePrueba.crear(autorId = usuario.uid, correo = correo)
    }

    @After
    fun cerrarSesion() = auth.signOut()

    /**
     * Criterios 1 y 4: al tocar una tarjeta se ven las fotos deslizables con «n / total», la
     * categoría, el título, el precio, la ciudad, «Hace 2 horas», la descripción y el correo.
     * La flecha regresa al listado.
     */
    @Test
    fun tocarTarjeta_muestraTodosLosDatos() {
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirAnuncio(Categoria.SERVICIOS, AnunciosDePrueba.TITULO_HACE_DOS_HORAS)

            esperarTexto("1 / 5")
            listOf("Servicios", "$80.000", "Tuluá", "Hace 2 horas", "Descripción", "Contacto").forEach(::verTexto)
            verTexto(AnunciosDePrueba.TITULO_HACE_DOS_HORAS)
            verTexto(correo)
            assertTrue(
                "Falta la descripción",
                compose.onAllNodesWithText("Anuncio de prueba de HU-07 y HU-08", substring = true)
                    .fetchSemanticsNodes().isNotEmpty()
            )

            // Fotos deslizables: al pasar a la segunda, el contador cambia.
            compose.onNodeWithTag(GALERIA_FOTOS).performTouchInput { swipeLeft() }
            esperarTexto("2 / 5")

            tocar("Volver")
            esperarTexto("anuncios encontrados", substring = true)
        }
    }

    /** Criterio 2: si el anuncio tiene teléfono, se ve como texto. */
    @Test
    fun conTelefono_muestraElTelefono() {
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirAnuncio(Categoria.EMPLEOS, "Auxiliar contable medio tiempo")
            esperarTexto("1 / 3")
            verTexto(AnunciosDePrueba.TELEFONO)
            verTexto(correo)
        }
    }

    /** Criterio 3: si el anuncio no tiene teléfono, no aparece la fila del teléfono. */
    @Test
    fun sinTelefono_noMuestraLaFila() {
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirAnuncio(Categoria.EMPLEOS, "Mesero para fines de semana")
            esperarTexto("1 / 1")
            verTexto(correo)
            assertTrue(
                "No debería verse un teléfono",
                compose.onAllNodesWithText(AnunciosDePrueba.TELEFONO).fetchSemanticsNodes().isEmpty()
            )
        }
    }

    /** Desde Inicio (o Todas las categorías) abre la categoría y toca la tarjeta con ese título. */
    private fun abrirAnuncio(categoria: Categoria, titulo: String) {
        esperarTexto("Ver todas")
        if (categoria in Categoria.PRINCIPALES) {
            tocar(categoria.nombre)
        } else {
            tocar("Ver todas")
            esperarTexto("Todas las categorías")
            tocar(categoria.nombre)
        }
        esperarTexto(titulo)
        compose.onNode(hasText(titulo) and hasClickAction(), useUnmergedTree = false).performScrollTo()
        tocar(titulo)
        esperarTexto("Contacto")
    }

    /** Comprueba que un texto exacto esté en pantalla, bajando hasta él si hace falta. */
    private fun verTexto(texto: String) {
        esperarTexto(texto)
        compose.onAllNodesWithText(texto)[0].performScrollTo()
    }

    /** Toca el elemento con ese texto o descripción. Los textos de una tarjeta están dentro de ella. */
    private fun tocar(texto: String) {
        val conTexto = hasText(texto) and hasClickAction()
        val conDescripcion = hasContentDescription(texto) and hasClickAction()
        compose.onNode(conTexto or conDescripcion).performSemanticsAction(SemanticsActions.OnClick)
    }

    /** Espera hasta 10 segundos a que aparezca un texto en pantalla. */
    private fun esperarTexto(texto: String, substring: Boolean = false) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto, substring = substring).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
