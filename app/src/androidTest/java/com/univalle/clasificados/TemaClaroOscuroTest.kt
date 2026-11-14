package com.univalle.clasificados

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.univalle.clasificados.data.repository.PreferenciasRepository
import com.univalle.clasificados.ui.theme.FondoClaro
import com.univalle.clasificados.ui.theme.FondoOscuro
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Pruebas de HU-11 · Cambiar entre tema claro y oscuro (RF-31, RF-32, RNF-06) con la app completa.
 * Para saber en qué tema está la app se mira el color del fondo de la pantalla (token «fondo» de
 * Figma: #F9FAFB en claro y #0B0F19 en oscuro). Cada prueba empieza como una instalación nueva
 * (sin preferencia guardada) y al terminar deja el tema claro.
 */
@RunWith(AndroidJUnit4::class)
class TemaClaroOscuroTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val auth = FirebaseAuth.getInstance()
    private val contexto: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun prepararComoInstalacionNueva() = runBlocking {
        borrarPreferencias()
        val argumentos = InstrumentationRegistry.getArguments()
        val correo = argumentos.getString("correoPrueba").orEmpty()
        val contrasena = argumentos.getString("contrasenaPrueba").orEmpty()
        assumeTrue("Falta PRUEBA_CORREO o PRUEBA_CONTRASENA en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        auth.signInWithEmailAndPassword(correo, contrasena).await()
        Unit
    }

    @After
    fun dejarTemaClaro() {
        borrarPreferencias()
        auth.signOut()
    }

    /** Criterio 1: el interruptor de Ajustes cambia toda la app al tema oscuro al instante, y de vuelta. */
    @Test
    fun interruptor_cambiaElTemaAlInstante() {
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirAjustes()
            esperarFondo(FondoClaro)

            compose.onNode(isToggleable()).performClick()
            compose.onNode(isToggleable()).assertIsOn()
            esperarFondo(FondoOscuro)

            // Toda la app: Inicio también se ve en oscuro, sin reiniciar.
            tocar("Inicio")
            esperarTexto("Ver todas")
            esperarFondo(FondoOscuro)

            // Y se puede volver al claro.
            abrirAjustes()
            compose.onNode(isToggleable()).performClick()
            compose.onNode(isToggleable()).assertIsOff()
            esperarFondo(FondoClaro)
        }
    }

    /** Criterio 2: después de activar el modo oscuro, al cerrar y abrir la app inicia en oscuro. */
    @Test
    fun alReabrir_conservaElModoOscuro() {
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirAjustes()
            compose.onNode(isToggleable()).performClick()
            esperarFondo(FondoOscuro)
        }
        // Al cerrar la app el proceso puede seguir vivo; se olvida la instancia para leer el archivo.
        PreferenciasRepository.olvidarInstancia()
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Ver todas")
            esperarFondo(FondoOscuro)
            abrirAjustes()
            compose.onNode(isToggleable()).assertIsOn()
        }
    }

    /** Criterio 3: sin preferencia guardada (primera instalación), la app se ve en tema claro. */
    @Test
    fun primeraVez_temaClaro() {
        ActivityScenario.launch(MainActivity::class.java).use {
            esperarTexto("Ver todas")
            esperarFondo(FondoClaro)
            abrirAjustes()
            compose.onNode(isToggleable()).assertIsOff()
        }
    }

    /**
     * Criterio 4: el archivo de preferencias de la app solo tiene la clave «modo_oscuro». Los demás
     * archivos de preferencias son de Firebase (la sesión iniciada, HU-03), no del código de la app.
     */
    @Test
    fun soloSeGuardaLaPreferenciaDeTema() {
        ActivityScenario.launch(MainActivity::class.java).use {
            abrirAjustes()
            compose.onNode(isToggleable()).performClick()
            esperarFondo(FondoOscuro)
        }
        val guardado = contexto.getSharedPreferences(PreferenciasRepository.ARCHIVO, Context.MODE_PRIVATE).all
        assertEquals(mapOf(PreferenciasRepository.CLAVE_MODO_OSCURO to true), guardado)

        val carpeta = File(contexto.applicationInfo.dataDir, "shared_prefs")
        val ajenos = carpeta.listFiles().orEmpty().map { it.name }
            .filter { it != "${PreferenciasRepository.ARCHIVO}.xml" }
        assertTrue("Archivos que no son de Firebase: $ajenos", ajenos.all { it.startsWith("com.google.firebase") })
        assertFalse("No debe haber contraseñas guardadas", guardado.keys.any { it.contains("contrasena") })
    }

    /** Borra la preferencia de tema y olvida la instancia, como en una instalación nueva. */
    private fun borrarPreferencias() {
        contexto.getSharedPreferences(PreferenciasRepository.ARCHIVO, Context.MODE_PRIVATE).edit().clear().commit()
        PreferenciasRepository.olvidarInstancia()
    }

    /** Espera Inicio y toca la pestaña «Ajustes». */
    private fun abrirAjustes() {
        esperarTexto("Ajustes")
        tocar("Ajustes")
        esperarTexto("APARIENCIA")
    }

    /**
     * Espera hasta 5 segundos a que el fondo de la pantalla tenga ese color. Se mira un punto del
     * borde izquierdo, a media altura, donde solo hay fondo.
     */
    private fun esperarFondo(color: Color) {
        compose.waitUntil(timeoutMillis = 5_000) {
            val imagen = compose.onRoot().captureToImage().toPixelMap()
            imagen[4, imagen.height / 2] == color
        }
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
