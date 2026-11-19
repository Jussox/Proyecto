package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.lifecycle.ViewModel
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.data.fechaHoy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Locale

class EstudianteRegistroViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EstudianteRegistroUiState())
    val uiState: StateFlow<EstudianteRegistroUiState> = _uiState.asStateFlow()

    private var bitacoraId: Int = 0
    private var mes: String = ""
    private var anio: String = ""
    private var estudiante: String = ""
    private var registroExistente: Registro? = null

    fun inicializar(
        bitacoraId: Int,
        mes: String = "",
        anio: String = "",
        estudiante: String,
        registro: Registro?
    ) {
        this.bitacoraId = bitacoraId
        this.mes = mes
        this.anio = anio
        this.estudiante = estudiante
        this.registroExistente = registro

        if (registro != null) {
            // Si el registro ya existía, separamos las dudas guardadas por línea
            val dudasSeparadas = if (registro.dudas.isNotBlank()) {
                registro.dudas.lines().map { it.trim().removePrefix("•").trim() }.filter { it.isNotBlank() }
            } else {
                emptyList()
            }

            _uiState.update {
                it.copy(
                    fecha = registro.fechaClase,
                    contenido = registro.contenido,
                    actividad = registro.actividad,
                    aprendizaje = registro.aprendizaje,
                    dudas = "",
                    listaDudas = dudasSeparadas,
                    fotografias = registro.fotografias,
                    archivos = registro.archivos,
                    enlaces = registro.enlaces,
                    mensaje = "",
                    mensajeEsError = false
                )
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
                    dudas = "" // Limpia el campo para redactar la siguiente
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

        // Une las dudas agregadas a la lista (y si el alumno dejó algo escrito en el campo, lo incluye)
        val dudasFinales = if (s.dudas.isNotBlank()) {
            s.listaDudas + s.dudas.trim()
        } else {
            s.listaDudas
        }
        val dudasTexto = dudasFinales.joinToString("\n")

        return Registro(
            id = reg?.id ?: 0,
            bitacoraId = bitacoraId,
            estudiante = estudiante,
            fechaClase = s.fecha,
            contenido = s.contenido,
            actividad = s.actividad,
            aprendizaje = s.aprendizaje,
            dudas = dudasTexto,
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
        _uiState.update { EstudianteRegistroUiState() }
    }

    fun guardarBorrador(onGuardar: (Registro) -> Unit) {
        val s = _uiState.value
        if (!fechaValida(s.fecha)) {
            _uiState.update {
                it.copy(
                    mensaje = "Ingrese una fecha válida con formato dd/MM/aaaa (ejemplo: 16/10/2026)",
                    mensajeEsError = true
                )
            }
            return
        }

        val registro = construir("borrador")
        onGuardar(registro)
        _uiState.update {
            it.copy(
                mensaje = "Borrador guardado con éxito.",
                mensajeEsError = false
            )
        }
    }

    fun enviarAlDocente(onGuardar: (Registro) -> Unit) {
        val s = _uiState.value
        if (!fechaValida(s.fecha) || s.contenido.isBlank() || s.actividad.isBlank() || s.aprendizaje.isBlank()) {
            _uiState.update {
                it.copy(
                    mensaje = "Ingrese una fecha válida (dd/MM/aaaa) y complete contenido, actividad y aprendizaje",
                    mensajeEsError = true
                )
            }
            return
        }

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