package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto

data class DocenteAportesUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val errorAviso: String? = null,
    val registros: List<RegistroDto> = emptyList(),
    val bitacorasMap: Map<Long, BitacoraDto> = emptyMap(),
    val estudiantesMap: Map<String, String> = emptyMap(),
    val filtroEstudiante: String = "",
    val filtroFecha: String = "",
    val filtroEstado: String = "todos"
)
