package com.univalle.clasificados.viewmodel

import androidx.annotation.StringRes

/** Estado de una pantalla marcador: por ahora solo su título. Cada historia lo reemplaza por el estado real. */
data class MarcadorUiState(
    @StringRes val titulo: Int
)
