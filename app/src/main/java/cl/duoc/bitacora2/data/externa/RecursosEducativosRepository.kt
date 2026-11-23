
package cl.duoc.bitacora2.data.externa

import android.util.Log

class RecursosEducativosRepository(
    private val api: RecursosEducativosApi = RecursosEducativosRetrofitClient.api
) {

    suspend fun buscarPorTema(tema: String): List<RecursoEducativoDto> {

        require(tema.isNotBlank()) {
            "Escribe un tema para buscar."
        }

        try {
            val respuesta = api.buscarPaginas(tema.trim(), 10)

            Log.d("WIKI_API", "Código HTTP: ${respuesta.code()}")

            if (!respuesta.isSuccessful) {
                val error = respuesta.errorBody()?.string()

                Log.e("WIKI_API", "Error del servidor: $error")

                throw Exception(
                    "Wikipedia respondió HTTP ${respuesta.code()}: $error"
                )
            }

            val paginas = respuesta.body()?.pages.orEmpty()

            Log.d("WIKI_API", "Páginas recibidas: ${paginas.size}")
            Log.d("WIKI_API", "Respuesta: $paginas")

            return paginas.map { recurso ->
                recurso.copy(
                    excerpt = limpiarHtml(recurso.excerpt)
                )
            }

        } catch (e: Exception) {
            Log.e("WIKI_API", "Error en la búsqueda", e)
            throw e
        }
    }
}

private fun limpiarHtml(texto: String): String = texto
    .replace(Regex("<[^>]*>"), "")
    .replace("&amp;", "&")
    .replace("&quot;", "\"")
    .replace("&#39;", "'")
    .replace("&lt;", "<")
    .replace("&gt;", ">")
    .trim()
