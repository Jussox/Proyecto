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

class EstudianteHistorialViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteHistorialUiState())
    val uiState: StateFlow<EstudianteHistorialUiState> = _uiState.asStateFlow()

    fun cargarHistorial() {
        viewModelScope.launch {
            val listaActual = _uiState.value.registros
            if (listaActual.isEmpty()) {
                _uiState.update { it.copy(cargando = true, error = null, errorAviso = null) }
            } else {
                _uiState.update { it.copy(errorAviso = null) }
            }

            try {
                val registros = repository.obtenerRegistrosEstudiante(Sesion.usuario)
                val bitacoras = repository.obtenerBitacorasEstudiante(Sesion.usuario)
                val bitacorasMap = bitacoras.mapNotNull { b ->
                    b.id?.let { id -> id to b }
                }.toMap()

                _uiState.update {
                    it.copy(
                        cargando = false,
                        registros = registros,
                        bitacorasMap = bitacorasMap,
                        error = null,
                        errorAviso = null
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                val mensaje = mensajeDeError(e)
                _uiState.update { estado ->
                    if (estado.registros.isEmpty()) {
                        estado.copy(cargando = false, error = mensaje)
                    } else {
                        estado.copy(cargando = false, errorAviso = mensaje)
                    }
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun mostrarDialogoEliminar(id: Long) {
        _uiState.update { it.copy(idPendienteEliminar = id) }
    }

    fun ocultarDialogoEliminar() {
        _uiState.update { it.copy(idPendienteEliminar = null) }
    }

    fun confirmarEliminar() {
        val id = _uiState.value.idPendienteEliminar ?: return
        if (_uiState.value.eliminandoId != null) return

        _uiState.update { it.copy(eliminandoId = id) }

        viewModelScope.launch {
            try {
                repository.eliminarRegistro(id, Sesion.usuario)
                _uiState.update { it.copy(idPendienteEliminar = null, eliminandoId = null) }
                cargarHistorial()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        eliminandoId = null,
                        idPendienteEliminar = null,
                        errorAviso = mensajeDeError(e)
                    )
                }
            }
        }
    }

    fun limpiarAviso() {
        _uiState.update { it.copy(errorAviso = null) }
    }
}
