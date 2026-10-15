package cl.duoc.bitacora2.ui.theme.login

fun validarUsuario(usuario: String): Boolean {
    return usuario.isNotBlank()
}

fun validarPasswordLogin(password: String): Boolean {
    return password.isNotBlank()
}

fun credencialesCorrectas(perfil: String, usuario: String, password: String): Boolean {
    if (perfil == "estudiante") {
        return usuario == "estudiante1" && password == "clave1234"
    }
    if (perfil == "docente") {
        return usuario == "docente1" && password == "clave1234"
    }
    return false
}