package com.univalle.clasificados

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.univalle.clasificados.ui.pantallas.BienvenidaScreen
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba de la pantalla 00 · Bienvenida por sí sola (criterio 1 de HU-03): muestra el nombre
 * «Clasificados», el lema y «Cargando…». Que sea la primera pantalla al abrir la app lo garantiza
 * la navegación (startDestination = Bienvenida) y se revisa a simple vista en el emulador.
 */
@RunWith(AndroidJUnit4::class)
class BienvenidaScreenTest {

    @get:Rule
    val compose = createComposeRule()

    /** Tema claro: se ven los tres textos de la pantalla 00. */
    @Test
    fun temaClaro_muestraNombreLemaYCargando() = revisarTextos(oscuro = false)

    /** Tema oscuro: se ven los tres textos de la pantalla 00. */
    @Test
    fun temaOscuro_muestraNombreLemaYCargando() = revisarTextos(oscuro = true)

    /** Dibuja la bienvenida con el tema indicado y revisa sus tres textos. */
    private fun revisarTextos(oscuro: Boolean) {
        compose.setContent { ClasificadosTheme(darkTheme = oscuro) { BienvenidaScreen() } }
        compose.onNodeWithText("Clasificados").assertIsDisplayed()
        compose.onNodeWithText("Encuentra lo que buscas").assertIsDisplayed()
        compose.onNodeWithText("Cargando…").assertIsDisplayed()
    }
}
