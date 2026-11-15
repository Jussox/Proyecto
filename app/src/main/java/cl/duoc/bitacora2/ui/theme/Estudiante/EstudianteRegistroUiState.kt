package cl.duoc.bitacora2.ui.theme.Estudiante

data class EstudianteRegistroUiState(
    val fecha: String = "",
    val contenido: String = "",
    val actividad: String = "",
    val aprendizaje: String = "",
    val dudas: String = "",
    val fotografias: String = "",
    val archivos: String = "",
    val enlaces: String = "",
    val mensaje: String = "",
    val mensajeEsError: Boolean = false
)
