package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto

data class EstudianteRegistroUiState(
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val error: String? = null,
    val mensaje: String = "",
    val mensajeEsError: Boolean = false,
    val bitacora: BitacoraDto? = null,
    val esModoEdicion: Boolean = false,
    val idRegistro: Long? = null,
    val estadoInvalido: Boolean = false,
    val mensajeEstadoInvalido: String = "",
    val fecha: String = "",
    val contenido: String = "",
    val actividad: String = "",
    val aprendizaje: String = "",
    val dudas: String = "",
    val listaDudas: List<String> = emptyList(),
    val fotografias: String = "",
    val archivos: String = "",
    val enlaces: String = ""
)
