package cl.duoc.bitacora2.util

import cl.duoc.bitacora2.data.Asignacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Date

class ValidacionesDocenteTest {

    // Helper privado para construir fechas fijas con Calendar
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
    // 1. numeroDeMes
    // ============================================================================

    @Test
    fun numeroDeMesMesValidoEnero() {
        // ARRANGE
        val mes = "enero"

        // ACT
        val resultado = numeroDeMes(mes)

        // ASSERT
        assertEquals(1, resultado)
    }

    @Test
    fun numeroDeMesMesValidoDiciembre() {
        // ARRANGE
        val mes = "diciembre"

        // ACT
        val resultado = numeroDeMes(mes)

        // ASSERT
        assertEquals(12, resultado)
    }

    @Test
    fun numeroDeMesMesValidoConMayusculas() {
        // ARRANGE
        val mes = "Octubre"

        // ACT
        val resultado = numeroDeMes(mes)

        // ASSERT
        assertEquals(10, resultado)
    }

    @Test
    fun numeroDeMesMesValidoConEspaciosAlrededor() {
        // ARRANGE
        val mes = "  mayo  "

        // ACT
        val resultado = numeroDeMes(mes)

        // ASSERT
        assertEquals(5, resultado)
    }

    @Test
    fun numeroDeMesMesInvalido() {
        // ARRANGE
        val mes = "mesFalso"

        // ACT
        val resultado = numeroDeMes(mes)

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun numeroDeMesMesVacio() {
        // ARRANGE
        val mes = ""

        // ACT
        val resultado = numeroDeMes(mes)

        // ASSERT
        assertNull(resultado)
    }

    // ============================================================================
    // 2. validarMes
    // ============================================================================

    @Test
    fun validarMesMesValido() {
        // ARRANGE
        val mes = "marzo"

        // ACT
        val resultado = validarMes(mes)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarMesMesInvalido() {
        // ARRANGE
        val mes = "eneroo"

        // ACT
        val resultado = validarMes(mes)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarMesMesVacio() {
        // ARRANGE
        val mes = ""

        // ACT
        val resultado = validarMes(mes)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 3. validarAnio
    // ============================================================================

    @Test
    fun validarAnioAnioActual() {
        // ARRANGE
        val anioActual = 2026
        val anio = "2026"

        // ACT
        val resultado = validarAnio(anio, anioActual)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarAnioAnioSiguiente() {
        // ARRANGE
        val anioActual = 2026
        val anio = "2027"

        // ACT
        val resultado = validarAnio(anio, anioActual)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarAnioAnioAnterior() {
        // ARRANGE
        val anioActual = 2026
        val anio = "2025"

        // ACT
        val resultado = validarAnio(anio, anioActual)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarAnioAnioDosAniosEnElFuturo() {
        // ARRANGE
        val anioActual = 2026
        val anio = "2028"

        // ACT
        val resultado = validarAnio(anio, anioActual)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarAnioAnioFormatoCortoDosDigitos() {
        // ARRANGE
        val anioActual = 2026
        val anio = "26"

        // ACT
        val resultado = validarAnio(anio, anioActual)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarAnioAnioCincoDigitos() {
        // ARRANGE
        val anioActual = 2026
        val anio = "20261"

        // ACT
        val resultado = validarAnio(anio, anioActual)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarAnioAnioConLetras() {
        // ARRANGE
        val anioActual = 2026
        val anio = "202a"

        // ACT
        val resultado = validarAnio(anio, anioActual)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarAnioAnioConEspaciosAlrededor() {
        // ARRANGE
        val anioActual = 2026
        val anio = " 2026 "

        // ACT
        val resultado = validarAnio(anio, anioActual)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarAnioAnioVacio() {
        // ARRANGE
        val anioActual = 2026
        val anio = ""

        // ACT
        val resultado = validarAnio(anio, anioActual)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 4. validarFormularioBitacora
    // ============================================================================

    @Test
    fun validarFormularioBitacoraTodosLosCamposValidos() {
        // ARRANGE
        val asignacion = Asignacion("docente1", "7.º Básico A", "Lenguaje")
        val mes = "Octubre"
        val anio = "2026"
        val titulo = "Bitácora de Octubre"
        val proposito = "Desarrollar habilidades"
        val instrucciones = "Completar semanalmente"
        val preguntas = "¿Qué aprendiste hoy?"

        // ACT
        val resultado = validarFormularioBitacora(
            asignacion, mes, anio, titulo, proposito, instrucciones, preguntas
        )

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarFormularioBitacoraAsignacionNull() {
        // ARRANGE
        val asignacion: Asignacion? = null
        val mes = "Octubre"
        val anio = "2026"
        val titulo = "Bitácora de Octubre"
        val proposito = "Desarrollar habilidades"
        val instrucciones = "Completar semanalmente"
        val preguntas = "¿Qué aprendiste hoy?"

        // ACT
        val resultado = validarFormularioBitacora(
            asignacion, mes, anio, titulo, proposito, instrucciones, preguntas
        )

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFormularioBitacoraAlgunCampoObligatorioVacio() {
        // ARRANGE
        val asignacion = Asignacion("docente1", "7.º Básico A", "Lenguaje")
        val mes = ""
        val anio = "2026"
        val titulo = "Bitácora de Octubre"
        val proposito = "Desarrollar habilidades"
        val instrucciones = "Completar semanalmente"
        val preguntas = "¿Qué aprendiste hoy?"

        // ACT
        val resultado = validarFormularioBitacora(
            asignacion, mes, anio, titulo, proposito, instrucciones, preguntas
        )

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFormularioBitacoraTituloSoloEspacios() {
        // ARRANGE
        val asignacion = Asignacion("docente1", "7.º Básico A", "Lenguaje")
        val mes = "Octubre"
        val anio = "2026"
        val titulo = "   "
        val proposito = "Desarrollar habilidades"
        val instrucciones = "Completar semanalmente"
        val preguntas = "¿Qué aprendiste hoy?"

        // ACT
        val resultado = validarFormularioBitacora(
            asignacion, mes, anio, titulo, proposito, instrucciones, preguntas
        )

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFormularioBitacoraAnioVacio() {
        // ARRANGE
        val asignacion = Asignacion("docente1", "7.º Básico A", "Lenguaje")
        val mes = "Octubre"
        val anio = ""
        val titulo = "Bitácora de Octubre"
        val proposito = "Desarrollar habilidades"
        val instrucciones = "Completar semanalmente"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarFormularioBitacora(
            asignacion, mes, anio, titulo, proposito, instrucciones, preguntas
        )

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFormularioBitacoraPropositoVacio() {
        // ARRANGE
        val asignacion = Asignacion("docente1", "7.º Básico A", "Lenguaje")
        val mes = "Octubre"
        val anio = "2026"
        val titulo = "Bitácora de Octubre"
        val proposito = ""
        val instrucciones = "Completar semanalmente"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarFormularioBitacora(
            asignacion, mes, anio, titulo, proposito, instrucciones, preguntas
        )

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFormularioBitacoraInstruccionesVacias() {
        // ARRANGE
        val asignacion = Asignacion("docente1", "7.º Básico A", "Lenguaje")
        val mes = "Octubre"
        val anio = "2026"
        val titulo = "Bitácora de Octubre"
        val proposito = "Desarrollar habilidades"
        val instrucciones = ""
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarFormularioBitacora(
            asignacion, mes, anio, titulo, proposito, instrucciones, preguntas
        )

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFormularioBitacoraPreguntasVacias() {
        // ARRANGE
        val asignacion = Asignacion("docente1", "7.º Básico A", "Lenguaje")
        val mes = "Octubre"
        val anio = "2026"
        val titulo = "Bitácora de Octubre"
        val proposito = "Desarrollar habilidades"
        val instrucciones = "Completar semanalmente"
        val preguntas = ""

        // ACT
        val resultado = validarFormularioBitacora(
            asignacion, mes, anio, titulo, proposito, instrucciones, preguntas
        )

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 5. validarCamposTextoBitacora
    // ============================================================================

    @Test
    fun validarCamposTextoBitacoraCamposValidosConAnioActual() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "Noviembre"
        val titulo = "Título de prueba"
        val proposito = "Propósito pedagógico"
        val instrucciones = "Instrucciones de llenado"
        val preguntas = "Preguntas guía"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarCamposTextoBitacoraCamposValidosConAnioSiguiente() {
        // ARRANGE
        val anioSiguienteReal = (Calendar.getInstance().get(Calendar.YEAR) + 1).toString()
        val mes = "Marzo"
        val titulo = "Bitácora anual"
        val proposito = "Propósito pedagógico"
        val instrucciones = "Instrucciones de llenado"
        val preguntas = "Preguntas guía"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioSiguienteReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarCamposTextoBitacoraMesVacioConDemasLlenos() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = ""
        val titulo = "Titulo"
        val proposito = "Proposito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Complete todos los campos obligatorios", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraAnioVacioConDemasLlenos() {
        // ARRANGE
        val mes = "Octubre"
        val anio = ""
        val titulo = "Titulo"
        val proposito = "Proposito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anio,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Complete todos los campos obligatorios", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraTituloVacioConDemasLlenos() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "Octubre"
        val titulo = ""
        val proposito = "Proposito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Complete todos los campos obligatorios", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraPropositoVacioConDemasLlenos() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "Octubre"
        val titulo = "Titulo"
        val proposito = ""
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Complete todos los campos obligatorios", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraInstruccionesVaciasConDemasLlenos() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "Octubre"
        val titulo = "Titulo"
        val proposito = "Proposito"
        val instrucciones = ""
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Complete todos los campos obligatorios", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraPreguntasVaciasConDemasLlenos() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "Octubre"
        val titulo = "Titulo"
        val proposito = "Proposito"
        val instrucciones = "Instrucciones"
        val preguntas = ""

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Complete todos los campos obligatorios", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraMesInvalido() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "mesInexistente"
        val titulo = "Título"
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Ingrese un mes válido (por ejemplo: Octubre)", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraAnioInvalidoPasado() {
        // ARRANGE
        val mes = "Noviembre"
        val anio = "1999"
        val titulo = "Título"
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anio,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("El año debe tener 4 dígitos y ser el actual o el siguiente", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraAnioInvalidoFuturoLejano() {
        // ARRANGE
        val mes = "Noviembre"
        val anio = "2099"
        val titulo = "Título"
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anio,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("El año debe tener 4 dígitos y ser el actual o el siguiente", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraAnioInvalidoDosDigitos() {
        // ARRANGE
        val mes = "Noviembre"
        val anio = "26"
        val titulo = "Título"
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anio,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("El año debe tener 4 dígitos y ser el actual o el siguiente", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraAnioInvalidoConLetras() {
        // ARRANGE
        val mes = "Noviembre"
        val anio = "202a"
        val titulo = "Título"
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anio,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("El año debe tener 4 dígitos y ser el actual o el siguiente", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraTituloSuperaLargoMaximo() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "Noviembre"
        val titulo = "a".repeat(101)
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("El título no puede superar los 100 caracteres", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraPropositoSuperaLargoMaximo() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "Noviembre"
        val titulo = "Título válido"
        val proposito = "b".repeat(2001)
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("El propósito no puede superar los 2000 caracteres", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraInstruccionesSuperaLargoMaximo() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "Noviembre"
        val titulo = "Título válido"
        val proposito = "Propósito válido"
        val instrucciones = "c".repeat(2001)
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Las instrucciones no pueden superar los 2000 caracteres", resultado)
    }

    @Test
    fun validarCamposTextoBitacoraPreguntasSuperaLargoMaximo() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val mes = "Noviembre"
        val titulo = "Título válido"
        val proposito = "Propósito válido"
        val instrucciones = "Instrucciones válidas"
        val preguntas = "d".repeat(2001)

        // ACT
        val resultado = validarCamposTextoBitacora(
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Las preguntas no pueden superar los 2000 caracteres", resultado)
    }

    // ============================================================================
    // 6. validarCrearBitacora
    // ============================================================================

    @Test
    fun validarCrearBitacoraConAsignacionYCamposValidosAnioActual() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val tieneAsignacion = true
        val mes = "Octubre"
        val titulo = "Bitácora"
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCrearBitacora(
            tieneAsignacion = tieneAsignacion,
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarCrearBitacoraConAsignacionYCamposValidosAnioSiguiente() {
        // ARRANGE
        val anioSiguienteReal = (Calendar.getInstance().get(Calendar.YEAR) + 1).toString()
        val tieneAsignacion = true
        val mes = "Marzo"
        val titulo = "Bitácora"
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCrearBitacora(
            tieneAsignacion = tieneAsignacion,
            mes = mes,
            anio = anioSiguienteReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarCrearBitacoraSinAsignacion() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val tieneAsignacion = false
        val mes = "Octubre"
        val titulo = "Bitácora"
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCrearBitacora(
            tieneAsignacion = tieneAsignacion,
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Seleccione un curso y asignatura", resultado)
    }

    @Test
    fun validarCrearBitacoraConAsignacionPeroCamposInvalidos() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()
        val tieneAsignacion = true
        val mes = ""
        val titulo = "Bitácora"
        val proposito = "Propósito"
        val instrucciones = "Instrucciones"
        val preguntas = "Preguntas"

        // ACT
        val resultado = validarCrearBitacora(
            tieneAsignacion = tieneAsignacion,
            mes = mes,
            anio = anioActualReal,
            titulo = titulo,
            proposito = proposito,
            instrucciones = instrucciones,
            preguntas = preguntas
        )

        // ASSERT
        assertEquals("Complete todos los campos obligatorios", resultado)
    }

    // ============================================================================
    // 7. validarComentario
    // ============================================================================

    @Test
    fun validarComentarioComentarioConTexto() {
        // ARRANGE
        val comentario = "Excelente trabajo en la bitácora"

        // ACT
        val resultado = validarComentario(comentario)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarComentarioComentarioVacio() {
        // ARRANGE
        val comentario = ""

        // ACT
        val resultado = validarComentario(comentario)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarComentarioComentarioSoloEspacios() {
        // ARRANGE
        val comentario = "     "

        // ACT
        val resultado = validarComentario(comentario)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 8. validarComentarioDocente
    // ============================================================================

    @Test
    fun validarComentarioDocenteComentarioValidoAccionGuardar() {
        // ARRANGE
        val comentario = "Comentario de revisión en borrador"
        val accion = "guardar"

        // ACT
        val resultado = validarComentarioDocente(comentario, accion)

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarComentarioDocenteComentarioValidoAccionRevisar() {
        // ARRANGE
        val comentario = "Comentario de retroalimentación final"
        val accion = "revisar"

        // ACT
        val resultado = validarComentarioDocente(comentario, accion)

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarComentarioDocenteComentarioVacioAccionGuardar() {
        // ARRANGE
        val comentario = ""
        val accion = "guardar"

        // ACT
        val resultado = validarComentarioDocente(comentario, accion)

        // ASSERT
        assertEquals("Escriba un comentario antes de guardar", resultado)
    }

    @Test
    fun validarComentarioDocenteComentarioVacioAccionRevisar() {
        // ARRANGE
        val comentario = ""
        val accion = "revisar"

        // ACT
        val resultado = validarComentarioDocente(comentario, accion)

        // ASSERT
        assertEquals("Escriba un comentario antes de marcar como revisado", resultado)
    }

    @Test
    fun validarComentarioDocenteComentarioSuperaLargoMaximo() {
        // ARRANGE
        val comentario = "c".repeat(2001)
        val accion = "guardar"

        // ACT
        val resultado = validarComentarioDocente(comentario, accion)

        // ASSERT
        assertEquals("El comentario no puede superar los 2000 caracteres", resultado)
    }

    // ============================================================================
    // 9. validarCaracteresFiltroFecha
    // ============================================================================

    @Test
    fun validarCaracteresFiltroFechaTextoFechaValidoParcial() {
        // ARRANGE
        val texto = "15/"

        // ACT
        val resultado = validarCaracteresFiltroFecha(texto)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarCaracteresFiltroFechaTextoFechaValidoCompleto() {
        // ARRANGE
        val texto = "15/03/2024"

        // ACT
        val resultado = validarCaracteresFiltroFecha(texto)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarCaracteresFiltroFechaTextoConSoloDigitos() {
        // ARRANGE
        val texto = "15032024"

        // ACT
        val resultado = validarCaracteresFiltroFecha(texto)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarCaracteresFiltroFechaTextoConLetras() {
        // ARRANGE
        val texto = "15/marzo"

        // ACT
        val resultado = validarCaracteresFiltroFecha(texto)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarCaracteresFiltroFechaTextoConGuiones() {
        // ARRANGE
        val texto = "15-03-2024"

        // ACT
        val resultado = validarCaracteresFiltroFecha(texto)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarCaracteresFiltroFechaTextoSuperaDiezCaracteres() {
        // ARRANGE
        val texto = "15/03/20241"

        // ACT
        val resultado = validarCaracteresFiltroFecha(texto)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 10. cumpleFiltrosAporte
    // ============================================================================

    @Test
    fun cumpleFiltrosAporteTodosLosFiltrosVaciosOTodos() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "", filtroFecha = "", filtroEstado = "todos"
        )

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun cumpleFiltrosAporteFiltroEstudiantePorUsuario() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "ESTUDIANTE", filtroFecha = "", filtroEstado = "todos"
        )

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun cumpleFiltrosAporteFiltroEstudiantePorNombre() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie González"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "gonzález", filtroFecha = "", filtroEstado = "todos"
        )

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun cumpleFiltrosAporteFiltroEstudianteConEspaciosAlrededor() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "  Angie  ", filtroFecha = "", filtroEstado = "todos"
        )

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun cumpleFiltrosAporteFiltroFechaCoincidente() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "", filtroFecha = "15/03", filtroEstado = "todos"
        )

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun cumpleFiltrosAporteFiltroEstadoDistintaCapitalizacion() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "", filtroFecha = "", filtroEstado = "PENDIENTE"
        )

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun cumpleFiltrosAporteSoloEstudianteCoincide() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "Angie", filtroFecha = "20/04/2024", filtroEstado = "todos"
        )

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun cumpleFiltrosAporteSoloFechaCoincide() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "Carlos", filtroFecha = "15/03/2024", filtroEstado = "todos"
        )

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun cumpleFiltrosAporteNingunoCoincide() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "Juan", filtroFecha = "01/01/2025", filtroEstado = "revisado"
        )

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 11. clasificarMensajeRecordatorio
    // ============================================================================

    @Test
    fun clasificarMensajeRecordatorioMensajeVacio() {
        // ARRANGE
        val mensaje = ""

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.NINGUNO, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioMensajeSoloEspacios() {
        // ARRANGE
        val mensaje = "    "

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.NINGUNO, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioPalabraIngresa() {
        // ARRANGE
        val mensaje = "Por favor ingresa una fecha"

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.ERROR, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioPalabraIndique() {
        // ARRANGE
        val mensaje = "Indique un tipo de recordatorio"

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.ERROR, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioPalabraValida() {
        // ARRANGE
        val mensaje = "La fecha no es válida"

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.ERROR, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioPalabraPaso() {
        // ARRANGE
        val mensaje = "La fecha ingresada ya pasó"

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.ERROR, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioPalabraAnterior() {
        // ARRANGE
        val mensaje = "No puede ser anterior a la actual"

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.ERROR, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioPalabraError() {
        // ARRANGE
        val mensaje = "Ocurrió un error en el guardado"

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.ERROR, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioPalabraSeleccione() {
        // ARRANGE
        val mensaje = "Seleccione un elemento de la lista"

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.ERROR, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioPalabraCompletad() {
        // ARRANGE
        val mensaje = "El recordatorio fue completado exitosamente"

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.COMPLETADO, resultado)
    }

    @Test
    fun clasificarMensajeRecordatorioMensajeCreacionNormal() {
        // ARRANGE
        val mensaje = "Recordatorio programado para mañana"

        // ACT
        val resultado = clasificarMensajeRecordatorio(mensaje)

        // ASSERT
        assertEquals(TipoMensajeRecordatorio.CREADO, resultado)
    }

    // ============================================================================
    // 12. esRecordatorioPendiente
    // ============================================================================

    @Test
    fun esRecordatorioPendienteEstadoPendienteMinuscula() {
        // ARRANGE
        val estado = "pendiente"

        // ACT
        val resultado = esRecordatorioPendiente(estado)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun esRecordatorioPendienteEstadoPendienteMayuscula() {
        // ARRANGE
        val estado = "PENDIENTE"

        // ACT
        val resultado = esRecordatorioPendiente(estado)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun esRecordatorioPendienteEstadoCompletado() {
        // ARRANGE
        val estado = "completado"

        // ACT
        val resultado = esRecordatorioPendiente(estado)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun esRecordatorioPendienteEstadoNull() {
        // ARRANGE
        val estado: String? = null

        // ACT
        val resultado = esRecordatorioPendiente(estado)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 13. validarFechaRecordatorio
    // ============================================================================

    @Test
    fun validarFechaRecordatorioFechaHoy() {
        // ARRANGE
        val fecha = "20/11/2026"
        val hoyFijo = crearFecha(2026, 11, 20)

        // ACT
        val resultado = validarFechaRecordatorio(fecha, hoyFijo)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarFechaRecordatorioFechaFutura() {
        // ARRANGE
        val fecha = "25/11/2026"
        val hoyFijo = crearFecha(2026, 11, 20)

        // ACT
        val resultado = validarFechaRecordatorio(fecha, hoyFijo)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarFechaRecordatorioFechaPasada() {
        // ARRANGE
        val fecha = "19/11/2026"
        val hoyFijo = crearFecha(2026, 11, 20)

        // ACT
        val resultado = validarFechaRecordatorio(fecha, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun validarFechaRecordatorioFormatoInvalido() {
        // ARRANGE
        val fecha = "20-11-2026"
        val hoyFijo = crearFecha(2026, 11, 20)

        // ACT
        val resultado = validarFechaRecordatorio(fecha, hoyFijo)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 14. validarFechaRecordatorioConMensaje
    // ============================================================================

    @Test
    fun validarFechaRecordatorioConMensajeFechaValidaFutura() {
        // ARRANGE
        val anioFuturo = (Calendar.getInstance().get(Calendar.YEAR) + 1).toString()
        val fecha = "15/06/$anioFuturo"

        // ACT
        val resultado = validarFechaRecordatorioConMensaje(fecha)

        // ASSERT
        assertNull(resultado)
    }

    @Test
    fun validarFechaRecordatorioConMensajeFechaVacia() {
        // ARRANGE
        val fecha = ""

        // ACT
        val resultado = validarFechaRecordatorioConMensaje(fecha)

        // ASSERT
        assertEquals("Ingresa una fecha programada en formato dd/mm/aaaa antes de continuar.", resultado)
    }

    @Test
    fun validarFechaRecordatorioConMensajeFechaInvalidaOPasada() {
        // ARRANGE
        val fecha = "01/01/2020"

        // ACT
        val resultado = validarFechaRecordatorioConMensaje(fecha)

        // ASSERT
        assertEquals(
            "La fecha ingresada no es válida o ya pasó. Debe tener formato dd/mm/aaaa y ser igual o posterior a hoy.",
            resultado
        )
    }

    // ============================================================================
    // 15. mensajeRecordatorioProgramado
    // ============================================================================

    @Test
    fun mensajeRecordatorioProgramadoParametrosValidos() {
        // ARRANGE
        val tipo = "Revisión general"
        val fecha = "25/11/2026"

        // ACT
        val resultado = mensajeRecordatorioProgramado(tipo, fecha)

        // ASSERT
        assertEquals(
            "Recordatorio de \"Revisión general\" programado para el 25/11/2026. Ya está visible en tu lista.",
            resultado
        )
    }

    // ============================================================================
    // 16. mensajeRecordatorioCompletado
    // ============================================================================

    @Test
    fun mensajeRecordatorioCompletadoConTipoEspecifico() {
        // ARRANGE
        val tipo = "Evaluación"

        // ACT
        val resultado = mensajeRecordatorioCompletado(tipo)

        // ASSERT
        assertEquals("El recordatorio \"Evaluación\" fue marcado como completado.", resultado)
    }

    @Test
    fun mensajeRecordatorioCompletadoConTipoNull() {
        // ARRANGE
        val tipo: String? = null

        // ACT
        val resultado = mensajeRecordatorioCompletado(tipo)

        // ASSERT
        assertEquals("El recordatorio fue marcado como completado.", resultado)
    }

    @Test
    fun mensajeRecordatorioCompletadoConTipoVacio() {
        // ARRANGE
        val tipo = ""

        // ACT
        val resultado = mensajeRecordatorioCompletado(tipo)

        // ASSERT
        assertEquals("El recordatorio fue marcado como completado.", resultado)
    }

    // ============================================================================
    // 17. esAporteRevisado
    // ============================================================================

    @Test
    fun esAporteRevisadoEstadoRevisadoMinuscula() {
        // ARRANGE
        val estado = "revisado"

        // ACT
        val resultado = esAporteRevisado(estado)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun esAporteRevisadoEstadoRevisadoMayuscula() {
        // ARRANGE
        val estado = "REVISADO"

        // ACT
        val resultado = esAporteRevisado(estado)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun esAporteRevisadoEstadoPendiente() {
        // ARRANGE
        val estado = "pendiente"

        // ACT
        val resultado = esAporteRevisado(estado)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun esAporteRevisadoEstadoNull() {
        // ARRANGE
        val estado: String? = null

        // ACT
        val resultado = esAporteRevisado(estado)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 18. tieneFotografiaValida
    // ============================================================================

    @Test
    fun tieneFotografiaValidaUriValida() {
        // ARRANGE
        val uri = "content://media/external/images/media/123"

        // ACT
        val resultado = tieneFotografiaValida(uri)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun tieneFotografiaValidaUriNull() {
        // ARRANGE
        val uri: String? = null

        // ACT
        val resultado = tieneFotografiaValida(uri)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun tieneFotografiaValidaUriVacia() {
        // ARRANGE
        val uri = ""

        // ACT
        val resultado = tieneFotografiaValida(uri)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun tieneFotografiaValidaSoloEspaciosEnBlanco() {
        // ARRANGE
        val uri = "   "

        // ACT
        val resultado = tieneFotografiaValida(uri)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun tieneFotografiaValidaGuionSinEspacios() {
        // ARRANGE
        val uri = "—"

        // ACT
        val resultado = tieneFotografiaValida(uri)

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun tieneFotografiaValidaGuionConEspaciosAlrededor() {
        // ARRANGE
        val uri = " — "

        // ACT
        val resultado = tieneFotografiaValida(uri)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // 19. textoContadorComentario
    // ============================================================================

    @Test
    fun textoContadorComentarioComentarioVacio() {
        // ARRANGE
        val comentario = ""

        // ACT
        val resultado = textoContadorComentario(comentario)

        // ASSERT
        assertEquals("0 / 2000", resultado)
    }

    @Test
    fun textoContadorComentarioComentarioCincuentaCaracteres() {
        // ARRANGE
        val comentario = "a".repeat(50)

        // ACT
        val resultado = textoContadorComentario(comentario)

        // ASSERT
        assertEquals("50 / 2000", resultado)
    }

    @Test
    fun textoContadorComentarioComentarioExactamenteDosMilCaracteres() {
        // ARRANGE
        val comentario = "x".repeat(2000)

        // ACT
        val resultado = textoContadorComentario(comentario)

        // ASSERT
        assertEquals("2000 / 2000", resultado)
    }

    // ============================================================================
    // COBERTURA DE PARÁMETROS CON VALOR POR DEFECTO
    // ============================================================================

    @Test
    fun validarAnioSinParametroAnioActual() {
        // ARRANGE
        val anioActualReal = Calendar.getInstance().get(Calendar.YEAR).toString()

        // ACT  — se llama SIN el segundo parámetro para ejecutar el valor por defecto
        val resultado = validarAnio(anioActualReal)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarFechaRecordatorioSinParametroFechaHoy() {
        // ARRANGE — año calculado dinámicamente para garantizar que la fecha sea siempre futura
        val anioFuturo = (Calendar.getInstance().get(Calendar.YEAR) + 1).toString()
        val fecha = "15/06/$anioFuturo"

        // ACT  — se llama SIN el segundo parámetro para ejecutar el valor por defecto (Date())
        val resultado = validarFechaRecordatorio(fecha)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun mensajeRecordatorioCompletadoSinArgumentos() {
        // ARRANGE — sin ningún argumento para disparar el valor por defecto tipo=null

        // ACT  — se llama SIN argumentos para ejecutar el valor por defecto
        val resultado = mensajeRecordatorioCompletado()

        // ASSERT
        assertEquals("El recordatorio fue marcado como completado.", resultado)
    }

    // ============================================================================
    // COBERTURA DE RAMAS INDIVIDUALES EN validarCaracteresFiltroFecha (&&)
    // ============================================================================

    @Test
    fun validarCaracteresFiltroFechaTextoVacio() {
        // ARRANGE
        val texto = ""

        // ACT
        val resultado = validarCaracteresFiltroFecha(texto)

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun validarCaracteresFiltroFechaLongitudExcedidaConCaracteresValidos() {
        // ARRANGE — 11 dígitos, todos válidos pero longitud > 10
        val texto = "12345678901"

        // ACT
        val resultado = validarCaracteresFiltroFecha(texto)

        // ASSERT
        assertFalse(resultado)
    }

    // ============================================================================
    // COBERTURA DE RAMAS INDIVIDUALES EN cumpleFiltrosAporte (filtroFecha no blank y no coincide)
    // ============================================================================

    @Test
    fun cumpleFiltrosAporteFiltroFechaNoCoincide() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "", filtroFecha = "20/04/2025", filtroEstado = "todos"
        )

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun cumpleFiltrosAporteFiltroEstadoNoCoincide() {
        // ARRANGE
        val estudianteUsuario = "estudiante1"
        val estudianteNombre = "Angie"
        val fechaClase = "15/03/2024"
        val estadoRegistro = "pendiente"

        // ACT
        val resultado = cumpleFiltrosAporte(
            estudianteUsuario, estudianteNombre, fechaClase, estadoRegistro,
            filtroEstudiante = "", filtroFecha = "", filtroEstado = "revisado"
        )

        // ASSERT
        assertFalse(resultado)
    }
}
