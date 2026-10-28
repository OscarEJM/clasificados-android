package com.univalle.clasificados.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/** Formato de precios en pesos colombianos (RN-21): 1200000 → «$1.200.000». */
object FormatoPrecio {

    /** Formato sin decimales y con punto como separador de miles (es-CO). */
    private val formato = DecimalFormat("#,##0", DecimalFormatSymbols(Locale.forLanguageTag("es-CO")).apply {
        groupingSeparator = '.'
    })

    /**
     * Convierte un precio en pesos a texto. El símbolo «$» va pegado al número.
     * @param precio valor entero en pesos colombianos; 0 significa gratis y se muestra «$0» (RN-09).
     */
    fun formatear(precio: Long): String = "$" + formato.format(precio)
}
