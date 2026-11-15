package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DocenteAportesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DocenteAportesUiState())
    val uiState: StateFlow<DocenteAportesUiState> = _uiState.asStateFlow()

    fun inicializarFiltroEstado(estado: String) {
        _uiState.update { it.copy(filtroEstado = estado) }
    }

    fun cambiarFiltroEstudiante(valor: String) {
        _uiState.update { it.copy(filtroEstudiante = valor) }
    }

    fun cambiarFiltroFecha(valor: String) {
        _uiState.update { it.copy(filtroFecha = valor) }
    }

    fun cambiarFiltroEstado(valor: String) {
        _uiState.update { it.copy(filtroEstado = valor) }
    }
}
