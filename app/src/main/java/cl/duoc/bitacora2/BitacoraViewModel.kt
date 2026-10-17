package cl.duoc.bitacora2

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.Recordatorio
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.data.fechaHoy
import cl.duoc.bitacora2.data.Asignacion
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.BitacoraDatabase
import cl.duoc.bitacora2.data.BitacoraRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BitacoraViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = BitacoraDatabase.getInstance(application)
    private val repository = BitacoraRepository(database.bitacoraDao())

    // Sesión y selección actual
    var usuarioActual by mutableStateOf("")
    var bitacoraSeleccionada by mutableStateOf(0)
    var registroSeleccionado by mutableStateOf(0)
    var asignacionFiltro by mutableStateOf<Asignacion?>(null)
    var soloRetroalimentacion by mutableStateOf(false)
    var soloHistorialBitacoras by mutableStateOf(false)
    var estadoAportes by mutableStateOf("todos")
    var bitacoraAportes by mutableStateOf(0)
    var modoParticipacion by mutableStateOf("seguimiento")

    // Datos
    val bitacoras: StateFlow<List<Bitacora>> =
        repository
            .obtenerBitacoras()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val registros: StateFlow<List<Registro>> =
        repository
            .obtenerRegistros()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val recordatorios: StateFlow<List<Recordatorio>> =
        repository
            .obtenerRecordatorios()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    //Registro e historial estudiante

    fun actualizarRegistro(registro: Registro) {
        viewModelScope.launch {
            repository.actualizarRegistro(registro)
        }
    }

    fun eliminarRegistro(id: Int) {
        viewModelScope.launch {
            repository.eliminarRegistro(id)
        }
    }

    // Acciones del docente
    fun guardarBitacora(bitacora: Bitacora) {
        viewModelScope.launch {
            repository.guardarBitacora(bitacora)
        }
    }

    fun publicarBitacora(id: Int) {
        viewModelScope.launch {
            repository.publicarBitacora(id, fechaHoy())
        }
    }

    fun cerrarBitacora(id: Int) {
        viewModelScope.launch {
            repository.cerrarBitacora(id, fechaHoy())
        }
    }

    fun guardarComentario(id: Int, comentario: String) {
        viewModelScope.launch {
            repository.guardarComentario(id, comentario, fechaHoy(), usuarioActual)
        }
    }

    fun marcarRevisado(id: Int) {
        viewModelScope.launch {
            repository.marcarRevisado(id)
        }
    }

    fun crearRecordatorio(tipo: String, fecha: String) {
        viewModelScope.launch {
            repository.guardarRecordatorio(
                Recordatorio(
                    docente = usuarioActual,
                    tipo = tipo,
                    fechaProgramada = fecha,
                    estado = "pendiente"
                )
            )
        }
    }

    fun completarRecordatorio(id: Int) {
        viewModelScope.launch {
            repository.completarRecordatorio(id)
        }
    }

    // Acciones del estudiante
    fun guardarRegistro(registro: Registro) {
        viewModelScope.launch {
            repository.guardarRegistro(registro)
        }
    }


    fun enviarRegistro(id: Int) {
        viewModelScope.launch {
            repository.enviarRegistro(id, fechaHoy())
        }
    }
}