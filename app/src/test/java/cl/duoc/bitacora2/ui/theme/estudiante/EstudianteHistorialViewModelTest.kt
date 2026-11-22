package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto
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
class EstudianteHistorialViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: EstudianteHistorialViewModel

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
        viewModel = EstudianteHistorialViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    // Helper privado para construir un registro de prueba
    private fun crearRegistro(id: Long, bitacoraId: Long = 1): RegistroDto {
        return RegistroDto(
            id = id,
            bitacoraId = bitacoraId,
            estudiante = "estudiante1",
            fechaClase = "10/10/2026",
            contenido = "Contenido $id",
            actividad = "Actividad $id",
            aprendizaje = "Aprendizaje $id",
            estado = "borrador"
        )
    }

    // Helper privado para construir una bitácora de prueba (el id puede ser null)
    private fun crearBitacora(id: Long?, titulo: String): BitacoraDto {
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

    // Helper privado para dejar al repositorio respondiendo bien con los datos indicados
    private fun stubCargaExitosa(registros: List<RegistroDto>, bitacoras: List<BitacoraDto>) {
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns registros
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns bitacoras
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
        assertTrue(estado.registros.isEmpty())
        assertTrue(estado.bitacorasMap.isEmpty())
        assertNull(estado.idPendienteEliminar)
        assertNull(estado.eliminandoId)
    }

    // ============================================================================
    // 2. cargarHistorial — carga exitosa
    // ============================================================================

    @Test
    fun cargarHistorialExitosoGuardaRegistrosYBitacoras() {
        // ARRANGE
        val registros = listOf(crearRegistro(1), crearRegistro(2))
        val bitacoras = listOf(crearBitacora(1, "Bitácora Octubre"))
        stubCargaExitosa(registros, bitacoras)

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals(registros, viewModel.uiState.value.registros)
        assertEquals(1, viewModel.uiState.value.bitacorasMap.size)
        assertEquals("Bitácora Octubre", viewModel.uiState.value.bitacorasMap[1L]?.titulo)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarHistorialIndexaLasBitacorasPorSuId() {
        // ARRANGE
        val bitacoras = listOf(
            crearBitacora(10, "Bitácora Octubre"),
            crearBitacora(20, "Bitácora Noviembre")
        )
        stubCargaExitosa(listOf(crearRegistro(1)), bitacoras)

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals(setOf(10L, 20L), viewModel.uiState.value.bitacorasMap.keys)
        assertEquals("Bitácora Noviembre", viewModel.uiState.value.bitacorasMap[20L]?.titulo)
    }

    @Test
    fun cargarHistorialIgnoraLasBitacorasSinId() {
        // ARRANGE
        val bitacoras = listOf(
            crearBitacora(null, "Sin id"),
            crearBitacora(1, "Bitácora Octubre")
        )
        stubCargaExitosa(listOf(crearRegistro(1)), bitacoras)

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals(1, viewModel.uiState.value.bitacorasMap.size)
        assertEquals("Bitácora Octubre", viewModel.uiState.value.bitacorasMap[1L]?.titulo)
    }

    @Test
    fun cargarHistorialSinDatosDejaRegistrosYBitacorasVacios() {
        // ARRANGE
        stubCargaExitosa(emptyList(), emptyList())

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertTrue(viewModel.uiState.value.registros.isEmpty())
        assertTrue(viewModel.uiState.value.bitacorasMap.isEmpty())
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarHistorialConsultaPorElUsuarioDeLaSesion() {
        // ARRANGE
        stubCargaExitosa(emptyList(), emptyList())

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerRegistrosEstudiante("estudiante1", any()) }
        coVerify(exactly = 1) { repository.obtenerBitacorasEstudiante("estudiante1") }
    }

    // ============================================================================
    // 3. cargarHistorial — estado de carga
    // ============================================================================

    @Test
    fun cargarHistorialPrimeraCargaMarcaCargandoMientrasEspera() {
        // ARRANGE — el repositorio queda suspendido hasta liberar la compuerta
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } coAnswers {
            compuerta.await()
            listOf(crearRegistro(1))
        }
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns emptyList()

        // ACT
        viewModel.cargarHistorial()

        // ASSERT — mientras espera
        assertTrue(viewModel.uiState.value.cargando)

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(1, viewModel.uiState.value.registros.size)
    }

    @Test
    fun cargarHistorialRefrescoConRegistrosNoMarcaCargando() {
        // ARRANGE — ya existe una lista cargada
        stubCargaExitosa(listOf(crearRegistro(1)), emptyList())
        viewModel.cargarHistorial()

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } coAnswers {
            compuerta.await()
            listOf(crearRegistro(1))
        }

        // ACT
        viewModel.cargarHistorial()

        // ASSERT — el refresco no muestra spinner a pantalla completa
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(1, viewModel.uiState.value.registros.size)

        compuerta.complete(Unit)
    }

    @Test
    fun cargarHistorialPrimeraCargaLimpiaElErrorAnteriorAlComenzar() {
        // ARRANGE — primero falla y deja un error en pantalla completa
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarHistorial()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } coAnswers {
            compuerta.await()
            emptyList()
        }

        // ACT
        viewModel.cargarHistorial()

        // ASSERT — el error previo ya no se muestra mientras carga
        assertNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.cargando)

        compuerta.complete(Unit)
    }

    // ============================================================================
    // 4. cargarHistorial — error en la primera carga (sin registros)
    // ============================================================================

    @Test
    fun cargarHistorialSinConexionMuestraErrorDePantallaCompleta() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
        assertTrue(viewModel.uiState.value.registros.isEmpty())
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarHistorialErrorEnRegistrosNoConsultaLasBitacoras() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        coVerify(exactly = 0) { repository.obtenerBitacorasEstudiante(any()) }
    }

    @Test
    fun cargarHistorialErrorEnBitacorasNoGuardaLosRegistros() {
        // ARRANGE — los registros llegan bien pero las bitácoras fallan
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns listOf(crearRegistro(1))
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws crearHttpException(500)

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.registros.isEmpty())
    }

    @Test
    fun cargarHistorialError401MuestraCredencialesIncorrectas() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws crearHttpException(401)

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals("Credenciales incorrectas", viewModel.uiState.value.error)
    }

    @Test
    fun cargarHistorialError403MuestraMensajeDeSinPermisos() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws crearHttpException(403)

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals("No tienes permisos para realizar esta acción", viewModel.uiState.value.error)
    }

    @Test
    fun cargarHistorialError404MuestraRecursoNoEncontrado() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws crearHttpException(404)

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals("El recurso solicitado no fue encontrado", viewModel.uiState.value.error)
    }

    @Test
    fun cargarHistorialError400SinCuerpoMuestraDatosInvalidos() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws crearHttpException(400)

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals("Datos inválidos o incompletos", viewModel.uiState.value.error)
    }

    @Test
    fun cargarHistorialExcepcionGenericaConMensajeMuestraEseMensaje() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws
                IllegalStateException("Cuerpo de respuesta vacío")

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals("Cuerpo de respuesta vacío", viewModel.uiState.value.error)
    }

    @Test
    fun cargarHistorialExcepcionGenericaSinMensajeMuestraErrorInesperado() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws RuntimeException()

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals("Ocurrió un error inesperado", viewModel.uiState.value.error)
    }

    // ============================================================================
    // 5. cargarHistorial — refresco con registros existentes
    // ============================================================================

    @Test
    fun cargarHistorialRefrescoExitosoReemplazaLosRegistros() {
        // ARRANGE
        stubCargaExitosa(listOf(crearRegistro(1)), emptyList())
        viewModel.cargarHistorial()

        val registrosNuevos = listOf(crearRegistro(1), crearRegistro(2))
        stubCargaExitosa(registrosNuevos, emptyList())

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals(registrosNuevos, viewModel.uiState.value.registros)
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarHistorialRefrescoConErrorMantieneLosRegistrosYMuestraAviso() {
        // ARRANGE
        val registros = listOf(crearRegistro(1))
        stubCargaExitosa(registros, emptyList())
        viewModel.cargarHistorial()

        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertEquals(registros, viewModel.uiState.value.registros)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarHistorialRefrescoExitosoLimpiaElAvisoAnterior() {
        // ARRANGE — lista cargada y luego un refresco fallido que deja un aviso
        stubCargaExitosa(listOf(crearRegistro(1)), emptyList())
        viewModel.cargarHistorial()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarHistorial()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)

        stubCargaExitosa(listOf(crearRegistro(1)), emptyList())

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarHistorialRefrescoLimpiaElAvisoAlComenzar() {
        // ARRANGE — lista cargada y aviso pendiente de un refresco fallido
        stubCargaExitosa(listOf(crearRegistro(1)), emptyList())
        viewModel.cargarHistorial()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarHistorial()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } coAnswers {
            compuerta.await()
            listOf(crearRegistro(1))
        }

        // ACT
        viewModel.cargarHistorial()

        // ASSERT — el aviso previo desaparece mientras se refresca
        assertNull(viewModel.uiState.value.errorAviso)

        compuerta.complete(Unit)
    }

    @Test
    fun cargarHistorialExitoDespuesDeErrorLimpiaElErrorDePantallaCompleta() {
        // ARRANGE — la primera carga falla
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarHistorial()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)

        val registros = listOf(crearRegistro(1))
        stubCargaExitosa(registros, emptyList())

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertNull(viewModel.uiState.value.error)
        assertEquals(registros, viewModel.uiState.value.registros)
    }

    // ============================================================================
    // 6. cargarHistorial — cancelación
    // ============================================================================

    @Test
    fun cargarHistorialCancelacionNoMuestraErrorYDejaCargandoEnFalse() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws
                CancellationException("cancelado")

        // ACT
        viewModel.cargarHistorial()

        // ASSERT
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
        assertFalse(viewModel.uiState.value.cargando)
        assertTrue(viewModel.uiState.value.registros.isEmpty())
    }

    // ============================================================================
    // 7. mostrarDialogoEliminar / ocultarDialogoEliminar
    // ============================================================================

    @Test
    fun mostrarDialogoEliminarGuardaElIdPendiente() {
        // ARRANGE
        val id = 3L

        // ACT
        viewModel.mostrarDialogoEliminar(id)

        // ASSERT
        assertEquals(3L, viewModel.uiState.value.idPendienteEliminar)
    }

    @Test
    fun mostrarDialogoEliminarConOtroIdReemplazaElAnterior() {
        // ARRANGE
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.mostrarDialogoEliminar(8)

        // ASSERT
        assertEquals(8L, viewModel.uiState.value.idPendienteEliminar)
    }

    @Test
    fun ocultarDialogoEliminarLimpiaElIdPendiente() {
        // ARRANGE
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.ocultarDialogoEliminar()

        // ASSERT
        assertNull(viewModel.uiState.value.idPendienteEliminar)
    }

    // ============================================================================
    // 8. confirmarEliminar
    // ============================================================================

    @Test
    fun confirmarEliminarSinIdPendienteNoHaceNada() {
        // ARRANGE — no se abrió ningún diálogo

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        coVerify(exactly = 0) { repository.eliminarRegistro(any(), any()) }
        assertNull(viewModel.uiState.value.eliminandoId)
    }

    @Test
    fun confirmarEliminarExitosoEliminaConElIdYElUsuarioDeLaSesion() {
        // ARRANGE
        coEvery { repository.eliminarRegistro(any(), any()) } returns Unit
        stubCargaExitosa(emptyList(), emptyList())
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        coVerify(exactly = 1) { repository.eliminarRegistro(3, "estudiante1") }
    }

    @Test
    fun confirmarEliminarExitosoLimpiaElDialogoYElIdEnEliminacion() {
        // ARRANGE
        coEvery { repository.eliminarRegistro(any(), any()) } returns Unit
        stubCargaExitosa(emptyList(), emptyList())
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        assertNull(viewModel.uiState.value.idPendienteEliminar)
        assertNull(viewModel.uiState.value.eliminandoId)
    }

    @Test
    fun confirmarEliminarExitosoRecargaElHistorial() {
        // ARRANGE — la lista tenía dos registros y el servidor devuelve solo uno
        stubCargaExitosa(listOf(crearRegistro(1), crearRegistro(3)), emptyList())
        viewModel.cargarHistorial()
        coEvery { repository.eliminarRegistro(any(), any()) } returns Unit
        stubCargaExitosa(listOf(crearRegistro(1)), emptyList())
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        assertEquals(1, viewModel.uiState.value.registros.size)
        assertEquals(1L, viewModel.uiState.value.registros[0].id)
        coVerify(exactly = 2) { repository.obtenerRegistrosEstudiante(any(), any()) }
    }

    @Test
    fun confirmarEliminarMarcaEliminandoIdMientrasEspera() {
        // ARRANGE — la eliminación queda suspendida hasta liberar la compuerta
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.eliminarRegistro(any(), any()) } coAnswers { compuerta.await() }
        stubCargaExitosa(emptyList(), emptyList())
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT — mientras espera
        assertEquals(3L, viewModel.uiState.value.eliminandoId)

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertNull(viewModel.uiState.value.eliminandoId)
    }

    @Test
    fun confirmarEliminarIgnoraUnSegundoIntentoMientrasEliminaOtroRegistro() {
        // ARRANGE
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.eliminarRegistro(any(), any()) } coAnswers { compuerta.await() }
        stubCargaExitosa(emptyList(), emptyList())
        viewModel.mostrarDialogoEliminar(3)
        viewModel.confirmarEliminar()

        // ACT — se abre otro diálogo y se intenta confirmar mientras el primero sigue en curso
        viewModel.mostrarDialogoEliminar(8)
        viewModel.confirmarEliminar()
        compuerta.complete(Unit)

        // ASSERT
        coVerify(exactly = 1) { repository.eliminarRegistro(any(), any()) }
        coVerify(exactly = 1) { repository.eliminarRegistro(3, "estudiante1") }
    }

    @Test
    fun confirmarEliminarConErrorMuestraAvisoYLimpiaElEstadoDeEliminacion() {
        // ARRANGE
        coEvery { repository.eliminarRegistro(any(), any()) } throws crearHttpException(403)
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        assertEquals(
            "No tienes permisos para realizar esta acción",
            viewModel.uiState.value.errorAviso
        )
        assertNull(viewModel.uiState.value.eliminandoId)
        assertNull(viewModel.uiState.value.idPendienteEliminar)
    }

    @Test
    fun confirmarEliminarSinConexionMuestraAvisoDeConexion() {
        // ARRANGE
        coEvery { repository.eliminarRegistro(any(), any()) } throws IOException("sin red")
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)
    }

    @Test
    fun confirmarEliminarConErrorNoRecargaElHistorial() {
        // ARRANGE
        coEvery { repository.eliminarRegistro(any(), any()) } throws crearHttpException(500)
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        coVerify(exactly = 0) { repository.obtenerRegistrosEstudiante(any(), any()) }
    }

    @Test
    fun confirmarEliminarConErrorMantieneLosRegistrosCargados() {
        // ARRANGE
        val registros = listOf(crearRegistro(1), crearRegistro(3))
        stubCargaExitosa(registros, emptyList())
        viewModel.cargarHistorial()
        coEvery { repository.eliminarRegistro(any(), any()) } throws crearHttpException(500)
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        assertEquals(registros, viewModel.uiState.value.registros)
        assertEquals("Error interno del servidor", viewModel.uiState.value.errorAviso)
    }

    @Test
    fun confirmarEliminarExitosoPeroRecargaFallidaMuestraAvisoYMantieneLosRegistros() {
        // ARRANGE — la eliminación funciona pero la recarga posterior falla
        val registros = listOf(crearRegistro(1), crearRegistro(3))
        stubCargaExitosa(registros, emptyList())
        viewModel.cargarHistorial()
        coEvery { repository.eliminarRegistro(any(), any()) } returns Unit
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        assertEquals(registros, viewModel.uiState.value.registros)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)
        assertNull(viewModel.uiState.value.eliminandoId)
        assertNull(viewModel.uiState.value.idPendienteEliminar)
    }

    @Test
    fun confirmarEliminarCancelacionNoMuestraAvisoNiRecarga() {
        // ARRANGE
        coEvery { repository.eliminarRegistro(any(), any()) } throws CancellationException("cancelado")
        viewModel.mostrarDialogoEliminar(3)

        // ACT
        viewModel.confirmarEliminar()

        // ASSERT
        assertNull(viewModel.uiState.value.errorAviso)
        coVerify(exactly = 0) { repository.obtenerRegistrosEstudiante(any(), any()) }
    }

    // ============================================================================
    // 9. limpiarAviso
    // ============================================================================

    @Test
    fun limpiarAvisoQuitaElAvisoYMantieneLosRegistros() {
        // ARRANGE — lista cargada y un aviso por refresco fallido
        val registros = listOf(crearRegistro(1))
        stubCargaExitosa(registros, emptyList())
        viewModel.cargarHistorial()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarHistorial()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)

        // ACT
        viewModel.limpiarAviso()

        // ASSERT
        assertNull(viewModel.uiState.value.errorAviso)
        assertEquals(registros, viewModel.uiState.value.registros)
    }

    @Test
    fun limpiarAvisoNoModificaElErrorDePantallaCompleta() {
        // ARRANGE — la primera carga falla y deja un error de pantalla completa
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarHistorial()

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