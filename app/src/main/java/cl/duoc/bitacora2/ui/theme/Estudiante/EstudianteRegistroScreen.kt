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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.ui.theme.Bitacora2Theme
import cl.duoc.bitacora2.ui.theme.comun.AdjuntosSection
import cl.duoc.bitacora2.ui.theme.comun.MensajeAnimado
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun EstudianteRegistroScreen(
    bitacora: Bitacora?,
    estudiante: String,
    registroExistente: Registro? = null,
    onVolver: () -> Unit,
    onGuardar: (Registro) -> Unit,
    viewModel: EstudianteRegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(registroExistente?.id, bitacora?.id, bitacora?.mes, bitacora?.anio) {
        viewModel.inicializar(
            bitacoraId = bitacora?.id ?: 0,
            mes = bitacora?.mes ?: "",
            anio = bitacora?.anio ?: "",
            estudiante = estudiante,
            registro = registroExistente
        )
    }

    PantallaBase(titulo = "Registro de clase", onVolver = onVolver) {
        if (bitacora == null) {
            item { Text("La bitácora no está disponible.") }
        } else {

            // 1. BANNER CABECERA DE LA BITÁCORA
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
                            text = bitacora.titulo,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${bitacora.curso} · ${bitacora.asignatura}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // 2. SECCIÓN: DATOS DE LA CLASE
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

            // 3. SECCIÓN: REFLEXIÓN, APRENDIZAJE Y LISTA DE DUDAS
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

                    // DUDAS: campo + botón agregar
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

                    // Lista de dudas agregadas
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

            // 4. SECCIÓN: ADJUNTOS Y EVIDENCIAS
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

            // 5. MENSAJE DE ESTADO / ERROR
            item {
                MensajeAnimado(
                    texto = uiState.mensaje,
                    esError = uiState.mensajeEsError
                )
            }

            // 6. BOTONES DE ACCIÓN
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.enviarAlDocente(onGuardar) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Enviar al docente",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.guardarBorrador(onGuardar) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Guardar borrador",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    TextButton(
                        onClick = { onVolver() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Volver / cancelar",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
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

// ==========================================
// VISTAS PREVIAS (AÑO 2026)
// ==========================================

@Preview(
    name = "Formulario Nuevo Registro",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun EstudianteRegistroNuevoPreview() {
    Bitacora2Theme {
        EstudianteRegistroScreen(
            bitacora = Bitacora(
                id = 1,
                docente = "Prof. Juan Pérez",
                curso = "Ingeniería en Informática",
                asignatura = "Taller de Integración",
                mes = "Noviembre",
                anio = "2026",
                titulo = "Bitácora Práctica Profesional",
                proposito = "Registrar las actividades semanales de clase.",
                instrucciones = "Completar después de cada sesión.",
                preguntas = "¿Qué dificultades encontraste?",
                estado = "publicada"
            ),
            estudiante = "david.alumno",
            registroExistente = null,
            onVolver = {},
            onGuardar = {}
        )
    }
}

@Preview(
    name = "Formulario Editando (con Dudas Listadas)",
    showBackground = true,
    showSystemUi = true,
    heightDp = 1200
)
@Composable
fun EstudianteRegistroEditandoPreview() {
    Bitacora2Theme {
        EstudianteRegistroScreen(
            bitacora = Bitacora(
                id = 1,
                docente = "Prof. Juan Pérez",
                curso = "Ingeniería en Informática",
                asignatura = "Taller de Integración",
                mes = "Noviembre",
                anio = "2026",
                titulo = "Bitácora Práctica Profesional",
                proposito = "Registrar las actividades de clase.",
                instrucciones = "Completar después de cada sesión.",
                preguntas = "¿Qué dificultades encontraste?",
                estado = "publicada"
            ),
            estudiante = "david.alumno",
            registroExistente = Registro(
                id = 1,
                bitacoraId = 1,
                estudiante = "david.alumno",
                fechaClase = "18/11/2026",
                contenido = "Arquitectura MVVM y Jetpack Compose",
                actividad = "Diseño de pantallas y validación de formularios",
                aprendizaje = "Gestión de StateFlow y componentes reutilizables",
                dudas = "Manejo de StateFlow en Compose\nValidación con LaunchedEffect",
                fotografias = "",
                archivos = "actividad_clase.pdf",
                enlaces = "https://github.com",
                estado = "borrador",
                comentario = "",
                fechaCreacion = "18/11/2026",
                fechaEnvio = "",
                fechaRetroalimentacion = "",
                docenteRevisor = ""
            ),
            onVolver = {},
            onGuardar = {}
        )
    }
}