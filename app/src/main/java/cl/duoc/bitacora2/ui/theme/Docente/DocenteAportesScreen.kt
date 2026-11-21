package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonOpcion
import cl.duoc.bitacora2.ui.theme.comun.Campo
import cl.duoc.bitacora2.ui.theme.comun.Estado
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun DocenteAportesScreen(
    bitacoraId: Long? = null,
    estadoInicial: String = "todos",
    titulo: String = "Aportes de estudiantes",
    onVolver: () -> Unit,
    onAbrir: (Long, Long) -> Unit,
    viewModel: DocenteAportesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarAportes(bitacoraId, estadoInicial)
    }

    val filtrados = remember(
        uiState.registros,
        uiState.filtroEstudiante,
        uiState.filtroFecha,
        uiState.filtroEstado,
        uiState.estudiantesMap
    ) {
        uiState.registros.filter { r ->
            val estudianteUsuario = r.estudiante ?: ""
            val estudianteNombre = uiState.estudiantesMap[estudianteUsuario] ?: ""
            val cumpleEstudiante = uiState.filtroEstudiante.isBlank() ||
                    estudianteUsuario.contains(uiState.filtroEstudiante, ignoreCase = true) ||
                    estudianteNombre.contains(uiState.filtroEstudiante, ignoreCase = true)

            val fechaClase = r.fechaClase ?: ""
            val cumpleFecha = uiState.filtroFecha.isBlank() || fechaClase.contains(uiState.filtroFecha)

            val estado = r.estado ?: ""
            val cumpleEstado = uiState.filtroEstado == "todos" || estado.equals(uiState.filtroEstado, ignoreCase = true)

            cumpleEstudiante && cumpleFecha && cumpleEstado
        }.sortedByDescending { it.id ?: 0L }
    }

    PantallaBase(titulo = titulo, onVolver = onVolver) {
        // 1. Banner de aviso si falla refresco y ya hay datos
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
        if (uiState.cargando && uiState.registros.isEmpty()) {
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

        // 3. Error general con botón Reintentar (solo si la lista está vacía)
        else if (uiState.error != null && uiState.registros.isEmpty()) {
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
                            text = uiState.error ?: "Error al cargar los aportes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.cargarAportes(bitacoraId, estadoInicial) }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }

        // 4. Contenido cargado con filtros y tarjetas
        else {
            item { Text("Filtros", style = MaterialTheme.typography.titleMedium) }
            item {
                Campo(uiState.filtroEstudiante, "Estudiante (nombre o usuario)") {
                    viewModel.cambiarFiltroEstudiante(it)
                }
            }
            item {
                Campo(uiState.filtroFecha, "Fecha de la clase (dd/mm/aaaa)") {
                    viewModel.cambiarFiltroFecha(it)
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BotonOpcion("Todos", uiState.filtroEstado == "todos", Modifier.weight(1f)) {
                        viewModel.cambiarFiltroEstado("todos")
                    }
                    BotonOpcion("Pendientes", uiState.filtroEstado == "pendiente", Modifier.weight(1f)) {
                        viewModel.cambiarFiltroEstado("pendiente")
                    }
                    BotonOpcion("Revisados", uiState.filtroEstado == "revisado", Modifier.weight(1f)) {
                        viewModel.cambiarFiltroEstado("revisado")
                    }
                }
            }
            item {
                Text(
                    text = "Aportes encontrados: ${filtrados.size}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (filtrados.isEmpty()) {
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
                            Text(
                                text = "No se encontraron aportes",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "No hay registros que coincidan con los filtros seleccionados.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            items(filtrados) { r ->
                val b = r.bitacoraId?.let { uiState.bitacorasMap[it] }
                val estudianteUsuario = r.estudiante ?: ""
                val nombreEstudiante = uiState.estudiantesMap[estudianteUsuario]?.ifBlank { estudianteUsuario }
                    ?: estudianteUsuario.ifBlank { "Estudiante" }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(nombreEstudiante, style = MaterialTheme.typography.titleMedium)
                        Text("Clase del ${r.fechaClase ?: "—"}")
                        Text("${b?.titulo ?: "Bitácora"} · ${b?.curso ?: ""} · ${b?.asignatura ?: ""}")
                        Text(
                            text = "Estado: ${Estado.textoDe(r.estado ?: "")}",
                            color = MaterialTheme.colorScheme.primary
                        )
                        val regId = r.id ?: 0L
                        val bId = r.bitacoraId ?: (bitacoraId ?: 0L)
                        BotonAncho("Revisar aporte") {
                            onAbrir(regId, bId)
                        }
                    }
                }
            }
        }
    }
}
