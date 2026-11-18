package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cl.duoc.bitacora2.data.Asignacion
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.BitacoraRepository
import cl.duoc.bitacora2.data.fechaHoy
import cl.duoc.bitacora2.util.MAX_LARGO_TEXTO_LARGO
import cl.duoc.bitacora2.util.MAX_LARGO_TITULO
import cl.duoc.bitacora2.util.validarAnio
import cl.duoc.bitacora2.util.validarFormularioBitacora
import cl.duoc.bitacora2.util.validarLargo
import cl.duoc.bitacora2.util.validarMes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DocenteCrearBitacoraViewModel(
    private val repository: BitacoraRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(DocenteCrearBitacoraUiState())
    val uiState: StateFlow<DocenteCrearBitacoraUiState> = _uiState.asStateFlow()

    fun seleccionarAsignacion(asignacion: Asignacion) {
        _uiState.update { it.copy(seleccionada = asignacion) }
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

    fun guardar(docente: String, publicar: Boolean, onGuardar: ((Bitacora) -> Unit)? = null) {
        val estado = _uiState.value
        val a = estado.seleccionada
        if (!validarFormularioBitacora(
                asignacion = a,
                mes = estado.mes,
                anio = estado.anio,
                titulo = estado.titulo,
                proposito = estado.proposito,
                instrucciones = estado.instrucciones,
                preguntas = estado.preguntas
            )
        ) {
            _uiState.update {
                it.copy(
                    mensaje = "Seleccione curso y asignatura y complete todos los campos",
                    mensajeEsError = true
                )
            }
        } else if (!validarMes(estado.mes)) {
            _uiState.update {
                it.copy(
                    mensaje = "Ingrese un mes válido (por ejemplo: Octubre)",
                    mensajeEsError = true
                )
            }
        } else if (!validarAnio(estado.anio)) {
            _uiState.update {
                it.copy(
                    mensaje = "El año debe tener 4 dígitos y ser el actual o el siguiente",
                    mensajeEsError = true
                )
            }
        } else if (!validarLargo(estado.titulo, MAX_LARGO_TITULO)) {
            _uiState.update {
                it.copy(
                    mensaje = "El título no puede superar los 100 caracteres",
                    mensajeEsError = true
                )
            }
        } else if (!validarLargo(estado.proposito, MAX_LARGO_TEXTO_LARGO)) {
            _uiState.update {
                it.copy(
                    mensaje = "El propósito no puede superar los 2000 caracteres",
                    mensajeEsError = true
                )
            }
        } else if (!validarLargo(estado.instrucciones, MAX_LARGO_TEXTO_LARGO)) {
            _uiState.update {
                it.copy(
                    mensaje = "Las instrucciones no pueden superar los 2000 caracteres",
                    mensajeEsError = true
                )
            }
        } else if (!validarLargo(estado.preguntas, MAX_LARGO_TEXTO_LARGO)) {
            _uiState.update {
                it.copy(
                    mensaje = "Las preguntas no pueden superar los 2000 caracteres",
                    mensajeEsError = true
                )
            }
        } else {
            val nuevaBitacora = Bitacora(
                docente = docente,
                curso = a!!.curso,
                asignatura = a.asignatura,
                mes = estado.mes,
                anio = estado.anio,
                titulo = estado.titulo,
                proposito = estado.proposito,
                instrucciones = estado.instrucciones,
                preguntas = estado.preguntas,
                estado = if (publicar) "publicada" else "guardada",
                fechaPublicacion = if (publicar) fechaHoy() else ""
            )

            viewModelScope.launch {
                repository?.guardarBitacora(nuevaBitacora)
                onGuardar?.invoke(nuevaBitacora)
            }

            _uiState.update {
                DocenteCrearBitacoraUiState(
                    mensaje = if (publicar) "Bitácora guardada y publicada."
                    else "Bitácora guardada. Puedes publicarla desde «Mis bitácoras mensuales».",
                    mensajeEsError = false
                )
            }
        }
    }
}

class DocenteCrearBitacoraViewModelFactory(
    private val repository: BitacoraRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DocenteCrearBitacoraViewModel::class.java)) {
            return DocenteCrearBitacoraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
