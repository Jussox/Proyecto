package cl.duoc.bitacora2.ui.theme.Docente

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.ui.theme.colores.MenuMoradoFondo
import cl.duoc.bitacora2.ui.theme.colores.MenuMoradoIcono
import cl.duoc.bitacora2.ui.theme.colores.MenuNaranjaFondo
import cl.duoc.bitacora2.ui.theme.colores.MenuNaranjaIcono
import cl.duoc.bitacora2.ui.theme.colores.MenuVerdeFondo
import cl.duoc.bitacora2.ui.theme.colores.MenuVerdeIcono
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionFondo
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionIcono
import cl.duoc.bitacora2.ui.theme.colores.RevisionEvidenciaFondo
import cl.duoc.bitacora2.ui.theme.colores.RevisionFeedbackFondo
import cl.duoc.bitacora2.ui.theme.colores.RevisionHeaderFondo
import cl.duoc.bitacora2.ui.theme.colores.RevisionReflexionFondo
import cl.duoc.bitacora2.ui.theme.colores.colorBadgeRevision
import cl.duoc.bitacora2.ui.theme.colores.colorFondoBadgeRevision
import cl.duoc.bitacora2.ui.theme.comun.AnimacionMensajeRevision
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.Campo
import cl.duoc.bitacora2.ui.theme.comun.Estado
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.util.esAporteRevisado
import cl.duoc.bitacora2.util.textoContadorComentario
import cl.duoc.bitacora2.util.tieneFotografiaValida
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
        // 1. Estado de carga
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
        }
        // 2. Error bloqueante con reintento
        else if (uiState.error != null && uiState.registro == null) {
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
                        Surface(
                            modifier = Modifier.size(54.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.errorContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Text(
                            text = uiState.error ?: "El aporte no está disponible.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Button(
                            onClick = { viewModel.cargarRevision(registroId, bitacoraId) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
        // 3. Contenido del aporte
        else {
            val registro = uiState.registro
            val bitacora = uiState.bitacora

            if (registro == null) {
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = RevisionHeaderFondo
                        )
                    ) {
                        Text(
                            text = "El aporte no está disponible.",
                            modifier = Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val cursoAsignatura = listOfNotNull(
                    bitacora?.curso?.takeIf { it.isNotBlank() },
                    bitacora?.asignatura?.takeIf { it.isNotBlank() }
                ).joinToString(" · ")

                val estadoActual = registro.estado ?: ""
                val yaRevisado = esAporteRevisado(estadoActual)

                // A. Tarjeta cabecera del estudiante (Estilo EstudianteScreen)
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = RevisionHeaderFondo
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    modifier = Modifier.size(52.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    color = ParticipacionFondo
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = ParticipacionIcono,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = colorFondoBadgeRevision(estadoActual)
                                    ) {
                                        Text(
                                            text = Estado.textoDe(estadoActual).uppercase(),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = colorBadgeRevision(estadoActual)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = uiState.nombreEstudiante.ifBlank { "Estudiante" },
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = bitacora?.titulo ?: "Bitácora",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            HorizontalDivider(
                                color = ParticipacionIcono.copy(alpha = 0.16f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = ParticipacionIcono,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = cursoAsignatura.ifBlank { "Sin curso asignado" },
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = null,
                                        tint = ParticipacionIcono,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = registro.fechaClase ?: "—",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        color = ParticipacionIcono
                                    )
                                }
                            }
                        }
                    }
                }

                // B. Bloque de desarrollo y aprendizaje (Tono Morado suave)
                item {
                    TarjetaSeccionRevision(
                        titulo = "Desarrollo y reflexión de la clase",
                        icono = Icons.Default.MenuBook,
                        fondoTarjeta = RevisionReflexionFondo,
                        fondoHeader = MenuMoradoFondo,
                        acento = MenuMoradoIcono
                    ) {
                        ItemDatoRevision(
                            etiqueta = "CONTENIDO TRABAJADO",
                            valor = registro.contenido ?: "—",
                            colorEtiqueta = MenuMoradoIcono
                        )
                        HorizontalDivider(color = MenuMoradoIcono.copy(alpha = 0.14f))
                        ItemDatoRevision(
                            etiqueta = "ACTIVIDAD REALIZADA",
                            valor = registro.actividad ?: "—",
                            colorEtiqueta = MenuMoradoIcono
                        )
                        HorizontalDivider(color = MenuMoradoIcono.copy(alpha = 0.14f))
                        ItemDatoRevision(
                            etiqueta = "QUÉ APRENDIÓ",
                            valor = registro.aprendizaje ?: "—",
                            colorEtiqueta = MenuMoradoIcono
                        )
                        HorizontalDivider(color = MenuMoradoIcono.copy(alpha = 0.14f))
                        ItemDatoRevision(
                            etiqueta = "DUDAS O DIFICULTADES",
                            valor = registro.dudas?.takeIf { it.isNotBlank() } ?: "Sin dudas reportadas",
                            colorEtiqueta = MenuMoradoIcono
                        )
                    }
                }

                // C. Bloque de evidencias, fotografía y enlaces (Tono Crema/Naranja suave)
                item {
                    TarjetaSeccionRevision(
                        titulo = "Evidencia y material adjunto",
                        icono = Icons.Default.Image,
                        fondoTarjeta = RevisionEvidenciaFondo,
                        fondoHeader = MenuNaranjaFondo,
                        acento = MenuNaranjaIcono
                    ) {
                        val fotos = registro.fotografias ?: ""
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "FOTOGRAFÍA ADJUNTA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MenuNaranjaIcono
                            )

                            if (!tieneFotografiaValida(fotos)) {
                                Text(
                                    text = "Sin fotografía adjunta",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                var errorCargaFoto by remember(fotos) { mutableStateOf(false) }
                                val uriParsed = remember(fotos) {
                                    try {
                                        Uri.parse(fotos)
                                    } catch (_: Throwable) {
                                        null
                                    }
                                }

                                if (errorCargaFoto || uriParsed == null) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MenuNaranjaFondo.copy(alpha = 0.7f),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Foto adjunta (no disponible en este dispositivo)",
                                            modifier = Modifier.padding(12.dp),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MenuNaranjaIcono
                                        )
                                    }
                                } else {
                                    AsyncImage(
                                        model = uriParsed,
                                        contentDescription = "Fotografía del estudiante",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(14.dp)),
                                        contentScale = ContentScale.FillWidth,
                                        onError = { errorCargaFoto = true }
                                    )
                                }
                            }
                        }

                        HorizontalDivider(color = MenuNaranjaIcono.copy(alpha = 0.14f))

                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = MenuNaranjaIcono,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            ItemDatoRevision(
                                etiqueta = "ARCHIVOS ADJUNTOS",
                                valor = registro.archivos?.takeIf { it.isNotBlank() } ?: "Ninguno",
                                colorEtiqueta = MenuNaranjaIcono
                            )
                        }

                        HorizontalDivider(color = MenuNaranjaIcono.copy(alpha = 0.14f))

                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Link,
                                contentDescription = null,
                                tint = MenuNaranjaIcono,
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(top = 2.dp)
                            )
                            ItemDatoRevision(
                                etiqueta = "ENLACES RELACIONADOS",
                                valor = registro.enlaces?.takeIf { it.isNotBlank() } ?: "Ninguno",
                                colorEtiqueta = MenuNaranjaIcono
                            )
                        }
                    }
                }

                // D. Bloque de retroalimentación docente (Tono Verde suave)
                item {
                    TarjetaSeccionRevision(
                        titulo = "Retroalimentación del docente",
                        icono = Icons.Default.RateReview,
                        fondoTarjeta = RevisionFeedbackFondo,
                        fondoHeader = MenuVerdeFondo,
                        acento = MenuVerdeIcono
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "COMENTARIO PARA EL ESTUDIANTE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MenuVerdeIcono
                                )
                                Text(
                                    text = textoContadorComentario(uiState.comentario),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Campo(
                                valor = uiState.comentario,
                                etiqueta = "Escribe observaciones o sugerencias...",
                                onCambio = { viewModel.cambiarComentario(it) }
                            )
                        }

                        AnimacionMensajeRevision(
                            mensaje = uiState.errorComentario,
                            esError = true
                        )

                        AnimacionMensajeRevision(
                            mensaje = uiState.errorOperacion,
                            esError = true
                        )

                        AnimacionMensajeRevision(
                            mensaje = uiState.mensajeExito,
                            esError = false
                        )

                        if (uiState.guardando) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MenuVerdeIcono)
                            }
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.guardarComentario() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(46.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, MenuVerdeIcono.copy(alpha = 0.5f)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MenuVerdeIcono,
                                        containerColor = Color.White.copy(alpha = 0.7f)
                                    )
                                ) {
                                    Text(
                                        text = "Guardar borrador de comentario",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }

                                if (yaRevisado) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = MenuVerdeFondo,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MenuVerdeIcono,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = "Este aporte ya fue marcado como revisado.",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontWeight = FontWeight.SemiBold
                                                ),
                                                color = MenuVerdeIcono
                                            )
                                        }
                                    }
                                } else {
                                    BotonAncho("Marcar como revisado") {
                                        viewModel.marcarRevisado(onExito = { onVolver() })
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaSeccionRevision(
    titulo: String,
    icono: ImageVector,
    fondoTarjeta: Color,
    fondoHeader: Color,
    acento: Color,
    contenido: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = fondoTarjeta
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, acento.copy(alpha = 0.22f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = fondoHeader,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(9.dp),
                            color = acento.copy(alpha = 0.15f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icono,
                                    contentDescription = null,
                                    tint = acento,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Text(
                            text = titulo,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = acento
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    contenido()
                }
            }
        }
    }
}

@Composable
private fun ItemDatoRevision(
    etiqueta: String,
    valor: String,
    colorEtiqueta: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            color = colorEtiqueta.copy(alpha = 0.85f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}