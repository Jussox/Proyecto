package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.externa.RecursoEducativoDto

data class RecursosEducativosUiState(
    val consulta: String = "",
    val cargando: Boolean = false,
    val recursos: List<RecursoEducativoDto> = emptyList(),
    val error: String? = null,
    val busquedaRealizada: Boolean = false
)
