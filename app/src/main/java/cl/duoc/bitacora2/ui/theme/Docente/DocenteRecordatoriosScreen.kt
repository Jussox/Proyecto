package cl.duoc.bitacora2.ui.theme.Docente

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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.data.Recordatorio
import cl.duoc.bitacora2.ui.theme.colores.MenuVerdeFondo
import cl.duoc.bitacora2.ui.theme.colores.MenuVerdeIcono
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionFondo
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionIcono
import cl.duoc.bitacora2.ui.theme.colores.colorEstadoRecordatorio
import cl.duoc.bitacora2.ui.theme.colores.colorFondoEstadoRecordatorio
import cl.duoc.bitacora2.ui.theme.comun.AnimacionMensajeRecordatorio
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonOpcion
import cl.duoc.bitacora2.ui.theme.comun.Campo
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.util.TipoMensajeRecordatorio
import cl.duoc.bitacora2.util.clasificarMensajeRecordatorio
import cl.duoc.bitacora2.util.esRecordatorioPendiente
import cl.duoc.bitacora2.util.validarCaracteresFiltroFecha

@Composable
fun DocenteRecordatoriosScreen(
    recordatorios: List<Recordatorio>,
    onVolver: () -> Unit,
    onCrear: (String, String) -> Unit,
    onCompletar: (Int) -> Unit,
    viewModel: DocenteRecordatoriosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tipos = listOf(
        "Cierre mensual",
        "Creación de la siguiente bitácora",
        "Inicio de semestre"
    )
    val pendientesCount = recordatorios.count { esRecordatorioPendiente(it.estado) }
    val tipoMensaje = clasificarMensajeRecordatorio(uiState.mensaje)

    PantallaBase(titulo = "Recordatorios internos", onVolver = onVolver) {
        // 1. Tarjeta cabecera + Formulario de configuración
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
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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
                                    imageVector = Icons.Default.Alarm,
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
                                    text = "GESTIÓN DOCENTE",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = ParticipacionIcono
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Configurar recordatorio",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$pendientesCount ${if (pendientesCount == 1) "pendiente activo" else "pendientes activos"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )

                    Text(
                        text = "Tipo de recordatorio",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        tipos.forEach { t ->
                            BotonOpcion(
                                texto = t,
                                seleccionado = uiState.tipo == t,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                viewModel.cambiarTipo(t)
                            }
                        }
                    }

                    Campo(
                        valor = uiState.fecha,
                        etiqueta = "Fecha programada (dd/mm/aaaa)"
                    ) { nuevaFecha ->
                        if (validarCaracteresFiltroFecha(nuevaFecha)) {
                            viewModel.cambiarFecha(nuevaFecha)
                        }
                    }

                    BotonAncho("Configurar recordatorio") {
                        viewModel.crearRecordatorio(onCrear)
                    }

                    AnimacionMensajeRecordatorio(
                        mensaje = uiState.mensaje,
                        esError = tipoMensaje == TipoMensajeRecordatorio.ERROR,
                        esCompletado = tipoMensaje == TipoMensajeRecordatorio.COMPLETADO
                    )
                }
            }
        }

        // 2. Título de sección
        item {
            Text(
                text = "Recordatorios disponibles (${recordatorios.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        // 3. Estado vacío
        if (recordatorios.isEmpty()) {
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
                                    imageVector = Icons.Default.NotificationsNone,
                                    contentDescription = null,
                                    tint = ParticipacionIcono,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Text(
                            text = "Sin recordatorios configurados",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Usa el formulario superior para programar hitos del curso.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 4. Listado de recordatorios
        items(
            items = recordatorios.sortedByDescending { it.id },
            key = { it.id }
        ) { r ->
            ItemRecordatorioDocente(
                recordatorio = r,
                onCompletar = {
                    viewModel.completarRecordatorio(
                        id = r.id,
                        onCompletar = onCompletar,
                        tipoRecordatorio = r.tipo
                    )
                }
            )
        }

        // 5. Nota informativa al pie
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Aviso interno: estos recordatorios funcionan como guía visual dentro de la aplicación y no envían notificaciones push al teléfono.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ItemRecordatorioDocente(
    recordatorio: Recordatorio,
    onCompletar: () -> Unit
) {
    val pendiente = esRecordatorioPendiente(recordatorio.estado)
    val colorEstado = colorEstadoRecordatorio(recordatorio.estado)
    val fondoEstado = colorFondoEstadoRecordatorio(recordatorio.estado)

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
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = fondoEstado
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (pendiente) Icons.Default.Schedule else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = colorEstado,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = fondoEstado
                    ) {
                        Text(
                            text = if (pendiente) "PENDIENTE" else "COMPLETADO",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = colorEstado
                        )
                    }

                    Text(
                        text = recordatorio.tipo,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
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
                            text = "Fecha programada: ${recordatorio.fechaProgramada}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (pendiente) {
                OutlinedButton(
                    onClick = onCompletar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MenuVerdeIcono.copy(alpha = 0.45f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MenuVerdeIcono,
                        containerColor = MenuVerdeFondo.copy(alpha = 0.35f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Marcar como completado",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}