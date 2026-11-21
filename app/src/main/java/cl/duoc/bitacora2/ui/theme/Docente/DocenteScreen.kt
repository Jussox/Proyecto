package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.colores.Blanco
import cl.duoc.bitacora2.ui.theme.colores.CerrarSesionFondo
import cl.duoc.bitacora2.ui.theme.colores.EstadoBorrador
import cl.duoc.bitacora2.ui.theme.colores.EstadoCerrada
import cl.duoc.bitacora2.ui.theme.colores.EstadoPendiente
import cl.duoc.bitacora2.ui.theme.colores.EstadoPublicada
import cl.duoc.bitacora2.ui.theme.colores.MenuMoradoFondo
import cl.duoc.bitacora2.ui.theme.colores.MenuMoradoIcono
import cl.duoc.bitacora2.ui.theme.colores.MenuNaranjaFondo
import cl.duoc.bitacora2.ui.theme.colores.MenuNaranjaIcono
import cl.duoc.bitacora2.ui.theme.colores.MenuVerdeFondo
import cl.duoc.bitacora2.ui.theme.colores.MenuVerdeIcono
import cl.duoc.bitacora2.ui.theme.colores.OpcionNoSeleccionadaFondo
import cl.duoc.bitacora2.ui.theme.colores.OpcionNoSeleccionadaTexto
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionFondo
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionIcono
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionTarjeta
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.Estado
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.ui.theme.comun.animacionEntradaExpandir
import cl.duoc.bitacora2.ui.theme.comun.animacionSalidaContraer

@Composable
fun DocenteScreen(
    nombre: String,
    aportesPendientes: Int,
    recordatoriosPendientes: Int,
    onCursos: () -> Unit,
    onCrear: () -> Unit,
    onBitacoras: () -> Unit,
    onAportes: () -> Unit,
    onPendientes: () -> Unit,
    onParticipacion: () -> Unit,
    onIndicadores: () -> Unit,
    onHistorial: () -> Unit,
    onInforme: () -> Unit,
    onRecordatorios: () -> Unit,
    onCerrarSesion: () -> Unit,
    viewModel: DocenteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PantallaBase(titulo = "Bitácora") {

        // Perfil Docente
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = OpcionNoSeleccionadaFondo
                        ) {
                            Text(
                                text = "DOCENTE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = OpcionNoSeleccionadaTexto
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nombre,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Recordatorios pendientes: $recordatoriosPendientes",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (recordatoriosPendientes > 0) {
                                EstadoPendiente
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(6.dp)) }

        // 1. ADMINISTRACIÓN DE BITÁCORAS
        item {
            SeccionAcordeonDocente(
                titulo = "Administración de bitácoras",
                subtitulo = "4 opciones disponibles",
                icono = Icons.Default.FolderOpen,
                colorIcono = MaterialTheme.colorScheme.primary,
                colorFondoIcono = OpcionNoSeleccionadaFondo,
                expandido = uiState.administracionExpandida,
                onToggle = { viewModel.toggleAdministracion() }
            ) {
                ItemSubMenuDocente(
                    titulo = "Mis cursos y asignaturas",
                    icono = Icons.Default.MenuBook,
                    colorIcono = MaterialTheme.colorScheme.primary,
                    colorFondo = OpcionNoSeleccionadaFondo,
                    onClick = onCursos
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 62.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ItemSubMenuDocente(
                    titulo = "Crear bitácora",
                    icono = Icons.Default.AddCircleOutline,
                    colorIcono = MenuVerdeIcono,
                    colorFondo = MenuVerdeFondo,
                    onClick = onCrear
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 62.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ItemSubMenuDocente(
                    titulo = "Mis bitácoras mensuales",
                    icono = Icons.Default.Assignment,
                    colorIcono = ParticipacionIcono,
                    colorFondo = ParticipacionFondo,
                    onClick = onBitacoras
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 62.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ItemSubMenuDocente(
                    titulo = "Historial de bitácoras",
                    icono = Icons.Default.History,
                    colorIcono = MenuNaranjaIcono,
                    colorFondo = MenuNaranjaFondo,
                    onClick = onHistorial
                )
            }
        }

        // 2. APORTES ENVIADOS POR LOS ESTUDIANTES (Tarjeta directa entre ambas secciones)
        item {
            TarjetaAccesoDirectoDocente(
                titulo = "Aportes enviados por los estudiantes",
                subtitulo = "Revisa y retroalimenta los registros",
                icono = Icons.Default.Inbox,
                colorIcono = ParticipacionIcono,
                colorFondo = ParticipacionFondo,
                onClick = onAportes
            )
        }

        // 3. SEGUIMIENTO
        item {
            SeccionAcordeonDocente(
                titulo = "Seguimiento",
                subtitulo = "Participación, indicadores e informes",
                icono = Icons.Default.Analytics,
                colorIcono = ParticipacionIcono,
                colorFondoIcono = ParticipacionFondo,
                expandido = uiState.seguimientoExpandido,
                onToggle = { viewModel.toggleSeguimiento() }
            ) {
                ItemSubMenuDocente(
                    titulo = "Seguimiento de participación",
                    icono = Icons.Default.Group,
                    colorIcono = ParticipacionIcono,
                    colorFondo = ParticipacionFondo,
                    onClick = onParticipacion
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 62.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ItemSubMenuDocente(
                    titulo = "Indicadores generales",
                    icono = Icons.Default.Analytics,
                    colorIcono = MenuVerdeIcono,
                    colorFondo = MenuVerdeFondo,
                    onClick = onIndicadores
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 62.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ItemSubMenuDocente(
                    titulo = "Informe mensual",
                    icono = Icons.Default.Description,
                    colorIcono = MenuMoradoIcono,
                    colorFondo = MenuMoradoFondo,
                    onClick = onInforme
                )
                HorizontalDivider(
                    modifier = Modifier.padding(start = 62.dp, end = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                ItemSubMenuDocente(
                    titulo = "Recordatorios internos",
                    icono = Icons.Default.NotificationsActive,
                    colorIcono = MenuNaranjaIcono,
                    colorFondo = MenuNaranjaFondo,
                    onClick = onRecordatorios
                )
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        item {
            Button(
                onClick = onCerrarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CerrarSesionFondo,
                    contentColor = Blanco
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerrar sesión",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun TarjetaAccesoDirectoDocente(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    colorIcono: Color,
    colorFondo: Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = colorFondo
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun SeccionAcordeonDocente(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    colorIcono: Color,
    colorFondoIcono: Color,
    expandido: Boolean,
    onToggle: () -> Unit,
    contenido: @Composable () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (expandido) 3.dp else 1.5.dp
        )
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color.Transparent,
            border = if (expandido) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            } else {
                null
            }
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (expandido) ParticipacionTarjeta else MaterialTheme.colorScheme.surface
                        )
                        .clickable(onClick = onToggle)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = colorFondoIcono
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icono,
                                contentDescription = null,
                                tint = colorIcono,
                                modifier = Modifier.size(21.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = titulo,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitulo,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = if (expandido) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = if (expandido) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                AnimatedVisibility(
                    visible = expandido,
                    enter = animacionEntradaExpandir,
                    exit = animacionSalidaContraer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        contenido()
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemSubMenuDocente(
    titulo: String,
    icono: ImageVector,
    colorIcono: Color,
    colorFondo: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 20.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = RoundedCornerShape(10.dp),
            color = colorFondo
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Text(
            text = titulo,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun DocenteBitacorasScreen(
    titulo: String,
    bitacoras: List<Bitacora>,
    onVolver: () -> Unit,
    onPublicar: (Int) -> Unit,
    onCerrar: (Int) -> Unit,
    onVerAportes: (Int) -> Unit
) {
    PantallaBase(titulo = titulo, onVolver = onVolver) {
        if (bitacoras.isEmpty()) {
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
                            text = "No hay bitácoras para mostrar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        items(bitacoras) { b ->
            val colorEstado = when (b.estado) {
                "publicada" -> EstadoPublicada
                "cerrada" -> EstadoCerrada
                else -> EstadoBorrador
            }

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
                                    text = Estado.textoDe(b.estado).uppercase(),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = colorEstado
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = b.titulo,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = "${b.curso} · ${b.asignatura} · ${b.mes} ${b.anio}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Dato("Fecha de publicación", b.fechaPublicacion)
                    Dato("Fecha de cierre", b.fechaCierre)

                    if (b.estado == "guardada") {
                        BotonAncho("Publicar bitácora") { onPublicar(b.id) }
                    }
                    if (b.estado == "publicada") {
                        BotonAncho("Cerrar bitácora") { onCerrar(b.id) }
                    }
                    if (b.estado != "guardada") {
                        BotonSecundario("Ver aportes") { onVerAportes(b.id) }
                    }
                }
            }
        }
    }
}