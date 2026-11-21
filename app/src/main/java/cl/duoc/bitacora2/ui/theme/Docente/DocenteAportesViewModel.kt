package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto
import cl.duoc.bitacora2.util.Sesion
import cl.duoc.bitacora2.util.mensajeDeError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocenteAportesViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    companion object {
        const val MAX_BITACORAS_CONSULTA = 20
    }

    private val _uiState = MutableStateFlow(DocenteAportesUiState())
    val uiState: StateFlow<DocenteAportesUiState> = _uiState.asStateFlow()

    fun inicializarFiltroEstado(estado: String) {
        if (estado.isNotBlank() && _uiState.value.filtroEstado != estado) {
            _uiState.update { it.copy(filtroEstado = estado) }
        }
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

    fun limpiarAviso() {
        _uiState.update { it.copy(errorAviso = null) }
    }

    fun cargarAportes(bitacoraId: Long?, estadoInicial: String = "todos") {
        inicializarFiltroEstado(estadoInicial)

        viewModelScope.launch {
            val listaActual = _uiState.value.registros
            if (listaActual.isEmpty()) {
                _uiState.update { it.copy(cargando = true, error = null, errorAviso = null) }
            } else {
                _uiState.update { it.copy(errorAviso = null) }
            }

            try {
                val estudiantesMap = try {
                    repository.obtenerEstudiantes().mapNotNull { e ->
                        val u = e.usuario
                        val n = e.nombre
                        if (!u.isNullOrBlank() && !n.isNullOrBlank()) u to n else null
                    }.toMap()
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Throwable) {
                    emptyMap()
                }

                if (bitacoraId != null && bitacoraId > 0L) {
                    val registros = repository.obtenerRegistrosBitacora(bitacoraId, Sesion.usuario)
                    val bitacora = try {
                        repository.obtenerBitacoraPorId(bitacoraId)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Throwable) {
                        null
                    }
                    val bitacorasMap = if (bitacora != null) mapOf(bitacoraId to bitacora) else emptyMap()

                    _uiState.update {
                        it.copy(
                            cargando = false,
                            registros = registros,
                            bitacorasMap = bitacorasMap,
                            estudiantesMap = estudiantesMap,
                            error = null,
                            errorAviso = null
                        )
                    }
                } else {
                    val todasBitacoras = repository.obtenerBitacorasDocente(Sesion.usuario)
                    val bitacorasAConsultar = todasBitacoras.take(MAX_BITACORAS_CONSULTA)
                    val bitacorasMap = bitacorasAConsultar.mapNotNull { b ->
                        b.id?.let { id -> id to b }
                    }.toMap()

                    val acumulados = mutableListOf<RegistroDto>()
                    var falloParcial = false

                    for (b in bitacorasAConsultar) {
                        val id = b.id ?: continue
                        try {
                            val regs = repository.obtenerRegistrosBitacora(id, Sesion.usuario)
                            acumulados.addAll(regs)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: Throwable) {
                            falloParcial = true
                        }
                    }

                    val aviso = if (falloParcial) "No se pudieron cargar los aportes de algunas bitácoras." else null

                    _uiState.update {
                        it.copy(
                            cargando = false,
                            registros = acumulados,
                            bitacorasMap = bitacorasMap,
                            estudiantesMap = estudiantesMap,
                            error = null,
                            errorAviso = aviso
                        )
                    }
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
}
