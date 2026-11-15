package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.BitacoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EstudianteHistorialViewModel(
    private val repository: BitacoraRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteHistorialUiState())
    val uiState: StateFlow<EstudianteHistorialUiState> = _uiState.asStateFlow()

    fun mostrarDialogoEliminar(id: Int) {
        _uiState.update { it.copy(idPendienteEliminar = id) }
    }

    fun ocultarDialogoEliminar() {
        _uiState.update { it.copy(idPendienteEliminar = null) }
    }

    fun confirmarEliminar(onEliminar: ((Int) -> Unit)? = null) {
        val id = _uiState.value.idPendienteEliminar
        if (id != null) {
            viewModelScope.launch {
                repository?.eliminarRegistro(id)
                onEliminar?.invoke(id)
            }
        }
        _uiState.update { it.copy(idPendienteEliminar = null) }
    }
}

class EstudianteHistorialViewModelFactory(
    private val repository: BitacoraRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstudianteHistorialViewModel::class.java)) {
            return EstudianteHistorialViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
