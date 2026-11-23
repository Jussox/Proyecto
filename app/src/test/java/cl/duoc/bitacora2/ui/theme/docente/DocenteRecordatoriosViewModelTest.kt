package cl.duoc.bitacora2.ui.theme.Docente

import androidx.lifecycle.ViewModel
import cl.duoc.bitacora2.data.BitacoraRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.Runs
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class DocenteRecordatoriosViewModelTest {

    private lateinit var repository: BitacoraRepository
    private lateinit var viewModel: DocenteRecordatoriosViewModel

    @Before
    fun configurar() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        repository = mockk()
        viewModel = DocenteRecordatoriosViewModel(repository)
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
    }

    private fun fechaDeHoy(): String =
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    private fun fechaDeAyer(): String {
        val calendario = Calendar.getInstance()
        calendario.add(Calendar.DAY_OF_MONTH, -1)
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendario.time)
    }

    @Test
    fun estadoInicialUsaTipoCierreMensualYFechaYMensajeVacios() {
        val estado = viewModel.uiState.value
        assertEquals("Cierre mensual", estado.tipo)
        assertEquals("", estado.fecha)
        assertEquals("", estado.mensaje)
    }

    @Test
    fun cambiarTipoActualizaElTipoYLimpiaElMensaje() {
        viewModel.crearRecordatorio()
        viewModel.cambiarTipo("Entrega final")
        assertEquals("Entrega final", viewModel.uiState.value.tipo)
        assertEquals("", viewModel.uiState.value.mensaje)
    }

    @Test
    fun cambiarFechaActualizaLaFechaYLimpiaElMensaje() {
        viewModel.crearRecordatorio()
        viewModel.cambiarFecha("25/12/2026")
        assertEquals("25/12/2026", viewModel.uiState.value.fecha)
        assertEquals("", viewModel.uiState.value.mensaje)
    }

    @Test
    fun crearRecordatorioMuestraErrorCuandoLaFechaEstaVacia() {
        viewModel.crearRecordatorio()
        assertEquals(
            "Ingresa una fecha programada en formato dd/mm/aaaa antes de continuar.",
            viewModel.uiState.value.mensaje
        )
        assertEquals("", viewModel.uiState.value.fecha)
    }

    @Test
    fun crearRecordatorioMuestraErrorCuandoLaFechaTieneFormatoInvalido() {
        viewModel.cambiarFecha("2026-12-25")
        viewModel.crearRecordatorio()
        assertEquals(
            "La fecha ingresada no es válida o ya pasó. Debe tener formato dd/mm/aaaa y ser igual o posterior a hoy.",
            viewModel.uiState.value.mensaje
        )
        assertEquals("2026-12-25", viewModel.uiState.value.fecha)
    }

    @Test
    fun crearRecordatorioMuestraErrorCuandoLaFechaEsAnteriorAHoy() {
        val ayer = fechaDeAyer()
        viewModel.cambiarFecha(ayer)
        viewModel.crearRecordatorio()
        assertTrue(viewModel.uiState.value.mensaje.contains("ya pasó"))
        assertEquals(ayer, viewModel.uiState.value.fecha)
    }

    @Test
    fun crearRecordatorioAceptaLaFechaDeHoyYGuardaUnRecordatorioPendiente() {
        coEvery { repository.guardarRecordatorio(any()) } just Runs
        val fecha = fechaDeHoy()
        viewModel.cambiarFecha(fecha)
        viewModel.crearRecordatorio(docente = "docente1")

        assertEquals("", viewModel.uiState.value.fecha)
        assertTrue(viewModel.uiState.value.mensaje.contains("programado"))
        coVerify(exactly = 1) {
            repository.guardarRecordatorio(match {
                it.docente == "docente1" &&
                    it.tipo == "Cierre mensual" &&
                    it.fechaProgramada == fecha &&
                    it.estado == "pendiente"
            })
        }
    }

    @Test
    fun crearRecordatorioLimpiaEspaciosDeLaFechaAntesDeGuardar() {
        coEvery { repository.guardarRecordatorio(any()) } just Runs
        val fecha = fechaDeHoy()
        viewModel.cambiarFecha("  $fecha  ")
        viewModel.crearRecordatorio(docente = "docente2")

        coVerify(exactly = 1) {
            repository.guardarRecordatorio(match { it.fechaProgramada == fecha })
        }
        assertTrue(viewModel.uiState.value.mensaje.contains(fecha))
    }

    @Test
    fun crearRecordatorioInvocaCallbackConTipoYFechaValidos() {
        coEvery { repository.guardarRecordatorio(any()) } just Runs
        val fecha = fechaDeHoy()
        viewModel.cambiarTipo("Revisión semanal")
        viewModel.cambiarFecha(fecha)
        var tipoRecibido: String? = null
        var fechaRecibida: String? = null

        viewModel.crearRecordatorio(
            onCrear = { tipo, fechaCallback ->
                tipoRecibido = tipo
                fechaRecibida = fechaCallback
            },
            docente = "docente1"
        )

        assertEquals("Revisión semanal", tipoRecibido)
        assertEquals(fecha, fechaRecibida)
    }

    @Test
    fun crearRecordatorioMuestraMensajeConElTipoYLaFecha() {
        coEvery { repository.guardarRecordatorio(any()) } just Runs
        val fecha = fechaDeHoy()
        viewModel.cambiarFecha(fecha)
        viewModel.crearRecordatorio(docente = "docente1")
        assertEquals(
            "Recordatorio de \"Cierre mensual\" programado para el $fecha. Ya está visible en tu lista.",
            viewModel.uiState.value.mensaje
        )
    }

    @Test
    fun crearRecordatorioSinRepositorioIgualActualizaElEstadoYEjecutaCallback() {
        val viewModelSinRepositorio = DocenteRecordatoriosViewModel()
        val fecha = fechaDeHoy()
        viewModelSinRepositorio.cambiarFecha(fecha)
        var callbackEjecutado = false

        viewModelSinRepositorio.crearRecordatorio(
            onCrear = { _, _ -> callbackEjecutado = true },
            docente = "docente1"
        )

        assertTrue(callbackEjecutado)
        assertEquals("", viewModelSinRepositorio.uiState.value.fecha)
        assertTrue(viewModelSinRepositorio.uiState.value.mensaje.contains("programado"))
    }

    @Test
    fun completarRecordatorioGuardaElIdYEjecutaCallback() {
        coEvery { repository.completarRecordatorio(15) } just Runs
        var idRecibido: Int? = null
        viewModel.completarRecordatorio(id = 15, onCompletar = { idRecibido = it })
        coVerify(exactly = 1) { repository.completarRecordatorio(15) }
        assertEquals(15, idRecibido)
    }

    @Test
    fun completarRecordatorioMuestraMensajeConElTipo() {
        coEvery { repository.completarRecordatorio(20) } just Runs
        viewModel.completarRecordatorio(id = 20, tipoRecordatorio = "Cierre mensual")
        assertEquals(
            "El recordatorio \"Cierre mensual\" fue marcado como completado.",
            viewModel.uiState.value.mensaje
        )
    }

    @Test
    fun completarRecordatorioMuestraMensajeGenericoCuandoNoSeIndicaTipo() {
        coEvery { repository.completarRecordatorio(21) } just Runs
        viewModel.completarRecordatorio(id = 21)
        assertEquals("El recordatorio fue marcado como completado.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun completarRecordatorioMuestraMensajeGenericoCuandoElTipoEstaEnBlanco() {
        coEvery { repository.completarRecordatorio(22) } just Runs
        viewModel.completarRecordatorio(id = 22, tipoRecordatorio = "   ")
        assertEquals("El recordatorio fue marcado como completado.", viewModel.uiState.value.mensaje)
    }

    @Test
    fun completarRecordatorioSinRepositorioIgualEjecutaCallback() {
        val viewModelSinRepositorio = DocenteRecordatoriosViewModel()
        var idRecibido: Int? = null
        viewModelSinRepositorio.completarRecordatorio(
            id = 30,
            onCompletar = { idRecibido = it },
            tipoRecordatorio = "Entrega"
        )
        assertEquals(30, idRecibido)
        assertTrue(viewModelSinRepositorio.uiState.value.mensaje.contains("completado"))
    }

    @Test
    fun fabricaCreaElViewModelSolicitado() {
        val fabrica = DocenteRecordatoriosViewModelFactory(repository)
        val creado = fabrica.create(DocenteRecordatoriosViewModel::class.java)
        assertTrue(creado is DocenteRecordatoriosViewModel)
    }

    @Test
    fun fabricaRechazaUnaClaseDeViewModelNoSoportada() {
        val fabrica = DocenteRecordatoriosViewModelFactory(repository)
        assertThrows(IllegalArgumentException::class.java) {
            fabrica.create(OtroViewModel::class.java)
        }
    }

    private class OtroViewModel : ViewModel()
}
