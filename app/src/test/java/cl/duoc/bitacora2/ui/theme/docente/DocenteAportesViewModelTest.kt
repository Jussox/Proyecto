package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.EstudianteDto
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto
import cl.duoc.bitacora2.util.Sesion
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
class DocenteAportesViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: DocenteAportesViewModel

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Sesion.usuario = "docente1"
        repository = mockk()
        viewModel = DocenteAportesViewModel(repository)
    }

    @After
    fun limpiar() {
        Sesion.cerrar()
        Dispatchers.resetMain()
    }

    @Test
    fun estadoInicialNoEstaCargandoYNoTieneRegistrosNiErrores() {
        val estado = viewModel.uiState.value

        assertFalse(estado.cargando)
        assertTrue(estado.registros.isEmpty())
        assertTrue(estado.bitacorasMap.isEmpty())
        assertTrue(estado.estudiantesMap.isEmpty())
        assertNull(estado.error)
        assertNull(estado.errorAviso)
        assertEquals("todos", estado.filtroEstado)
    }

    @Test
    fun inicializarFiltroEstadoGuardaUnEstadoNoVacio() {
        viewModel.inicializarFiltroEstado("pendiente")

        assertEquals("pendiente", viewModel.uiState.value.filtroEstado)
    }

    @Test
    fun inicializarFiltroEstadoIgnoraUnValorEnBlanco() {
        viewModel.inicializarFiltroEstado("   ")

        assertEquals("todos", viewModel.uiState.value.filtroEstado)
    }

    @Test
    fun cambiarFiltrosActualizaLosTresValoresDelEstado() {
        viewModel.cambiarFiltroEstudiante("estudiante1")
        viewModel.cambiarFiltroFecha("2026-11-22")
        viewModel.cambiarFiltroEstado("revisado")

        val estado = viewModel.uiState.value
        assertEquals("estudiante1", estado.filtroEstudiante)
        assertEquals("2026-11-22", estado.filtroFecha)
        assertEquals("revisado", estado.filtroEstado)
    }

    @Test
    fun limpiarAvisoEliminaElAvisoActual() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns listOf(BitacoraDto(id = 1L))
        coEvery { repository.obtenerRegistrosBitacora(1L, "docente1") } throws IOException("fallo parcial")

        viewModel.cargarAportes(null)
        assertEquals("No se pudieron cargar los aportes de algunas bitácoras.", viewModel.uiState.value.errorAviso)

        viewModel.limpiarAviso()

        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarAportesDeUnaBitacoraGuardaRegistrosBitacoraYEstudiantes() {
        val registros = listOf(RegistroDto(id = 10L, estudiante = "estudiante1"))
        val estudiantes = listOf(
            EstudianteDto(usuario = "estudiante1", nombre = "Ana"),
            EstudianteDto(usuario = "", nombre = "Sin usuario"),
            EstudianteDto(usuario = "estudiante2", nombre = " "),
            EstudianteDto(usuario = null, nombre = "Sin usuario nulo"),
            EstudianteDto(usuario = "estudiante3", nombre = null)
        )
        val bitacora = BitacoraDto(id = 5L, titulo = "Bitácora noviembre")
        coEvery { repository.obtenerEstudiantes() } returns estudiantes
        coEvery { repository.obtenerRegistrosBitacora(5L, "docente1") } returns registros
        coEvery { repository.obtenerBitacoraPorId(5L) } returns bitacora

        viewModel.cargarAportes(5L)

        val estado = viewModel.uiState.value
        assertEquals(registros, estado.registros)
        assertEquals(bitacora, estado.bitacorasMap[5L])
        assertEquals(mapOf("estudiante1" to "Ana"), estado.estudiantesMap)
        assertFalse(estado.cargando)
        assertNull(estado.error)
        assertNull(estado.errorAviso)
    }

    @Test
    fun cargarAportesDeUnaBitacoraIgnoraElErrorAlObtenerLaBitacora() {
        val registros = listOf(RegistroDto(id = 11L))
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(7L, "docente1") } returns registros
        coEvery { repository.obtenerBitacoraPorId(7L) } throws IOException("bitácora no disponible")

        viewModel.cargarAportes(7L)

        assertEquals(registros, viewModel.uiState.value.registros)
        assertTrue(viewModel.uiState.value.bitacorasMap.isEmpty())
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarAportesSinIdConsultaBitacorasYAcumulaSusRegistros() {
        val bitacoras = listOf(BitacoraDto(id = 1L), BitacoraDto(id = 2L), BitacoraDto(id = null))
        val registrosUno = listOf(RegistroDto(id = 10L, bitacoraId = 1L))
        val registrosDos = listOf(RegistroDto(id = 20L, bitacoraId = 2L))
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns bitacoras
        coEvery { repository.obtenerRegistrosBitacora(1L, "docente1") } returns registrosUno
        coEvery { repository.obtenerRegistrosBitacora(2L, "docente1") } returns registrosDos

        viewModel.cargarAportes(null)

        assertEquals(registrosUno + registrosDos, viewModel.uiState.value.registros)
        assertEquals(setOf(1L, 2L), viewModel.uiState.value.bitacorasMap.keys)
        assertNull(viewModel.uiState.value.errorAviso)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarAportesSinIdConsultaComoMaximoVeinteBitacoras() {
        val bitacoras = (1L..25L).map { BitacoraDto(id = it) }
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns bitacoras
        (1L..20L).forEach { id ->
            coEvery { repository.obtenerRegistrosBitacora(id, "docente1") } returns emptyList()
        }

        viewModel.cargarAportes(null)

        assertEquals(20, viewModel.uiState.value.bitacorasMap.size)
        coVerify(exactly = 1) { repository.obtenerRegistrosBitacora(20L, "docente1") }
        coVerify(exactly = 0) { repository.obtenerRegistrosBitacora(21L, "docente1") }
    }

    @Test
    fun cargarAportesSinIdMuestraAvisoCuandoFallaLaCargaDeAlgunasBitacoras() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns
            listOf(BitacoraDto(id = 1L), BitacoraDto(id = 2L))
        coEvery { repository.obtenerRegistrosBitacora(1L, "docente1") } returns listOf(RegistroDto(id = 1L))
        coEvery { repository.obtenerRegistrosBitacora(2L, "docente1") } throws IOException("sin conexión")

        viewModel.cargarAportes(null)

        assertEquals(1, viewModel.uiState.value.registros.size)
        assertEquals("No se pudieron cargar los aportes de algunas bitácoras.", viewModel.uiState.value.errorAviso)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarAportesContinuaAunqueFalleLaCargaDeEstudiantes() {
        val registros = listOf(RegistroDto(id = 30L))
        coEvery { repository.obtenerEstudiantes() } throws IOException("estudiantes no disponibles")
        coEvery { repository.obtenerRegistrosBitacora(9L, "docente1") } returns registros
        coEvery { repository.obtenerBitacoraPorId(9L) } returns BitacoraDto(id = 9L)

        viewModel.cargarAportes(9L)

        assertEquals(registros, viewModel.uiState.value.registros)
        assertTrue(viewModel.uiState.value.estudiantesMap.isEmpty())
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarAportesConErrorPrincipalYSinRegistrosMuestraElError() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(3L, "docente1") } throws IOException("sin red")

        viewModel.cargarAportes(3L)

        assertTrue(viewModel.uiState.value.registros.isEmpty())
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarAportesConErrorPrincipalYRegistrosAnterioresMuestraUnAviso() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(4L, "docente1") } returns listOf(RegistroDto(id = 40L))
        coEvery { repository.obtenerBitacoraPorId(4L) } returns BitacoraDto(id = 4L)
        viewModel.cargarAportes(4L)

        coEvery { repository.obtenerRegistrosBitacora(6L, "docente1") } throws IOException("sin red")
        viewModel.cargarAportes(6L)

        assertEquals(1, viewModel.uiState.value.registros.size)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarAportesConIdNuloOCeroConsultaTodasLasBitacoras() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns emptyList()

        viewModel.cargarAportes(0L)

        coVerify(exactly = 1) { repository.obtenerBitacorasDocente("docente1") }
        coVerify(exactly = 0) { repository.obtenerRegistrosBitacora(any(), any()) }
    }
}
