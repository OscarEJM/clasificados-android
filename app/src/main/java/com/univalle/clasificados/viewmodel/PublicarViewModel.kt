package com.univalle.clasificados.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.univalle.clasificados.data.repository.AnunciosRepository
import com.univalle.clasificados.data.repository.AuthRepository
import com.univalle.clasificados.data.repository.FotoParaSubir
import com.univalle.clasificados.data.repository.FotosRepository
import com.univalle.clasificados.data.repository.GaleriaRepository
import com.univalle.clasificados.model.Anuncio
import com.univalle.clasificados.model.Categoria
import com.univalle.clasificados.model.Ciudad
import com.univalle.clasificados.util.Validador
import io.github.jan.supabase.exceptions.HttpRequestException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Borrador del anuncio mientras se llena el formulario de Publicar (HU-12). Vive solo en memoria:
 * en el teléfono no se guarda nada más que el tema (RN-17).
 *
 * @property categoria la categoría elegida en el paso 1; `null` si todavía no se eligió.
 * @property precio lo escrito en el campo de precio, solo dígitos (se convierte a número al guardar).
 * @property fotos fotos elegidas en la galería, en el orden en que se agregaron (HU-13). Son las
 * direcciones de las fotos en el teléfono; se suben a Supabase al publicar (HU-14).
 * @property ciudad ciudad elegida en el paso 4 (HU-14); `null` si todavía no se eligió.
 * @property correo correo de contacto; se prellena con el de la cuenta (aprobado por Oscar).
 * @property telefono teléfono de contacto opcional; se prellena con el del registro (RN-11).
 * @property contactoPrellenado `true` cuando ya se prellenaron correo y teléfono, para no
 * volver a escribir encima de lo que el usuario cambió.
 */
data class BorradorAnuncio(
    val categoria: Categoria? = null,
    val titulo: String = "",
    val descripcion: String = "",
    val precio: String = "",
    val fotos: List<Uri> = emptyList(),
    val ciudad: Ciudad? = null,
    val correo: String = "",
    val telefono: String = "",
    val contactoPrellenado: Boolean = false
)

/**
 * Estado del formulario de cuatro pasos (RF-21).
 * @property paso paso actual, de 1 a 4.
 * @property errorCategoria mensaje del paso 1, o `null`.
 * @property errorFotos mensaje del paso 3 («Agrega al menos 1 foto», «Máximo 5 fotos» o
 * «Solo se permiten imágenes JPG o PNG de máximo 5 MB»), o `null`.
 * @property errorCiudad y [errorCorreo] mensajes del paso 4 (RF-25), o `null`.
 * @property publicando `true` mientras se suben las fotos y se guarda el anuncio (pantalla 11b).
 * @property errorPublicar mensaje si la publicación falló: el de RN-18 sin internet o
 * [PublicarViewModel.ERROR_PUBLICAR] en otro caso.
 * @property publicadoId id del anuncio recién publicado; la pantalla lo usa para abrir
 * Confirmación y luego llama a [PublicarViewModel.reiniciar].
 */
data class PublicarUiState(
    val paso: Int = 1,
    val borrador: BorradorAnuncio = BorradorAnuncio(),
    val errorCategoria: String? = null,
    val errorTitulo: String? = null,
    val errorDescripcion: String? = null,
    val errorPrecio: String? = null,
    val errorFotos: String? = null,
    val errorCiudad: String? = null,
    val errorCorreo: String? = null,
    val publicando: Boolean = false,
    val errorPublicar: String? = null,
    val publicadoId: String? = null
) {
    /** Contador vivo del título: «Mínimo 10 caracteres · N escritos» (sección 1.5). */
    val contadorTitulo: String
        get() = "Mínimo ${Validador.MINIMO_TITULO} caracteres · ${borrador.titulo.length} escritos"

    /** Contador del paso 3: «N / 5» (RF-24). */
    val contadorFotos: String
        get() = "${borrador.fotos.size} / ${Validador.MAXIMO_FOTOS}"

    /** Cuántas fotos más se pueden agregar. */
    val cuposFotos: Int
        get() = Validador.MAXIMO_FOTOS - borrador.fotos.size
}

/**
 * ViewModel del formulario de Publicar, compartido por los cuatro pasos (HU-12 · Publicar anuncio:
 * pasos de categoría y detalles; RF-21, RF-22, RF-23).
 *
 * Guarda en memoria el borrador del anuncio, así «Volver» no pierde datos. Lo crea la actividad
 * (no la pantalla), de modo que el borrador se conserva si el usuario sale de Publicar y vuelve,
 * mientras la app esté abierta (acordado con Oscar). Si cambia la cuenta con sesión, el borrador
 * se borra para que otra persona no vea lo que se estaba escribiendo.
 *
 * Los errores aparecen al tocar «Continuar» y el de cada campo se borra al escribir en él, igual
 * que en Registro (acordado con Oscar).
 *
 * En el paso 3 (HU-13 · Publicar anuncio: agregar fotos; RF-24) revisa cada foto que llega de la
 * galería con [GaleriaRepository] y [Validador.esFotoValida]: solo agrega las JPG o PNG de hasta
 * 5 MB, y como máximo 5 en total.
 *
 * En el paso 4 (HU-14 · Publicar anuncio: ubicación, contacto y confirmación; RF-25, RF-26)
 * prellena correo y teléfono, valida ciudad y correo y publica: sube las fotos a Supabase, guarda
 * el anuncio en Firestore y, si algo falla, borra las fotos subidas para no dejar nada a medias.
 *
 * `@JvmOverloads` crea también el constructor que solo recibe [Application], que es el que usa
 * Android para crear el ViewModel.
 */
class PublicarViewModel @JvmOverloads constructor(
    aplicacion: Application,
    private val autenticacion: AuthRepository = AuthRepository(),
    private val galeria: GaleriaRepository = GaleriaRepository(aplicacion.contentResolver),
    private val anuncios: AnunciosRepository = AnunciosRepository(),
    private val fotosNube: FotosRepository = FotosRepository()
) : AndroidViewModel(aplicacion) {

    /** Estado interno que solo este ViewModel puede cambiar. */
    private val _estado = MutableStateFlow(PublicarUiState())

    /** Estado de solo lectura que las pantallas observan con collectAsStateWithLifecycle(). */
    val estado: StateFlow<PublicarUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            // El primer valor es la cuenta actual; a partir del segundo, la cuenta cambió.
            autenticacion.uidActual.distinctUntilChanged().drop(1).collect { reiniciar() }
        }
    }

    /** Paso 1: el usuario tocó una categoría; queda resaltada y se borra el error. */
    fun elegirCategoria(categoria: Categoria) =
        _estado.update { it.copy(borrador = it.borrador.copy(categoria = categoria), errorCategoria = null) }

    /** Paso 2: cambia el título. El contador se actualiza en vivo. */
    fun alCambiarTitulo(valor: String) =
        _estado.update { it.copy(borrador = it.borrador.copy(titulo = valor), errorTitulo = null) }

    /** Paso 2: cambia la descripción. */
    fun alCambiarDescripcion(valor: String) =
        _estado.update { it.copy(borrador = it.borrador.copy(descripcion = valor), errorDescripcion = null) }

    /** Paso 2: cambia el precio. Solo se aceptan dígitos (RF-23), hasta 10. */
    fun alCambiarPrecio(valor: String) =
        _estado.update {
            it.copy(borrador = it.borrador.copy(precio = Validador.limpiarPrecio(valor)), errorPrecio = null)
        }

    /** «Continuar»: valida el paso actual y solo avanza si es válido (RF-21). */
    fun continuar() {
        _estado.update { actual ->
            val borrador = actual.borrador
            when (actual.paso) {
                1 -> {
                    val error = Validador.validarCategoria(borrador.categoria?.id)
                    if (error == null) actual.copy(paso = 2) else actual.copy(errorCategoria = error)
                }
                2 -> {
                    val conErrores = actual.copy(
                        errorTitulo = Validador.validarTitulo(borrador.titulo),
                        errorDescripcion = Validador.validarDescripcion(borrador.descripcion),
                        errorPrecio = Validador.validarPrecio(borrador.precio)
                    )
                    val valido = conErrores.errorTitulo == null &&
                        conErrores.errorDescripcion == null && conErrores.errorPrecio == null
                    if (valido) conErrores.copy(paso = 3) else conErrores
                }
                3 -> {
                    val error = Validador.validarCantidadFotos(borrador.fotos.size)
                    if (error == null) actual.copy(paso = 4, errorFotos = null) else actual.copy(errorFotos = error)
                }
                else -> actual // En el paso 4 el botón es «Publicar anuncio» (publicar()).
            }
        }
        if (_estado.value.paso == 4) prellenarContacto()
    }

    /**
     * Paso 4: la primera vez que se llega, pone el correo de la cuenta (aprobado por Oscar) y el
     * teléfono guardado al registrarse, si existe (RN-11, RF-25 c). Solo llena los campos vacíos
     * y una sola vez por borrador, así no borra lo que el usuario escribió. Sin internet el
     * teléfono simplemente queda vacío.
     */
    private fun prellenarContacto() {
        if (_estado.value.borrador.contactoPrellenado) return
        val correoCuenta = autenticacion.correoActual()
        _estado.update {
            val b = it.borrador
            it.copy(borrador = b.copy(correo = b.correo.ifEmpty { correoCuenta }, contactoPrellenado = true))
        }
        val uid = autenticacion.uid() ?: return
        viewModelScope.launch {
            val telefono = try {
                autenticacion.perfil(uid)?.telefono
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                null
            }
            if (!telefono.isNullOrBlank()) {
                _estado.update { it.copy(borrador = it.borrador.copy(telefono = it.borrador.telefono.ifEmpty { telefono })) }
            }
        }
    }

    /**
     * Paso 3: el usuario tocó «Toca para agregar fotos». Si ya hay 5, muestra «Máximo 5 fotos» y
     * no se abre la galería (criterio 3).
     * @return `true` si se puede abrir la galería.
     */
    fun puedeAgregarFotos(): Boolean {
        if (_estado.value.cuposFotos > 0) return true
        _estado.update { it.copy(errorFotos = Validador.FOTOS_MAXIMO) }
        return false
    }

    /**
     * Paso 3: llegaron fotos de la galería (RF-24). Se agregan solo las JPG o PNG de hasta 5 MB
     * que no estén ya, hasta completar 5. Si alguna no cumple el formato o el tamaño, se muestra
     * «Solo se permiten imágenes JPG o PNG de máximo 5 MB»; si sobran fotos, «Máximo 5 fotos».
     */
    fun agregarFotos(elegidas: List<Uri>) {
        if (elegidas.isEmpty()) return // El usuario cerró la galería sin elegir.
        viewModelScope.launch {
            val actuales = _estado.value.borrador.fotos
            val nuevas = elegidas.filter { it !in actuales }
            // Leer tipo y tamaño consulta al teléfono: se hace fuera del hilo de la pantalla.
            val validas = withContext(Dispatchers.IO) {
                nuevas.filter { uri ->
                    val info = galeria.info(uri)
                    Validador.esFotoValida(info.tipo, info.tamanoBytes)
                }
            }
            _estado.update { estado ->
                val yaEstan = estado.borrador.fotos
                val caben = validas.filter { it !in yaEstan }.take(Validador.MAXIMO_FOTOS - yaEstan.size)
                val error = when {
                    validas.size < nuevas.size -> Validador.FOTO_INVALIDA
                    caben.size < validas.size -> Validador.FOTOS_MAXIMO
                    else -> null
                }
                estado.copy(borrador = estado.borrador.copy(fotos = yaEstan + caben), errorFotos = error)
            }
        }
    }

    /** Paso 3: quita una foto con la «✕» de su miniatura (pedido por Oscar). */
    fun quitarFoto(foto: Uri) =
        _estado.update { it.copy(borrador = it.borrador.copy(fotos = it.borrador.fotos - foto), errorFotos = null) }

    /** Paso 4: el usuario eligió una ciudad de la lista (RF-25 a). */
    fun elegirCiudad(ciudad: Ciudad) =
        _estado.update { it.copy(borrador = it.borrador.copy(ciudad = ciudad), errorCiudad = null, errorPublicar = null) }

    /** Paso 4: cambia el correo de contacto (RF-25 b). */
    fun alCambiarCorreo(valor: String) =
        _estado.update { it.copy(borrador = it.borrador.copy(correo = valor), errorCorreo = null, errorPublicar = null) }

    /** Paso 4: cambia el teléfono de contacto opcional (RF-25 c). */
    fun alCambiarTelefono(valor: String) =
        _estado.update { it.copy(borrador = it.borrador.copy(telefono = valor), errorPublicar = null) }

    /**
     * «Publicar anuncio» (RF-26). Valida ciudad y correo; si todo está bien:
     * 1. pide un id nuevo para el anuncio;
     * 2. sube las fotos a `{uid}/{id}/{n}.jpg` en Supabase y obtiene sus URL públicas;
     * 3. guarda el anuncio en Firestore con el autor, el título en minúsculas y la fecha del servidor.
     *
     * Mientras tanto [PublicarUiState.publicando] es `true` (pantalla 11b). Si algo falla, borra
     * las fotos que subió, no crea el anuncio y muestra el mensaje: el de RN-18 sin internet o
     * [ERROR_PUBLICAR] en otro caso (aprobado por Oscar). El borrador se conserva.
     */
    fun publicar() {
        val actual = _estado.value
        if (actual.publicando) return
        val b = actual.borrador
        val errorCiudad = Validador.validarCiudad(b.ciudad?.nombre)
        val errorCorreo = Validador.validarCorreoContacto(b.correo)
        if (errorCiudad != null || errorCorreo != null) {
            _estado.update { it.copy(errorCiudad = errorCiudad, errorCorreo = errorCorreo) }
            return
        }
        val autorId = autenticacion.uid() ?: return
        _estado.update { it.copy(publicando = true, errorPublicar = null) }

        viewModelScope.launch {
            val id = anuncios.nuevoId()
            var fotosSubidas = 0
            try {
                val paraSubir = withContext(Dispatchers.IO) {
                    b.fotos.map { uri ->
                        val tipo = galeria.info(uri).tipo?.lowercase() ?: TIPO_JPG
                        FotoParaSubir(galeria.leer(uri), tipo)
                    }
                }
                val urls = fotosNube.subirFotosAnuncio(autorId, id, paraSubir)
                fotosSubidas = urls.size
                val titulo = b.titulo.trim()
                anuncios.crear(
                    id,
                    Anuncio(
                        autorId = autorId,
                        categoria = b.categoria!!.id,
                        titulo = titulo,
                        tituloMinusculas = titulo.lowercase(),
                        descripcion = b.descripcion.trim(),
                        precio = b.precio.toLong(),
                        fotos = urls,
                        ciudad = b.ciudad!!.nombre,
                        correo = b.correo.trim(),
                        telefono = b.telefono.trim().ifEmpty { null }
                    )
                )
                _estado.update { it.copy(publicando = false, publicadoId = id) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // El anuncio no se creó: se borran las fotos que alcanzaron a subir.
                if (fotosSubidas > 0) fotosNube.borrarFotosAnuncio(autorId, id, fotosSubidas)
                _estado.update { it.copy(publicando = false, errorPublicar = mensajeDeError(e)) }
            }
        }
    }

    /** Mensaje de RN-18 si el fallo fue por falta de internet; si no, [ERROR_PUBLICAR]. */
    private fun mensajeDeError(e: Exception): String {
        val sinInternet = e is HttpRequestException ||
            e is FirebaseNetworkException ||
            (e is FirebaseFirestoreException && e.code == FirebaseFirestoreException.Code.UNAVAILABLE) ||
            (e is IOException && e !is java.io.FileNotFoundException)
        return if (sinInternet) SIN_CONEXION else ERROR_PUBLICAR
    }

    /**
     * «Volver»: regresa al paso anterior conservando los datos (RF-21). Mientras se publica no
     * hace nada (en la 11b «Volver» está apagado).
     * @return `false` si ya estaba en el paso 1, para que la pantalla cierre Publicar.
     */
    fun volver(): Boolean {
        if (_estado.value.publicando) return true
        if (_estado.value.paso <= 1) return false
        _estado.update { it.copy(paso = it.paso - 1, errorPublicar = null) }
        return true
    }

    /** Borra el borrador y vuelve al paso 1 (al cambiar de cuenta y después de publicar). */
    fun reiniciar() {
        _estado.value = PublicarUiState()
    }

    companion object {
        /** Mensaje de RN-18 (sección 1.5). */
        const val SIN_CONEXION = "Sin conexión a internet. Verifica tu conexión e intenta de nuevo."

        /** Mensaje cuando la publicación falla por otra razón (aprobado por Oscar el 15/11/2026). */
        const val ERROR_PUBLICAR = "No se pudo publicar el anuncio. Intenta de nuevo."

        private const val TIPO_JPG = "image/jpeg"
    }
}
