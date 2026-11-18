package cl.duoc.bitacora2.ui.theme.Estudiante

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.Bitacora2Theme
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.ui.theme.comun.colorEstado

@Composable
fun EstudianteScreen(
    nombre: String,
    curso: String,
    onCursos: () -> Unit,
    onBitacoras: () -> Unit,
    onHistorial: () -> Unit,
    onRetroalimentacion: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    PantallaBase(titulo = "Bitácora") {
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
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "ESTUDIANTE",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = nombre,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = curso,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(10.dp)) }

        item {
            Text(
                text = "Acciones rápidas",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )
        }

        item {
            ItemMenuEstudiante(
                titulo = "Mis cursos y asignaturas",
                subtitulo = "Revisa las materias inscritas",
                icono = Icons.Default.MenuBook,
                colorIcono = MaterialTheme.colorScheme.primary,
                colorFondo = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                onClick = onCursos
            )
        }

        item {
            ItemMenuEstudiante(
                titulo = "Bitácoras mensuales publicadas",
                subtitulo = "Actividades activas para completar",
                icono = Icons.Default.Assignment,
                colorIcono = Color(0xFF1E8E3E),
                colorFondo = Color(0xFFE6F4EA),
                onClick = onBitacoras
            )
        }

        item {
            ItemMenuEstudiante(
                titulo = "Estado e historial",
                subtitulo = "Revisa registros enviados y borradores",
                icono = Icons.Default.History,
                colorIcono = Color(0xFFE37400),
                colorFondo = Color(0xFFFEF7E0),
                onClick = onHistorial
            )
        }

        item {
            ItemMenuEstudiante(
                titulo = "Retroalimentación del docente",
                subtitulo = "Comentarios y observaciones del profesor",
                icono = Icons.Default.Feedback,
                colorIcono = Color(0xFF9333EA),
                colorFondo = Color(0xFFF3E8FF),
                onClick = onRetroalimentacion
            )
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }

        item {
            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
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
private fun ItemMenuEstudiante(
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
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = colorFondo
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
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
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EstudianteBitacorasScreen(
    titulo: String,
    bitacoras: List<Bitacora>,
    onVolver: () -> Unit,
    onAbrir: (Int) -> Unit
) {
    PantallaBase(titulo = titulo, onVolver = onVolver) {
        if (bitacoras.isEmpty()) {
            item { Text("No hay bitácoras publicadas para mostrar.") }
        }
        items(bitacoras) { b ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("BITÁCORA ACTIVA", color = colorEstado("publicada"), fontWeight = FontWeight.Bold)
                    Text(b.titulo, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("${b.curso} · ${b.asignatura}")
                    Text("${b.mes} ${b.anio}")
                    BotonAncho("Abrir bitácora") { onAbrir(b.id) }
                }
            }
        }
    }
}

@Composable
fun EstudianteBitacoraScreen(
    bitacora: Bitacora?,
    onVolver: () -> Unit,
    onNuevoRegistro: () -> Unit
) {
    PantallaBase(titulo = "Bitácora mensual", onVolver = onVolver) {
        if (bitacora == null) {
            item { Text("La bitácora no está disponible.") }
        } else {
            item { Text(bitacora.titulo, style = MaterialTheme.typography.headlineSmall) }
            item { Dato("Curso", bitacora.curso) }
            item { Dato("Asignatura", bitacora.asignatura) }
            item { Dato("Mes y año", "${bitacora.mes} ${bitacora.anio}") }
            item { Dato("Propósito general", bitacora.proposito) }
            item { Dato("Instrucciones", bitacora.instrucciones) }
            item { Dato("Preguntas orientadoras", bitacora.preguntas) }
            item { BotonAncho("Crear registro de una clase") { onNuevoRegistro() } }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EstudianteScreenPreview() {
    Bitacora2Theme {
        EstudianteScreen(
            nombre = "David González",
            curso = "Desarrollo de Aplicaciones Móviles",
            onCursos = {},
            onBitacoras = {},
            onHistorial = {},
            onRetroalimentacion = {},
            onCerrarSesion = {}
        )
    }
}