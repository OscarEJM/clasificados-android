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
import com.univalle.clasificados.ui.pantallas.RegistroScreen
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import org.junit.After
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-04 · Registrarme sobre la pantalla 02 · Registro. Revisan los mensajes de los
 * criterios 2 a 5 y el de «Este correo ya está registrado» (criterio 6) usando el usuario de
 * prueba que ya existe en Firebase (PRUEBA_CORREO de local.properties), así que no crean cuentas.
 * El criterio 1 (crear una cuenta real) lo prueba Oscar en el emulador.
 */
@RunWith(AndroidJUnit4::class)
class RegistroScreenTest {

    @get:Rule
    val compose = createComposeRule()

    @Before
    fun dibujarRegistro() {
        FirebaseAuth.getInstance().signOut()
        compose.setContent { ClasificadosTheme { RegistroScreen(alVolver = {}) } }
    }

    @After
    fun cerrarSesion() = FirebaseAuth.getInstance().signOut()

    /** Criterio 2: con el nombre vacío aparece «El nombre es obligatorio». */
    @Test
    fun nombreVacio_muestraNombreObligatorio() {
        llenar(nombre = "", correo = "nuevo@correo.com", contrasena = "123456", confirmacion = "123456")
        tocarCrearCuenta()
        compose.onNodeWithText("El nombre es obligatorio").assertExists()
    }

    /** Criterio 3: con un correo sin formato aparece «Ingresa un correo válido». */
    @Test
    fun correoInvalido_muestraCorreoInvalido() {
        llenar(nombre = "Ana Gómez", correo = "anagomez.com", contrasena = "123456", confirmacion = "123456")
        tocarCrearCuenta()
        compose.onNodeWithText("Ingresa un correo válido").assertExists()
    }

    /** Criterio 4: con menos de 6 caracteres aparece el mensaje de contraseña corta. */
    @Test
    fun contrasenaCorta_muestraContrasenaCorta() {
        llenar(nombre = "Ana Gómez", correo = "ana@correo.com", contrasena = "123", confirmacion = "123")
        tocarCrearCuenta()
        compose.onNodeWithText("La contraseña debe tener al menos 6 caracteres").assertExists()
    }

    /** Criterio 5: si la confirmación es distinta aparece «Las contraseñas no coinciden». */
    @Test
    fun confirmacionDistinta_muestraNoCoinciden() {
        llenar(nombre = "Ana Gómez", correo = "ana@correo.com", contrasena = "123456", confirmacion = "654321")
        tocarCrearCuenta()
        compose.onNodeWithText("Las contraseñas no coinciden").assertExists()
    }

    /** Acordado con Oscar: el error de un campo se borra apenas el usuario lo corrige. */
    @Test
    fun alCorregirUnCampo_seBorraSuError() {
        tocarCrearCuenta()
        compose.onNodeWithText("El nombre es obligatorio").assertExists()
        campo(NOMBRE).performTextInput("Ana")
        compose.onNodeWithText("El nombre es obligatorio").assertDoesNotExist()
    }

    /** Criterio 6: con un correo que ya tiene cuenta aparece «Este correo ya está registrado». */
    @Test
    fun correoExistente_muestraCorreoRegistrado() {
        val correo = InstrumentationRegistry.getArguments().getString("correoPrueba").orEmpty()
        assumeTrue("Falta PRUEBA_CORREO en local.properties", correo.isNotBlank())
        llenar(nombre = "Prueba", correo = correo, contrasena = "otra-clave-1", confirmacion = "otra-clave-1")
        tocarCrearCuenta()
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText("Este correo ya está registrado").fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Escribe en los campos del formulario (el teléfono se deja vacío porque es opcional). */
    private fun llenar(nombre: String, correo: String, contrasena: String, confirmacion: String) {
        if (nombre.isNotEmpty()) campo(NOMBRE).performTextInput(nombre)
        campo(CORREO).performTextInput(correo)
        campo(CONTRASENA).performTextInput(contrasena)
        campo(CONFIRMACION).performTextInput(confirmacion)
    }

    /** Campo de texto por su posición en la pantalla (0 = nombre … 4 = confirmación). */
    private fun campo(posicion: Int): SemanticsNodeInteraction =
        compose.onAllNodes(hasSetTextAction())[posicion]

    /**
     * Activa el botón «Crear cuenta» (no el título de la barra superior, que dice lo mismo).
     * Se activa directamente y no con un toque en pantalla, porque el teclado o una sugerencia de
     * autocompletar de Android pueden quedar encima del botón y tragarse el toque.
     */
    private fun tocarCrearCuenta() {
        compose.onNode(hasText("Crear cuenta") and hasClickAction())
            .performSemanticsAction(SemanticsActions.OnClick)
    }

    private companion object {
        const val NOMBRE = 0
        const val CORREO = 1
        const val CONTRASENA = 3
        const val CONFIRMACION = 4
    }
}
