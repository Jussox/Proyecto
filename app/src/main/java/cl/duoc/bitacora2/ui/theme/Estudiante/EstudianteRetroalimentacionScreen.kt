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
import cl.duoc.bitacora2.data.DatosPrueba
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun EstudianteRetroalimentacionScreen(
    registros: List<Registro>,
    bitacoras: List<Bitacora>,
    onVolver: () -> Unit
) {
    // Solo registros del estudiante que tienen retroalimentación
    val retroalimentados = registros
        .filter { it.comentario.isNotBlank() || it.estado == "revisado" }
        .sortedByDescending { it.fechaRetroalimentacion }

    PantallaBase(
        titulo = "Retroalimentación",
        onVolver = onVolver
    ) {
        if (retroalimentados.isEmpty()) {
            item { Text("Aún no tienes retroalimentación de tus aportes.") }
        }
        items(retroalimentados) { r ->
            val b = bitacoras.firstOrNull { it.id == r.bitacoraId }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(b?.titulo ?: "Bitácora", style = MaterialTheme.typography.titleMedium)
                    Text("${b?.curso ?: ""} · ${b?.asignatura ?: ""}")
                    Dato("Clase del", r.fechaClase)
                    Dato("Contenido trabajado", r.contenido)
                    Text(
                        "Estado: ${if (r.estado == "revisado") "Revisado" else r.estado}",
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (r.comentario.isNotBlank()) {
                        Text("Comentario del docente:", style = MaterialTheme.typography.titleSmall)
                        Text(r.comentario)
                        Dato("Fecha de retroalimentación", r.fechaRetroalimentacion)
                        Dato("Revisado por", DatosPrueba.nombreDe(r.docenteRevisor))
                    }
                }
            }
        }
    }
}