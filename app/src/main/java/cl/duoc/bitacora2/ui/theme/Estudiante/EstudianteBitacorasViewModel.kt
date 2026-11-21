package cl.duoc.bitacora2.ui.theme.Estudiante

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

class EstudianteBitacorasViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteBitacorasUiState())
    val uiState: StateFlow<EstudianteBitacorasUiState> = _uiState.asStateFlow()

    fun cargarBitacoras() {
        viewModelScope.launch {
            val listaActual = _uiState.value.bitacoras
            if (listaActual.isEmpty()) {
                // Primera carga: spinner a pantalla completa
                _uiState.update { it.copy(cargando = true, error = null, errorAviso = null) }
            } else {
                // Refresco: mantener lista, limpiar aviso previo
                _uiState.update { it.copy(errorAviso = null) }
            }
            try {
                val lista = repository.obtenerBitacorasEstudiante(Sesion.usuario)
                _uiState.update {
                    it.copy(cargando = false, bitacoras = lista, error = null, errorAviso = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                val mensaje = mensajeDeError(e)
                _uiState.update { estado ->
                    if (estado.bitacoras.isEmpty()) {
                        // Sin lista: pantalla de error completa
                        estado.copy(cargando = false, error = mensaje)
                    } else {
                        // Con lista: banner de aviso, mantener lista
                        estado.copy(cargando = false, errorAviso = mensaje)
                    }
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun limpiarAviso() {
        _uiState.update { it.copy(errorAviso = null) }
    }
}
