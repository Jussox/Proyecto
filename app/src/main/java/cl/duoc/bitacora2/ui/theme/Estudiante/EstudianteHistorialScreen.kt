package cl.duoc.bitacora2.ui.theme.Estudiante

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.ui.theme.Bitacora2Theme
import cl.duoc.bitacora2.ui.theme.comun.Estado
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun EstudianteHistorialScreen(
    titulo: String = "Mis registros e historial",
    registros: List<Registro>,
    bitacoras: List<Bitacora>,
    onVolver: () -> Unit,
    onEditar: (Registro) -> Unit,
    onEliminar: (Int) -> Unit,
    viewModel: EstudianteHistorialViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val ordenados = registros.sortedByDescending { it.id }

    PantallaBase(titulo = titulo, onVolver = onVolver) {
        if (ordenados.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "Todavía no tienes registros",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Los registros de clases que redactes o envíes aparecerán aquí.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(ordenados) { registro ->
            val bitacora = bitacoras.firstOrNull { it.id == registro.bitacoraId }

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Fila superior: Asignatura + Insignia de estado oficial
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = bitacora?.asignatura ?: "Asignatura",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Insignia estilizada conectada a Estado
                        InsigniaEstado(estado = registro.estado)
                    }

                    // Título de la bitácora
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = bitacora?.titulo ?: "Bitácora",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = bitacora?.curso ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

                    // Información de la clase
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(28.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Clase del ${registro.fechaClase}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = registro.contenido,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Creado el ${registro.fechaCreacion}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    // Botones de acción en una sola fila
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onEditar(registro) },
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (registro.estado == "borrador") "Seguir editando" else "Ver / Editar",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.mostrarDialogoEliminar(registro.id) },
                            modifier = Modifier.height(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (uiState.idPendienteEliminar != null) {
        AlertDialog(
            onDismissRequest = { viewModel.ocultarDialogoEliminar() },
            title = { Text("Eliminar registro") },
            text = { Text("¿Estás seguro de que deseas eliminar este registro de clase? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmarEliminar(onEliminar) },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.ocultarDialogoEliminar() }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

/**
 * Insignia visual conectada al enum Estado del proyecto
 */
@Composable
private fun InsigniaEstado(estado: String) {
    val color = Estado.colorDe(estado)
    val texto = Estado.textoDe(estado)
    val icono = when (estado) {
        "revisado" -> Icons.Default.CheckCircle
        "pendiente" -> Icons.Default.HourglassTop
        else -> Icons.Default.Edit
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = color
            )
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
    }
}

// ==========================================
// VISTA PREVIA
// ==========================================

@Preview(
    name = "Historial Mejorado",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun EstudianteHistorialPreview() {
    Bitacora2Theme {
        EstudianteHistorialScreen(
            registros = listOf(
                Registro(
                    id = 1,
                    bitacoraId = 1,
                    estudiante = "david.alumno",
                    fechaClase = "18/11/2024",
                    contenido = "Patrones de diseño de software y estados en UI",
                    actividad = "Implementación de ViewModel y Compose",
                    aprendizaje = "Manejo de flujo unidireccional y estados en UI",
                    dudas = "Ninguna",
                    fotografias = "",
                    archivos = "",
                    enlaces = "",
                    estado = "borrador",
                    comentario = "",
                    fechaCreacion = "18/11/2024",
                    fechaEnvio = "",
                    fechaRetroalimentacion = "",
                    docenteRevisor = ""
                ),
                Registro(
                    id = 2,
                    bitacoraId = 1,
                    estudiante = "david.alumno",
                    fechaClase = "14/11/2024",
                    contenido = "Diseño de formularios y componentes Material 3",
                    actividad = "Diseño de menú y formularios",
                    aprendizaje = "Uso de Scaffold y navegación",
                    dudas = "Ninguna",
                    fotografias = "",
                    archivos = "",
                    enlaces = "",
                    estado = "revisado",
                    comentario = "Excelente entrega.",
                    fechaCreacion = "14/11/2024",
                    fechaEnvio = "15/11/2024",
                    fechaRetroalimentacion = "16/11/2024",
                    docenteRevisor = "Prof. Juan Pérez"
                )
            ),
            bitacoras = listOf(
                Bitacora(
                    id = 1,
                    docente = "Prof. Juan Pérez",
                    curso = "Ingeniería en Informática",
                    asignatura = "Taller de Apps Móviles",
                    mes = "Noviembre",
                    anio = "2024",
                    titulo = "Bitácora Práctica Profesional",
                    proposito = "Seguimiento",
                    instrucciones = "Llenar semanalmente",
                    preguntas = "¿Dudas?",
                    estado = "publicada"
                )
            ),
            onVolver = {},
            onEditar = {},
            onEliminar = {}
        )
    }
}