package com.univalle.clasificados

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.core.app.ActivityOptionsCompat
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.ui.pantallas.MINIATURA_FOTO
import com.univalle.clasificados.ui.pantallas.PublicarScreen
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.util.Validador
import com.univalle.clasificados.viewmodel.PublicarViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

/**
 * Pruebas de HU-13 · Publicar anuncio: agregar fotos (RF-24, RNF-09) sobre el formulario de
 * Publicar. Una prueba no puede tocar la galería del sistema, así que se reemplaza por una
 * «galería simulada» que devuelve fotos reales creadas en el emulador para la prueba (JPG, PNG,
 * GIF y una JPG de 6 MB). Al terminar, esas fotos se borran.
 */
@RunWith(AndroidJUnit4::class)
class PublicarFotosTest {

    @get:Rule
    val compose = createAndroidComposeRule<ComponentActivity>()

    private val aplicacion: Application = ApplicationProvider.getApplicationContext()
    private val creadas = mutableListOf<Uri>()
    private lateinit var galeria: GaleriaSimulada
    private lateinit var viewModel: PublicarViewModel

    /** Abre el formulario ya en el paso 3, con los pasos 1 y 2 llenos. */
    @Before
    fun abrirPaso3() {
        galeria = GaleriaSimulada()
        viewModel = PublicarViewModel(aplicacion).apply {
            elegirCategoria(Categoria.DEPORTES)
            alCambiarTitulo("Bicicleta todoterreno rin 29")
            alCambiarDescripcion("En muy buen estado, con frenos de disco")
            alCambiarPrecio("500000")
            continuar()
            continuar()
        }
        val duenoRegistro = object : ActivityResultRegistryOwner {
            override val activityResultRegistry: ActivityResultRegistry = galeria
        }
        compose.setContent {
            CompositionLocalProvider(LocalActivityResultRegistryOwner provides duenoRegistro) {
                ClasificadosTheme { PublicarScreen(viewModel = viewModel, alSalir = {}) }
            }
        }
        esperarPaso(3)
    }

    @After
    fun borrarFotosDePrueba() {
        creadas.forEach { runCatching { aplicacion.contentResolver.delete(it, null, null) } }
    }

    /** Criterio 1: sin fotos, «Continuar» muestra «Agrega al menos 1 foto» y no avanza. */
    @Test
    fun sinFotos_muestraMensajeYNoAvanza() {
        tocar("Continuar")
        esperarTexto(Validador.FOTOS_MINIMO)
        esperarPaso(3)
        esperarTexto("0 / 5")
    }

    /** Criterio 2: al elegir fotos se ven sus miniaturas y el contador; luego se puede continuar. */
    @Test
    fun elegirFotos_muestraMiniaturasYContador() {
        galeria.respuesta = listOf(foto("jpg"), foto("png"))
        tocar("Toca para agregar fotos")
        esperarTexto("2 / 5")
        esperarMiniaturas(2)

        // La «✕» quita una foto.
        tocar("Quitar foto 1")
        esperarTexto("1 / 5")
        esperarMiniaturas(1)

        tocar("Continuar")
        esperarPaso(4)
    }

    /** Criterio 3: con 5 fotos, intentar agregar otra muestra «Máximo 5 fotos» y no abre la galería. */
    @Test
    fun conCincoFotos_otraMuestraMaximo() {
        galeria.respuesta = List(5) { foto("jpg") }
        tocar("Toca para agregar fotos")
        esperarTexto("5 / 5")
        esperarMiniaturas(5)
        val abiertas = galeria.vecesAbierta

        tocar("Toca para agregar fotos")
        esperarTexto(Validador.FOTOS_MAXIMO)
        assertEquals("La galería no se debe abrir con 5 fotos", abiertas, galeria.vecesAbierta)
        esperarMiniaturas(5)
    }

    /** Criterio 4: una foto de más de 5 MB o que no es JPG ni PNG no se agrega y se ve el mensaje. */
    @Test
    fun archivoInvalido_noSeAgregaYMuestraMensaje() {
        galeria.respuesta = listOf(foto("gif"), foto("grande"), foto("jpg"))
        tocar("Toca para agregar fotos")
        esperarTexto(Validador.FOTO_INVALIDA)
        // Solo la JPG pequeña entró.
        esperarTexto("1 / 5")
        esperarMiniaturas(1)
    }

    // --- Ayudas ---

    /**
     * Crea en el emulador una foto de prueba y devuelve su dirección.
     * @param clase «jpg», «png», «gif» o «grande» (una JPG de 6 MB).
     */
    private fun foto(clase: String): Uri {
        val (tipo, bytes) = when (clase) {
            "png" -> "image/png" to imagen(Bitmap.CompressFormat.PNG)
            "gif" -> "image/gif" to GIF_MINIMO
            "grande" -> "image/jpeg" to imagen(Bitmap.CompressFormat.JPEG).copyOf(6 * 1024 * 1024)
            else -> "image/jpeg" to imagen(Bitmap.CompressFormat.JPEG)
        }
        val valores = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "prueba_hu13_${System.nanoTime()}")
            put(MediaStore.Images.Media.MIME_TYPE, tipo)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PruebasClasificados")
        }
        val resolver = aplicacion.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)!!
        resolver.openOutputStream(uri)!!.use { it.write(bytes) }
        creadas += uri
        return uri
    }

    /** Imagen de 40 × 40 de un solo color, comprimida en el formato pedido. */
    private fun imagen(formato: Bitmap.CompressFormat): ByteArray {
        val mapa = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.rgb(37, 99, 235)) }
        return ByteArrayOutputStream().also { mapa.compress(formato, 90, it) }.toByteArray()
    }

    /** Espera a que haya exactamente [cantidad] miniaturas. */
    private fun esperarMiniaturas(cantidad: Int) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithTag(MINIATURA_FOTO).fetchSemanticsNodes().size == cantidad
        }
    }

    /** Espera a que el indicador marque ese paso como el actual. */
    private fun esperarPaso(paso: Int) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithContentDescription("Paso $paso de 4", substring = true)
                .fetchSemanticsNodes()
                .any { nodo -> nodo.config[SemanticsProperties.ContentDescription].any { it.endsWith(", actual") } }
        }
    }

    /** Toca el elemento que se puede tocar con ese texto, o con esa descripción (la «✕»). */
    private fun tocar(texto: String) {
        val nodo = (hasText(texto) or hasContentDescription(texto)) and hasClickAction()
        compose.onNode(nodo).performSemanticsAction(SemanticsActions.OnClick)
    }

    /** Espera hasta 10 segundos a que aparezca un texto en pantalla. */
    private fun esperarTexto(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Galería simulada: en vez de abrir el selector de fotos del sistema, devuelve [respuesta].
     * Cuenta cuántas veces se intentó abrir.
     */
    private class GaleriaSimulada : ActivityResultRegistry() {
        var respuesta: List<Uri> = emptyList()
        var vecesAbierta = 0

        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?
        ) {
            vecesAbierta++
            val resultado: Any? =
                if (contract is ActivityResultContracts.PickVisualMedia) respuesta.firstOrNull() else respuesta
            dispatchResult(requestCode, resultado)
        }
    }

    private companion object {
        /** El GIF más pequeño posible (1 × 1), para probar un formato que no es JPG ni PNG. */
        val GIF_MINIMO = byteArrayOf(
            0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00, 0x80.toByte(), 0x00, 0x00,
            0xFF.toByte(), 0xFF.toByte(), 0xFF.toByte(), 0x00, 0x00, 0x00, 0x21, 0xF9.toByte(), 0x04,
            0x01, 0x00, 0x00, 0x00, 0x00, 0x2C, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00,
            0x02, 0x02, 0x44, 0x01, 0x00, 0x3B
        )
    }
}
