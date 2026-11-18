package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.BitacoraRepository
import cl.duoc.bitacora2.data.Recordatorio
import cl.duoc.bitacora2.util.validarFechaRecordatorio
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
        _uiState.update { it.copy(tipo = valor) }
    }

    fun cambiarFecha(valor: String) {
        _uiState.update { it.copy(fecha = valor) }
    }

    fun crearRecordatorio(onCrear: ((String, String) -> Unit)? = null, docente: String = "") {
        val estado = _uiState.value
        if (!validarFechaRecordatorio(estado.fecha)) {
            _uiState.update { it.copy(mensaje = "Indique una fecha válida (dd/mm/aaaa) que no sea anterior a hoy") }
        } else {
            viewModelScope.launch {
                repository?.guardarRecordatorio(
                    Recordatorio(
                        docente = docente,
                        tipo = estado.tipo,
                        fechaProgramada = estado.fecha,
                        estado = "pendiente"
                    )
                )
                onCrear?.invoke(estado.tipo, estado.fecha)
            }
            _uiState.update {
                it.copy(
                    fecha = "",
                    mensaje = "Recordatorio configurado"
                )
            }
        }
    }

    fun completarRecordatorio(id: Int, onCompletar: ((Int) -> Unit)? = null) {
        viewModelScope.launch {
            repository?.completarRecordatorio(id)
            onCompletar?.invoke(id)
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
