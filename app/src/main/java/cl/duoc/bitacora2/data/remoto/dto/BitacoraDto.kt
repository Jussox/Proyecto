package cl.duoc.bitacora2.data.remoto.dto

data class BitacoraDto(
    val id: Long? = null,
    val docente: String? = "",
    val curso: String? = "",
    val asignatura: String? = "",
    val mes: String? = "",
    val anio: String? = "",
    val titulo: String? = "",
    val proposito: String? = "",
    val instrucciones: String? = "",
    val preguntas: String? = "",
    val estado: String? = "",
    val fechaPublicacion: String? = "",
    val fechaCierre: String? = ""
)
