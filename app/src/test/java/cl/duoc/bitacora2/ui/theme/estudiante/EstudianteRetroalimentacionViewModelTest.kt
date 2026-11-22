package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.DocenteDto
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
class EstudianteRetroalimentacionViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: EstudianteRetroalimentacionViewModel

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
        viewModel = EstudianteRetroalimentacionViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    // Helper privado para construir un registro con retroalimentación
    private fun crearRegistro(
        id: Long,
        estado: String? = "revisado",
        comentario: String? = "Buen trabajo",
        fechaRetroalimentacion: String? = "2026-10-10"
    ): RegistroDto {
        return RegistroDto(
            id = id,
            bitacoraId = 1,
            estudiante = "estudiante1",
            fechaClase = "10/10/2026",
            contenido = "Contenido $id",
            estado = estado,
            comentario = comentario,
            fechaRetroalimentacion = fechaRetroalimentacion
        )
    }

    // Helper privado para construir una bitácora de prueba (el id puede ser null)
    private fun crearBitacora(id: Long?, titulo: String): BitacoraDto {
        return BitacoraDto(
            id = id,
            docente = "docente1",
            curso = "3A",
            asignatura = "Programación",
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
    private fun stubCargaExitosa(
        registros: List<RegistroDto>,
        bitacoras: List<BitacoraDto> = emptyList(),
        docentes: List<DocenteDto> = emptyList()
    ) {
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns registros
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns bitacoras
        coEvery { repository.obtenerDocentes() } returns docentes
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
        assertTrue(estado.docentesMap.isEmpty())
    }

    // ============================================================================
    // 2. cargarRetroalimentacion — carga exitosa
    // ============================================================================

    @Test
    fun cargarRetroalimentacionExitosaGuardaLosRegistrosRetroalimentados() {
        // ARRANGE
        val registros = listOf(crearRegistro(1), crearRegistro(2, fechaRetroalimentacion = "2026-10-05"))
        stubCargaExitosa(registros)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(2, viewModel.uiState.value.registros.size)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarRetroalimentacionConsultaPorElUsuarioDeLaSesion() {
        // ARRANGE
        stubCargaExitosa(emptyList())

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerRegistrosEstudiante("estudiante1", any()) }
        coVerify(exactly = 1) { repository.obtenerBitacorasEstudiante("estudiante1") }
    }

    @Test
    fun cargarRetroalimentacionSinDatosDejaTodoVacio() {
        // ARRANGE
        stubCargaExitosa(emptyList())

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertTrue(viewModel.uiState.value.registros.isEmpty())
        assertTrue(viewModel.uiState.value.bitacorasMap.isEmpty())
        assertTrue(viewModel.uiState.value.docentesMap.isEmpty())
        assertNull(viewModel.uiState.value.error)
    }

    // ============================================================================
    // 3. cargarRetroalimentacion — filtro de registros
    // ============================================================================

    @Test
    fun cargarRetroalimentacionIncluyeRegistrosRevisadosSinComentario() {
        // ARRANGE
        stubCargaExitosa(listOf(crearRegistro(1, estado = "revisado", comentario = null)))

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(1, viewModel.uiState.value.registros.size)
    }

    @Test
    fun cargarRetroalimentacionIncluyeRegistrosNoRevisadosConComentario() {
        // ARRANGE
        stubCargaExitosa(listOf(crearRegistro(1, estado = "borrador", comentario = "Falta detallar")))

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(1, viewModel.uiState.value.registros.size)
    }

    @Test
    fun cargarRetroalimentacionExcluyeRegistrosNoRevisadosSinComentario() {
        // ARRANGE
        stubCargaExitosa(listOf(crearRegistro(1, estado = "pendiente", comentario = null)))

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertTrue(viewModel.uiState.value.registros.isEmpty())
    }

    @Test
    fun cargarRetroalimentacionExcluyeRegistrosConComentarioEnBlanco() {
        // ARRANGE
        stubCargaExitosa(listOf(crearRegistro(1, estado = "pendiente", comentario = "    ")))

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertTrue(viewModel.uiState.value.registros.isEmpty())
    }

    @Test
    fun cargarRetroalimentacionExcluyeRegistrosConEstadoNullYSinComentario() {
        // ARRANGE
        stubCargaExitosa(listOf(crearRegistro(1, estado = null, comentario = "")))

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertTrue(viewModel.uiState.value.registros.isEmpty())
    }

    @Test
    fun cargarRetroalimentacionFiltraSoloLosRegistrosConRetroalimentacion() {
        // ARRANGE
        val registros = listOf(
            crearRegistro(1, estado = "revisado", comentario = "Muy bien"),
            crearRegistro(2, estado = "pendiente", comentario = null),
            crearRegistro(3, estado = "borrador", comentario = "Corregir"),
            crearRegistro(4, estado = "borrador", comentario = "")
        )
        stubCargaExitosa(registros)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        val ids = viewModel.uiState.value.registros.map { it.id }.toSet()
        assertEquals(setOf(1L, 3L), ids)
    }

    // ============================================================================
    // 4. cargarRetroalimentacion — orden de los registros
    // ============================================================================

    @Test
    fun cargarRetroalimentacionOrdenaPorFechaDeRetroalimentacionDescendente() {
        // ARRANGE
        val registros = listOf(
            crearRegistro(1, fechaRetroalimentacion = "2026-10-01"),
            crearRegistro(2, fechaRetroalimentacion = "2026-10-20"),
            crearRegistro(3, fechaRetroalimentacion = "2026-10-10")
        )
        stubCargaExitosa(registros)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(listOf(2L, 3L, 1L), viewModel.uiState.value.registros.map { it.id })
    }

    @Test
    fun cargarRetroalimentacionDejaAlFinalLosRegistrosSinFechaDeRetroalimentacion() {
        // ARRANGE
        val registros = listOf(
            crearRegistro(1, fechaRetroalimentacion = null),
            crearRegistro(2, fechaRetroalimentacion = "2026-10-20"),
            crearRegistro(3, fechaRetroalimentacion = "2026-10-10")
        )
        stubCargaExitosa(registros)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(listOf(2L, 3L, 1L), viewModel.uiState.value.registros.map { it.id })
    }

    // ============================================================================
    // 5. cargarRetroalimentacion — mapa de bitácoras
    // ============================================================================

    @Test
    fun cargarRetroalimentacionIndexaLasBitacorasPorSuId() {
        // ARRANGE
        val bitacoras = listOf(
            crearBitacora(10, "Bitácora Octubre"),
            crearBitacora(20, "Bitácora Noviembre")
        )
        stubCargaExitosa(listOf(crearRegistro(1)), bitacoras = bitacoras)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(setOf(10L, 20L), viewModel.uiState.value.bitacorasMap.keys)
        assertEquals("Bitácora Noviembre", viewModel.uiState.value.bitacorasMap[20L]?.titulo)
    }

    @Test
    fun cargarRetroalimentacionIgnoraLasBitacorasSinId() {
        // ARRANGE
        val bitacoras = listOf(
            crearBitacora(null, "Sin id"),
            crearBitacora(1, "Bitácora Octubre")
        )
        stubCargaExitosa(listOf(crearRegistro(1)), bitacoras = bitacoras)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(1, viewModel.uiState.value.bitacorasMap.size)
        assertEquals("Bitácora Octubre", viewModel.uiState.value.bitacorasMap[1L]?.titulo)
    }

    // ============================================================================
    // 6. cargarRetroalimentacion — mapa de docentes
    // ============================================================================

    @Test
    fun cargarRetroalimentacionRelacionaCadaUsuarioDocenteConSuNombre() {
        // ARRANGE
        val docentes = listOf(
            DocenteDto(id = 1, usuario = "docente1", nombre = "Juan Soto"),
            DocenteDto(id = 2, usuario = "docente2", nombre = "María López")
        )
        stubCargaExitosa(listOf(crearRegistro(1)), docentes = docentes)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(
            mapOf("docente1" to "Juan Soto", "docente2" to "María López"),
            viewModel.uiState.value.docentesMap
        )
    }

    @Test
    fun cargarRetroalimentacionIgnoraDocentesConUsuarioNullOEnBlanco() {
        // ARRANGE
        val docentes = listOf(
            DocenteDto(id = 1, usuario = null, nombre = "Sin usuario"),
            DocenteDto(id = 2, usuario = "   ", nombre = "Usuario en blanco"),
            DocenteDto(id = 3, usuario = "docente3", nombre = "María López")
        )
        stubCargaExitosa(listOf(crearRegistro(1)), docentes = docentes)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(mapOf("docente3" to "María López"), viewModel.uiState.value.docentesMap)
    }

    @Test
    fun cargarRetroalimentacionIgnoraDocentesConNombreNullOEnBlanco() {
        // ARRANGE
        val docentes = listOf(
            DocenteDto(id = 1, usuario = "docente1", nombre = null),
            DocenteDto(id = 2, usuario = "docente2", nombre = ""),
            DocenteDto(id = 3, usuario = "docente3", nombre = "María López")
        )
        stubCargaExitosa(listOf(crearRegistro(1)), docentes = docentes)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(mapOf("docente3" to "María López"), viewModel.uiState.value.docentesMap)
    }

    @Test
    fun cargarRetroalimentacionSiFallanLosDocentesIgualCargaLosRegistros() {
        // ARRANGE — la carga de docentes es opcional: si falla se ignora
        val registros = listOf(crearRegistro(1))
        stubCargaExitosa(registros, bitacoras = listOf(crearBitacora(1, "Bitácora Octubre")))
        coEvery { repository.obtenerDocentes() } throws IOException("sin red")

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(1, viewModel.uiState.value.registros.size)
        assertEquals(1, viewModel.uiState.value.bitacorasMap.size)
        assertTrue(viewModel.uiState.value.docentesMap.isEmpty())
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarRetroalimentacionSiLosDocentesFallanConErrorHttpNoMuestraError() {
        // ARRANGE
        stubCargaExitosa(listOf(crearRegistro(1)))
        coEvery { repository.obtenerDocentes() } throws crearHttpException(500)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(1, viewModel.uiState.value.registros.size)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarRetroalimentacionCancelacionAlCargarDocentesSePropaga() {
        // ARRANGE — una cancelación en la carga de docentes no debe tragarse
        stubCargaExitosa(listOf(crearRegistro(1)))
        coEvery { repository.obtenerDocentes() } throws CancellationException("cancelado")

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertTrue(viewModel.uiState.value.registros.isEmpty())
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
        assertFalse(viewModel.uiState.value.cargando)
    }

    // ============================================================================
    // 7. cargarRetroalimentacion — estado de carga
    // ============================================================================

    @Test
    fun cargarRetroalimentacionPrimeraCargaMarcaCargandoMientrasEspera() {
        // ARRANGE — el repositorio queda suspendido hasta liberar la compuerta
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } coAnswers {
            compuerta.await()
            listOf(crearRegistro(1))
        }
        coEvery { repository.obtenerBitacorasEstudiante(any()) } returns emptyList()
        coEvery { repository.obtenerDocentes() } returns emptyList()

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT — mientras espera
        assertTrue(viewModel.uiState.value.cargando)

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(1, viewModel.uiState.value.registros.size)
    }

    @Test
    fun cargarRetroalimentacionRefrescoConRegistrosNoMarcaCargando() {
        // ARRANGE — ya existe una lista cargada
        stubCargaExitosa(listOf(crearRegistro(1)))
        viewModel.cargarRetroalimentacion()

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } coAnswers {
            compuerta.await()
            listOf(crearRegistro(1))
        }

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT — el refresco no muestra spinner a pantalla completa
        assertFalse(viewModel.uiState.value.cargando)
        assertEquals(1, viewModel.uiState.value.registros.size)

        compuerta.complete(Unit)
    }

    @Test
    fun cargarRetroalimentacionPrimeraCargaLimpiaElErrorAnteriorAlComenzar() {
        // ARRANGE — primero falla y deja un error en pantalla completa
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarRetroalimentacion()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } coAnswers {
            compuerta.await()
            emptyList()
        }

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT — el error previo ya no se muestra mientras carga
        assertNull(viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.cargando)

        compuerta.complete(Unit)
    }

    // ============================================================================
    // 8. cargarRetroalimentacion — error en la primera carga (sin registros)
    // ============================================================================

    @Test
    fun cargarRetroalimentacionSinConexionMuestraErrorDePantallaCompleta() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
        assertTrue(viewModel.uiState.value.registros.isEmpty())
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarRetroalimentacionErrorEnRegistrosNoConsultaBitacorasNiDocentes() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        coVerify(exactly = 0) { repository.obtenerBitacorasEstudiante(any()) }
        coVerify(exactly = 0) { repository.obtenerDocentes() }
    }

    @Test
    fun cargarRetroalimentacionErrorEnBitacorasNoGuardaLosRegistros() {
        // ARRANGE — los registros llegan bien pero las bitácoras fallan
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns listOf(crearRegistro(1))
        coEvery { repository.obtenerBitacorasEstudiante(any()) } throws crearHttpException(500)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.error)
        assertTrue(viewModel.uiState.value.registros.isEmpty())
        coVerify(exactly = 0) { repository.obtenerDocentes() }
    }

    @Test
    fun cargarRetroalimentacionError401MuestraCredencialesIncorrectas() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws crearHttpException(401)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals("Credenciales incorrectas", viewModel.uiState.value.error)
    }

    @Test
    fun cargarRetroalimentacionError403MuestraMensajeDeSinPermisos() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws crearHttpException(403)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals("No tienes permisos para realizar esta acción", viewModel.uiState.value.error)
    }

    @Test
    fun cargarRetroalimentacionError404MuestraRecursoNoEncontrado() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws crearHttpException(404)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals("El recurso solicitado no fue encontrado", viewModel.uiState.value.error)
    }

    @Test
    fun cargarRetroalimentacionError400SinCuerpoMuestraDatosInvalidos() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws crearHttpException(400)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals("Datos inválidos o incompletos", viewModel.uiState.value.error)
    }

    @Test
    fun cargarRetroalimentacionExcepcionGenericaConMensajeMuestraEseMensaje() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws
                IllegalStateException("Cuerpo de respuesta vacío")

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals("Cuerpo de respuesta vacío", viewModel.uiState.value.error)
    }

    @Test
    fun cargarRetroalimentacionExcepcionGenericaSinMensajeMuestraErrorInesperado() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws RuntimeException()

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals("Ocurrió un error inesperado", viewModel.uiState.value.error)
    }

    // ============================================================================
    // 9. cargarRetroalimentacion — refresco con registros existentes
    // ============================================================================

    @Test
    fun cargarRetroalimentacionRefrescoExitosoReemplazaLosRegistros() {
        // ARRANGE
        stubCargaExitosa(listOf(crearRegistro(1)))
        viewModel.cargarRetroalimentacion()
        stubCargaExitosa(listOf(crearRegistro(1), crearRegistro(2, fechaRetroalimentacion = "2026-10-05")))

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(2, viewModel.uiState.value.registros.size)
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarRetroalimentacionRefrescoConErrorMantieneLosRegistrosYMuestraAviso() {
        // ARRANGE
        stubCargaExitosa(listOf(crearRegistro(1)))
        viewModel.cargarRetroalimentacion()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals(1, viewModel.uiState.value.registros.size)
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarRetroalimentacionRefrescoExitosoLimpiaElAvisoAnterior() {
        // ARRANGE — lista cargada y luego un refresco fallido que deja un aviso
        stubCargaExitosa(listOf(crearRegistro(1)))
        viewModel.cargarRetroalimentacion()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarRetroalimentacion()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)

        stubCargaExitosa(listOf(crearRegistro(1)))

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarRetroalimentacionRefrescoLimpiaElAvisoAlComenzar() {
        // ARRANGE — lista cargada y aviso pendiente de un refresco fallido
        stubCargaExitosa(listOf(crearRegistro(1)))
        viewModel.cargarRetroalimentacion()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarRetroalimentacion()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } coAnswers {
            compuerta.await()
            listOf(crearRegistro(1))
        }

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT — el aviso previo desaparece mientras se refresca
        assertNull(viewModel.uiState.value.errorAviso)

        compuerta.complete(Unit)
    }

    @Test
    fun cargarRetroalimentacionSinRetroalimentacionYConErrorUsaPantallaCompletaYNoAviso() {
        // ARRANGE — el servidor responde sin registros retroalimentados y luego falla
        stubCargaExitosa(listOf(crearRegistro(1, estado = "pendiente", comentario = null)))
        viewModel.cargarRetroalimentacion()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws crearHttpException(500)

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
    }

    @Test
    fun cargarRetroalimentacionExitoDespuesDeErrorLimpiaElErrorDePantallaCompleta() {
        // ARRANGE — la primera carga falla
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarRetroalimentacion()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)

        stubCargaExitosa(listOf(crearRegistro(1)))

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertNull(viewModel.uiState.value.error)
        assertEquals(1, viewModel.uiState.value.registros.size)
    }

    // ============================================================================
    // 10. cargarRetroalimentacion — cancelación
    // ============================================================================

    @Test
    fun cargarRetroalimentacionCancelacionNoMuestraErrorYDejaCargandoEnFalse() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws
                CancellationException("cancelado")

        // ACT
        viewModel.cargarRetroalimentacion()

        // ASSERT
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.errorAviso)
        assertFalse(viewModel.uiState.value.cargando)
        assertTrue(viewModel.uiState.value.registros.isEmpty())
    }

    // ============================================================================
    // 11. limpiarAviso
    // ============================================================================

    @Test
    fun limpiarAvisoQuitaElAvisoYMantieneLosRegistros() {
        // ARRANGE — lista cargada y un aviso por refresco fallido
        stubCargaExitosa(listOf(crearRegistro(1)))
        viewModel.cargarRetroalimentacion()
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarRetroalimentacion()
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorAviso)

        // ACT
        viewModel.limpiarAviso()

        // ASSERT
        assertNull(viewModel.uiState.value.errorAviso)
        assertEquals(1, viewModel.uiState.value.registros.size)
    }

    @Test
    fun limpiarAvisoNoModificaElErrorDePantallaCompleta() {
        // ARRANGE — la primera carga falla y deja un error de pantalla completa
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")
        viewModel.cargarRetroalimentacion()

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