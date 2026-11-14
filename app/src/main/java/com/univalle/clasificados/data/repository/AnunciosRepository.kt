package com.univalle.clasificados.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.univalle.clasificados.model.Anuncio
import kotlinx.coroutines.tasks.await

/** Acceso a los anuncios en Cloud Firestore (RNF-05). Las pantallas llegan aquí a través de su ViewModel. */
class AnunciosRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Anuncios de una categoría, del más reciente al más antiguo (HU-07 · RF-12, RN-13).
     *
     * Se piden siempre al servidor ([Source.SERVER]): como la caché de Firestore está solo en
     * memoria (RN-17), pedirlos a la caché sin internet daría una lista vacía en vez de un error.
     * Sin conexión, la función lanza una excepción y el ViewModel muestra el mensaje de RN-18.
     *
     * Firestore necesita un índice compuesto (categoria + fechaPublicacion descendente) para
     * esta consulta; se crea una sola vez desde la consola de Firebase.
     *
     * @param categoriaId id de la categoría, por ejemplo «empleos».
     */
    suspend fun anunciosDeCategoria(categoriaId: String): List<Anuncio> =
        db.collection(COLECCION_ANUNCIOS)
            .whereEqualTo(CAMPO_CATEGORIA, categoriaId)
            .orderBy(CAMPO_FECHA, Query.Direction.DESCENDING)
            .get(Source.SERVER)
            .await()
            .toObjects(Anuncio::class.java)

    /**
     * Un anuncio por su id, para la pantalla 07 · Detalle (HU-08 · RF-14). Se pide al servidor
     * por la misma razón que [anunciosDeCategoria]: sin internet debe dar error.
     *
     * @param id id del documento en la colección «anuncios».
     * @return el anuncio, o `null` si ya no existe (por ejemplo, lo borró su autor).
     */
    suspend fun anuncio(id: String): Anuncio? =
        db.collection(COLECCION_ANUNCIOS).document(id)
            .get(Source.SERVER)
            .await()
            .toObject(Anuncio::class.java)

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
        /** Campo con el id de la categoría del anuncio (sección 1.8). */
        const val CAMPO_CATEGORIA = "categoria"
        /** Campo con la fecha de publicación puesta por el servidor (RN-12). */
        const val CAMPO_FECHA = "fechaPublicacion"
        private const val COLECCION_PRUEBAS = "pruebas"
        const val MENSAJE_PRUEBA = "Prueba de conexión desde la app Clasificados"
    }
}
