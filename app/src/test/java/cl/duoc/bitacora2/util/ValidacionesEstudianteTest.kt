package cl.duoc.bitacora2.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Date

class ValidacionesEstudianteTest {

    // Helper privado para construir fechas fijas con Calendar sin parseo de cadenas
    private fun crearFecha(anio: Int, mes: Int, dia: Int): Date {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, anio)
            set(Calendar.MONTH, mes - 1)
            set(Calendar.DAY_OF_MONTH, dia)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    // ============================================================================
    // 1. validarEnlace
    // ============================================================================

    @Test
    fun validarEnlaceEnlaceVacio() {
        // ARRANGE
        val enlace = ""

        // ACT
        val resultado = validarEnlace(enlace)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarEnlaceEnlaceSoloEspacios() {
        // ARRANGE
        val enlace = "   "

        // ACT
        val resultado = validarEnlace(enlace)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarEnlaceEnlaceHttpValido() {
        // ARRANGE
        val enlace = "http://www.google.cl"

        // ACT
        val resultado = validarEnlace(enlace)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarEnlaceEnlaceHttpsValido() {
        // ARRANGE
        val enlace = "https://duoc.cl/recursos"

        // ACT
        val resultado = validarEnlace(enlace)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarEnlaceEnlaceHttpConMayusculas() {
        // ARRANGE
        val enlace = "HTTP://EJEMPLO.COM"

        // ACT
        val resultado = validarEnlace(enlace)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarEnlaceEnlaceHttpsConEspaciosAlrededor() {
        // ARRANGE
        val enlace = "  https://sitio.org/documento  "

        // ACT
        val resultado = validarEnlace(enlace)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarEnlaceEnlaceSinProtocoloHttp() {
        // ARRANGE
        val enlace = "www.duoc.cl"

        // ACT
        val resultado = validarEnlace(enlace)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarEnlaceEnlaceInvalidoCualquiera() {
        // ARRANGE
        val enlace = "enlace-invalido"

        // ACT
        val resultado = validarEnlace(enlace)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 2. validarCamposRegistro
    // ============================================================================

    @Test
    fun validarCamposRegistroTodosLosCamposConTexto() {
        // ARRANGE
        val contenido = "Contenido de la clase"
        val actividad = "Actividad práctica realizada"
        val aprendizaje = "Lo aprendido en la sesión"

        // ACT
        val resultado = validarCamposRegistro(contenido, actividad, aprendizaje)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarCamposRegistroContenidoVacio() {
        // ARRANGE
        val contenido = ""
        val actividad = "Actividad práctica realizada"
        val aprendizaje = "Lo aprendido en la sesión"

        // ACT
        val resultado = validarCamposRegistro(contenido, actividad, aprendizaje)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarCamposRegistroActividadVacia() {
        // ARRANGE
        val contenido = "Contenido de la clase"
        val actividad = ""
        val aprendizaje = "Lo aprendido en la sesión"

        // ACT
        val resultado = validarCamposRegistro(contenido, actividad, aprendizaje)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarCamposRegistroAprendizajeVacio() {
        // ARRANGE
        val contenido = "Contenido de la clase"
        val actividad = "Actividad práctica realizada"
        val aprendizaje = ""

        // ACT
        val resultado = validarCamposRegistro(contenido, actividad, aprendizaje)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarCamposRegistroContenidoSoloEspacios() {
        // ARRANGE
        val contenido = "    "
        val actividad = "Actividad práctica"
        val aprendizaje = "Aprendizaje adquirido"

        // ACT
        val resultado = validarCamposRegistro(contenido, actividad, aprendizaje)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarCamposRegistroTodosLosCamposVacios() {
        // ARRANGE
        val contenido = ""
        val actividad = ""
        val aprendizaje = ""

        // ACT
        val resultado = validarCamposRegistro(contenido, actividad, aprendizaje)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 3. validarFechaClase
    // ============================================================================

    @Test
    fun validarFechaClaseFechaPasadaQueCoincideConMesYAnio() {
        // ARRANGE
        val fecha = "15/03/2024"
        val mes = "marzo"
        val anio = "2024"
        val hoyFijo = crearFecha(2024, 3, 20)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarFechaClaseFechaMismoDiaDeHoy() {
        // ARRANGE
        val fecha = "15/03/2024"
        val mes = "marzo"
        val anio = "2024"
        val hoyFijo = crearFecha(2024, 3, 15)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarFechaClaseFechaFuturaRespectoAHoy() {
        // ARRANGE
        val fecha = "16/03/2024"
        val mes = "marzo"
        val anio = "2024"
        val hoyFijo = crearFecha(2024, 3, 15)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaClaseFechaValidaPeroMesNoCoincide() {
        // ARRANGE
        val fecha = "15/04/2024"
        val mes = "marzo"
        val anio = "2024"
        val hoyFijo = crearFecha(2024, 5, 1)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaClaseFechaValidaPeroAnioNoCoincide() {
        // ARRANGE
        val fecha = "15/03/2023"
        val mes = "marzo"
        val anio = "2024"
        val hoyFijo = crearFecha(2024, 5, 1)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaClaseFormatoFechaInvalidoGuiones() {
        // ARRANGE
        val fecha = "15-03-2024"
        val mes = "marzo"
        val anio = "2024"
        val hoyFijo = crearFecha(2024, 5, 1)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaClaseFormatoFechaInvalidoTexto() {
        // ARRANGE
        val fecha = "invalida"
        val mes = "marzo"
        val anio = "2024"
        val hoyFijo = crearFecha(2024, 5, 1)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaClaseFormatoFechaDiaInexistenteBisiesto() {
        // ARRANGE
        val fecha = "29/02/2023"
        val mes = "febrero"
        val anio = "2023"
        val hoyFijo = crearFecha(2024, 1, 1)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaClaseMesInvalido() {
        // ARRANGE
        val fecha = "15/03/2024"
        val mes = "mesInexistente"
        val anio = "2024"
        val hoyFijo = crearFecha(2024, 5, 1)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaClaseAnioConMenosDeCuatroDigitos() {
        // ARRANGE
        val fecha = "15/03/2024"
        val mes = "marzo"
        val anio = "24"
        val hoyFijo = crearFecha(2024, 5, 1)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaClaseAnioConLetras() {
        // ARRANGE
        val fecha = "15/03/2024"
        val mes = "marzo"
        val anio = "202a"
        val hoyFijo = crearFecha(2024, 5, 1)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaClaseFechaConEspaciosAlrededor() {
        // ARRANGE
        val fecha = " 15/03/2024 "
        val mes = "marzo"
        val anio = "2024"
        val hoyFijo = crearFecha(2024, 3, 20)

        // ACT
        val resultado = validarFechaClase(fecha, mes, anio, hoyFijo)

        // ASSERT
        assertTrue(resultado)
    }

    // ============================================================================
    // 4. validarFormularioRegistroEstudiante
    // ============================================================================

    @Test
    fun validarFormularioRegistroEstudianteFormularioCompletoValido() {
        // ARRANGE
        val bitacoraDisponible = true
        val fecha = "15/03/2024"
        val mesBitacora = "Marzo"
        val anioBitacora = "2024"
        val contenido = "Introducción a Kotlin"
        val actividad = "Ejercicios prácticos"
        val aprendizaje = "Sintaxis básica y null safety"
        val enlaces = "https://kotlinlang.org"

        // ACT
        val resultado = validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacoraDisponible,
            fecha = fecha,
            mesBitacora = mesBitacora,
            anioBitacora = anioBitacora,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            enlaces = enlaces
        )

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarFormularioRegistroEstudianteBitacoraNoDisponible() {
        // ARRANGE
        val bitacoraDisponible = false
        val fecha = "15/03/2024"
        val mesBitacora = "Marzo"
        val anioBitacora = "2024"
        val contenido = "Contenido"
        val actividad = "Actividad"
        val aprendizaje = "Aprendizaje"
        val enlaces = ""

        // ACT
        val resultado = validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacoraDisponible,
            fecha = fecha,
            mesBitacora = mesBitacora,
            anioBitacora = anioBitacora,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            enlaces = enlaces
        )

        // ASSERT
        assertEquals("La bitácora no está disponible", resultado)
    }

    @Test
    fun validarFormularioRegistroEstudianteFechaNoValidaOInconsistenteConBitacora() {
        // ARRANGE
        val bitacoraDisponible = true
        val fecha = "15/05/2024"
        val mesBitacora = "Marzo"
        val anioBitacora = "2024"
        val contenido = "Contenido"
        val actividad = "Actividad"
        val aprendizaje = "Aprendizaje"
        val enlaces = ""

        // ACT
        val resultado = validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacoraDisponible,
            fecha = fecha,
            mesBitacora = mesBitacora,
            anioBitacora = anioBitacora,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            enlaces = enlaces
        )

        // ASSERT
        assertEquals(
            "Ingrese una fecha válida con formato dd/mm/aaaa, no futura y que corresponda al mes y año de la bitácora (Marzo 2024)",
            resultado
        )
    }

    @Test
    fun validarFormularioRegistroEstudianteCamposObligatoriosIncompletos() {
        // ARRANGE
        val bitacoraDisponible = true
        val fecha = "15/03/2024"
        val mesBitacora = "Marzo"
        val anioBitacora = "2024"
        val contenido = ""
        val actividad = "Actividad"
        val aprendizaje = "Aprendizaje"
        val enlaces = ""

        // ACT
        val resultado = validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacoraDisponible,
            fecha = fecha,
            mesBitacora = mesBitacora,
            anioBitacora = anioBitacora,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            enlaces = enlaces
        )

        // ASSERT
        assertEquals("Complete los campos obligatorios (contenido, actividad y aprendizaje)", resultado)
    }

    @Test
    fun validarFormularioRegistroEstudianteEnlaceInvalido() {
        // ARRANGE
        val bitacoraDisponible = true
        val fecha = "15/03/2024"
        val mesBitacora = "Marzo"
        val anioBitacora = "2024"
        val contenido = "Contenido"
        val actividad = "Actividad"
        val aprendizaje = "Aprendizaje"
        val enlaces = "ftp://servidor.com"

        // ACT
        val resultado = validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacoraDisponible,
            fecha = fecha,
            mesBitacora = mesBitacora,
            anioBitacora = anioBitacora,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            enlaces = enlaces
        )

        // ASSERT
        assertEquals("El enlace debe comenzar con http:// o https://", resultado)
    }

    @Test
    fun validarFormularioRegistroEstudianteEnlaceVacioValido() {
        // ARRANGE
        val bitacoraDisponible = true
        val fecha = "15/03/2024"
        val mesBitacora = "Marzo"
        val anioBitacora = "2024"
        val contenido = "Contenido"
        val actividad = "Actividad"
        val aprendizaje = "Aprendizaje"
        val enlaces = ""

        // ACT
        val resultado = validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacoraDisponible,
            fecha = fecha,
            mesBitacora = mesBitacora,
            anioBitacora = anioBitacora,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            enlaces = enlaces
        )

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarFormularioRegistroEstudianteContenidoSuperaLargoMaximo() {
        // ARRANGE
        val bitacoraDisponible = true
        val fecha = "15/03/2024"
        val mesBitacora = "Marzo"
        val anioBitacora = "2024"
        val contenido = "a".repeat(2001)
        val actividad = "Actividad"
        val aprendizaje = "Aprendizaje"
        val enlaces = ""

        // ACT
        val resultado = validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacoraDisponible,
            fecha = fecha,
            mesBitacora = mesBitacora,
            anioBitacora = anioBitacora,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            enlaces = enlaces
        )

        // ASSERT
        assertEquals("El contenido no puede superar los 2000 caracteres", resultado)
    }

    @Test
    fun validarFormularioRegistroEstudianteActividadSuperaLargoMaximo() {
        // ARRANGE
        val bitacoraDisponible = true
        val fecha = "15/03/2024"
        val mesBitacora = "Marzo"
        val anioBitacora = "2024"
        val contenido = "Contenido"
        val actividad = "b".repeat(2001)
        val aprendizaje = "Aprendizaje"
        val enlaces = ""

        // ACT
        val resultado = validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacoraDisponible,
            fecha = fecha,
            mesBitacora = mesBitacora,
            anioBitacora = anioBitacora,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            enlaces = enlaces
        )

        // ASSERT
        assertEquals("La actividad no puede superar los 2000 caracteres", resultado)
    }

    @Test
    fun validarFormularioRegistroEstudianteAprendizajeSuperaLargoMaximo() {
        // ARRANGE
        val bitacoraDisponible = true
        val fecha = "15/03/2024"
        val mesBitacora = "Marzo"
        val anioBitacora = "2024"
        val contenido = "Contenido"
        val actividad = "Actividad"
        val aprendizaje = "c".repeat(2001)
        val enlaces = ""

        // ACT
        val resultado = validarFormularioRegistroEstudiante(
            bitacoraDisponible = bitacoraDisponible,
            fecha = fecha,
            mesBitacora = mesBitacora,
            anioBitacora = anioBitacora,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            enlaces = enlaces
        )

        // ASSERT
        assertEquals("El aprendizaje no puede superar los 2000 caracteres", resultado)
    }

    // ============================================================================
    // COBERTURA DE PARÁMETROS CON VALOR POR DEFECTO
    // ============================================================================

    @Test
    fun validarFechaClaseSinParametroFechaHoy() {
        // ARRANGE — fecha de marzo 2024, siempre pasada respecto a cualquier ejecución futura
        val fecha = "15/03/2024"
        val mes = "marzo"
        val anio = "2024"

        // ACT  — se llama SIN el parámetro fechaHoy para ejecutar el valor por defecto (Date())
        val resultado = validarFechaClase(fecha, mes, anio)

        // ASSERT
        assertTrue(resultado)
    }

    // ============================================================================
    // COBERTURA DE RAMAS INDIVIDUALES EN validarEnlace (||)
    // ============================================================================

    @Test
    fun validarEnlaceEnlaceConSoloUnEspacio() {
        // ARRANGE — un solo espacio: trim() lo deja vacío, isEmpty() = true
        val enlace = " "

        // ACT
        val resultado = validarEnlace(enlace)

        // ASSERT
        assertTrue(resultado)
    }
}
