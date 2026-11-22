package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class EstudianteBitacoraViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: EstudianteBitacoraViewModel

    @Before
    fun configurar() {
        // viewModelScope usa Dispatchers.Main, que no existe en tests JVM
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
        viewModel = EstudianteBitacoraViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    // Helper privado para construir una bitácora de prueba
    private fun crearBitacora(id: Long, titulo: String): BitacoraDto {
        return BitacoraDto(
            id = id,
            docente = "docente1",
            curso = "3A",
            asignatura = "Programación",
            mes = "Octubre",
            anio = "2026",
            titulo = titulo,
            estado = "publicada"
        )
    }

    // Helper privado para construir una HttpException con el código indicado
    private fun crearHttpException(codigo: Int): HttpException {
        val body = "".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(codigo, body))
    }

    // ============================================================================
    // 1. ESTADO INICIAL
    // ============================================================================

    @Test
    fun estadoInicialValoresPorDefecto() {
        // ARRANGE — viewModel recién creado en configurar()

        // ACT
        val estado = viewModel.uiState.value

        // ASSERT
        assertFalse(estado.cargando)
        assertNull(estado.error)
        assertNull(estado.bitacora)
    }

    // ============================================================================
    // 2. cargarBitacora — carga exitosa
    // ============================================================================

    @Test
    fun cargarBitacoraExitosaGuardaLaBitacora() {
        // ARRANGE
        val bitacora = crearBitacora(5, "Bitácora Octubre")
        coEvery { repository.obtenerBitacoraPorId(any()) } returns bitacora

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals(bitacora, viewModel.uiState.value.bitacora)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacoraConsultaPorElIdIndicado() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } returns crearBitacora(7, "Bitácora Mayo")

        // ACT
        viewModel.cargarBitacora(7)

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerBitacoraPorId(7) }
    }

    @Test
    fun cargarBitacoraSegundaCargaReemplazaLaBitacoraAnterior() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(1) } returns crearBitacora(1, "Bitácora Octubre")
        coEvery { repository.obtenerBitacoraPorId(2) } returns crearBitacora(2, "Bitácora Noviembre")
        viewModel.cargarBitacora(1)

        // ACT
        viewModel.cargarBitacora(2)

        // ASSERT
        assertEquals(2L, viewModel.uiState.value.bitacora?.id)
        assertEquals("Bitácora Noviembre", viewModel.uiState.value.bitacora?.titulo)
    }

    // ============================================================================
    // 3. cargarBitacora — estado de carga
    // ============================================================================

    @Test
    fun cargarBitacoraMarcaCargandoMientrasEspera() {
        // ARRANGE — el repositorio queda suspendido hasta liberar la compuerta
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerBitacoraPorId(any()) } coAnswers {
            compuerta.await()
            crearBitacora(5, "Bitácora Octubre")
        }

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT — mientras espera
        assertTrue(viewModel.uiState.value.cargando)

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(5L, viewModel.uiState.value.bitacora?.id)
    }

    @Test
    fun cargarBitacoraLimpiaElErrorAnteriorAlComenzar() {
        // ARRANGE — primero falla y deja un error
        coEvery { repository.obtenerBitacoraPorId(any()) } throws IOException("sin red")
        viewModel.cargarBitacora(5)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerBitacoraPorId(any()) } coAnswers {
            compuerta.await()
            crearBitacora(5, "Bitácora Octubre")
        }

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT — el error previo ya no se muestra mientras carga
        assertNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.cargando)

        compuerta.complete(Unit)
    }

    // ============================================================================
    // 4. cargarBitacora — errores
    // ============================================================================

    @Test
    fun cargarBitacoraSinConexionMuestraErrorDeConexion() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.bitacora)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarBitacoraError401MuestraCredencialesIncorrectas() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws crearHttpException(401)

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals("Credenciales incorrectas", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacoraError403MuestraMensajeDeSinPermisos() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws crearHttpException(403)

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals("No tienes permisos para realizar esta acción", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacoraError404MuestraRecursoNoEncontrado() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws crearHttpException(404)

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals("El recurso solicitado no fue encontrado", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacoraError500MuestraErrorInternoDelServidor() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws crearHttpException(500)

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacoraError400SinCuerpoMuestraDatosInvalidos() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws crearHttpException(400)

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals("Datos inválidos o incompletos", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacoraExcepcionGenericaConMensajeMuestraEseMensaje() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws
                IllegalStateException("Cuerpo de respuesta vacío")

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals("Cuerpo de respuesta vacío", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacoraExcepcionGenericaSinMensajeMuestraErrorInesperado() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws RuntimeException()

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals("Ocurrió un error inesperado", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacoraConErrorMantieneLaBitacoraCargadaAnteriormente() {
        // ARRANGE — primero carga bien y luego falla
        val bitacora = crearBitacora(5, "Bitácora Octubre")
        coEvery { repository.obtenerBitacoraPorId(any()) } returns bitacora
        viewModel.cargarBitacora(5)
        coEvery { repository.obtenerBitacoraPorId(any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertEquals(bitacora, viewModel.uiState.value.bitacora)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarBitacoraExitoDespuesDeErrorLimpiaElError() {
        // ARRANGE — la primera carga falla
        coEvery { repository.obtenerBitacoraPorId(any()) } throws IOException("sin red")
        viewModel.cargarBitacora(5)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)

        val bitacora = crearBitacora(5, "Bitácora Octubre")
        coEvery { repository.obtenerBitacoraPorId(any()) } returns bitacora

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertNull(viewModel.uiState.value.error)
        assertEquals(bitacora, viewModel.uiState.value.bitacora)
    }

    // ============================================================================
    // 5. cargarBitacora — cancelación
    // ============================================================================

    @Test
    fun cargarBitacoraCancelacionNoMuestraErrorYDejaCargandoEnFalse() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws CancellationException("cancelado")

        // ACT
        viewModel.cargarBitacora(5)

        // ASSERT
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.bitacora)
        assertFalse(viewModel.uiState.value.cargando)
    }
}