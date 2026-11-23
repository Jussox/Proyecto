package cl.duoc.bitacora2.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SesionTest {

    @Before
    fun prepararSesion() {
        // ARRANGE — asegurar que cada prueba comience con la sesión limpia
        Sesion.cerrar()
    }

    // ============================================================================
    // 1. iniciar
    // ============================================================================

    @Test
    fun iniciarDatosValidosGuardaInformacionDeSesion() {
        // ARRANGE
        val usuario = "docente1"
        val nombre = "Ana Pérez"
        val perfil = "docente"
        val curso = "Programación"

        // ACT
        Sesion.iniciar(
            usuario = usuario,
            nombre = nombre,
            perfil = perfil,
            curso = curso
        )

        // ASSERT
        assertEquals(usuario, Sesion.usuario)
        assertEquals(nombre, Sesion.nombre)
        assertEquals(perfil, Sesion.perfil)
        assertEquals(curso, Sesion.curso)
    }

    @Test
    fun iniciarNuevaSesionReemplazaDatosAnteriores() {
        // ARRANGE
        Sesion.iniciar("estudiante1", "Pedro Soto", "estudiante", "Primero")
        val nuevoUsuario = "docente2"
        val nuevoNombre = "María Díaz"
        val nuevoPerfil = "docente"
        val nuevoCurso = "Segundo"

        // ACT
        Sesion.iniciar(nuevoUsuario, nuevoNombre, nuevoPerfil, nuevoCurso)

        // ASSERT
        assertEquals(nuevoUsuario, Sesion.usuario)
        assertEquals(nuevoNombre, Sesion.nombre)
        assertEquals(nuevoPerfil, Sesion.perfil)
        assertEquals(nuevoCurso, Sesion.curso)
    }

    // ============================================================================
    // 2. cerrar
    // ============================================================================

    @Test
    fun cerrarSesionLimpiaTodosLosDatos() {
        // ARRANGE
        Sesion.iniciar("docente1", "Ana Pérez", "docente", "Programación")

        // ACT
        Sesion.cerrar()

        // ASSERT
        assertEquals("", Sesion.usuario)
        assertEquals("", Sesion.nombre)
        assertEquals("", Sesion.perfil)
        assertEquals("", Sesion.curso)
    }

    // ============================================================================
    // 3. estaActiva
    // ============================================================================

    @Test
    fun estaActivaUsuarioConContenidoRetornaTrue() {
        // ARRANGE
        Sesion.usuario = "docente1"

        // ACT
        val resultado = Sesion.estaActiva

        // ASSERT
        assertTrue(resultado)
    }

    @Test
    fun estaActivaUsuarioVacioRetornaFalse() {
        // ARRANGE
        Sesion.usuario = ""

        // ACT
        val resultado = Sesion.estaActiva

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun estaActivaUsuarioSoloConEspaciosRetornaFalse() {
        // ARRANGE
        Sesion.usuario = "     "

        // ACT
        val resultado = Sesion.estaActiva

        // ASSERT
        assertFalse(resultado)
    }

    @Test
    fun estaActivaDespuesDeCerrarSesionRetornaFalse() {
        // ARRANGE
        Sesion.iniciar("estudiante1", "Pedro Soto", "estudiante", "Primero")

        // ACT
        Sesion.cerrar()
        val resultado = Sesion.estaActiva

        // ASSERT
        assertFalse(resultado)
    }
}
