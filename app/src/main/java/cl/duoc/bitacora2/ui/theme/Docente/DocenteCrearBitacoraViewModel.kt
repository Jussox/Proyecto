package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.AsignacionDto
import cl.duoc.bitacora2.data.remoto.dto.BitacoraEdicionRequest
import cl.duoc.bitacora2.data.remoto.dto.BitacoraRequest
import cl.duoc.bitacora2.util.MAX_LARGO_TEXTO_LARGO
import cl.duoc.bitacora2.util.MAX_LARGO_TITULO
import cl.duoc.bitacora2.util.mensajeDeError
import cl.duoc.bitacora2.util.validarAnio
import cl.duoc.bitacora2.util.validarLargo
import cl.duoc.bitacora2.util.validarMes
import cl.duoc.bitacora2.util.validarTextoObligatorio
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocenteCrearBitacoraViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocenteCrearBitacoraUiState())
    val uiState: StateFlow<DocenteCrearBitacoraUiState> = _uiState.asStateFlow()

    // ===== INICIALIZACIÓN =====

    fun inicializar(bitacoraId: Long?, docente: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val asignaciones = repository.obtenerAsignacionesDocente(docente)
                if (bitacoraId != null) {
                    // Modo edición: cargar bitácora
                    val bitacora = repository.obtenerBitacoraPorId(bitacoraId)
                    if (bitacora.estado != "borrador") {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                esModoEdicion = true,
                                estadoInvalidoParaEdicion = true,
                                asignaciones = asignaciones
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                esModoEdicion = true,
                                estadoInvalidoParaEdicion = false,
                                idBitacoraCreada = bitacora.id,
                                asignaciones = asignaciones,
                                cursoFijo = bitacora.curso ?: "",
                                asignaturaFija = bitacora.asignatura ?: "",
                                mes = bitacora.mes ?: "",
                                anio = bitacora.anio ?: "",
                                titulo = bitacora.titulo ?: "",
                                proposito = bitacora.proposito ?: "",
                                instrucciones = bitacora.instrucciones ?: "",
                                preguntas = bitacora.preguntas ?: ""
                            )
                        }
                    }
                } else {
                    // Modo creación
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            esModoEdicion = false,
                            asignaciones = asignaciones
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    // ===== CAMBIOS DE CAMPO =====

    fun seleccionarAsignacion(asignacion: AsignacionDto) {
        _uiState.update { it.copy(asignacionSeleccionada = asignacion) }
    }

    fun cambiarMes(valor: String) {
        _uiState.update { it.copy(mes = valor) }
    }

    fun cambiarAnio(valor: String) {
        _uiState.update { it.copy(anio = valor) }
    }

    fun cambiarTitulo(valor: String) {
        _uiState.update { it.copy(titulo = valor) }
    }

    fun cambiarProposito(valor: String) {
        _uiState.update { it.copy(proposito = valor) }
    }

    fun cambiarInstrucciones(valor: String) {
        _uiState.update { it.copy(instrucciones = valor) }
    }

    fun cambiarPreguntas(valor: String) {
        _uiState.update { it.copy(preguntas = valor) }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = "", mensajeEsError = false) }
    }

    // ===== VALIDACIÓN INTERNA (sin Room.Asignacion) =====

    private fun validarCamposTexto(): String? {
        val estado = _uiState.value
        return when {
            !validarTextoObligatorio(estado.mes) ||
            !validarTextoObligatorio(estado.anio) ||
            !validarTextoObligatorio(estado.titulo) ||
            !validarTextoObligatorio(estado.proposito) ||
            !validarTextoObligatorio(estado.instrucciones) ||
            !validarTextoObligatorio(estado.preguntas) ->
                "Complete todos los campos obligatorios"
            !validarMes(estado.mes) ->
                "Ingrese un mes válido (por ejemplo: Octubre)"
            !validarAnio(estado.anio) ->
                "El año debe tener 4 dígitos y ser el actual o el siguiente"
            !validarLargo(estado.titulo, MAX_LARGO_TITULO) ->
                "El título no puede superar los 100 caracteres"
            !validarLargo(estado.proposito, MAX_LARGO_TEXTO_LARGO) ->
                "El propósito no puede superar los 2000 caracteres"
            !validarLargo(estado.instrucciones, MAX_LARGO_TEXTO_LARGO) ->
                "Las instrucciones no pueden superar los 2000 caracteres"
            !validarLargo(estado.preguntas, MAX_LARGO_TEXTO_LARGO) ->
                "Las preguntas no pueden superar los 2000 caracteres"
            else -> null
        }
    }

    private fun validarParaCrear(): String? {
        val estado = _uiState.value
        if (estado.asignacionSeleccionada == null) {
            return "Seleccione un curso y asignatura"
        }
        return validarCamposTexto()
    }

    // ===== ACCIONES =====

    /** Guardar como borrador (crear o editar según idBitacoraCreada) */
    fun guardarBorrador(docente: String) {
        if (_uiState.value.guardando) return
        val error = if (_uiState.value.idBitacoraCreada != null || _uiState.value.esModoEdicion)
            validarCamposTexto()
        else
            validarParaCrear()
        if (error != null) {
            _uiState.update { it.copy(mensaje = error, mensajeEsError = true) }
            return
        }
        _uiState.update { it.copy(guardando = true, mensaje = "", mensajeEsError = false) }
        viewModelScope.launch {
            try {
                val estado = _uiState.value
                val idExistente = estado.idBitacoraCreada
                if (idExistente != null) {
                    // Ya existe: usar PUT
                    repository.editarBitacora(
                        idExistente, docente,
                        BitacoraEdicionRequest(
                            titulo = estado.titulo,
                            proposito = estado.proposito,
                            instrucciones = estado.instrucciones,
                            preguntas = estado.preguntas
                        )
                    )
                    _uiState.update {
                        it.copy(
                            guardando = false,
                            mensaje = "Cambios guardados como borrador.",
                            mensajeEsError = false,
                            guardado = true
                        )
                    }
                } else {
                    // Crear nueva
                    val a = estado.asignacionSeleccionada!!
                    val nueva = repository.crearBitacora(
                        BitacoraRequest(
                            docente = docente,
                            curso = a.curso ?: "",
                            asignatura = a.asignatura ?: "",
                            mes = estado.mes.trim().replaceFirstChar { it.uppercase() },
                            anio = estado.anio.trim(),
                            titulo = estado.titulo,
                            proposito = estado.proposito,
                            instrucciones = estado.instrucciones,
                            preguntas = estado.preguntas
                        )
                    )
                    _uiState.update {
                        it.copy(
                            guardando = false,
                            idBitacoraCreada = nueva.id,
                            mensaje = "Bitácora guardada. Puedes publicarla desde «Mis bitácoras mensuales».",
                            mensajeEsError = false,
                            guardado = true
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update { it.copy(mensaje = mensajeDeError(e), mensajeEsError = true) }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }

    /** Guardar y publicar (crear o editar + publicar) */
    fun guardarYPublicar(docente: String) {
        if (_uiState.value.guardando) return
        val error = if (_uiState.value.idBitacoraCreada != null || _uiState.value.esModoEdicion)
            validarCamposTexto()
        else
            validarParaCrear()
        if (error != null) {
            _uiState.update { it.copy(mensaje = error, mensajeEsError = true) }
            return
        }
        _uiState.update { it.copy(guardando = true, mensaje = "", mensajeEsError = false) }
        viewModelScope.launch {
            try {
                val estado = _uiState.value
                val idExistente = estado.idBitacoraCreada
                val idParaPublicar: Long
                if (idExistente != null) {
                    // Ya existe: PUT primero
                    repository.editarBitacora(
                        idExistente, docente,
                        BitacoraEdicionRequest(
                            titulo = estado.titulo,
                            proposito = estado.proposito,
                            instrucciones = estado.instrucciones,
                            preguntas = estado.preguntas
                        )
                    )
                    idParaPublicar = idExistente
                } else {
                    // Crear nueva
                    val a = estado.asignacionSeleccionada!!
                    val nueva = repository.crearBitacora(
                        BitacoraRequest(
                            docente = docente,
                            curso = a.curso ?: "",
                            asignatura = a.asignatura ?: "",
                            mes = estado.mes.trim().replaceFirstChar { it.uppercase() },
                            anio = estado.anio.trim(),
                            titulo = estado.titulo,
                            proposito = estado.proposito,
                            instrucciones = estado.instrucciones,
                            preguntas = estado.preguntas
                        )
                    )
                    idParaPublicar = nueva.id ?: throw IllegalStateException("No se obtuvo id de la bitácora creada")
                    _uiState.update { it.copy(idBitacoraCreada = idParaPublicar) }
                }
                // Publicar
                repository.publicarBitacora(idParaPublicar, docente)
                _uiState.update {
                    it.copy(
                        guardando = false,
                        mensaje = "Bitácora guardada y publicada.",
                        mensajeEsError = false,
                        guardado = true
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                // Si el POST funcionó pero publicar falló, mostrar aviso (idBitacoraCreada ya fue seteado)
                _uiState.update {
                    val yaCreada = it.idBitacoraCreada != null
                    it.copy(
                        mensaje = if (yaCreada)
                            "Quedó guardada como borrador. ${mensajeDeError(e)}"
                        else
                            mensajeDeError(e),
                        mensajeEsError = true
                    )
                }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }

    /** Guardar cambios en modo edición puro (volver sin publicar) */
    fun guardarCambios(docente: String) {
        if (_uiState.value.guardando) return
        val error = validarCamposTexto()
        if (error != null) {
            _uiState.update { it.copy(mensaje = error, mensajeEsError = true) }
            return
        }
        val id = _uiState.value.idBitacoraCreada ?: return
        _uiState.update { it.copy(guardando = true, mensaje = "", mensajeEsError = false) }
        viewModelScope.launch {
            try {
                val estado = _uiState.value
                repository.editarBitacora(
                    id, docente,
                    BitacoraEdicionRequest(
                        titulo = estado.titulo,
                        proposito = estado.proposito,
                        instrucciones = estado.instrucciones,
                        preguntas = estado.preguntas
                    )
                )
                _uiState.update {
                    it.copy(
                        guardando = false,
                        mensaje = "Cambios guardados.",
                        mensajeEsError = false,
                        guardado = true
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                _uiState.update { it.copy(mensaje = mensajeDeError(e), mensajeEsError = true) }
            } finally {
                _uiState.update { it.copy(guardando = false) }
            }
        }
    }
}
