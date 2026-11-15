package cl.duoc.bitacora2.ui.theme.Docente

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
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
import coil3.compose.AsyncImage

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DocenteAportesScreen(
    titulo: String,
    registros: List<Registro>,
    bitacoras: List<Bitacora>,
    estadoInicial: String,
    onVolver: () -> Unit,
    onAbrir: (Int) -> Unit,
    viewModel: DocenteAportesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estadoInicial) {
        viewModel.inicializarFiltroEstado(estadoInicial)
    }

    val filtrados = registros.filter { r ->
        (uiState.filtroEstudiante.isBlank() ||
                r.estudiante.contains(uiState.filtroEstudiante, ignoreCase = true) ||
                DatosPrueba.nombreDe(r.estudiante).contains(uiState.filtroEstudiante, ignoreCase = true)) &&
                (uiState.filtroFecha.isBlank() || r.fechaClase.contains(uiState.filtroFecha)) &&
                (uiState.filtroEstado == "todos" || r.estado == uiState.filtroEstado)
    }.sortedByDescending { it.id }

    PantallaBase(titulo = titulo, onVolver = onVolver) {
        item { Text("Filtros", style = MaterialTheme.typography.titleMedium) }
        item { Campo(uiState.filtroEstudiante, "Estudiante (nombre o usuario)") { viewModel.cambiarFiltroEstudiante(it) } }
        item { Campo(uiState.filtroFecha, "Fecha de la clase (dd/mm/aaaa)") { viewModel.cambiarFiltroFecha(it) } }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                BotonOpcion("Todos", uiState.filtroEstado == "todos", Modifier.weight(1f)) { viewModel.cambiarFiltroEstado("todos") }
                BotonOpcion("Pendientes", uiState.filtroEstado == "pendiente", Modifier.weight(1f)) { viewModel.cambiarFiltroEstado("pendiente") }
                BotonOpcion("Revisados", uiState.filtroEstado == "revisado", Modifier.weight(1f)) { viewModel.cambiarFiltroEstado("revisado") }
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
    onMarcarRevisado: (Int) -> Unit,
    viewModel: DocenteRevisionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(registro?.id) {
        viewModel.inicializarComentario(registro?.comentario ?: "")
    }

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
            item { Text("Fotografías:") }
            if (registro.fotografias.isBlank()) {
                item { Text("—") }
            } else {
                item {
                    AsyncImage(
                        model = Uri.parse(registro.fotografias),
                        contentDescription = "Fotografía del estudiante",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }
            }
            item { Dato("Archivos adjuntos", registro.archivos) }
            item { Dato("Enlaces relacionados", registro.enlaces) }
            item { Dato("Estado", nombreEstado(registro.estado)) }
            item { Text("Retroalimentación", style = MaterialTheme.typography.titleMedium) }
            item { Campo(uiState.comentario, "Comentario de retroalimentación") { viewModel.cambiarComentario(it) } }
            item {
                BotonSecundario("Guardar comentario") {
                    viewModel.guardarComentario(registro.id, bitacora?.docente ?: "") { id, c ->
                        onGuardarComentario(id, c)
                    }
                }
            }
            if (registro.estado == "revisado") {
                item { Text("Este aporte ya fue marcado como revisado.") }
            } else {
                item {
                    BotonAncho("Marcar como revisado") {
                        viewModel.marcarRevisado(registro.id) { id ->
                            onMarcarRevisado(id)
                        }
                    }
                }
            }
            if (uiState.mensaje.isNotEmpty()) {
                item { Text(uiState.mensaje, color = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}
