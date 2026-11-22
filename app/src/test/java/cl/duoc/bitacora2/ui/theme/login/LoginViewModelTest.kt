package cl.duoc.bitacora2.ui.theme.login

import cl.duoc.bitacora2.data.remoto.RemotoRepository
import cl.duoc.bitacora2.data.remoto.dto.LoginRequest
import cl.duoc.bitacora2.data.remoto.dto.LoginResponse
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
class LoginViewModelTest {

    private lateinit var repository: RemotoRepository
    private lateinit var viewModel: LoginViewModel

    // Resultados de los callbacks de navegación
    private var usuarioEstudianteNavegado: String? = null
    private var usuarioDocenteNavegado: String? = null

    @Before
    fun configurar() {
        // viewModelScope usa Dispatchers.Main, que no existe en tests JVM
        Dispatchers.setMain(UnconfinedTestDispatcher())
        Sesion.cerrar()
        repository = mockk()
        viewModel = LoginViewModel(repository)
        usuarioEstudianteNavegado = null
        usuarioDocenteNavegado = null
    }

    @After
    fun limpiar() {
        Dispatchers.resetMain()
        Sesion.cerrar()
    }

    // Helper privado para ejecutar iniciarSesion capturando la navegación
    private fun iniciarSesion() {
        viewModel.iniciarSesion(
            onEstudianteValido = { usuarioEstudianteNavegado = it },
            onDocenteValido = { usuarioDocenteNavegado = it }
        )
    }

    // Helper privado para cargar el formulario con datos válidos
    private fun completarFormulario(
        usuario: String = "estudiante1",
        password: String = "clave1234",
        perfil: String = "estudiante"
    ) {
        viewModel.cambiarPerfil(perfil)
        viewModel.cambiarUsuario(usuario)
        viewModel.cambiarPassword(password)
    }

    // Helper privado para construir una HttpException con el código indicado
    private fun crearHttpException(codigo: Int, cuerpo: String = ""): HttpException {
        val body = cuerpo.toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<LoginResponse>(codigo, body))
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
        assertEquals("estudiante", estado.perfil)
        assertEquals("", estado.usuario)
        assertEquals("", estado.password)
        assertEquals("", estado.mensajeError)
        assertFalse(estado.cargando)
    }

    // ============================================================================
    // 2. cambiarPerfil
    // ============================================================================

    @Test
    fun cambiarPerfilActualizaElPerfil() {
        // ARRANGE
        val nuevoPerfil = "docente"

        // ACT
        viewModel.cambiarPerfil(nuevoPerfil)

        // ASSERT
        assertEquals("docente", viewModel.uiState.value.perfil)
    }

    @Test
    fun cambiarPerfilLimpiaElMensajeDeError() {
        // ARRANGE — se genera un error de validación previo
        iniciarSesion()
        assertEquals("Debe ingresar usuario y contraseña", viewModel.uiState.value.mensajeError)

        // ACT
        viewModel.cambiarPerfil("docente")

        // ASSERT
        assertEquals("", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun cambiarPerfilNoModificaUsuarioNiPassword() {
        // ARRANGE
        viewModel.cambiarUsuario("docente1")
        viewModel.cambiarPassword("clave1234")

        // ACT
        viewModel.cambiarPerfil("docente")

        // ASSERT
        assertEquals("docente1", viewModel.uiState.value.usuario)
        assertEquals("clave1234", viewModel.uiState.value.password)
    }

    // ============================================================================
    // 3. cambiarUsuario
    // ============================================================================

    @Test
    fun cambiarUsuarioActualizaElUsuario() {
        // ARRANGE
        val nuevoUsuario = "estudiante1"

        // ACT
        viewModel.cambiarUsuario(nuevoUsuario)

        // ASSERT
        assertEquals("estudiante1", viewModel.uiState.value.usuario)
    }

    @Test
    fun cambiarUsuarioNoLimpiaElMensajeDeError() {
        // ARRANGE — se genera un error de validación previo
        iniciarSesion()

        // ACT
        viewModel.cambiarUsuario("estudiante1")

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", viewModel.uiState.value.mensajeError)
    }

    // ============================================================================
    // 4. cambiarPassword
    // ============================================================================

    @Test
    fun cambiarPasswordActualizaLaPassword() {
        // ARRANGE
        val nuevaPassword = "clave1234"

        // ACT
        viewModel.cambiarPassword(nuevaPassword)

        // ASSERT
        assertEquals("clave1234", viewModel.uiState.value.password)
    }

    @Test
    fun cambiarPasswordNoModificaElUsuario() {
        // ARRANGE
        viewModel.cambiarUsuario("estudiante1")

        // ACT
        viewModel.cambiarPassword("clave1234")

        // ASSERT
        assertEquals("estudiante1", viewModel.uiState.value.usuario)
    }

    // ============================================================================
    // 5. iniciarSesion — validaciones locales
    // ============================================================================

    @Test
    fun iniciarSesionUsuarioVacioMuestraErrorYNoLlamaAlRepositorio() {
        // ARRANGE
        completarFormulario(usuario = "", password = "clave1234")

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", viewModel.uiState.value.mensajeError)
        assertFalse(viewModel.uiState.value.cargando)
        coVerify(exactly = 0) { repository.login(any()) }
    }

    @Test
    fun iniciarSesionPasswordVaciaMuestraErrorYNoLlamaAlRepositorio() {
        // ARRANGE
        completarFormulario(usuario = "estudiante1", password = "")

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", viewModel.uiState.value.mensajeError)
        assertFalse(viewModel.uiState.value.cargando)
        coVerify(exactly = 0) { repository.login(any()) }
    }

    @Test
    fun iniciarSesionAmbosCamposVaciosMuestraError() {
        // ARRANGE
        completarFormulario(usuario = "", password = "")

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", viewModel.uiState.value.mensajeError)
        coVerify(exactly = 0) { repository.login(any()) }
    }

    @Test
    fun iniciarSesionUsuarioSoloEspaciosMuestraError() {
        // ARRANGE
        completarFormulario(usuario = "     ", password = "clave1234")

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", viewModel.uiState.value.mensajeError)
        coVerify(exactly = 0) { repository.login(any()) }
    }

    @Test
    fun iniciarSesionPasswordSoloEspaciosMuestraError() {
        // ARRANGE
        completarFormulario(usuario = "estudiante1", password = "     ")

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Debe ingresar usuario y contraseña", viewModel.uiState.value.mensajeError)
        coVerify(exactly = 0) { repository.login(any()) }
    }

    @Test
    fun iniciarSesionConErrorDeValidacionNoInvocaNavegacion() {
        // ARRANGE
        completarFormulario(usuario = "", password = "")

        // ACT
        iniciarSesion()

        // ASSERT
        assertNull(usuarioEstudianteNavegado)
        assertNull(usuarioDocenteNavegado)
    }

    @Test
    fun iniciarSesionConErrorDeValidacionNoIniciaSesion() {
        // ARRANGE
        completarFormulario(usuario = "", password = "")

        // ACT
        iniciarSesion()

        // ASSERT
        assertFalse(Sesion.estaActiva)
    }

    // ============================================================================
    // 6. iniciarSesion — login exitoso
    // ============================================================================

    @Test
    fun iniciarSesionEstudianteValidoNavegaComoEstudiante() {
        // ARRANGE
        completarFormulario(usuario = "estudiante1", password = "clave1234", perfil = "estudiante")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "estudiante1",
            nombre = "Ana Pérez",
            perfil = "estudiante",
            curso = "3A"
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("estudiante1", usuarioEstudianteNavegado)
        assertNull(usuarioDocenteNavegado)
    }

    @Test
    fun iniciarSesionDocenteValidoNavegaComoDocente() {
        // ARRANGE
        completarFormulario(usuario = "docente1", password = "clave1234", perfil = "docente")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "docente1",
            nombre = "Juan Soto",
            perfil = "docente",
            curso = null
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("docente1", usuarioDocenteNavegado)
        assertNull(usuarioEstudianteNavegado)
    }

    @Test
    fun iniciarSesionEnviaAlRepositorioLosDatosDelFormulario() {
        // ARRANGE
        completarFormulario(usuario = "docente1", password = "clave1234", perfil = "docente")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "docente1",
            nombre = "Juan Soto",
            perfil = "docente"
        )

        // ACT
        iniciarSesion()

        // ASSERT
        coVerify(exactly = 1) {
            repository.login(
                LoginRequest(usuario = "docente1", password = "clave1234", perfil = "docente")
            )
        }
    }

    @Test
    fun iniciarSesionExitosoGuardaLosDatosEnLaSesion() {
        // ARRANGE
        completarFormulario(usuario = "estudiante1", password = "clave1234", perfil = "estudiante")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "estudiante1",
            nombre = "Ana Pérez",
            perfil = "estudiante",
            curso = "3A"
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertTrue(Sesion.estaActiva)
        assertEquals("estudiante1", Sesion.usuario)
        assertEquals("Ana Pérez", Sesion.nombre)
        assertEquals("estudiante", Sesion.perfil)
        assertEquals("3A", Sesion.curso)
    }

    @Test
    fun iniciarSesionExitosoLimpiaElFormulario() {
        // ARRANGE
        completarFormulario(usuario = "estudiante1", password = "clave1234", perfil = "docente")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "estudiante1",
            nombre = "Ana Pérez",
            perfil = "estudiante",
            curso = "3A"
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals(LoginUiState(), viewModel.uiState.value)
    }

    @Test
    fun iniciarSesionExitosoDejaCargandoEnFalse() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "estudiante1",
            perfil = "estudiante"
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun iniciarSesionRespuestaSinUsuarioUsaElUsuarioIngresado() {
        // ARRANGE
        completarFormulario(usuario = "estudiante1", password = "clave1234")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = null,
            nombre = "Ana Pérez",
            perfil = "estudiante",
            curso = "3A"
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("estudiante1", Sesion.usuario)
        assertEquals("estudiante1", usuarioEstudianteNavegado)
    }

    @Test
    fun iniciarSesionDocenteSinUsuarioEnRespuestaNavegaConElUsuarioIngresado() {
        // ARRANGE
        completarFormulario(usuario = "docente1", password = "clave1234", perfil = "docente")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = null,
            nombre = "Juan Soto",
            perfil = "docente",
            curso = null
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("docente1", usuarioDocenteNavegado)
        assertNull(usuarioEstudianteNavegado)
    }

    @Test
    fun iniciarSesionRespuestaSinNombreNiCursoGuardaCadenasVacias() {
        // ARRANGE
        completarFormulario(usuario = "docente1", password = "clave1234", perfil = "docente")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "docente1",
            nombre = null,
            perfil = "docente",
            curso = null
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("", Sesion.nombre)
        assertEquals("", Sesion.curso)
    }

    @Test
    fun iniciarSesionRespuestaSinPerfilUsaElPerfilSeleccionadoEnLaSesion() {
        // ARRANGE
        completarFormulario(usuario = "docente1", password = "clave1234", perfil = "docente")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "docente1",
            nombre = "Juan Soto",
            perfil = null,
            curso = null
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("docente", Sesion.perfil)
    }

    @Test
    fun iniciarSesionRespuestaSinPerfilNavegaComoDocente() {
        // ARRANGE
        completarFormulario(usuario = "estudiante1", password = "clave1234", perfil = "estudiante")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "estudiante1",
            nombre = "Ana Pérez",
            perfil = null,
            curso = "3A"
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("estudiante1", usuarioDocenteNavegado)
        assertNull(usuarioEstudianteNavegado)
    }

    @Test
    fun iniciarSesionNavegaSegunElPerfilDelBackendYNoElSeleccionado() {
        // ARRANGE — el usuario eligió "docente" pero el backend responde "estudiante"
        completarFormulario(usuario = "estudiante1", password = "clave1234", perfil = "docente")
        coEvery { repository.login(any()) } returns LoginResponse(
            usuario = "estudiante1",
            nombre = "Ana Pérez",
            perfil = "estudiante",
            curso = "3A"
        )

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("estudiante1", usuarioEstudianteNavegado)
        assertNull(usuarioDocenteNavegado)
        assertEquals("estudiante", Sesion.perfil)
    }

    // ============================================================================
    // 7. iniciarSesion — errores HTTP
    // ============================================================================

    @Test
    fun iniciarSesionError401MuestraUsuarioOPasswordIncorrectos() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws crearHttpException(401)

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Usuario o contraseña incorrectos", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun iniciarSesionError403MuestraMensajeDeSinPermisos() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws crearHttpException(403)

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals(
            "No tienes permisos para realizar esta acción",
            viewModel.uiState.value.mensajeError
        )
    }

    @Test
    fun iniciarSesionError404MuestraRecursoNoEncontrado() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws crearHttpException(404)

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals(
            "El recurso solicitado no fue encontrado",
            viewModel.uiState.value.mensajeError
        )
    }

    @Test
    fun iniciarSesionError500MuestraErrorInternoDelServidor() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws crearHttpException(500)

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Error interno del servidor", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun iniciarSesionError400SinCuerpoMuestraDatosInvalidos() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws crearHttpException(400)

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Datos inválidos o incompletos", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun iniciarSesionErrorHttpDesconocidoSinCuerpoMuestraCodigo() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws crearHttpException(418)

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Error en el servidor (418)", viewModel.uiState.value.mensajeError)
    }

    // ============================================================================
    // 8. iniciarSesion — otros errores
    // ============================================================================

    @Test
    fun iniciarSesionSinConexionMuestraErrorDeConexion() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws IOException("timeout")

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("No hay conexión con el servidor", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun iniciarSesionExcepcionGenericaConMensajeMuestraEseMensaje() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws IllegalStateException("Cuerpo de respuesta vacío")

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Cuerpo de respuesta vacío", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun iniciarSesionExcepcionGenericaSinMensajeMuestraErrorInesperado() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws RuntimeException()

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("Ocurrió un error inesperado", viewModel.uiState.value.mensajeError)
    }

    @Test
    fun iniciarSesionConErrorDejaCargandoEnFalse() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws IOException("sin red")

        // ACT
        iniciarSesion()

        // ASSERT
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun iniciarSesionConErrorConservaUsuarioYPassword() {
        // ARRANGE
        completarFormulario(usuario = "estudiante1", password = "claveErronea")
        coEvery { repository.login(any()) } throws crearHttpException(401)

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("estudiante1", viewModel.uiState.value.usuario)
        assertEquals("claveErronea", viewModel.uiState.value.password)
    }

    @Test
    fun iniciarSesionConErrorNoIniciaSesionNiNavega() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws crearHttpException(401)

        // ACT
        iniciarSesion()

        // ASSERT
        assertFalse(Sesion.estaActiva)
        assertNull(usuarioEstudianteNavegado)
        assertNull(usuarioDocenteNavegado)
    }

    @Test
    fun iniciarSesionCancelacionNoMuestraErrorYDejaCargandoEnFalse() {
        // ARRANGE
        completarFormulario()
        coEvery { repository.login(any()) } throws CancellationException("cancelado")

        // ACT
        iniciarSesion()

        // ASSERT
        assertEquals("", viewModel.uiState.value.mensajeError)
        assertFalse(viewModel.uiState.value.cargando)
        assertNull(usuarioEstudianteNavegado)
        assertNull(usuarioDocenteNavegado)
    }

    // ============================================================================
    // 9. iniciarSesion — estado de carga
    // ============================================================================

    @Test
    fun iniciarSesionMarcaCargandoMientrasEsperaLaRespuesta() {
        // ARRANGE — el repositorio queda suspendido hasta liberar la compuerta
        completarFormulario()
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.login(any()) } coAnswers {
            compuerta.await()
            LoginResponse(usuario = "estudiante1", perfil = "estudiante")
        }

        // ACT
        iniciarSesion()

        // ASSERT — mientras espera
        assertTrue(viewModel.uiState.value.cargando)

        // ACT — llega la respuesta
        compuerta.complete(Unit)

        // ASSERT — al terminar
        assertFalse(viewModel.uiState.value.cargando)
    }

    @Test
    fun iniciarSesionLimpiaElMensajeDeErrorAnteriorAlComenzar() {
        // ARRANGE — primero se genera un error de validación
        iniciarSesion()
        assertEquals("Debe ingresar usuario y contraseña", viewModel.uiState.value.mensajeError)

        completarFormulario()
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.login(any()) } coAnswers {
            compuerta.await()
            LoginResponse(usuario = "estudiante1", perfil = "estudiante")
        }

        // ACT
        iniciarSesion()

        // ASSERT — el error previo ya no se muestra mientras carga
        assertEquals("", viewModel.uiState.value.mensajeError)
        assertTrue(viewModel.uiState.value.cargando)

        compuerta.complete(Unit)
    }

    @Test
    fun iniciarSesionIgnoraUnSegundoIntentoMientrasEstaCargando() {
        // ARRANGE
        completarFormulario()
        val compuerta = CompletableDeferred<Unit>()
        coEvery { repository.login(any()) } coAnswers {
            compuerta.await()
            LoginResponse(usuario = "estudiante1", perfil = "estudiante")
        }

        // ACT
        iniciarSesion()
        iniciarSesion()
        compuerta.complete(Unit)

        // ASSERT
        coVerify(exactly = 1) { repository.login(any()) }
    }

    @Test
    fun iniciarSesionPermiteReintentarDespuesDeUnError() {
        // ARRANGE — el primer intento falla y el segundo funciona
        completarFormulario(usuario = "estudiante1", password = "clave1234")
        coEvery { repository.login(any()) } throws crearHttpException(401) andThen
                LoginResponse(
                    usuario = "estudiante1",
                    nombre = "Ana Pérez",
                    perfil = "estudiante",
                    curso = "3A"
                )

        // ACT
        iniciarSesion()
        val errorPrimerIntento = viewModel.uiState.value.mensajeError
        iniciarSesion()

        // ASSERT
        assertEquals("Usuario o contraseña incorrectos", errorPrimerIntento)
        assertEquals("estudiante1", usuarioEstudianteNavegado)
        assertEquals("", viewModel.uiState.value.mensajeError)
        coVerify(exactly = 2) { repository.login(any()) }
    }
}