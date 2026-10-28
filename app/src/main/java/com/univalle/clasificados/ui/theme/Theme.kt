package com.univalle.clasificados.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Tokens de Figma que no tienen equivalente directo en el ColorScheme de Material 3. */
@Immutable
data class ColoresExtra(
    val primarioSuave: Color,
    val publicar: Color,
    val textoSecundario: Color,
    val borde: Color,
    val exito: Color
)

private val EsquemaClaro = lightColorScheme(
    primary = PrimarioClaro,
    onPrimary = SobrePrimarioClaro,
    primaryContainer = PrimarioSuaveClaro,
    onPrimaryContainer = PrimarioClaro,
    secondaryContainer = PrimarioSuaveClaro,
    onSecondaryContainer = PrimarioClaro,
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = SuperficieClaro,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieVarianteClaro,
    onSurfaceVariant = TextoSecundarioClaro,
    surfaceContainer = SuperficieClaro,
    outline = BordeClaro,
    outlineVariant = BordeClaro,
    error = ErrorClaro,
    onError = SobrePrimarioClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = PrimarioOscuro,
    onPrimary = SobrePrimarioOscuro,
    primaryContainer = PrimarioSuaveOscuro,
    onPrimaryContainer = TextoOscuro,
    secondaryContainer = PrimarioSuaveOscuro,
    onSecondaryContainer = TextoOscuro,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = SuperficieOscuro,
    onSurface = TextoOscuro,
    surfaceVariant = SuperficieVarianteOscuro,
    onSurfaceVariant = TextoSecundarioOscuro,
    surfaceContainer = SuperficieOscuro,
    outline = BordeOscuro,
    outlineVariant = BordeOscuro,
    error = ErrorOscuro,
    onError = SobrePrimarioOscuro
)

private val ExtraClaro = ColoresExtra(
    primarioSuave = PrimarioSuaveClaro,
    publicar = PublicarClaro,
    textoSecundario = TextoSecundarioClaro,
    borde = BordeClaro,
    exito = ExitoClaro
)

private val ExtraOscuro = ColoresExtra(
    primarioSuave = PrimarioSuaveOscuro,
    publicar = PublicarOscuro,
    textoSecundario = TextoSecundarioOscuro,
    borde = BordeOscuro,
    exito = ExitoOscuro
)

private val LocalColoresExtra = staticCompositionLocalOf { ExtraClaro }

/** Acceso a los colores extra: ClasificadosTheme.colores.publicar, etc. */
object ClasificadosTheme {
    val colores: ColoresExtra
        @Composable
        @ReadOnlyComposable
        get() = LocalColoresExtra.current
}

/**
 * Tema de la aplicación con los colores de Figma. No usa el color dinámico de Android 12
 * para que siempre se vean los colores del diseño. El tema claro es el valor por defecto (RN-17).
 */
@Composable
fun ClasificadosTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalColoresExtra provides if (darkTheme) ExtraOscuro else ExtraClaro) {
        MaterialTheme(
            colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
            content = content
        )
    }
}
