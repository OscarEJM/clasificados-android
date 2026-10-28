package com.univalle.clasificados.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/** Formato de precios en pesos colombianos (RN-21): 1200000 → «$1.200.000». */
object FormatoPrecio {

    private val formato = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.forLanguageTag("es-CO")).apply {
        groupingSeparator = '.'
    })

    fun formatear(precio: Long): String = "$" + formato.format(precio)
}
