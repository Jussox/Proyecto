package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.externa.RecursosEducativosRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class RecursosEducativosViewModel(
    private val repository: RecursosEducativosRepository = RecursosEducativosRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecursosEducativosUiState())
    val uiState: StateFlow<RecursosEducativosUiState> = _uiState.asStateFlow()

    fun cambiarConsulta(valor: String) {
        _uiState.value = _uiState.value.copy(consulta = valor, error = null)
    }

    fun buscar() {
        val tema = _uiState.value.consulta.trim()
        if (tema.isBlank() || _uiState.value.cargando) return
        _uiState.value = _uiState.value.copy(cargando = true, error = null, busquedaRealizada = true)
        viewModelScope.launch {
            try {
                val recursos = repository.buscarPorTema(tema)
                _uiState.value = _uiState.value.copy(cargando = false, recursos = recursos)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = "No fue posible conectar con Wikipedia. Revisa tu conexión a Internet."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    error = e.message ?: "No se pudieron obtener recursos para este tema."
                )
            }
        }
    }
}
