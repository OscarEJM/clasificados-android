package com.univalle.clasificados.ui.pantallas

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.univalle.clasificados.R
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.componentes.BarraSuperior
import com.univalle.clasificados.ui.componentes.BotonPrincipal
import com.univalle.clasificados.ui.componentes.BotonSecundario
import com.univalle.clasificados.ui.componentes.CampoTexto
import com.univalle.clasificados.ui.componentes.IndicadorPasos
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.viewmodel.PublicarUiState
import com.univalle.clasificados.viewmodel.PublicarViewModel

/** Forma de las opciones de categoría del paso 1: esquinas de 12 dp (Figma, pantalla 08). */
private val FormaOpcion = RoundedCornerShape(12.dp)

/**
 * Formulario «Publicar anuncio» de cuatro pasos (HU-12 · Publicar anuncio: pasos de categoría y
 * detalles; RF-21, RF-22, RF-23), según las pantallas 08, 09 y 09b de Figma. No lleva la barra
 * inferior: arriba tiene la flecha atrás y el título, luego el indicador de pasos, el contenido
 * del paso y abajo los botones.
 *
 * - Paso 1 · Categoría: las 13 categorías de dos en dos; la elegida queda resaltada.
 * - Paso 2 · Detalles: título con el contador vivo, descripción y precio (solo números).
 * - Paso 3: marcador con el indicador y «Volver» hasta HU-13 (acordado con Oscar).
 *
 * La flecha atrás y el «atrás» del teléfono regresan al paso anterior; en el paso 1 cierran
 * Publicar. El borrador vive en [PublicarViewModel], que comparte toda la app.
 *
 * @param alSalir cierra Publicar y vuelve a la pantalla anterior.
 */
@Composable
fun PublicarScreen(viewModel: PublicarViewModel, alSalir: () -> Unit) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val volver = { if (!viewModel.volver()) alSalir() }
    BackHandler(onBack = volver)

    Column(modifier = Modifier.fillMaxSize().imePadding()) {
        BarraSuperior(titulo = stringResource(R.string.publicar_titulo), alVolver = volver)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            IndicadorPasos(pasoActual = estado.paso)
            when (estado.paso) {
                1 -> PasoCategoria(estado, viewModel)
                2 -> PasoDetalles(estado, viewModel)
                else -> Unit // Marcador del paso 3 (fotos) hasta HU-13.
            }
        }
        // Botones de Figma: 12 dp arriba y 32 dp abajo; en el paso 1 solo «Continuar».
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (estado.paso > 1) {
                BotonSecundario(
                    texto = stringResource(R.string.publicar_volver),
                    alTocar = volver,
                    modifier = Modifier.weight(1f)
                )
            }
            if (estado.paso < 3) {
                BotonPrincipal(
                    texto = stringResource(R.string.publicar_continuar),
                    cargando = false,
                    alTocar = viewModel::continuar,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/** Título y subtítulo de cada paso (20 sp y 14 sp, Figma). */
@Composable
private fun TituloPaso(titulo: String, subtitulo: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(titulo, color = MaterialTheme.colorScheme.onSurface, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Text(subtitulo, color = ClasificadosTheme.colores.textoSecundario, fontSize = 14.sp)
    }
}

/**
 * Paso 1 · Categoría (pantalla 08, RF-22): las 13 categorías en botones de 44 dp, dos por
 * renglón con 10 dp de separación. Sin categoría, al tocar «Continuar» aparece el mensaje de la
 * sección 1.5 debajo de las opciones, en color *error* (no hay pantalla de error en Figma).
 */
@Composable
private fun PasoCategoria(estado: PublicarUiState, viewModel: PublicarViewModel) {
    TituloPaso(stringResource(R.string.publicar_p1_titulo), stringResource(R.string.publicar_p1_subtitulo))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Categoria.entries.chunked(2).forEach { renglon ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                renglon.forEach { categoria ->
                    OpcionCategoria(
                        categoria = categoria,
                        elegida = estado.borrador.categoria == categoria,
                        alTocar = { viewModel.elegirCategoria(categoria) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // La última fila tiene una sola categoría: se deja el espacio de la segunda.
                if (renglon.size == 1) Spacer(Modifier.weight(1f))
            }
        }
        estado.errorCategoria?.let {
            Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
    }
}

/**
 * Opción de categoría de Figma: 44 dp de alto, esquinas de 12 dp. Elegida: fondo primario-suave,
 * borde primario de 1,5 dp y texto primario seminegrita; sin elegir: fondo superficie y borde gris.
 */
@Composable
private fun OpcionCategoria(categoria: Categoria, elegida: Boolean, alTocar: () -> Unit, modifier: Modifier) {
    val colores = MaterialTheme.colorScheme
    val extra = ClasificadosTheme.colores
    Row(
        modifier = modifier
            .height(48.dp) // 44 dp de Figma + zona táctil de 48 dp (RNF-12).
            .padding(vertical = 2.dp)
            .clip(FormaOpcion)
            .background(if (elegida) extra.primarioSuave else colores.surface)
            .border(
                if (elegida) 1.5.dp else 1.dp,
                if (elegida) colores.primary else extra.borde,
                FormaOpcion
            )
            .semantics { selected = elegida }
            .clickable(role = Role.RadioButton, onClick = alTocar),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = categoria.nombre,
            color = if (elegida) colores.primary else colores.onSurface,
            fontSize = 14.sp,
            fontWeight = if (elegida) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

/**
 * Paso 2 · Detalles (pantallas 09 y 09b, RF-23): título con el contador «Mínimo 10 caracteres ·
 * N escritos», descripción de varias líneas y precio con teclado numérico que solo admite dígitos.
 * Con error, el borde y el mensaje se ven en color *error* y el mensaje reemplaza a la ayuda.
 */
@Composable
private fun PasoDetalles(estado: PublicarUiState, viewModel: PublicarViewModel) {
    TituloPaso(stringResource(R.string.publicar_p2_titulo), stringResource(R.string.publicar_p2_subtitulo))
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CampoTexto(
            etiqueta = stringResource(R.string.publicar_campo_titulo),
            valor = estado.borrador.titulo,
            alCambiar = viewModel::alCambiarTitulo,
            ejemplo = stringResource(R.string.publicar_campo_titulo_ejemplo),
            error = estado.errorTitulo,
            ayuda = estado.contadorTitulo
        )
        CampoTexto(
            etiqueta = stringResource(R.string.publicar_campo_descripcion),
            valor = estado.borrador.descripcion,
            alCambiar = viewModel::alCambiarDescripcion,
            ejemplo = stringResource(R.string.publicar_campo_descripcion_ejemplo),
            error = estado.errorDescripcion,
            ayuda = stringResource(R.string.publicar_campo_descripcion_ayuda),
            multilinea = true
        )
        CampoTexto(
            etiqueta = stringResource(R.string.publicar_campo_precio),
            valor = estado.borrador.precio,
            alCambiar = viewModel::alCambiarPrecio,
            ejemplo = stringResource(R.string.publicar_campo_precio_ejemplo),
            error = estado.errorPrecio,
            ayuda = stringResource(R.string.publicar_campo_precio_ayuda),
            tipoTeclado = KeyboardType.Number,
            prefijo = "$ "
        )
    }
}
