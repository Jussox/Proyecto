package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.BitacoraRepository
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.data.fechaHoy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class EstudianteRegistroViewModel(
    private val repository: BitacoraRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteRegistroUiState())
    val uiState: StateFlow<EstudianteRegistroUiState> = _uiState.asStateFlow()

    private var bitacoraId: Int = 0
    private var estudiante: String = ""
    private var registroExistente: Registro? = null

    fun inicializar(bitacoraId: Int, estudiante: String, registro: Registro?) {
        this.bitacoraId = bitacoraId
        this.estudiante = estudiante
        this.registroExistente = registro
        if (registro != null) {
            _uiState.update {
                it.copy(
                    fecha = registro.fechaClase,
                    contenido = registro.contenido,
                    actividad = registro.actividad,
                    aprendizaje = registro.aprendizaje,
                    dudas = registro.dudas,
                    fotografias = registro.fotografias,
                    archivos = registro.archivos,
                    enlaces = registro.enlaces
                )
            }
        }
    }

    fun cambiarFecha(valor: String) {
        _uiState.update { it.copy(fecha = valor) }
    }

    fun cambiarContenido(valor: String) {
        _uiState.update { it.copy(contenido = valor) }
    }

    fun cambiarActividad(valor: String) {
        _uiState.update { it.copy(actividad = valor) }
    }

    fun cambiarAprendizaje(valor: String) {
        _uiState.update { it.copy(aprendizaje = valor) }
    }

    fun cambiarDudas(valor: String) {
        _uiState.update { it.copy(dudas = valor) }
    }

    fun cambiarFotografias(valor: String) {
        _uiState.update { it.copy(fotografias = valor) }
    }

    fun cambiarArchivos(valor: String) {
        _uiState.update { it.copy(archivos = valor) }
    }

    fun cambiarEnlaces(valor: String) {
        _uiState.update { it.copy(enlaces = valor) }
    }

    private fun fechaValida(fecha: String): Boolean {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formato.isLenient = false
            formato.parse(fecha) != null
        } catch (e: Exception) {
            false
        }
    }

    private fun construir(estado: String): Registro {
        val s = _uiState.value
        val reg = registroExistente
        return Registro(
            id = reg?.id ?: 0,
            bitacoraId = bitacoraId,
            estudiante = estudiante,
            fechaClase = s.fecha,
            contenido = s.contenido,
            actividad = s.actividad,
            aprendizaje = s.aprendizaje,
            dudas = s.dudas,
            fotografias = s.fotografias,
            archivos = s.archivos,
            enlaces = s.enlaces,
            estado = estado,
            comentario = reg?.comentario ?: "",
            fechaCreacion = reg?.fechaCreacion ?: fechaHoy(),
            fechaEnvio = if (estado == "pendiente") {
                fechaHoy()
            } else {
                reg?.fechaEnvio ?: ""
            },
            fechaRetroalimentacion = reg?.fechaRetroalimentacion ?: "",
            docenteRevisor = reg?.docenteRevisor ?: ""
        )
    }

    private fun limpiar() {
        _uiState.update {
            EstudianteRegistroUiState()
        }
    }

    fun guardarBorrador(onGuardar: ((Registro) -> Unit)? = null) {
        val s = _uiState.value
        if (!fechaValida(s.fecha)) {
            _uiState.update {
                it.copy(
                    mensaje = "Ingrese una fecha válida con formato dd/MM/aaaa (ejemplo: 16/10/2026)",
                    mensajeEsError = true
                )
            }
        } else {
            val registro = construir("borrador")
            viewModelScope.launch {
                if (registro.id == 0) {
                    repository?.guardarRegistro(registro)
                } else {
                    repository?.actualizarRegistro(registro)
                }
                onGuardar?.invoke(registro)
            }
            limpiar()
            _uiState.update {
                it.copy(
                    mensaje = "Borrador guardado. Puedes enviarlo desde tu historial.",
                    mensajeEsError = false
                )
            }
        }
    }

    fun enviarAlDocente(onGuardar: ((Registro) -> Unit)? = null) {
        val s = _uiState.value
        if (!fechaValida(s.fecha) ||
            s.contenido.isBlank() ||
            s.actividad.isBlank() ||
            s.aprendizaje.isBlank()
        ) {
            _uiState.update {
                it.copy(
                    mensaje = "Ingrese una fecha válida (dd/MM/aaaa) y complete contenido, actividad y aprendizaje",
                    mensajeEsError = true
                )
            }
        } else {
            val registro = construir("pendiente")
            viewModelScope.launch {
                if (registro.id == 0) {
                    repository?.guardarRegistro(registro)
                } else {
                    repository?.actualizarRegistro(registro)
                }
                onGuardar?.invoke(registro)
            }
            limpiar()
            _uiState.update {
                it.copy(
                    mensaje = "Registro enviado. Estado: pendiente de revisión.",
                    mensajeEsError = false
                )
            }
        }
    }
}

class EstudianteRegistroViewModelFactory(
    private val repository: BitacoraRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EstudianteRegistroViewModel::class.java)) {
            return EstudianteRegistroViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
