package cl.duoc.bitacora2.util

import cl.duoc.bitacora2.data.DatosPrueba

fun validarUsuario(usuario: String): Boolean {
    return usuario.isNotBlank()
}

fun validarPasswordLogin(password: String): Boolean {
    return password.isNotBlank()
}

fun credencialesCorrectas(perfil: String, usuario: String, password: String): Boolean {
    return DatosPrueba.buscarUsuario(usuario, password, perfil) != null
}