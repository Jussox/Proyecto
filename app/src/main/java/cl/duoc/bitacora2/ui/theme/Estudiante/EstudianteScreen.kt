package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.EstadoTexto
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.ui.theme.comun.colorEstado

import cl.duoc.bitacora2.data.Registro


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
        item { BotonAncho("Estado e historial") { onHistorial() } }
        item { BotonAncho("Retroalimentación del docente") { onRetroalimentacion() } }

        item {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }


        item {
            Button(
                onClick = { onCerrarSesion() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9F3434),
                    contentColor = Color.White
                )
            ) {

                Text("Cerrar sesión")
            }
        }
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
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("BITÁCORA ACTIVA", color = colorEstado("publicada"))
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


@Composable
fun EstudianteHistorialScreen(
    registros: List<Registro>,
    bitacoras: List<Bitacora>,
    onVolver: () -> Unit,
    onEditar: (Registro) -> Unit,
    onEliminar: (Int) -> Unit
) {
    var idPendienteEliminar by remember {
        mutableStateOf<Int?>(null)
    }

    PantallaBase(
        titulo = "Mis registros e historial",
        onVolver = onVolver
    ) {
        if (registros.isEmpty()) {
            item {
                Text("Todavía no tienes registros guardados.")
            }
        }

        items(registros.sortedByDescending { it.id }) { registro ->
            val bitacora = bitacoras.firstOrNull {
                it.id == registro.bitacoraId
            }

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
                        idPendienteEliminar = registro.id
                    }
                }
            }
        }
    }

    // El diálogo queda fuera de PantallaBase.
    if (idPendienteEliminar != null) {
        AlertDialog(
            onDismissRequest = {
                idPendienteEliminar = null
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
                        val id = idPendienteEliminar
                        if (id != null) {
                            onEliminar(id)
                        }
                        idPendienteEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        idPendienteEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}