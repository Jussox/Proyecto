package cl.duoc.bitacora2.ui.theme.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.util.credencialesCorrectas
import cl.duoc.bitacora2.util.validarPasswordLogin
import cl.duoc.bitacora2.util.validarUsuario
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun cambiarPerfil(nuevoPerfil: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(
                perfil = nuevoPerfil,
                mensajeError = ""
            )
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

        // Evita iniciar varios intentos simultáneamente.
        if (estado.cargando) return

        // Mostrar el indicador y limpiar errores anteriores.
        _uiState.update {
            it.copy(
                cargando = true,
                mensajeError = ""
            )
        }

        viewModelScope.launch {
            // Permite que el usuario vea la animación.
            // En una autenticación real, se esperaría la respuesta
            // del servicio en lugar de utilizar este retraso.
            delay(2000)

            if (
                !validarUsuario(estado.usuario) ||
                !validarPasswordLogin(estado.password)
            ) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError =
                            "Debe ingresar usuario y contraseña"
                    )
                }
                return@launch
            }

            if (
                !credencialesCorrectas(
                    estado.perfil,
                    estado.usuario,
                    estado.password
                )
            ) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError =
                            "Usuario o contraseña incorrectos"
                    )
                }
                return@launch
            }

            // Guardamos el usuario autenticado.
            val usuarioAutenticado = estado.usuario
            val perfilAutenticado = estado.perfil

            // Limpiamos el estado del formulario.
            _uiState.value = LoginUiState()

            // Navegamos según el perfil seleccionado.
            if (perfilAutenticado == "estudiante") {
                onEstudianteValido(usuarioAutenticado)
            } else {
                onDocenteValido(usuarioAutenticado)
            }
        }
    }
}
