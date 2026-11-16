package com.univalle.clasificados

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.univalle.clasificados.ui.navegacion.NavegacionApp
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.viewmodel.TemaViewModel

/**
 * Única actividad de la app. Activa el modo de pantalla completa (edge-to-edge) para que
 * el contenido respete el área segura (RNF-10) y muestra la navegación con el tema de Figma.
 *
 * El tema claro u oscuro sale de [TemaViewModel], que lee la preferencia guardada en el teléfono
 * (HU-11 · RF-32). Se lee antes de dibujar la primera pantalla, así no hay parpadeo del tema
 * contrario; al cambiarla en Ajustes, Compose redibuja toda la app sin reiniciarla (RF-31).
 * contrario; al cambiarla en Ajustes, Compose redibuja toda la app sin reiniciarla (RF-31).
 */
class MainActivity : ComponentActivity() {

    /** Tema elegido por el usuario, compartido por toda la app. */
    private val tema: TemaViewModel by viewModels()

    /**
     * Deja las barras del sistema transparentes (cada pantalla pinta su fondo detrás, por ejemplo
     * el azul de la 00 · Bienvenida), elige el color de sus íconos según el tema y dibuja la app.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Muestra el arranque con el diseño de la pantalla 00 (Theme.Clasificados.Arranque).
        // Debe llamarse antes de super.onCreate para que funcione igual en Android 8 a 16.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        aplicarTemaAlSistema(tema.modoOscuro.value)
        setContent {
            val modoOscuro by tema.modoOscuro.collectAsStateWithLifecycle()
            // Cada vez que cambia el tema, se ajustan también las barras del sistema y el arranque.
            LaunchedEffect(modoOscuro) { aplicarTemaAlSistema(modoOscuro) }
            ClasificadosTheme(darkTheme = modoOscuro) {
                NavegacionApp()
            }
        }
    }

    /**
     * Ajusta lo que está fuera de Compose: el color de los íconos de la barra de estado y de
     * navegación (claros sobre fondo oscuro y al revés) y el arranque de la próxima apertura.
     */
    private fun aplicarTemaAlSistema(modoOscuro: Boolean) {
        val transparente = Color.TRANSPARENT
        val estiloBarras = if (modoOscuro) {
            SystemBarStyle.dark(transparente)
        } else {
            SystemBarStyle.light(transparente, transparente)
        }
        enableEdgeToEdge(statusBarStyle = estiloBarras, navigationBarStyle = estiloBarras)
        recordarArranqueSegunTema(modoOscuro)
    }

    /**
     * Android dibuja el arranque antes de que la app se ejecute, así que no puede leer la
     * preferencia en ese momento. En Android 13 o superior la app le deja dicho a Android qué
     * versión de la pantalla 00 (clara u oscura) mostrar la próxima vez que se abra. En Android 8
     * a 12 no existe esta opción y el arranque sigue el modo oscuro del teléfono
     * (values-night/themes.xml); acordado con Oscar.
     */
    private fun recordarArranqueSegunTema(modoOscuro: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            splashScreen.setSplashScreenTheme(
                if (modoOscuro) R.style.Theme_Clasificados_Arranque_Oscuro
                else R.style.Theme_Clasificados_Arranque_Claro
            )
        }
    }
}
