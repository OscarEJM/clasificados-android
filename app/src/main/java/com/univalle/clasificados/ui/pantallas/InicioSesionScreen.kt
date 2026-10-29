package com.univalle.clasificados.ui.pantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import com.univalle.clasificados.viewmodel.LoginViewModel

/**
 * Pantalla 01 · Inicio de sesión de Figma (HU-05: RF-06 y RF-07). Muestra el logo, el título,
 * los campos de correo y contraseña, el botón «Iniciar sesión» y el enlace «¿No tienes cuenta?
 * Regístrate». Toda la lógica está en [LoginViewModel]; la pantalla solo dibuja el estado que lee
 * con collectAsStateWithLifecycle(). Al entrar, la navegación lleva a Inicio y limpia la pila.
 *
 * @param alRegistrarse abre la pantalla 02 · Registro.
 */
@Composable
fun InicioSesionScreen(
    alRegistrarse: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val colores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            // El teclado no tapa los campos (sin contar dos veces la barra de navegación).
            .windowInsetsPadding(WindowInsets.ime.exclude(WindowInsets.navigationBars))
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 48.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Encabezado de Figma: logo azul de 64 dp y «Clasificados».
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(colores.primary, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_categorias),
                    contentDescription = null,
                    tint = colores.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = stringResource(R.string.app_name),
                color = colores.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Título y subtítulo.
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = stringResource(R.string.inicio_sesion_titulo),
                color = colores.onSurface,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.inicio_sesion_subtitulo),
                color = ClasificadosTheme.colores.textoSecundario,
                fontSize = 14.sp
            )
        }

        // Formulario.
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                etiqueta = stringResource(R.string.registro_contrasena),
                valor = estado.contrasena,
                alCambiar = viewModel::alCambiarContrasena,
                ejemplo = stringResource(R.string.registro_contrasena_ejemplo),
                error = estado.errorContrasena,
                esContrasena = true,
                habilitado = !estado.cargando
            )
            estado.errorGeneral?.let {
                Text(text = it, color = colores.error, fontSize = 12.sp)
            }
        }

        BotonPrincipal(
            texto = stringResource(R.string.inicio_sesion_boton),
            cargando = estado.cargando,
            alTocar = viewModel::iniciarSesion
        )
        EnlaceInferior(
            pregunta = stringResource(R.string.inicio_sesion_sin_cuenta),
            accion = stringResource(R.string.inicio_sesion_registrate),
            alTocar = alRegistrarse
        )
    }
}
