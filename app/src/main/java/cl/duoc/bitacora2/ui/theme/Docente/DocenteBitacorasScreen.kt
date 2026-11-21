package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.data.remoto.dto.BitacoraDto
import cl.duoc.bitacora2.ui.theme.colores.EstadoCerrada
import cl.duoc.bitacora2.ui.theme.colores.OpcionNoSeleccionadaFondo
import cl.duoc.bitacora2.ui.theme.colores.colorEstadoBitacora
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.Estado
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun DocenteBitacorasScreen(
    titulo: String = "Bitácoras mensuales",
    soloCerradas: Boolean = false,
    filtroCurso: String? = null,
    filtroAsignatura: String? = null,
    onVolver: () -> Unit,
    onVerAportes: (Long) -> Unit,
    onEditar: ((Long) -> Unit)? = null,
    viewModel: DocenteBitacorasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var bitacoraParaCerrar by remember { mutableStateOf<BitacoraDto?>(null) }

    val bitacorasAMostrar = remember(uiState.bitacoras, soloCerradas, filtroCurso, filtroAsignatura) {
        var lista = uiState.bitacoras
        if (soloCerradas) {
            lista = lista.filter { it.estado == "cerrada" }
        }
        if (!filtroCurso.isNullOrBlank()) {
            lista = lista.filter { it.curso.equals(filtroCurso, ignoreCase = true) }
        }
        if (!filtroAsignatura.isNullOrBlank()) {
            lista = lista.filter { it.asignatura.equals(filtroAsignatura, ignoreCase = true) }
        }
        lista
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        viewModel.cargarBitacoras()
    }

    PantallaBase(titulo = titulo, onVolver = onVolver) {

        // 1. Mensaje de error de acción (publicar/cerrar)
        if (uiState.errorAccion != null) {
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
                            text = uiState.errorAccion ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.limpiarErrorAccion() }) {
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
        if (uiState.cargando && bitacorasAMostrar.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // 3. Error de carga general (solo si la lista está vacía)
        else if (uiState.error != null && bitacorasAMostrar.isEmpty()) {
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
                            text = uiState.error ?: "Error al cargar las bitácoras",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        BotonAncho("Reintentar") {
                            viewModel.cargarBitacoras()
                        }
                    }
                }
            }
        }

        // 4. Lista vacía
        else if (bitacorasAMostrar.isEmpty() && !uiState.cargando) {
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
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            color = OpcionNoSeleccionadaFondo
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            text = if (!filtroAsignatura.isNullOrBlank())
                                "No hay bitácoras registradas para $filtroAsignatura ($filtroCurso)."
                            else
                                "No hay bitácoras para mostrar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 5. Lista de bitácoras
        else {
            items(bitacorasAMostrar, key = { it.id ?: 0L }) { b ->
                val colorEstado = colorEstadoBitacora(b.estado)

                val accionEnEjecucion = uiState.accionEnProgresoId == b.id

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(46.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Assignment,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = OpcionNoSeleccionadaFondo
                                ) {
                                    Text(
                                        text = Estado.textoDe(b.estado ?: "borrador").uppercase(),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = colorEstado
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = b.titulo ?: "",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Text(
                            text = "${b.curso ?: ""} · ${b.asignatura ?: ""} · ${b.mes ?: ""} ${b.anio ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                        Dato("Fecha de publicación", b.fechaPublicacion ?: "")
                        Dato("Fecha de cierre", b.fechaCierre ?: "")

                        // Acciones según estado válido ("borrador", "publicada", "cerrada")
                        if (b.estado == "borrador") {
                            BotonAncho(
                                texto = if (accionEnEjecucion) "Publicando..." else "Publicar bitácora",
                                enabled = uiState.accionEnProgresoId == null
                            ) {
                                b.id?.let { viewModel.publicarBitacora(it) }
                            }
                            BotonSecundario("Editar bitácora") {
                                b.id?.let { onEditar?.invoke(it) }
                            }
                        }

                        if (b.estado == "publicada") {
                            BotonAncho(
                                texto = if (accionEnEjecucion) "Cerrando..." else "Cerrar bitácora",
                                enabled = uiState.accionEnProgresoId == null
                            ) {
                                bitacoraParaCerrar = b
                            }
                            BotonSecundario("Ver aportes") {
                                b.id?.let { onVerAportes(it) }
                            }
                        }

                        if (b.estado == "cerrada") {
                            BotonSecundario("Ver aportes") {
                                b.id?.let { onVerAportes(it) }
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación para cerrar bitácora
    bitacoraParaCerrar?.let { b ->
        AlertDialog(
            onDismissRequest = { bitacoraParaCerrar = null },
            title = {
                Text(
                    text = "Confirmar cierre de bitácora",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Esta acción no se puede deshacer. La bitácora quedará cerrada y los estudiantes no podrán enviar nuevos registros."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val id = b.id
                        bitacoraParaCerrar = null
                        if (id != null) {
                            viewModel.cerrarBitacora(id)
                        }
                    }
                ) {
                    Text(
                        text = "Cerrar bitácora",
                        color = EstadoCerrada,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { bitacoraParaCerrar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
