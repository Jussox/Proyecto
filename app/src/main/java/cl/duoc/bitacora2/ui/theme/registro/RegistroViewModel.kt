package cl.duoc.bitacora2.ui.theme.registro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.AppDatabase
import cl.duoc.bitacora2.data.UsuarioRepository
import cl.duoc.bitacora2.model.Usuario
import cl.duoc.bitacora2.util.passwordsCoinciden
import cl.duoc.bitacora2.util.validarApellido
import cl.duoc.bitacora2.util.validarCorreo
import cl.duoc.bitacora2.util.validarNombre
import cl.duoc.bitacora2.util.validarPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistroViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = UsuarioRepository(database.usuarioDao())

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun cambiarNombre(nuevoNombre: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(nombre = nuevoNombre)
        }
    }


    fun cambiarApellido(nuevoApellido: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(apellido = nuevoApellido)
        }
    }

    fun cambiarCorreo(nuevoCorreo: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(correo = nuevoCorreo)
        }
    }

    fun cambiarPassword(nuevaPassword: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(password = nuevaPassword)
        }
    }

    fun cambiarRepetirPassword(nuevaRepetirPassword: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(repetirPassword = nuevaRepetirPassword)
        }
    }

    fun cambiarAceptaTerminos(nuevoValor: Boolean) {
        _uiState.update { estadoActual ->
            estadoActual.copy(aceptaTerminos = nuevoValor)
        }
    }


    fun registrar() {
        val estado = _uiState.value
        val errorNombre = !validarNombre(estado.nombre)
        val errorApellido = !validarApellido(estado.apellido)
        val errorCorreo = !validarCorreo(estado.correo)
        val errorPassword = !validarPassword(estado.password)
        val errorRepetirPassword = !passwordsCoinciden(estado.password, estado.repetirPassword)
        val errorTerminos = !estado.aceptaTerminos

        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorNombre = errorNombre,
                errorApellido = errorApellido,
                errorCorreo = errorCorreo,
                errorPassword = errorPassword,
                errorRepetirPassword = errorRepetirPassword,
                errorTerminos = errorTerminos
            )
        }

        val formularioValido =
            !errorNombre &&
                    !errorApellido &&
                    !errorCorreo &&
                    !errorPassword &&
                    !errorRepetirPassword &&
                    !errorTerminos

        if (formularioValido) {
            val usuario = Usuario(
                nombre = estado.nombre,
                apellido = estado.apellido,
                correo = estado.correo,
                password = estado.password
            )
            viewModelScope.launch {
                repository.guardar(usuario)
                _uiState.value = RegistroUiState()
            }
        }
    }
}