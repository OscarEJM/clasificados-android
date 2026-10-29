package com.univalle.clasificados.util

/**
 * Validaciones de los formularios con funciones puras, sin nada de Android, para poder probarlas
 * con JUnit (sección 1.10; se prueban en HU-22). Cada función devuelve el mensaje de error exacto
 * de la sección 1.5 o `null` si el dato es válido. Se usan en Registro (HU-04) y en otras historias.
 */
object Validador {

    /** Mensajes exactos de la sección 1.5 de la guía. No cambiarlos sin autorización. */
    const val NOMBRE_OBLIGATORIO = "El nombre es obligatorio"
    const val CORREO_INVALIDO = "Ingresa un correo válido"
    const val CONTRASENA_CORTA = "La contraseña debe tener al menos 6 caracteres"
    const val CONTRASENAS_NO_COINCIDEN = "Las contraseñas no coinciden"
    const val CONTRASENA_OBLIGATORIA = "La contraseña es obligatoria"

    /** Mínimo de caracteres de la contraseña (RN-03). */
    const val MINIMO_CONTRASENA = 6

    /** Formato usuario@dominio.ext: algo antes de la @, un dominio y una extensión, sin espacios. */
    private val formatoCorreo = Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")

    /** RF-04 (a): el nombre completo es obligatorio. */
    fun validarNombre(nombre: String): String? =
        if (nombre.isBlank()) NOMBRE_OBLIGATORIO else null

    /** RF-04 (b) y RF-07: el correo es obligatorio y con formato usuario@dominio.ext. */
    fun validarCorreo(correo: String): String? =
        if (formatoCorreo.matches(correo.trim())) null else CORREO_INVALIDO

    /** RF-04 (c): la contraseña tiene al menos [MINIMO_CONTRASENA] caracteres. */
    fun validarContrasena(contrasena: String): String? =
        if (contrasena.length >= MINIMO_CONTRASENA) null else CONTRASENA_CORTA

    /** RF-07: en Inicio de sesión la contraseña no puede estar vacía (HU-05). */
    fun validarContrasenaObligatoria(contrasena: String): String? =
        if (contrasena.isEmpty()) CONTRASENA_OBLIGATORIA else null

    /** RF-04 (d): la confirmación es igual a la contraseña. */
    fun validarConfirmacion(contrasena: String, confirmacion: String): String? =
        if (contrasena == confirmacion) null else CONTRASENAS_NO_COINCIDEN
}
