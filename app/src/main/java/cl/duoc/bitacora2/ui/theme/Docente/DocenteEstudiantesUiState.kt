package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.dto.EstudianteDto

// Estado de la pantalla de estudiantes por curso del docente
data class DocenteEstudiantesUiState(
    val cargando: Boolean = false,
    val estudiantes: List<EstudianteDto> = emptyList(),
    val error: String? = null
)
