package cl.duoc.bitacora2.data.externa

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class RecursoEducativoDto(
    val id: Int,
    val key: String,
    val title: String,
    val excerpt: String,
    val description: String?,
    val thumbnail: MiniaturaDto?
) {
    val urlArticulo: String
        get() = "https://es.wikipedia.org/wiki/${Uri.encode(key)}"

    val urlImagen: String?
        get() = thumbnail?.url?.let { if (it.startsWith("//")) "https:$it" else it }
}

data class MiniaturaDto(
    @SerializedName("url") val url: String?
)

data class BusquedaRecursosResponse(
    val pages: List<RecursoEducativoDto> = emptyList()
)
