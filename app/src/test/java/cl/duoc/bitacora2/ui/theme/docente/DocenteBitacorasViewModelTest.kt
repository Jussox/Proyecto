package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.util.Sesion
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
class DocenteBitacorasViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: DocenteBitacorasViewModel

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Sesion.cerrar()
        Sesion.iniciar(
            usuario = "docente1",
            nombre = "Carlos Pérez",
            perfil = "docente",
            curso = "3A"
        )
        repository = mockk()
        viewModel = DocenteBitacorasViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    private fun crearBitacora(
        id: Long = 1,
        estado: String = "borrador",
        titulo: String = "Bitácora de prueba"
    ): BitacoraDto {
        return BitacoraDto(
            id = id,
            docente = "docente1",
            curso = "3A",
            asignatura = "Programación",
            mes = "Enero",
            anio = "2026",
            titulo = titulo,
            estado = estado
        )
    }

    // ============================================================================
    // 1. Estado inicial
    // ============================================================================

    @Test
    fun estadoInicialNoEstaCargandoYNoTieneBitacorasNiErrores() {
        // ARRANGE — viewModel recién creado en configurar()

        // ACT
        val estado = viewModel.uiState.value

        // ASSERT
        assertFalse(estado.cargando)
        assertTrue(estado.bitacoras.isEmpty())
        assertNull(estado.error)
        assertNull(estado.accionEnProgresoId)
        assertNull(estado.errorAccion)
    }

    // ============================================================================
    // 2. cargarBitacoras
    // ============================================================================

    @Test
    fun cargarBitacorasObtieneYGuardaLasBitacorasDelDocente() {
        // ARRANGE
        val bitacoras = listOf(crearBitacora(id = 1), crearBitacora(id = 2, titulo = "Segunda"))
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns bitacoras

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals(bitacoras, viewModel.uiState.value.bitacoras)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
        coVerify(exactly = 1) { repository.obtenerBitacorasDocente("docente1") }
    }

    @Test
    fun cargarBitacorasConsultaElUsuarioIndicado() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasDocente("docente2") } returns emptyList()

        // ACT
        viewModel.cargarBitacoras("docente2")

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerBitacorasDocente("docente2") }
    }

    @Test
    fun cargarBitacorasConUsuarioVacioNoConsultaAlRepositorio() {
        // ARRANGE

        // ACT
        viewModel.cargarBitacoras("   ")

        // ASSERT
        assertFalse(viewModel.uiState.value.cargando)
        coVerify(exactly = 0) { repository.obtenerBitacorasDocente(any()) }
    }

    @Test
    fun cargarBitacorasConErrorMuestraMensajeYFinalizaLaCarga() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasDocente("docente1") } throws IOException("sin red")

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarBitacorasMarcaCargandoMientrasEsperaLaRespuesta() {
        // ARRANGE
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerBitacorasDocente("docente1") } coAnswers {
            compuerta.await()
            listOf(crearBitacora())
        }

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT — mientras espera
        assertTrue(viewModel.uiState.value.cargando)

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(1, viewModel.uiState.value.bitacoras.size)
    }

    // ============================================================================
    // 3. publicarBitacora
    // ============================================================================

    @Test
    fun publicarBitacoraPublicaYActualizaLaLista() {
        // ARRANGE
        val bitacorasActualizadas = listOf(crearBitacora(estado = "publicada"))
        coEvery { repository.publicarBitacora(1, "docente1") } returns crearBitacora(estado = "publicada")
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns bitacorasActualizadas

        // ACT
        viewModel.publicarBitacora(1)

        // ASSERT
        coVerify(exactly = 1) { repository.publicarBitacora(1, "docente1") }
        coVerify(exactly = 1) { repository.obtenerBitacorasDocente("docente1") }
        assertEquals(bitacorasActualizadas, viewModel.uiState.value.bitacoras)
        assertNull(viewModel.uiState.value.accionEnProgresoId)
        assertNull(viewModel.uiState.value.errorAccion)
    }

    @Test
    fun publicarBitacoraConErrorMuestraMensajeYFinalizaLaAccion() {
        // ARRANGE
        coEvery { repository.publicarBitacora(1, "docente1") } throws IOException("sin red")

        // ACT
        viewModel.publicarBitacora(1)

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAccion)
        assertNull(viewModel.uiState.value.accionEnProgresoId)
        coVerify(exactly = 0) { repository.obtenerBitacorasDocente(any()) }
    }

    @Test
    fun publicarBitacoraMarcaElIdComoAccionEnProgresoMientrasEspera() {
        // ARRANGE
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.publicarBitacora(7, "docente1") } coAnswers {
            compuerta.await()
            crearBitacora(id = 7, estado = "publicada")
        }
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns emptyList()

        // ACT
        viewModel.publicarBitacora(7)

        // ASSERT — mientras espera
        assertEquals(7L, viewModel.uiState.value.accionEnProgresoId)

        // ACT — se completa la publicación
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertNull(viewModel.uiState.value.accionEnProgresoId)
    }

    // ============================================================================
    // 4. cerrarBitacora
    // ============================================================================

    @Test
    fun cerrarBitacoraCierraYActualizaLaLista() {
        // ARRANGE
        val bitacorasActualizadas = listOf(crearBitacora(estado = "cerrada"))
        coEvery { repository.cerrarBitacora(1, "docente1") } returns crearBitacora(estado = "cerrada")
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns bitacorasActualizadas

        // ACT
        viewModel.cerrarBitacora(1)

        // ASSERT
        coVerify(exactly = 1) { repository.cerrarBitacora(1, "docente1") }
        coVerify(exactly = 1) { repository.obtenerBitacorasDocente("docente1") }
        assertEquals(bitacorasActualizadas, viewModel.uiState.value.bitacoras)
        assertNull(viewModel.uiState.value.accionEnProgresoId)
        assertNull(viewModel.uiState.value.errorAccion)
    }

    @Test
    fun cerrarBitacoraConErrorMuestraMensajeYFinalizaLaAccion() {
        // ARRANGE
        coEvery { repository.cerrarBitacora(1, "docente1") } throws IOException("sin red")

        // ACT
        viewModel.cerrarBitacora(1)

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAccion)
        assertNull(viewModel.uiState.value.accionEnProgresoId)
        coVerify(exactly = 0) { repository.obtenerBitacorasDocente(any()) }
    }

    @Test
    fun cerrarBitacoraMarcaElIdComoAccionEnProgresoMientrasEspera() {
        // ARRANGE
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.cerrarBitacora(8, "docente1") } coAnswers {
            compuerta.await()
            crearBitacora(id = 8, estado = "cerrada")
        }
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns emptyList()

        // ACT
        viewModel.cerrarBitacora(8)

        // ASSERT — mientras espera
        assertEquals(8L, viewModel.uiState.value.accionEnProgresoId)

        // ACT — se completa el cierre
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertNull(viewModel.uiState.value.accionEnProgresoId)
    }

    // ============================================================================
    // 5. limpiarErrorAccion
    // ============================================================================

    @Test
    fun limpiarErrorAccionEliminaElMensajeDeError() {
        // ARRANGE
        coEvery { repository.publicarBitacora(1, "docente1") } throws IOException("sin red")
        viewModel.publicarBitacora(1)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAccion)

        // ACT
        viewModel.limpiarErrorAccion()

        // ASSERT
        assertNull(viewModel.uiState.value.errorAccion)
    }
}
