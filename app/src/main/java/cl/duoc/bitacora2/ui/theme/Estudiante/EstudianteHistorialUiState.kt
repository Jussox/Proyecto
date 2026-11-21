package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto

data class EstudianteHistorialUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val errorAviso: String? = null,
    val registros: List<RegistroDto> = emptyList(),
    val bitacorasMap: Map<Long, BitacoraDto> = emptyMap(),
    val idPendienteEliminar: Long? = null,
    val eliminandoId: Long? = null
)
