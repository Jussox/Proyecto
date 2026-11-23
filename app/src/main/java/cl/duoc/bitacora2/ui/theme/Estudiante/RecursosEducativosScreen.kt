package cl.duoc.bitacora2.ui.theme.Estudiante

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun RecursosEducativosScreen(
    onVolver: () -> Unit,
    viewModel: RecursosEducativosViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    PantallaBase(titulo = "Recursos para aprender", onVolver = onVolver) {
        item {
            Text(
                "Busca el tema de tu bitácora para encontrar artículos e imágenes que complementen lo trabajado en clases.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            OutlinedTextField(
                value = state.consulta,
                onValueChange = viewModel::cambiarConsulta,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Tema de la actividad") },
                placeholder = { Text("Ej.: sistema solar, fracciones, pueblos originarios") },
                singleLine = true
            )
        }
        item {
            Button(
                onClick = viewModel::buscar,
                enabled = state.consulta.isNotBlank() && !state.cargando,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null)
                Text("Buscar recursos", modifier = Modifier.padding(start = 8.dp))
            }
        }
        item {
            Text(
                "Los resultados provienen de Wikipedia en español. Úsalos como material de consulta complementario y revisa la información con las instrucciones de tu docente.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (state.cargando) {
            item { CircularProgressIndicator(modifier = Modifier.padding(20.dp)) }
        }
        state.error?.let { mensaje ->
            item { Text(mensaje, color = MaterialTheme.colorScheme.error) }
        }
        if (state.busquedaRealizada && !state.cargando && state.error == null && state.recursos.isEmpty()) {
            item { Text("No se encontraron resultados para ese tema.") }
        }
        items(state.recursos) { recurso ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    recurso.urlImagen?.let { imagen ->
                        AsyncImage(
                            model = imagen,
                            contentDescription = "Imagen relacionada con ${recurso.title}",
                            modifier = Modifier.fillMaxWidth().height(170.dp)
                        )
                    }
                    Text(recurso.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    recurso.description?.takeIf { it.isNotBlank() }?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                    Text(recurso.excerpt, style = MaterialTheme.typography.bodyMedium)
                    Row {
                        Button(onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(recurso.urlArticulo)))
                        }) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text(" Leer artículo", modifier = Modifier.padding(start = 6.dp))
                        }
                    }
                }
            }
        }
    }
}
