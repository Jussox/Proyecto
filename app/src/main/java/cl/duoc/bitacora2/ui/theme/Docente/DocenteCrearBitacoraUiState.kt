package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.dto.AsignacionDto

data class DocenteCrearBitacoraUiState(
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val error: String? = null,
    val esModoEdicion: Boolean = false,
    val estadoInvalidoParaEdicion: Boolean = false,
    val idBitacoraCreada: Long? = null,
    val asignaciones: List<AsignacionDto> = emptyList(),
    val asignacionSeleccionada: AsignacionDto? = null,
    val cursoFijo: String = "",
    val asignaturaFija: String = "",
    val mes: String = "",
    val anio: String = "",
    val titulo: String = "",
    val proposito: String = "",
    val instrucciones: String = "",
    val preguntas: String = "",
    val mensaje: String = "",
    val mensajeEsError: Boolean = false
)
