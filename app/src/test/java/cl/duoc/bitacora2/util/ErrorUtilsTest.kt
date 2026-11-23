package cl.duoc.bitacora2.util

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ErrorUtilsTest {

    // Helper para construir HttpException con un cuerpo de error.
    private fun crearHttpException(codigo: Int, cuerpo: String? = null): HttpException {
        val contenido = cuerpo ?: ""
        val response = Response.error<Any>(
            codigo,
            contenido.toResponseBody("application/json".toMediaType())
        )
        return HttpException(response)
    }

    // ============================================================================
    // 1. Errores de conexión
    // ============================================================================

    @Test
    fun mensajeDeErrorIOExceptionRetornaMensajeDeConexion() {
        // ARRANGE
        val error = IOException("Sin conexión")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("No hay conexión con el servidor", resultado)
    }

    // ============================================================================
    // 2. Errores HTTP según código
    // ============================================================================

    @Test
    fun mensajeDeErrorHttp400SinMensajeBackendRetornaMensajePredeterminado() {
        // ARRANGE
        val error = crearHttpException(400)

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Datos inválidos o incompletos", resultado)
    }

    @Test
    fun mensajeDeErrorHttp400ConMensajeJsonRetornaMensajeBackend() {
        // ARRANGE
        val error = crearHttpException(400, """{"message":"Faltan datos"}""")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Faltan datos", resultado)
    }

    @Test
    fun mensajeDeErrorHttp401RetornaCredencialesIncorrectas() {
        // ARRANGE
        val error = crearHttpException(401, """{"message":"Mensaje del backend"}""")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Credenciales incorrectas", resultado)
    }

    @Test
    fun mensajeDeErrorHttp403RetornaSinPermisos() {
        // ARRANGE
        val error = crearHttpException(403)

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("No tienes permisos para realizar esta acción", resultado)
    }

    @Test
    fun mensajeDeErrorHttp404RetornaRecursoNoEncontrado() {
        // ARRANGE
        val error = crearHttpException(404)

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("El recurso solicitado no fue encontrado", resultado)
    }

    @Test
    fun mensajeDeErrorHttp409ConMensajeBackendRetornaMensajeBackend() {
        // ARRANGE
        val error = crearHttpException(409, """{"message":"El registro ya existe"}""")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("El registro ya existe", resultado)
    }

    @Test
    fun mensajeDeErrorHttp409SinMensajeBackendRetornaMensajePredeterminado() {
        // ARRANGE
        val error = crearHttpException(409)

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Conflicto con los datos existentes", resultado)
    }

    @Test
    fun mensajeDeErrorHttp500RetornaErrorInterno() {
        // ARRANGE
        val error = crearHttpException(500, """{"message":"Detalle interno"}""")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Error interno del servidor", resultado)
    }

    @Test
    fun mensajeDeErrorHttpOtroCodigoSinMensajeRetornaCodigoEnMensaje() {
        // ARRANGE
        val error = crearHttpException(503)

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Error en el servidor (503)", resultado)
    }

    @Test
    fun mensajeDeErrorHttpOtroCodigoConMensajeJsonRetornaMensajeBackend() {
        // ARRANGE
        val error = crearHttpException(422, """{"message":"No se puede procesar"}""")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("No se puede procesar", resultado)
    }

    // ============================================================================
    // 3. Interpretación del cuerpo de error
    // ============================================================================

    @Test
    fun mensajeDeErrorJsonSinPropiedadMessageUsaMensajePredeterminado() {
        // ARRANGE
        val error = crearHttpException(400, """{"error":"Solicitud incorrecta"}""")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Datos inválidos o incompletos", resultado)
    }

    @Test
    fun mensajeDeErrorJsonConMessageVacioUsaMensajePredeterminado() {
        // ARRANGE
        val error = crearHttpException(400, """{"message":"   "}""")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Datos inválidos o incompletos", resultado)
    }

    @Test
    fun mensajeDeErrorJsonMalformadoRetornaTextoLimpio() {
        // ARRANGE
        val error = crearHttpException(400, "respuesta no válida")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("respuesta no válida", resultado)
    }

    @Test
    fun mensajeDeErrorTextoPlanoCortoRetornaTextoLimpio() {
        // ARRANGE
        val error = crearHttpException(400, "  Usuario no encontrado  ")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Usuario no encontrado", resultado)
    }

    @Test
    fun mensajeDeErrorCuerpoHtmlUsaMensajePredeterminado() {
        // ARRANGE
        val error = crearHttpException(400, "<html>Error</html>")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Datos inválidos o incompletos", resultado)
    }

    @Test
    fun mensajeDeErrorTextoPlanoDe200CaracteresUsaMensajePredeterminado() {
        // ARRANGE
        val cuerpoLargo = "a".repeat(200)
        val error = crearHttpException(400, cuerpoLargo)

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Datos inválidos o incompletos", resultado)
    }

    @Test
    fun mensajeDeErrorCuerpoConEspaciosUsaMensajePredeterminado() {
        // ARRANGE
        val error = crearHttpException(400, "   ")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Datos inválidos o incompletos", resultado)
    }

    // ============================================================================
    // 4. Errores generales
    // ============================================================================

    @Test
    fun mensajeDeErrorExcepcionConMensajeRetornaMensajeDeExcepcion() {
        // ARRANGE
        val error = IllegalStateException("Ocurrió un problema")

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Ocurrió un problema", resultado)
    }

    @Test
    fun mensajeDeErrorExcepcionSinMensajeRetornaMensajePredeterminado() {
        // ARRANGE
        val error = object : Throwable() {
            override val message: String?
                get() = null
        }

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Ocurrió un error inesperado", resultado)
    }

    // ============================================================================
// 5. Casos adicionales de cobertura
// ============================================================================

    @Test
    fun mensajeDeErrorHttpSinErrorBodyRetornaMensajePredeterminado() {
        // ARRANGE
        val response = Response.success<Any>(Any())
        val error = HttpException(response)

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Error en el servidor (200)", resultado)
    }

    @Test
    fun mensajeDeErrorJsonConMessageNullRetornaCuerpoComoTextoPlano() {
        // ARRANGE
        val cuerpo = """{"message":null}"""
        val error = crearHttpException(400, cuerpo)

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals(cuerpo, resultado)
    }

    @Test
    fun mensajeDeErrorCuandoFallaLecturaDelErrorBodyRetornaMensajePredeterminado() {
        // ARRANGE
        val body = object : okhttp3.ResponseBody() {
            override fun contentType(): okhttp3.MediaType? = null

            override fun contentLength(): Long = -1

            override fun source(): okio.BufferedSource {
                throw IOException("Error al leer el cuerpo")
            }
        }

        val response = Response.error<Any>(400, body)
        val error = HttpException(response)

        // ACT
        val resultado = mensajeDeError(error)

        // ASSERT
        assertEquals("Datos inválidos o incompletos", resultado)
    }
}
