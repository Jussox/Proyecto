package cl.duoc.bitacora2.data.externa

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Cliente Retrofit independiente del cliente que se comunica con ProyectoApp. */
object RecursosEducativosRetrofitClient {
    private const val BASE_URL = "https://es.wikipedia.org/w/rest.php/v1/"

    val api: RecursosEducativosApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecursosEducativosApi::class.java)
    }
}
