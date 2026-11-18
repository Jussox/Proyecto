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
import cl.duoc.bitacora2.ui.theme.comun.Estado
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
                    Text("Estado: ${Estado.textoDe(r.estado)}", color = MaterialTheme.colorScheme.primary)
                    BotonAncho("Revisar aporte") { onAbrir(r.id) }
                }
            }
        }
    }
}


