package com.univalle.clasificados.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/** Acceso a los anuncios en Cloud Firestore. */
class AnunciosRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    // --- Funciones de prueba de conexión (HU-02). Escriben y leen en la colección «pruebas». ---

    /** Escribe un documento de prueba y devuelve su id. */
    suspend fun escribirPrueba(): String {
        val referencia = db.collection(COLECCION_PRUEBAS)
            .add(
                mapOf(
                    "mensaje" to MENSAJE_PRUEBA,
                    "fecha" to FieldValue.serverTimestamp()
                )
            )
            .await()
        return referencia.id
    }

    /** Lee el mensaje del documento de prueba con el id dado. */
    suspend fun leerPrueba(id: String): String? =
        db.collection(COLECCION_PRUEBAS).document(id).get().await().getString("mensaje")

    companion object {
        const val COLECCION_ANUNCIOS = "anuncios"
        private const val COLECCION_PRUEBAS = "pruebas"
        const val MENSAJE_PRUEBA = "Prueba de conexión desde la app Clasificados"
    }
}
