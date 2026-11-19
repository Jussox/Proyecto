package cl.duoc.bitacora2.ui.theme.Estudiante

data class EstudianteRegistroUiState(
    val fecha: String = "",
    val contenido: String = "",
    val actividad: String = "",
    val aprendizaje: String = "",
    val dudas: String = "",                     // Texto de la duda que se está escribiendo
    val listaDudas: List<String> = emptyList(), // Lista de dudas agregadas
    val fotografias: String = "",
    val archivos: String = "",
    val enlaces: String = "",
    val mensaje: String = "",
    val mensajeEsError: Boolean = false
)


