package cl.duoc.bitacora2.util

import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

fun mensajeDeError(e: Throwable): String {
    return when (e) {
        is IOException -> "No hay conexión con el servidor"
        is HttpException -> {
            val errorBody = try {
                e.response()?.errorBody()?.string()
            } catch (_: Exception) {
                null
            }

            val mensajeBackend = errorBody?.let { body ->
                try {
                    val json = JSONObject(body)
                    if (json.has("message") && !json.getString("message").isNullOrBlank()) {
                        json.getString("message")
                    } else null
                } catch (_: Exception) {
                    if (body.isNotBlank() && body.length < 200 && !body.startsWith("<")) body.trim() else null
                }
            }

            when (e.code()) {
                400 -> mensajeBackend ?: "Datos inválidos o incompletos"
                401 -> "Credenciales incorrectas"
                403 -> "No tienes permisos para realizar esta acción"
                404 -> "El recurso solicitado no fue encontrado"
                409 -> mensajeBackend ?: "Conflicto con los datos existentes"
                500 -> "Error interno del servidor"
                else -> mensajeBackend ?: "Error en el servidor (${e.code()})"
            }
        }
        else -> e.message ?: "Ocurrió un error inesperado"
    }
}
