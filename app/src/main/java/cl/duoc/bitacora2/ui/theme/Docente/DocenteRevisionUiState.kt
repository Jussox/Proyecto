package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto

data class DocenteRevisionUiState(
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val error: String? = null,
    val errorOperacion: String? = null,
    val mensajeExito: String? = null,
    val registro: RegistroDto? = null,
    val bitacora: BitacoraDto? = null,
    val nombreEstudiante: String = "",
    val comentario: String = "",
    val comentarioOriginal: String = "",
    val errorComentario: String? = null
)
