package cl.duoc.bitacora2.ui.theme.Estudiante

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto
import cl.duoc.bitacora2.data.remoto.dto.RegistroRequest
import cl.duoc.bitacora2.util.Sesion
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
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
class EstudianteRegistroViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: EstudianteRegistroViewModel

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
        viewModel = EstudianteRegistroViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    // Helper privado para construir una bitácora de prueba.
    // Se usa Enero 2020 para que la fecha de clase sea siempre pasada, sin depender de la fecha actual.
    private fun crearBitacora(
        id: Long? = 1,
        estado: String = "publicada",
        mes: String = "Enero",
        anio: String = "2020"
    ): BitacoraDto {
        return BitacoraDto(
            id = id,
            docente = "docente1",
            curso = "3A",
            asignatura = "Programación",
            mes = mes,
            anio = anio,
            titulo = "Bitácora de prueba",
            estado = estado
        )
    }

    // Helper privado para construir un registro de prueba
    private fun crearRegistro(
        id: Long = 5,
        bitacoraId: Long? = 1,
        estado: String = "borrador",
        dudas: String? = "• Duda 1\n• Duda 2"
    ): RegistroDto {
        return RegistroDto(
            id = id,
            bitacoraId = bitacoraId,
            estudiante = "estudiante1",
            fechaClase = "15/01/2020",
            contenido = "Contenido guardado",
            actividad = "Actividad guardada",
            aprendizaje = "Aprendizaje guardado",
            dudas = dudas,
            fotografias = "foto.jpg",
            archivos = "archivo.pdf",
            enlaces = "https://ejemplo.cl",
            estado = estado
        )
    }

    // Helper privado para construir una HttpException con el código indicado
    private fun crearHttpException(codigo: Int): HttpException {
        val body = "".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(codigo, body))
    }

    // Helper privado para completar los campos obligatorios con valores válidos
    private fun completarFormulario() {
        viewModel.cambiarFecha("15/01/2020")
        viewModel.cambiarContenido("Contenido de la clase")
        viewModel.cambiarActividad("Actividad de la clase")
        viewModel.cambiarAprendizaje("Aprendizaje de la clase")
    }

    // Helper privado para dejar el ViewModel en modo creación con el formulario válido
    private fun prepararCreacionConFormularioValido(bitacora: BitacoraDto = crearBitacora()) {
        coEvery { repository.obtenerBitacoraPorId(any()) } returns bitacora
        viewModel.inicializar(bitacoraId = 1, registroId = null)
        completarFormulario()
    }

    // Helper privado para dejar el ViewModel en modo edición con el registro cargado
    private fun prepararEdicion(registro: RegistroDto = crearRegistro()) {
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns listOf(registro)
        coEvery { repository.obtenerBitacoraPorId(any()) } returns crearBitacora()
        viewModel.inicializar(bitacoraId = null, registroId = registro.id)
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
        assertEquals(EstudianteRegistroUiState(), estado)
    }

    // ============================================================================
    // 2. dudasAListoString (companion object)
    // ============================================================================

    @Test
    fun dudasAListoStringListaSinDudaPendiente() {
        // ARRANGE
        val lista = listOf("Duda 1", "Duda 2")

        // ACT
        val resultado = EstudianteRegistroViewModel.dudasAListoString(lista)

        // ASSERT
        assertEquals("Duda 1\nDuda 2", resultado)
    }

    @Test
    fun dudasAListoStringAgregaLaDudaPendienteAlFinal() {
        // ARRANGE
        val lista = listOf("Duda 1")

        // ACT
        val resultado = EstudianteRegistroViewModel.dudasAListoString(lista, "Duda pendiente")

        // ASSERT
        assertEquals("Duda 1\nDuda pendiente", resultado)
    }

    @Test
    fun dudasAListoStringDudaPendienteConEspaciosSeRecorta() {
        // ARRANGE
        val lista = listOf("Duda 1")

        // ACT
        val resultado = EstudianteRegistroViewModel.dudasAListoString(lista, "   Duda pendiente   ")

        // ASSERT
        assertEquals("Duda 1\nDuda pendiente", resultado)
    }

    @Test
    fun dudasAListoStringDudaPendienteEnBlancoSeIgnora() {
        // ARRANGE
        val lista = listOf("Duda 1")

        // ACT
        val resultado = EstudianteRegistroViewModel.dudasAListoString(lista, "     ")

        // ASSERT
        assertEquals("Duda 1", resultado)
    }

    @Test
    fun dudasAListoStringListaVaciaConDudaPendiente() {
        // ARRANGE
        val lista = emptyList<String>()

        // ACT
        val resultado = EstudianteRegistroViewModel.dudasAListoString(lista, "Duda pendiente")

        // ASSERT
        assertEquals("Duda pendiente", resultado)
    }

    @Test
    fun dudasAListoStringListaVaciaSinDudaPendiente() {
        // ARRANGE
        val lista = emptyList<String>()

        // ACT
        val resultado = EstudianteRegistroViewModel.dudasAListoString(lista)

        // ASSERT
        assertEquals("", resultado)
    }

    // ============================================================================
    // 3. stringADudasLista (companion object)
    // ============================================================================

    @Test
    fun stringADudasListaTextoNullDevuelveListaVacia() {
        // ARRANGE
        val texto: String? = null

        // ACT
        val resultado = EstudianteRegistroViewModel.stringADudasLista(texto)

        // ASSERT
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun stringADudasListaTextoVacioDevuelveListaVacia() {
        // ARRANGE
        val texto = ""

        // ACT
        val resultado = EstudianteRegistroViewModel.stringADudasLista(texto)

        // ASSERT
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun stringADudasListaTextoEnBlancoDevuelveListaVacia() {
        // ARRANGE
        val texto = "    "

        // ACT
        val resultado = EstudianteRegistroViewModel.stringADudasLista(texto)

        // ASSERT
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun stringADudasListaSeparaPorSaltosDeLinea() {
        // ARRANGE
        val texto = "Duda 1\nDuda 2\nDuda 3"

        // ACT
        val resultado = EstudianteRegistroViewModel.stringADudasLista(texto)

        // ASSERT
        assertEquals(listOf("Duda 1", "Duda 2", "Duda 3"), resultado)
    }

    @Test
    fun stringADudasListaQuitaElPrefijoDeVineta() {
        // ARRANGE
        val texto = "• Duda 1\n•Duda 2"

        // ACT
        val resultado = EstudianteRegistroViewModel.stringADudasLista(texto)

        // ASSERT
        assertEquals(listOf("Duda 1", "Duda 2"), resultado)
    }

    @Test
    fun stringADudasListaIgnoraLineasEnBlanco() {
        // ARRANGE
        val texto = "Duda 1\n\n   \nDuda 2"

        // ACT
        val resultado = EstudianteRegistroViewModel.stringADudasLista(texto)

        // ASSERT
        assertEquals(listOf("Duda 1", "Duda 2"), resultado)
    }

    @Test
    fun stringADudasListaRecortaEspaciosDeCadaLinea() {
        // ARRANGE
        val texto = "   Duda 1   \n  Duda 2  "

        // ACT
        val resultado = EstudianteRegistroViewModel.stringADudasLista(texto)

        // ASSERT
        assertEquals(listOf("Duda 1", "Duda 2"), resultado)
    }

    @Test
    fun stringADudasListaIgnoraLineaQueSoloTieneLaVineta() {
        // ARRANGE
        val texto = "•\nDuda 1"

        // ACT
        val resultado = EstudianteRegistroViewModel.stringADudasLista(texto)

        // ASSERT
        assertEquals(listOf("Duda 1"), resultado)
    }

    // ============================================================================
    // 4. inicializar — modo creación
    // ============================================================================

    @Test
    fun inicializarCreacionConBitacoraPublicadaGuardaLaBitacora() {
        // ARRANGE
        val bitacora = crearBitacora(estado = "publicada")
        coEvery { repository.obtenerBitacoraPorId(any()) } returns bitacora

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT
        assertEquals(bitacora, viewModel.uiState.value.bitacora)
        assertFalse(viewModel.uiState.value.esModoEdicion)
        assertFalse(viewModel.uiState.value.cargando)
        assertFalse(viewModel.uiState.value.estadoInvalido)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun inicializarCreacionConsultaLaBitacoraPorElIdIndicado() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } returns crearBitacora(id = 7)

        // ACT
        viewModel.inicializar(bitacoraId = 7, registroId = null)

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerBitacoraPorId(7) }
        coVerify(exactly = 0) { repository.obtenerRegistrosEstudiante(any(), any()) }
    }

    @Test
    fun inicializarCreacionConBitacoraCerradaMarcaEstadoInvalido() {
        // ARRANGE
        val bitacora = crearBitacora(estado = "cerrada")
        coEvery { repository.obtenerBitacoraPorId(any()) } returns bitacora

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT
        assertTrue(viewModel.uiState.value.estadoInvalido)
        assertEquals(
            "La bitácora está cerrada. No se pueden crear nuevos registros.",
            viewModel.uiState.value.mensajeEstadoInvalido
        )
        assertEquals(bitacora, viewModel.uiState.value.bitacora)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun inicializarCreacionConBitacoraEnBorradorMarcaEstadoInvalido() {
        // ARRANGE — solo se permite crear registros en bitácoras publicadas
        coEvery { repository.obtenerBitacoraPorId(any()) } returns crearBitacora(estado = "borrador")

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT
        assertTrue(viewModel.uiState.value.estadoInvalido)
    }

    @Test
    fun inicializarCreacionConErrorMuestraElMensajeDeError() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws IOException("sin red")

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.bitacora)
    }

    @Test
    fun inicializarCreacionConError404MuestraRecursoNoEncontrado() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws crearHttpException(404)

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT
        assertEquals("El recurso solicitado no fue encontrado", viewModel.uiState.value.error)
    }

    @Test
    fun inicializarCreacionConError500MuestraErrorInternoDelServidor() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws crearHttpException(500)

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.error)
    }

    @Test
    fun inicializarCreacionConExcepcionGenericaSinMensajeMuestraErrorInesperado() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws RuntimeException()

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT
        assertEquals("Ocurrió un error inesperado", viewModel.uiState.value.error)
    }

    @Test
    fun inicializarMarcaCargandoMientrasEspera() {
        // ARRANGE — el repositorio queda suspendido hasta liberar la compuerta
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerBitacoraPorId(any()) } coAnswers {
            compuerta.await()
            crearBitacora()
        }

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT — mientras espera
        assertTrue(viewModel.uiState.value.cargando)

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun inicializarLimpiaElErrorYElEstadoInvalidoAnterioresAlComenzar() {
        // ARRANGE — primero queda un estado inválido y luego un error
        coEvery { repository.obtenerBitacoraPorId(any()) } returns crearBitacora(estado = "cerrada")
        viewModel.inicializar(bitacoraId = 1, registroId = null)
        assertTrue(viewModel.uiState.value.estadoInvalido)

        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.obtenerBitacoraPorId(any()) } coAnswers {
            compuerta.await()
            crearBitacora()
        }

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT
        assertFalse(viewModel.uiState.value.estadoInvalido)
        assertNull(viewModel.uiState.value.error)

        compuerta.complete(Unit)
    }

    @Test
    fun inicializarSinIdsNoConsultaAlRepositorio() {
        // ARRANGE — ni bitácora ni registro

        // ACT
        viewModel.inicializar(bitacoraId = null, registroId = null)

        // ASSERT
        coVerify(exactly = 0) { repository.obtenerBitacoraPorId(any()) }
        coVerify(exactly = 0) { repository.obtenerRegistrosEstudiante(any(), any()) }
        assertNull(viewModel.uiState.value.bitacora)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun inicializarCancelacionNoMuestraError() {
        // ARRANGE
        coEvery { repository.obtenerBitacoraPorId(any()) } throws CancellationException("cancelado")

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = null)

        // ASSERT
        assertNull(viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.bitacora)
    }

    // ============================================================================
    // 5. inicializar — modo edición
    // ============================================================================

    @Test
    fun inicializarEdicionCargaLosCamposDelRegistro() {
        // ARRANGE
        val registro = crearRegistro(id = 5)

        // ACT
        prepararEdicion(registro)

        // ASSERT
        val estado = viewModel.uiState.value
        assertTrue(estado.esModoEdicion)
        assertEquals(5L, estado.idRegistro)
        assertEquals("15/01/2020", estado.fecha)
        assertEquals("Contenido guardado", estado.contenido)
        assertEquals("Actividad guardada", estado.actividad)
        assertEquals("Aprendizaje guardado", estado.aprendizaje)
        assertEquals("foto.jpg", estado.fotografias)
        assertEquals("archivo.pdf", estado.archivos)
        assertEquals("https://ejemplo.cl", estado.enlaces)
        assertNotNullBitacora()
        assertFalse(estado.cargando)
        assertNull(estado.error)
    }

    // Helper privado de aserción para verificar que se cargó la bitácora
    private fun assertNotNullBitacora() {
        assertEquals(1L, viewModel.uiState.value.bitacora?.id)
    }

    @Test
    fun inicializarEdicionConvierteLasDudasGuardadasEnLista() {
        // ARRANGE
        val registro = crearRegistro(dudas = "• Duda 1\n• Duda 2")

        // ACT
        prepararEdicion(registro)

        // ASSERT
        assertEquals(listOf("Duda 1", "Duda 2"), viewModel.uiState.value.listaDudas)
        assertEquals("", viewModel.uiState.value.dudas)
    }

    @Test
    fun inicializarEdicionConDudasNullDejaListaVacia() {
        // ARRANGE
        val registro = crearRegistro(dudas = null)

        // ACT
        prepararEdicion(registro)

        // ASSERT
        assertTrue(viewModel.uiState.value.listaDudas.isEmpty())
    }

    @Test
    fun inicializarEdicionConCamposNullUsaCadenasVacias() {
        // ARRANGE
        val registro = RegistroDto(
            id = 5,
            bitacoraId = 1,
            fechaClase = null,
            contenido = null,
            actividad = null,
            aprendizaje = null,
            dudas = null,
            fotografias = null,
            archivos = null,
            enlaces = null,
            estado = "borrador"
        )

        // ACT
        prepararEdicion(registro)

        // ASSERT
        val estado = viewModel.uiState.value
        assertEquals("", estado.fecha)
        assertEquals("", estado.contenido)
        assertEquals("", estado.actividad)
        assertEquals("", estado.aprendizaje)
        assertEquals("", estado.fotografias)
        assertEquals("", estado.archivos)
        assertEquals("", estado.enlaces)
    }

    @Test
    fun inicializarEdicionConsultaLosRegistrosDelUsuarioDeLaSesion() {
        // ARRANGE
        val registro = crearRegistro(id = 5)

        // ACT
        prepararEdicion(registro)

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerRegistrosEstudiante("estudiante1", any()) }
    }

    @Test
    fun inicializarEdicionConsultaLaBitacoraDelRegistro() {
        // ARRANGE
        val registro = crearRegistro(id = 5, bitacoraId = 9)

        // ACT
        prepararEdicion(registro)

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerBitacoraPorId(9) }
    }

    @Test
    fun inicializarEdicionConRegistroSinBitacoraIdConsultaLaBitacoraCero() {
        // ARRANGE
        val registro = crearRegistro(id = 5, bitacoraId = null)

        // ACT
        prepararEdicion(registro)

        // ASSERT
        coVerify(exactly = 1) { repository.obtenerBitacoraPorId(0L) }
    }

    @Test
    fun inicializarEdicionTienePrioridadSobreLaCreacion() {
        // ARRANGE — se entregan ambos ids
        val registro = crearRegistro(id = 5, bitacoraId = 9)
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns listOf(registro)
        coEvery { repository.obtenerBitacoraPorId(any()) } returns crearBitacora(id = 9)

        // ACT
        viewModel.inicializar(bitacoraId = 1, registroId = 5)

        // ASSERT
        assertTrue(viewModel.uiState.value.esModoEdicion)
        coVerify(exactly = 1) { repository.obtenerBitacoraPorId(9) }
        coVerify(exactly = 0) { repository.obtenerBitacoraPorId(1) }
    }

    @Test
    fun inicializarEdicionConRegistroInexistenteMuestraError() {
        // ARRANGE — el servidor no devuelve el registro buscado
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns
                listOf(crearRegistro(id = 1))

        // ACT
        viewModel.inicializar(bitacoraId = null, registroId = 99)

        // ASSERT
        assertEquals("Registro no encontrado", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
        coVerify(exactly = 0) { repository.obtenerBitacoraPorId(any()) }
    }

    @Test
    fun inicializarEdicionConListaDeRegistrosVaciaMuestraError() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns emptyList()

        // ACT
        viewModel.inicializar(bitacoraId = null, registroId = 5)

        // ASSERT
        assertEquals("Registro no encontrado", viewModel.uiState.value.error)
    }

    @Test
    fun inicializarEdicionConRegistroEnviadoMarcaEstadoInvalido() {
        // ARRANGE — solo se pueden editar registros en estado borrador
        val registro = crearRegistro(id = 5, estado = "pendiente")
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns listOf(registro)

        // ACT
        viewModel.inicializar(bitacoraId = null, registroId = 5)

        // ASSERT
        assertTrue(viewModel.uiState.value.estadoInvalido)
        assertEquals(
            "Solo se puede editar un registro en estado borrador",
            viewModel.uiState.value.mensajeEstadoInvalido
        )
        assertFalse(viewModel.uiState.value.cargando)
        coVerify(exactly = 0) { repository.obtenerBitacoraPorId(any()) }
    }

    @Test
    fun inicializarEdicionConBitacoraCerradaMarcaEstadoInvalido() {
        // ARRANGE
        val bitacoraCerrada = crearBitacora(estado = "cerrada")
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns listOf(crearRegistro(id = 5))
        coEvery { repository.obtenerBitacoraPorId(any()) } returns bitacoraCerrada

        // ACT
        viewModel.inicializar(bitacoraId = null, registroId = 5)

        // ASSERT
        assertTrue(viewModel.uiState.value.estadoInvalido)
        assertEquals(
            "La bitácora está cerrada. No se pueden modificar ni enviar registros.",
            viewModel.uiState.value.mensajeEstadoInvalido
        )
        assertEquals(bitacoraCerrada, viewModel.uiState.value.bitacora)
        assertFalse(viewModel.uiState.value.esModoEdicion)
    }

    @Test
    fun inicializarEdicionConErrorAlObtenerRegistrosMuestraElError() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } throws IOException("sin red")

        // ACT
        viewModel.inicializar(bitacoraId = null, registroId = 5)

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun inicializarEdicionConErrorAlObtenerLaBitacoraMuestraElError() {
        // ARRANGE
        coEvery { repository.obtenerRegistrosEstudiante(any(), any()) } returns listOf(crearRegistro(id = 5))
        coEvery { repository.obtenerBitacoraPorId(any()) } throws crearHttpException(500)

        // ACT
        viewModel.inicializar(bitacoraId = null, registroId = 5)

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    // ============================================================================
    // 6. Cambios de campos del formulario
    // ============================================================================

    @Test
    fun cambiarFechaActualizaLaFecha() {
        // ARRANGE
        val valor = "15/01/2020"

        // ACT
        viewModel.cambiarFecha(valor)

        // ASSERT
        assertEquals("15/01/2020", viewModel.uiState.value.fecha)
    }

    @Test
    fun cambiarContenidoActualizaElContenido() {
        // ARRANGE
        val valor = "Contenido de la clase"

        // ACT
        viewModel.cambiarContenido(valor)

        // ASSERT
        assertEquals("Contenido de la clase", viewModel.uiState.value.contenido)
    }

    @Test
    fun cambiarActividadActualizaLaActividad() {
        // ARRANGE
        val valor = "Actividad de la clase"

        // ACT
        viewModel.cambiarActividad(valor)

        // ASSERT
        assertEquals("Actividad de la clase", viewModel.uiState.value.actividad)
    }

    @Test
    fun cambiarAprendizajeActualizaElAprendizaje() {
        // ARRANGE
        val valor = "Aprendizaje de la clase"

        // ACT
        viewModel.cambiarAprendizaje(valor)

        // ASSERT
        assertEquals("Aprendizaje de la clase", viewModel.uiState.value.aprendizaje)
    }

    @Test
    fun cambiarDudasActualizaLaDudaEnCurso() {
        // ARRANGE
        val valor = "Una duda"

        // ACT
        viewModel.cambiarDudas(valor)

        // ASSERT
        assertEquals("Una duda", viewModel.uiState.value.dudas)
    }

    @Test
    fun cambiarFotografiasActualizaLasFotografias() {
        // ARRANGE
        val valor = "foto.jpg"

        // ACT
        viewModel.cambiarFotografias(valor)

        // ASSERT
        assertEquals("foto.jpg", viewModel.uiState.value.fotografias)
    }

    @Test
    fun cambiarArchivosActualizaLosArchivos() {
        // ARRANGE
        val valor = "archivo.pdf"

        // ACT
        viewModel.cambiarArchivos(valor)

        // ASSERT
        assertEquals("archivo.pdf", viewModel.uiState.value.archivos)
    }

    @Test
    fun cambiarEnlacesActualizaLosEnlaces() {
        // ARRANGE
        val valor = "https://ejemplo.cl"

        // ACT
        viewModel.cambiarEnlaces(valor)

        // ASSERT
        assertEquals("https://ejemplo.cl", viewModel.uiState.value.enlaces)
    }

    // ============================================================================
    // 7. agregarDuda / eliminarDuda
    // ============================================================================

    @Test
    fun agregarDudaAgregaLaDudaALaListaYLimpiaElCampo() {
        // ARRANGE
        viewModel.cambiarDudas("¿Cómo funciona X?")

        // ACT
        viewModel.agregarDuda()

        // ASSERT
        assertEquals(listOf("¿Cómo funciona X?"), viewModel.uiState.value.listaDudas)
        assertEquals("", viewModel.uiState.value.dudas)
    }

    @Test
    fun agregarDudaRecortaLosEspacios() {
        // ARRANGE
        viewModel.cambiarDudas("   Una duda   ")

        // ACT
        viewModel.agregarDuda()

        // ASSERT
        assertEquals(listOf("Una duda"), viewModel.uiState.value.listaDudas)
    }

    @Test
    fun agregarDudaVaciaNoAgregaNada() {
        // ARRANGE
        viewModel.cambiarDudas("")

        // ACT
        viewModel.agregarDuda()

        // ASSERT
        assertTrue(viewModel.uiState.value.listaDudas.isEmpty())
    }

    @Test
    fun agregarDudaSoloConEspaciosNoAgregaNada() {
        // ARRANGE
        viewModel.cambiarDudas("     ")

        // ACT
        viewModel.agregarDuda()

        // ASSERT
        assertTrue(viewModel.uiState.value.listaDudas.isEmpty())
        assertEquals("     ", viewModel.uiState.value.dudas)
    }

    @Test
    fun agregarDudaMantieneLasDudasAnterioresEnOrden() {
        // ARRANGE
        viewModel.cambiarDudas("Duda 1")
        viewModel.agregarDuda()
        viewModel.cambiarDudas("Duda 2")

        // ACT
        viewModel.agregarDuda()

        // ASSERT
        assertEquals(listOf("Duda 1", "Duda 2"), viewModel.uiState.value.listaDudas)
    }

    @Test
    fun eliminarDudaConIndiceValidoQuitaLaDuda() {
        // ARRANGE
        listOf("Duda 1", "Duda 2", "Duda 3").forEach {
            viewModel.cambiarDudas(it)
            viewModel.agregarDuda()
        }

        // ACT
        viewModel.eliminarDuda(1)

        // ASSERT
        assertEquals(listOf("Duda 1", "Duda 3"), viewModel.uiState.value.listaDudas)
    }

    @Test
    fun eliminarDudaConIndiceNegativoNoCambiaLaLista() {
        // ARRANGE
        viewModel.cambiarDudas("Duda 1")
        viewModel.agregarDuda()

        // ACT
        viewModel.eliminarDuda(-1)

        // ASSERT
        assertEquals(listOf("Duda 1"), viewModel.uiState.value.listaDudas)
    }

    @Test
    fun eliminarDudaConIndiceFueraDeRangoNoCambiaLaLista() {
        // ARRANGE
        viewModel.cambiarDudas("Duda 1")
        viewModel.agregarDuda()

        // ACT
        viewModel.eliminarDuda(5)

        // ASSERT
        assertEquals(listOf("Duda 1"), viewModel.uiState.value.listaDudas)
    }

    @Test
    fun eliminarDudaConListaVaciaNoFalla() {
        // ARRANGE — lista de dudas vacía

        // ACT
        viewModel.eliminarDuda(0)

        // ASSERT
        assertTrue(viewModel.uiState.value.listaDudas.isEmpty())
    }

    // ============================================================================
    // 8. mostrarError / limpiarMensaje
    // ============================================================================

    @Test
    fun mostrarErrorGuardaElMensajeYLoMarcaComoError() {
        // ARRANGE
        val mensaje = "No se pudo adjuntar el archivo"

        // ACT
        viewModel.mostrarError(mensaje)

        // ASSERT
        assertEquals("No se pudo adjuntar el archivo", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
    }

    @Test
    fun limpiarMensajeQuitaElMensajeYLaMarcaDeError() {
        // ARRANGE
        viewModel.mostrarError("Un error")

        // ACT
        viewModel.limpiarMensaje()

        // ASSERT
        assertEquals("", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.mensajeEsError)
    }

    // ============================================================================
    // 9. guardarBorrador — validaciones
    // ============================================================================

    @Test
    fun guardarBorradorSinBitacoraMuestraErrorYNoLlamaAlRepositorio() {
        // ARRANGE — no se inicializó la bitácora
        completarFormulario()

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals("La bitácora no está disponible", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
        coVerify(exactly = 0) { repository.actualizarRegistro(any(), any(), any()) }
    }

    @Test
    fun guardarBorradorConFechaInvalidaMuestraErrorDeFecha() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        viewModel.cambiarFecha("32/13/2020")

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals(
            "Ingrese una fecha válida con formato dd/mm/aaaa, no futura y que corresponda al mes y año de la bitácora (Enero 2020)",
            viewModel.uiState.value.mensaje
        )
        assertTrue(viewModel.uiState.value.mensajeEsError)
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
    }

    @Test
    fun guardarBorradorConCamposObligatoriosVaciosMuestraError() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        viewModel.cambiarContenido("")

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals(
            "Complete los campos obligatorios (contenido, actividad y aprendizaje)",
            viewModel.uiState.value.mensaje
        )
        assertTrue(viewModel.uiState.value.mensajeEsError)
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
    }

    @Test
    fun guardarBorradorConEnlaceInvalidoMuestraError() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        viewModel.cambiarEnlaces("www.sin-protocolo.cl")

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals("El enlace debe comenzar con http:// o https://", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
    }

    @Test
    fun guardarBorradorConBitacoraSinMesMuestraErrorDeFecha() {
        // ARRANGE — la bitácora existe pero no trae mes
        prepararCreacionConFormularioValido(crearBitacora().copy(mes = null))

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals(
            "Ingrese una fecha válida con formato dd/mm/aaaa, no futura y que corresponda al mes y año de la bitácora ( 2020)",
            viewModel.uiState.value.mensaje
        )
        assertTrue(viewModel.uiState.value.mensajeEsError)
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
    }

    @Test
    fun guardarBorradorConBitacoraSinAnioMuestraErrorDeFecha() {
        // ARRANGE — la bitácora existe pero no trae año
        prepararCreacionConFormularioValido(crearBitacora().copy(anio = null))

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals(
            "Ingrese una fecha válida con formato dd/mm/aaaa, no futura y que corresponda al mes y año de la bitácora (Enero )",
            viewModel.uiState.value.mensaje
        )
        assertTrue(viewModel.uiState.value.mensajeEsError)
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
    }

    // ============================================================================
    // 10. guardarBorrador — creación y actualización
    // ============================================================================

    @Test
    fun guardarBorradorNuevoCreaElRegistroYGuardaSuId() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } returns RegistroDto(id = 20)

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals(20L, viewModel.uiState.value.idRegistro)
        assertTrue(viewModel.uiState.value.guardado)
        assertEquals("Borrador guardado con éxito.", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.actualizarRegistro(any(), any(), any()) }
    }

    @Test
    fun guardarBorradorNuevoEnviaLaSolicitudConLosDatosDelFormulario() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        viewModel.cambiarFecha("  15/01/2020  ")
        viewModel.cambiarContenido("  Contenido  ")
        viewModel.cambiarActividad("  Actividad  ")
        viewModel.cambiarAprendizaje("  Aprendizaje  ")
        viewModel.cambiarDudas("Duda 1")
        viewModel.agregarDuda()
        viewModel.cambiarDudas("Duda pendiente")
        viewModel.cambiarFotografias("foto.jpg")
        viewModel.cambiarArchivos("archivo.pdf")
        viewModel.cambiarEnlaces("https://ejemplo.cl")
        coEvery { repository.crearRegistro(any()) } returns RegistroDto(id = 20)

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        val esperada = RegistroRequest(
            bitacoraId = 1,
            estudiante = "estudiante1",
            fechaClase = "15/01/2020",
            contenido = "Contenido",
            actividad = "Actividad",
            aprendizaje = "Aprendizaje",
            dudas = "Duda 1\nDuda pendiente",
            fotografias = "foto.jpg",
            archivos = "archivo.pdf",
            enlaces = "https://ejemplo.cl"
        )
        coVerify(exactly = 1) { repository.crearRegistro(esperada) }
    }

    @Test
    fun guardarBorradorExistenteActualizaElRegistroEnVezDeCrearlo() {
        // ARRANGE — modo edición con un registro ya guardado
        prepararEdicion(crearRegistro(id = 5))
        coEvery { repository.actualizarRegistro(any(), any(), any()) } returns RegistroDto(id = 5)

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        coVerify(exactly = 1) { repository.actualizarRegistro(5, "estudiante1", any()) }
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
        assertTrue(viewModel.uiState.value.guardado)
        assertEquals("Borrador guardado con éxito.", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.mensajeEsError)
        assertEquals(5L, viewModel.uiState.value.idRegistro)
    }

    @Test
    fun guardarBorradorMarcaGuardandoMientrasEspera() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.crearRegistro(any()) } coAnswers {
            compuerta.await()
            RegistroDto(id = 20)
        }

        // ACT
        viewModel.guardarBorrador()

        // ASSERT — mientras espera
        assertTrue(viewModel.uiState.value.guardando)

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun guardarBorradorLimpiaElMensajeAnteriorAlComenzar() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        viewModel.mostrarError("Mensaje anterior")
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.crearRegistro(any()) } coAnswers {
            compuerta.await()
            RegistroDto(id = 20)
        }

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals("", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.mensajeEsError)

        compuerta.complete(Unit)
    }

    @Test
    fun guardarBorradorIgnoraUnSegundoIntentoMientrasGuarda() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.crearRegistro(any()) } coAnswers {
            compuerta.await()
            RegistroDto(id = 20)
        }

        // ACT
        viewModel.guardarBorrador()
        viewModel.guardarBorrador()
        compuerta.complete(Unit)

        // ASSERT
        coVerify(exactly = 1) { repository.crearRegistro(any()) }
    }

    // ============================================================================
    // 11. guardarBorrador — errores
    // ============================================================================

    @Test
    fun guardarBorradorSinConexionMuestraErrorYNoMarcaGuardado() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } throws IOException("sin red")

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardado)
        assertFalse(viewModel.uiState.value.guardando)
        assertNull(viewModel.uiState.value.idRegistro)
    }

    @Test
    fun guardarBorradorError500MuestraErrorInternoDelServidor() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } throws crearHttpException(500)

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
    }

    @Test
    fun guardarBorradorActualizacionConErrorMuestraElMensaje() {
        // ARRANGE
        prepararEdicion(crearRegistro(id = 5))
        coEvery { repository.actualizarRegistro(any(), any(), any()) } throws crearHttpException(403)

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals("No tienes permisos para realizar esta acción", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardado)
    }

    @Test
    fun guardarBorradorConBitacoraSinIdMuestraBitacoraNoDisponible() {
        // ARRANGE — la bitácora cargada no trae id
        prepararCreacionConFormularioValido(crearBitacora(id = null))

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals("Bitácora no disponible", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
    }

    @Test
    fun guardarBorradorCancelacionNoMuestraMensajeYDejaGuardandoEnFalse() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } throws CancellationException("cancelado")

        // ACT
        viewModel.guardarBorrador()

        // ASSERT
        assertEquals("", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.guardado)
        assertFalse(viewModel.uiState.value.guardando)
    }

    // ============================================================================
    // 12. enviarAlDocente — validaciones
    // ============================================================================

    @Test
    fun enviarAlDocenteSinBitacoraMuestraErrorYNoLlamaAlRepositorio() {
        // ARRANGE — no se inicializó la bitácora
        completarFormulario()

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        assertEquals("La bitácora no está disponible", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
        coVerify(exactly = 0) { repository.enviarRegistro(any(), any()) }
    }

    @Test
    fun enviarAlDocenteConFechaInvalidaMuestraErrorDeFecha() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        viewModel.cambiarFecha("fecha inválida")

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        assertEquals(
            "Ingrese una fecha válida con formato dd/mm/aaaa, no futura y que corresponda al mes y año de la bitácora (Enero 2020)",
            viewModel.uiState.value.mensaje
        )
        coVerify(exactly = 0) { repository.enviarRegistro(any(), any()) }
    }

    @Test
    fun enviarAlDocenteConCamposObligatoriosVaciosMuestraError() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        viewModel.cambiarAprendizaje("   ")

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        assertEquals(
            "Complete los campos obligatorios (contenido, actividad y aprendizaje)",
            viewModel.uiState.value.mensaje
        )
        assertTrue(viewModel.uiState.value.mensajeEsError)
        coVerify(exactly = 0) { repository.enviarRegistro(any(), any()) }
    }

    // ============================================================================
    // 13. enviarAlDocente — flujo exitoso
    // ============================================================================

    @Test
    fun enviarAlDocenteNuevoCreaYLuegoEnviaElRegistro() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } returns RegistroDto(id = 20)
        coEvery { repository.enviarRegistro(any(), any()) } returns RegistroDto(id = 20)

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        coVerifyOrder {
            repository.crearRegistro(any())
            repository.enviarRegistro(20, "estudiante1")
        }
        coVerify(exactly = 0) { repository.actualizarRegistro(any(), any(), any()) }
        assertEquals(20L, viewModel.uiState.value.idRegistro)
        assertTrue(viewModel.uiState.value.guardado)
        assertEquals(
            "Registro enviado. Estado: pendiente de revisión.",
            viewModel.uiState.value.mensaje
        )
        assertFalse(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun enviarAlDocenteExistenteActualizaYLuegoEnviaElRegistro() {
        // ARRANGE — modo edición con un registro ya guardado
        prepararEdicion(crearRegistro(id = 5))
        coEvery { repository.actualizarRegistro(any(), any(), any()) } returns RegistroDto(id = 5)
        coEvery { repository.enviarRegistro(any(), any()) } returns RegistroDto(id = 5)

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        coVerifyOrder {
            repository.actualizarRegistro(5, "estudiante1", any())
            repository.enviarRegistro(5, "estudiante1")
        }
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
        assertTrue(viewModel.uiState.value.guardado)
        assertEquals(
            "Registro enviado. Estado: pendiente de revisión.",
            viewModel.uiState.value.mensaje
        )
    }

    @Test
    fun enviarAlDocenteMarcaGuardandoMientrasEspera() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.crearRegistro(any()) } coAnswers {
            compuerta.await()
            RegistroDto(id = 20)
        }
        coEvery { repository.enviarRegistro(any(), any()) } returns RegistroDto(id = 20)

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT — mientras espera
        assertTrue(viewModel.uiState.value.guardando)

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun enviarAlDocenteIgnoraUnSegundoIntentoMientrasEnvia() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.crearRegistro(any()) } coAnswers {
            compuerta.await()
            RegistroDto(id = 20)
        }
        coEvery { repository.enviarRegistro(any(), any()) } returns RegistroDto(id = 20)

        // ACT
        viewModel.enviarAlDocente()
        viewModel.enviarAlDocente()
        compuerta.complete(Unit)

        // ASSERT
        coVerify(exactly = 1) { repository.crearRegistro(any()) }
        coVerify(exactly = 1) { repository.enviarRegistro(any(), any()) }
    }

    // ============================================================================
    // 14. enviarAlDocente — errores
    // ============================================================================

    @Test
    fun enviarAlDocenteCuandoCrearFallaNoEnviaYNoGuardaId() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } throws IOException("sin red")

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertNull(viewModel.uiState.value.idRegistro)
        assertFalse(viewModel.uiState.value.guardado)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.enviarRegistro(any(), any()) }
    }

    @Test
    fun enviarAlDocenteCuandoElRegistroCreadoNoTraeIdMuestraError() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } returns RegistroDto(id = null)

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        assertEquals("No se obtuvo ID del registro creado", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardado)
        coVerify(exactly = 0) { repository.enviarRegistro(any(), any()) }
    }

    @Test
    fun enviarAlDocenteCuandoEnviarFallaConservaElIdDelRegistroCreado() {
        // ARRANGE — el registro se crea pero el envío falla
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } returns RegistroDto(id = 20)
        coEvery { repository.enviarRegistro(any(), any()) } throws crearHttpException(500)

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        assertEquals(20L, viewModel.uiState.value.idRegistro)
        assertEquals("Error interno del servidor", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardado)
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun enviarAlDocenteReintentoTrasFallaDeEnvioActualizaEnVezDeCrearDuplicado() {
        // ARRANGE — primer intento: se crea el registro y el envío falla
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } returns RegistroDto(id = 20)
        coEvery { repository.enviarRegistro(any(), any()) } throws IOException("sin red")
        viewModel.enviarAlDocente()

        coEvery { repository.actualizarRegistro(any(), any(), any()) } returns RegistroDto(id = 20)
        coEvery { repository.enviarRegistro(any(), any()) } returns RegistroDto(id = 20)

        // ACT — segundo intento
        viewModel.enviarAlDocente()

        // ASSERT
        coVerify(exactly = 1) { repository.crearRegistro(any()) }
        coVerify(exactly = 1) { repository.actualizarRegistro(20, "estudiante1", any()) }
        assertTrue(viewModel.uiState.value.guardado)
        assertEquals(
            "Registro enviado. Estado: pendiente de revisión.",
            viewModel.uiState.value.mensaje
        )
    }

    @Test
    fun enviarAlDocenteCuandoActualizarFallaNoEnvia() {
        // ARRANGE
        prepararEdicion(crearRegistro(id = 5))
        coEvery { repository.actualizarRegistro(any(), any(), any()) } throws crearHttpException(403)

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        assertEquals("No tienes permisos para realizar esta acción", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        coVerify(exactly = 0) { repository.enviarRegistro(any(), any()) }
    }

    @Test
    fun enviarAlDocenteConBitacoraSinIdMuestraBitacoraNoDisponible() {
        // ARRANGE — la bitácora cargada no trae id
        prepararCreacionConFormularioValido(crearBitacora(id = null))

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        assertEquals("Bitácora no disponible", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.crearRegistro(any()) }
    }

    @Test
    fun enviarAlDocenteCancelacionNoMuestraMensajeYDejaGuardandoEnFalse() {
        // ARRANGE
        prepararCreacionConFormularioValido()
        coEvery { repository.crearRegistro(any()) } throws CancellationException("cancelado")

        // ACT
        viewModel.enviarAlDocente()

        // ASSERT
        assertEquals("", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.guardado)
        assertFalse(viewModel.uiState.value.guardando)
    }
}