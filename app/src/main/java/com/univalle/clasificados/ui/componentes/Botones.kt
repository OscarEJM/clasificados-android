package com.univalle.clasificados.ui.componentes

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.univalle.clasificados.ui.theme.ClasificadosTheme

/**
 * Componente «Botón» primario de Figma: 48 dp de alto, esquinas de 24 dp, fondo primario y texto
 * de 15 sp. Mientras [cargando], muestra un circulito blanco y no se puede tocar (diseño aprobado
 * por Oscar), así se evita crear dos cuentas con dos toques.
 */
@Composable
fun BotonPrincipal(texto: String, cargando: Boolean, alTocar: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    Button(
        onClick = alTocar,
        enabled = !cargando,
        modifier = Modifier.fillMaxWidth().height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colores.primary,
            contentColor = colores.onPrimary,
            disabledContainerColor = colores.primary,
            disabledContentColor = colores.onPrimary
        )
    ) {
        if (cargando) {
            CircularProgressIndicator(
                color = colores.onPrimary,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
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
