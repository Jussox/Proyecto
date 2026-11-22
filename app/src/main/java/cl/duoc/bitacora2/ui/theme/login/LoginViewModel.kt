package cl.duoc.bitacora2.ui.theme.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.LoginRequest
import cl.duoc.bitacora2.util.Sesion
import cl.duoc.bitacora2.util.mensajeDeError
import cl.duoc.bitacora2.util.validarCamposLogin
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun cambiarPerfil(nuevoPerfil: String) {
        _uiState.update { it.copy(perfil = nuevoPerfil, mensajeError = "") }
    }

    fun cambiarUsuario(nuevoUsuario: String) {
        _uiState.update { it.copy(usuario = nuevoUsuario) }
    }

    fun cambiarPassword(nuevaPassword: String) {
        _uiState.update { it.copy(password = nuevaPassword) }
    }

    fun iniciarSesion(
        onEstudianteValido: (String) -> Unit,
        onDocenteValido: (String) -> Unit
    ) {
        val estado = _uiState.value

        // Evita iniciar varios intentos simultáneamente.
        if (estado.cargando) return

        // Validaciones locales previas.
        val errorValidacion = validarCamposLogin(estado.usuario, estado.password)
        if (errorValidacion != null) {
            _uiState.update { it.copy(mensajeError = errorValidacion) }
            return
        }

        _uiState.update { it.copy(cargando = true, mensajeError = "") }

        viewModelScope.launch {
            try {
                val respuesta = repository.login(
                    LoginRequest(
                        usuario = estado.usuario,
                        password = estado.password,
                        perfil = estado.perfil
                    )
                )

                // Guardamos la sesión con los datos reales del backend.
                Sesion.iniciar(
                    usuario = respuesta.usuario ?: estado.usuario,
                    nombre = respuesta.nombre ?: "",
                    perfil = respuesta.perfil ?: estado.perfil,
                    curso = respuesta.curso ?: ""
                )

                // Limpiamos el formulario.
                _uiState.value = LoginUiState()

                // Navegamos según el perfil devuelto por el backend.
                if (respuesta.perfil == "estudiante") {
                    onEstudianteValido(respuesta.usuario ?: estado.usuario)
                } else {
                    onDocenteValido(respuesta.usuario ?: estado.usuario)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                val mensaje = if (e.code() == 401) {
                    "Usuario o contraseña incorrectos"
                } else {
                    mensajeDeError(e)
                }
                _uiState.update { it.copy(mensajeError = mensaje) }
            } catch (e: Throwable) {
                _uiState.update { it.copy(mensajeError = mensajeDeError(e)) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}