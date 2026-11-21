package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.DatosPrueba
import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.EstudianteDto
import cl.duoc.bitacora2.util.mensajeDeError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocenteEstudiantesViewModel(
    private val repository: RemotoRepository = RemotoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocenteEstudiantesUiState())
    val uiState: StateFlow<DocenteEstudiantesUiState> = _uiState.asStateFlow()

    fun cargarEstudiantes(curso: String) {
        if (curso.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            try {
                val listaRemota = repository.obtenerEstudiantes(curso)
                val listaFinal = if (listaRemota.isNotEmpty()) {
                    listaRemota
                } else {
                    // Si el backend no tiene estudiantes para este curso, usa los de prueba
                    DatosPrueba.estudiantesDeCurso(curso).map {
                        EstudianteDto(usuario = it.usuario, nombre = it.nombre, curso = it.curso)
                    }
                }
                _uiState.update { it.copy(cargando = false, estudiantes = listaFinal, error = null) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                // Si falla la conexión, usa los datos locales de prueba como respaldo
                val fallback = DatosPrueba.estudiantesDeCurso(curso).map {
                    EstudianteDto(usuario = it.usuario, nombre = it.nombre, curso = it.curso)
                }
                if (fallback.isNotEmpty()) {
                    _uiState.update { it.copy(cargando = false, estudiantes = fallback, error = null) }
                } else {
                    _uiState.update { it.copy(cargando = false, error = mensajeDeError(e)) }
                }
            }
        }
    }
}
