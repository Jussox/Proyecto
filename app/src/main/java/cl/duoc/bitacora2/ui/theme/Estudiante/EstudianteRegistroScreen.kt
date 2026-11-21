package cl.duoc.bitacora2.ui.theme.Estudiante

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.ui.theme.comun.AdjuntosSection
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.MensajeAnimado
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun EstudianteRegistroScreen(
    bitacoraId: Long? = null,
    registroId: Long? = null,
    onVolver: () -> Unit,
    viewModel: EstudianteRegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(bitacoraId, registroId) {
        viewModel.inicializar(bitacoraId, registroId)
    }

    LaunchedEffect(uiState.guardado) {
        if (uiState.guardado) {
            onVolver()
        }
    }

    val tituloPantalla = if (uiState.esModoEdicion) "Editar registro de clase" else "Registro de clase"

    PantallaBase(titulo = tituloPantalla, onVolver = onVolver) {

        // 1. Cargando
        if (uiState.cargando) {
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
            return@PantallaBase
        }

        // 2. Error general de carga
        if (uiState.error != null) {
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
                            text = uiState.error ?: "Error al cargar la información",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        BotonAncho("Reintentar") {
                            viewModel.inicializar(bitacoraId, registroId)
                        }
                        BotonSecundario("Volver") {
                            onVolver()
                        }
                    }
                }
            }
            return@PantallaBase
        }

        // 3. Estado inválido (bitácora cerrada o registro no borrador)
        if (uiState.estadoInvalido) {
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
                            text = uiState.mensajeEstadoInvalido,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        BotonSecundario("Volver") {
                            onVolver()
                        }
                    }
                }
            }
            return@PantallaBase
        }

        val bitacora = uiState.bitacora
        if (bitacora == null) {
            item { Text("La bitácora no está disponible.") }
            return@PantallaBase
        }

        // 4. Banner cabecera de la bitácora
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "BITÁCORA ACTIVA",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = bitacora.titulo ?: "",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${bitacora.curso ?: ""} · ${bitacora.asignatura ?: ""} · ${bitacora.mes ?: ""} ${bitacora.anio ?: ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // 5. Sección: Datos de la clase
        item {
            TarjetaSeccionRegistro(
                titulo = "Datos de la clase",
                icono = Icons.Default.DateRange
            ) {
                CampoFormulario(
                    valor = uiState.fecha,
                    etiqueta = "Fecha de la clase (dd/mm/aaaa)",
                    icono = Icons.Default.DateRange,
                    onValorCambio = { viewModel.cambiarFecha(it) }
                )
                CampoFormulario(
                    valor = uiState.contenido,
                    etiqueta = "Contenido trabajado",
                    icono = Icons.Default.Description,
                    onValorCambio = { viewModel.cambiarContenido(it) }
                )
                CampoFormulario(
                    valor = uiState.actividad,
                    etiqueta = "Actividad realizada",
                    icono = Icons.Default.Assignment,
                    onValorCambio = { viewModel.cambiarActividad(it) }
                )
            }
        }

        // 6. Sección: Reflexión y aprendizaje
        item {
            TarjetaSeccionRegistro(
                titulo = "Reflexión y aprendizaje",
                icono = Icons.Default.Lightbulb
            ) {
                CampoFormulario(
                    valor = uiState.aprendizaje,
                    etiqueta = "¿Qué aprendió en la clase?",
                    icono = Icons.Default.Lightbulb,
                    minLineas = 2,
                    onValorCambio = { viewModel.cambiarAprendizaje(it) }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                )

                Text(
                    text = "Dudas o dificultades",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.dudas,
                        onValueChange = { viewModel.cambiarDudas(it) },
                        label = { Text("Escribe una duda...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { viewModel.agregarDuda() },
                        enabled = uiState.dudas.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Agregar duda",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar")
                    }
                }

                if (uiState.listaDudas.isEmpty()) {
                    Text(
                        text = "Sin dudas registradas (opcional).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        uiState.listaDudas.forEachIndexed { index, duda ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HelpOutline,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = duda,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    IconButton(
                                        onClick = { viewModel.eliminarDuda(index) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Eliminar duda",
                                            modifier = Modifier.size(16.dp),
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 7. Sección: Evidencias y adjuntos
        item {
            TarjetaSeccionRegistro(
                titulo = "Evidencias y adjuntos",
                icono = Icons.Default.AttachFile
            ) {
                AdjuntosSection(
                    uriFoto = uiState.fotografias,
                    enlace = uiState.enlaces,
                    onFotoCambio = { viewModel.cambiarFotografias(it) },
                    onEnlaceCambio = { viewModel.cambiarEnlaces(it) },
                    onError = { viewModel.mostrarError(it) }
                )
                CampoFormulario(
                    valor = uiState.archivos,
                    etiqueta = "Archivos adjuntos (nombre del archivo)",
                    icono = Icons.Default.AttachFile,
                    onValorCambio = { viewModel.cambiarArchivos(it) }
                )
            }
        }

        // 8. Mensaje de estado / error
        if (uiState.mensaje.isNotEmpty()) {
            item {
                MensajeAnimado(
                    texto = uiState.mensaje,
                    esError = uiState.mensajeEsError
                )
            }
        }

        // 9. Botones de acción
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BotonAncho(
                    texto = if (uiState.guardando) "Enviando..." else "Enviar al docente",
                    enabled = !uiState.guardando
                ) {
                    viewModel.enviarAlDocente()
                }

                BotonSecundario(
                    texto = if (uiState.guardando) "Guardando..." else "Guardar borrador",
                    enabled = !uiState.guardando
                ) {
                    viewModel.guardarBorrador()
                }

                BotonSecundario("Volver / cancelar") {
                    onVolver()
                }
            }
        }
    }
}

// ==========================================
// COMPOSABLES PRIVADOS
// ==========================================

@Composable
private fun TarjetaSeccionRegistro(
    titulo: String,
    icono: ImageVector,
    contenido: @Composable () -> Unit
) {
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.size(28.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icono,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

            contenido()
        }
    }
}

@Composable
private fun CampoFormulario(
    valor: String,
    etiqueta: String,
    icono: ImageVector,
    minLineas: Int = 1,
    onValorCambio: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambio,
        label = { Text(etiqueta) },
        leadingIcon = {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
        },
        minLines = minLineas,
        maxLines = if (minLineas > 1) 4 else 1,
        singleLine = minLineas == 1,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    )
}
