package com.univalle.clasificados

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-10 · Cerrar sesión (RF-08) con la app completa. Usan el usuario de prueba de
 * local.properties (PRUEBA_CORREO y PRUEBA_CONTRASENA); si no está, se saltan.
 */
@RunWith(AndroidJUnit4::class)
class CerrarSesionTest {

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

    /** Criterio 1: al tocar «Cerrar sesión» en Ajustes se ve Inicio de sesión y ya no hay sesión. */
    @Test
    fun cerrarSesion_llevaAInicioDeSesion() {
        ActivityScenario.launch(MainActivity::class.java).use {
            cerrarSesionDesdeAjustes()
            assertNull("La sesión debería estar cerrada", auth.currentUser)
        }
    }

    /** Criterio 2: después de cerrar sesión, al cerrar y volver a abrir la app pide correo y contraseña. */
    @Test
    fun alReabrir_pideCredenciales() {
        ActivityScenario.launch(MainActivity::class.java).use { cerrarSesionDesdeAjustes() }
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Inicia sesión")
            esperarTexto("Iniciar sesión")
            assertEquals(0, compose.onAllNodesWithText("Ver todas").fetchSemanticsNodes().size)
        }
    }

    /** Criterio 3: después de cerrar sesión, «atrás» en Inicio de sesión no vuelve a las pantallas internas. */
    @Test
    fun atras_noVuelveAPantallasInternas() {
        ActivityScenario.launch(MainActivity::class.java).use { escenario ->
            cerrarSesionDesdeAjustes()
            // Inicio de sesión es la única pantalla que queda: «atrás» cierra la app.
            Espresso.pressBackUnconditionally()
            val limite = System.currentTimeMillis() + 5_000
            while (escenario.state != Lifecycle.State.DESTROYED && System.currentTimeMillis() < limite) {
                Thread.sleep(100)
            }
            assertEquals(Lifecycle.State.DESTROYED, escenario.state)
        }
    }

    /** Desde Inicio abre Ajustes, toca «Cerrar sesión» y espera la pantalla Inicio de sesión. */
    private fun cerrarSesionDesdeAjustes() {
        esperarTexto("Ver todas")
        tocar("Ajustes")
        esperarTexto("CUENTA")
        tocar("Cerrar sesión")
        esperarTexto("Inicia sesión")
        assertEquals(0, compose.onAllNodesWithText("CUENTA").fetchSemanticsNodes().size)
    }

    /** Toca el elemento que se puede tocar con ese texto. */
    private fun tocar(texto: String) {
        compose.onNode(hasText(texto) and hasClickAction()).performSemanticsAction(SemanticsActions.OnClick)
    }

    /** Espera hasta 10 segundos a que aparezca un texto exacto en pantalla. */
    private fun esperarTexto(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
