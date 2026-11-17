package cl.duoc.bitacora2.ui.theme.Docente

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.BitacoraDatabase
import cl.duoc.bitacora2.data.BitacoraRepository
import cl.duoc.bitacora2.data.DatosPrueba
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.util.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocenteParticipacionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = BitacoraDatabase.getInstance(application)
    private val repository = BitacoraRepository(database.bitacoraDao())

    private val _uiState = MutableStateFlow(DocenteParticipacionUiState())
    val uiState: StateFlow<DocenteParticipacionUiState> = _uiState.asStateFlow()

    private var todasLasBitacoras: List<Bitacora> = emptyList()
    private var todosLosRegistros: List<Registro> = emptyList()

    init {
        viewModelScope.launch {
            combine(
                repository.obtenerBitacoras(),
                repository.obtenerRegistros()
            ) { bitacoras, registros ->
                Pair(bitacoras, registros)
            }.collect { (bitacoras, registros) ->
                todasLasBitacoras = bitacoras
                todosLosRegistros = registros
                recalcular()
            }
        }
    }

    fun seleccionarBitacora(id: Int) {
        _uiState.update { it.copy(seleccionada = id) }
        recalcular()
    }

    private fun recalcular() {
        val docenteActual = Sesion.usuario
        val bitacorasDocente = todasLasBitacoras.filter { it.docente == docenteActual }
        val registrosDocente = todosLosRegistros.filter { r ->
            bitacorasDocente.any { it.id == r.bitacoraId }
        }
        val bitacorasFiltradas = bitacorasDocente.filter {
            it.estado == "publicada" || it.estado == "cerrada"
        }

        val seleccionadaId = _uiState.value.seleccionada
        val bitacora = bitacorasFiltradas.firstOrNull { it.id == seleccionadaId }

        if (bitacora != null) {
            val enviados = registrosDocente.filter { it.bitacoraId == bitacora.id }
            val estudiantes = DatosPrueba.estudiantesDeCurso(bitacora.curso)
            val sinParticipacion = estudiantes.filter { e -> enviados.none { it.estudiante == e.usuario } }
            val participantes = estudiantes.filter { e -> enviados.any { it.estudiante == e.usuario } }
            val pendientes = enviados.count { it.estado == "pendiente" }
            val revisados = enviados.count { it.estado == "revisado" }
            val nombreDocente = DatosPrueba.nombreDe(bitacora.docente)

            _uiState.update {
                it.copy(
                    bitacoras = bitacorasFiltradas,
                    bitacora = bitacora,
                    nombreDocente = nombreDocente,
                    estudiantes = estudiantes,
                    participantes = participantes,
                    sinParticipacion = sinParticipacion,
                    enviados = enviados,
                    pendientes = pendientes,
                    revisados = revisados
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    bitacoras = bitacorasFiltradas,
                    bitacora = null,
                    nombreDocente = "",
                    estudiantes = emptyList(),
                    participantes = emptyList(),
                    sinParticipacion = emptyList(),
                    enviados = emptyList(),
                    pendientes = 0,
                    revisados = 0
                )
            }
        }
    }
}
