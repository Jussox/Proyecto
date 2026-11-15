package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Recordatorio
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonOpcion
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Campo
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DocenteRecordatoriosScreen(
    recordatorios: List<Recordatorio>,
    onVolver: () -> Unit,
    onCrear: (String, String) -> Unit,
    onCompletar: (Int) -> Unit,
    viewModel: DocenteRecordatoriosViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tipos = listOf("Cierre mensual", "Creación de la siguiente bitácora", "Inicio de semestre")

    PantallaBase(titulo = "Recordatorios internos", onVolver = onVolver) {
        item { Text("Configurar recordatorio", style = MaterialTheme.typography.titleMedium) }
        items(tipos) { t ->
            BotonOpcion(t, uiState.tipo == t) { viewModel.cambiarTipo(t) }
        }
        item { Campo(uiState.fecha, "Fecha programada (dd/mm/aaaa)") { viewModel.cambiarFecha(it) } }
        item {
            BotonAncho("Configurar recordatorio") {
                viewModel.crearRecordatorio(onCrear)
            }
        }
        if (uiState.mensaje.isNotEmpty()) {
            item { Text(uiState.mensaje, color = MaterialTheme.colorScheme.primary) }
        }
        item { Text("Recordatorios disponibles", style = MaterialTheme.typography.titleMedium) }
        if (recordatorios.isEmpty()) {
            item { Text("No hay recordatorios configurados.") }
        }
        items(recordatorios.sortedByDescending { it.id }) { r ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(r.tipo, style = MaterialTheme.typography.titleMedium)
                    Text("Fecha programada: ${r.fechaProgramada}")
                    Text("Estado: ${if (r.estado == "pendiente") "Pendiente" else "Completado"}")
                    if (r.estado == "pendiente") {
                        BotonSecundario("Marcar como completado") {
                            viewModel.completarRecordatorio(r.id, onCompletar)
                        }
                    }
                }
            }
        }
        item { Text("Los recordatorios se muestran solo dentro de la aplicación; no envían notificaciones.") }
    }
}
