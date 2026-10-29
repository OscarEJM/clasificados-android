package com.univalle.clasificados.ui.navegacion

/** Rutas de navegación de la aplicación. */
object Rutas {
    const val BIENVENIDA = "bienvenida"
    const val INICIO_SESION = "inicio_sesion"
    const val REGISTRO = "registro"
    const val INICIO = "inicio"
    const val PUBLICAR = "publicar"
    const val MIS_ANUNCIOS = "mis_anuncios"
    const val AJUSTES = "ajustes"

    /** Pantallas que se pueden ver sin sesión: 00, 01 y 02 (RF-09). Registro llega en HU-04. */
    val PUBLICAS = setOf(BIENVENIDA, INICIO_SESION, REGISTRO)

    /** Pantallas que muestran la barra inferior y el botón «Publicar». */
    val CON_BARRA = setOf(INICIO, PUBLICAR, MIS_ANUNCIOS, AJUSTES)
}
