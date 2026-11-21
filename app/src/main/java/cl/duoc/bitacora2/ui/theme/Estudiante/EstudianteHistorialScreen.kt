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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.ui.theme.colores.EstadoBorrador
import cl.duoc.bitacora2.ui.theme.colores.EstadoCerrada
import cl.duoc.bitacora2.ui.theme.colores.EstadoPendiente
import cl.duoc.bitacora2.ui.theme.colores.EstadoRevisado
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun EstudianteHistorialScreen(
    titulo: String = "Mis registros e historial",
    onVolver: () -> Unit,
    onEditar: (Long) -> Unit,
    onVerRetroalimentacion: () -> Unit,
    viewModel: EstudianteHistorialViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarHistorial()
    }

    val registrosOrdenados = remember(uiState.registros) {
        uiState.registros.sortedByDescending { it.id ?: 0L }
    }

    PantallaBase(titulo = titulo, onVolver = onVolver) {

        // 1. Banner de aviso si falla refresco y ya hay lista
        if (uiState.errorAviso != null) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = uiState.errorAviso ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.limpiarAviso() }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }

        // 2. Cargando a pantalla completa (solo si la lista está vacía)
        if (uiState.cargando && registrosOrdenados.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // 3. Error de carga general (solo si la lista está vacía)
        else if (uiState.error != null && registrosOrdenados.isEmpty()) {
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
                            text = uiState.error ?: "Error al cargar los registros",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        BotonAncho("Reintentar") {
                            viewModel.cargarHistorial()
                        }
                    }
                }
            }
        }

        // 4. Lista vacía
        else if (registrosOrdenados.isEmpty() && !uiState.cargando) {
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

        // 5. Lista de registros
        else {
            items(registrosOrdenados, key = { it.id ?: 0L }) { registro ->
                val bitacora = registro.bitacoraId?.let { uiState.bitacorasMap[it] }
                val bitacoraTitulo = bitacora?.titulo ?: "Bitácora"
                val bitacoraAsignatura = bitacora?.asignatura ?: "Asignatura"
                val bitacoraCurso = bitacora?.curso ?: ""
                val bitacoraCerrada = bitacora?.estado == "cerrada"

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
                        // Fila superior: Asignatura + Insignia de estado
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
                                        text = bitacoraAsignatura,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            InsigniaEstadoRegistro(estado = registro.estado ?: "borrador")
                        }

                        // Título de la bitácora
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = bitacoraTitulo,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (bitacoraCurso.isNotBlank()) {
                                Text(
                                    text = bitacoraCurso,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                                    text = "Clase del ${registro.fechaClase ?: ""}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = registro.contenido ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (!registro.fechaCreacion.isNullOrBlank()) {
                                    Text(
                                        text = "Creado el ${registro.fechaCreacion}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }

                        // Acciones según el estado del registro
                        when (registro.estado) {
                            "borrador" -> {
                                if (bitacoraCerrada) {
                                    // Bitácora cerrada: no permite editar ni enviar, solo eliminar borrador
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = EstadoCerrada.copy(alpha = 0.12f)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Lock,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(14.dp),
                                                    tint = EstadoCerrada
                                                )
                                                Text(
                                                    text = "Bitácora cerrada",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = EstadoCerrada
                                                )
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = { registro.id?.let { viewModel.mostrarDialogoEliminar(it) } },
                                            modifier = Modifier.height(40.dp),
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Eliminar", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                } else {
                                    // Borrador normal: editar o eliminar
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { registro.id?.let { onEditar(it) } },
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
                                                text = "Seguir editando",
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = { registro.id?.let { viewModel.mostrarDialogoEliminar(it) } },
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

                            "pendiente" -> {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EstadoPendiente.copy(alpha = 0.1f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.HourglassTop,
                                            contentDescription = null,
                                            tint = EstadoPendiente,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = if (!registro.fechaEnvio.isNullOrBlank())
                                                "Enviado el ${registro.fechaEnvio} · Esperando revisión"
                                            else
                                                "Enviado · Esperando revisión del docente",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = EstadoPendiente
                                        )
                                    }
                                }
                            }

                            "revisado" -> {
                                Button(
                                    onClick = { onVerRetroalimentacion() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Feedback,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Ver retroalimentación",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar borrador
    if (uiState.idPendienteEliminar != null) {
        val estaEliminando = uiState.eliminandoId != null
        AlertDialog(
            onDismissRequest = {
                if (!estaEliminando) viewModel.ocultarDialogoEliminar()
            },
            title = { Text("Eliminar registro") },
            text = { Text("¿Estás seguro de que deseas eliminar este registro de clase? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmarEliminar() },
                    shape = RoundedCornerShape(10.dp),
                    enabled = !estaEliminando
                ) {
                    Text(if (estaEliminando) "Eliminando..." else "Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.ocultarDialogoEliminar() },
                    enabled = !estaEliminando
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun InsigniaEstadoRegistro(estado: String) {
    val color = when (estado) {
        "revisado" -> EstadoRevisado
        "pendiente" -> EstadoPendiente
        else -> EstadoBorrador
    }
    val texto = when (estado) {
        "revisado" -> "Revisado"
        "pendiente" -> "Pendiente"
        else -> "Borrador"
    }
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
