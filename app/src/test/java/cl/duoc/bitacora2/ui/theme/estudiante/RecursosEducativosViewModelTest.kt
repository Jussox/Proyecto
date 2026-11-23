
package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.externa.RecursosEducativosRepository
import cl.duoc.bitacora2.data.externa.RecursoEducativoDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class RecursosEducativosViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: RecursosEducativosRepository

    @Before
    fun preparar() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    @Test
    fun cambiarConsulta_actualizaTextoYLimpiaError() {
        val vm = RecursosEducativosViewModel(repository)

        vm.cambiarConsulta("Sistema solar")

        assertEquals("Sistema solar", vm.uiState.value.consulta)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun buscar_conConsultaVacia_noLlamaRepositorio() =
        runTest(dispatcher) {
            val vm = RecursosEducativosViewModel(repository)

            vm.cambiarConsulta("   ")
            vm.buscar()
            advanceUntilIdle()

            coVerify(exactly = 0) {
                repository.buscarPorTema(any())
            }

            assertFalse(vm.uiState.value.cargando)
        }

    @Test
    fun buscar_conResultados_actualizaEstado() =
        runTest(dispatcher) {
            val recurso = mockk<RecursoEducativoDto>()

            coEvery {
                repository.buscarPorTema("célula")
            } returns listOf(recurso)

            val vm = RecursosEducativosViewModel(repository)

            vm.cambiarConsulta(" célula ")
            vm.buscar()
            advanceUntilIdle()

            assertFalse(vm.uiState.value.cargando)
            assertTrue(vm.uiState.value.busquedaRealizada)
            assertEquals(listOf(recurso), vm.uiState.value.recursos)
            assertNull(vm.uiState.value.error)

            coVerify(exactly = 1) {
                repository.buscarPorTema("célula")
            }
        }

    @Test
    fun buscar_siYaEstaCargando_noIniciaOtraConsulta() =
        runTest(dispatcher) {
            val pendiente = CompletableDeferred<List<RecursoEducativoDto>>()

            coEvery {
                repository.buscarPorTema("planetas")
            } coAnswers {
                pendiente.await()
            }

            val vm = RecursosEducativosViewModel(repository)

            vm.cambiarConsulta("planetas")
            vm.buscar()

            testScheduler.runCurrent()

            assertTrue(vm.uiState.value.cargando)

            vm.buscar()

            coVerify(exactly = 1) {
                repository.buscarPorTema("planetas")
            }

            pendiente.complete(emptyList())
            advanceUntilIdle()

            assertFalse(vm.uiState.value.cargando)
        }

    @Test
    fun buscar_siHayErrorDeRed_muestraMensajeDeConexion() =
        runTest(dispatcher) {
            coEvery {
                repository.buscarPorTema("agua")
            } throws IOException("sin red")

            val vm = RecursosEducativosViewModel(repository)

            vm.cambiarConsulta("agua")
            vm.buscar()
            advanceUntilIdle()

            assertFalse(vm.uiState.value.cargando)

            assertEquals(
                "No fue posible conectar con Wikipedia. Revisa tu conexión a Internet.",
                vm.uiState.value.error
            )
        }

    @Test
    fun buscar_siHayOtroError_muestraMensajeDeLaExcepcion() =
        runTest(dispatcher) {
            coEvery {
                repository.buscarPorTema("tierra")
            } throws IllegalStateException("falló consulta")

            val vm = RecursosEducativosViewModel(repository)

            vm.cambiarConsulta("tierra")
            vm.buscar()
            advanceUntilIdle()

            assertFalse(vm.uiState.value.cargando)
            assertEquals("falló consulta", vm.uiState.value.error)
        }

    @Test
    fun buscar_usaMensajePredeterminadoCuandoExcepcionNoTieneMensaje() =
        runTest(dispatcher) {
            val errorSinMensaje = Exception()

            coEvery {
                repository.buscarPorTema("sistema solar")
            } throws errorSinMensaje

            val vm = RecursosEducativosViewModel(repository)

            vm.cambiarConsulta("sistema solar")
            vm.buscar()
            advanceUntilIdle()

            assertEquals(
                "No se pudieron obtener recursos para este tema.",
                vm.uiState.value.error
            )

            assertFalse(vm.uiState.value.cargando)
        }
}
