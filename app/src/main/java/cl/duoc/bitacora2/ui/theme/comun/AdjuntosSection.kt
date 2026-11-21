package cl.duoc.bitacora2.ui.theme.comun

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.util.MAX_TAMANO_ARCHIVO_BYTES
import cl.duoc.bitacora2.util.validarArchivoAdjunto
import coil3.compose.AsyncImage

private fun obtenerMetadatosArchivo(context: Context, uri: Uri): Pair<String?, Long> {
    val mime = context.contentResolver.getType(uri)
    var tamano = 0L

    try {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (sizeIndex != -1 && cursor.moveToFirst()) {
                tamano = cursor.getLong(sizeIndex)
            }
        }
    } catch (_: Exception) {
    }

    if (tamano <= 0L) {
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                val buffer = ByteArray(8192)
                var leidosTotal = 0L
                val limite = MAX_TAMANO_ARCHIVO_BYTES + 1L
                while (leidosTotal < limite) {
                    val porLeer = minOf(buffer.size.toLong(), limite - leidosTotal).toInt()
                    val leidos = stream.read(buffer, 0, porLeer)
                    if (leidos == -1) break
                    leidosTotal += leidos
                }
                tamano = leidosTotal
            }
        } catch (_: Exception) {
        }
    }

    return Pair(mime, tamano)
}

@Composable
fun AdjuntosSection(
    uriFoto: String,
    enlace: String,
    onFotoCambio: (String) -> Unit,
    onEnlaceCambio: (String) -> Unit,
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current

    val selectorFotos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val (tipoMime, tamanoBytes) = obtenerMetadatosArchivo(context, uri)
            val errorArchivo = validarArchivoAdjunto(tipoMime, tamanoBytes)
            if (errorArchivo != null) {
                onError(errorArchivo)
                return@rememberLauncherForActivityResult
            }

            // Clave: la URI debe sobrevivir al cerrar la app (los registros persisten en Room)
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            onFotoCambio(uri.toString())
        }
    }

    Campo(enlace, "Enlace (opcional)") { onEnlaceCambio(it) }

    BotonAncho(if (uriFoto.isBlank()) "Adjuntar fotografía" else "Cambiar fotografía") {
        selectorFotos.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    AnimacionFotoAdjunta(visible = uriFoto.isNotBlank()) {
        if (uriFoto.isNotBlank()) {
            AsyncImage(
                model = Uri.parse(uriFoto),
                contentDescription = "Fotografía adjunta",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.FillWidth
            )
            BotonSecundario("Quitar fotografía") { onFotoCambio("") }
        }
    }
}
