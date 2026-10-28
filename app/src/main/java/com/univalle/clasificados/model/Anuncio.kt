package com.univalle.clasificados.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

/**
 * Anuncio guardado en la colección «anuncios» de Firestore (GUIA_DESARROLLO.md, sección 1.8).
 * Los valores por defecto permiten que Firestore convierta los documentos en esta clase.
 */
data class Anuncio(
    @DocumentId val id: String = "",
    val autorId: String = "",
    val categoria: String = "",
    val titulo: String = "",
    val tituloMinusculas: String = "",
    val descripcion: String = "",
    val precio: Long = 0,
    val fotos: List<String> = emptyList(),
    val ciudad: String = "",
    val correo: String = "",
    val telefono: String? = null,
    @ServerTimestamp val fechaPublicacion: Timestamp? = null
)
