package cl.duoc.bitacora2.data.remoto.dto

data class InformeMensualResponse(
    val curso: String? = "",
    val asignatura: String? = "",
    val mes: String? = "",
    val anio: String? = "",
    val docente: String? = "",
    val titulo: String? = "",
    val proposito: String? = "",
    val fechasRegistros: List<String>? = emptyList(),
    val resumenTemasActividades: List<String>? = emptyList(),
    val participacion: ParticipacionResponse? = null,
    val fechaCierre: String? = "",
    val fechaGeneracion: String? = ""
)
