package com.univalle.clasificados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.univalle.clasificados.ui.navegacion.NavegacionApp
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.ui.theme.FondoClaro
import com.univalle.clasificados.ui.theme.FondoOscuro

/**
 * Única actividad de la app. Activa el modo de pantalla completa (edge-to-edge) para que
 * el contenido respete el área segura (RNF-10) y muestra la navegación con el tema de Figma.
 */
class MainActivity : ComponentActivity() {

    /** Tema actual. Por ahora se cambia a mano aquí; en HU-11 se leerá de SharedPreferences. */
    private val modoOscuro = false

    /** Configura las barras del sistema con el color de fondo del tema y dibuja la app. */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val estiloBarras = if (modoOscuro) {
            SystemBarStyle.dark(FondoOscuro.toArgb())
        } else {
            SystemBarStyle.light(FondoClaro.toArgb(), FondoOscuro.toArgb())
        }
        enableEdgeToEdge(statusBarStyle = estiloBarras, navigationBarStyle = estiloBarras)
        setContent {
            ClasificadosTheme(darkTheme = modoOscuro) {
                NavegacionApp()
            }
        }
    }
}
