package com.univalle.clasificados

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.model.Categoria
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Anuncios de prueba de HU-07 · Ver los anuncios de una categoría y HU-08 · Ver el detalle de un
 * anuncio. Los crea directamente en Firestore el usuario de prueba (PRUEBA_CORREO), como pide la
 * explicación técnica de HU-07.
 *
 * Cada anuncio tiene un id fijo («prueba-hu07-…» o «prueba-hu08-…»), así que volver a correr las
 * pruebas los reescribe en vez de duplicarlos. Las fechas de HU-07 son fijas y distintas, para
 * comprobar el orden «Más recientes primero» (RN-13); en la app real la fecha la pone el
 * servidor (RN-12).
 *
 * - Empleos: 5 anuncios (uno con título largo para ver el corte a dos líneas).
 * - Vivienda: 3 anuncios (uno gratis, que se muestra «$0»).
 * - Servicios: 1 anuncio con 5 fotos, publicado «hace 2 horas» cada vez que corren las pruebas (HU-08).
 * - Eventos: ninguno, para el estado «No se encontraron anuncios» (RF-15).
 * - Con teléfono: «Auxiliar contable medio tiempo» y «Apartamento de 2 habitaciones…» (HU-08).
 *
 * Las fotos son `pruebas/foto1.jpg` a `pruebas/foto5.jpg` del bucket (cuadros de colores
 * numerados del 1 al 5, que Oscar sube a Supabase) y la imagen `pruebas/prueba.png` de HU-02.
 */
object AnunciosDePrueba {

    /** Carpeta pública de las fotos de prueba en Supabase Storage (bucket «anuncios-fotos»). */
    private const val CARPETA =
        "https://ruwqqnprgpjplvfgznup.supabase.co/storage/v1/object/public/anuncios-fotos/pruebas"

    /** URL de la foto de prueba número [n] (1 a 5); con 0 da la imagen de HU-02. */
    fun foto(n: Int) = if (n == 0) "$CARPETA/prueba.png" else "$CARPETA/foto$n.jpg"

    /** Categoría que se deja sin anuncios de prueba. */
    val CATEGORIA_VACIA = Categoria.EVENTOS

    /** Id del anuncio de HU-08 publicado hace dos horas y con 5 fotos. */
    const val ID_HACE_DOS_HORAS = "prueba-hu08-01"

    /** Título del anuncio publicado hace dos horas. */
    const val TITULO_HACE_DOS_HORAS = "Clases de guitarra para principiantes"

    /** Teléfono de contacto de los anuncios de prueba que lo tienen. */
    const val TELEFONO = "(+57) 300 123 4567"

    /**
     * Datos que cambian entre un anuncio de prueba y otro.
     * @property fecha en hora de Colombia; `null` significa «hace dos horas».
     * @property fotos números de las fotos de prueba, en orden.
     */
    private data class Semilla(
        val id: String,
        val categoria: Categoria,
        val titulo: String,
        val precio: Long,
        val ciudad: String,
        val fecha: String?,
        val fotos: List<Int>,
        val telefono: String? = null
    )

    private val semillas = listOf(
        Semilla("prueba-hu07-01", Categoria.EMPLEOS, "Auxiliar contable medio tiempo", 1_800_000, "Tuluá", "2026-11-13T09:00:00", listOf(1, 2, 3), TELEFONO),
        Semilla("prueba-hu07-02", Categoria.EMPLEOS, "Desarrollador Android junior con experiencia en Kotlin y Jetpack Compose para startup", 3_500_000, "Cali", "2026-11-12T15:30:00", listOf(2)),
        Semilla("prueba-hu07-03", Categoria.EMPLEOS, "Mesero para fines de semana", 60_000, "Bogotá", "2026-11-11T18:00:00", listOf(3)),
        Semilla("prueba-hu07-04", Categoria.EMPLEOS, "Profesor de inglés a domicilio", 1_200_000, "Medellín", "2026-11-09T08:15:00", listOf(4)),
        Semilla("prueba-hu07-05", Categoria.EMPLEOS, "Conductor con licencia C2", 2_400_000, "Pereira", "2026-11-05T11:45:00", listOf(5)),
        Semilla("prueba-hu07-06", Categoria.VIVIENDA, "Apartamento de 2 habitaciones cerca a la Univalle", 950_000, "Tuluá", "2026-11-13T07:20:00", listOf(4, 5), TELEFONO),
        Semilla("prueba-hu07-07", Categoria.VIVIENDA, "Habitación amoblada para estudiante", 450_000, "Cali", "2026-11-10T19:10:00", listOf(1)),
        Semilla("prueba-hu07-08", Categoria.VIVIENDA, "Regalo cama sencilla en buen estado", 0, "Manizales", "2026-11-08T10:00:00", listOf(0)),
        Semilla(ID_HACE_DOS_HORAS, Categoria.SERVICIOS, TITULO_HACE_DOS_HORAS, 80_000, "Tuluá", null, listOf(1, 2, 3, 4, 5))
    )

    /**
     * Crea o reescribe los anuncios de prueba con el usuario que tenga la sesión iniciada.
     * @param autorId uid del usuario de prueba.
     * @param correo correo de contacto que llevan los anuncios.
     */
    suspend fun crear(autorId: String, correo: String) {
        val coleccion = FirebaseFirestore.getInstance().collection(AnunciosRepository.COLECCION_ANUNCIOS)
        val formato = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("America/Bogota")
        }
        val haceDosHoras = Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L)
        semillas.forEach { s ->
            val anuncio = Anuncio(
                autorId = autorId,
                categoria = s.categoria.id,
                titulo = s.titulo,
                tituloMinusculas = s.titulo.lowercase(),
                descripcion = "Anuncio de prueba de HU-07 y HU-08. Sirve para revisar el listado de " +
                    "la categoría y el detalle del anuncio.",
                precio = s.precio,
                fotos = s.fotos.map(::foto),
                ciudad = s.ciudad,
                correo = correo,
                telefono = s.telefono,
                fechaPublicacion = Timestamp(s.fecha?.let { formato.parse(it) as Date } ?: haceDosHoras)
            )
            // Las reglas de HU-16 no dejan cambiar la fecha de un anuncio al editarlo (RN-12): el
            // anuncio «hace dos horas» cambia de fecha en cada ejecución, así que se borra y se crea.
            if (s.fecha == null) coleccion.document(s.id).delete().await()
            coleccion.document(s.id).set(anuncio).await()
        }
    }

    // --- HU-15 · Buscar anuncios por título (aprobado por Oscar el 16/11/2026) ---

    /** Palabra que solo aparece en la descripción de un anuncio de prueba, nunca en un título (criterio 3). */
    const val PALABRA_SOLO_DESCRIPCION = "acuarela"

    /** Títulos con «bici» en dos categorías distintas: Deportes y Vehículos (criterios 1 y 2). */
    const val TITULO_BICI_DEPORTES = "Bicicleta de ruta talla M"
    const val TITULO_BICI_VEHICULOS = "Bicicleta eléctrica de segunda"

    /** Anuncio de HU-15 cuya descripción tiene [PALABRA_SOLO_DESCRIPCION] pero su título no. */
    const val TITULO_PINTURA = "Clases de pintura para niños"

    /**
     * Crea o reescribe los 3 anuncios de prueba de HU-15 («prueba-hu15-01» a «03»). Se quedan en
     * la base para que Oscar también pueda probar la búsqueda a mano. Ninguno está en Empleos,
     * Vivienda ni Eventos, para no cambiar los conteos de las pruebas de HU-07.
     */
    suspend fun crearHu15(autorId: String, correo: String) {
        val coleccion = FirebaseFirestore.getInstance().collection(AnunciosRepository.COLECCION_ANUNCIOS)
        val formato = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("America/Bogota")
        }
        listOf(
            Triple("prueba-hu15-01", Semilla("prueba-hu15-01", Categoria.DEPORTES, TITULO_BICI_DEPORTES, 1_200_000, "Cali", "2026-11-15T10:00:00", listOf(1)),
                "Bicicleta de ruta en aluminio, cambios Shimano. Anuncio de prueba de HU-15."),
            Triple("prueba-hu15-02", Semilla("prueba-hu15-02", Categoria.VEHICULOS, TITULO_BICI_VEHICULOS, 2_800_000, "Bogotá", "2026-11-15T09:00:00", listOf(2)),
                "Bicicleta eléctrica con batería nueva y cargador. Anuncio de prueba de HU-15."),
            Triple("prueba-hu15-03", Semilla("prueba-hu15-03", Categoria.EDUCACION, TITULO_PINTURA, 120_000, "Tuluá", "2026-11-15T08:00:00", listOf(3)),
                "Aprenden dibujo y $PALABRA_SOLO_DESCRIPCION los sábados. Anuncio de prueba de HU-15.")
        ).forEach { (id, s, descripcion) ->
            val anuncio = Anuncio(
                autorId = autorId,
                categoria = s.categoria.id,
                titulo = s.titulo,
                tituloMinusculas = s.titulo.lowercase(),
                descripcion = descripcion,
                precio = s.precio,
                fotos = s.fotos.map(::foto),
                ciudad = s.ciudad,
                correo = correo,
                fechaPublicacion = Timestamp(formato.parse(s.fecha!!) as Date)
            )
            coleccion.document(id).set(anuncio).await()
        }
    }
}
