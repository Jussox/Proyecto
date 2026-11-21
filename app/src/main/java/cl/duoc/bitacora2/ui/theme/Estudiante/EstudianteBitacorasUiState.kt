package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto

data class EstudianteBitacorasUiState(
    val cargando: Boolean = false,
    val error: String? = null,
    val errorAviso: String? = null,   // Error cuando ya hay lista (banner, no pantalla completa)
    val bitacoras: List<BitacoraDto> = emptyList()
)
