
package cl.duoc.bitacora2.data.externa

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * API REST externa de Wikipedia en español.
 * No utiliza el backend de bitácoras.
 */
interface RecursosEducativosApi {

    @Headers(
        "User-Agent: BitacoraDuoc/1.0 (contacto: TU_CORREO)",
        "Api-User-Agent: BitacoraDuoc/1.0 (contacto: TU_CORREO)"
    )
    @GET("search/page")
    suspend fun buscarPaginas(
        @Query("q") tema: String,
        @Query("limit") limite: Int = 10
    ): Response<BusquedaRecursosResponse>
}
