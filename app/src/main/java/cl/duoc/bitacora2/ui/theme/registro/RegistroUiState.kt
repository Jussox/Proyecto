package cl.duoc.bitacora2.ui.theme.registro

data class RegistroUiState(
    val nombre: String = "",
    val apellido: String = "",
    val correo: String = "",
    val password: String = "",
    val repetirPassword: String = "",
    val aceptaTerminos: Boolean = false,

    val errorNombre: Boolean = false,
    val errorApellido: Boolean = false,
    val errorCorreo: Boolean = false,
    val errorPassword: Boolean = false,
    val errorRepetirPassword: Boolean = false,
    val errorTerminos: Boolean = false
)