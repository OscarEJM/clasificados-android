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

class MainActivity : ComponentActivity() {

    // Por ahora el tema se cambia a mano aquí. En HU-11 se leerá de SharedPreferences.
    private val modoOscuro = false

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
