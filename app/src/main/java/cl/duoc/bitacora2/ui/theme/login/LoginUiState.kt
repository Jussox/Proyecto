package cl.duoc.bitacora2.ui.theme.login

data class LoginUiState(
    val perfil: String = "estudiante",
    val usuario: String = "",
    val password: String = "",
    val mensajeError: String = ""
)