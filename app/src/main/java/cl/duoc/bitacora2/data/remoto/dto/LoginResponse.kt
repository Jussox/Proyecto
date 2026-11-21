package cl.duoc.bitacora2.data.remoto.dto

data class LoginResponse(
    val usuario: String? = null,
    val nombre: String? = null,
    val perfil: String? = null,
    val curso: String? = null
)
