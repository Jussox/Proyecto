package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.EstudianteDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DocenteEstudiantesViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: DocenteEstudiantesViewModel

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
        viewModel = DocenteEstudiantesViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // ============================================================================
    // 1. Estado inicial
    // ============================================================================

    @Test
    fun estadoInicialNoEstaCargandoYNoTieneEstudiantesNiErrores() {
        // ARRANGE — viewModel recién creado en configurar()

        // ACT
        val estado = viewModel.uiState.value

        // ASSERT
        assertFalse(estado.cargando)
        assertTrue(estado.estudiantes.isEmpty())
        assertNull(estado.error)
    }

    // ============================================================================
    // 2. cargarEstudiantes: validación del curso
    // ============================================================================

    @Test
    fun cargarEstudiantesConCursoVacioNoConsultaAlRepositorio() {
        // ARRANGE

        // ACT
        viewModel.cargarEstudiantes("")

        // ASSERT
        assertFalse(viewModel.uiState.value.cargando)
        assertTrue(viewModel.uiState.value.estudiantes.isEmpty())
        coVerify(exactly = 0) { repository.obtenerEstudiantes(any()) }
    }

    @Test
    fun cargarEstudiantesConCursoEnBlancoNoConsultaAlRepositorio() {
        // ARRANGE

        // ACT
        viewModel.cargarEstudiantes("   ")

        // ASSERT
        assertFalse(viewModel.uiState.value.cargando)
        coVerify(exactly = 0) { repository.obtenerEstudiantes(any()) }
    }

    // ============================================================================
    // 3. cargarEstudiantes: respuesta del servidor
    // ============================================================================

    @Test
    fun cargarEstudiantesConRespuestaRemotaGuardaLosEstudiantesRecibidos() {
        // ARRANGE
        val estudiantes = listOf(
            EstudianteDto(id = 1, usuario = "estudiante10", nombre = "Ana", curso = "8.º Básico B"),
            EstudianteDto(id = 2, usuario = "estudiante11", nombre = "Luis", curso = "8.º Básico B")
        )
        coEvery { repository.obtenerEstudiantes("8.º Básico B") } returns estudiantes

        // ACT
        viewModel.cargarEstudiantes("8.º Básico B")

        // ASSERT
        assertEquals(estudiantes, viewModel.uiState.value.estudiantes)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
        coVerify(exactly = 1) { repository.obtenerEstudiantes("8.º Básico B") }
    }

    @Test
    fun cargarEstudiantesConRespuestaRemotaVaciaUsaLosEstudiantesDePruebaDelCurso() {
        // ARRANGE
        coEvery { repository.obtenerEstudiantes("7.º Básico A") } returns emptyList()

        // ACT
        viewModel.cargarEstudiantes("7.º Básico A")

        // ASSERT
        val estudiantes = viewModel.uiState.value.estudiantes
        assertEquals(3, estudiantes.size)
        assertEquals("estudiante1", estudiantes[0].usuario)
        assertEquals("Angie", estudiantes[0].nombre)
        assertEquals("7.º Básico A", estudiantes[0].curso)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarEstudiantesConRespuestaRemotaVaciaYCursoSinDatosDePruebaDejaLaListaVacia() {
        // ARRANGE
        coEvery { repository.obtenerEstudiantes("Curso inexistente") } returns emptyList()

        // ACT
        viewModel.cargarEstudiantes("Curso inexistente")

        // ASSERT
        assertTrue(viewModel.uiState.value.estudiantes.isEmpty())
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
    }

    // ============================================================================
    // 4. cargarEstudiantes: errores de conexión y respaldo local
    // ============================================================================

    @Test
    fun cargarEstudiantesConErrorDeConexionUsaLosEstudiantesDePruebaComoRespaldo() {
        // ARRANGE
        coEvery { repository.obtenerEstudiantes("7.º Básico A") } throws IOException("sin red")

        // ACT
        viewModel.cargarEstudiantes("7.º Básico A")

        // ASSERT
        val estudiantes = viewModel.uiState.value.estudiantes
        assertEquals(3, estudiantes.size)
        assertEquals("estudiante1", estudiantes[0].usuario)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarEstudiantesConErrorDeConexionYCursoSinRespaldoMuestraElError() {
        // ARRANGE
        coEvery { repository.obtenerEstudiantes("Curso inexistente") } throws IOException("sin red")

        // ACT
        viewModel.cargarEstudiantes("Curso inexistente")

        // ASSERT
        assertTrue(viewModel.uiState.value.estudiantes.isEmpty())
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
    }

    // ============================================================================
    // 5. Estado de carga
    // ============================================================================

    @Test
    fun cargarEstudiantesMantieneElEstadoDeCargaMientrasEsperaLaRespuesta() {
        // ARRANGE
        val compuerta = CompletableDeferred<Unit>()
        val estudiantes = listOf(
            EstudianteDto(id = 1, usuario = "estudiante10", nombre = "Ana", curso = "8.º Básico B")
        )
        coEvery { repository.obtenerEstudiantes("8.º Básico B") } coAnswers {
            compuerta.await()
            estudiantes
        }

        // ACT
        viewModel.cargarEstudiantes("8.º Básico B")

        // ASSERT — mientras espera
        assertTrue(viewModel.uiState.value.cargando)
        assertTrue(viewModel.uiState.value.estudiantes.isEmpty())

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(estudiantes, viewModel.uiState.value.estudiantes)
        assertNull(viewModel.uiState.value.error)
    }
}
