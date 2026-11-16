package com.univalle.clasificados.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/**
 * Componente «Botón» primario de Figma: 48 dp de alto, esquinas de 24 dp, fondo primario y texto
 * de 15 sp. Mientras [cargando], muestra un circulito blanco y no se puede tocar (diseño aprobado
 * por Oscar), así se evita crear dos cuentas con dos toques.
 *
 * @param color fondo del botón; por defecto el primario. «Publicar anuncio» usa el verde
 * *publicar* (Figma, pantallas 11 y 11b; HU-14).
 * @param descripcionCargando texto que leen el lector de pantalla y las pruebas mientras se ve el
 * circulito, por ejemplo «Publicando anuncio».
 */
@Composable
fun BotonPrincipal(
    texto: String,
    cargando: Boolean,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color? = null,
    descripcionCargando: String? = null
) {
    val colores = MaterialTheme.colorScheme
    val fondo = color ?: colores.primary
    Button(
        onClick = alTocar,
        enabled = !cargando,
        modifier = modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = fondo,
            contentColor = colores.onPrimary,
            disabledContainerColor = fondo,
            disabledContentColor = colores.onPrimary
        )
    ) {
        if (cargando) {
            CircularProgressIndicator(
                color = colores.onPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp).semantics { descripcionCargando?.let { contentDescription = it } }
            )
        } else {
            Text(text = texto, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * Enlace al pie de las pantallas de cuenta, por ejemplo «¿Ya tienes cuenta? Inicia sesión»:
 * la pregunta en gris y la acción en azul. Toda la fila se puede tocar y mide al menos 48 dp.
 */
@Composable
fun EnlaceInferior(
    pregunta: String,
    accion: String,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clickable(onClickLabel = accion, role = Role.Button, onClick = alTocar),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = pregunta, color = ClasificadosTheme.colores.textoSecundario, fontSize = 14.sp)
        Text(
            text = accion,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Componente «Botón» secundario de Figma (por ejemplo «Volver» en Publicar, HU-12): 48 dp de
 * alto, esquinas de 24 dp, fondo superficie con borde y texto primario de 15 sp.
 */
@Composable
fun BotonSecundario(
    texto: String,
    alTocar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    val colores = MaterialTheme.colorScheme
    OutlinedButton(
        onClick = alTocar,
        enabled = habilitado,
        modifier = modifier.fillMaxWidth().height(48.dp).alpha(if (habilitado) 1f else 0.5f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, ClasificadosTheme.colores.borde),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = colores.surface,
            contentColor = colores.primary,
            disabledContainerColor = colores.surface,
            disabledContentColor = colores.primary
        )
    ) {
        Text(text = texto, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}
