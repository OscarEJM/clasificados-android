package com.univalle.clasificados

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.univalle.clasificados.ui.pantallas.InicioSesionScreen
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-05 · Iniciar sesión sobre la pantalla 01 (criterios 2 a 4). Usan el usuario de
 * prueba de local.properties (PRUEBA_CORREO) y no crean cuentas. El criterio 1 (entrar y llegar
 * a Inicio) está en SesionPersistenteTest porque necesita la app completa.
 */
@RunWith(AndroidJUnit4::class)
class InicioSesionScreenTest {

    @get:Rule
    val compose = createComposeRule()

    private val auth = FirebaseAuth.getInstance()

    @Before
    fun dibujarInicioSesion() {
        auth.signOut()
        compose.setContent { ClasificadosTheme { InicioSesionScreen(alRegistrarse = {}) } }
    }

    @After
    fun cerrarSesion() = auth.signOut()

    /** Criterio 2: con un correo sin formato aparece «Ingresa un correo válido». */
    @Test
    fun correoInvalido_muestraCorreoInvalido() {
        campo(CORREO).performTextInput("prueba.com")
        campo(CONTRASENA).performTextInput("123456")
        tocarIniciarSesion()
        compose.onNodeWithText("Ingresa un correo válido").assertExists()
    }

    /** Criterio 3: con la contraseña vacía aparece «La contraseña es obligatoria». */
    @Test
    fun contrasenaVacia_muestraContrasenaObligatoria() {
        campo(CORREO).performTextInput("ana@correo.com")
        tocarIniciarSesion()
        compose.onNodeWithText("La contraseña es obligatoria").assertExists()
    }

    /** Criterio 4: con la contraseña equivocada aparece «Correo o contraseña incorrectos» y no entra. */
    @Test
    fun contrasenaIncorrecta_muestraCredencialesIncorrectas() {
        val correo = correoDePrueba()
        campo(CORREO).performTextInput(correo)
        campo(CONTRASENA).performTextInput("clave-equivocada-1")
        tocarIniciarSesion()
        esperarTexto("Correo o contraseña incorrectos")
        assertNull(auth.currentUser)
    }

    /** Criterio 4: con un correo que no existe aparece «Correo o contraseña incorrectos» y no entra. */
    @Test
    fun correoInexistente_muestraCredencialesIncorrectas() {
        campo(CORREO).performTextInput("no.existe.${System.currentTimeMillis()}@clasificados.com")
        campo(CONTRASENA).performTextInput("123456")
        tocarIniciarSesion()
        esperarTexto("Correo o contraseña incorrectos")
        assertNull(auth.currentUser)
    }

    /** Correo del usuario de prueba; si no está configurado, la prueba se salta. */
    private fun correoDePrueba(): String {
        val correo = InstrumentationRegistry.getArguments().getString("correoPrueba").orEmpty()
        assumeTrue("Falta PRUEBA_CORREO en local.properties", correo.isNotBlank())
        return correo
    }

    /** Campo de texto por su posición en la pantalla (0 = correo, 1 = contraseña). */
    private fun campo(posicion: Int): SemanticsNodeInteraction =
        compose.onAllNodes(hasSetTextAction())[posicion]

    /**
     * Activa el botón «Iniciar sesión» directamente (no con un toque en pantalla), porque el
     * teclado o una sugerencia de autocompletar pueden quedar encima y tragarse el toque.
     */
    private fun tocarIniciarSesion() {
        compose.onNode(hasText("Iniciar sesión") and hasClickAction())
            .performSemanticsAction(SemanticsActions.OnClick)
    }

    /** Espera hasta 10 segundos a que aparezca un texto exacto en pantalla. */
    private fun esperarTexto(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private companion object {
        const val CORREO = 0
        const val CONTRASENA = 1
    }
}
