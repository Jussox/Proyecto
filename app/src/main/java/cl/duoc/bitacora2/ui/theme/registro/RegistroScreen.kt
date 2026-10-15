package cl.duoc.bitacora2.ui.theme.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegistroScreen(
    modifier: Modifier = Modifier,
    viewModel: RegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Crear cuenta",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = uiState.nombre,
            onValueChange = {
                viewModel.cambiarNombre(it)
            },
            label = {
                Text("Nombre")
            },
            isError = uiState.errorNombre,
            supportingText = {
                if (uiState.errorNombre) {
                    Text("El nombre es obligatorio")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = uiState.apellido,
            onValueChange = {
                viewModel.cambiarApellido(it)
            },
            label = {
                Text("Apellido")
            },
            isError = uiState.errorApellido,
            supportingText = {
                if (uiState.errorApellido) {
                    Text("El apellido es obligatorio")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.correo,
            onValueChange = {
                viewModel.cambiarCorreo(it)
            },
            label = {
                Text("Correo")
            },
            isError = uiState.errorCorreo,
            supportingText = {
                if (uiState.errorCorreo) {
                    Text("El correo debe contener @ y terminar en .cl")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.password,
            onValueChange = {
                viewModel.cambiarPassword(it)
            },
            label = {
                Text("Contraseña")
            },
            isError = uiState.errorPassword,
            supportingText = {
                if (uiState.errorPassword) {
                    Text("La contraseña debe tener mínimo 8 caracteres")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = uiState.repetirPassword,
            onValueChange = {
                viewModel.cambiarRepetirPassword(it)
            },
            label = {
                Text("Repetir contraseña")
            },
            isError = uiState.errorRepetirPassword,
            supportingText = {
                if (uiState.errorRepetirPassword) {
                    Text("Las contraseñas no coinciden")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )


        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Checkbox(
                checked = uiState.aceptaTerminos,
                onCheckedChange = {
                    viewModel.cambiarAceptaTerminos(it)
                }
            )

            Text("Acepto los términos y condiciones")
        }

        if (uiState.errorTerminos) {
            Text(
                text = "Debe aceptar los términos y condiciones",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = {
                viewModel.registrar()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrarse")
        }
    }
}

