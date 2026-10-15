package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

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
        item { Text("Perfil: Estudiante", style = MaterialTheme.typography.headlineSmall) }
        item { Text("Estudiante: $nombre") }
        item { Text("Curso: $curso") }
        item { BotonAncho("Mis cursos y asignaturas") { onCursos() } }
        item { BotonAncho("Bitácoras mensuales publicadas") { onBitacoras() } }
        item { BotonAncho("Mis registros, estados e historial") { onHistorial() } }
        item { BotonAncho("Retroalimentación del docente") { onRetroalimentacion() } }
        item { BotonSecundario("Cerrar sesión") { onCerrarSesion() } }
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("BITÁCORA ACTIVA", color = MaterialTheme.colorScheme.primary)
                    Text(b.titulo, style = MaterialTheme.typography.titleMedium)
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