package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto

data class DocenteBitacorasUiState(
    val cargando: Boolean = false,
    val bitacoras: List<BitacoraDto> = emptyList(),
    val error: String? = null,
    val accionEnProgresoId: Long? = null,
    val errorAccion: String? = null
)
