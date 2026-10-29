package com.univalle.clasificados.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.clasificados.model.Usuario
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/** Resultado de intentar crear una cuenta (HU-04). */
enum class ResultadoRegistro {
    /** La cuenta se creó y el usuario quedó con la sesión iniciada (RN-04). */
    CREADA,

    /** El correo ya pertenece a otra cuenta (RF-05). */
    CORREO_EXISTENTE,

    /** No hay internet (RN-18). */
    SIN_CONEXION,

    /** Cualquier otro fallo de Firebase. */
    ERROR
}

/**
 * Acceso a Firebase Authentication y al perfil del usuario en Firestore. Es la única clase que
 * habla con Firebase sobre la sesión y las cuentas; las pantallas lo hacen a través de los
 * ViewModels. Firebase guarda la sesión por sí solo, así que la app no guarda nada en el
 * dispositivo (RNF-06) y nunca guarda contraseñas (RNF-17).
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Emite `true` cuando hay un usuario con sesión y `false` cuando no lo hay (HU-03). Emite el
     * estado actual apenas alguien empieza a escuchar y vuelve a emitir cada vez que la sesión
     * cambia (por ejemplo al registrarse, iniciar o cerrar sesión).
     */
    val sesionActiva: Flow<Boolean> = callbackFlow {
        val oyente = FirebaseAuth.AuthStateListener { trySend(it.currentUser != null) }
        auth.addAuthStateListener(oyente)
        awaitClose { auth.removeAuthStateListener(oyente) }
    }

    /**
     * Crea la cuenta con correo y contraseña (RF-03) y guarda nombre y teléfono en
     * `usuarios/{uid}`. Al crearse, Firebase deja la sesión iniciada y la navegación lleva a Inicio.
     * Se ejecuta sin poder cancelarse: al iniciarse la sesión la pantalla de Registro se cierra,
     * y el perfil se debe guardar igual.
     *
     * @param telefono teléfono opcional; vacío se guarda como `null`.
     */
    suspend fun registrar(
        nombre: String,
        correo: String,
        telefono: String,
        contrasena: String
    ): ResultadoRegistro = withContext(NonCancellable) {
        try {
            val resultado = auth.createUserWithEmailAndPassword(correo, contrasena).await()
            val uid = resultado.user?.uid ?: return@withContext ResultadoRegistro.ERROR
            val perfil = Usuario(nombre = nombre, telefono = telefono.ifBlank { null })
            db.collection(COLECCION_USUARIOS).document(uid).set(perfil).await()
            ResultadoRegistro.CREADA
        } catch (e: FirebaseAuthUserCollisionException) {
            ResultadoRegistro.CORREO_EXISTENTE
        } catch (e: FirebaseNetworkException) {
            ResultadoRegistro.SIN_CONEXION
        } catch (e: Exception) {
            ResultadoRegistro.ERROR
        }
    }

    companion object {
        /** Colección de perfiles de usuario en Firestore (sección 1.8). */
        const val COLECCION_USUARIOS = "usuarios"
    }
}
