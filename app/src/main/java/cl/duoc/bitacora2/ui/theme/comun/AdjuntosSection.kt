package cl.duoc.bitacora2.ui.theme.comun

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage

@Composable
fun AdjuntosSection(
    uriFoto: String,
    enlace: String,
    onFotoCambio: (String) -> Unit,
    onEnlaceCambio: (String) -> Unit
) {
    val context = LocalContext.current

    val selectorFotos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
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

    AnimatedVisibility(
        visible = uriFoto.isNotBlank(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        if (uriFoto.isNotBlank()) {
            AsyncImage(
                model = Uri.parse(uriFoto),
                contentDescription = "Fotografía adjunta",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            BotonSecundario("Quitar fotografía") { onFotoCambio("") }
        }
    }
}