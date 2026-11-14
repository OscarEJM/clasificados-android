package com.univalle.clasificados

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.model.Categoria
import kotlinx.coroutines.tasks.await
import java.util.Date

/**
 * Anuncios de prueba de HU-07 · Ver los anuncios de una categoría. Los crea directamente en
 * Firestore el usuario de prueba (PRUEBA_CORREO), como pide la explicación técnica de la historia.
 *
 * Cada anuncio tiene un id fijo («prueba-hu07-…»), así que volver a correr las pruebas los
 * reescribe en vez de duplicarlos. Las fechas también son fijas y distintas, para comprobar el
 * orden «Más recientes primero» (RN-13); en la app real la fecha la pone el servidor (RN-12).
 *
 * - Empleos: 5 anuncios (uno con título largo para ver el corte a dos líneas).
 * - Vivienda: 3 anuncios (uno gratis, que se muestra «$0»).
 * - Eventos: ninguno, para el estado «No se encontraron anuncios» (RF-15).
 */
object AnunciosDePrueba {

    /** Foto de prueba que se subió al bucket en HU-02 (Supabase Storage, pública). */
    const val FOTO =
        "https://ruwqqnprgpjplvfgznup.supabase.co/storage/v1/object/public/anuncios-fotos/pruebas/prueba.png"

    /** Categoría que se deja sin anuncios de prueba. */
    val CATEGORIA_VACIA = Categoria.EVENTOS

    /** Datos que cambian entre un anuncio de prueba y otro; la fecha va en hora de Colombia. */
    private data class Semilla(
        val id: String,
        val categoria: Categoria,
        val titulo: String,
        val precio: Long,
        val ciudad: String,
        val fecha: String
    )

    private val semillas = listOf(
        Semilla("prueba-hu07-01", Categoria.EMPLEOS, "Auxiliar contable medio tiempo", 1_800_000, "Tuluá", "2026-11-13T09:00:00"),
        Semilla("prueba-hu07-02", Categoria.EMPLEOS, "Desarrollador Android junior con experiencia en Kotlin y Jetpack Compose para startup", 3_500_000, "Cali", "2026-11-12T15:30:00"),
        Semilla("prueba-hu07-03", Categoria.EMPLEOS, "Mesero para fines de semana", 60_000, "Bogotá", "2026-11-11T18:00:00"),
        Semilla("prueba-hu07-04", Categoria.EMPLEOS, "Profesor de inglés a domicilio", 1_200_000, "Medellín", "2026-11-09T08:15:00"),
        Semilla("prueba-hu07-05", Categoria.EMPLEOS, "Conductor con licencia C2", 2_400_000, "Pereira", "2026-11-05T11:45:00"),
        Semilla("prueba-hu07-06", Categoria.VIVIENDA, "Apartamento de 2 habitaciones cerca a la Univalle", 950_000, "Tuluá", "2026-11-13T07:20:00"),
        Semilla("prueba-hu07-07", Categoria.VIVIENDA, "Habitación amoblada para estudiante", 450_000, "Cali", "2026-11-10T19:10:00"),
        Semilla("prueba-hu07-08", Categoria.VIVIENDA, "Regalo cama sencilla en buen estado", 0, "Manizales", "2026-11-08T10:00:00")
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
        semillas.forEach { s ->
            val anuncio = Anuncio(
                autorId = autorId,
                categoria = s.categoria.id,
                titulo = s.titulo,
                tituloMinusculas = s.titulo.lowercase(),
                descripcion = "Anuncio de prueba de HU-07 · Ver los anuncios de una categoría.",
                precio = s.precio,
                fotos = listOf(FOTO),
                ciudad = s.ciudad,
                correo = correo,
                telefono = null,
                fechaPublicacion = Timestamp(formato.parse(s.fecha) as Date)
            )
            coleccion.document(s.id).set(anuncio).await()
        }
    }
}
