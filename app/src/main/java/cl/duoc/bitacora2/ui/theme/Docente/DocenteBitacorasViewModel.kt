package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.util.Sesion
import cl.duoc.bitacora2.util.mensajeDeError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocenteBitacorasViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocenteBitacorasUiState())
    val uiState: StateFlow<DocenteBitacorasUiState> = _uiState.asStateFlow()

    fun cargarBitacoras(usuario: String = Sesion.usuario) {
        if (usuario.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val lista = repository.obtenerBitacorasDocente(usuario)
                _uiState.update { it.copy(bitacoras = lista, cargando = false, error = null) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun publicarBitacora(id: Long, usuario: String = Sesion.usuario) {
        viewModelScope.launch {
            _uiState.update { it.copy(accionEnProgresoId = id, errorAccion = null) }
            try {
                repository.publicarBitacora(id, usuario)
                val lista = repository.obtenerBitacorasDocente(usuario)
                _uiState.update { it.copy(bitacoras = lista) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorAccion = mensajeDeError(e)) }
            } finally {
                _uiState.update { it.copy(accionEnProgresoId = null) }
            }
        }
    }

    fun cerrarBitacora(id: Long, usuario: String = Sesion.usuario) {
        viewModelScope.launch {
            _uiState.update { it.copy(accionEnProgresoId = id, errorAccion = null) }
            try {
                repository.cerrarBitacora(id, usuario)
                val lista = repository.obtenerBitacorasDocente(usuario)
                _uiState.update { it.copy(bitacoras = lista) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update { it.copy(errorAccion = mensajeDeError(e)) }
            } finally {
                _uiState.update { it.copy(accionEnProgresoId = null) }
            }
        }
    }

    fun limpiarErrorAccion() {
        _uiState.update { it.copy(errorAccion = null) }
    }
}
