package cl.duoc.bitacora2.data.externa

class RecursosEducativosRepository(
    private val api: RecursosEducativosApi = RecursosEducativosRetrofitClient.api
) {
    suspend fun buscarPorTema(tema: String): List<RecursoEducativoDto> {
        require(tema.isNotBlank()) { "Escribe un tema para buscar." }
        return api.buscarPaginas(tema.trim(), 10).pages.map { recurso ->
            recurso.copy(excerpt = limpiarHtml(recurso.excerpt))
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
