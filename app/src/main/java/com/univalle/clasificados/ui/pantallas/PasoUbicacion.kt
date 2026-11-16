package com.univalle.clasificados.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.model.Ciudad
import com.univalle.clasificados.ui.componentes.CampoTexto
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.viewmodel.PublicarUiState
import com.univalle.clasificados.viewmodel.PublicarViewModel

/** Forma de la caja del selector de ciudad: esquinas de 12 dp, igual que «Campo de texto» de Figma. */
private val FormaCaja = RoundedCornerShape(12.dp)

/**
 * Paso 4 · Ubicación y contacto (pantallas 11 y 11b de Figma; HU-14 · Publicar anuncio:
 * ubicación, contacto y confirmación; RF-25).
 *
 * - «Ciudad *»: caja con la flecha hacia abajo que abre la lista de las 10 ciudades (sección 1.4).
 * - «Email *»: correo de contacto, prellenado con el de la cuenta (aprobado por Oscar).
 * - «Teléfono (opcional)»: prellenado con el del registro si existe (RN-11), con la ayuda de la 1.5.
 *
 * Los errores aparecen al tocar «Publicar anuncio». Mientras se publica (11b) los campos no se
 * pueden cambiar. Si la publicación falla, el mensaje se ve debajo de los campos en color *error*
 * (Figma no tiene pantalla para ese caso).
 */
@Composable
fun PasoUbicacion(estado: PublicarUiState, viewModel: PublicarViewModel) {
    val habilitado = !estado.publicando
    TituloPaso(stringResource(R.string.publicar_p4_titulo), stringResource(R.string.publicar_p4_subtitulo))
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SelectorCiudad(
            elegida = estado.borrador.ciudad,
            error = estado.errorCiudad,
            habilitado = habilitado,
            alElegir = viewModel::elegirCiudad
        )
        CampoTexto(
            etiqueta = stringResource(R.string.publicar_campo_correo),
            valor = estado.borrador.correo,
            alCambiar = viewModel::alCambiarCorreo,
            ejemplo = stringResource(R.string.publicar_campo_correo_ejemplo),
            icono = R.drawable.ic_correo,
            error = estado.errorCorreo,
            tipoTeclado = KeyboardType.Email,
            habilitado = habilitado
        )
        CampoTexto(
            etiqueta = stringResource(R.string.publicar_campo_telefono),
            valor = estado.borrador.telefono,
            alCambiar = viewModel::alCambiarTelefono,
            ejemplo = stringResource(R.string.publicar_campo_telefono_ejemplo),
            icono = R.drawable.ic_telefono,
            ayuda = stringResource(R.string.publicar_campo_telefono_ayuda),
            tipoTeclado = KeyboardType.Phone,
            habilitado = habilitado
        )
        estado.errorPublicar?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
        }
    }
}

/**
 * Selector «Ciudad *» de Figma: caja de 48 dp con borde y esquinas de 12 dp, el nombre de la
 * ciudad (o «Selecciona tu ciudad» en gris) y la flecha hacia abajo. Al tocarla se abre la lista
 * con las 10 ciudades en el orden de la sección 1.4. Con error, el borde y el mensaje en *error*.
 */
@Composable
private fun SelectorCiudad(
    elegida: Ciudad?,
    error: String?,
    habilitado: Boolean,
    alElegir: (Ciudad) -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val extra = ClasificadosTheme.colores
    var abierta by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            stringResource(R.string.publicar_campo_ciudad),
            color = colores.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(FormaCaja)
                    .background(colores.surface)
                    .border(1.dp, if (error != null) colores.error else extra.borde, FormaCaja)
                    .clickable(enabled = habilitado, role = Role.DropdownList) { abierta = true }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = elegida?.nombre ?: stringResource(R.string.publicar_campo_ciudad_ejemplo),
                    color = if (elegida != null) colores.onSurface else extra.textoSecundario,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_abajo),
                    contentDescription = null,
                    tint = colores.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            DropdownMenu(
                expanded = abierta,
                onDismissRequest = { abierta = false },
                containerColor = colores.surface
            ) {
                Ciudad.entries.forEach { ciudad ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                ciudad.nombre,
                                fontSize = 15.sp,
                                color = if (ciudad == elegida) colores.primary else colores.onSurface,
                                fontWeight = if (ciudad == elegida) FontWeight.SemiBold else FontWeight.Normal
                            )
                        },
                        onClick = {
                            alElegir(ciudad)
                            abierta = false
                        }
                    )
                }
            }
        }
        if (error != null) Text(error, color = colores.error, fontSize = 12.sp)
    }
}
