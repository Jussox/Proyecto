package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.AsignacionDto
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.BitacoraEdicionRequest
import cl.duoc.bitacora2.data.remoto.dto.BitacoraRequest
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
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class DocenteCrearBitacoraViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: DocenteCrearBitacoraViewModel

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
        viewModel = DocenteCrearBitacoraViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun crearAsignacion(): AsignacionDto = AsignacionDto(
        id = 1, docente = "docente1", curso = "3A", asignatura = "Programación"
    )

    private fun crearBitacora(
        id: Long? = 10,
        estado: String = "borrador"
    ): BitacoraDto = BitacoraDto(
        id = id, docente = "docente1", curso = "3A", asignatura = "Programación",
        mes = "Enero", anio = "2026", titulo = "Bitácora de prueba",
        proposito = "Propósito", instrucciones = "Instrucciones", preguntas = "Preguntas",
        estado = estado
    )

    private fun completarFormulario() {
        viewModel.cambiarMes("Enero")
        viewModel.cambiarAnio("2026")
        viewModel.cambiarTitulo("Bitácora de prueba")
        viewModel.cambiarProposito("Propósito")
        viewModel.cambiarInstrucciones("Instrucciones")
        viewModel.cambiarPreguntas("Preguntas")
    }

    private fun prepararCreacion() {
        coEvery { repository.obtenerAsignacionesDocente("docente1") } returns listOf(crearAsignacion())
        viewModel.inicializar(null, "docente1")
        viewModel.seleccionarAsignacion(crearAsignacion())
        completarFormulario()
    }

    private fun prepararEdicion(bitacora: BitacoraDto = crearBitacora()) {
        coEvery { repository.obtenerAsignacionesDocente("docente1") } returns listOf(crearAsignacion())
        coEvery { repository.obtenerBitacoraPorId(10) } returns bitacora
        viewModel.inicializar(10, "docente1")
    }

    @Test
    fun estadoInicialNoEstaCargandoNiGuardandoYNoTieneAsignaciones() {
        // ARRANGE — viewModel recién creado
        // ACT
        val estado = viewModel.uiState.value
        // ASSERT
        assertFalse(estado.cargando)
        assertFalse(estado.guardando)
        assertTrue(estado.asignaciones.isEmpty())
        assertNull(estado.error)
        assertNull(estado.idBitacoraCreada)
    }

    @Test
    fun inicializarEnModoCreacionCargaLasAsignacionesDelDocente() {
        // ARRANGE
        val asignaciones = listOf(crearAsignacion())
        coEvery { repository.obtenerAsignacionesDocente("docente1") } returns asignaciones
        // ACT
        viewModel.inicializar(null, "docente1")
        // ASSERT
        assertEquals(asignaciones, viewModel.uiState.value.asignaciones)
        assertFalse(viewModel.uiState.value.esModoEdicion)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(viewModel.uiState.value.error)
        coVerify(exactly = 1) { repository.obtenerAsignacionesDocente("docente1") }
    }

    @Test
    fun inicializarEnModoEdicionCargaLosDatosDeLaBitacoraEnBorrador() {
        // ARRANGE
        prepararEdicion()
        // ACT
        val estado = viewModel.uiState.value
        // ASSERT
        assertTrue(estado.esModoEdicion)
        assertFalse(estado.estadoInvalidoParaEdicion)
        assertEquals(10L, estado.idBitacoraCreada)
        assertEquals("3A", estado.cursoFijo)
        assertEquals("Programación", estado.asignaturaFija)
        assertEquals("Enero", estado.mes)
        assertEquals("2026", estado.anio)
        assertEquals("Bitácora de prueba", estado.titulo)
        assertEquals("Propósito", estado.proposito)
        assertEquals("Instrucciones", estado.instrucciones)
        assertEquals("Preguntas", estado.preguntas)
    }

    @Test
    fun inicializarEnModoEdicionConBitacoraPublicadaMarcaEstadoInvalido() {
        // ARRANGE
        coEvery { repository.obtenerAsignacionesDocente("docente1") } returns listOf(crearAsignacion())
        coEvery { repository.obtenerBitacoraPorId(10) } returns crearBitacora(estado = "publicada")
        // ACT
        viewModel.inicializar(10, "docente1")
        // ASSERT
        assertTrue(viewModel.uiState.value.esModoEdicion)
        assertTrue(viewModel.uiState.value.estadoInvalidoParaEdicion)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun inicializarConErrorDeConexionMuestraMensajeDeError() {
        // ARRANGE
        coEvery { repository.obtenerAsignacionesDocente("docente1") } throws IOException("sin red")
        // ACT
        viewModel.inicializar(null, "docente1")
        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun seleccionarAsignacionActualizaLaAsignacionSeleccionada() {
        // ARRANGE
        val asignacion = crearAsignacion()
        // ACT
        viewModel.seleccionarAsignacion(asignacion)
        // ASSERT
        assertEquals(asignacion, viewModel.uiState.value.asignacionSeleccionada)
    }

    @Test
    fun cambiarCamposActualizaLosValoresDelFormulario() {
        // ARRANGE
        // ACT
        completarFormulario()
        // ASSERT
        val estado = viewModel.uiState.value
        assertEquals("Enero", estado.mes)
        assertEquals("2026", estado.anio)
        assertEquals("Bitácora de prueba", estado.titulo)
        assertEquals("Propósito", estado.proposito)
        assertEquals("Instrucciones", estado.instrucciones)
        assertEquals("Preguntas", estado.preguntas)
    }

    @Test
    fun guardarBorradorSinAsignacionMuestraErrorYNoCreaBitacora() {
        // ARRANGE
        completarFormulario()
        // ACT
        viewModel.guardarBorrador("docente1")
        // ASSERT
        assertEquals("Seleccione un curso y asignatura", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        coVerify(exactly = 0) { repository.crearBitacora(any()) }
    }

    @Test
    fun guardarBorradorConCamposVaciosMuestraErrorYNoCreaBitacora() {
        // ARRANGE
        viewModel.seleccionarAsignacion(crearAsignacion())
        // ACT
        viewModel.guardarBorrador("docente1")
        // ASSERT
        assertEquals("Complete todos los campos obligatorios", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        coVerify(exactly = 0) { repository.crearBitacora(any()) }
    }

    @Test
    fun guardarBorradorConDatosValidosCreaLaBitacora() {
        // ARRANGE
        prepararCreacion()
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 25)
        // ACT
        viewModel.guardarBorrador("docente1")
        // ASSERT
        assertEquals(25L, viewModel.uiState.value.idBitacoraCreada)
        assertTrue(viewModel.uiState.value.guardado)
        assertFalse(viewModel.uiState.value.mensajeEsError)
        assertEquals("Bitácora guardada. Puedes publicarla desde «Mis bitácoras mensuales».", viewModel.uiState.value.mensaje)
        coVerify(exactly = 1) { repository.crearBitacora(any()) }
    }

    @Test
    fun guardarBorradorEnviaLosDatosCorrectosAlRepositorio() {
        // ARRANGE
        prepararCreacion()
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 25)
        // ACT
        viewModel.guardarBorrador("docente1")
        // ASSERT
        coVerify(exactly = 1) {
            repository.crearBitacora(match<BitacoraRequest> {
                it.docente == "docente1" && it.curso == "3A" &&
                    it.asignatura == "Programación" && it.mes == "Enero" &&
                    it.anio == "2026" && it.titulo == "Bitácora de prueba"
            })
        }
    }

    @Test
    fun guardarBorradorEnModoEdicionActualizaLaBitacoraExistente() {
        // ARRANGE
        prepararEdicion()
        coEvery { repository.editarBitacora(10, "docente1", any()) } returns crearBitacora()
        viewModel.cambiarTitulo("Título actualizado")
        // ACT
        viewModel.guardarBorrador("docente1")
        // ASSERT
        assertTrue(viewModel.uiState.value.guardado)
        assertEquals("Cambios guardados como borrador.", viewModel.uiState.value.mensaje)
        coVerify(exactly = 1) { repository.editarBitacora(10, "docente1", match<BitacoraEdicionRequest> { it.titulo == "Título actualizado" }) }
        coVerify(exactly = 0) { repository.crearBitacora(any()) }
    }

    @Test
    fun guardarBorradorConErrorDelRepositorioMuestraMensaje() {
        // ARRANGE
        prepararCreacion()
        coEvery { repository.crearBitacora(any()) } throws IOException("sin red")
        // ACT
        viewModel.guardarBorrador("docente1")
        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun guardarYPublicarEnModoCreacionCreaYPublicaLaBitacora() {
        // ARRANGE
        prepararCreacion()
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 30)
        coEvery { repository.publicarBitacora(30, "docente1") } returns crearBitacora(id = 30, estado = "publicada")
        // ACT
        viewModel.guardarYPublicar("docente1")
        // ASSERT
        coVerifyOrder {
            repository.crearBitacora(any())
            repository.publicarBitacora(30, "docente1")
        }
        assertEquals(30L, viewModel.uiState.value.idBitacoraCreada)
        assertTrue(viewModel.uiState.value.guardado)
        assertEquals("Bitácora guardada y publicada.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun guardarYPublicarEnModoEdicionActualizaAntesDePublicar() {
        // ARRANGE
        prepararEdicion()
        coEvery { repository.editarBitacora(10, "docente1", any()) } returns crearBitacora()
        coEvery { repository.publicarBitacora(10, "docente1") } returns crearBitacora(estado = "publicada")
        // ACT
        viewModel.guardarYPublicar("docente1")
        // ASSERT
        coVerifyOrder {
            repository.editarBitacora(10, "docente1", any())
            repository.publicarBitacora(10, "docente1")
        }
        assertTrue(viewModel.uiState.value.guardado)
    }

    @Test
    fun guardarYPublicarSiFallaLaPublicacionConservaLaBitacoraComoBorrador() {
        // ARRANGE
        prepararCreacion()
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 30)
        coEvery { repository.publicarBitacora(30, "docente1") } throws IOException("sin red")
        // ACT
        viewModel.guardarYPublicar("docente1")
        // ASSERT
        assertEquals(30L, viewModel.uiState.value.idBitacoraCreada)
        assertTrue(viewModel.uiState.value.mensaje.startsWith("Quedó guardada como borrador."))
        assertTrue(viewModel.uiState.value.mensajeEsError)
    }

    @Test
    fun guardarCambiosActualizaLaBitacoraEnModoEdicion() {
        // ARRANGE
        prepararEdicion()
        coEvery { repository.editarBitacora(10, "docente1", any()) } returns crearBitacora()
        viewModel.cambiarTitulo("Título nuevo")
        // ACT
        viewModel.guardarCambios("docente1")
        // ASSERT
        assertTrue(viewModel.uiState.value.guardado)
        assertEquals("Cambios guardados.", viewModel.uiState.value.mensaje)
        coVerify(exactly = 1) { repository.editarBitacora(10, "docente1", any()) }
    }


    @Test
    fun guardarYPublicarEnModoEdicionConCamposInvalidosMuestraErrorYNoActualiza() {
        // ARRANGE
        prepararEdicion()
        viewModel.cambiarTitulo("")
        // ACT
        viewModel.guardarYPublicar("docente1")
        // ASSERT
        assertEquals("Complete todos los campos obligatorios", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.editarBitacora(any(), any(), any()) }
        coVerify(exactly = 0) { repository.publicarBitacora(any(), any()) }
    }

    @Test
    fun guardarYPublicarConErrorAlCrearMuestraElMensajeDelError() {
        // ARRANGE
        prepararCreacion()
        coEvery { repository.crearBitacora(any()) } throws IOException("sin red")
        // ACT
        viewModel.guardarYPublicar("docente1")
        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertNull(viewModel.uiState.value.idBitacoraCreada)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.publicarBitacora(any(), any()) }
    }

    @Test
    fun guardarYPublicarConErrorAlEditarMuestraElMensajeDelError() {
        // ARRANGE
        prepararEdicion()
        coEvery { repository.editarBitacora(10, "docente1", any()) } throws IOException("sin red")
        // ACT
        viewModel.guardarYPublicar("docente1")
        // ASSERT
        assertEquals(
            "Quedó guardada como borrador. No hay conexión con el servidor",
            viewModel.uiState.value.mensaje
        )
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.publicarBitacora(any(), any()) }
    }

    @Test
    fun guardarCambiosConErrorDelRepositorioMuestraMensajeDeError() {
        // ARRANGE
        prepararEdicion()
        coEvery { repository.editarBitacora(10, "docente1", any()) } throws IOException("sin red")
        // ACT
        viewModel.guardarCambios("docente1")
        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
        assertFalse(viewModel.uiState.value.guardado)
    }

    @Test
    fun guardarCambiosConCamposInvalidosMuestraErrorYNoActualiza() {
        // ARRANGE
        prepararEdicion()
        viewModel.cambiarTitulo("")
        // ACT
        viewModel.guardarCambios("docente1")
        // ASSERT
        assertEquals("Complete todos los campos obligatorios", viewModel.uiState.value.mensaje)
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.editarBitacora(any(), any(), any()) }
    }

    @Test
    fun guardarCambiosSinIdDeBitacoraNoLlamaAlRepositorio() {
        // ARRANGE — formulario válido, pero en modo creación no existe ID para editar
        completarFormulario()
        // ACT
        viewModel.guardarCambios("docente1")
        // ASSERT
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.editarBitacora(any(), any(), any()) }
    }

    @Test
    fun guardarCambiosMientrasYaGuardaNoIniciaOtraActualizacion() {
        // ARRANGE
        prepararEdicion()
        val permitirFinalizar = CompletableDeferred<Unit>()
        coEvery { repository.editarBitacora(10, "docente1", any()) } coAnswers {
            permitirFinalizar.await()
            crearBitacora()
        }
        // ACT
        viewModel.guardarCambios("docente1")
        assertTrue(viewModel.uiState.value.guardando)
        viewModel.guardarCambios("docente1")
        permitirFinalizar.complete(Unit)
        // ASSERT
        coVerify(exactly = 1) { repository.editarBitacora(10, "docente1", any()) }
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun limpiarMensajeEliminaElMensajeYLaMarcaDeError() {
        // ARRANGE — generar un mensaje de validación
        viewModel.guardarBorrador("docente1")
        assertTrue(viewModel.uiState.value.mensaje.isNotEmpty())
        // ACT
        viewModel.limpiarMensaje()
        // ASSERT
        assertEquals("", viewModel.uiState.value.mensaje)
        assertFalse(viewModel.uiState.value.mensajeEsError)
    }


    @Test
    fun inicializarEnModoEdicionConCamposNulosReemplazaLosValoresPorCadenasVacias() {
        // ARRANGE
        val bitacoraConCamposNulos = BitacoraDto(
            id = 10,
            docente = "docente1",
            curso = null,
            asignatura = null,
            mes = null,
            anio = null,
            titulo = null,
            proposito = null,
            instrucciones = null,
            preguntas = null,
            estado = "borrador"
        )
        coEvery { repository.obtenerAsignacionesDocente("docente1") } returns emptyList()
        coEvery { repository.obtenerBitacoraPorId(10) } returns bitacoraConCamposNulos

        // ACT
        viewModel.inicializar(10, "docente1")

        // ASSERT
        val estado = viewModel.uiState.value
        assertEquals("", estado.cursoFijo)
        assertEquals("", estado.asignaturaFija)
        assertEquals("", estado.mes)
        assertEquals("", estado.anio)
        assertEquals("", estado.titulo)
        assertEquals("", estado.proposito)
        assertEquals("", estado.instrucciones)
        assertEquals("", estado.preguntas)
    }

    @Test
    fun guardarBorradorConAsignacionSinCursoNiAsignaturaEnviaCadenasVaciasYCapitalizaMes() {
        // ARRANGE
        val asignacionSinDatos = AsignacionDto(
            id = 2, docente = "docente1", curso = null, asignatura = null
        )
        coEvery { repository.obtenerAsignacionesDocente("docente1") } returns listOf(asignacionSinDatos)
        viewModel.inicializar(null, "docente1")
        viewModel.seleccionarAsignacion(asignacionSinDatos)
        viewModel.cambiarMes("enero")
        viewModel.cambiarAnio("2026")
        viewModel.cambiarTitulo("Bitácora de prueba")
        viewModel.cambiarProposito("Propósito")
        viewModel.cambiarInstrucciones("Instrucciones")
        viewModel.cambiarPreguntas("Preguntas")
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 26)

        // ACT
        viewModel.guardarBorrador("docente1")

        // ASSERT
        coVerify(exactly = 1) {
            repository.crearBitacora(match<BitacoraRequest> {
                it.curso == "" && it.asignatura == "" && it.mes == "Enero"
            })
        }
    }


    @Test
    fun guardarBorradorMientrasYaGuardaNoIniciaOtraOperacion() {
        // ARRANGE
        prepararCreacion()
        val permitirFinalizar = CompletableDeferred<Unit>()
        coEvery { repository.crearBitacora(any()) } coAnswers {
            permitirFinalizar.await()
            crearBitacora(id = 40)
        }
        // ACT
        viewModel.guardarBorrador("docente1")
        assertTrue(viewModel.uiState.value.guardando)
        viewModel.guardarBorrador("docente1")
        permitirFinalizar.complete(Unit)
        // ASSERT
        coVerify(exactly = 1) { repository.crearBitacora(any()) }
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun guardarYPublicarMientrasYaGuardaNoIniciaOtraOperacion() {
        // ARRANGE
        prepararCreacion()
        val permitirFinalizar = CompletableDeferred<Unit>()
        coEvery { repository.crearBitacora(any()) } coAnswers {
            permitirFinalizar.await()
            crearBitacora(id = 41)
        }
        coEvery { repository.publicarBitacora(41, "docente1") } returns crearBitacora(id = 41, estado = "publicada")
        // ACT
        viewModel.guardarYPublicar("docente1")
        assertTrue(viewModel.uiState.value.guardando)
        viewModel.guardarYPublicar("docente1")
        permitirFinalizar.complete(Unit)
        // ASSERT
        coVerify(exactly = 1) { repository.crearBitacora(any()) }
        coVerify(exactly = 1) { repository.publicarBitacora(41, "docente1") }
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun guardarYPublicarCuandoLaBitacoraCreadaNoTieneIdMuestraError() {
        // ARRANGE
        prepararCreacion()
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = null)
        // ACT
        viewModel.guardarYPublicar("docente1")
        // ASSERT
        assertTrue(viewModel.uiState.value.mensajeEsError)
        assertEquals("No se obtuvo id de la bitácora creada", viewModel.uiState.value.mensaje)
        assertNull(viewModel.uiState.value.idBitacoraCreada)
        coVerify(exactly = 0) { repository.publicarBitacora(any(), any()) }
    }

    @Test
    fun guardarBorradorEnModoEdicionSinIdUsaLaAsignacionParaCrear() {
        // ARRANGE
        val asignacion = crearAsignacion()
        coEvery { repository.obtenerAsignacionesDocente("docente1") } returns listOf(asignacion)
        coEvery { repository.obtenerBitacoraPorId(10) } returns crearBitacora(id = null)
        viewModel.inicializar(10, "docente1")
        viewModel.seleccionarAsignacion(asignacion)
        completarFormulario()
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 42)
        // ACT
        viewModel.guardarBorrador("docente1")
        // ASSERT
        coVerify(exactly = 1) { repository.crearBitacora(any()) }
        assertEquals(42L, viewModel.uiState.value.idBitacoraCreada)
    }

    @Test
    fun guardarYPublicarEnModoEdicionSinIdCreaLaBitacoraYLaPublica() {
        // ARRANGE
        val asignacion = crearAsignacion()
        coEvery { repository.obtenerAsignacionesDocente("docente1") } returns listOf(asignacion)
        coEvery { repository.obtenerBitacoraPorId(10) } returns crearBitacora(id = null)
        viewModel.inicializar(10, "docente1")
        viewModel.seleccionarAsignacion(asignacion)
        completarFormulario()
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 43)
        coEvery { repository.publicarBitacora(43, "docente1") } returns crearBitacora(id = 43, estado = "publicada")
        // ACT
        viewModel.guardarYPublicar("docente1")
        // ASSERT
        coVerifyOrder {
            repository.crearBitacora(any())
            repository.publicarBitacora(43, "docente1")
        }
        assertTrue(viewModel.uiState.value.guardado)
    }

    @Test
    fun guardarBorradorConMesEnMayusculasLoConservaAlCrear() {
        // ARRANGE
        prepararCreacion()
        viewModel.cambiarMes("ENERO")
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 44)
        // ACT
        viewModel.guardarBorrador("docente1")
        // ASSERT
        coVerify(exactly = 1) {
            repository.crearBitacora(match<BitacoraRequest> { it.mes == "ENERO" })
        }
    }

    @Test
    fun guardarYPublicarConMesEnMayusculasLoConservaAlCrear() {
        // ARRANGE
        prepararCreacion()
        viewModel.cambiarMes("ENERO")
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 45)
        coEvery { repository.publicarBitacora(45, "docente1") } returns
            crearBitacora(id = 45, estado = "publicada")

        // ACT
        viewModel.guardarYPublicar("docente1")

        // ASSERT
        coVerify(exactly = 1) {
            repository.crearBitacora(match<BitacoraRequest> { it.mes == "ENERO" })
        }
        coVerify(exactly = 1) { repository.publicarBitacora(45, "docente1") }
        assertTrue(viewModel.uiState.value.guardado)
    }

    @Test
    fun guardarYPublicarConAsignacionSinCursoNiAsignaturaEnviaCadenasVaciasYCapitalizaMes() {
        // ARRANGE
        val asignacionSinDatos = AsignacionDto(
            id = 3, docente = "docente1", curso = null, asignatura = null
        )
        coEvery { repository.obtenerAsignacionesDocente("docente1") } returns listOf(asignacionSinDatos)
        viewModel.inicializar(null, "docente1")
        viewModel.seleccionarAsignacion(asignacionSinDatos)
        viewModel.cambiarMes("enero")
        viewModel.cambiarAnio("2026")
        viewModel.cambiarTitulo("Bitácora de prueba")
        viewModel.cambiarProposito("Propósito")
        viewModel.cambiarInstrucciones("Instrucciones")
        viewModel.cambiarPreguntas("Preguntas")
        coEvery { repository.crearBitacora(any()) } returns crearBitacora(id = 46)
        coEvery { repository.publicarBitacora(46, "docente1") } returns
            crearBitacora(id = 46, estado = "publicada")

        // ACT
        viewModel.guardarYPublicar("docente1")

        // ASSERT
        coVerify(exactly = 1) {
            repository.crearBitacora(match<BitacoraRequest> {
                it.curso == "" && it.asignatura == "" && it.mes == "Enero"
            })
        }
        coVerify(exactly = 1) { repository.publicarBitacora(46, "docente1") }
        assertTrue(viewModel.uiState.value.guardado)
    }

}
