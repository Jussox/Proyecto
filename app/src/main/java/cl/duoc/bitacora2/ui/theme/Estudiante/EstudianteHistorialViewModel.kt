package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EstudianteHistorialViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteHistorialUiState())
    val uiState: StateFlow<EstudianteHistorialUiState> = _uiState.asStateFlow()

    fun mostrarDialogoEliminar(id: Int) {
        _uiState.update { it.copy(idPendienteEliminar = id) }
    }

    fun ocultarDialogoEliminar() {
        _uiState.update { it.copy(idPendienteEliminar = null) }
    }

    fun confirmarEliminar(onEliminar: (Int) -> Unit) {
        val id = _uiState.value.idPendienteEliminar
        if (id != null) {
            onEliminar(id)
        }
        _uiState.update { it.copy(idPendienteEliminar = null) }
    }
}