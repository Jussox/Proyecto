package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DocenteViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        DocenteUiState(
            administracionExpandida = true,
            revisionExpandida = false,
            seguimientoExpandido = false
        )
    )
    val uiState: StateFlow<DocenteUiState> = _uiState.asStateFlow()

    fun toggleAdministracion() {
        _uiState.update { it.copy(administracionExpandida = !it.administracionExpandida) }
    }

    fun toggleRevision() {
        _uiState.update { it.copy(revisionExpandida = !it.revisionExpandida) }
    }

    fun toggleSeguimiento() {
        _uiState.update { it.copy(seguimientoExpandido = !it.seguimientoExpandido) }
    }
}