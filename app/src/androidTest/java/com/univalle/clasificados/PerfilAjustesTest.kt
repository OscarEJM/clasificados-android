package com.univalle.clasificados

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.univalle.clasificados.data.repository.AuthRepository
import com.univalle.clasificados.util.Iniciales
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Rule
import org.junit.Assume.assumeTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-09 · Ver mi perfil en Ajustes (RF-33), con la app completa y Firebase real.
 * Usan las dos cuentas de prueba de local.properties (PRUEBA_… y PRUEBA2_…); si faltan, se saltan.
 * El nombre esperado se lee directamente de Firestore, sin pasar por la app.
 */
@RunWith(AndroidJUnit4::class)
class PerfilAjustesTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val auth = FirebaseAuth.getInstance()
    private val argumentos = InstrumentationRegistry.getArguments()

    @After
    fun cerrarSesion() = auth.signOut()

    /** Criterio 1: con sesión iniciada, Ajustes muestra el nombre, el correo y las iniciales. */
    @Test
    fun ajustesMuestraNombreYCorreo() {
        val cuenta = cuenta("correoPrueba", "contrasenaPrueba")
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirAjustes()
            verPerfil(cuenta)
        }
    }

    /** Criterio 2: al cerrar sesión y entrar con otra cuenta, Ajustes muestra los datos de la nueva. */
    @Test
    fun otraCuenta_muestraSusDatos() {
        val primera = cuenta("correoPrueba", "contrasenaPrueba")
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirAjustes()
            verPerfil(primera)

            // Cerrar sesión y entrar con la segunda cuenta (el botón «Cerrar sesión» llega en HU-10).
            auth.signOut()
            esperarTexto("Inicia sesión")
            val segunda = cuenta("correoPrueba2", "contrasenaPrueba2")

            abrirAjustes()
            verPerfil(segunda)
            esperarQueDesaparezca(primera.correo)
        }
    }

    /** Datos de una cuenta de prueba: correo, nombre guardado en Firestore e iniciales. */
    private data class Cuenta(val correo: String, val nombre: String, val iniciales: String)

    /** Inicia sesión con la cuenta de esos argumentos y lee su nombre de `usuarios/{uid}`. */
    private fun cuenta(argCorreo: String, argContrasena: String): Cuenta = runBlocking {
        val correo = argumentos.getString(argCorreo).orEmpty()
        val contrasena = argumentos.getString(argContrasena).orEmpty()
        assumeTrue("Falta la cuenta de prueba en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        val uid = auth.signInWithEmailAndPassword(correo, contrasena).await().user!!.uid
        val nombre = FirebaseFirestore.getInstance().collection(AuthRepository.COLECCION_USUARIOS)
            .document(uid).get(Source.SERVER).await().getString("nombre").orEmpty()
        Cuenta(correo, nombre, Iniciales.de(nombre, correo))
    }

    /** Espera Inicio y toca la pestaña «Ajustes». */
    private fun abrirAjustes() {
        esperarTexto("Ver todas")
        compose.onNode(hasText("Ajustes") and hasClickAction()).performSemanticsAction(SemanticsActions.OnClick)
        esperarTexto("APARIENCIA")
    }

    /** La tarjeta de perfil muestra el correo, las iniciales y el nombre (si la cuenta lo tiene). */
    private fun verPerfil(cuenta: Cuenta) {
        esperarTexto(cuenta.correo)
        esperarTexto(cuenta.iniciales)
        if (cuenta.nombre.isNotBlank()) esperarTexto(cuenta.nombre)
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
