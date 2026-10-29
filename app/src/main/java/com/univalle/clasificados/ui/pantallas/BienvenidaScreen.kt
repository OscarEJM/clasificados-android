package com.univalle.clasificados.ui.pantallas

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.univalle.clasificados.R

/**
 * Pantalla 00 · Bienvenida de Figma (RF-01). Se muestra al abrir la app mientras
 * SesionViewModel revisa si hay sesión; la navegación decide a dónde seguir. No tiene lógica:
 * fondo primario que llega hasta detrás de la barra de estado, logo de 88 dp con esquinas de
 * 26 dp, «Clasificados», el lema y «Cargando…» abajo. En modo oscuro toma los tokens oscuros.
 */
@Composable
fun BienvenidaScreen() {
    IconosClarosEnBarraDeEstado()
    val colores = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colores.primary)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        // En Figma el bloque central se centra en el espacio que queda encima del pie (55 dp).
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 55.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(colores.surface, RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_categorias),
                    contentDescription = null,
                    tint = colores.primary,
                    modifier = Modifier.size(44.dp)
                )
            }
            Text(
                text = stringResource(R.string.app_name),
                color = colores.onPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.bienvenida_lema),
                color = colores.onPrimary.copy(alpha = 0.85f),
                fontSize = 15.sp
            )
        }
        Text(
            text = stringResource(R.string.bienvenida_cargando),
            color = colores.onPrimary.copy(alpha = 0.7f),
            fontSize = 13.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 40.dp),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Pone blancos los íconos de la barra de estado mientras se ve la bienvenida (fondo azul, como
 * en Figma) y deja la configuración anterior al salir de la pantalla.
 */
@Composable
private fun IconosClarosEnBarraDeEstado() {
    val vista = LocalView.current
    if (vista.isInEditMode) return
    DisposableEffect(vista) {
        val ventana = (vista.context as Activity).window
        val controlador = WindowCompat.getInsetsController(ventana, vista)
        val anterior = controlador.isAppearanceLightStatusBars
        controlador.isAppearanceLightStatusBars = false
        onDispose { controlador.isAppearanceLightStatusBars = anterior }
    }
}
