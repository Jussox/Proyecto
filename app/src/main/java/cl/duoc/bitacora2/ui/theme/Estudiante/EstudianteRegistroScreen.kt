package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.compose.material3.*
import androidx.compose.runtime.*
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.comun.*

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun EstudianteRegistroScreen(
    bitacora: Bitacora?,
    estudiante: String,
    registroExistente: Registro? = null,
    onVolver: () -> Unit,
    onGuardar: (Registro) -> Unit,
    viewModel: EstudianteRegistroViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(registroExistente?.id) {
        viewModel.inicializar(bitacora?.id ?: 0, estudiante, registroExistente)
    }

    PantallaBase(titulo = "Registro de clase", onVolver = onVolver) {
        if (bitacora == null) {
            item { Text("La bitácora no está disponible.") }
        } else {
            item { Text(bitacora.titulo, style = MaterialTheme.typography.titleMedium) }
            item { Campo(uiState.fecha, "Fecha de la clase (dd/mm/aaaa)") { viewModel.cambiarFecha(it) } }
            item { Campo(uiState.contenido, "Contenido trabajado") { viewModel.cambiarContenido(it) } }
            item { Campo(uiState.actividad, "Actividad realizada") { viewModel.cambiarActividad(it) } }
            item { Campo(uiState.aprendizaje, "Qué aprendió") { viewModel.cambiarAprendizaje(it) } }
            item { Campo(uiState.dudas, "Dudas o dificultades") { viewModel.cambiarDudas(it) } }

            item {
                Text("Adjuntos", style = MaterialTheme.typography.titleMedium)
            }
            item {
                AdjuntosSection(
                    uriFoto = uiState.fotografias,
                    enlace = uiState.enlaces,
                    onFotoCambio = { viewModel.cambiarFotografias(it) },
                    onEnlaceCambio = { viewModel.cambiarEnlaces(it) }
                )
            }

            item { Campo(uiState.archivos, "Archivos adjuntos (nombre del archivo)") { viewModel.cambiarArchivos(it) } }

            item {
                MensajeAnimado(
                    texto = uiState.mensaje,
                    esError = uiState.mensajeEsError
                )
            }

            item {
                BotonSecundario("Guardar borrador") {
                    viewModel.guardarBorrador(onGuardar)
                }
            }
            item {
                BotonAncho("Enviar al docente") {
                    viewModel.enviarAlDocente(onGuardar)
                }
            }
            item { BotonSecundario("Volver / cancelar") { onVolver() } }
        }
    }
}
