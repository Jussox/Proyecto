package cl.duoc.bitacora2.ui.theme.Docente

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.data.remoto.dto.ComentarioRequest
import cl.duoc.bitacora2.data.remoto.dto.EstudianteDto
import cl.duoc.bitacora2.data.remoto.dto.RegistroDto
import cl.duoc.bitacora2.util.Sesion
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
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
class DocenteRevisionViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: DocenteRevisionViewModel

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Sesion.usuario = "docente1"
        repository = mockk()
        viewModel = DocenteRevisionViewModel(repository)
    }

    @After
    fun limpiar() {
        Sesion.cerrar()
        Dispatchers.resetMain()
    }

    @Test
    fun estadoInicialNoEstaCargandoNiGuardandoYNoTieneRegistro() {
        val estado = viewModel.uiState.value

        assertFalse(estado.cargando)
        assertFalse(estado.guardando)
        assertFalse(estado.guardado)
        assertNull(estado.registro)
        assertNull(estado.bitacora)
        assertEquals("", estado.nombreEstudiante)
        assertEquals("", estado.comentario)
        assertNull(estado.error)
        assertNull(estado.errorOperacion)
    }

    @Test
    fun cargarRevisionPorBitacoraGuardaRegistroBitacoraEstudianteYComentario() {
        val registro = RegistroDto(id = 10L, estudiante = "estudiante1", comentario = "Mi avance")
        val bitacora = BitacoraDto(id = 5L, titulo = "Bitácora noviembre")
        coEvery { repository.obtenerEstudiantes() } returns listOf(
            EstudianteDto(usuario = "estudiante1", nombre = "Ana Pérez")
        )
        coEvery { repository.obtenerRegistrosBitacora(5L, "docente1") } returns listOf(registro)
        coEvery { repository.obtenerBitacoraPorId(5L) } returns bitacora

        viewModel.cargarRevision(10L, 5L)

        val estado = viewModel.uiState.value
        assertEquals(registro, estado.registro)
        assertEquals(bitacora, estado.bitacora)
        assertEquals("Ana Pérez", estado.nombreEstudiante)
        assertEquals("Mi avance", estado.comentario)
        assertEquals("Mi avance", estado.comentarioOriginal)
        assertFalse(estado.cargando)
        assertNull(estado.error)
    }

    @Test
    fun cargarRevisionUsaElUsuarioCuandoNoEncuentraNombreDelEstudiante() {
        val registro = RegistroDto(id = 11L, estudiante = "estudiante2", comentario = null)
        coEvery { repository.obtenerEstudiantes() } returns listOf(
            EstudianteDto(usuario = "estudiante2", nombre = " ")
        )
        coEvery { repository.obtenerRegistrosBitacora(4L, "docente1") } returns listOf(registro)
        coEvery { repository.obtenerBitacoraPorId(4L) } returns BitacoraDto(id = 4L)

        viewModel.cargarRevision(11L, 4L)

        assertEquals("estudiante2", viewModel.uiState.value.nombreEstudiante)
        assertEquals("", viewModel.uiState.value.comentario)
        assertEquals("", viewModel.uiState.value.comentarioOriginal)
    }

    @Test
    fun cargarRevisionUsaNombreGenericoCuandoElRegistroNoTieneEstudiante() {
        val registro = RegistroDto(id = 12L, estudiante = null)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(3L, "docente1") } returns listOf(registro)
        coEvery { repository.obtenerBitacoraPorId(3L) } returns BitacoraDto(id = 3L)

        viewModel.cargarRevision(12L, 3L)

        assertEquals("Estudiante", viewModel.uiState.value.nombreEstudiante)
    }

    @Test
    fun cargarRevisionIgnoraEstudiantesConUsuarioONombreVacio() {
        val registro = RegistroDto(id = 13L, estudiante = "estudiante3")
        coEvery { repository.obtenerEstudiantes() } returns listOf(
            EstudianteDto(usuario = null, nombre = "Sin usuario"),
            EstudianteDto(usuario = "", nombre = "Sin usuario"),
            EstudianteDto(usuario = "estudiante3", nombre = null),
            EstudianteDto(usuario = "estudiante4", nombre = "Carla")
        )
        coEvery { repository.obtenerRegistrosBitacora(2L, "docente1") } returns listOf(registro)
        coEvery { repository.obtenerBitacoraPorId(2L) } returns BitacoraDto(id = 2L)

        viewModel.cargarRevision(13L, 2L)

        assertEquals("estudiante3", viewModel.uiState.value.nombreEstudiante)
    }

    @Test
    fun cargarRevisionContinuaAunqueFalleLaConsultaDeEstudiantes() {
        val registro = RegistroDto(id = 14L, estudiante = "estudiante5")
        coEvery { repository.obtenerEstudiantes() } throws IOException("sin conexión")
        coEvery { repository.obtenerRegistrosBitacora(6L, "docente1") } returns listOf(registro)
        coEvery { repository.obtenerBitacoraPorId(6L) } returns BitacoraDto(id = 6L)

        viewModel.cargarRevision(14L, 6L)

        assertEquals(registro, viewModel.uiState.value.registro)
        assertEquals("estudiante5", viewModel.uiState.value.nombreEstudiante)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarRevisionContinuaSinBitacoraCuandoFallaObtenerBitacoraPorId() {
        val registro = RegistroDto(id = 15L)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(7L, "docente1") } returns listOf(registro)
        coEvery { repository.obtenerBitacoraPorId(7L) } throws IOException("no disponible")

        viewModel.cargarRevision(15L, 7L)

        assertEquals(registro, viewModel.uiState.value.registro)
        assertNull(viewModel.uiState.value.bitacora)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun cargarRevisionPropagaLaCancelacionAlConsultarLaBitacoraPorId() {
        val registro = RegistroDto(id = 150L)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(7L, "docente1") } returns listOf(registro)
        coEvery { repository.obtenerBitacoraPorId(7L) } throws CancellationException("consulta cancelada")

        viewModel.cargarRevision(150L, 7L)

        assertNull(viewModel.uiState.value.registro)
        assertNull(viewModel.uiState.value.bitacora)
        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarRevisionBuscaEnTodasLasBitacorasCuandoNoSeEntregaIdValido() {
        val registro = RegistroDto(id = 16L, estudiante = "estudiante6")
        val bitacora = BitacoraDto(id = 9L, titulo = "Encontrada")
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns listOf(
            BitacoraDto(id = null), BitacoraDto(id = 8L), bitacora
        )
        coEvery { repository.obtenerRegistrosBitacora(8L, "docente1") } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(9L, "docente1") } returns listOf(registro)

        viewModel.cargarRevision(16L, null)

        assertEquals(registro, viewModel.uiState.value.registro)
        assertEquals(bitacora, viewModel.uiState.value.bitacora)
    }

    @Test
    fun cargarRevisionBuscaEnTodasLasBitacorasCuandoElRegistroNoEstaEnLaBitacoraIndicada() {
        val registro = RegistroDto(id = 17L)
        val bitacoraEncontrada = BitacoraDto(id = 12L)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(2L, "docente1") } returns emptyList()
        coEvery { repository.obtenerBitacoraPorId(2L) } returns BitacoraDto(id = 2L)
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns listOf(bitacoraEncontrada)
        coEvery { repository.obtenerRegistrosBitacora(12L, "docente1") } returns listOf(registro)

        viewModel.cargarRevision(17L, 2L)

        assertEquals(registro, viewModel.uiState.value.registro)
        assertEquals(bitacoraEncontrada, viewModel.uiState.value.bitacora)
    }

    @Test
    fun cargarRevisionContinuaBuscandoCuandoFallaLaConsultaDeUnaBitacora() {
        val registro = RegistroDto(id = 18L)
        val bitacoraEncontrada = BitacoraDto(id = 22L)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns
            listOf(BitacoraDto(id = 21L), bitacoraEncontrada)
        coEvery { repository.obtenerRegistrosBitacora(21L, "docente1") } throws IOException("fallo parcial")
        coEvery { repository.obtenerRegistrosBitacora(22L, "docente1") } returns listOf(registro)

        viewModel.cargarRevision(18L, null)

        assertEquals(registro, viewModel.uiState.value.registro)
        assertEquals(bitacoraEncontrada, viewModel.uiState.value.bitacora)
    }

    @Test
    fun cargarRevisionMuestraErrorCuandoElAporteNoExiste() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns emptyList()

        viewModel.cargarRevision(99L, null)

        assertEquals("El aporte no está disponible.", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.registro)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cargarRevisionMuestraErrorCuandoFallaLaConsultaPrincipal() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(5L, "docente1") } throws IOException("sin conexión")

        viewModel.cargarRevision(20L, 5L)

        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun cambiarComentarioActualizaTextoYLimpiaAvisos() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(1L, "docente1") } returns listOf(RegistroDto(id = 1L))
        coEvery { repository.obtenerBitacoraPorId(1L) } returns BitacoraDto(id = 1L)
        viewModel.cargarRevision(1L, 1L)
        viewModel.guardarComentario()

        viewModel.cambiarComentario("  Buen trabajo  ")

        val estado = viewModel.uiState.value
        assertEquals("  Buen trabajo  ", estado.comentario)
        assertNull(estado.errorComentario)
        assertNull(estado.errorOperacion)
        assertNull(estado.mensajeExito)
    }

    @Test
    fun guardarComentarioGuardaTextoSinEspaciosExterioresYEjecutaCallback() {
        var callbackEjecutado = false
        val registro = RegistroDto(id = 30L, comentario = "Comentario guardado")
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(30L, "docente1") } returns listOf(RegistroDto(id = 30L))
        coEvery { repository.obtenerBitacoraPorId(30L) } returns BitacoraDto(id = 30L)
        coEvery { repository.agregarComentario(30L, "docente1", ComentarioRequest("Comentario nuevo")) } returns registro
        viewModel.cargarRevision(30L, 30L)
        viewModel.cambiarComentario("  Comentario nuevo  ")

        viewModel.guardarComentario { callbackEjecutado = true }

        val estado = viewModel.uiState.value
        assertEquals(registro, estado.registro)
        assertEquals("Comentario guardado", estado.comentarioOriginal)
        assertEquals("Comentario guardado", estado.mensajeExito)
        assertFalse(estado.guardando)
        assertTrue(callbackEjecutado)
    }

    @Test
    fun guardarComentarioMuestraErrorCuandoElComentarioEstaVacio() {
        viewModel.cambiarComentario("   ")

        viewModel.guardarComentario()

        assertEquals("Escriba un comentario antes de guardar", viewModel.uiState.value.errorComentario)
        coVerify(exactly = 0) { repository.agregarComentario(any(), any(), any()) }
    }

    @Test
    fun guardarComentarioMuestraErrorCuandoSuperaElMaximoPermitido() {
        viewModel.cambiarComentario("a".repeat(2001))

        viewModel.guardarComentario()

        assertEquals("El comentario no puede superar los 2000 caracteres", viewModel.uiState.value.errorComentario)
        coVerify(exactly = 0) { repository.agregarComentario(any(), any(), any()) }
    }

    @Test
    fun guardarComentarioNoHaceNadaCuandoNoHayRegistroCargado() {
        viewModel.cambiarComentario("Comentario válido")

        viewModel.guardarComentario()

        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.agregarComentario(any(), any(), any()) }
    }

    @Test
    fun guardarComentarioMuestraErrorCuandoFallaElRepositorio() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(31L, "docente1") } returns listOf(RegistroDto(id = 31L))
        coEvery { repository.obtenerBitacoraPorId(31L) } returns BitacoraDto(id = 31L)
        coEvery { repository.agregarComentario(31L, "docente1", any()) } throws IOException("sin conexión")
        viewModel.cargarRevision(31L, 31L)
        viewModel.cambiarComentario("Comentario válido")

        viewModel.guardarComentario()

        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorOperacion)
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun marcarRevisadoSinCambiosGuardaRevisionSinAgregarComentario() {
        var callbackEjecutado = false
        val registroInicial = RegistroDto(id = 40L, comentario = "Ya comentado")
        val registroRevisado = registroInicial.copy(estado = "revisado")
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(40L, "docente1") } returns listOf(registroInicial)
        coEvery { repository.obtenerBitacoraPorId(40L) } returns BitacoraDto(id = 40L)
        coEvery { repository.marcarRevisado(40L, "docente1") } returns registroRevisado
        viewModel.cargarRevision(40L, 40L)

        viewModel.marcarRevisado { callbackEjecutado = true }

        assertTrue(viewModel.uiState.value.guardado)
        assertEquals("Aporte marcado como revisado", viewModel.uiState.value.mensajeExito)
        assertEquals(registroRevisado, viewModel.uiState.value.registro)
        assertTrue(callbackEjecutado)
        coVerify(exactly = 0) { repository.agregarComentario(any(), any(), any()) }
    }

    @Test
    fun marcarRevisadoGuardaComentarioModificadoAntesDeMarcar() {
        val registroInicial = RegistroDto(id = 41L, comentario = "Comentario anterior")
        val registroConComentario = registroInicial.copy(comentario = "Comentario nuevo")
        val registroRevisado = registroConComentario.copy(estado = "revisado")
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(41L, "docente1") } returns listOf(registroInicial)
        coEvery { repository.obtenerBitacoraPorId(41L) } returns BitacoraDto(id = 41L)
        coEvery { repository.agregarComentario(41L, "docente1", ComentarioRequest("Comentario nuevo")) } returns registroConComentario
        coEvery { repository.marcarRevisado(41L, "docente1") } returns registroRevisado
        viewModel.cargarRevision(41L, 41L)
        viewModel.cambiarComentario("Comentario nuevo")

        viewModel.marcarRevisado()

        assertEquals(registroRevisado, viewModel.uiState.value.registro)
        assertTrue(viewModel.uiState.value.guardado)
        coVerify(exactly = 1) { repository.agregarComentario(41L, "docente1", ComentarioRequest("Comentario nuevo")) }
        coVerify(exactly = 1) { repository.marcarRevisado(41L, "docente1") }
    }

    @Test
    fun marcarRevisadoAgregaComentarioCuandoElOriginalEstaVacio() {
        val registroInicial = RegistroDto(id = 42L, comentario = "")
        val registroConComentario = registroInicial.copy(comentario = "Primera revisión")
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(42L, "docente1") } returns listOf(registroInicial)
        coEvery { repository.obtenerBitacoraPorId(42L) } returns BitacoraDto(id = 42L)
        coEvery { repository.agregarComentario(42L, "docente1", ComentarioRequest("Primera revisión")) } returns registroConComentario
        coEvery { repository.marcarRevisado(42L, "docente1") } returns registroConComentario.copy(estado = "revisado")
        viewModel.cargarRevision(42L, 42L)
        viewModel.cambiarComentario("Primera revisión")

        viewModel.marcarRevisado()

        coVerify(exactly = 1) { repository.agregarComentario(42L, "docente1", ComentarioRequest("Primera revisión")) }
        assertTrue(viewModel.uiState.value.guardado)
    }

    @Test
    fun marcarRevisadoMuestraErrorCuandoElComentarioEstaVacio() {
        viewModel.cambiarComentario(" ")

        viewModel.marcarRevisado()

        assertEquals("Escriba un comentario antes de marcar como revisado", viewModel.uiState.value.errorComentario)
        coVerify(exactly = 0) { repository.marcarRevisado(any(), any()) }
    }

    @Test
    fun marcarRevisadoMuestraErrorCuandoElComentarioSuperaElMaximo() {
        viewModel.cambiarComentario("x".repeat(2001))

        viewModel.marcarRevisado()

        assertEquals("El comentario no puede superar los 2000 caracteres", viewModel.uiState.value.errorComentario)
        coVerify(exactly = 0) { repository.marcarRevisado(any(), any()) }
    }

    @Test
    fun marcarRevisadoNoHaceNadaCuandoNoHayRegistroCargado() {
        viewModel.cambiarComentario("Comentario válido")

        viewModel.marcarRevisado()

        coVerify(exactly = 0) { repository.marcarRevisado(any(), any()) }
    }

    @Test
    fun marcarRevisadoMuestraErrorCuandoFallaLaActualizacionDelComentario() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(43L, "docente1") } returns listOf(RegistroDto(id = 43L, comentario = "Anterior"))
        coEvery { repository.obtenerBitacoraPorId(43L) } returns BitacoraDto(id = 43L)
        coEvery { repository.agregarComentario(43L, "docente1", any()) } throws IOException("falló comentario")
        viewModel.cargarRevision(43L, 43L)
        viewModel.cambiarComentario("Nuevo comentario")

        viewModel.marcarRevisado()

        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorOperacion)
        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.marcarRevisado(43L, "docente1") }
    }

    @Test
    fun marcarRevisadoMuestraErrorCuandoFallaElMarcado() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(44L, "docente1") } returns listOf(RegistroDto(id = 44L, comentario = "Comentario"))
        coEvery { repository.obtenerBitacoraPorId(44L) } returns BitacoraDto(id = 44L)
        coEvery { repository.marcarRevisado(44L, "docente1") } throws IOException("falló revisión")
        viewModel.cargarRevision(44L, 44L)

        viewModel.marcarRevisado()

        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.errorOperacion)
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun limpiarAvisosEliminaErrorDeOperacionYMensajeDeExito() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(45L, "docente1") } returns listOf(RegistroDto(id = 45L))
        coEvery { repository.obtenerBitacoraPorId(45L) } returns BitacoraDto(id = 45L)
        coEvery { repository.agregarComentario(45L, "docente1", any()) } throws IOException("error temporal")
        viewModel.cargarRevision(45L, 45L)
        viewModel.cambiarComentario("Comentario")
        viewModel.guardarComentario()

        viewModel.limpiarAvisos()

        assertNull(viewModel.uiState.value.errorOperacion)
        assertNull(viewModel.uiState.value.mensajeExito)
    }

    @Test
    fun cargarRevisionEncuentraElRegistroEntreOtrosRegistrosDeLaBitacoraIndicada() {
        val registroBuscado = RegistroDto(id = 51L, estudiante = "estudiante51")
        val registroDiferente = RegistroDto(id = 999L, estudiante = "otro")
        val bitacora = BitacoraDto(id = 51L)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(51L, "docente1") } returns
            listOf(registroDiferente, registroBuscado)
        coEvery { repository.obtenerBitacoraPorId(51L) } returns bitacora

        viewModel.cargarRevision(51L, 51L)

        assertEquals(registroBuscado, viewModel.uiState.value.registro)
        assertEquals(bitacora, viewModel.uiState.value.bitacora)
    }

    @Test
    fun cargarRevisionEncuentraElRegistroEntreOtrosRegistrosAlBuscarEnTodasLasBitacoras() {
        val registroBuscado = RegistroDto(id = 52L, estudiante = "estudiante52")
        val registroDiferente = RegistroDto(id = 998L, estudiante = "otro")
        val bitacora = BitacoraDto(id = 52L)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns listOf(bitacora)
        coEvery { repository.obtenerRegistrosBitacora(52L, "docente1") } returns
            listOf(registroDiferente, registroBuscado)

        viewModel.cargarRevision(52L, null)

        assertEquals(registroBuscado, viewModel.uiState.value.registro)
        assertEquals(bitacora, viewModel.uiState.value.bitacora)
    }

    @Test
    fun cargarRevisionBuscaEnTodasLasBitacorasCuandoElIdRecibidoEsCero() {
        val registro = RegistroDto(id = 19L, estudiante = "estudiante19")
        val bitacora = BitacoraDto(id = 19L, titulo = "Bitácora encontrada")
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns listOf(bitacora)
        coEvery { repository.obtenerRegistrosBitacora(19L, "docente1") } returns listOf(
            RegistroDto(id = 999L),
            registro
        )

        viewModel.cargarRevision(19L, 0L)

        assertEquals(registro, viewModel.uiState.value.registro)
        assertEquals(bitacora, viewModel.uiState.value.bitacora)
        coVerify(exactly = 0) { repository.obtenerBitacoraPorId(0L) }
    }

    @Test
    fun guardarComentarioUsaElTextoIngresadoCuandoElRepositorioDevuelveComentarioNulo() {
        val registroInicial = RegistroDto(id = 46L, comentario = "Anterior")
        val registroActualizado = RegistroDto(id = 46L, comentario = null)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(46L, "docente1") } returns listOf(registroInicial)
        coEvery { repository.obtenerBitacoraPorId(46L) } returns BitacoraDto(id = 46L)
        coEvery {
            repository.agregarComentario(46L, "docente1", ComentarioRequest("Comentario nuevo"))
        } returns registroActualizado
        viewModel.cargarRevision(46L, 46L)
        viewModel.cambiarComentario("  Comentario nuevo  ")

        viewModel.guardarComentario()

        assertEquals(registroActualizado, viewModel.uiState.value.registro)
        assertEquals("Comentario nuevo", viewModel.uiState.value.comentarioOriginal)
        assertEquals("Comentario guardado", viewModel.uiState.value.mensajeExito)
    }

    @Test
    fun marcarRevisadoUsaElTextoIngresadoCuandoElRepositorioDevuelveComentarioNulo() {
        val registroInicial = RegistroDto(id = 47L, comentario = "Anterior")
        val registroActualizado = RegistroDto(id = 47L, comentario = null)
        val registroRevisado = registroActualizado.copy(estado = "revisado")
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(47L, "docente1") } returns listOf(registroInicial)
        coEvery { repository.obtenerBitacoraPorId(47L) } returns BitacoraDto(id = 47L)
        coEvery {
            repository.agregarComentario(47L, "docente1", ComentarioRequest("Comentario nuevo"))
        } returns registroActualizado
        coEvery { repository.marcarRevisado(47L, "docente1") } returns registroRevisado
        viewModel.cargarRevision(47L, 47L)
        viewModel.cambiarComentario("Comentario nuevo")

        viewModel.marcarRevisado()

        assertEquals(registroRevisado, viewModel.uiState.value.registro)
        assertEquals("Comentario nuevo", viewModel.uiState.value.comentarioOriginal)
        assertTrue(viewModel.uiState.value.guardado)
    }

    @Test
    fun marcarRevisadoAgregaComentarioSiElRegistroQuedoSinComentarioAunqueElTextoNoCambio() {
        val registroInicial = RegistroDto(id = 53L, comentario = "Anterior")
        val registroActualizadoSinComentario = RegistroDto(id = 53L, comentario = null)
        val registroRevisado = registroActualizadoSinComentario.copy(estado = "revisado")
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(53L, "docente1") } returns listOf(registroInicial)
        coEvery { repository.obtenerBitacoraPorId(53L) } returns BitacoraDto(id = 53L)
        coEvery {
            repository.agregarComentario(53L, "docente1", ComentarioRequest("Comentario nuevo"))
        } returns registroActualizadoSinComentario
        coEvery { repository.marcarRevisado(53L, "docente1") } returns registroRevisado

        viewModel.cargarRevision(53L, 53L)
        viewModel.cambiarComentario("Comentario nuevo")
        viewModel.guardarComentario()

        // El repositorio devuelve comentario null; el ViewModel conserva el texto ingresado
        // como comentarioOriginal. Al revisar, el texto no cambió, pero el registro sigue vacío.
        viewModel.marcarRevisado()

        // Se guarda una vez manualmente y otra vez al revisar porque el registro
        // sigue llegando sin comentario desde el repositorio.
        coVerify(exactly = 2) {
            repository.agregarComentario(53L, "docente1", ComentarioRequest("Comentario nuevo"))
        }
        coVerify(exactly = 1) { repository.marcarRevisado(53L, "docente1") }
        assertTrue(viewModel.uiState.value.guardado)
        assertEquals(registroRevisado, viewModel.uiState.value.registro)
    }

    @Test
    fun cargarRevisionContinuaBuscandoCuandoLaPrimeraBitacoraNoContieneElRegistro() {
        val registroBuscado = RegistroDto(id = 48L)
        val primeraBitacora = BitacoraDto(id = 48L)
        val segundaBitacora = BitacoraDto(id = 49L)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns
            listOf(primeraBitacora, segundaBitacora)
        coEvery { repository.obtenerRegistrosBitacora(48L, "docente1") } returns
            listOf(RegistroDto(id = 999L))
        coEvery { repository.obtenerRegistrosBitacora(49L, "docente1") } returns
            listOf(registroBuscado)

        viewModel.cargarRevision(48L, null)

        assertEquals(registroBuscado, viewModel.uiState.value.registro)
        assertEquals(segundaBitacora, viewModel.uiState.value.bitacora)
    }


    @Test
    fun guardarComentarioIgnoraUnaSegundaSolicitudMientrasLaPrimeraSigueGuardando() {
        val scheduler = TestCoroutineScheduler()
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        viewModel = DocenteRevisionViewModel(repository)
        val registroInicial = RegistroDto(id = 49L, comentario = "Anterior")
        val registroActualizado = RegistroDto(id = 49L, comentario = "Nuevo comentario")
        val respuestaPendiente = CompletableDeferred<RegistroDto>()
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(49L, "docente1") } returns listOf(registroInicial)
        coEvery { repository.obtenerBitacoraPorId(49L) } returns BitacoraDto(id = 49L)
        coEvery { repository.agregarComentario(49L, "docente1", any()) } coAnswers {
            respuestaPendiente.await()
        }
        viewModel.cargarRevision(49L, 49L)
        scheduler.runCurrent()
        viewModel.cambiarComentario("Nuevo comentario")

        viewModel.guardarComentario()
        scheduler.runCurrent()
        viewModel.guardarComentario()

        coVerify(exactly = 1) { repository.agregarComentario(49L, "docente1", any()) }
        respuestaPendiente.complete(registroActualizado)
        scheduler.runCurrent()
        assertFalse(viewModel.uiState.value.guardando)
    }

    @Test
    fun marcarRevisadoIgnoraUnaSegundaSolicitudMientrasLaPrimeraSigueGuardando() {
        val scheduler = TestCoroutineScheduler()
        Dispatchers.setMain(StandardTestDispatcher(scheduler))
        viewModel = DocenteRevisionViewModel(repository)
        val registroInicial = RegistroDto(id = 50L, comentario = "Comentario existente")
        val registroRevisado = registroInicial.copy(estado = "revisado")
        val respuestaPendiente = CompletableDeferred<RegistroDto>()
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(50L, "docente1") } returns listOf(registroInicial)
        coEvery { repository.obtenerBitacoraPorId(50L) } returns BitacoraDto(id = 50L)
        coEvery { repository.marcarRevisado(50L, "docente1") } coAnswers {
            respuestaPendiente.await()
        }
        viewModel.cargarRevision(50L, 50L)
        scheduler.runCurrent()

        viewModel.marcarRevisado()
        scheduler.runCurrent()
        viewModel.marcarRevisado()

        coVerify(exactly = 1) { repository.marcarRevisado(50L, "docente1") }
        respuestaPendiente.complete(registroRevisado)
        scheduler.runCurrent()
        assertTrue(viewModel.uiState.value.guardado)
        assertFalse(viewModel.uiState.value.guardando)
    }


    @Test
    fun cargarRevisionBuscaEnOtrasBitacorasCuandoLaBitacoraIndicadaNoTieneElRegistro() {
        val registroBuscado = RegistroDto(id = 60L, estudiante = "estudiante60")
        val bitacoraAlternativa = BitacoraDto(id = 61L)
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerRegistrosBitacora(60L, "docente1") } returns
            listOf(RegistroDto(id = 999L))
        coEvery { repository.obtenerBitacoraPorId(60L) } returns BitacoraDto(id = 60L)
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns listOf(bitacoraAlternativa)
        coEvery { repository.obtenerRegistrosBitacora(61L, "docente1") } returns listOf(registroBuscado)

        viewModel.cargarRevision(60L, 60L)

        assertEquals(registroBuscado, viewModel.uiState.value.registro)
        assertEquals(bitacoraAlternativa, viewModel.uiState.value.bitacora)
    }

    @Test
    fun cargarRevisionMuestraErrorCuandoNingunaBitacoraContieneElRegistroBuscado() {
        coEvery { repository.obtenerEstudiantes() } returns emptyList()
        coEvery { repository.obtenerBitacorasDocente("docente1") } returns listOf(BitacoraDto(id = 62L))
        coEvery { repository.obtenerRegistrosBitacora(62L, "docente1") } returns
            listOf(RegistroDto(id = 999L))

        viewModel.cargarRevision(63L, null)

        assertEquals("El aporte no está disponible.", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.registro)
    }

    @Test
    fun guardarComentarioNoContinuaCuandoElRegistroNoTieneId() {
        viewModel.cambiarComentario("Comentario válido")

        viewModel.guardarComentario()

        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.agregarComentario(any(), any(), any()) }
    }

    @Test
    fun marcarRevisadoNoContinuaCuandoElRegistroNoTieneId() {
        viewModel.cambiarComentario("Comentario válido")

        viewModel.marcarRevisado()

        assertFalse(viewModel.uiState.value.guardando)
        coVerify(exactly = 0) { repository.marcarRevisado(any(), any()) }
    }

}
