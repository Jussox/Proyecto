package cl.duoc.bitacora2.data.remoto.dto

data class ParticipacionResponse(
    val bitacoraId: Long? = null,
    val curso: String? = "",
    val asignatura: String? = "",
    val totalEstudiantes: Int = 0,
    val estudiantesParticiparon: Int = 0,
    val estudiantesSinParticipacion: Int = 0,
    val sinParticipacion: List<String>? = emptyList(),
    val totalAportes: Int = 0,
    val aportesRevisados: Int = 0,
    val aportesPendientes: Int = 0
)
