package com.univalle.clasificados.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/*
 * Estados de carga que comparten el listado de una categoría (pantalla 05, HU-07) y el detalle
 * del anuncio (pantalla 07, HU-08). Cada uno ocupa el espacio que le dé su [Modifier].
 */

/**
 * Circulito azul en el centro mientras llegan los datos (aprobado por Oscar en HU-07).
 * @param descripcion texto para lectores de pantalla y pruebas, por ejemplo «Cargando anuncios».
 */
@Composable
fun EstadoCargando(descripcion: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.semantics { contentDescription = descripcion }
        )
    }
}

/**
 * Estado vacío de la pantalla 05b (RF-15): círculo gris de 80 dp con la lupa de 36 dp y
 * «No se encontraron anuncios» en 16 sp seminegrita, centrados en el espacio libre.
 */
@Composable
fun EstadoSinResultados(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_buscar),
                contentDescription = null,
                tint = ClasificadosTheme.colores.textoSecundario,
                modifier = Modifier.size(36.dp)
            )
        }
        Text(
            text = stringResource(R.string.listado_vacio),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/** Mensaje sin conexión de RN-18, centrado (acordado con Oscar hasta HU-21, pantalla 05c). */
@Composable
fun EstadoSinConexion(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().padding(horizontal = 32.dp), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.sin_conexion),
            color = ClasificadosTheme.colores.textoSecundario,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
