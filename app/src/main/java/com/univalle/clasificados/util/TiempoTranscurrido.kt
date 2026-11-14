package com.univalle.clasificados.util

/**
 * Texto del tiempo que pasó desde la publicación de un anuncio, siempre en español
 * (HU-08 · Ver el detalle de un anuncio, RF-14). Por ejemplo «Hace 2 horas».
 *
 * No se usa la función de Android `DateUtils.getRelativeTimeSpanString` porque escribe en el
 * idioma del teléfono: en un teléfono en inglés diría «2 hours ago» (acordado con Oscar).
 * Son funciones puras, sin Android, para probarlas con JUnit.
 *
 * Textos aprobados por Oscar el 14/11/2026:
 * - menos de 1 minuto: «Hace un momento»
 * - minutos, horas y días: «Hace 1 minuto», «Hace 5 minutos», «Hace 1 hora», «Hace 3 días»…
 * - desde 30 días: meses («Hace 1 mes», «Hace 2 meses»)
 * - desde 365 días: años («Hace 1 año», «Hace 2 años»)
 */
object TiempoTranscurrido {

    private const val MINUTO = 60_000L
    private const val HORA = 60 * MINUTO
    private const val DIA = 24 * HORA
    private const val MES = 30 * DIA
    private const val ANIO = 365 * DIA

    /**
     * @param publicadoMillis momento de la publicación, en milisegundos.
     * @param ahoraMillis momento actual, en milisegundos. Si la fecha queda en el futuro (por
     * ejemplo, el reloj del teléfono va atrasado) se muestra «Hace un momento».
     */
    fun texto(publicadoMillis: Long, ahoraMillis: Long): String {
        val pasado = ahoraMillis - publicadoMillis
        return when {
            pasado < MINUTO -> "Hace un momento"
            pasado < HORA -> hace(pasado / MINUTO, "minuto", "minutos")
            pasado < DIA -> hace(pasado / HORA, "hora", "horas")
            pasado < MES -> hace(pasado / DIA, "día", "días")
            // Entre 360 y 364 días la cuenta daría «12 meses»; se deja en 11 hasta cumplir el año.
            pasado < ANIO -> hace(minOf(pasado / MES, 11), "mes", "meses")
            else -> hace(pasado / ANIO, "año", "años")
        }
    }

    /** «Hace 1 hora» o «Hace N horas», según la cantidad. */
    private fun hace(cantidad: Long, singular: String, plural: String) =
        "Hace $cantidad ${if (cantidad == 1L) singular else plural}"
}
