package cl.duoc.bitacora2.data.externa

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/** API REST externa de Wikipedia en español; no utiliza el backend de bitácoras. */
interface RecursosEducativosApi {
    @Headers("Api-User-Agent: BitacoraDuoc/1.0 (Android; proyecto educativo)")
    @GET("search/page")
    suspend fun buscarPaginas(
        @Query("q") tema: String,
        @Query("limit") limite: Int = 10
    ): BusquedaRecursosResponse
}
