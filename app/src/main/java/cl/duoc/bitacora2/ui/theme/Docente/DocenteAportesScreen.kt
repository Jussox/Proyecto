package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.DatosPrueba
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonOpcion
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Campo
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.ui.theme.comun.nombreEstado

@Composable
fun DocenteAportesScreen(
    titulo: String,
    registros: List<Registro>,
    bitacoras: List<Bitacora>,
    estadoInicial: String,
    onVolver: () -> Unit,
    onAbrir: (Int) -> Unit
) {
    var filtroEstudiante by remember { mutableStateOf("") }
    var filtroFecha by remember { mutableStateOf("") }
    var filtroEstado by remember(estadoInicial) { mutableStateOf(estadoInicial) }

    val filtrados = registros.filter { r ->
        (filtroEstudiante.isBlank() ||
                r.estudiante.contains(filtroEstudiante, ignoreCase = true) ||
                DatosPrueba.nombreDe(r.estudiante).contains(filtroEstudiante, ignoreCase = true)) &&
                (filtroFecha.isBlank() || r.fechaClase.contains(filtroFecha)) &&
                (filtroEstado == "todos" || r.estado == filtroEstado)
    }.sortedByDescending { it.id }

    PantallaBase(titulo = titulo, onVolver = onVolver) {
        item { Text("Filtros", style = MaterialTheme.typography.titleMedium) }
        item { Campo(filtroEstudiante, "Estudiante (nombre o usuario)") { filtroEstudiante = it } }
        item { Campo(filtroFecha, "Fecha de la clase (dd/mm/aaaa)") { filtroFecha = it } }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonOpcion("Todos", filtroEstado == "todos", Modifier.weight(1f)) { filtroEstado = "todos" }
                BotonOpcion("Pendientes", filtroEstado == "pendiente", Modifier.weight(1f)) { filtroEstado = "pendiente" }
                BotonOpcion("Revisados", filtroEstado == "revisado", Modifier.weight(1f)) { filtroEstado = "revisado" }
            }
        }
        item { Text("Aportes encontrados: ${filtrados.size}") }
        items(filtrados) { r ->
            val b = bitacoras.firstOrNull { it.id == r.bitacoraId }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(DatosPrueba.nombreDe(r.estudiante), style = MaterialTheme.typography.titleMedium)
                    Text("Clase del ${r.fechaClase}")
                    Text("${b?.titulo ?: "—"} · ${b?.curso ?: ""} · ${b?.asignatura ?: ""}")
                    Text("Estado: ${nombreEstado(r.estado)}", color = MaterialTheme.colorScheme.primary)
                    BotonAncho("Revisar aporte") { onAbrir(r.id) }
                }
            }
        }
    }
}

@Composable
fun DocenteRevisionScreen(
    registro: Registro?,
    bitacora: Bitacora?,
    onVolver: () -> Unit,
    onGuardarComentario: (Int, String) -> Unit,
    onMarcarRevisado: (Int) -> Unit
) {
    var comentario by remember(registro?.id) { mutableStateOf(registro?.comentario ?: "") }
    var mensaje by remember { mutableStateOf("") }

    PantallaBase(titulo = "Revisión de aporte", onVolver = onVolver) {
        if (registro == null) {
            item { Text("El aporte no está disponible.") }
        } else {
            item { Dato("Estudiante", DatosPrueba.nombreDe(registro.estudiante)) }
            item { Dato("Bitácora", bitacora?.titulo ?: "") }
            item { Dato("Curso y asignatura", "${bitacora?.curso ?: ""} · ${bitacora?.asignatura ?: ""}") }
            item { Dato("Fecha de la clase", registro.fechaClase) }
            item { Dato("Contenido trabajado", registro.contenido) }
            item { Dato("Actividad realizada", registro.actividad) }
            item { Dato("Qué aprendió", registro.aprendizaje) }
            item { Dato("Dudas o dificultades", registro.dudas) }
            item { Dato("Fotografías", registro.fotografias) }
            item { Dato("Archivos adjuntos", registro.archivos) }
            item { Dato("Enlaces relacionados", registro.enlaces) }
            item { Dato("Estado", nombreEstado(registro.estado)) }
            item { Text("Retroalimentación", style = MaterialTheme.typography.titleMedium) }
            item { Campo(comentario, "Comentario de retroalimentación") { comentario = it } }
            item {
                BotonSecundario("Guardar comentario") {
                    if (comentario.isBlank()) {
                        mensaje = "Escriba un comentario antes de guardar"
                    } else {
                        onGuardarComentario(registro.id, comentario)
                        mensaje = "Comentario guardado"
                    }
                }
            }
            if (registro.estado == "revisado") {
                item { Text("Este aporte ya fue marcado como revisado.") }
            } else {
                item {
                    BotonAncho("Marcar como revisado") {
                        onMarcarRevisado(registro.id)
                        mensaje = "Aporte marcado como revisado"
                    }
                }
            }
            if (mensaje.isNotEmpty()) {
                item { Text(mensaje, color = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}