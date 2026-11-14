package com.univalle.clasificados.ui.pantallas

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.BuildConfig
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.componentes.EstadoCargando
import com.univalle.clasificados.ui.componentes.EstadoSinConexion
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.viewmodel.AjustesViewModel
import com.univalle.clasificados.viewmodel.EstadoPerfil

/** Forma de las tarjetas de Ajustes en Figma: esquinas de 20 dp. */
private val FormaTarjeta = RoundedCornerShape(20.dp)

/** Alto que ocupa la tarjeta de perfil de Figma (52 dp de avatar + 16 dp arriba y abajo + borde). */
private val AltoTarjetaPerfil = 86.dp

/**
 * Pantalla 16 · Ajustes (HU-09 · Ver mi perfil en Ajustes, RF-33), según Figma.
 *
 * Arriba, la tarjeta de perfil con las iniciales, el nombre y el correo de la cuenta con sesión.
 * Debajo, «Apariencia» con el interruptor «Modo oscuro» y «Cuenta» con «Cerrar sesión»: se ven
 * como en Figma, pero se conectan en HU-11 y HU-10 (acordado con Oscar). El interruptor muestra
 * el tema que está activo. Al final, «Clasificados · Versión …».
 *
 * Mientras llega el nombre se ve el circulito azul en lugar de la tarjeta, y sin internet el
 * mensaje de RN-18 (acordado con Oscar).
 */
@Composable
fun AjustesScreen(viewModel: AjustesViewModel = viewModel()) {
    val perfil by viewModel.perfil.collectAsStateWithLifecycle()
    val colores = ClasificadosTheme.colores

    Column(modifier = Modifier.fillMaxSize()) {
        // Barra superior de Figma: solo el título, 56 dp, 16 dp a la izquierda.
        Box(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(start = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(R.string.pestana_ajustes),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            when (val actual = perfil) {
                is EstadoPerfil.Listo -> TarjetaPerfil(actual)
                EstadoPerfil.Cargando -> EstadoCargando(
                    stringResource(R.string.ajustes_cargando),
                    Modifier.height(AltoTarjetaPerfil)
                )
                EstadoPerfil.Error -> EstadoSinConexion(Modifier.height(AltoTarjetaPerfil))
            }

            Seccion(stringResource(R.string.ajustes_apariencia)) {
                // Figma: 14 dp arriba y abajo de un interruptor de 32 dp (60 dp en total). El
                // interruptor ya ocupa 48 dp por su zona táctil (RNF-12), así que se dejan 6 dp.
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    IconoAjuste(R.drawable.ic_luna, MaterialTheme.colorScheme.onSurface)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            stringResource(R.string.ajustes_modo_oscuro),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            stringResource(R.string.ajustes_modo_oscuro_ayuda),
                            color = colores.textoSecundario,
                            fontSize = 12.sp
                        )
                    }
                    // Muestra el tema activo; cambiarlo es de HU-11.
                    Switch(
                        checked = colores.oscuro,
                        onCheckedChange = {},
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MaterialTheme.colorScheme.primary,
                            checkedBorderColor = MaterialTheme.colorScheme.primary,
                            uncheckedThumbColor = colores.textoSecundario,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedBorderColor = colores.textoSecundario
                        )
                    )
                }
            }

            Seccion(stringResource(R.string.ajustes_cuenta)) {
                // «Cerrar sesión» se conecta en HU-10.
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    IconoAjuste(R.drawable.ic_salir, MaterialTheme.colorScheme.error)
                    Text(
                        stringResource(R.string.ajustes_cerrar_sesion),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.weight(1f))
            // Versión al final; el espacio de abajo deja libre el botón «Publicar», como en Figma (80 + 12 dp).
            Text(
                text = stringResource(R.string.ajustes_version, BuildConfig.VERSION_NAME),
                color = colores.textoSecundario,
                fontSize = 12.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 92.dp)
            )
        }
    }
}

/**
 * Tarjeta de perfil de Figma: avatar circular de 52 dp en primario-suave con las iniciales en
 * primario, y al lado el nombre (16 sp) y el correo (13 sp). Sin nombre guardado, solo el correo.
 */
@Composable
private fun TarjetaPerfil(perfil: EstadoPerfil.Listo) {
    Row(
        modifier = Modifier.fillMaxWidth().tarjeta().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(ClasificadosTheme.colores.primarioSuave, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                perfil.iniciales,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (perfil.nombre.isNotBlank()) {
                Text(
                    perfil.nombre,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                perfil.correo,
                color = ClasificadosTheme.colores.textoSecundario,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Sección de Figma: título en mayúsculas de 12 sp y una tarjeta con su contenido. */
@Composable
private fun Seccion(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            titulo,
            color = ClasificadosTheme.colores.textoSecundario,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Column(modifier = Modifier.fillMaxWidth().tarjeta(), content = contenido)
    }
}

/** Fondo superficie, borde de 1 dp y esquinas de 20 dp, como las tarjetas de Figma. */
@Composable
private fun Modifier.tarjeta(): Modifier = this
    .clip(FormaTarjeta)
    .background(MaterialTheme.colorScheme.surface)
    .border(1.dp, ClasificadosTheme.colores.borde, FormaTarjeta)

/** Ícono de 22 dp de las filas de Ajustes. */
@Composable
private fun IconoAjuste(@DrawableRes icono: Int, color: Color) {
    Icon(painterResource(icono), contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
}
