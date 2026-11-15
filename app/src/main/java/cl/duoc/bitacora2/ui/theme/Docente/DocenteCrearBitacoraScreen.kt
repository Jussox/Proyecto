package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import cl.duoc.bitacora2.data.Asignacion
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.comun.*

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DocenteCrearBitacoraScreen(
    asignaciones: List<Asignacion>,
    docente: String,
    onVolver: () -> Unit,
    onGuardar: (Bitacora) -> Unit,
    viewModel: DocenteCrearBitacoraViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PantallaBase(titulo = "Crear bitácora", onVolver = onVolver) {
        item { Text("Curso y asignatura", style = MaterialTheme.typography.titleMedium) }
        items(asignaciones) { a ->
            BotonOpcion("${a.curso} · ${a.asignatura}", uiState.seleccionada == a) { viewModel.seleccionarAsignacion(a) }
        }
        item { Campo(uiState.mes, "Mes (por ejemplo: Octubre)") { viewModel.cambiarMes(it) } }
        item { Campo(uiState.anio, "Año (por ejemplo: 2026)") { viewModel.cambiarAnio(it) } }
        item { Campo(uiState.titulo, "Título de la bitácora") { viewModel.cambiarTitulo(it) } }
        item { Campo(uiState.proposito, "Propósito general") { viewModel.cambiarProposito(it) } }
        item { Campo(uiState.instrucciones, "Instrucciones para los estudiantes") { viewModel.cambiarInstrucciones(it) } }
        item { Campo(uiState.preguntas, "Preguntas orientadoras") { viewModel.cambiarPreguntas(it) } }

        if (uiState.mensaje.isNotEmpty()) {
            item {
                Text(
                    text = uiState.mensaje,
                    color = if (uiState.mensajeEsError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }
        }

        item { BotonSecundario("Guardar") { viewModel.guardar(docente, false, onGuardar) } }
        item { BotonAncho("Guardar y publicar bitácora") { viewModel.guardar(docente, true, onGuardar) } }
        item { BotonSecundario("Volver / cancelar") { onVolver() } }
    }
}
