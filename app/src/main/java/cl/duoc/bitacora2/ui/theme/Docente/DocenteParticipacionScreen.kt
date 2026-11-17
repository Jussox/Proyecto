package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.data.fechaHoy
import cl.duoc.bitacora2.ui.theme.comun.*

@Composable
fun DocenteParticipacionScreen(
    modo: String,
    onVolver: () -> Unit,
    viewModel: DocenteParticipacionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val titulo = if (modo == "indicadores") "Indicadores generales"
    else if (modo == "informe") "Informe mensual"
    else "Seguimiento de participación"
    val bitacora = uiState.bitacora

    PantallaBase(titulo = titulo, onVolver = onVolver) {
        item { Text("Selecciona una bitácora", style = MaterialTheme.typography.titleMedium) }
        if (uiState.bitacoras.isEmpty()) {
            item { Text("Aún no hay bitácoras publicadas o cerradas.") }
        }
        items(uiState.bitacoras) { b ->
            BotonOpcion("${b.titulo} · ${b.curso} · ${b.asignatura}", b.id == uiState.seleccionada) {
                viewModel.seleccionarBitacora(b.id)
            }
        }

        if (bitacora != null) {
            val enviados = uiState.enviados
            val estudiantes = uiState.estudiantes
            val sinParticipacion = uiState.sinParticipacion
            val participantes = uiState.participantes
            val pendientes = uiState.pendientes
            val revisados = uiState.revisados

            if (modo == "seguimiento") {
                item { Text("Estudiantes sin participación (${sinParticipacion.size})", style = MaterialTheme.typography.titleMedium) }
                if (sinParticipacion.isEmpty()) {
                    item { Text("Todos los estudiantes del curso han enviado aportes.") }
                }
                items(sinParticipacion) { e -> Text("• ${e.nombre}") }
                item { Text("Estudiantes que participaron (${participantes.size})", style = MaterialTheme.typography.titleMedium) }
                items(participantes) { e -> Text("• ${e.nombre}") }
            }

            if (modo == "indicadores") {
                item { Text("Indicadores de ${bitacora.curso} · ${bitacora.asignatura}", style = MaterialTheme.typography.titleMedium) }
                item { Dato("Total de estudiantes", "${estudiantes.size}") }
                item { Dato("Estudiantes participantes", "${participantes.size}") }
                item { Dato("Estudiantes sin participación", "${sinParticipacion.size}") }
                item { Dato("Aportes enviados", "${enviados.size}") }
                //item { Dato("Aportes pendientes de revisión", "$pendientes") }
                item { Dato("Aportes revisados", "$revisados") }
            }

            if (modo == "informe") {
                item { Text("Informe mensual", style = MaterialTheme.typography.titleMedium) }
                item { Dato("Bitácora", bitacora.titulo) }
                item { Dato("Curso y asignatura", "${bitacora.curso} · ${bitacora.asignatura}") }
                item { Dato("Mes y año", "${bitacora.mes} ${bitacora.anio}") }
                item { Dato("Docente responsable", uiState.nombreDocente) }
                item { Dato("Propósito", bitacora.proposito) }
                item { Dato("Resumen de participación", "${participantes.size} de ${estudiantes.size} estudiantes participaron") }
                item { Dato("Aportes enviados", "${enviados.size}") }
                item { Dato("Aportes pendientes", "$pendientes") }
                item { Dato("Aportes revisados", "$revisados") }
                item {
                    Dato(
                        "Estudiantes sin participación",
                        if (sinParticipacion.isEmpty()) "Ninguno" else sinParticipacion.joinToString(", ") { it.nombre }
                    )
                }
                item { Dato("Fechas de las clases registradas", enviados.joinToString(", ") { it.fechaClase }) }
                item { Dato("Temas trabajados", enviados.joinToString("; ") { it.contenido }) }
                item { Dato("Fecha de cierre", bitacora.fechaCierre) }
                item { Dato("Fecha de generación del informe", fechaHoy()) }
                item { Text("La descarga en PDF no está disponible: no aparece en el material de clases.") }
            }
        }
    }
}
