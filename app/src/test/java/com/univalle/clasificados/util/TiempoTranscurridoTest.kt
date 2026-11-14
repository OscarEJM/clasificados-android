package com.univalle.clasificados.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias del texto «Hace …» del detalle del anuncio (HU-08 · RF-14, criterio 4).
 * Se ejecutan sin emulador: ./gradlew testDebugUnitTest
 */
class TiempoTranscurridoTest {

    private val ahora = 1_800_000_000_000L
    private val segundo = 1_000L
    private val minuto = 60 * segundo
    private val hora = 60 * minuto
    private val dia = 24 * hora

    private fun hace(milisegundos: Long) = TiempoTranscurrido.texto(ahora - milisegundos, ahora)

    /** Criterio 4: un anuncio de hace dos horas dice «Hace 2 horas». */
    @Test
    fun dosHoras() = assertEquals("Hace 2 horas", hace(2 * hora + 10 * minuto))

    /** Menos de un minuto, o una fecha en el futuro, dice «Hace un momento». */
    @Test
    fun recienPublicado() {
        assertEquals("Hace un momento", hace(30 * segundo))
        assertEquals("Hace un momento", hace(-5 * minuto))
    }

    /** Singular con 1 y plural con más. */
    @Test
    fun singularYPlural() {
        assertEquals("Hace 1 minuto", hace(minuto))
        assertEquals("Hace 45 minutos", hace(45 * minuto))
        assertEquals("Hace 1 hora", hace(hora))
        assertEquals("Hace 1 día", hace(dia))
        assertEquals("Hace 29 días", hace(29 * dia))
    }

    /** Desde 30 días cuenta meses y desde 365 días cuenta años. */
    @Test
    fun mesesYAnios() {
        assertEquals("Hace 1 mes", hace(30 * dia))
        assertEquals("Hace 11 meses", hace(364 * dia))
        assertEquals("Hace 1 año", hace(365 * dia))
        assertEquals("Hace 2 años", hace(800 * dia))
    }
}
