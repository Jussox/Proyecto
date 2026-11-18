package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.lifecycle.ViewModel
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.data.fechaHoy
import cl.duoc.bitacora2.util.MAX_LARGO_TEXTO_LARGO
import cl.duoc.bitacora2.util.validarCamposRegistro
import cl.duoc.bitacora2.util.validarEnlace
import cl.duoc.bitacora2.util.validarFechaClase
import cl.duoc.bitacora2.util.validarLargo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class EstudianteRegistroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteRegistroUiState())
    val uiState: StateFlow<EstudianteRegistroUiState> = _uiState.asStateFlow()

    private var bitacoraId: Int = 0
    private var bitacoraMes: String = ""
    private var bitacoraAnio: String = ""
    private var estudiante: String = ""
    private var registroExistente: Registro? = null
    private var yaCargado: Boolean = false

    fun inicializar(
        bitacoraId: Int,
        mes: String,
        anio: String,
        estudiante: String,
        registro: Registro?
    ) {
        if (bitacoraId != 0) {
            this.bitacoraId = bitacoraId
        }
        if (mes.isNotBlank()) {
            this.bitacoraMes = mes
        }
        if (anio.isNotBlank()) {
            this.bitacoraAnio = anio
        }
        if (estudiante.isNotBlank()) {
            this.estudiante = estudiante
        }
        this.registroExistente = registro

        if (registro != null) {
            if (!yaCargado) {
                yaCargado = true
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
        } else {
            yaCargado = false
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

    fun mostrarError(texto: String) {
        _uiState.update {
            it.copy(
                mensaje = texto,
                mensajeEsError = true
            )
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
            fechaEnvio = if (estado == "pendiente") fechaHoy() else reg?.fechaEnvio ?: "",
            fechaRetroalimentacion = reg?.fechaRetroalimentacion ?: "",
            docenteRevisor = reg?.docenteRevisor ?: ""
        )
    }

    private fun limpiar() {
        yaCargado = false
        _uiState.update { EstudianteRegistroUiState() }
    }

    fun guardarBorrador(onGuardar: (Registro) -> Unit) {
        val s = _uiState.value
        if (!validarFechaClase(s.fecha, bitacoraMes, bitacoraAnio)) {
            mostrarError("La fecha debe tener formato dd/mm/aaaa, no ser futura y estar dentro de $bitacoraMes $bitacoraAnio (el mes de la bitácora)")
        } else if (!validarLargo(s.contenido, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("El contenido no puede superar los 2000 caracteres")
        } else if (!validarLargo(s.actividad, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("La actividad no puede superar los 2000 caracteres")
        } else if (!validarLargo(s.aprendizaje, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("El aprendizaje no puede superar los 2000 caracteres")
        } else if (!validarLargo(s.dudas, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("Las dudas no pueden superar los 2000 caracteres")
        } else if (!validarLargo(s.enlaces, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("Los enlaces no pueden superar los 2000 caracteres")
        } else if (!validarEnlace(s.enlaces)) {
            mostrarError("El enlace debe comenzar con http:// o https://")
        } else {
            val registro = construir("borrador")
            onGuardar(registro)
            limpiar()
            _uiState.update {
                it.copy(
                    mensaje = "Borrador guardado. Puedes enviarlo desde tu historial.",
                    mensajeEsError = false
                )
            }
        }
    }

    fun enviarAlDocente(onGuardar: (Registro) -> Unit) {
        val s = _uiState.value
        if (!validarFechaClase(s.fecha, bitacoraMes, bitacoraAnio)) {
            mostrarError("La fecha debe tener formato dd/mm/aaaa, no ser futura y estar dentro de $bitacoraMes $bitacoraAnio (el mes de la bitácora)")
        } else if (!validarCamposRegistro(s.contenido, s.actividad, s.aprendizaje)) {
            mostrarError("Complete contenido, actividad y aprendizaje")
        } else if (!validarLargo(s.contenido, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("El contenido no puede superar los 2000 caracteres")
        } else if (!validarLargo(s.actividad, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("La actividad no puede superar los 2000 caracteres")
        } else if (!validarLargo(s.aprendizaje, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("El aprendizaje no puede superar los 2000 caracteres")
        } else if (!validarLargo(s.dudas, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("Las dudas no pueden superar los 2000 caracteres")
        } else if (!validarLargo(s.enlaces, MAX_LARGO_TEXTO_LARGO)) {
            mostrarError("Los enlaces no pueden superar los 2000 caracteres")
        } else if (!validarEnlace(s.enlaces)) {
            mostrarError("El enlace debe comenzar con http:// o https://")
        } else {
            val registro = construir("pendiente")
            onGuardar(registro)
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
