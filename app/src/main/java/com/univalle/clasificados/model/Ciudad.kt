package com.univalle.clasificados.model

/**
 * Las 10 ciudades del catálogo fijo (sección 1.4 de la guía), en el orden en que se muestran.
 * En Firestore el anuncio guarda el [nombre] de la ciudad (sección 1.8). Se usan en el paso 4 de
 * Publicar (HU-14 · Publicar anuncio: ubicación, contacto y confirmación; RF-25) y más adelante en
 * los filtros (HU-17).
 *
 * @property nombre nombre visible y guardado, con tildes, por ejemplo «Bogotá».
 */
enum class Ciudad(val nombre: String) {
    BOGOTA("Bogotá"),
    MEDELLIN("Medellín"),
    CALI("Cali"),
    BARRANQUILLA("Barranquilla"),
    CARTAGENA("Cartagena"),
    BUCARAMANGA("Bucaramanga"),
    PEREIRA("Pereira"),
    MANIZALES("Manizales"),
    PASTO("Pasto"),
    TULUA("Tuluá")
}
