package cl.duoc.bitacora2.data.remoto.dto

data class LoginRequest(
    val usuario: String,
    val password: String,
    val perfil: String
)
