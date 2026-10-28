package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import com.univalle.clasificados.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AjustesViewModel : ViewModel() {
    private val _estado = MutableStateFlow(MarcadorUiState(titulo = R.string.pestana_ajustes))
    val estado: StateFlow<MarcadorUiState> = _estado.asStateFlow()
}
