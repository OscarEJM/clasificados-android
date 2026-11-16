package com.univalle.clasificados.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/** Nombres de los cuatro pasos de Publicar, en orden (Figma, componente «Indicador de pasos»). */
private val NombresPasos = listOf(
    R.string.publicar_paso_categoria,
    R.string.publicar_paso_detalles,
    R.string.publicar_paso_fotos,
    R.string.publicar_paso_ubicacion
)

/**
 * Componente «Indicador de pasos» de Figma (pantallas 08 a 11, HU-12 · RF-21): cuatro círculos
 * de 32 dp con su nombre debajo (11 sp), del mismo ancho cada uno.
 * - Paso terminado: círculo color *éxito* con el ícono de check blanco.
 * - Paso actual: círculo primario con el número blanco y el nombre en primario seminegrita.
 * - Paso pendiente: círculo superficie-variante con el número y el nombre en gris.
 *
 * @param pasoActual paso en el que está el usuario, de 1 a 4.
 */
@Composable
fun IndicadorPasos(pasoActual: Int, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    val extra = ClasificadosTheme.colores
    Row(modifier = modifier.fillMaxWidth()) {
        NombresPasos.forEachIndexed { indice, nombre ->
            val numero = indice + 1
            val terminado = numero < pasoActual
            val actual = numero == pasoActual
            val textoNombre = stringResource(nombre)
            Column(
                modifier = Modifier
                    .weight(1f)
                    // Para lectores de pantalla y pruebas: «Paso 2 de 4, Detalles, actual».
                    .semantics {
                        contentDescription = "Paso $numero de 4, $textoNombre" +
                            when { actual -> ", actual"; terminado -> ", completado"; else -> "" }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            when {
                                terminado -> extra.exito
                                actual -> colores.primary
                                else -> colores.surfaceVariant
                            },
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (terminado) {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            contentDescription = null,
                            tint = colores.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            text = numero.toString(),
                            color = if (actual) colores.onPrimary else extra.textoSecundario,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = textoNombre,
                    color = if (actual) colores.primary else extra.textoSecundario,
                    fontSize = 11.sp,
                    fontWeight = if (actual) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}
