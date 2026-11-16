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

    /** Pantalla 04 · Todas las categorías (HU-06). */
    const val TODAS_CATEGORIAS = "todas_categorias"

    /** Nombre del argumento con el id de la categoría en la ruta del listado. */
    const val ARG_CATEGORIA = "categoriaId"

    /** Listado de anuncios de una categoría (pantalla 05, HU-07). */
    const val LISTADO_CATEGORIA = "listado/{$ARG_CATEGORIA}"

    /** Ruta del listado de la categoría con ese [categoriaId]. */
    fun listado(categoriaId: String) = "listado/$categoriaId"

    /** Nombre del argumento con el id del anuncio en la ruta del detalle (HU-08). */
    const val ARG_ANUNCIO = "anuncioId"

    /** Pantalla 07 · Detalle del anuncio (HU-08). No muestra la barra inferior, como en Figma. */
    const val DETALLE = "detalle/{$ARG_ANUNCIO}"

    /** Ruta del detalle del anuncio con ese [anuncioId]. */
    fun detalle(anuncioId: String) = "detalle/$anuncioId"

    /** Pantalla 12 · Confirmación del anuncio recién publicado (HU-14). Sin barra inferior, como en Figma. */
    const val CONFIRMACION = "confirmacion/{$ARG_ANUNCIO}"

    /** Ruta de Confirmación del anuncio con ese [anuncioId]. */
    fun confirmacion(anuncioId: String) = "confirmacion/$anuncioId"

    /** Nombre del argumento con el texto buscado en la ruta de resultados (HU-15). */
    const val ARG_TEXTO = "texto"

    /** Pantalla 03b · Resultados de búsqueda desde Inicio (HU-15). Lleva la barra inferior con «Inicio» marcado. */
    const val BUSQUEDA = "busqueda/{$ARG_TEXTO}"

    /** Ruta de resultados para ese [texto]; se codifica para que «/», «?» o tildes no rompan la ruta. */
    fun busqueda(texto: String) = "busqueda/${android.net.Uri.encode(texto)}"

    /** Pantallas que se pueden ver sin sesión: 00, 01 y 02 (RF-09). */
    val PUBLICAS = setOf(BIENVENIDA, INICIO_SESION, REGISTRO)

    /**
     * Pestaña de la barra inferior que se marca en cada pantalla: Todas las categorías, el
     * listado y los resultados de búsqueda se abren desde Inicio, así que marcan «Inicio».
     */
    fun pestanaDe(ruta: String?): String? =
        if (ruta == TODAS_CATEGORIAS || ruta == LISTADO_CATEGORIA || ruta == BUSQUEDA) INICIO else ruta

    /**
     * Pantallas que muestran la barra inferior y el botón «Publicar». Publicar no la lleva: en
     * Figma (pantallas 08 a 11) tiene su propia barra con flecha atrás (HU-12).
     */
    val CON_BARRA = setOf(INICIO, MIS_ANUNCIOS, AJUSTES, TODAS_CATEGORIAS, LISTADO_CATEGORIA, BUSQUEDA)
}
