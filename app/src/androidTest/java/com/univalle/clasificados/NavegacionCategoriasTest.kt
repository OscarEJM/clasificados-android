package com.univalle.clasificados

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-06 · Explorar categorías con la app completa: la navegación real entre
 * Inicio, Todas las categorías y el listado (marcador hasta HU-07). Usan el usuario de prueba
 * de local.properties (PRUEBA_CORREO y PRUEBA_CONTRASENA); si no está, se saltan.
 */
@RunWith(AndroidJUnit4::class)
class NavegacionCategoriasTest {

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

    /** Criterios 2 y 3: «Más categorías» abre la 04, y tocar una categoría ahí abre su listado. */
    @Test
    fun masCategorias_abreTodas_yLuegoElListado() {
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Más categorías")
            tocar("Más categorías")
            esperarTexto("Todas las categorías")
            esperarQueDesaparezca("Ver todas")
            tocar("Comunidad")
            esperarQueDesaparezca("Todas las categorías")
            esperarTexto("Comunidad")
            // La pestaña «Inicio» regresa a la pantalla 03 desde el listado.
            tocar("Inicio")
            esperarTexto("Ver todas")
        }
    }

    /** Criterios 2 y 3: «Ver todas» abre la 04, y desde Inicio una categoría abre su listado. */
    @Test
    fun verTodas_abreTodas_eInicioAbreElListado() {
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Ver todas")
            tocar("Ver todas")
            esperarTexto("Todas las categorías")
            tocar("Volver")
            esperarQueDesaparezca("Todas las categorías")
            esperarTexto("Ver todas")
            tocar("Vivienda")
            esperarQueDesaparezca("Ver todas")
            esperarTexto("Vivienda")
        }
    }

    /** Toca el elemento con ese texto o descripción (por ejemplo la flecha «Volver»). */
    private fun tocar(texto: String) {
        val conTexto = hasText(texto) and hasClickAction()
        val conDescripcion = hasContentDescription(texto) and hasClickAction()
        compose.onNode(conTexto or conDescripcion).performSemanticsAction(SemanticsActions.OnClick)
    }

    /** Espera hasta 10 segundos a que aparezca un texto exacto en pantalla. */
    private fun esperarTexto(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Espera hasta 10 segundos a que un texto deje de estar en pantalla. */
    private fun esperarQueDesaparezca(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isEmpty()
        }
    }
}
