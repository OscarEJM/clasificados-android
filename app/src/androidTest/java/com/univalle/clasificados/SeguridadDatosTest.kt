package com.univalle.clasificados

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.univalle.clasificados.data.SesionEnFotos
import com.univalle.clasificados.data.SupabaseCliente
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.data.repository.AuthRepository
import com.univalle.clasificados.data.repository.FotosRepository
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Assume.assumeTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pruebas de HU-16 · Reglas de seguridad de los datos (RNF-15, RNF-16, RN-05, RN-12) contra las
 * reglas reales publicadas en Firestore y Supabase. Usan las cuentas prueba@ (usuario B, autor de
 * los anuncios de prueba) y prueba2@ (usuario A) de local.properties.
 *
 * Cada prueba intenta algo prohibido y comprueba que el servidor lo rechaza; no se cambia ni se
 * borra ningún dato real.
 */
@RunWith(AndroidJUnit4::class)
class SeguridadDatosTest {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val argumentos = InstrumentationRegistry.getArguments()
    private val anuncioDeB = db.collection(AnunciosRepository.COLECCION_ANUNCIOS).document("prueba-hu07-01")

    /** uid del usuario B (prueba@), dueño de los anuncios de prueba. */
    private lateinit var uidB: String

    @Before
    fun crearAnunciosDeB() = runBlocking {
        uidB = entrar("correoPrueba", "contrasenaPrueba")
        AnunciosDePrueba.crear(autorId = uidB, correo = argumentos.getString("correoPrueba").orEmpty())
        auth.signOut()
    }

    @After
    fun cerrarSesion() = auth.signOut()

    /** Criterio 1: sin sesión, leer «anuncios» es rechazado. */
    @Test
    fun sinSesion_leerAnuncios_rechazado() = runBlocking {
        rechazado("leer un anuncio sin sesión") { anuncioDeB.get(Source.SERVER).await() }
        rechazado("leer la lista sin sesión") {
            db.collection(AnunciosRepository.COLECCION_ANUNCIOS).limit(1).get(Source.SERVER).await()
        }
    }

    /** Criterio 2: el usuario A no puede editar ni borrar un anuncio del usuario B. */
    @Test
    fun otroUsuario_noEditaNiBorra() = runBlocking {
        entrar("correoPrueba2", "contrasenaPrueba2")
        // A sí puede ver el anuncio de B (RN-06)…
        assertEquals(uidB, anuncioDeB.get(Source.SERVER).await().getString("autorId"))
        // …pero no cambiarlo ni borrarlo (RNF-16).
        rechazado("editar un anuncio ajeno") { anuncioDeB.update("titulo", "Título cambiado por otro usuario").await() }
        rechazado("borrar un anuncio ajeno") { anuncioDeB.delete().await() }
    }

    /** Criterio 3: el usuario A no puede crear un anuncio a nombre de otro (autorId distinto a su uid). */
    @Test
    fun crearConOtroAutor_rechazado() = runBlocking {
        entrar("correoPrueba2", "contrasenaPrueba2")
        val nuevo = db.collection(AnunciosRepository.COLECCION_ANUNCIOS).document("prueba-hu16-ajeno")
        rechazado("crear un anuncio con otro autor") {
            nuevo.set(mapOf("autorId" to uidB, "titulo" to "Anuncio a nombre de otro", "categoria" to "empleos")).await()
        }
    }

    /** El autor sí edita su anuncio, pero no puede cambiar el autor (RN-05) ni la fecha (RN-12). */
    @Test
    fun autor_editaSinCambiarAutorNiFecha() = runBlocking {
        entrar("correoPrueba", "contrasenaPrueba")
        val titulo = anuncioDeB.get(Source.SERVER).await().getString("titulo")!!
        anuncioDeB.update("titulo", titulo).await() // Permitido: es su anuncio.
        rechazado("cambiar el autor") { anuncioDeB.update("autorId", "otro-usuario").await() }
        rechazado("cambiar la fecha") { anuncioDeB.update("fechaPublicacion", Timestamp.now()).await() }
    }

    /** Los perfiles solo los ve su dueño, y las demás colecciones (como «pruebas») están cerradas. */
    @Test
    fun perfilAjeno_yOtrasColecciones_rechazados() = runBlocking {
        entrar("correoPrueba2", "contrasenaPrueba2")
        val perfilDeB = db.collection(AuthRepository.COLECCION_USUARIOS).document(uidB)
        rechazado("leer el perfil de otro") { perfilDeB.get(Source.SERVER).await() }
        rechazado("escribir el perfil de otro") { perfilDeB.set(mapOf("telefono" to "000"), SetOptions.merge()).await() }
        rechazado("leer la colección pruebas") { db.collection("pruebas").limit(1).get(Source.SERVER).await() }
    }

    /**
     * Criterio 4 (RNF-17): el perfil guardado en Firestore solo tiene nombre y teléfono; ningún
     * campo de contraseña. La contraseña solo la maneja Firebase Authentication.
     */
    @Test
    fun perfilSinContrasena() = runBlocking {
        val uid = entrar("correoPrueba2", "contrasenaPrueba2")
        val campos = db.collection(AuthRepository.COLECCION_USUARIOS).document(uid)
            .get(Source.SERVER).await().data.orEmpty().keys
        assertTrue("Campos inesperados en el perfil: $campos", campos.all { it in setOf("nombre", "telefono") })
    }

    /** Supabase: sin sesión no se ve ninguna foto; con sesión sí, aunque sea de otro usuario. */
    @Test
    fun fotos_soloConSesion() = runBlocking {
        val enlace = AnunciosDePrueba.foto(1)
        // Supabase guarda en su caché las fotos ya vistas hasta 1 hora; con «?sin-cache=…» se pregunta
        // directamente al bucket, que es lo que deciden las reglas.
        val sinSesion = OkHttpClient()
        assertNotEquals("Sin sesión la foto no se debe ver", 200, codigo(sinSesion, "$enlace?sin-cache=${System.nanoTime()}"))

        entrar("correoPrueba2", "contrasenaPrueba2")
        val conSesion = OkHttpClient.Builder().addInterceptor(SesionEnFotos()).build()
        assertEquals("Con sesión la foto se ve", 200, codigo(conSesion, enlace))
    }

    /** Supabase: el usuario A no puede subir ni borrar fotos en la carpeta del usuario B. */
    @Test
    fun fotos_noSeSubeNiBorraEnCarpetaAjena() = runBlocking {
        entrar("correoPrueba2", "contrasenaPrueba2")
        val bucket = SupabaseCliente.cliente.storage.from(FotosRepository.BUCKET)
        rechazado("subir a la carpeta de otro") { bucket.upload("$uidB/prueba-hu16/1.jpg", byteArrayOf(1, 2, 3)) }
        // Borrar algo ajeno no da error en Supabase, pero no borra nada: la foto de prueba sigue ahí.
        runCatching { bucket.delete("pruebas/foto1.jpg") }
        val conSesion = OkHttpClient.Builder().addInterceptor(SesionEnFotos()).build()
        assertEquals("La foto ajena no se borró", 200, codigo(conSesion, AnunciosDePrueba.foto(1)))
    }

    // --- Ayudas ---

    /** Inicia sesión con la cuenta de esos argumentos y devuelve su uid; si faltan, se salta la prueba. */
    private suspend fun entrar(argCorreo: String, argContrasena: String): String {
        val correo = argumentos.getString(argCorreo).orEmpty()
        val contrasena = argumentos.getString(argContrasena).orEmpty()
        assumeTrue("Falta la cuenta de prueba en local.properties", correo.isNotBlank() && contrasena.isNotBlank())
        return auth.signInWithEmailAndPassword(correo, contrasena).await().user!!.uid
    }

    /** Ejecuta [accion] y comprueba que el servidor la rechazó. */
    private suspend fun rechazado(que: String, accion: suspend () -> Unit) {
        try {
            accion()
        } catch (e: FirebaseFirestoreException) {
            assertEquals("$que: debe ser falta de permiso", FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
            return
        } catch (e: Exception) {
            return // Supabase responde con su propio error (sin permiso).
        }
        fail("Se permitió $que y debía rechazarse")
    }

    /** Código de respuesta HTTP al pedir [enlace] con ese cliente. */
    private suspend fun codigo(cliente: OkHttpClient, enlace: String): Int = withContext(Dispatchers.IO) {
        cliente.newCall(Request.Builder().url(enlace).build()).execute().use { it.code }
    }
}
