package cl.duoc.bitacora2.data.remoto.dto

data class RecordatorioDto(
    val id: Long? = null,
    val docente: String? = "",
    val tipo: String? = "",
    val fechaProgramada: String? = "",
    val estado: String? = ""
)
