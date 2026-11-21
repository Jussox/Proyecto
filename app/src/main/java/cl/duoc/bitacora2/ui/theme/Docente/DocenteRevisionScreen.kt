package cl.duoc.bitacora2.ui.theme.Docente

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Campo
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.Estado
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import coil3.compose.AsyncImage

@Composable
fun DocenteRevisionScreen(
    registroId: Long,
    bitacoraId: Long? = null,
    onVolver: () -> Unit,
    viewModel: DocenteRevisionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(registroId, bitacoraId) {
        viewModel.cargarRevision(registroId, bitacoraId)
    }

    PantallaBase(titulo = "Revisión de aporte", onVolver = onVolver) {
        if (uiState.cargando) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        } else if (uiState.error != null && uiState.registro == null) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = uiState.error ?: "El aporte no está disponible.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.cargarRevision(registroId, bitacoraId) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        } else {
            val registro = uiState.registro
            val bitacora = uiState.bitacora

            if (registro == null) {
                item { Text("El aporte no está disponible.") }
            } else {
                item { Dato("Estudiante", uiState.nombreEstudiante) }
                item { Dato("Bitácora", bitacora?.titulo ?: "Bitácora") }
                item {
                    val cursoAsignatura = listOfNotNull(
                        bitacora?.curso?.takeIf { it.isNotBlank() },
                        bitacora?.asignatura?.takeIf { it.isNotBlank() }
                    ).joinToString(" · ")
                    Dato("Curso y asignatura", cursoAsignatura.ifBlank { "—" })
                }
                item { Dato("Fecha de la clase", registro.fechaClase ?: "—") }
                item { Dato("Contenido trabajado", registro.contenido ?: "—") }
                item { Dato("Actividad realizada", registro.actividad ?: "—") }
                item { Dato("Qué aprendió", registro.aprendizaje ?: "—") }
                item { Dato("Dudas o dificultades", registro.dudas?.takeIf { it.isNotBlank() } ?: "—") }

                item { Text("Fotografías:") }
                val fotos = registro.fotografias ?: ""
                if (fotos.isBlank()) {
                    item { Text("—") }
                } else {
                    item {
                        var errorCargaFoto by remember(fotos) { mutableStateOf(false) }

                        if (errorCargaFoto) {
                            Text(
                                text = "Foto adjunta (no disponible)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            val uriParsed = remember(fotos) {
                                try {
                                    Uri.parse(fotos)
                                } catch (_: Throwable) {
                                    null
                                }
                            }

                            if (uriParsed == null) {
                                Text(
                                    text = "Foto adjunta (no disponible)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                AsyncImage(
                                    model = uriParsed,
                                    contentDescription = "Fotografía del estudiante",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp)),
                                    contentScale = ContentScale.FillWidth,
                                    onError = {
                                        errorCargaFoto = true
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    val archivos = registro.archivos ?: ""
                    val textoArchivos = if (archivos.isBlank()) "—" else archivos
                    Dato("Archivos adjuntos", textoArchivos)
                }

                item {
                    val enlaces = registro.enlaces ?: ""
                    val textoEnlaces = if (enlaces.isBlank()) "—" else enlaces
                    Dato("Enlaces relacionados", textoEnlaces)
                }

                item { Dato("Estado", Estado.textoDe(registro.estado ?: "")) }

                item { Text("Retroalimentación", style = MaterialTheme.typography.titleMedium) }

                item {
                    Campo(
                        valor = uiState.comentario,
                        etiqueta = "Comentario de retroalimentación",
                        onCambio = { viewModel.cambiarComentario(it) }
                    )
                }

                if (uiState.errorComentario != null) {
                    item {
                        Text(
                            text = uiState.errorComentario ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (uiState.guardando) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                } else {
                    item {
                        BotonSecundario("Guardar comentario") {
                            viewModel.guardarComentario()
                        }
                    }

                    if (registro.estado == "revisado") {
                        item {
                            Text(
                                text = "Este aporte ya fue marcado como revisado.",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        item {
                            BotonAncho("Marcar como revisado") {
                                viewModel.marcarRevisado(onExito = { onVolver() })
                            }
                        }
                    }
                }

                if (uiState.errorOperacion != null) {
                    item {
                        Text(
                            text = uiState.errorOperacion ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (uiState.mensajeExito != null) {
                    item {
                        Text(
                            text = uiState.mensajeExito ?: "",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
