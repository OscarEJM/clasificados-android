package com.univalle.clasificados

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.data.repository.AuthRepository
import com.univalle.clasificados.data.repository.FotosRepository
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.util.Validador
import com.univalle.clasificados.viewmodel.PublicarViewModel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream

/**
 * Pruebas de HU-14 · Publicar anuncio: ubicación, contacto y confirmación (RF-25, RF-26, RF-27),
 * con la app completa, Firebase y Supabase reales y la cuenta prueba2@ de local.properties.
 *
 * Los pasos 1 a 3 se llenan directamente en el borrador (ya se probaron en HU-12 y HU-13) con
 * dos fotos de prueba fáciles de reconocer: «HU-14 · Foto 1» (verde, JPG) y «HU-14 · Foto 2»
 * (morada, PNG). Esas fotos se quedan en la galería del emulador, carpeta Pictures/PruebasHU14
 * (pedido por Oscar). Los anuncios que publican las pruebas se borran al terminar, con sus fotos
 * en Supabase (autorizado por Oscar).
 */
@RunWith(AndroidJUnit4::class)
class PublicarAnuncioTest {

    @get:Rule
    val compose = createEmptyComposeRule()

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val argumentos = InstrumentationRegistry.getArguments()

    private lateinit var uid: String
    private lateinit var correo: String
    private lateinit var escenario: ActivityScenario<MainActivity>

    /** Ids de los anuncios publicados por la prueba, para borrarlos al final. */
    private val publicados = mutableListOf<String>()

    /** `true` si la prueba puso un teléfono a la cuenta y hay que quitarlo al final. */
    private var telefonoAgregado = false

    @Before
    fun iniciarSesion() = runBlocking {
        correo = argumentos.getString("correoPrueba2").orEmpty()
        val contrasena = argumentos.getString("contrasenaPrueba2").orEmpty()
        assumeTrue("Falta la cuenta prueba2 en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        uid = auth.signInWithEmailAndPassword(correo, contrasena).await().user!!.uid
    }

    @After
    fun limpiar() = runBlocking {
        if (::escenario.isInitialized) escenario.close()
        publicados.forEach { id ->
            runCatching { FotosRepository().borrarFotosAnuncio(uid, id, 2) }
            runCatching { db.collection(AnunciosRepository.COLECCION_ANUNCIOS).document(id).delete().await() }
        }
        if (telefonoAgregado) {
            runCatching {
                db.collection(AuthRepository.COLECCION_USUARIOS).document(uid)
                    .update("telefono", FieldValue.delete()).await()
            }
        }
        auth.signOut()
    }

    /** Criterio 1: sin ciudad, «Publicar anuncio» muestra el mensaje y no publica. */
    @Test
    fun sinCiudad_muestraMensajeYNoPublica() {
        abrirPaso4()
        tocar("Publicar anuncio")
        esperarTexto(Validador.CIUDAD_OBLIGATORIA)
        esperarTexto("Ubicación y contacto")
    }

    /** Criterio 2: correo vacío y correo sin «@» muestran cada uno su mensaje y no se publica. */
    @Test
    fun correoVacioOInvalido_muestraMensaje() {
        abrirPaso4()
        elegirCiudad("Cali")

        escribirCorreo("")
        tocar("Publicar anuncio")
        esperarTexto(Validador.CORREO_CONTACTO_OBLIGATORIO)

        escribirCorreo("ana.gomez.email.com")
        tocar("Publicar anuncio")
        esperarTexto(Validador.CORREO_CONTACTO_INVALIDO)
        esperarTexto("Ubicación y contacto")
    }

    /** Criterio 3: el teléfono del registro y el correo de la cuenta aparecen ya escritos. */
    @Test
    fun telefonoYCorreoPrellenados() {
        val telefono = telefonoDeLaCuenta()
        abrirPaso4()
        esperarTexto(correo)
        esperarTexto(telefono)
    }

    /**
     * Criterios 4, 5 y 6: con todo válido se ve el circulito «Publicando anuncio» y luego
     * Confirmación; el anuncio queda guardado con autor, fecha del servidor y sus 2 fotos, y es
     * el primero de su categoría. «Ver anuncio» abre su detalle y «atrás» lleva a Inicio.
     */
    @Test
    fun publicar_muestraConfirmacionYVerAnuncio() {
        val titulo = "Prueba HU-14 bicicleta de ruta talla M"
        abrirPaso4(titulo)
        elegirCiudad("Tuluá")
        tocar("Publicar anuncio")

        esperarDescripcion("Publicando anuncio")
        esperarTexto("¡Anuncio publicado correctamente!", espera = 60_000)
        esperarTexto("Tu publicación ya está disponible para otros usuarios.")

        val anuncio = anuncioPublicado(titulo)
        assertEquals(uid, anuncio.autorId)
        assertEquals(titulo.lowercase(), anuncio.tituloMinusculas)
        assertEquals("Tuluá", anuncio.ciudad)
        assertEquals(correo, anuncio.correo)
        assertNotNull("La fecha la pone el servidor", anuncio.fechaPublicacion)
        assertEquals(2, anuncio.fotos.size)
        assertTrue(anuncio.fotos[0].endsWith("$uid/${anuncio.id}/1.jpg"))

        // Criterio 6: otro usuario lo ve de primero en su categoría (las reglas permiten leer a
        // cualquier usuario con sesión; el orden es el mismo para todos).
        val primero = runBlocking { AnunciosRepository().anunciosDeCategoria(Categoria.DEPORTES.id).first() }
        assertEquals(anuncio.id, primero.id)

        // Criterio 5: «Ver anuncio» abre el detalle; «atrás» desde el detalle lleva a Inicio.
        tocar("Ver anuncio")
        esperarTexto(titulo)
        esperarTexto("Tuluá")
        Espresso.pressBack()
        esperarTexto("Ver todas")
    }

    /** Criterio 5: «Volver al inicio» abre Inicio y el formulario queda vacío para otro anuncio. */
    @Test
    fun publicar_volverAlInicio() {
        val titulo = "Prueba HU-14 balón de fútbol número 5"
        abrirPaso4(titulo)
        elegirCiudad("Cali")
        tocar("Publicar anuncio")
        esperarTexto("¡Anuncio publicado correctamente!", espera = 60_000)
        anuncioPublicado(titulo)

        tocar("Volver al inicio")
        esperarTexto("Ver todas")

        // El borrador se borró: Publicar empieza otra vez en el paso 1.
        tocar("Publicar")
        esperarTexto("Selecciona una categoría")
    }

    // --- Ayudas ---

    /**
     * Abre la app, llena los pasos 1 a 3 en el borrador compartido, toca «Publicar» y espera el
     * paso 4 (pantalla 11).
     */
    private fun abrirPaso4(titulo: String = "Prueba HU-14 raqueta de tenis") {
        escenario = ActivityScenario.launch(MainActivity::class.java)
        esperarTexto("Ver todas")
        lateinit var viewModel: PublicarViewModel
        escenario.onActivity { viewModel = ViewModelProvider(it)[PublicarViewModel::class.java] }
        val fotos = fotosDePrueba()
        viewModel.apply {
            elegirCategoria(Categoria.DEPORTES)
            alCambiarTitulo(titulo)
            alCambiarDescripcion("Anuncio creado por la prueba automática de HU-14; se borra al terminar.")
            alCambiarPrecio("250000")
            continuar()
            continuar()
            agregarFotos(fotos)
        }
        compose.waitUntil(timeoutMillis = 10_000) { viewModel.estado.value.borrador.fotos.size == 2 }
        viewModel.continuar()
        tocar("Publicar")
        esperarTexto("Ubicación y contacto")
    }

    /** Teléfono guardado en el registro de la cuenta; si no tiene, le pone uno de prueba (se quita al final). */
    private fun telefonoDeLaCuenta(): String = runBlocking {
        val perfil = db.collection(AuthRepository.COLECCION_USUARIOS).document(uid)
        val actual = perfil.get(Source.SERVER).await().getString("telefono")
        if (!actual.isNullOrBlank()) return@runBlocking actual
        perfil.set(mapOf("telefono" to TELEFONO_PRUEBA), SetOptions.merge()).await()
        telefonoAgregado = true
        TELEFONO_PRUEBA
    }

    /** Busca en Firestore el anuncio recién publicado con ese título y lo anota para borrarlo. */
    private fun anuncioPublicado(titulo: String): Anuncio = runBlocking {
        val anuncio = db.collection(AnunciosRepository.COLECCION_ANUNCIOS)
            .whereEqualTo("autorId", uid)
            .whereEqualTo("titulo", titulo)
            .get(Source.SERVER).await()
            .toObjects(Anuncio::class.java)
            .single()
        publicados += anuncio.id
        anuncio
    }

    /** Elige una ciudad en la lista del paso 4. */
    private fun elegirCiudad(ciudad: String) {
        tocar("Selecciona tu ciudad")
        esperarTexto(ciudad)
        tocar(ciudad)
    }

    /** Reemplaza el texto del campo de correo (el que tiene el ícono de sobre). */
    private fun escribirCorreo(texto: String) {
        val campo = compose.onAllNodesWithText("Email *").fetchSemanticsNodes()
        assertTrue(campo.isNotEmpty())
        // El campo de correo es el primer campo editable del paso 4.
        compose.onAllNodes(hasSetTextAction())[0].performTextReplacement(texto)
    }

    /** Toca el elemento que se puede tocar con ese texto. */
    private fun tocar(texto: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(hasText(texto) and hasClickAction()).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onAllNodes(hasText(texto) and hasClickAction())[0].performSemanticsAction(SemanticsActions.OnClick)
    }

    /** Espera a que aparezca un texto exacto en pantalla. */
    private fun esperarTexto(texto: String, espera: Long = 15_000) {
        compose.waitUntil(timeoutMillis = espera) {
            compose.onAllNodesWithText(texto).fetchSemanticsNodes().isNotEmpty()
        }
    }

    /** Espera a que aparezca un elemento con esa descripción (el circulito de progreso). */
    private fun esperarDescripcion(descripcion: String) {
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodesWithContentDescription(descripcion).fetchSemanticsNodes().isNotEmpty() ||
                compose.onAllNodes(hasContentDescription(descripcion), useUnmergedTree = true)
                    .fetchSemanticsNodes().isNotEmpty()
        }
    }

    /**
     * Las dos fotos de prueba de HU-14 en la galería del emulador (Pictures/PruebasHU14): verde
     * con «HU-14 · Foto 1» (JPG) y morada con «HU-14 · Foto 2» (PNG). Se crean una vez por
     * ejecución y no se borran (pedido por Oscar).
     */
    private fun fotosDePrueba(): List<Uri> = fotosCreadas ?: listOf(
        crearFoto("HU14_foto1", "image/jpeg", Bitmap.CompressFormat.JPEG, Color.rgb(22, 163, 74), 1),
        crearFoto("HU14_foto2", "image/png", Bitmap.CompressFormat.PNG, Color.rgb(124, 58, 237), 2)
    ).also { fotosCreadas = it }

    /** Crea una foto de 600 × 400 de un color con el texto «HU-14 · Foto N» en blanco. */
    private fun crearFoto(nombre: String, tipo: String, formato: Bitmap.CompressFormat, color: Int, numero: Int): Uri {
        val mapa = Bitmap.createBitmap(600, 400, Bitmap.Config.ARGB_8888)
        Canvas(mapa).apply {
            drawColor(color)
            val pincel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = Color.WHITE
                textSize = 64f
                textAlign = Paint.Align.CENTER
                isFakeBoldText = true
            }
            drawText("HU-14 · Foto $numero", 300f, 220f, pincel)
        }
        val bytes = ByteArrayOutputStream().also { mapa.compress(formato, 90, it) }.toByteArray()
        val resolver = ApplicationProvider.getApplicationContext<android.app.Application>().contentResolver
        val valores = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nombre)
            put(MediaStore.Images.Media.MIME_TYPE, tipo)
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PruebasHU14")
        }
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)!!
        resolver.openOutputStream(uri)!!.use { it.write(bytes) }
        return uri
    }

    private companion object {
        /** Teléfono que se pone a la cuenta solo si no tiene uno (y se quita al final). */
        const val TELEFONO_PRUEBA = "(+57) 311 222 3344"

        /** Fotos creadas en esta ejecución, para no crearlas otra vez en cada prueba. */
        var fotosCreadas: List<Uri>? = null
    }
}
