package com.univalle.clasificados.ui.navegacion

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.componentes.BarraInferior
import com.univalle.clasificados.ui.pantallas.PantallaMarcador

/**
 * Carcasa de la aplicación: Scaffold con barra inferior y NavHost.
 * El innerPadding del Scaffold incluye las barras del sistema, así el contenido respeta el área segura.
 */
@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BarraInferior(
                rutaActual = rutaActual,
                alSeleccionar = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.INICIO,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rutas.INICIO) {
                PantallaMarcador(stringResource(R.string.pestana_inicio))
            }
            composable(Rutas.PUBLICAR) {
                PantallaMarcador(stringResource(R.string.pestana_publicar))
            }
            composable(Rutas.MIS_ANUNCIOS) {
                PantallaMarcador(stringResource(R.string.pestana_mis_anuncios))
            }
            composable(Rutas.AJUSTES) {
                PantallaMarcador(stringResource(R.string.pestana_ajustes))
            }
        }
    }
}
