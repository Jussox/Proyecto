package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.ui.theme.Bitacora2Theme
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.EstadoTexto
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun EstudianteHistorialScreen(
    titulo: String = "Mis registros e historial",
    registros: List<Registro>,
    bitacoras: List<Bitacora>,
    onVolver: () -> Unit,
    onEditar: (Registro) -> Unit, // 👈 AQUÍ ESTÁ EL onEditar QUE PEDÍA Navegacion.kt
    onEliminar: (Int) -> Unit,
    viewModel: EstudianteHistorialViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val ordenados = registros.sortedByDescending { it.id }

    PantallaBase(titulo = titulo, onVolver = onVolver) {
        if (ordenados.isEmpty()) {
            item {
                Text("Todavía no tienes registros guardados.")
            }
        }

        items(ordenados) { registro ->
            val bitacora = bitacoras.firstOrNull { it.id == registro.bitacoraId }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        bitacora?.titulo ?: "Bitácora no disponible",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Asignatura: ${bitacora?.asignatura ?: "No disponible"}")
                    Text("Fecha de clase: ${registro.fechaClase}")
                    Text("Contenido: ${registro.contenido}")
                    EstadoTexto(registro.estado)
                    Text("Creado: ${registro.fechaCreacion}")

                    BotonAncho("Seguir editándolo") {
                        onEditar(registro)
                    }

                    BotonSecundario("Eliminarlo") {
                        viewModel.mostrarDialogoEliminar(registro.id)
                    }
                }
            }
        }
    }

    if (uiState.idPendienteEliminar != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.ocultarDialogoEliminar()
            },
            title = {
                Text("Eliminar registro")
            },
            text = {
                Text("¿Seguro que quieres eliminar este registro?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.confirmarEliminar(onEliminar)
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.ocultarDialogoEliminar()
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EstudianteHistorialPreview() {
    Bitacora2Theme {
        EstudianteHistorialScreen(
            registros = emptyList(),
            bitacoras = emptyList(),
            onVolver = {},
            onEditar = {},
            onEliminar = {}
        )
    }
}