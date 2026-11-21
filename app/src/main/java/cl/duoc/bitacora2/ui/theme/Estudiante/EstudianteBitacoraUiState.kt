package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto

data class EstudianteBitacoraUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val bitacora: BitacoraDto? = null
)
