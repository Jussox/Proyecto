package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.util.mensajeDeError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EstudianteBitacoraViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteBitacoraUiState())
    val uiState: StateFlow<EstudianteBitacoraUiState> = _uiState.asStateFlow()

    fun cargarBitacora(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val bitacora = repository.obtenerBitacoraPorId(id)
                _uiState.update { it.copy(cargando = false, bitacora = bitacora, error = null) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update { it.copy(error = mensajeDeError(e)) }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }
}
