package com.univalle.clasificados.ui.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.univalle.clasificados.R
import com.univalle.clasificados.ui.componentes.BotonPrincipal
import com.univalle.clasificados.ui.componentes.CampoTexto
import com.univalle.clasificados.ui.componentes.EnlaceInferior
import com.univalle.clasificados.ui.theme.ClasificadosTheme
import com.univalle.clasificados.viewmodel.RegistroViewModel

/**
 * Pantalla 02 · Registro de Figma (HU-04). Muestra los cinco campos, el botón «Crear cuenta» y el
 * enlace «¿Ya tienes cuenta? Inicia sesión». Toda la lógica está en [RegistroViewModel]; la pantalla
 * solo dibuja el estado que lee con collectAsStateWithLifecycle(). Al crearse la cuenta, la
 * navegación lleva a Inicio porque ya hay sesión (RN-04).
 *
 * @param alVolver flecha atrás y «Inicia sesión»: regresan a Inicio de sesión.
 */
@Composable
fun RegistroScreen(
    alVolver: () -> Unit,
    viewModel: RegistroViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val colores = MaterialTheme.colorScheme
    val extra = ClasificadosTheme.colores

    Column(
        modifier = Modifier
            .fillMaxSize()
            // El teclado no tapa los campos (sin contar dos veces la barra de navegación).
            .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
    ) {
        // Barra superior de Figma: flecha atrás y título «Crear cuenta».
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = alVolver) {
                Icon(
                    painter = painterResource(R.drawable.ic_atras),
                    contentDescription = stringResource(R.string.accion_volver),
                    tint = colores.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = stringResource(R.string.registro_titulo),
                color = colores.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            // El contenido se desplaza en pantallas pequeñas; en las grandes el botón queda abajo.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .heightIn(min = maxHeight)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.registro_subtitulo),
                    color = extra.textoSecundario,
                    fontSize = 14.sp
                )
                CampoTexto(
                    etiqueta = stringResource(R.string.registro_nombre),
                    valor = estado.nombre,
                    alCambiar = viewModel::alCambiarNombre,
                    ejemplo = stringResource(R.string.registro_nombre_ejemplo),
                    error = estado.errorNombre,
                    habilitado = !estado.cargando
                )
                CampoTexto(
                    etiqueta = stringResource(R.string.registro_correo),
                    valor = estado.correo,
                    alCambiar = viewModel::alCambiarCorreo,
                    ejemplo = stringResource(R.string.registro_correo_ejemplo),
                    icono = R.drawable.ic_correo,
                    error = estado.errorCorreo,
                    tipoTeclado = KeyboardType.Email,
                    habilitado = !estado.cargando
                )
                CampoTexto(
                    etiqueta = stringResource(R.string.registro_telefono),
                    valor = estado.telefono,
                    alCambiar = viewModel::alCambiarTelefono,
                    ejemplo = stringResource(R.string.registro_telefono_ejemplo),
                    icono = R.drawable.ic_telefono,
                    tipoTeclado = KeyboardType.Phone,
                    habilitado = !estado.cargando
                )
                CampoTexto(
                    etiqueta = stringResource(R.string.registro_contrasena),
                    valor = estado.contrasena,
                    alCambiar = viewModel::alCambiarContrasena,
                    ejemplo = stringResource(R.string.registro_contrasena_ejemplo),
                    error = estado.errorContrasena,
                    ayuda = stringResource(R.string.registro_ayuda_contrasena),
                    esContrasena = true,
                    habilitado = !estado.cargando
                )
                CampoTexto(
                    etiqueta = stringResource(R.string.registro_confirmacion),
                    valor = estado.confirmacion,
                    alCambiar = viewModel::alCambiarConfirmacion,
                    ejemplo = stringResource(R.string.registro_contrasena_ejemplo),
                    error = estado.errorConfirmacion,
                    esContrasena = true,
                    habilitado = !estado.cargando
                )

                Spacer(Modifier.weight(1f))

                estado.errorGeneral?.let {
                    Text(text = it, color = colores.error, fontSize = 12.sp)
                }
                BotonPrincipal(
                    texto = stringResource(R.string.registro_crear_cuenta),
                    cargando = estado.cargando,
                    alTocar = viewModel::crearCuenta
                )
                EnlaceInferior(
                    pregunta = stringResource(R.string.registro_ya_tienes_cuenta),
                    accion = stringResource(R.string.registro_inicia_sesion),
                    alTocar = alVolver,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}
