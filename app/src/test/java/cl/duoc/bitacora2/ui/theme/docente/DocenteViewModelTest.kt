package cl.duoc.bitacora2.ui.theme.Docente

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DocenteViewModelTest {

    private lateinit var viewModel: DocenteViewModel

    @Before
    fun configurar() {
        viewModel = DocenteViewModel()
    }

    // ============================================================================
    // 1. Estado inicial
    // ============================================================================

    @Test
    fun estadoInicialMuestraAdministracionExpandidaYLasOtrasSeccionesContraidas() {
        // ARRANGE — viewModel recién creado en configurar()

        // ACT
        val estado = viewModel.uiState.value

        // ASSERT
        assertTrue(estado.administracionExpandida)
        assertFalse(estado.revisionExpandida)
        assertFalse(estado.seguimientoExpandido)
    }

    // ============================================================================
    // 2. toggleAdministracion
    // ============================================================================

    @Test
    fun alternarAdministracionContraeLaSeccionInicialmenteExpandida() {
        // ARRANGE — administración comienza expandida

        // ACT
        viewModel.toggleAdministracion()

        // ASSERT
        assertFalse(viewModel.uiState.value.administracionExpandida)
    }

    @Test
    fun alternarAdministracionDosVecesVuelveAExpandirLaSeccion() {
        // ARRANGE — administración comienza expandida

        // ACT
        viewModel.toggleAdministracion()
        viewModel.toggleAdministracion()

        // ASSERT
        assertTrue(viewModel.uiState.value.administracionExpandida)
    }

    // ============================================================================
    // 3. toggleRevision
    // ============================================================================

    @Test
    fun alternarRevisionExpandeLaSeccionInicialmenteContraida() {
        // ARRANGE — revisión comienza contraída

        // ACT
        viewModel.toggleRevision()

        // ASSERT
        assertTrue(viewModel.uiState.value.revisionExpandida)
    }

    @Test
    fun alternarRevisionDosVecesVuelveAContraerLaSeccion() {
        // ARRANGE — revisión comienza contraída

        // ACT
        viewModel.toggleRevision()
        viewModel.toggleRevision()

        // ASSERT
        assertFalse(viewModel.uiState.value.revisionExpandida)
    }

    // ============================================================================
    // 4. toggleSeguimiento
    // ============================================================================

    @Test
    fun alternarSeguimientoExpandeLaSeccionInicialmenteContraida() {
        // ARRANGE — seguimiento comienza contraído

        // ACT
        viewModel.toggleSeguimiento()

        // ASSERT
        assertTrue(viewModel.uiState.value.seguimientoExpandido)
    }

    @Test
    fun alternarSeguimientoDosVecesVuelveAContraerLaSeccion() {
        // ARRANGE — seguimiento comienza contraído

        // ACT
        viewModel.toggleSeguimiento()
        viewModel.toggleSeguimiento()

        // ASSERT
        assertFalse(viewModel.uiState.value.seguimientoExpandido)
    }

    // ============================================================================
    // 5. Independencia de las secciones
    // ============================================================================

    @Test
    fun alternarUnaSeccionNoModificaElEstadoDeLasOtras() {
        // ARRANGE — administración expandida; revisión y seguimiento contraídos

        // ACT
        viewModel.toggleRevision()

        // ASSERT
        val estado = viewModel.uiState.value
        assertTrue(estado.administracionExpandida)
        assertTrue(estado.revisionExpandida)
        assertFalse(estado.seguimientoExpandido)
    }
}
