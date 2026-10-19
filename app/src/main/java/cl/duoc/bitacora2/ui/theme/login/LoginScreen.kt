package cl.duoc.bitacora2.ui.theme.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.ui.theme.Bitacora2Theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.Color


@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onIrAEstudiante: (String) -> Unit,
    onIrADocente: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Bitácora",
                style = MaterialTheme.typography.headlineMedium
            )

            Text("Selecciona tu perfil")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.perfil == "estudiante") {
                    Button(
                        onClick = { viewModel.cambiarPerfil("estudiante") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Estudiante")
                    }
                    OutlinedButton(
                        onClick = { viewModel.cambiarPerfil("docente") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Docente")
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.cambiarPerfil("estudiante") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Estudiante")
                    }
                    Button(
                        onClick = { viewModel.cambiarPerfil("docente") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Docente")
                    }
                }
            }

            if (uiState.perfil == "estudiante") {
                Text("Perfil seleccionado: Estudiante")
            } else {
                Text("Perfil seleccionado: Docente")
            }

            OutlinedTextField(
                value = uiState.usuario,
                onValueChange = {
                    viewModel.cambiarUsuario(it)
                },
                label = {
                    Text("Nombre de usuario")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.cargando

            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = {
                    viewModel.cambiarPassword(it)
                },
                label = {
                    Text("Contraseña")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.cargando
            )
            //"AnimatedVisibility" muestra el círculo con una animación.
            AnimatedVisibility(
                visible = uiState.mensajeError.isNotEmpty(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = uiState.mensajeError,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    viewModel.iniciarSesion(
                        onEstudianteValido = onIrAEstudiante,
                        onDocenteValido = onIrADocente
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.cargando
            ) {
                Text("Iniciar sesión")
            }

            // Indicador circular debajo del botón.
            AnimatedVisibility(
                visible = uiState.cargando,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    //el circulo de carga
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp,
                        color = Color.Green
                    )

                    Text(
                        text = "Verificando acceso...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    Bitacora2Theme {
        LoginScreen(
            onIrAEstudiante = {},
            onIrADocente = {}
        )
    }
}