package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
fun EstudianteBitacorasScreen(
    titulo: String = "Bitácoras publicadas",
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

@Preview(name = "Bitácoras del Estudiante", showBackground = true, showSystemUi = true)
@Composable
fun EstudianteBitacorasPreview() {
    Bitacora2Theme {
        EstudianteBitacorasScreen(
            titulo = "Mis cursos y asignaturas",
            bitacoras = listOf(
                Bitacora(
                    id = 1,
                    docente = "Prof. Juan Pérez",
                    curso = "Ingeniería en Informática",
                    asignatura = "Taller de Integración",
                    mes = "Noviembre",
                    anio = "2024",
                    titulo = "Bitácora Práctica Profesional",
                    proposito = "Registrar las actividades semanales del proyecto.",
                    instrucciones = "Completar después de cada sesión de laboratorio.",
                    preguntas = "¿Qué dificultades encontraste hoy?",
                    estado = "publicada"
                )
            ),
            onVolver = {},
            onAbrir = {}
        )
    }
}