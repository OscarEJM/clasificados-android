package com.univalle.clasificados.ui.navegacion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.univalle.clasificados.ui.componentes.BarraInferior
import com.univalle.clasificados.ui.componentes.BotonPublicar
import com.univalle.clasificados.ui.pantallas.AjustesScreen
import com.univalle.clasificados.ui.pantallas.BienvenidaScreen
import com.univalle.clasificados.ui.pantallas.InicioScreen
import com.univalle.clasificados.ui.pantallas.InicioSesionScreen
import com.univalle.clasificados.ui.pantallas.MisAnunciosScreen
import com.univalle.clasificados.ui.pantallas.PublicarScreen
import com.univalle.clasificados.ui.pantallas.RegistroScreen
import com.univalle.clasificados.viewmodel.EstadoSesion
import com.univalle.clasificados.viewmodel.SesionViewModel

/**
 * Carcasa de la aplicación: Scaffold, NavHost y reglas de sesión (HU-03).
 * - Arranca en la pantalla 00 · Bienvenida. Cuando [SesionViewModel] termina de revisar la
 *   sesión, va a Inicio (hay sesión, RF-02) o a Inicio de sesión (no hay, RF-02) y quita la
 *   bienvenida de la pila para que «atrás» no vuelva a ella.
 * - Protege las rutas (RF-09): si no hay sesión y la pantalla no es pública, lleva a Inicio de sesión.
 * - La barra inferior y el botón flotante «Publicar» solo se ven en las pantallas privadas.
 *   El botón flota sobre el contenido; las listas con scroll dejan EspacioBotonPublicar al final.
 *
 * @param sesionViewModel estado de la sesión, compartido por toda la app.
 */
@Composable
fun NavegacionApp(sesionViewModel: SesionViewModel = viewModel()) {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val estadoSesion by sesionViewModel.estado.collectAsStateWithLifecycle()
    val conBarra = rutaActual in Rutas.CON_BARRA

    LaunchedEffect(estadoSesion, rutaActual) {
        aplicarReglasDeSesion(navController, estadoSesion, rutaActual)
    }

    // Navega a una pestaña sin apilar pantallas repetidas y conservando el estado de cada una.
    val irA: (String) -> Unit = { ruta ->
        navController.navigate(ruta) {
            popUpTo(Rutas.INICIO) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (conBarra) BarraInferior(rutaActual = rutaActual, alSeleccionar = irA)
        },
        floatingActionButton = {
            if (conBarra) {
                // El Scaffold deja 16 dp sobre la barra; se baja 9 dp para quedar a 7 dp como en Figma.
                Box(Modifier.offset(y = 9.dp)) {
                    BotonPublicar(alTocar = { irA(Rutas.PUBLICAR) })
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.BIENVENIDA
        ) {
            // La bienvenida ocupa toda la pantalla (su fondo va detrás de la barra de estado).
            composable(Rutas.BIENVENIDA) {
                // El segundo mínimo cuenta desde que la 00 aparece, no desde que abre la app.
                LaunchedEffect(Unit) { sesionViewModel.contarTiempoBienvenida() }
                BienvenidaScreen()
            }
            composable(Rutas.INICIO_SESION) {
                Box(Modifier.padding(innerPadding)) {
                    InicioSesionScreen(alRegistrarse = { navController.navigate(Rutas.REGISTRO) })
                }
            }
            composable(Rutas.REGISTRO) {
                Box(Modifier.padding(innerPadding)) {
                    RegistroScreen(alVolver = { navController.popBackStack() })
                }
            }
            composable(Rutas.INICIO) { Box(Modifier.padding(innerPadding)) { InicioScreen() } }
            composable(Rutas.PUBLICAR) { Box(Modifier.padding(innerPadding)) { PublicarScreen() } }
            composable(Rutas.MIS_ANUNCIOS) { Box(Modifier.padding(innerPadding)) { MisAnunciosScreen() } }
            composable(Rutas.AJUSTES) { Box(Modifier.padding(innerPadding)) { AjustesScreen() } }
        }
    }
}

/**
 * Decide a dónde mover al usuario según la sesión (RF-01, RF-02, RF-09 y RN-04).
 * - Desde la bienvenida: a Inicio si hay sesión, si no a Inicio de sesión.
 * - Sin sesión en una pantalla privada: a Inicio de sesión.
 * - Con sesión en Inicio de sesión o Registro (acaba de entrar o de crear su cuenta): a Inicio.
 * En todos los casos se limpia la pila de navegación para que «atrás» no regrese.
 */
private fun aplicarReglasDeSesion(
    navController: NavHostController,
    estado: EstadoSesion,
    rutaActual: String?
) {
    if (estado == EstadoSesion.VERIFICANDO || rutaActual == null) return
    val destino = when {
        rutaActual == Rutas.BIENVENIDA ->
            if (estado == EstadoSesion.CON_SESION) Rutas.INICIO else Rutas.INICIO_SESION
        estado == EstadoSesion.SIN_SESION && rutaActual !in Rutas.PUBLICAS -> Rutas.INICIO_SESION
        estado == EstadoSesion.CON_SESION &&
            (rutaActual == Rutas.INICIO_SESION || rutaActual == Rutas.REGISTRO) -> Rutas.INICIO
        else -> return
    }
    navController.navigate(destino) {
        popUpTo(navController.graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
