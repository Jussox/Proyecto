package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.BitacoraRepository
import cl.duoc.bitacora2.data.Recordatorio
import cl.duoc.bitacora2.util.mensajeRecordatorioCompletado
import cl.duoc.bitacora2.util.mensajeRecordatorioProgramado
import cl.duoc.bitacora2.util.validarFechaRecordatorioConMensaje
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocenteRecordatoriosViewModel(
    private val repository: BitacoraRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocenteRecordatoriosUiState())
    val uiState: StateFlow<DocenteRecordatoriosUiState> = _uiState.asStateFlow()

    fun cambiarTipo(valor: String) {
        _uiState.update { it.copy(tipo = valor, mensaje = "") }
    }

    fun cambiarFecha(valor: String) {
        _uiState.update { it.copy(fecha = valor, mensaje = "") }
    }

    fun crearRecordatorio(onCrear: ((String, String) -> Unit)? = null, docente: String = "") {
        val estado = _uiState.value
        val fechaLimpia = estado.fecha.trim()
        val errorFecha = validarFechaRecordatorioConMensaje(fechaLimpia)

        if (errorFecha != null) {
            _uiState.update { it.copy(mensaje = errorFecha) }
        } else {
            val tipoSeleccionado = estado.tipo
            viewModelScope.launch {
                repository?.guardarRecordatorio(
                    Recordatorio(
                        docente = docente,
                        tipo = tipoSeleccionado,
                        fechaProgramada = fechaLimpia,
                        estado = "pendiente"
                    )
                )
                onCrear?.invoke(tipoSeleccionado, fechaLimpia)
            }
            _uiState.update {
                it.copy(
                    fecha = "",
                    mensaje = mensajeRecordatorioProgramado(tipoSeleccionado, fechaLimpia)
                )
            }
        }
    }

    fun completarRecordatorio(
        id: Int,
        onCompletar: ((Int) -> Unit)? = null,
        tipoRecordatorio: String? = null
    ) {
        viewModelScope.launch {
            repository?.completarRecordatorio(id)
            onCompletar?.invoke(id)
        }
        _uiState.update {
            it.copy(mensaje = mensajeRecordatorioCompletado(tipoRecordatorio))
        }
    }
}

class DocenteRecordatoriosViewModelFactory(
    private val repository: BitacoraRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DocenteRecordatoriosViewModel::class.java)) {
            return DocenteRecordatoriosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}