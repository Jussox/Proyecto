package cl.duoc.bitacora2.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesComunTest {

    // ============================================================================
    // 1. validarTextoObligatorio
    // ============================================================================

    @Test
    fun validarTextoObligatorioTextoValido() {
        // ARRANGE
        val texto = "Texto con contenido"

        // ACT
        val resultado = validarTextoObligatorio(texto)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarTextoObligatorioTextoVacio() {
        // ARRANGE
        val texto = ""

        // ACT
        val resultado = validarTextoObligatorio(texto)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarTextoObligatorioSoloEspaciosEnBlanco() {
        // ARRANGE
        val texto = "     "

        // ACT
        val resultado = validarTextoObligatorio(texto)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarTextoObligatorioTextoConEspaciosAlrededor() {
        // ARRANGE
        val texto = "   contenido válido   "

        // ACT
        val resultado = validarTextoObligatorio(texto)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarTextoObligatorioSaltosDeLineaYTabulaciones() {
        // ARRANGE
        val texto = "\n\t  \r\n"

        // ACT
        val resultado = validarTextoObligatorio(texto)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 2. validarLargo
    // ============================================================================

    @Test
    fun validarLargoTextoMenorAlLimite() {
        // ARRANGE
        val texto = "Hola mundo"
        val maximo = 20

        // ACT
        val resultado = validarLargo(texto, maximo)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarLargoTextoEnElLimiteExacto() {
        // ARRANGE
        val texto = "12345"
        val maximo = 5

        // ACT
        val resultado = validarLargo(texto, maximo)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarLargoTextoUnCaracterSobreElLimite() {
        // ARRANGE
        val texto = "123456"
        val maximo = 5

        // ACT
        val resultado = validarLargo(texto, maximo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarLargoTextoVacioConLimiteMayorACero() {
        // ARRANGE
        val texto = ""
        val maximo = 10

        // ACT
        val resultado = validarLargo(texto, maximo)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarLargoLimiteCeroConTextoVacio() {
        // ARRANGE
        val texto = ""
        val maximo = 0

        // ACT
        val resultado = validarLargo(texto, maximo)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarLargoLimiteCeroConTextoNoVacio() {
        // ARRANGE
        val texto = "a"
        val maximo = 0

        // ACT
        val resultado = validarLargo(texto, maximo)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 3. validarUsuario
    // ============================================================================

    @Test
    fun validarUsuarioUsuarioValido() {
        // ARRANGE
        val usuario = "estudiante1"

        // ACT
        val resultado = validarUsuario(usuario)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarUsuarioUsuarioVacio() {
        // ARRANGE
        val usuario = ""

        // ACT
        val resultado = validarUsuario(usuario)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarUsuarioUsuarioSoloEspacios() {
        // ARRANGE
        val usuario = "    "

        // ACT
        val resultado = validarUsuario(usuario)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 4. validarPasswordLogin
    // ============================================================================

    @Test
    fun validarPasswordLoginPasswordValido() {
        // ARRANGE
        val password = "claveSegura123"

        // ACT
        val resultado = validarPasswordLogin(password)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarPasswordLoginPasswordVacio() {
        // ARRANGE
        val password = ""

        // ACT
        val resultado = validarPasswordLogin(password)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarPasswordLoginPasswordSoloEspacios() {
        // ARRANGE
        val password = "   "

        // ACT
        val resultado = validarPasswordLogin(password)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 5. validarCamposLogin
    // ============================================================================

    @Test
    fun validarCamposLoginCamposValidos() {
        // ARRANGE
        val usuario = "docente1"
        val password = "password123"

        // ACT
        val resultado = validarCamposLogin(usuario, password)

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarCamposLoginUsuarioVacio() {
        // ARRANGE
        val usuario = ""
        val password = "password123"

        // ACT
        val resultado = validarCamposLogin(usuario, password)

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", resultado)
    }

    @Test
    fun validarCamposLoginPasswordVacio() {
        // ARRANGE
        val usuario = "docente1"
        val password = ""

        // ACT
        val resultado = validarCamposLogin(usuario, password)

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", resultado)
    }

    @Test
    fun validarCamposLoginAmbosCamposVacios() {
        // ARRANGE
        val usuario = ""
        val password = ""

        // ACT
        val resultado = validarCamposLogin(usuario, password)

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", resultado)
    }

    @Test
    fun validarCamposLoginUsuarioConEspaciosYPasswordValido() {
        // ARRANGE
        val usuario = "   "
        val password = "password123"

        // ACT
        val resultado = validarCamposLogin(usuario, password)

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", resultado)
    }

    // ============================================================================
    // 6. credencialesCorrectas
    // ============================================================================

    @Test
    fun credencialesCorrectasEstudianteExistenteConClaveCorrecta() {
        // ARRANGE
        val perfil = "estudiante"
        val usuario = "estudiante1"
        val password = "clave1234"

        // ACT
        val resultado = credencialesCorrectas(perfil, usuario, password)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun credencialesCorrectasDocenteExistenteConClaveCorrecta() {
        // ARRANGE
        val perfil = "docente"
        val usuario = "docente1"
        val password = "clave1234"

        // ACT
        val resultado = credencialesCorrectas(perfil, usuario, password)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun credencialesCorrectasClaveIncorrecta() {
        // ARRANGE
        val perfil = "estudiante"
        val usuario = "estudiante1"
        val password = "claveErronea"

        // ACT
        val resultado = credencialesCorrectas(perfil, usuario, password)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun credencialesCorrectasUsuarioNoExiste() {
        // ARRANGE
        val perfil = "docente"
        val usuario = "usuarioInexistente"
        val password = "clave1234"

        // ACT
        val resultado = credencialesCorrectas(perfil, usuario, password)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun credencialesCorrectasPerfilNoCoincideConElUsuario() {
        // ARRANGE
        val perfil = "docente"
        val usuario = "estudiante1"
        val password = "clave1234"

        // ACT
        val resultado = credencialesCorrectas(perfil, usuario, password)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun credencialesCorrectasCredencialesVacias() {
        // ARRANGE
        val perfil = ""
        val usuario = ""
        val password = ""

        // ACT
        val resultado = credencialesCorrectas(perfil, usuario, password)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 7. validarArchivo
    // ============================================================================

    @Test
    fun validarArchivoMimeJpegValidoYTamanoEnRango() {
        // ARRANGE
        val tipoMime = "image/jpeg"
        val tamanoBytes = 1024L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarArchivoMimeJpgValidoYTamanoEnRango() {
        // ARRANGE
        val tipoMime = "image/jpg"
        val tamanoBytes = 2048L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarArchivoMimePngValidoYTamanoEnRango() {
        // ARRANGE
        val tipoMime = "image/png"
        val tamanoBytes = 500000L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarArchivoMimeConMayusculas() {
        // ARRANGE
        val tipoMime = "IMAGE/PNG"
        val tamanoBytes = 1024L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarArchivoMimeConEspaciosAlrededor() {
        // ARRANGE
        val tipoMime = "  image/jpeg  "
        val tamanoBytes = 1024L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarArchivoMimeNoPermitidoPdf() {
        // ARRANGE
        val tipoMime = "application/pdf"
        val tamanoBytes = 1024L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarArchivoMimeNull() {
        // ARRANGE
        val tipoMime: String? = null
        val tamanoBytes = 1024L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarArchivoTamanoCeroBytes() {
        // ARRANGE
        val tipoMime = "image/jpeg"
        val tamanoBytes = 0L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarArchivoTamanoNegativo() {
        // ARRANGE
        val tipoMime = "image/png"
        val tamanoBytes = -1L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarArchivoTamanoEnElLimiteExactoDeCincoMegas() {
        // ARRANGE
        val tipoMime = "image/jpeg"
        val tamanoBytes = 5L * 1024L * 1024L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarArchivoTamanoUnByteSobreCincoMegas() {
        // ARRANGE
        val tipoMime = "image/jpeg"
        val tamanoBytes = (5L * 1024L * 1024L) + 1L

        // ACT
        val resultado = validarArchivo(tipoMime, tamanoBytes)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 8. validarArchivoAdjunto
    // ============================================================================

    @Test
    fun validarArchivoAdjuntoArchivoValido() {
        // ARRANGE
        val tipoMime = "image/png"
        val tamanoBytes = 1024L

        // ACT
        val resultado = validarArchivoAdjunto(tipoMime, tamanoBytes)

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarArchivoAdjuntoTipoMimeInvalido() {
        // ARRANGE
        val tipoMime = "text/plain"
        val tamanoBytes = 1024L

        // ACT
        val resultado = validarArchivoAdjunto(tipoMime, tamanoBytes)

        // ASSERT
        assertEquals("Solo se permiten fotografías JPG o PNG de hasta 5 MB", resultado)
    }

    @Test
    fun validarArchivoAdjuntoTamanoMayorAlLimite() {
        // ARRANGE
        val tipoMime = "image/jpeg"
        val tamanoBytes = (5L * 1024L * 1024L) + 10L

        // ACT
        val resultado = validarArchivoAdjunto(tipoMime, tamanoBytes)

        // ASSERT
        assertEquals("Solo se permiten fotografías JPG o PNG de hasta 5 MB", resultado)
    }

    @Test
    fun validarArchivoAdjuntoTipoMimeNull() {
        // ARRANGE
        val tipoMime: String? = null
        val tamanoBytes = 1024L

        // ACT
        val resultado = validarArchivoAdjunto(tipoMime, tamanoBytes)

        // ASSERT
        assertEquals("Solo se permiten fotografías JPG o PNG de hasta 5 MB", resultado)
    }

    @Test
    fun validarArchivoAdjuntoTamanoCero() {
        // ARRANGE
        val tipoMime = "image/jpeg"
        val tamanoBytes = 0L

        // ACT
        val resultado = validarArchivoAdjunto(tipoMime, tamanoBytes)

        // ASSERT
        assertEquals("Solo se permiten fotografías JPG o PNG de hasta 5 MB", resultado)
    }

    // ============================================================================
    // COBERTURA DE PARÁMETROS CON VALOR POR DEFECTO
    // ============================================================================

    @Test
    fun validarComentarioDocenteSinParametroAccion() {
        // ARRANGE — comentario vacío para forzar el retorno del mensaje de error

        // ACT  — se llama SIN el parámetro accion para ejecutar el valor por defecto "guardar"
        val resultado = validarComentarioDocente("")

        // ASSERT
        assertEquals("Escriba un comentario antes de guardar", resultado)
    }
}
