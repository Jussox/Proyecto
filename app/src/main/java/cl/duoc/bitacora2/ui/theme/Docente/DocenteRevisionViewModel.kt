package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.ComentarioRequest
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto
import cl.duoc.bitacora2.util.MAX_LARGO_TEXTO_LARGO
import cl.duoc.bitacora2.util.Sesion
import cl.duoc.bitacora2.util.mensajeDeError
import cl.duoc.bitacora2.util.validarComentario
import cl.duoc.bitacora2.util.validarLargo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocenteRevisionViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocenteRevisionUiState())
    val uiState: StateFlow<DocenteRevisionUiState> = _uiState.asStateFlow()

    fun cargarRevision(registroId: Long, bitacoraId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, errorOperacion = null) }

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

                var registroEncontrado: RegistroDto? = null
                var bitacoraEncontrada: BitacoraDto? = null

                if (bitacoraId != null && bitacoraId > 0L) {
                    val regs = repository.obtenerRegistrosBitacora(bitacoraId, Sesion.usuario)
                    registroEncontrado = regs.firstOrNull { it.id == registroId }
                    bitacoraEncontrada = try {
                        repository.obtenerBitacoraPorId(bitacoraId)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Throwable) {
                        null
                    }
                }

                if (registroEncontrado == null) {
                    val bitacoras = repository.obtenerBitacorasDocente(Sesion.usuario)
                    for (b in bitacoras) {
                        val bId = b.id ?: continue
                        val regs = try {
                            repository.obtenerRegistrosBitacora(bId, Sesion.usuario)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (_: Throwable) {
                            emptyList()
                        }
                        val r = regs.firstOrNull { it.id == registroId }
                        if (r != null) {
                            registroEncontrado = r
                            bitacoraEncontrada = b
                            break
                        }
                    }
                }

                if (registroEncontrado == null) {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            error = "El aporte no está disponible.",
                            registro = null
                        )
                    }
                } else {
                    val estudianteUsuario = registroEncontrado.estudiante ?: ""
                    val nombreEstudiante = estudiantesMap[estudianteUsuario]?.ifBlank { estudianteUsuario }
                        ?: estudianteUsuario.ifBlank { "Estudiante" }
                    val comentario = registroEncontrado.comentario ?: ""

                    _uiState.update {
                        it.copy(
                            cargando = false,
                            registro = registroEncontrado,
                            bitacora = bitacoraEncontrada,
                            nombreEstudiante = nombreEstudiante,
                            comentario = comentario,
                            comentarioOriginal = comentario,
                            error = null,
                            errorOperacion = null
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        error = mensajeDeError(e)
                    )
                }
            } finally {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun cambiarComentario(valor: String) {
        _uiState.update {
            it.copy(
                comentario = valor,
                errorComentario = null,
                errorOperacion = null,
                mensajeExito = null
            )
        }
    }

    fun guardarComentario(onExito: (() -> Unit)? = null) {
        if (_uiState.value.guardando) return

        val comentario = _uiState.value.comentario
        if (!validarComentario(comentario)) {
            _uiState.update { it.copy(errorComentario = "Escriba un comentario antes de guardar") }
            return
        }
        if (!validarLargo(comentario, MAX_LARGO_TEXTO_LARGO)) {
            _uiState.update { it.copy(errorComentario = "El comentario no puede superar los 2000 caracteres") }
            return
        }

        val registroId = _uiState.value.registro?.id ?: return

        _uiState.update { it.copy(guardando = true, errorOperacion = null, mensajeExito = null) }

        viewModelScope.launch {
            try {
                val actualizado = repository.agregarComentario(
                    id = registroId,
                    docente = Sesion.usuario,
                    request = ComentarioRequest(comentario = comentario.trim())
                )
                _uiState.update {
                    it.copy(
                        guardando = false,
                        registro = actualizado,
                        comentarioOriginal = actualizado.comentario ?: comentario.trim(),
                        mensajeExito = "Comentario guardado",
                        errorOperacion = null
                    )
                }
                onExito?.invoke()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        errorOperacion = mensajeDeError(e)
                    )
                }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }

    fun marcarRevisado(onExito: (() -> Unit)? = null) {
        if (_uiState.value.guardando) return

        val comentario = _uiState.value.comentario
        if (!validarComentario(comentario)) {
            _uiState.update { it.copy(errorComentario = "Escriba un comentario antes de marcar como revisado") }
            return
        }
        if (!validarLargo(comentario, MAX_LARGO_TEXTO_LARGO)) {
            _uiState.update { it.copy(errorComentario = "El comentario no puede superar los 2000 caracteres") }
            return
        }

        val registroId = _uiState.value.registro?.id ?: return
        val comentarioOriginal = _uiState.value.comentarioOriginal

        _uiState.update { it.copy(guardando = true, errorOperacion = null, mensajeExito = null) }

        viewModelScope.launch {
            try {
                val comentarioCambio = comentario.trim() != comentarioOriginal.trim() ||
                        _uiState.value.registro?.comentario.isNullOrBlank()

                if (comentarioCambio) {
                    val actualizadoConComentario = repository.agregarComentario(
                        id = registroId,
                        docente = Sesion.usuario,
                        request = ComentarioRequest(comentario = comentario.trim())
                    )
                    _uiState.update {
                        it.copy(
                            registro = actualizadoConComentario,
                            comentarioOriginal = actualizadoConComentario.comentario ?: comentario.trim()
                        )
                    }
                }

                val revisado = repository.marcarRevisado(registroId, Sesion.usuario)
                _uiState.update {
                    it.copy(
                        guardando = false,
                        guardado = true,
                        registro = revisado,
                        mensajeExito = "Aporte marcado como revisado",
                        errorOperacion = null
                    )
                }
                onExito?.invoke()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        guardando = false,
                        errorOperacion = mensajeDeError(e)
                    )
                }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }

    fun limpiarAvisos() {
        _uiState.update { it.copy(errorOperacion = null, mensajeExito = null) }
    }
}
