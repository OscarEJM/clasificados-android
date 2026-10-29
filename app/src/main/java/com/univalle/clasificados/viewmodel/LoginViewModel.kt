package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.clasificados.data.repository.AuthRepository
import com.univalle.clasificados.data.repository.ResultadoInicioSesion
import com.univalle.clasificados.util.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla 01 · Inicio de sesión: lo que escribió el usuario, el error de cada campo
 * (`null` = sin error) y si se está iniciando sesión.
 */
data class LoginUiState(
    val correo: String = "",
    val contrasena: String = "",
    val errorCorreo: String? = null,
    /** Error de la contraseña; aquí también va «Correo o contraseña incorrectos» (acordado con Oscar). */
    val errorContrasena: String? = null,
    /** Mensaje que no es de un campo en particular, por ejemplo «Sin conexión…». */
    val errorGeneral: String? = null,
    /** `true` mientras se inicia sesión: el botón muestra la carga y no se puede tocar. */
    val cargando: Boolean = false
)

/**
 * ViewModel de la pantalla 01 · Inicio de sesión (HU-05: RF-06 y RF-07).
 * Al tocar «Iniciar sesión» valida el correo y que la contraseña no esté vacía; si todo es válido,
 * inicia sesión con [AuthRepository]. El error de un campo se borra apenas el usuario lo cambia,
 * igual que en Registro. Al entrar, la navegación lleva a Inicio y limpia la pila.
 */
class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(LoginUiState())

    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    /** El usuario cambió el correo: se guarda y se borra su error. */
    fun alCambiarCorreo(valor: String) =
        _estado.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }

    /** El usuario cambió la contraseña: se guarda y se borra su error. */
    fun alCambiarContrasena(valor: String) =
        _estado.update { it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null) }

    /**
     * El usuario tocó «Iniciar sesión». Valida el formato del correo y que la contraseña no esté
     * vacía (RF-07); si algo falla, muestra el mensaje y no llama a Firebase. Si Firebase rechaza
     * las credenciales, muestra «Correo o contraseña incorrectos» sin decir cuál falló.
     * No hace nada si ya está cargando.
     */
    fun iniciarSesion() {
        val actual = _estado.value
        if (actual.cargando) return
        val conErrores = actual.copy(
            errorCorreo = Validador.validarCorreo(actual.correo),
            errorContrasena = Validador.validarContrasenaObligatoria(actual.contrasena),
            errorGeneral = null
        )
        if (conErrores.errorCorreo != null || conErrores.errorContrasena != null) {
            _estado.value = conErrores
            return
        }

        _estado.value = conErrores.copy(cargando = true)
        viewModelScope.launch {
            val resultado = authRepository.iniciarSesion(actual.correo.trim(), actual.contrasena)
            _estado.update {
                when (resultado) {
                    ResultadoInicioSesion.ENTRO -> it.copy(cargando = false)
                    ResultadoInicioSesion.CREDENCIALES_INCORRECTAS ->
                        it.copy(cargando = false, errorContrasena = CREDENCIALES_INCORRECTAS)
                    ResultadoInicioSesion.SIN_CONEXION ->
                        it.copy(cargando = false, errorGeneral = SIN_CONEXION)
                    ResultadoInicioSesion.ERROR ->
                        it.copy(cargando = false, errorGeneral = ERROR_GENERAL)
                }
            }
        }
    }

    companion object {
        /** Mensaje exacto de la sección 1.5 (RF-07). */
        const val CREDENCIALES_INCORRECTAS = "Correo o contraseña incorrectos"

        /** Mensaje exacto de la sección 1.5 (RN-18). */
        const val SIN_CONEXION = "Sin conexión a internet. Verifica tu conexión e intenta de nuevo."

        /** Fallo inesperado, por ejemplo demasiados intentos. Texto aprobado por Oscar (sección 1.5). */
        const val ERROR_GENERAL = "No se pudo iniciar sesión. Intenta de nuevo."
    }
}
