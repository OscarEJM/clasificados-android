package com.univalle.clasificados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.univalle.clasificados.data.repository.AuthRepository
import com.univalle.clasificados.data.repository.ResultadoRegistro
import com.univalle.clasificados.util.Validador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la pantalla 02 · Registro: lo que escribió el usuario, el error de cada campo
 * (`null` = sin error) y si se está creando la cuenta.
 */
data class RegistroUiState(
    val nombre: String = "",
    val correo: String = "",
    val telefono: String = "",
    val contrasena: String = "",
    val confirmacion: String = "",
    val errorNombre: String? = null,
    val errorCorreo: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmacion: String? = null,
    /** Mensaje que no es de un campo en particular, por ejemplo «Sin conexión…». */
    val errorGeneral: String? = null,
    /** `true` mientras se crea la cuenta: el botón muestra la carga y no se puede tocar. */
    val cargando: Boolean = false
)

/**
 * ViewModel de la pantalla 02 · Registro (HU-04: RF-03, RF-04 y RF-05).
 * Valida con [Validador] al tocar «Crear cuenta»; si todo es válido, crea la cuenta con
 * [AuthRepository]. El error de un campo se borra apenas el usuario lo cambia (acordado con Oscar).
 * Al crearse la cuenta queda la sesión iniciada y la navegación lleva a Inicio (RN-04).
 */
class RegistroViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(RegistroUiState())

    /** Estado de solo lectura que la pantalla observa con collectAsStateWithLifecycle(). */
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    /** El usuario cambió el nombre: se guarda y se borra su error. */
    fun alCambiarNombre(valor: String) =
        _estado.update { it.copy(nombre = valor, errorNombre = null, errorGeneral = null) }

    /** El usuario cambió el correo: se guarda y se borran su error y el de correo existente. */
    fun alCambiarCorreo(valor: String) =
        _estado.update { it.copy(correo = valor, errorCorreo = null, errorGeneral = null) }

    /** El usuario cambió el teléfono (opcional, sin validación). */
    fun alCambiarTelefono(valor: String) =
        _estado.update { it.copy(telefono = valor, errorGeneral = null) }

    /** El usuario cambió la contraseña: se guarda y se borra su error. */
    fun alCambiarContrasena(valor: String) =
        _estado.update { it.copy(contrasena = valor, errorContrasena = null, errorGeneral = null) }

    /** El usuario cambió la confirmación: se guarda y se borra su error. */
    fun alCambiarConfirmacion(valor: String) =
        _estado.update { it.copy(confirmacion = valor, errorConfirmacion = null, errorGeneral = null) }

    /**
     * El usuario tocó «Crear cuenta». Valida todos los campos (RF-04); si alguno es inválido
     * muestra su mensaje y no crea la cuenta. Si todo es válido, crea la cuenta (RF-03) y, si el
     * correo ya existe, lo indica debajo del correo (RF-05). No hace nada si ya está cargando.
     */
    fun crearCuenta() {
        val actual = _estado.value
        if (actual.cargando) return
        val conErrores = actual.copy(
            errorNombre = Validador.validarNombre(actual.nombre),
            errorCorreo = Validador.validarCorreo(actual.correo),
            errorContrasena = Validador.validarContrasena(actual.contrasena),
            errorConfirmacion = Validador.validarConfirmacion(actual.contrasena, actual.confirmacion),
            errorGeneral = null
        )
        val hayErrores = listOf(
            conErrores.errorNombre, conErrores.errorCorreo,
            conErrores.errorContrasena, conErrores.errorConfirmacion
        ).any { it != null }
        if (hayErrores) {
            _estado.value = conErrores
            return
        }

        _estado.value = conErrores.copy(cargando = true)
        viewModelScope.launch {
            val resultado = authRepository.registrar(
                nombre = actual.nombre.trim(),
                correo = actual.correo.trim(),
                telefono = actual.telefono.trim(),
                contrasena = actual.contrasena
            )
            _estado.update {
                when (resultado) {
                    ResultadoRegistro.CREADA -> it.copy(cargando = false)
                    ResultadoRegistro.CORREO_EXISTENTE ->
                        it.copy(cargando = false, errorCorreo = CORREO_EXISTENTE)
                    ResultadoRegistro.SIN_CONEXION ->
                        it.copy(cargando = false, errorGeneral = SIN_CONEXION)
                    ResultadoRegistro.ERROR ->
                        it.copy(cargando = false, errorGeneral = ERROR_GENERAL)
                }
            }
        }
    }

    companion object {
        /** Mensaje exacto de la sección 1.5 (RF-05). */
        const val CORREO_EXISTENTE = "Este correo ya está registrado"

        /** Mensaje exacto de la sección 1.5 (RN-18). */
        const val SIN_CONEXION = "Sin conexión a internet. Verifica tu conexión e intenta de nuevo."

        /** Fallo inesperado de Firebase. Texto aprobado por Oscar (sección 1.5). */
        const val ERROR_GENERAL = "No se pudo crear la cuenta. Intenta de nuevo."
    }
}
