package cl.duoc.bitacora2.data.remoto.dto

data class RegistroDto(
    val id: Long? = null,
    val bitacoraId: Long? = null,
    val estudiante: String? = "",
    val fechaClase: String? = "",
    val contenido: String? = "",
    val actividad: String? = "",
    val aprendizaje: String? = "",
    val dudas: String? = "",
    val fotografias: String? = "",
    val archivos: String? = "",
    val enlaces: String? = "",
    val estado: String? = "",
    val comentario: String? = "",
    val fechaCreacion: String? = "",
    val fechaEnvio: String? = "",
    val fechaRetroalimentacion: String? = "",
    val docenteRevisor: String? = ""
)
