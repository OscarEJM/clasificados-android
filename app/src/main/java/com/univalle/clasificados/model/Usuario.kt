package com.univalle.clasificados.model

/**
 * Perfil de un usuario en la colección «usuarios» de Firestore; el id del documento es su uid
 * de Firebase (sección 1.8). El correo vive en Firebase Authentication y la contraseña nunca se
 * guarda en Firestore (RNF-17). Los valores por defecto permiten que Firestore lea el documento.
 *
 * @property nombre nombre completo, obligatorio.
 * @property telefono teléfono opcional; `null` si el usuario no lo escribió.
 */
data class Usuario(
    val nombre: String = "",
    val telefono: String? = null
)
