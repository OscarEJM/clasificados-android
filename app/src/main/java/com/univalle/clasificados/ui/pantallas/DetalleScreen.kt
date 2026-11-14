package com.univalle.clasificados.ui.pantallas

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import com.univalle.clasificados.R
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.componentes.BarraSuperior
import com.univalle.clasificados.ui.componentes.EstadoCargando
import com.univalle.clasificados.ui.componentes.EstadoSinConexion
import com.univalle.clasificados.ui.componentes.EstadoSinResultados
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.util.FormatoPrecio
import com.univalle.clasificados.util.TiempoTranscurrido
import com.univalle.clasificados.viewmodel.DetalleViewModel
import com.univalle.clasificados.viewmodel.EstadoDetalle

/** Etiqueta de la galería de fotos, para deslizarla en las pruebas automáticas (HU-08). */
const val GALERIA_FOTOS = "galeria_fotos"

/** Alto de la galería de Figma sin contar la barra de estado (284 − 24 dp). */
private val AltoGaleria = 260.dp

/**
 * Pantalla 07 · Detalle del anuncio (HU-08 · Ver el detalle de un anuncio, RF-14, RNF-13).
 *
 * Arriba, las fotos deslizables con el contador «n / total» y la flecha atrás sobre un círculo.
 * La galería llega hasta el borde superior (detrás de la barra de estado, como en Figma), pero
 * la flecha queda debajo de la barra de estado (RNF-10). Debajo, con desplazamiento: categoría,
 * título, precio, ciudad, tiempo transcurrido, descripción y contacto. El correo y el teléfono
 * son solo texto (RN-11), y la fila del teléfono aparece solo si el anuncio lo tiene.
 * Esta pantalla no lleva la barra inferior ni el botón «Publicar», igual que en Figma.
 *
 * @param alVolver flecha atrás: regresa al listado.
 */
@Composable
fun DetalleScreen(
    alVolver: () -> Unit,
    viewModel: DetalleViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        when (val actual = estado) {
            is EstadoDetalle.Listo -> ContenidoDetalle(actual.anuncio, alVolver)
            else -> Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
                BarraSuperior(titulo = "", alVolver = alVolver)
                when (actual) {
                    EstadoDetalle.Cargando ->
                        EstadoCargando(stringResource(R.string.detalle_cargando), Modifier.weight(1f))
                    EstadoDetalle.NoEncontrado -> EstadoSinResultados(Modifier.weight(1f))
                    else -> EstadoSinConexion(Modifier.weight(1f))
                }
            }
        }
    }
}

/** Todos los datos del anuncio, según la pantalla 07 de Figma. */
@Composable
private fun ContenidoDetalle(anuncio: Anuncio, alVolver: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    val secundario = ClasificadosTheme.colores.textoSecundario
    // El tiempo se calcula una vez al abrir la pantalla.
    val publicado = remember(anuncio.fechaPublicacion) {
        anuncio.fechaPublicacion?.let {
            TiempoTranscurrido.texto(it.toDate().time, System.currentTimeMillis())
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GaleriaFotos(fotos = anuncio.fotos, alVolver = alVolver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(start = 16.dp, top = 20.dp, end = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Resumen: categoría, título, precio, ciudad y fecha.
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = Categoria.desdeId(anuncio.categoria)?.nombre ?: anuncio.categoria,
                    color = colores.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(ClasificadosTheme.colores.primarioSuave, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                Text(
                    text = anuncio.titulo,
                    color = colores.onSurface,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = FormatoPrecio.formatear(anuncio.precio),
                    color = colores.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconoPequeno(R.drawable.ic_ubicacion)
                    Text(anuncio.ciudad, color = secundario, fontSize = 13.sp)
                    if (publicado != null) {
                        Text("·", color = secundario, fontSize = 13.sp)
                        IconoPequeno(R.drawable.ic_reloj)
                        Text(publicado, color = secundario, fontSize = 13.sp)
                    }
                }
            }
            HorizontalDivider(color = ClasificadosTheme.colores.borde)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Subtitulo(stringResource(R.string.detalle_descripcion))
                Text(
                    text = anuncio.descripcion,
                    color = secundario,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }
            HorizontalDivider(color = ClasificadosTheme.colores.borde)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Subtitulo(stringResource(R.string.detalle_contacto))
                DatoContacto(R.drawable.ic_correo, anuncio.correo)
                // RF-14: la fila del teléfono solo aparece si el anuncio tiene teléfono.
                anuncio.telefono?.takeIf { it.isNotBlank() }?.let { DatoContacto(R.drawable.ic_telefono, it) }
            }
        }
    }
}

/**
 * Galería de Figma: fotos deslizables (HorizontalPager) sobre el gris superficie-variante, con
 * la flecha atrás en un círculo de 40 dp arriba a la izquierda y el contador «n / total» abajo a
 * la derecha. Mientras una foto carga, o si falla, se ve el ícono de imagen de 48 dp.
 */
@Composable
private fun GaleriaFotos(fotos: List<String>, alVolver: () -> Unit) {
    val total = fotos.size
    val estadoPaginas = rememberPagerState { total }
    val altoBarraEstado = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(altoBarraEstado + AltoGaleria)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        if (total == 0) {
            IconoSinFoto(Modifier.fillMaxSize())
        } else {
            HorizontalPager(
                state = estadoPaginas,
                modifier = Modifier.fillMaxSize().testTag(GALERIA_FOTOS)
            ) { pagina ->
                SubcomposeAsyncImage(
                    model = fotos[pagina],
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = { IconoSinFoto(Modifier.fillMaxSize()) },
                    error = { IconoSinFoto(Modifier.fillMaxSize()) }
                )
            }
            // Contador «n / total» sobre fondo negro al 55 %, como en Figma.
            Text(
                text = stringResource(R.string.detalle_contador_fotos, estadoPaginas.currentPage + 1, total),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        // Flecha atrás: círculo de 40 dp color superficie dentro de una zona táctil de 48 dp (RNF-12),
        // 8 dp debajo de la barra de estado como en Figma.
        IconButton(
            onClick = alVolver,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 8.dp, top = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_atras),
                    contentDescription = stringResource(R.string.accion_volver),
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/** Ícono de imagen de Figma (48 dp) centrado: se ve mientras no hay foto que mostrar. */
@Composable
private fun IconoSinFoto(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(R.drawable.ic_imagen_grande),
            contentDescription = null,
            tint = ClasificadosTheme.colores.textoSecundario,
            modifier = Modifier.size(48.dp)
        )
    }
}

/** Ícono de 16 dp en texto-secundario, para la fila de ciudad y fecha. */
@Composable
private fun IconoPequeno(@DrawableRes icono: Int) {
    Icon(
        painter = painterResource(icono),
        contentDescription = null,
        tint = ClasificadosTheme.colores.textoSecundario,
        modifier = Modifier.size(16.dp)
    )
}

/** Título de sección («Descripción», «Contacto»): 16 sp seminegrita. */
@Composable
private fun Subtitulo(texto: String) {
    Text(texto, color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
}

/**
 * Fila de contacto de Figma: ícono de 20 dp en un cuadro primario-suave de 40 dp y el dato
 * como texto de 15 sp (RN-11: no hay botones para llamar ni escribir).
 */
@Composable
private fun DatoContacto(@DrawableRes icono: Int, texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(ClasificadosTheme.colores.primarioSuave, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icono),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(texto, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}
