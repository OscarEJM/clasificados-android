package com.univalle.clasificados

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.univalle.clasificados.ui.navegacion.NavegacionApp
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/**
 * Única actividad de la app. Activa el modo de pantalla completa (edge-to-edge) para que
 * el contenido respete el área segura (RNF-10) y muestra la navegación con el tema de Figma.
 */
class MainActivity : ComponentActivity() {

    /** Tema actual. Por ahora se cambia a mano aquí; en HU-11 se leerá de SharedPreferences. */
    private val modoOscuro = false

    /**
     * Deja las barras del sistema transparentes (cada pantalla pinta su fondo detrás, por ejemplo
     * el azul de la 00 · Bienvenida), elige el color de sus íconos según el tema y dibuja la app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Muestra el arranque con el diseño de la pantalla 00 (Theme.Clasificados.Arranque).
        // Debe llamarse antes de super.onCreate para que funcione igual en Android 8 a 16.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        recordarArranqueSegunTema()
        val transparente = Color.TRANSPARENT
        val estiloBarras = if (modoOscuro) {
            SystemBarStyle.dark(transparente)
        } else {
            SystemBarStyle.light(transparente, transparente)
        }
        enableEdgeToEdge(statusBarStyle = estiloBarras, navigationBarStyle = estiloBarras)
        setContent {
            ClasificadosTheme(darkTheme = modoOscuro) {
                NavegacionApp()
            }
        }
    }

    /**
     * Android dibuja el arranque antes de que la app se ejecute, así que no puede leer [modoOscuro]
     * en ese momento. En Android 13 o superior la app le deja dicho a Android qué versión de la
     * pantalla 00 (clara u oscura) mostrar la próxima vez que se abra. En Android 8 a 12 no existe
     * esta opción y el arranque sigue el modo oscuro del teléfono (values-night/themes.xml).
     */
    private fun recordarArranqueSegunTema() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            splashScreen.setSplashScreenTheme(
                if (modoOscuro) R.style.Theme_Clasificados_Arranque_Oscuro
                else R.style.Theme_Clasificados_Arranque_Claro
            )
        }
    }
}
