package cl.duoc.bitacora2.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object Sesion {
    var usuario by mutableStateOf("")
    var perfil by mutableStateOf("")

    fun iniciar(usuario: String, perfil: String) {
        this.usuario = usuario
        this.perfil = perfil
    }

    fun cerrar() {
        this.usuario = ""
        this.perfil = ""
    }

    val estaActiva: Boolean
        get() = usuario.isNotBlank()
}
