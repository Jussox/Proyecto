package cl.duoc.bitacora2.ui.theme.login

import androidx.lifecycle.ViewModel
import cl.duoc.bitacora2.util.credencialesCorrectas
import cl.duoc.bitacora2.util.validarPasswordLogin
import cl.duoc.bitacora2.util.validarUsuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun cambiarPerfil(nuevoPerfil: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(perfil = nuevoPerfil, mensajeError = "")
        }
    }

    fun cambiarUsuario(nuevoUsuario: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(usuario = nuevoUsuario)
        }
    }

    fun cambiarPassword(nuevaPassword: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(password = nuevaPassword)
        }
    }

    fun iniciarSesion(
        onEstudianteValido: (String) -> Unit,
        onDocenteValido: (String) -> Unit
    ) {
        val estado = _uiState.value

        if (!validarUsuario(estado.usuario) || !validarPasswordLogin(estado.password)) {
            _uiState.update { estadoActual ->
                estadoActual.copy(
                    mensajeError = "Debe ingresar usuario y contraseña"
                )
            }
            return
        }

        if (!credencialesCorrectas(estado.perfil, estado.usuario, estado.password)) {
            _uiState.update { estadoActual ->
                estadoActual.copy(
                    mensajeError = "Usuario o contraseña incorrectos"
                )
            }
            return
        }

// Conservamos el usuario que inició sesión.
        val usuarioAutenticado = estado.usuario

        _uiState.value = LoginUiState()

        if (estado.perfil == "estudiante") {
            onEstudianteValido(usuarioAutenticado)
        } else {
            onDocenteValido(usuarioAutenticado)
        }

    }
}