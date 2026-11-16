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

    // --- Publicar anuncio (HU-12 · Publicar anuncio: pasos de categoría y detalles) ---

    /** Mensajes exactos de la sección 1.5 para publicar. No cambiarlos sin autorización. */
    const val CATEGORIA_OBLIGATORIA = "Por favor selecciona una categoría para continuar"
    const val TITULO_OBLIGATORIO = "El título es obligatorio"
    const val TITULO_CORTO = "El título debe tener al menos 10 caracteres"
    const val DESCRIPCION_OBLIGATORIA = "La descripción es obligatoria"
    const val DESCRIPCION_CORTA = "La descripción debe tener al menos 20 caracteres"
    const val PRECIO_OBLIGATORIO = "Indica un precio o escribe '0' si es gratis"

    /** Mínimo de caracteres del título (RN-08). */
    const val MINIMO_TITULO = 10

    /** Mínimo de caracteres de la descripción (RN-08). */
    const val MINIMO_DESCRIPCION = 20

    /** Máximo de dígitos del precio: hasta $9.999.999.999 (aprobado por Oscar el 15/11/2026). */
    const val MAXIMO_DIGITOS_PRECIO = 10

    /** RF-22: hay que elegir una de las 13 categorías. */
    fun validarCategoria(categoriaId: String?): String? =
        if (categoriaId.isNullOrBlank()) CATEGORIA_OBLIGATORIA else null

    /**
     * RF-23 (a): el título es obligatorio y tiene al menos [MINIMO_TITULO] caracteres. Los
     * espacios al principio y al final no cuentan.
     */
    fun validarTitulo(titulo: String): String? = when {
        titulo.isBlank() -> TITULO_OBLIGATORIO
        titulo.trim().length < MINIMO_TITULO -> TITULO_CORTO
        else -> null
    }

    /** RF-23 (b): la descripción es obligatoria y tiene al menos [MINIMO_DESCRIPCION] caracteres. */
    fun validarDescripcion(descripcion: String): String? = when {
        descripcion.isBlank() -> DESCRIPCION_OBLIGATORIA
        descripcion.trim().length < MINIMO_DESCRIPCION -> DESCRIPCION_CORTA
        else -> null
    }

    /** RF-23 (c) y RN-09: el precio es obligatorio y es un número entero mayor o igual a 0. */
    fun validarPrecio(precio: String): String? =
        if (precio.isNotEmpty() && precio.all { it.isDigit() } && precio.length <= MAXIMO_DIGITOS_PRECIO) {
            null
        } else {
            PRECIO_OBLIGATORIO
        }

    // --- Fotos del anuncio (HU-13 · Publicar anuncio: agregar fotos; RF-24, RNF-09, RN-10) ---

    /** Mensajes exactos de la sección 1.5 para el paso 3. No cambiarlos sin autorización. */
    const val FOTOS_MINIMO = "Agrega al menos 1 foto"
    const val FOTOS_MAXIMO = "Máximo 5 fotos"
    const val FOTO_INVALIDA = "Solo se permiten imágenes JPG o PNG de máximo 5 MB"

    /** Máximo de fotos por anuncio (RN-10). */
    const val MAXIMO_FOTOS = 5

    /** Tamaño máximo de cada foto: 5 MB (RN-10). */
    const val MAXIMO_BYTES_FOTO = 5L * 1024 * 1024

    /** Formatos aceptados: JPG y PNG (RN-10). */
    private val tiposFoto = setOf("image/jpeg", "image/png")

    /**
     * RF-24: una foto es válida si es JPG o PNG y pesa como máximo 5 MB.
     * @param tipo tipo del archivo (por ejemplo «image/jpeg»); `null` si no se pudo saber.
     * @param tamanoBytes tamaño del archivo; `null` si no se pudo saber (se rechaza).
     */
    fun esFotoValida(tipo: String?, tamanoBytes: Long?): Boolean =
        tipo?.lowercase() in tiposFoto && tamanoBytes != null && tamanoBytes in 1..MAXIMO_BYTES_FOTO

    /** RF-24: el anuncio necesita al menos una foto para continuar. */
    fun validarCantidadFotos(cantidad: Int): String? =
        if (cantidad < 1) FOTOS_MINIMO else null

    // --- Ubicación y contacto (HU-14 · Publicar anuncio: ubicación, contacto y confirmación; RF-25) ---

    /** Mensajes exactos de la sección 1.5 para el paso 4. No cambiarlos sin autorización. */
    const val CIUDAD_OBLIGATORIA = "Selecciona tu ciudad para que te encuentren"
    const val CORREO_CONTACTO_OBLIGATORIO = "El email es obligatorio para recibir contactos"
    const val CORREO_CONTACTO_INVALIDO = "Por favor ingresa un email válido"

    /** RF-25 (a): hay que elegir una de las 10 ciudades. */
    fun validarCiudad(ciudad: String?): String? =
        if (ciudad.isNullOrBlank()) CIUDAD_OBLIGATORIA else null

    /**
     * RF-25 (b) y RN-11: el correo de contacto es obligatorio y tiene el formato
     * usuario@dominio.ext (el mismo de Registro).
     */
    fun validarCorreoContacto(correo: String): String? = when {
        correo.isBlank() -> CORREO_CONTACTO_OBLIGATORIO
        !formatoCorreo.matches(correo.trim()) -> CORREO_CONTACTO_INVALIDO
        else -> null
    }

    /**
     * Deja en el campo de precio solo los dígitos y como máximo [MAXIMO_DIGITOS_PRECIO]: así el
     * campo solo acepta números enteros aunque se pegue otro texto (RF-23).
     */
    fun limpiarPrecio(texto: String): String =
        texto.filter { it in '0'..'9' }.take(MAXIMO_DIGITOS_PRECIO)
}
