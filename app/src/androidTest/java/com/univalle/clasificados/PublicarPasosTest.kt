package com.univalle.clasificados

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.univalle.clasificados.util.Validador
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-12 · Publicar anuncio: pasos de categoría y detalles (RF-21, RF-22, RF-23) con la
 * app completa. Usan el usuario de prueba de local.properties (PRUEBA_CORREO y PRUEBA_CONTRASENA);
 * si no está, se saltan. Los mensajes se comparan con los de la sección 1.5 (en [Validador]).
 */
@RunWith(AndroidJUnit4::class)
class PublicarPasosTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val auth = FirebaseAuth.getInstance()

    @Before
    fun iniciarSesionDePrueba() = runBlocking {
        val argumentos = InstrumentationRegistry.getArguments()
        val correo = argumentos.getString("correoPrueba").orEmpty()
        val contrasena = argumentos.getString("contrasenaPrueba").orEmpty()
        assumeTrue("Falta PRUEBA_CORREO o PRUEBA_CONTRASENA en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        auth.signInWithEmailAndPassword(correo, contrasena).await()
        Unit
    }

    @After
    fun cerrarSesion() = auth.signOut()

    /** Criterio 1: al tocar «Publicar» se ve el indicador de 4 pasos con el paso 1 activo. */
    @Test
    fun publicar_muestraIndicadorConPaso1Activo() {
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirPublicar()
            listOf("Categoría", "Detalles", "Fotos", "Ubicación").forEach(::esperarTexto)
            esperarPaso(1)
            esperarTexto("Selecciona una categoría")
        }
    }

    /** Criterio 2: sin categoría, «Continuar» muestra el mensaje y no avanza. */
    @Test
    fun sinCategoria_muestraMensajeYNoAvanza() {
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirPublicar()
            tocar("Continuar")
            esperarTexto(Validador.CATEGORIA_OBLIGATORIA)
            esperarPaso(1)
            // Al elegir una categoría queda resaltada y el mensaje se va.
            tocar("Vivienda")
            compose.onNode(hasText("Vivienda") and hasClickAction()).assertIsSelected()
            esperarQueDesaparezca(Validador.CATEGORIA_OBLIGATORIA)
        }
    }

    /** Criterio 3: un título de menos de 10 caracteres muestra el mensaje; el contador cuenta lo escrito. */
    @Test
    fun tituloCorto_muestraMensajeYContador() {
        ActivityScenario.launch(MainActivity::class.java).use {
            irAlPaso2()
            esperarTexto("Mínimo 10 caracteres · 0 escritos")
            campo(TITULO).performTextInput("Apto")
            esperarTexto("Mínimo 10 caracteres · 4 escritos")
            tocar("Continuar")
            esperarTexto(Validador.TITULO_CORTO)
            esperarPaso(2)
        }
    }

    /** Criterio 4: descripción vacía o de menos de 20 caracteres muestra su mensaje y no avanza. */
    @Test
    fun descripcionVaciaOCorta_muestraMensaje() {
        ActivityScenario.launch(MainActivity::class.java).use {
            irAlPaso2()
            campo(TITULO).performTextInput("Bicicleta todoterreno rin 29")
            campo(PRECIO).performTextInput("500000")
            tocar("Continuar")
            esperarTexto(Validador.DESCRIPCION_OBLIGATORIA)
            esperarPaso(2)

            campo(DESCRIPCION).performTextInput("Muy buen estado")
            tocar("Continuar")
            esperarTexto(Validador.DESCRIPCION_CORTA)
            esperarPaso(2)
        }
    }

    /** Criterio 5: precio vacío muestra el mensaje y el campo solo acepta números enteros. */
    @Test
    fun precio_vacioYSoloNumeros() {
        ActivityScenario.launch(MainActivity::class.java).use {
            irAlPaso2()
            campo(TITULO).performTextInput("Bicicleta todoterreno rin 29")
            campo(DESCRIPCION).performTextInput("En muy buen estado, con frenos de disco")
            tocar("Continuar")
            esperarTexto(Validador.PRECIO_OBLIGATORIO)
            esperarPaso(2)

            // Letras, puntos y signos se descartan: solo quedan los dígitos.
            campo(PRECIO).performTextReplacement("1.200.000 pesos")
            assertEquals("1200000", textoDe(campo(PRECIO)))
        }
    }

    /** Criterio 6: con datos en el paso 2, «Volver» y luego «Continuar» los conserva. */
    @Test
    fun volverYContinuar_conservaLosDatos() {
        ActivityScenario.launch(MainActivity::class.java).use {
            irAlPaso2()
            campo(TITULO).performTextInput("Bicicleta todoterreno rin 29")
            campo(DESCRIPCION).performTextInput("En muy buen estado, con frenos de disco")
            campo(PRECIO).performTextInput("0")

            tocar("Volver")
            esperarPaso(1)
            compose.onNode(hasText("Empleos") and hasClickAction()).assertIsSelected()
            tocar("Continuar")
            esperarPaso(2)

            campo(TITULO).assertTextContains("Bicicleta todoterreno rin 29")
            campo(DESCRIPCION).assertTextContains("En muy buen estado, con frenos de disco")
            assertEquals("0", textoDe(campo(PRECIO)))

            // Con todo válido, «Continuar» pasa al paso 3, y «Volver» regresa con los datos.
            tocar("Continuar")
            esperarPaso(3)
            tocar("Volver")
            esperarPaso(2)
            campo(TITULO).assertTextContains("Bicicleta todoterreno rin 29")
        }
    }

    /** Espera Inicio y toca el botón verde «Publicar». */
    private fun abrirPublicar() {
        esperarTexto("Ver todas")
        tocar("Publicar")
        esperarTexto("Publicar anuncio")
    }

    /** Abre Publicar, elige Empleos y pasa al paso 2. */
    private fun irAlPaso2() {
        abrirPublicar()
        tocar("Empleos")
        tocar("Continuar")
        esperarPaso(2)
    }

    /** Campo de texto del paso 2 por su orden: título, descripción o precio. */
    private fun campo(indice: Int): SemanticsNodeInteraction =
        compose.onAllNodes(hasSetTextAction())[indice].also { it.performScrollTo() }

    /** Texto escrito en un campo. */
    private fun textoDe(nodo: SemanticsNodeInteraction): String =
        nodo.fetchSemanticsNode().config[SemanticsProperties.EditableText].text

    /** Espera a que el indicador marque ese paso como el actual. */
    private fun esperarPaso(paso: Int) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithContentDescription("Paso $paso de 4", substring = true)
                .fetchSemanticsNodes()
                .any { nodo ->
                    nodo.config[SemanticsProperties.ContentDescription].any { it.endsWith(", actual") }
                }
        }
    }

    /**
     * Toca el elemento que se puede tocar con ese texto. Se busca por texto (no por descripción)
     * para tocar el botón «Volver» de abajo y no la flecha de arriba, que también dice «Volver».
     */
    private fun tocar(texto: String) {
        val conTexto = hasText(texto) and hasClickAction()
        compose.onNode(conTexto).performSemanticsAction(SemanticsActions.OnClick)
    }

    /** Espera hasta 10 segundos a que aparezca un texto en pantalla. */
    private fun esperarTexto(texto: String, substring: Boolean = false) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto, substring = substring).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Espera hasta 10 segundos a que un texto deje de estar en pantalla. */
    private fun esperarQueDesaparezca(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isEmpty()
        }
    }

    private companion object {
        const val TITULO = 0
        const val DESCRIPCION = 1
        const val PRECIO = 2
    }
}
