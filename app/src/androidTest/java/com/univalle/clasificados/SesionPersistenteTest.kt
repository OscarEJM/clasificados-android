package com.univalle.clasificados

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
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
 * Pruebas de HU-03 · Bienvenida y sesión persistente y del criterio 1 de HU-05 · Iniciar sesión.
 * Se ejecutan en el emulador con internet:
 * ./gradlew connectedDebugAndroidTest
 *
 * Las pruebas con sesión usan un usuario de prueba creado por Oscar en Firebase, cuyo correo y
 * contraseña vienen de local.properties (PRUEBA_CORREO y PRUEBA_CONTRASENA). Si no están, esas
 * pruebas se saltan. Cada prueba deja la sesión cerrada al terminar.
 */
@RunWith(AndroidJUnit4::class)
class SesionPersistenteTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val auth = FirebaseAuth.getInstance()
    private val argumentos = InstrumentationRegistry.getArguments()

    @Before
    fun empezarSinSesion() = auth.signOut()

    @After
    fun cerrarSesion() = auth.signOut()

    /** Criterio 2: con sesión, después de la bienvenida la app llega a Inicio sin pedir credenciales. */
    @Test
    fun conSesion_llegaAInicio() {
        iniciarSesionDePrueba()
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Mis anuncios")
            compose.onNodeWithText("Inicia sesión").assertDoesNotExist()
        }
    }

    /** Criterio 3: sin sesión, después de la bienvenida la app llega a Inicio de sesión. */
    @Test
    fun sinSesion_llegaAInicioDeSesion() {
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Inicia sesión")
            compose.onNodeWithText("Mis anuncios").assertDoesNotExist()
        }
    }

    /** Criterio 4: si la sesión se cierra estando en una pantalla privada, la app lleva a Inicio de sesión. */
    @Test
    fun sinSesion_noPermitePantallasPrivadas() {
        iniciarSesionDePrueba()
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Mis anuncios")
            auth.signOut()
            esperarTexto("Inicia sesión")
            compose.onNodeWithText("Mis anuncios").assertDoesNotExist()
        }
    }

    /**
     * HU-05, criterio 1: sin sesión, al escribir el correo y la contraseña correctos en la
     * pantalla 01 y tocar «Iniciar sesión», la app llega a Inicio.
     */
    @Test
    fun credencialesCorrectas_llevanAInicio() {
        val correo = argumentos.getString("correoPrueba").orEmpty()
        val contrasena = argumentos.getString("contrasenaPrueba").orEmpty()
        assumeTrue("Falta PRUEBA_CORREO o PRUEBA_CONTRASENA en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Inicia sesión")
            compose.onAllNodes(hasSetTextAction())[0].performTextInput(correo)
            compose.onAllNodes(hasSetTextAction())[1].performTextInput(contrasena)
            compose.onNode(hasText("Iniciar sesión") and hasClickAction())
                .performSemanticsAction(SemanticsActions.OnClick)
            esperarTexto("Mis anuncios")
        }
    }

    /** Inicia sesión con el usuario de prueba; si no está configurado, la prueba se salta. */
    private fun iniciarSesionDePrueba() = runBlocking {
        val correo = argumentos.getString("correoPrueba").orEmpty()
        val contrasena = argumentos.getString("contrasenaPrueba").orEmpty()
        assumeTrue("Falta PRUEBA_CORREO o PRUEBA_CONTRASENA en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        auth.signInWithEmailAndPassword(correo, contrasena).await()
    }

    /** Espera hasta 10 segundos a que aparezca un texto exacto en pantalla. */
    private fun esperarTexto(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
