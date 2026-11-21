package cl.duoc.bitacora2.data.remoto.dto

data class RegistroRequest(
    val bitacoraId: Long? = null,
    val estudiante: String? = null,
    val fechaClase: String = "",
    val contenido: String = "",
    val actividad: String = "",
    val aprendizaje: String = "",
    val dudas: String? = "",
    val fotografias: String? = "",
    val archivos: String? = "",
    val enlaces: String? = ""
)
