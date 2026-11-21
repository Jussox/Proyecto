package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.RegistroRequest
import cl.duoc.bitacora2.util.Sesion
import cl.duoc.bitacora2.util.mensajeDeError
import cl.duoc.bitacora2.util.validarFormularioRegistroEstudiante
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EstudianteRegistroViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteRegistroUiState())
    val uiState: StateFlow<EstudianteRegistroUiState> = _uiState.asStateFlow()

    companion object {
        /** Convierte la lista de dudas y la duda en curso a un solo String con saltos de línea. */
        fun dudasAListoString(lista: List<String>, dudaPendiente: String = ""): String {
            val todas = if (dudaPendiente.isNotBlank()) lista + dudaPendiente.trim() else lista
            return todas.joinToString("\n")
        }

        /** Convierte el String guardado en el backend a la lista de dudas separadas. */
        fun stringADudasLista(texto: String?): List<String> {
            if (texto.isNullOrBlank()) return emptyList()
            return texto.lines()
                .map { it.trim().removePrefix("•").trim() }
                .filter { it.isNotBlank() }
        }
    }

    fun inicializar(bitacoraId: Long?, registroId: Long?) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, estadoInvalido = false) }
            try {
                if (registroId != null) {
                    // Modo edición: cargar registro existente
                    val registros = repository.obtenerRegistrosEstudiante(Sesion.usuario)
                    val registro = registros.firstOrNull { it.id == registroId }
                    if (registro == null) {
                        _uiState.update { it.copy(cargando = false, error = "Registro no encontrado") }
                        return@launch
                    }

                    if (registro.estado != "borrador") {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                estadoInvalido = true,
                                mensajeEstadoInvalido = "Solo se puede editar un registro en estado borrador"
                            )
                        }
                        return@launch
                    }

                    val bitacora = repository.obtenerBitacoraPorId(registro.bitacoraId ?: 0L)
                    if (bitacora.estado == "cerrada") {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                bitacora = bitacora,
                                estadoInvalido = true,
                                mensajeEstadoInvalido = "La bitácora está cerrada. No se pueden modificar ni enviar registros."
                            )
                        }
                        return@launch
                    }

                    val listaDudas = stringADudasLista(registro.dudas)
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            bitacora = bitacora,
                            idRegistro = registro.id,
                            esModoEdicion = true,
                            fecha = registro.fechaClase ?: "",
                            contenido = registro.contenido ?: "",
                            actividad = registro.actividad ?: "",
                            aprendizaje = registro.aprendizaje ?: "",
                            dudas = "",
                            listaDudas = listaDudas,
                            fotografias = registro.fotografias ?: "",
                            archivos = registro.archivos ?: "",
                            enlaces = registro.enlaces ?: "",
                            error = null
                        )
                    }
                } else if (bitacoraId != null) {
                    // Modo creación: cargar bitácora
                    val bitacora = repository.obtenerBitacoraPorId(bitacoraId)
                    if (bitacora.estado != "publicada") {
                        _uiState.update {
                            it.copy(
                                cargando = false,
                                bitacora = bitacora,
                                estadoInvalido = true,
                                mensajeEstadoInvalido = "La bitácora está cerrada. No se pueden crear nuevos registros."
                            )
                        }
                        return@launch
                    }

                    _uiState.update {
                        it.copy(
                            cargando = false,
                            bitacora = bitacora,
                            esModoEdicion = false,
                            error = null
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

    fun cambiarFecha(valor: String) { _uiState.update { it.copy(fecha = valor) } }
    fun cambiarContenido(valor: String) { _uiState.update { it.copy(contenido = valor) } }
    fun cambiarActividad(valor: String) { _uiState.update { it.copy(actividad = valor) } }
    fun cambiarAprendizaje(valor: String) { _uiState.update { it.copy(aprendizaje = valor) } }
    fun cambiarDudas(valor: String) { _uiState.update { it.copy(dudas = valor) } }
    fun cambiarFotografias(valor: String) { _uiState.update { it.copy(fotografias = valor) } }
    fun cambiarArchivos(valor: String) { _uiState.update { it.copy(archivos = valor) } }
    fun cambiarEnlaces(valor: String) { _uiState.update { it.copy(enlaces = valor) } }

    fun agregarDuda() {
        val texto = _uiState.value.dudas.trim()
        if (texto.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    listaDudas = it.listaDudas + texto,
                    dudas = ""
                )
            }
        }
    }

    fun eliminarDuda(indice: Int) {
        _uiState.update {
            val nuevaLista = it.listaDudas.toMutableList()
            if (indice in nuevaLista.indices) {
                nuevaLista.removeAt(indice)
            }
            it.copy(listaDudas = nuevaLista)
        }
    }

    fun mostrarError(mensaje: String) {
        _uiState.update { it.copy(mensaje = mensaje, mensajeEsError = true) }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensaje = "", mensajeEsError = false) }
    }

    private fun validarFormulario(): String? {
        val s = _uiState.value
        val bitacora = s.bitacora
        return validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacora != null,
            fecha = s.fecha,
            mesBitacora = bitacora?.mes ?: "",
            anioBitacora = bitacora?.anio ?: "",
            contenido = s.contenido,
            actividad = s.actividad,
            aprendizaje = s.aprendizaje,
            enlaces = s.enlaces
        )
    }

    fun guardarBorrador() {
        if (_uiState.value.guardando) return
        val errorValidacion = validarFormulario()
        if (errorValidacion != null) {
            _uiState.update { it.copy(mensaje = errorValidacion, mensajeEsError = true) }
            return
        }

        _uiState.update { it.copy(guardando = true, mensaje = "", mensajeEsError = false) }

        viewModelScope.launch {
            try {
                val s = _uiState.value
                val dudasTexto = dudasAListoString(s.listaDudas, s.dudas)
                val bitacoraId = s.bitacora?.id ?: throw IllegalStateException("Bitácora no disponible")

                val request = RegistroRequest(
                    bitacoraId = bitacoraId,
                    estudiante = Sesion.usuario,
                    fechaClase = s.fecha.trim(),
                    contenido = s.contenido.trim(),
                    actividad = s.actividad.trim(),
                    aprendizaje = s.aprendizaje.trim(),
                    dudas = dudasTexto,
                    fotografias = s.fotografias,
                    archivos = s.archivos,
                    enlaces = s.enlaces
                )

                val idExistente = s.idRegistro
                if (idExistente != null) {
                    repository.actualizarRegistro(idExistente, Sesion.usuario, request)
                    _uiState.update {
                        it.copy(
                            guardado = true,
                            mensaje = "Borrador guardado con éxito.",
                            mensajeEsError = false
                        )
                    }
                } else {
                    val creado = repository.crearRegistro(request)
                    _uiState.update {
                        it.copy(
                            idRegistro = creado.id,
                            guardado = true,
                            mensaje = "Borrador guardado con éxito.",
                            mensajeEsError = false
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

    fun enviarAlDocente() {
        if (_uiState.value.guardando) return
        val errorValidacion = validarFormulario()
        if (errorValidacion != null) {
            _uiState.update { it.copy(mensaje = errorValidacion, mensajeEsError = true) }
            return
        }

        _uiState.update { it.copy(guardando = true, mensaje = "", mensajeEsError = false) }

        viewModelScope.launch {
            try {
                val s = _uiState.value
                val dudasTexto = dudasAListoString(s.listaDudas, s.dudas)
                val bitacoraId = s.bitacora?.id ?: throw IllegalStateException("Bitácora no disponible")

                val request = RegistroRequest(
                    bitacoraId = bitacoraId,
                    estudiante = Sesion.usuario,
                    fechaClase = s.fecha.trim(),
                    contenido = s.contenido.trim(),
                    actividad = s.actividad.trim(),
                    aprendizaje = s.aprendizaje.trim(),
                    dudas = dudasTexto,
                    fotografias = s.fotografias,
                    archivos = s.archivos,
                    enlaces = s.enlaces
                )

                val idFinal: Long
                val idExistente = s.idRegistro
                if (idExistente != null) {
                    // Si ya existe ID: primero actualizamos los datos con PUT
                    repository.actualizarRegistro(idExistente, Sesion.usuario, request)
                    idFinal = idExistente
                } else {
                    // Si es nuevo: creamos con POST
                    val creado = repository.crearRegistro(request)
                    idFinal = creado.id ?: throw IllegalStateException("No se obtuvo ID del registro creado")
                    // Guardamos inmediatamente idRegistro para evitar duplicados si falla el envío
                    _uiState.update { it.copy(idRegistro = idFinal) }
                }

                // Luego enviamos al docente
                repository.enviarRegistro(idFinal, Sesion.usuario)
                _uiState.update {
                    it.copy(
                        guardado = true,
                        mensaje = "Registro enviado. Estado: pendiente de revisión.",
                        mensajeEsError = false
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
