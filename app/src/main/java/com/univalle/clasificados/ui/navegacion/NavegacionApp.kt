package com.univalle.clasificados.ui.navegacion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.univalle.clasificados.ui.componentes.BarraInferior
import com.univalle.clasificados.ui.componentes.BotonPublicar
import com.univalle.clasificados.ui.pantallas.AjustesScreen
import com.univalle.clasificados.ui.pantallas.InicioScreen
import com.univalle.clasificados.ui.pantallas.MisAnunciosScreen
import com.univalle.clasificados.ui.pantallas.PublicarScreen

/**
 * Carcasa de la aplicación: Scaffold con barra inferior de tres pestañas, botón flotante
 * «Publicar» centrado y NavHost. El innerPadding del Scaffold incluye la barra y las barras del
 * sistema, así el contenido respeta el área segura. El botón flota sobre el contenido; las listas
 * con scroll dejan EspacioBotonPublicar al final para que no tape el último elemento.
 */
@Composable
fun NavegacionApp() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    // Navega a una pestaña sin apilar pantallas repetidas y conservando el estado de cada una.
    val irA: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BarraInferior(rutaActual = rutaActual, alSeleccionar = irA)
        },
        floatingActionButton = {
            // El Scaffold deja 16 dp sobre la barra; se baja 9 dp para quedar a 7 dp como en Figma.
            Box(Modifier.offset(y = 9.dp)) {
                BotonPublicar(alTocar = { irA(Rutas.PUBLICAR) })
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.INICIO,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Rutas.INICIO) { InicioScreen() }
            composable(Rutas.PUBLICAR) { PublicarScreen() }
            composable(Rutas.MIS_ANUNCIOS) { MisAnunciosScreen() }
            composable(Rutas.AJUSTES) { AjustesScreen() }
        }
    }
}
