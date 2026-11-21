package cl.duoc.bitacora2.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object Sesion {
    var usuario by mutableStateOf("")
    var nombre by mutableStateOf("")
    var perfil by mutableStateOf("")
    var curso by mutableStateOf("")

    fun iniciar(usuario: String, nombre: String, perfil: String, curso: String) {
        this.usuario = usuario
        this.nombre = nombre
        this.perfil = perfil
        this.curso = curso
    }

    fun cerrar() {
        this.usuario = ""
        this.nombre = ""
        this.perfil = ""
        this.curso = ""
    }

    val estaActiva: Boolean
        get() = usuario.isNotBlank()
}
