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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PersonOff
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
import cl.duoc.bitacora2.data.fechaHoy
import cl.duoc.bitacora2.ui.theme.colores.EstadoBorrador
import cl.duoc.bitacora2.ui.theme.colores.EstadoPendiente
import cl.duoc.bitacora2.ui.theme.colores.EstadoRevisado
import cl.duoc.bitacora2.ui.theme.colores.MenuNaranjaFondo
import cl.duoc.bitacora2.ui.theme.colores.MenuVerdeFondo
import cl.duoc.bitacora2.ui.theme.colores.OpcionNoSeleccionadaFondo
import cl.duoc.bitacora2.ui.theme.colores.OpcionNoSeleccionadaTexto
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionFondo
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionIcono
import cl.duoc.bitacora2.ui.theme.colores.ParticipacionTarjeta
import cl.duoc.bitacora2.ui.theme.colores.colorFondoParticipacion
import cl.duoc.bitacora2.ui.theme.colores.colorParticipacion
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun DocenteParticipacionScreen(
    modo: String,
    onVolver: () -> Unit,
    viewModel: DocenteParticipacionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val titulo = when (modo) {
        "indicadores" -> "Indicadores generales"
        "informe" -> "Informe mensual"
        else -> "Seguimiento de participación"
    }

    val bitacora = uiState.bitacora

    PantallaBase(titulo = titulo, onVolver = onVolver) {

        // ENCABEZADO (Estilo tarjeta superior de EstudianteScreen usando tus tokens Participacion*)
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = ParticipacionTarjeta
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
                        color = ParticipacionFondo
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (modo) {
                                    "indicadores" -> Icons.Default.Analytics
                                    "informe" -> Icons.Default.Description
                                    else -> Icons.Default.Group
                                },
                                contentDescription = null,
                                tint = ParticipacionIcono,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = ParticipacionFondo
                        ) {
                            Text(
                                text = when (modo) {
                                    "indicadores" -> "INDICADORES"
                                    "informe" -> "INFORME"
                                    else -> "DOCENTE"
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = ParticipacionIcono
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = when (modo) {
                                "indicadores" -> "Resumen del rendimiento"
                                "informe" -> "Resumen de actividad"
                                else -> "Participación de estudiantes"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Consulta el progreso y la actividad de los estudiantes de cada curso.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        // SELECCIÓN DE BITÁCORA
        item {
            SeccionTitulo("Seleccionar bitácora")
        }

        if (uiState.bitacoras.isEmpty()) {
            item {
                TarjetaMensaje(
                    titulo = "Sin bitácoras disponibles",
                    descripcion = "Todavía no hay bitácoras publicadas o cerradas para consultar."
                )
            }
        }

        items(uiState.bitacoras) { b ->
            val seleccionada = b.id == uiState.seleccionada

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.seleccionarBitacora(b.id) },
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
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(42.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (seleccionada) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                OpcionNoSeleccionadaFondo
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = if (seleccionada) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        OpcionNoSeleccionadaTexto
                                    },
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            if (seleccionada) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "SELECCIONADA",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            Text(
                                text = b.titulo,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "${b.curso} · ${b.asignatura}",
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

        // CONTENIDO DE LA BITÁCORA SELECCIONADA
        if (bitacora != null) {

            val enviados = uiState.enviados
            val estudiantes = uiState.estudiantes
            val sinParticipacion = uiState.sinParticipacion
            val participantes = uiState.participantes
            val pendientes = uiState.pendientes
            val revisados = uiState.revisados

            item {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = bitacora.titulo,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${bitacora.curso} · ${bitacora.asignatura}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // SEGUIMIENTO
            if (modo == "seguimiento") {

                item {
                    SeccionTitulo("Resumen de participación")
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TarjetaIndicador(
                            titulo = "Participaron",
                            valor = participantes.size.toString(),
                            icono = Icons.Default.CheckCircle,
                            colorIcono = EstadoRevisado,
                            colorFondoIcono = MenuVerdeFondo,
                            modifier = Modifier.weight(1f)
                        )

                        TarjetaIndicador(
                            titulo = "Sin participar",
                            valor = sinParticipacion.size.toString(),
                            icono = Icons.Default.PersonOff,
                            colorIcono = EstadoPendiente,
                            colorFondoIcono = MenuNaranjaFondo,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SeccionTitulo(
                        "Estudiantes sin participación (${sinParticipacion.size})"
                    )
                }

                if (sinParticipacion.isEmpty()) {
                    item {
                        TarjetaMensaje(
                            titulo = "¡Excelente participación!",
                            descripcion = "Todos los estudiantes del curso han enviado aportes."
                        )
                    }
                }

                items(sinParticipacion) { e ->
                    TarjetaEstudiante(
                        nombre = e.nombre,
                        participo = false
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))

                    SeccionTitulo(
                        "Estudiantes que participaron (${participantes.size})"
                    )
                }

                if (participantes.isEmpty()) {
                    item {
                        TarjetaMensaje(
                            titulo = "Sin participantes",
                            descripcion = "Todavía no se han registrado aportes de estudiantes."
                        )
                    }
                }

                items(participantes) { e ->
                    TarjetaEstudiante(
                        nombre = e.nombre,
                        participo = true
                    )
                }
            }

            // INDICADORES
            if (modo == "indicadores") {

                item {
                    SeccionTitulo("Indicadores del curso")
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TarjetaIndicador(
                            titulo = "Estudiantes",
                            valor = estudiantes.size.toString(),
                            icono = Icons.Default.Group,
                            colorIcono = ParticipacionIcono,
                            colorFondoIcono = ParticipacionFondo,
                            modifier = Modifier.weight(1f)
                        )

                        TarjetaIndicador(
                            titulo = "Participantes",
                            valor = participantes.size.toString(),
                            icono = Icons.Default.CheckCircle,
                            colorIcono = EstadoRevisado,
                            colorFondoIcono = MenuVerdeFondo,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TarjetaIndicador(
                            titulo = "Sin participación",
                            valor = sinParticipacion.size.toString(),
                            icono = Icons.Default.PersonOff,
                            colorIcono = EstadoPendiente,
                            colorFondoIcono = MenuNaranjaFondo,
                            modifier = Modifier.weight(1f)
                        )

                        TarjetaIndicador(
                            titulo = "Aportes enviados",
                            valor = enviados.size.toString(),
                            icono = Icons.Default.Assignment,
                            colorIcono = MaterialTheme.colorScheme.primary,
                            colorFondoIcono = OpcionNoSeleccionadaFondo,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SeccionTitulo("Estado de las revisiones")
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TarjetaIndicador(
                            titulo = "Pendientes de revisión",
                            valor = pendientes.toString(),
                            icono = Icons.Default.PendingActions,
                            colorIcono = EstadoPendiente,
                            colorFondoIcono = MenuNaranjaFondo,
                            modifier = Modifier.weight(1f)
                        )

                        TarjetaIndicador(
                            titulo = "Aportes revisados",
                            valor = revisados.toString(),
                            icono = Icons.Default.CheckCircle,
                            colorIcono = EstadoRevisado,
                            colorFondoIcono = MenuVerdeFondo,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // INFORME
            if (modo == "informe") {

                item {
                    SeccionTitulo("Información general")
                    Spacer(modifier = Modifier.height(8.dp))

                    TarjetaGrupoDatos(
                        datos = listOf(
                            "Bitácora" to bitacora.titulo,
                            "Curso y asignatura" to "${bitacora.curso} · ${bitacora.asignatura}",
                            "Mes y año" to "${bitacora.mes} ${bitacora.anio}",
                            "Docente responsable" to uiState.nombreDocente,
                            "Propósito" to bitacora.proposito
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SeccionTitulo("Resultados de participación")
                    Spacer(modifier = Modifier.height(8.dp))

                    TarjetaGrupoDatos(
                        datos = listOf(
                            "Participación" to "${participantes.size} de ${estudiantes.size} estudiantes",
                            "Aportes enviados" to enviados.size.toString(),
                            "Aportes pendientes" to pendientes.toString(),
                            "Aportes revisados" to revisados.toString(),
                            "Estudiantes sin participación" to if (sinParticipacion.isEmpty()) {
                                "Ninguno"
                            } else {
                                sinParticipacion.joinToString(", ") { it.nombre }
                            }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SeccionTitulo("Registro de clases")
                    Spacer(modifier = Modifier.height(8.dp))

                    TarjetaGrupoDatos(
                        datos = listOf(
                            "Fechas de clases" to enviados.joinToString(", ") { it.fechaClase },
                            "Temas trabajados" to enviados.joinToString("; ") { it.contenido }
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    SeccionTitulo("Fechas del informe")
                    Spacer(modifier = Modifier.height(8.dp))

                    TarjetaGrupoDatos(
                        datos = listOf(
                            "Fecha de cierre" to bitacora.fechaCierre,
                            "Generación del informe" to fechaHoy()
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TarjetaMensaje(
                        titulo = "Descarga de informe",
                        descripcion = "La descarga en PDF no está disponible en la versión actual de la aplicación."
                    )
                }
            }
        }
    }
}

// --------------------------------------------------
// COMPONENTES VISUALES
// --------------------------------------------------

@Composable
private fun SeccionTitulo(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun TarjetaIndicador(
    titulo: String,
    valor: String,
    icono: ImageVector,
    colorIcono: Color,
    colorFondoIcono: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(11.dp),
                color = colorFondoIcono
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = valor,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TarjetaEstudiante(
    nombre: String,
    participo: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
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
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (participo) MenuVerdeFondo else MenuNaranjaFondo,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = nombre.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorParticipacion(participo)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (participo) "Con aporte enviado" else "Pendiente de envío",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (participo) EstadoRevisado else EstadoBorrador
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = colorFondoParticipacion(participo)
            ) {
                Text(
                    text = if (participo) "PARTICIPÓ" else "SIN ENVÍO",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = colorParticipacion(participo)
                )
            }
        }
    }
}

@Composable
private fun TarjetaGrupoDatos(
    datos: List<Pair<String, String>>
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            datos.forEachIndexed { index, (titulo, valor) ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = valor.ifBlank { "Sin información" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (index < datos.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun TarjetaMensaje(
    titulo: String,
    descripcion: String
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = ParticipacionTarjeta
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(11.dp),
                color = ParticipacionFondo
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ParticipacionIcono,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}