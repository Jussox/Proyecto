package cl.duoc.bitacora2.ui.theme.Estudiante

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
class EstudianteBitacorasViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: EstudianteBitacorasViewModel

    @Before
    fun configurar() {
        // viewModelScope usa Dispatchers.Main, que no existe en tests JVM
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Sesion.cerrar()
        Sesion.iniciar(
            usuario = "estudiante1",
            nombre = "Ana Pérez",
            perfil = "estudiante",
            curso = "3A"
        )
        repository = mockk()
        viewModel = EstudianteBitacorasViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
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
        assertNull(estado.errorAviso)
        assertTrue(estado.bitacoras.isEmpty())
    }

    // ============================================================================
    // 2. cargarBitacoras — primera carga exitosa
    // ============================================================================

    @Test
    fun cargarBitacorasPrimeraCargaExitosaGuardaLaLista() {
        // ARRANGE
        val lista = listOf(crearBitacora(1, "Bitácora Octubre"), crearBitacora(2, "Bitácora Noviembre"))
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns lista

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals(lista, viewModel.uiState.value.bitacoras)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarBitacorasListaVaciaDelServidorDejaLaListaVacia() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns emptyList()

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertTrue(viewModel.uiState.value.bitacoras.isEmpty())
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacorasConsultaPorElUsuarioDeLaSesion() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns emptyList()

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerBitacorasEstudiante("estudiante1") }
    }

    @Test
    fun cargarBitacorasConSesionVaciaConsultaConUsuarioVacio() {
        // ARRANGE
        Sesion.cerrar()
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns emptyList()

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerBitacorasEstudiante("") }
    }

    // ============================================================================
    // 3. cargarBitacoras — estado de carga
    // ============================================================================

    @Test
    fun cargarBitacorasPrimeraCargaMarcaCargandoMientrasEspera() {
        // ARRANGE — el repositorio queda suspendido hasta liberar la compuerta
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerBitacorasEstudiante(any()) } coAnswers {
            compuerta.await()
            listOf(crearBitacora(1, "Bitácora Octubre"))
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

    @Test
    fun cargarBitacorasRefrescoConListaNoMarcaCargando() {
        // ARRANGE — ya existe una lista cargada
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns
                listOf(crearBitacora(1, "Bitácora Octubre"))
        viewModel.cargarBitacoras()

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerBitacorasEstudiante(any()) } coAnswers {
            compuerta.await()
            listOf(crearBitacora(1, "Bitácora Octubre"))
        }

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT — el refresco no muestra spinner a pantalla completa
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(1, viewModel.uiState.value.bitacoras.size)

        compuerta.complete(Unit)
    }

    @Test
    fun cargarBitacorasPrimeraCargaLimpiaElErrorAnteriorAlComenzar() {
        // ARRANGE — primero falla y deja un error en pantalla completa
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws IOException("sin red")
        viewModel.cargarBitacoras()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerBitacorasEstudiante(any()) } coAnswers {
            compuerta.await()
            emptyList()
        }

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT — el error previo ya no se muestra mientras carga
        assertNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.cargando)

        compuerta.complete(Unit)
    }

    // ============================================================================
    // 4. cargarBitacoras — error en la primera carga (sin lista)
    // ============================================================================

    @Test
    fun cargarBitacorasSinConexionMuestraErrorDePantallaCompleta() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
        assertTrue(viewModel.uiState.value.bitacoras.isEmpty())
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarBitacorasError401MuestraCredencialesIncorrectas() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws crearHttpException(401)

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("Credenciales incorrectas", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacorasError403MuestraMensajeDeSinPermisos() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws crearHttpException(403)

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("No tienes permisos para realizar esta acción", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacorasError404MuestraRecursoNoEncontrado() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws crearHttpException(404)

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("El recurso solicitado no fue encontrado", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacorasError500MuestraErrorInternoDelServidor() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws crearHttpException(500)

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacorasError400SinCuerpoMuestraDatosInvalidos() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws crearHttpException(400)

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("Datos inválidos o incompletos", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacorasExcepcionGenericaConMensajeMuestraEseMensaje() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws
                IllegalStateException("Cuerpo de respuesta vacío")

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("Cuerpo de respuesta vacío", viewModel.uiState.value.error)
    }

    @Test
    fun cargarBitacorasExcepcionGenericaSinMensajeMuestraErrorInesperado() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws RuntimeException()

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("Ocurrió un error inesperado", viewModel.uiState.value.error)
    }

    // ============================================================================
    // 5. cargarBitacoras — refresco con lista existente
    // ============================================================================

    @Test
    fun cargarBitacorasRefrescoExitosoReemplazaLaLista() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns
                listOf(crearBitacora(1, "Bitácora Octubre"))
        viewModel.cargarBitacoras()

        val listaNueva = listOf(crearBitacora(1, "Bitácora Octubre"), crearBitacora(2, "Bitácora Noviembre"))
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns listaNueva

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals(listaNueva, viewModel.uiState.value.bitacoras)
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarBitacorasRefrescoConErrorMantieneLaListaYMuestraAviso() {
        // ARRANGE
        val lista = listOf(crearBitacora(1, "Bitácora Octubre"))
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns lista
        viewModel.cargarBitacoras()

        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals(lista, viewModel.uiState.value.bitacoras)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarBitacorasRefrescoExitosoLimpiaElAvisoAnterior() {
        // ARRANGE — lista cargada y luego un refresco fallido que deja un aviso
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns
                listOf(crearBitacora(1, "Bitácora Octubre"))
        viewModel.cargarBitacoras()
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws IOException("sin red")
        viewModel.cargarBitacoras()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)

        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns
                listOf(crearBitacora(1, "Bitácora Octubre"))

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarBitacorasRefrescoLimpiaElAvisoAlComenzar() {
        // ARRANGE — lista cargada y aviso pendiente de un refresco fallido
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns
                listOf(crearBitacora(1, "Bitácora Octubre"))
        viewModel.cargarBitacoras()
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws IOException("sin red")
        viewModel.cargarBitacoras()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerBitacorasEstudiante(any()) } coAnswers {
            compuerta.await()
            listOf(crearBitacora(1, "Bitácora Octubre"))
        }

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT — el aviso previo desaparece mientras se refresca
        assertNull(viewModel.uiState.value.errorAviso)

        compuerta.complete(Unit)
    }

    @Test
    fun cargarBitacorasConListaVaciaYErrorUsaPantallaCompletaYNoAviso() {
        // ARRANGE — el servidor responde vacío y luego falla
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns emptyList()
        viewModel.cargarBitacoras()
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws crearHttpException(500)

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarBitacorasExitoDespuesDeErrorLimpiaElErrorDePantallaCompleta() {
        // ARRANGE — la primera carga falla
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws IOException("sin red")
        viewModel.cargarBitacoras()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)

        val lista = listOf(crearBitacora(1, "Bitácora Octubre"))
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns lista

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertNull(viewModel.uiState.value.error)
        assertEquals(lista, viewModel.uiState.value.bitacoras)
    }

    // ============================================================================
    // 6. cargarBitacoras — cancelación
    // ============================================================================

    @Test
    fun cargarBitacorasCancelacionNoMuestraErrorYDejaCargandoEnFalse() {
        // ARRANGE
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws CancellationException("cancelado")

        // ACT
        viewModel.cargarBitacoras()

        // ASSERT
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
        assertFalse(viewModel.uiState.value.cargando)
        assertTrue(viewModel.uiState.value.bitacoras.isEmpty())
    }

    // ============================================================================
    // 7. limpiarAviso
    // ============================================================================

    @Test
    fun limpiarAvisoQuitaElAvisoYMantieneLaLista() {
        // ARRANGE — lista cargada y un aviso por refresco fallido
        val lista = listOf(crearBitacora(1, "Bitácora Octubre"))
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns lista
        viewModel.cargarBitacoras()
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws IOException("sin red")
        viewModel.cargarBitacoras()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)

        // ACT
        viewModel.limpiarAviso()

        // ASSERT
        assertNull(viewModel.uiState.value.errorAviso)
        assertEquals(lista, viewModel.uiState.value.bitacoras)
    }

    @Test
    fun limpiarAvisoNoModificaElErrorDePantallaCompleta() {
        // ARRANGE — la primera carga falla y deja un error de pantalla completa
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws IOException("sin red")
        viewModel.cargarBitacoras()

        // ACT
        viewModel.limpiarAviso()

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
    }

    @Test
    fun limpiarAvisoSinAvisoNoCambiaElEstado() {
        // ARRANGE
        val estadoAntes = viewModel.uiState.value

        // ACT
        viewModel.limpiarAviso()

        // ASSERT
        assertEquals(estadoAntes, viewModel.uiState.value)
    }
}