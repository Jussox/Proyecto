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
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.ui.theme.comun.nombreEstado

@Composable
fun EstudianteHistorialScreen(
    titulo: String,
    registros: List<Registro>,
    bitacoras: List<Bitacora>,
    onVolver: () -> Unit,
    onAbrir: (Int) -> Unit,
    onEnviar: (Int) -> Unit
) {
    val ordenados = registros.sortedByDescending { it.id }

    PantallaBase(titulo = titulo, onVolver = onVolver) {
        if (ordenados.isEmpty()) {
            item { Text("Aún no hay registros para mostrar.") }
        }
        items(ordenados) { r ->
            val b = bitacoras.firstOrNull { it.id == r.bitacoraId }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Clase del ${r.fechaClase}", style = MaterialTheme.typography.titleMedium)
                    Text("Bitácora: ${b?.titulo ?: "—"}")
                    Text("${b?.curso ?: ""} · ${b?.asignatura ?: ""}")
                    Text("Estado: ${nombreEstado(r.estado)}", color = MaterialTheme.colorScheme.primary)
                    BotonSecundario("Ver detalle") { onAbrir(r.id) }
                    if (r.estado == "borrador") {
                        BotonAncho("Enviar al docente") { onEnviar(r.id) }
                    }
                }
            }
        }
    }
}

@Composable
fun EstudianteRegistroDetalleScreen(
    registro: Registro?,
    bitacora: Bitacora?,
    onVolver: () -> Unit
) {
    PantallaBase(titulo = "Detalle del registro", onVolver = onVolver) {
        if (registro == null) {
            item { Text("El registro no está disponible.") }
        } else {
            item { Dato("Bitácora", bitacora?.titulo ?: "") }
            item { Dato("Curso y asignatura", "${bitacora?.curso ?: ""} · ${bitacora?.asignatura ?: ""}") }
            item { Dato("Fecha de la clase", registro.fechaClase) }
            item { Dato("Contenido trabajado", registro.contenido) }
            item { Dato("Actividad realizada", registro.actividad) }
            item { Dato("Aprendizaje obtenido", registro.aprendizaje) }
            item { Dato("Dudas o dificultades", registro.dudas) }
            item { Dato("Fotografías", registro.fotografias) }
            item { Dato("Archivos adjuntos", registro.archivos) }
            item { Dato("Enlaces", registro.enlaces) }
            item { Dato("Estado", nombreEstado(registro.estado)) }
            item { Dato("Fecha de envío", registro.fechaEnvio) }
            item { Text("Retroalimentación del docente", style = MaterialTheme.typography.titleMedium) }
            if (registro.comentario.isBlank()) {
                item { Text("Aún no hay retroalimentación.") }
            } else {
                item { Text(registro.comentario) }
                item { Dato("Fecha de retroalimentación", registro.fechaRetroalimentacion) }
            }
        }
    }
}