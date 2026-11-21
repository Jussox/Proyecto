package cl.duoc.bitacora2.data.remoto.dto

data class BitacoraEdicionRequest(
    val titulo: String,
    val proposito: String,
    val instrucciones: String,
    val preguntas: String
)
