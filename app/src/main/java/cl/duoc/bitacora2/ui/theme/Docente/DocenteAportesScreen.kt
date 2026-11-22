package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionFondo
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionIcono
import cl.duoc.bitacora2.ui.theme.colores.colorEstadoRegistro
import cl.duoc.bitacora2.ui.theme.colores.colorFondoAvatarAporte
import cl.duoc.bitacora2.ui.theme.colores.colorFondoEstadoRegistro
import cl.duoc.bitacora2.ui.theme.colores.colorIconoAvatarAporte
import cl.duoc.bitacora2.ui.theme.comun.AnimacionBannerAvisoDocente
import cl.duoc.bitacora2.ui.theme.comun.AnimacionFiltrosAportes
import cl.duoc.bitacora2.ui.theme.comun.BotonOpcion
import cl.duoc.bitacora2.ui.theme.comun.Campo
import cl.duoc.bitacora2.ui.theme.comun.Estado
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.util.cumpleFiltrosAporte
import cl.duoc.bitacora2.util.validarCaracteresFiltroFecha

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
    var filtrosExpandidos by rememberSaveable { mutableStateOf(true) }

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
            cumpleFiltrosAporte(
                estudianteUsuario = estudianteUsuario,
                estudianteNombre = estudianteNombre,
                fechaClase = r.fechaClase ?: "",
                estadoRegistro = r.estado ?: "",
                filtroEstudiante = uiState.filtroEstudiante,
                filtroFecha = uiState.filtroFecha,
                filtroEstado = uiState.filtroEstado
            )
        }.sortedByDescending { it.id ?: 0L }
    }

    val hayFiltrosActivos = uiState.filtroEstudiante.isNotBlank() ||
            uiState.filtroFecha.isNotBlank() ||
            uiState.filtroEstado != "todos"

    PantallaBase(titulo = titulo, onVolver = onVolver) {
        // 1. Banner animado de aviso si falla refresco y ya hay datos en pantalla
        item {
            AnimacionBannerAvisoDocente(visible = uiState.errorAviso != null) {
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
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.limpiarAviso() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar aviso",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Estado de carga inicial
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

        // 3. Estado de error bloqueante con reintento
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
                            text = uiState.error ?: "Error al cargar los aportes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Button(
                            onClick = { viewModel.cargarAportes(bitacoraId, estadoInicial) },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }

        // 4. Contenido principal: Tarjeta resumen + Filtros + Lista de aportes
        else {
            // Tarjeta cabecera estilo EstudianteScreen
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(50.dp),
                                shape = RoundedCornerShape(16.dp),
                                color = ParticipacionFondo
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.AssignmentTurnedIn,
                                        contentDescription = null,
                                        tint = ParticipacionIcono,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = ParticipacionFondo
                                ) {
                                    Text(
                                        text = "REVISIÓN DOCENTE",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = ParticipacionIcono
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${filtrados.size} ${if (filtrados.size == 1) "aporte encontrado" else "aportes encontrados"}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Toca una tarjeta para revisar y retroalimentar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { filtrosExpandidos = !filtrosExpandidos }) {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "Mostrar u ocultar filtros",
                                    tint = if (hayFiltrosActivos) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Panel de filtros colapsable animado
                        AnimacionFiltrosAportes(visible = filtrosExpandidos) {
                            Column(
                                modifier = Modifier.padding(top = 14.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Filtrar entregas",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (hayFiltrosActivos) {
                                        TextButton(
                                            onClick = {
                                                viewModel.cambiarFiltroEstudiante("")
                                                viewModel.cambiarFiltroFecha("")
                                                viewModel.cambiarFiltroEstado("todos")
                                            }
                                        ) {
                                            Text(
                                                text = "Limpiar",
                                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                }

                                Campo(
                                    valor = uiState.filtroEstudiante,
                                    etiqueta = "Estudiante (nombre o usuario)"
                                ) {
                                    viewModel.cambiarFiltroEstudiante(it)
                                }

                                Campo(
                                    valor = uiState.filtroFecha,
                                    etiqueta = "Fecha de la clase (dd/mm/aaaa)"
                                ) { nuevoTexto ->
                                    if (validarCaracteresFiltroFecha(nuevoTexto)) {
                                        viewModel.cambiarFiltroFecha(nuevoTexto)
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    BotonOpcion(
                                        texto = "Todos",
                                        seleccionado = uiState.filtroEstado == "todos",
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        viewModel.cambiarFiltroEstado("todos")
                                    }
                                    BotonOpcion(
                                        texto = "Pendientes",
                                        seleccionado = uiState.filtroEstado == "pendiente",
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        viewModel.cambiarFiltroEstado("pendiente")
                                    }
                                    BotonOpcion(
                                        texto = "Revisados",
                                        seleccionado = uiState.filtroEstado == "revisado",
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        viewModel.cambiarFiltroEstado("revisado")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Listado de entregas",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }

            // 5. Estado vacío si los filtros no arrojan resultados
            if (filtrados.isEmpty()) {
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.size(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = ParticipacionFondo
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        tint = ParticipacionIcono,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Text(
                                text = "No se encontraron aportes",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
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

            // 6. Tarjetas individuales estilo ItemMenuEstudiante
            items(
                items = filtrados,
                key = { it.id ?: it.hashCode() }
            ) { r ->
                val b = r.bitacoraId?.let { uiState.bitacorasMap[it] }
                val estudianteUsuario = r.estudiante ?: ""
                val nombreEstudiante = uiState.estudiantesMap[estudianteUsuario]?.ifBlank { estudianteUsuario }
                    ?: estudianteUsuario.ifBlank { "Estudiante" }

                val regId = r.id ?: 0L
                val bId = r.bitacoraId ?: (bitacoraId ?: 0L)
                val estadoActual = r.estado ?: ""

                ItemAporteEstudiante(
                    nombreEstudiante = nombreEstudiante,
                    fechaClase = r.fechaClase ?: "—",
                    tituloBitacora = b?.titulo ?: "Bitácora",
                    cursoAsignatura = listOfNotNull(
                        b?.curso?.takeIf { it.isNotBlank() },
                        b?.asignatura?.takeIf { it.isNotBlank() }
                    ).joinToString(" · "),
                    estado = estadoActual,
                    onClick = { onAbrir(regId, bId) }
                )
            }
        }
    }
}

@Composable
private fun ItemAporteEstudiante(
    nombreEstudiante: String,
    fechaClase: String,
    tituloBitacora: String,
    cursoAsignatura: String,
    estado: String,
    onClick: () -> Unit
) {
    val colorEstado = colorEstadoRegistro(estado)
    val fondoEstado = colorFondoEstadoRegistro(estado)
    val fondoAvatar = colorFondoAvatarAporte(estado)
    val iconoAvatar = colorIconoAvatarAporte(estado)

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Avatar cuadrado con esquinas redondeadas de 12dp igual que EstudianteScreen
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = fondoAvatar
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = iconoAvatar,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Información central del aporte
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Badge superior con el estado del registro
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = fondoEstado
                ) {
                    Text(
                        text = Estado.textoDe(estado).uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = colorEstado
                    )
                }

                Text(
                    text = nombreEstudiante,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = if (cursoAsignatura.isNotBlank()) {
                        "$tituloBitacora · $cursoAsignatura"
                    } else {
                        tituloBitacora
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "Clase del $fechaClase",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Indicador de navegación lateral idéntico a EstudianteScreen
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Revisar aporte",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}