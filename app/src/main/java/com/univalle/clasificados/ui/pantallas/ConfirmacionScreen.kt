package com.univalle.clasificados.ui.pantallas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.componentes.BotonPrincipal
import com.univalle.clasificados.ui.componentes.BotonSecundario
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/**
 * Pantalla 12 · Confirmación (HU-14 · Publicar anuncio: ubicación, contacto y confirmación;
 * RF-27), según Figma: círculo verde de 96 dp con el visto blanco, «¡Anuncio publicado
 * correctamente!» (22 sp negrita), «Tu publicación ya está disponible para otros usuarios.»
 * (15 sp) y abajo los botones «Ver anuncio» (primario) y «Volver al inicio» (secundario).
 *
 * No lleva barra inferior ni flecha atrás. El «atrás» del teléfono lleva a Inicio, nunca de
 * vuelta al formulario (el anuncio ya se publicó).
 *
 * @param alVerAnuncio abre el detalle del anuncio recién publicado.
 * @param alVolverInicio abre la pantalla 03 · Inicio.
 */
@Composable
fun ConfirmacionScreen(alVerAnuncio: () -> Unit, alVolverInicio: () -> Unit) {
    BackHandler(onBack = alVolverInicio)
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier.size(96.dp).background(ClasificadosTheme.colores.exito, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Text(
                stringResource(R.string.confirmacion_titulo),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                stringResource(R.string.confirmacion_texto),
                color = ClasificadosTheme.colores.textoSecundario,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonPrincipal(
                texto = stringResource(R.string.confirmacion_ver_anuncio),
                cargando = false,
                alTocar = alVerAnuncio
            )
            BotonSecundario(texto = stringResource(R.string.confirmacion_volver_inicio), alTocar = alVolverInicio)
        }
    }
}
