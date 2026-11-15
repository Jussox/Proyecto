package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.Asignacion

data class DocenteCrearBitacoraUiState(
    val seleccionada: Asignacion? = null,
    val mes: String = "",
    val anio: String = "",
    val titulo: String = "",
    val proposito: String = "",
    val instrucciones: String = "",
    val preguntas: String = "",
    val mensaje: String = "",
    val mensajeEsError: Boolean = false
)
