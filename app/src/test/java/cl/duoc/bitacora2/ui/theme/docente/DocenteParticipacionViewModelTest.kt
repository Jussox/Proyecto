package cl.duoc.bitacora2.ui.theme.Docente

import android.app.Application
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.BitacoraDao
import cl.duoc.bitacora2.data.BitacoraDatabase
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.util.Sesion
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DocenteParticipacionViewModelTest {

    private lateinit var application: Application
    private lateinit var database: BitacoraDatabase
    private lateinit var dao: BitacoraDao
    private lateinit var usuarioAnterior: String
    private lateinit var viewModel: DocenteParticipacionViewModel

    private val bitacoras = listOf(
        crearBitacora(id = 1, docente = "docente1", estado = "publicada"),
        crearBitacora(id = 2, docente = "docente1", estado = "cerrada"),
        crearBitacora(id = 3, docente = "docente1", estado = "borrador"),
        crearBitacora(id = 4, docente = "docente2", estado = "publicada")
    )

    private val registros = listOf(
        crearRegistro(id = 1, bitacoraId = 1, estudiante = "estudiante1", estado = "pendiente"),
        crearRegistro(id = 2, bitacoraId = 1, estudiante = "estudiante2", estado = "revisado"),
        crearRegistro(id = 3, bitacoraId = 1, estudiante = "estudiante1", estado = "revisado"),
        crearRegistro(id = 4, bitacoraId = 2, estudiante = "estudiante3", estado = "pendiente"),
        crearRegistro(id = 5, bitacoraId = 3, estudiante = "estudiante2", estado = "pendiente"),
        crearRegistro(id = 6, bitacoraId = 4, estudiante = "estudiante4", estado = "pendiente")
    )

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        usuarioAnterior = Sesion.usuario
        Sesion.usuario = "docente1"

        application = mockk(relaxed = true)
        database = mockk()
        dao = mockk()
        mockkObject(BitacoraDatabase.Companion)
        every { BitacoraDatabase.getInstance(any()) } returns database
        every { database.bitacoraDao() } returns dao
        every { dao.obtenerBitacoras() } returns flowOf(bitacoras)
        every { dao.obtenerRegistros() } returns flowOf(registros)

        viewModel = DocenteParticipacionViewModel(application)
    }

    @After
    fun limpiar() {
        unmockkObject(BitacoraDatabase.Companion)
        Sesion.usuario = usuarioAnterior
        Dispatchers.resetMain()
    }

    @Test
    fun estadoInicialMuestraBitacorasPublicadasYCerradasDelDocente() {
        val estado = viewModel.uiState.value

        assertEquals(listOf(1, 2), estado.bitacoras.map { it.id })
        assertEquals(0, estado.seleccionada)
        assertNull(estado.bitacora)
        assertTrue(estado.estudiantes.isEmpty())
        assertTrue(estado.participantes.isEmpty())
        assertTrue(estado.sinParticipacion.isEmpty())
        assertTrue(estado.enviados.isEmpty())
        assertEquals(0, estado.pendientes)
        assertEquals(0, estado.revisados)
    }

    @Test
    fun seleccionarBitacoraPublicadaCargaEstudiantesYParticipacion() {
        viewModel.seleccionarBitacora(1)

        val estado = viewModel.uiState.value
        assertEquals(1, estado.seleccionada)
        assertEquals(1, estado.bitacora?.id)
        assertEquals("Docente Prueba 1", estado.nombreDocente)
        assertEquals(3, estado.estudiantes.size)
        assertEquals(listOf("estudiante1", "estudiante2"), estado.participantes.map { it.usuario })
        assertEquals(listOf("estudiante3"), estado.sinParticipacion.map { it.usuario })
        assertEquals(3, estado.enviados.size)
        assertEquals(1, estado.pendientes)
        assertEquals(2, estado.revisados)
    }

    @Test
    fun seleccionarBitacoraCerradaTambienPermiteConsultarParticipacion() {
        viewModel.seleccionarBitacora(2)

        val estado = viewModel.uiState.value
        assertEquals(2, estado.bitacora?.id)
        assertEquals(listOf("estudiante3"), estado.participantes.map { it.usuario })
        assertEquals(1, estado.pendientes)
        assertEquals(0, estado.revisados)
        assertEquals(1, estado.enviados.size)
    }

    @Test
    fun seleccionarBitacoraEnBorradorLimpiaLaSeleccion() {
        viewModel.seleccionarBitacora(1)
        viewModel.seleccionarBitacora(3)

        val estado = viewModel.uiState.value
        assertEquals(3, estado.seleccionada)
        assertNull(estado.bitacora)
        assertEquals("", estado.nombreDocente)
        assertTrue(estado.estudiantes.isEmpty())
        assertTrue(estado.participantes.isEmpty())
        assertTrue(estado.sinParticipacion.isEmpty())
        assertTrue(estado.enviados.isEmpty())
        assertEquals(0, estado.pendientes)
        assertEquals(0, estado.revisados)
    }

    @Test
    fun seleccionarBitacoraDeOtroDocenteNoLaMuestra() {
        viewModel.seleccionarBitacora(4)

        val estado = viewModel.uiState.value
        assertNull(estado.bitacora)
        assertTrue(estado.bitacoras.none { it.id == 4 })
        assertTrue(estado.enviados.isEmpty())
    }

    @Test
    fun seleccionarIdInexistenteLimpiaLosDatosDeLaSeleccionAnterior() {
        viewModel.seleccionarBitacora(1)
        viewModel.seleccionarBitacora(999)

        val estado = viewModel.uiState.value
        assertEquals(999, estado.seleccionada)
        assertNull(estado.bitacora)
        assertEquals("", estado.nombreDocente)
        assertTrue(estado.estudiantes.isEmpty())
        assertTrue(estado.participantes.isEmpty())
        assertTrue(estado.sinParticipacion.isEmpty())
        assertTrue(estado.enviados.isEmpty())
    }

    @Test
    fun recalcularSoloCuentaRegistrosDeLaBitacoraSeleccionada() {
        viewModel.seleccionarBitacora(1)

        assertEquals(3, viewModel.uiState.value.enviados.size)
        assertTrue(viewModel.uiState.value.enviados.all { it.bitacoraId == 1 })
    }

    @Test
    fun recalcularNoIncluyeRegistrosDeBitacorasEnBorradorNiDeOtrosDocentes() {
        viewModel.seleccionarBitacora(1)

        val ids = viewModel.uiState.value.enviados.map { it.id }
        assertTrue(5 !in ids)
        assertTrue(6 !in ids)
    }

    @Test
    fun seleccionarOtraBitacoraActualizaLosContadoresDePendientesYRevisados() {
        viewModel.seleccionarBitacora(1)
        viewModel.seleccionarBitacora(2)

        assertEquals(1, viewModel.uiState.value.pendientes)
        assertEquals(0, viewModel.uiState.value.revisados)
    }

    @Test
    fun seleccionSinRegistrosMantieneParticipantesSinEnvios() {
        every { dao.obtenerRegistros() } returns flowOf(emptyList())
        viewModel = DocenteParticipacionViewModel(application)

        viewModel.seleccionarBitacora(1)

        val estado = viewModel.uiState.value
        assertTrue(estado.enviados.isEmpty())
        assertTrue(estado.participantes.isEmpty())
        assertEquals(3, estado.sinParticipacion.size)
        assertEquals(0, estado.pendientes)
        assertEquals(0, estado.revisados)
    }

    private companion object {
        fun crearBitacora(
            id: Int,
            docente: String,
            estado: String
        ) = Bitacora(
            id = id,
            docente = docente,
            curso = "7.º Básico A",
            asignatura = "Lenguaje",
            mes = "Noviembre",
            anio = "2026",
            titulo = "Bitácora $id",
            proposito = "Propósito",
            instrucciones = "Instrucciones",
            preguntas = "Preguntas",
            estado = estado
        )

        fun crearRegistro(
            id: Int,
            bitacoraId: Int,
            estudiante: String,
            estado: String
        ) = Registro(
            id = id,
            bitacoraId = bitacoraId,
            estudiante = estudiante,
            fechaClase = "20/11/2026",
            contenido = "Contenido",
            actividad = "Actividad",
            aprendizaje = "Aprendizaje",
            dudas = "Dudas",
            fotografias = "",
            archivos = "",
            enlaces = "",
            estado = estado,
            fechaCreacion = "20/11/2026"
        )
    }
}
