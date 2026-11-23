package cl.duoc.bitacora2.data.remoto

import cl.duoc.bitacora2.data.remoto.dto.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response
import org.junit.Assert.fail
import cl.duoc.bitacora2.data.remoto.dto.LoginRequest
import cl.duoc.bitacora2.data.remoto.dto.LoginResponse
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertThrows
import retrofit2.HttpException


class RemotoRepositoryTest {
    private fun <T : Any> respuestaExitosa(cuerpo: T): Response<T> = mockk {
        every { isSuccessful } returns true
        every { body() } returns cuerpo
    }

    private fun respuestaVaciaExitosa(): Response<Void> = mockk {
        every { isSuccessful } returns true
    }

    @Test
    fun operacionesDelRepositorio_deleganEnApiYDevuelvenElCuerpo() = runTest {
        val api = mockk<BitacoraApi>()
        val repo = RemotoRepository(api)

        val loginRequest = mockk<LoginRequest>()
        val loginResponse = mockk<LoginResponse>()
        coEvery { api.login(loginRequest) } returns respuestaExitosa(loginResponse)
        assertEquals(loginResponse, repo.login(loginRequest))

        val bitacoras = emptyList<BitacoraDto>()
        coEvery { api.obtenerBitacorasDocente("docente1") } returns respuestaExitosa(bitacoras)
        coEvery { api.obtenerBitacorasEstudiante("estudiante1") } returns respuestaExitosa(bitacoras)
        coEvery { api.obtenerBitacoraPorId(1L) } returns respuestaExitosa(mockk())
        val bitacoraRequest = mockk<BitacoraRequest>()
        coEvery { api.crearBitacora(bitacoraRequest) } returns respuestaExitosa(mockk())
        coEvery { api.publicarBitacora(1L, "docente1") } returns respuestaExitosa(mockk())
        coEvery { api.cerrarBitacora(1L, "docente1") } returns respuestaExitosa(mockk())
        val edicion = mockk<BitacoraEdicionRequest>()
        coEvery { api.editarBitacora(1L, "docente1", edicion) } returns respuestaExitosa(mockk())
        coEvery { api.obtenerParticipacion(1L, "docente1") } returns respuestaExitosa(mockk())
        coEvery { api.obtenerInforme(1L, "docente1") } returns respuestaExitosa(mockk())

        assertEquals(bitacoras, repo.obtenerBitacorasDocente("docente1"))
        assertEquals(bitacoras, repo.obtenerBitacorasEstudiante("estudiante1"))
        repo.obtenerBitacoraPorId(1L)
        repo.crearBitacora(bitacoraRequest)
        repo.publicarBitacora(1L, "docente1")
        repo.cerrarBitacora(1L, "docente1")
        repo.editarBitacora(1L, "docente1", edicion)
        repo.obtenerParticipacion(1L, "docente1")
        repo.obtenerInforme(1L, "docente1")

        val registroRequest = mockk<RegistroRequest>()
        val registros = emptyList<RegistroDto>()
        coEvery { api.crearRegistro(registroRequest) } returns respuestaExitosa(mockk())
        coEvery { api.actualizarRegistro(2L, "estudiante1", registroRequest) } returns respuestaExitosa(mockk())
        coEvery { api.enviarRegistro(2L, "estudiante1") } returns respuestaExitosa(mockk())
        coEvery { api.eliminarRegistro(2L, "estudiante1") } returns respuestaVaciaExitosa()
        coEvery { api.obtenerRegistrosEstudiante("estudiante1", null) } returns respuestaExitosa(registros)
        coEvery { api.obtenerRegistrosEstudiante("estudiante1", 1L) } returns respuestaExitosa(registros)
        coEvery { api.obtenerRegistrosBitacora(1L, "docente1", null, null, null) } returns respuestaExitosa(registros)
        coEvery { api.obtenerRegistrosBitacora(1L, "docente1", "estudiante1", "2026-11-22", "PENDIENTE") } returns respuestaExitosa(registros)
        coEvery { api.obtenerRegistrosPendientes(1L, "docente1") } returns respuestaExitosa(registros)
        val comentario = mockk<ComentarioRequest>()
        coEvery { api.agregarComentario(2L, "docente1", comentario) } returns respuestaExitosa(mockk())
        coEvery { api.marcarRevisado(2L, "docente1") } returns respuestaExitosa(mockk())

        repo.crearRegistro(registroRequest)
        repo.actualizarRegistro(2L, "estudiante1", registroRequest)
        repo.enviarRegistro(2L, "estudiante1")
        repo.eliminarRegistro(2L, "estudiante1")
        assertEquals(registros, repo.obtenerRegistrosEstudiante("estudiante1"))
        assertEquals(registros, repo.obtenerRegistrosEstudiante("estudiante1", 1L))
        assertEquals(registros, repo.obtenerRegistrosBitacora(1L, "docente1"))
        assertEquals(registros, repo.obtenerRegistrosBitacora(1L, "docente1", "estudiante1", "2026-11-22", "PENDIENTE"))
        assertEquals(registros, repo.obtenerRegistrosPendientes(1L, "docente1"))
        repo.agregarComentario(2L, "docente1", comentario)
        repo.marcarRevisado(2L, "docente1")

        val recordatorios = emptyList<RecordatorioDto>()
        coEvery { api.obtenerRecordatorios("docente1") } returns respuestaExitosa(recordatorios)
        val recordatorioRequest = mockk<RecordatorioRequest>()
        coEvery { api.crearRecordatorio(recordatorioRequest) } returns respuestaExitosa(mockk())
        coEvery { api.completarRecordatorio(3L, "docente1") } returns respuestaExitosa(mockk())
        assertEquals(recordatorios, repo.obtenerRecordatorios("docente1"))
        repo.crearRecordatorio(recordatorioRequest)
        repo.completarRecordatorio(3L, "docente1")

        val asignaciones = emptyList<AsignacionDto>()
        coEvery { api.obtenerAsignacionesDocente("docente1") } returns respuestaExitosa(asignaciones)
        coEvery { api.obtenerAsignacionesEstudiante("estudiante1") } returns respuestaExitosa(asignaciones)
        assertEquals(asignaciones, repo.obtenerAsignacionesDocente("docente1"))
        assertEquals(asignaciones, repo.obtenerAsignacionesEstudiante("estudiante1"))

        val docentes = emptyList<DocenteDto>()
        val estudiantes = emptyList<EstudianteDto>()
        coEvery { api.obtenerDocentes() } returns respuestaExitosa(docentes)
        coEvery { api.obtenerEstudiantes(null) } returns respuestaExitosa(estudiantes)
        coEvery { api.obtenerEstudiantes("5A") } returns respuestaExitosa(estudiantes)
        assertEquals(docentes, repo.obtenerDocentes())
        assertEquals(estudiantes, repo.obtenerEstudiantes())
        assertEquals(estudiantes, repo.obtenerEstudiantes("5A"))

        coVerify(exactly = 1) { api.login(loginRequest) }
        coVerify(exactly = 1) { api.eliminarRegistro(2L, "estudiante1") }
    }

    @Test
    fun `login lanza IllegalStateException cuando respuesta exitosa no tiene body`() = runTest {
        val api = mockk<BitacoraApi>()
        val repository = RemotoRepository(api)
        val request = LoginRequest(
            usuario = "estudiante1",
            password = "123456",
            perfil = "ESTUDIANTE"
        )

        coEvery { api.login(request) } returns Response.success(null)

        try {
            repository.login(request)
            fail("Se esperaba IllegalStateException por cuerpo vacío")
        } catch (error: IllegalStateException) {
            assertEquals("Cuerpo de respuesta vacío", error.message)
        }
    }

    @Test
    fun login_conRespuestaHttpFallida_lanzaHttpException() = runTest {
        val request = mockk<LoginRequest>()

        coEvery {
            api.login(request)
        } returns Response.error<LoginResponse>(
            400,
            "Error de solicitud".toResponseBody()
        )

        assertThrows(HttpException::class.java) {
            kotlinx.coroutines.runBlocking {
                repository.login(request)
            }
        }
    }

    private val api = mockk<BitacoraApi>()
    private val repository = RemotoRepository(api)

    @Test
    fun eliminarRegistro_conRespuestaHttpFallida_lanzaHttpException() = runTest {
        coEvery {
            api.eliminarRegistro(1L, "estudiante1")
        } returns Response.error<Void>(
            404,
            "Registro no encontrado".toResponseBody()
        )

        assertThrows(HttpException::class.java) {
            kotlinx.coroutines.runBlocking {
                repository.eliminarRegistro(1L, "estudiante1")
            }
        }
    }
}
