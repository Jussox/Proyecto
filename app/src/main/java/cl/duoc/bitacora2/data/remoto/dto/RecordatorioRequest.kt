package cl.duoc.bitacora2.data.remoto.dto

data class RecordatorioRequest(
    val docente: String,
    val tipo: String,
    val fechaProgramada: String
)
