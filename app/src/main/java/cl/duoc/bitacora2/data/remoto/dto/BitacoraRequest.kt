package cl.duoc.bitacora2.data.remoto.dto

data class BitacoraRequest(
    val docente: String,
    val curso: String,
    val asignatura: String,
    val mes: String,
    val anio: String,
    val titulo: String,
    val proposito: String,
    val instrucciones: String,
    val preguntas: String
)
