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

class EstudianteRetroalimentacionViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteRetroalimentacionUiState())
    val uiState: StateFlow<EstudianteRetroalimentacionUiState> = _uiState.asStateFlow()

    fun cargarRetroalimentacion() {
        viewModelScope.launch {
            val listaActual = _uiState.value.registros
            if (listaActual.isEmpty()) {
                _uiState.update { it.copy(cargando = true, error = null, errorAviso = null) }
            } else {
                _uiState.update { it.copy(errorAviso = null) }
            }

            try {
                val todos = repository.obtenerRegistrosEstudiante(Sesion.usuario)
                val retroalimentados = todos
                    .filter { it.estado == "revisado" || !it.comentario.isNullOrBlank() }
                    .sortedByDescending { it.fechaRetroalimentacion }

                val bitacoras = repository.obtenerBitacorasEstudiante(Sesion.usuario)
                val bitacorasMap = bitacoras.mapNotNull { b ->
                    b.id?.let { id -> id to b }
                }.toMap()

                val docentesMap = try {
                    repository.obtenerDocentes().mapNotNull { d ->
                        val u = d.usuario
                        val n = d.nombre
                        if (!u.isNullOrBlank() && !n.isNullOrBlank()) u to n else null
                    }.toMap()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Throwable) {
                    emptyMap()
                }

                _uiState.update {
                    it.copy(
                        cargando = false,
                        registros = retroalimentados,
                        bitacorasMap = bitacorasMap,
                        docentesMap = docentesMap,
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

    fun limpiarAviso() {
        _uiState.update { it.copy(errorAviso = null) }
    }
}
