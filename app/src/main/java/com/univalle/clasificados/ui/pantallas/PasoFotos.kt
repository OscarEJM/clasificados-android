package com.univalle.clasificados.ui.pantallas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.util.Validador
import com.univalle.clasificados.viewmodel.PublicarUiState
import com.univalle.clasificados.viewmodel.PublicarViewModel

/** Etiqueta de cada miniatura de foto, para contarlas en las pruebas automáticas (HU-13). */
const val MINIATURA_FOTO = "miniatura_foto"

/** Forma de la zona punteada (16 dp) y de los espacios de fotos (12 dp), como en Figma. */
private val RadioZona = 16.dp
private val FormaEspacio = RoundedCornerShape(12.dp)

/**
 * Paso 3 · Fotos (pantallas 10 y 10b de Figma; HU-13 · Publicar anuncio: agregar fotos, RF-24).
 *
 * - Zona punteada de 168 dp «Toca para agregar fotos»: abre el selector de fotos del sistema,
 *   que deja elegir solo las que faltan para llegar a 5. Con 5 fotos muestra «Máximo 5 fotos».
 * - «Fotos seleccionadas» con el contador «N / 5» y, debajo, el mensaje de error si hay uno.
 * - Cinco espacios de 58 dp: las fotos elegidas se ven como miniaturas con una «✕» para quitarlas
 *   (pedida por Oscar; no está en Figma); los vacíos muestran el ícono de imagen.
 * - Con error (10b), el borde punteado se pone de color *error*.
 */
@Composable
fun PasoFotos(estado: PublicarUiState, viewModel: PublicarViewModel) {
    val colores = MaterialTheme.colorScheme
    val extra = ClasificadosTheme.colores
    val fotos = estado.borrador.fotos

    // Selector de fotos del sistema (funciona desde Android 8 con Google Play Services). Para
    // varias fotos pide como máximo las que caben; si solo cabe una, se usa el de una sola foto.
    val elegirVarias = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxOf(2, estado.cuposFotos))
    ) { elegidas: List<Uri> -> viewModel.agregarFotos(elegidas) }
    val elegirUna = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { elegida: Uri? -> viewModel.agregarFotos(listOfNotNull(elegida)) }
    val soloImagenes = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)

    TituloPaso(stringResource(R.string.publicar_p3_titulo), stringResource(R.string.publicar_p3_subtitulo))
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(168.dp)
                .clip(RoundedCornerShape(RadioZona))
                .background(colores.surface)
                .bordePunteado(if (estado.errorFotos != null) colores.error else colores.primary, RadioZona)
                .clickable(role = Role.Button) {
                    if (viewModel.puedeAgregarFotos()) {
                        if (estado.cuposFotos == 1) elegirUna.launch(soloImagenes) else elegirVarias.launch(soloImagenes)
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier.size(56.dp).background(extra.primarioSuave, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camara),
                    contentDescription = null,
                    tint = colores.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Text(
                stringResource(R.string.publicar_fotos_zona),
                color = colores.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(stringResource(R.string.publicar_fotos_reglas), color = extra.textoSecundario, fontSize = 13.sp)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                stringResource(R.string.publicar_fotos_seleccionadas),
                color = colores.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(estado.contadorFotos, color = extra.textoSecundario, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }

        estado.errorFotos?.let { Text(it, color = colores.error, fontSize = 12.sp) }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(Validador.MAXIMO_FOTOS) { indice ->
                val foto = fotos.getOrNull(indice)
                EspacioFoto(
                    numero = indice + 1,
                    foto = foto,
                    alQuitar = { foto?.let(viewModel::quitarFoto) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Espacio de una foto (58 dp de alto, esquinas de 12 dp). Con foto, la miniatura recortada y la
 * «✕» arriba a la derecha (círculo negro al 55 % como el contador del detalle, zona táctil de
 * 48 dp, RNF-12). Sin foto, el ícono de imagen de Figma sobre superficie-variante.
 */
@Composable
private fun EspacioFoto(numero: Int, foto: Uri?, alQuitar: () -> Unit, modifier: Modifier) {
    val extra = ClasificadosTheme.colores
    Box(
        modifier = modifier
            .height(58.dp)
            .clip(FormaEspacio)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        if (foto == null) {
            Icon(
                painter = painterResource(R.drawable.ic_imagen_pequena),
                contentDescription = null,
                tint = extra.textoSecundario,
                modifier = Modifier.size(20.dp)
            )
        } else {
            val descripcion = stringResource(R.string.publicar_foto_numero, numero)
            AsyncImage(
                model = foto,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(MINIATURA_FOTO)
                    .semantics { contentDescription = descripcion }
            )
            IconButton(onClick = alQuitar, modifier = Modifier.align(Alignment.TopEnd).size(48.dp)) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(22.dp)
                        .background(Color.Black.copy(alpha = 0.55f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_cerrar),
                        contentDescription = stringResource(R.string.publicar_quitar_foto, numero),
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/** Borde punteado de 1,5 dp con esquinas redondeadas, como la «Zona para subir fotos» de Figma. */
private fun Modifier.bordePunteado(color: Color, radio: Dp): Modifier = drawBehind {
    val grosor = 1.5.dp.toPx()
    drawRoundRect(
        color = color,
        topLeft = androidx.compose.ui.geometry.Offset(grosor / 2, grosor / 2),
        size = androidx.compose.ui.geometry.Size(size.width - grosor, size.height - grosor),
        cornerRadius = CornerRadius(radio.toPx()),
        style = Stroke(width = grosor, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 4.dp.toPx())))
    )
}
