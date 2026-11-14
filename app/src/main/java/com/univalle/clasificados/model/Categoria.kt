package com.univalle.clasificados.model

import androidx.annotation.DrawableRes
import com.univalle.clasificados.R

/**
 * Las 13 categorías fijas de la aplicación, en el orden de la sección 1.4 de la guía (HU-06:
 * RF-10 y RF-11). No se guardan en la nube: cada anuncio guarda solo el [id] de su categoría.
 * Los íconos y colores vienen de Figma (pantallas 03 y 04, claro y oscuro).
 *
 * @property id identificador que se guarda en Firestore y viaja en la navegación (sin tildes).
 * @property nombre nombre visible en la app.
 * @property icono ícono de línea de Figma (28 dp); el color se aplica con tint.
 * @property fondoClaro color del fondo del ícono en modo claro.
 * @property iconoClaro color del ícono en modo claro. En modo oscuro el fondo es este mismo color
 *   con opacidad del 18 % (sección 1.4).
 * @property iconoOscuro color aclarado del ícono en modo oscuro, tomado de la pantalla 04 oscura.
 */
enum class Categoria(
    val id: String,
    val nombre: String,
    @DrawableRes val icono: Int,
    val fondoClaro: Long,
    val iconoClaro: Long,
    val iconoOscuro: Long
) {
    EMPLEOS("empleos", "Empleos", R.drawable.ic_empleos, 0xFFDBEAFE, 0xFF2563EB, 0xFF65A3FF),
    VIVIENDA("vivienda", "Vivienda", R.drawable.ic_vivienda, 0xFFDCFCE7, 0xFF16A34A, 0xFF56E38A),
    SERVICIOS("servicios", "Servicios", R.drawable.ic_servicios, 0xFFF3E8FF, 0xFF9333EA, 0xFFD373FF),
    COMUNIDAD("comunidad", "Comunidad", R.drawable.ic_comunidad, 0xFFFFEDD5, 0xFFEA580C, 0xFFFF984C),
    VEHICULOS("vehiculos", "Vehículos", R.drawable.ic_vehiculos, 0xFFFEE2E2, 0xFFDC2626, 0xFFFF6666),
    TECNOLOGIA("tecnologia", "Tecnología", R.drawable.ic_tecnologia, 0xFFCFFAFE, 0xFF0891B2, 0xFF48D1F2),
    ELECTRONICA("electronica", "Electrónica", R.drawable.ic_electronica, 0xFFFEF9C3, 0xFFCA8A04, 0xFFFFCA44),
    MASCOTAS("mascotas", "Mascotas", R.drawable.ic_mascotas, 0xFFFCE7F3, 0xFFDB2777, 0xFFFF67B7),
    EDUCACION("educacion", "Educación", R.drawable.ic_educacion, 0xFFE0E7FF, 0xFF4F46E5, 0xFF8F86FF),
    SALUD("salud", "Salud", R.drawable.ic_salud, 0xFFD1FAE5, 0xFF059669, 0xFF45D6A9),
    EVENTOS("eventos", "Eventos", R.drawable.ic_eventos, 0xFFEDE9FE, 0xFF7C3AED, 0xFFBC7AFF),
    MODA("moda", "Moda", R.drawable.ic_moda, 0xFFFFE4E6, 0xFFE11D48, 0xFFFF5D88),
    DEPORTES("deportes", "Deportes", R.drawable.ic_deportes, 0xFFECFCCB, 0xFF65A30D, 0xFFA5E34D);

    companion object {
        /** Las 5 primeras, que Inicio muestra junto a la tarjeta «Más categorías» (RF-10). */
        val PRINCIPALES: List<Categoria> = entries.take(5)

        /** Busca una categoría por su [id]; devuelve `null` si no existe. */
        fun desdeId(id: String?): Categoria? = entries.firstOrNull { it.id == id }
    }
}
