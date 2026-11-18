package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.BitacoraRepository
import cl.duoc.bitacora2.data.fechaHoy
import cl.duoc.bitacora2.util.MAX_LARGO_TEXTO_LARGO
import cl.duoc.bitacora2.util.validarComentario
import cl.duoc.bitacora2.util.validarLargo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocenteRevisionViewModel(
    private val repository: BitacoraRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocenteRevisionUiState())
    val uiState: StateFlow<DocenteRevisionUiState> = _uiState.asStateFlow()

    fun inicializarComentario(comentario: String) {
        _uiState.update { it.copy(comentario = comentario) }
    }

    fun cambiarComentario(valor: String) {
        _uiState.update { it.copy(comentario = valor) }
    }

    fun cambiarMensaje(valor: String) {
        _uiState.update { it.copy(mensaje = valor) }
    }

    fun guardarComentario(id: Int, docente: String, onGuardado: ((Int, String) -> Unit)? = null) {
        val comentario = _uiState.value.comentario
        if (!validarComentario(comentario)) {
            _uiState.update { it.copy(mensaje = "Escriba un comentario antes de guardar") }
        } else if (!validarLargo(comentario, MAX_LARGO_TEXTO_LARGO)) {
            _uiState.update { it.copy(mensaje = "El comentario no puede superar los 2000 caracteres") }
        } else {
            viewModelScope.launch {
                repository?.guardarComentario(id, comentario, fechaHoy(), docente)
                onGuardado?.invoke(id, comentario)
            }
            _uiState.update { it.copy(mensaje = "Comentario guardado") }
        }
    }

    fun marcarRevisado(id: Int, onMarcado: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            repository?.marcarRevisado(id)
            onMarcado?.invoke(id)
        }
        _uiState.update { it.copy(mensaje = "Aporte marcado como revisado") }
    }
}

class DocenteRevisionViewModelFactory(
    private val repository: BitacoraRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DocenteRevisionViewModel::class.java)) {
            return DocenteRevisionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
