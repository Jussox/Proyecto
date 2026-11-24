package cl.duoc.bitacora2.data.externa

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response

class RecursosEducativosRepositoryTest {
    @Test
    fun buscarPorTema_consultaElTemaYDevuelveLosResultados() = runTest {
        val api = mockk<RecursosEducativosApi>()
        val respuesta = BusquedaRecursosResponse(
            pages = listOf(
                RecursoEducativoDto(
                    id = 1,
                    key = "Sistema_solar",
                    title = "Sistema solar",
                    excerpt = "<b>El sistema</b> solar &amp; sus planetas",
                    description = "Sistema planetario",
                    thumbnail = MiniaturaDto("//upload.wikimedia.org/solar.jpg")
                )
            )
        )
        coEvery { api.buscarPaginas("sistema solar", 10) } returns Response.success(respuesta)

        val resultado = RecursosEducativosRepository(api).buscarPorTema(" sistema solar ")

        assertEquals(1, resultado.size)
        assertEquals("Sistema solar", resultado.first().title)
        assertEquals("El sistema solar & sus planetas", resultado.first().excerpt)
        assertEquals("https://upload.wikimedia.org/solar.jpg", resultado.first().urlImagen)
        coVerify(exactly = 1) { api.buscarPaginas("sistema solar", 10) }
    }

    @Test
    fun buscarPorTema_conTextoVacio_lanzaIllegalArgumentException() = runTest {
        val api = mockk<RecursosEducativosApi>(relaxed = true)

        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking { RecursosEducativosRepository(api).buscarPorTema("   ") }
        }
        coVerify(exactly = 0) { api.buscarPaginas(any(), any()) }
    }
}
