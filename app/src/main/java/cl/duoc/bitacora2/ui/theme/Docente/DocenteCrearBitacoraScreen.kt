package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.RadioButtonUnchecked
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
import cl.duoc.bitacora2.data.Asignacion
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.colores.EstadoRevisado
import cl.duoc.bitacora2.ui.theme.colores.MenuVerdeFondo
import cl.duoc.bitacora2.ui.theme.colores.MenuVerdeIcono
import cl.duoc.bitacora2.ui.theme.colores.OpcionNoSeleccionadaFondo
import cl.duoc.bitacora2.ui.theme.colores.OpcionNoSeleccionadaTexto
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionFondo
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionIcono
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionTarjeta
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Campo
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun DocenteCrearBitacoraScreen(
    asignaciones: List<Asignacion>,
    docente: String,
    onVolver: () -> Unit,
    onGuardar: (Bitacora) -> Unit,
    viewModel: DocenteCrearBitacoraViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PantallaBase(titulo = "Crear bitácora", onVolver = onVolver) {

        // Tarjeta de encabezado
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
                        modifier = Modifier.size(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MenuVerdeFondo
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.AddCircleOutline,
                                contentDescription = null,
                                tint = MenuVerdeIcono,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = OpcionNoSeleccionadaFondo
                        ) {
                            Text(
                                text = "NUEVA BITÁCORA",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = OpcionNoSeleccionadaTexto
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Configuración mensual",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Define el curso, período y orientaciones para los estudiantes.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(6.dp)) }

        // 1. SELECCIÓN DE CURSO Y ASIGNATURA
        item {
            SeccionFormularioTitulo(
                titulo = "1. Curso y asignatura",
                icono = Icons.Default.MenuBook,
                colorIcono = MaterialTheme.colorScheme.primary,
                colorFondoIcono = OpcionNoSeleccionadaFondo
            )
        }

        items(asignaciones) { a ->
            val seleccionada = uiState.seleccionada == a

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.seleccionarAsignacion(a) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = if (seleccionada) ParticipacionTarjeta else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if (seleccionada) 3.dp else 1.dp
                )
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Transparent,
                    border = if (seleccionada) {
                        BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        null
                    }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (seleccionada) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                OpcionNoSeleccionadaFondo
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = if (seleccionada) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        OpcionNoSeleccionadaTexto
                                    },
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = a.curso,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = a.asignatura,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = if (seleccionada) {
                                Icons.Default.CheckCircle
                            } else {
                                Icons.Default.RadioButtonUnchecked
                            },
                            contentDescription = null,
                            tint = if (seleccionada) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(6.dp)) }

        // 2. PERÍODO Y TÍTULO
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
                    SeccionFormularioTitulo(
                        titulo = "2. Período e identificación",
                        icono = Icons.Default.CalendarMonth,
                        colorIcono = ParticipacionIcono,
                        colorFondoIcono = ParticipacionFondo
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            Campo(uiState.mes, "Mes (ej: Octubre)") { viewModel.cambiarMes(it) }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            Campo(uiState.anio, "Año (ej: 2026)") { viewModel.cambiarAnio(it) }
                        }
                    }

                    Campo(uiState.titulo, "Título de la bitácora") { viewModel.cambiarTitulo(it) }
                }
            }
        }

        // 3. CONTENIDO Y ORIENTACIONES
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
                    SeccionFormularioTitulo(
                        titulo = "3. Contenido y orientaciones",
                        icono = Icons.Default.Description,
                        colorIcono = MenuVerdeIcono,
                        colorFondoIcono = MenuVerdeFondo
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    Campo(uiState.proposito, "Propósito general") { viewModel.cambiarProposito(it) }
                    Campo(uiState.instrucciones, "Instrucciones para los estudiantes") {
                        viewModel.cambiarInstrucciones(it)
                    }
                    Campo(uiState.preguntas, "Preguntas orientadoras") {
                        viewModel.cambiarPreguntas(it)
                    }
                }
            }
        }

        // Mensaje de validación / confirmación
        if (uiState.mensaje.isNotEmpty()) {
            item {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (uiState.mensajeEsError) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MenuVerdeFondo
                        }
                    ),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.mensajeEsError) {
                                Icons.Default.ErrorOutline
                            } else {
                                Icons.Default.CheckCircle
                            },
                            contentDescription = null,
                            tint = if (uiState.mensajeEsError) {
                                MaterialTheme.colorScheme.error
                            } else {
                                EstadoRevisado
                            },
                            modifier = Modifier.size(22.dp)
                        )

                        Text(
                            text = uiState.mensaje,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = if (uiState.mensajeEsError) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                EstadoRevisado
                            }
                        )
                    }
                }
            }
        }

        // Botones de acción
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BotonAncho("Guardar y publicar bitácora") {
                    viewModel.guardar(docente, true, onGuardar)
                }
                BotonSecundario("Guardar como borrador") {
                    viewModel.guardar(docente, false, onGuardar)
                }
                BotonSecundario("Volver / cancelar") {
                    onVolver()
                }
            }
        }
    }
}

@Composable
private fun SeccionFormularioTitulo(
    titulo: String,
    icono: ImageVector,
    colorIcono: Color,
    colorFondoIcono: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            modifier = Modifier.size(34.dp),
            shape = RoundedCornerShape(10.dp),
            color = colorFondoIcono
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
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}