package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.compose.material3.*
import androidx.compose.runtime.*
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.data.fechaHoy
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.comun.*

@Composable
fun EstudianteRegistroScreen(
    bitacora: Bitacora?,
    estudiante: String,
    onVolver: () -> Unit,
    onGuardar: (Registro) -> Unit
) {
    var fecha by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var actividad by remember { mutableStateOf("") }
    var aprendizaje by remember { mutableStateOf("") }
    var dudas by remember { mutableStateOf("") }
    var fotografias by remember { mutableStateOf("") }
    var archivos by remember { mutableStateOf("") }
    var enlaces by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var mensajeEsError by remember { mutableStateOf(false) }

    fun construir(estado: String): Registro {
        return Registro(
            bitacoraId = bitacora?.id ?: 0,
            estudiante = estudiante,
            fechaClase = fecha,
            contenido = contenido,
            actividad = actividad,
            aprendizaje = aprendizaje,
            dudas = dudas,
            fotografias = fotografias,
            archivos = archivos,
            enlaces = enlaces,
            estado = estado,
            fechaCreacion = fechaHoy(),
            fechaEnvio = if (estado == "pendiente") fechaHoy() else ""
        )
    }

    fun limpiar() {
        fecha = ""
        contenido = ""
        actividad = ""
        aprendizaje = ""
        dudas = ""
        fotografias = ""
        archivos = ""
        enlaces = ""
    }

    PantallaBase(titulo = "Registro de clase", onVolver = onVolver) {
        if (bitacora == null) {
            item { Text("La bitácora no está disponible.") }
        } else {
            item { Text(bitacora.titulo, style = MaterialTheme.typography.titleMedium) }
            item { Campo(fecha, "Fecha de la clase (dd/mm/aaaa)") { fecha = it } }
            item { Campo(contenido, "Contenido trabajado") { contenido = it } }
            item { Campo(actividad, "Actividad realizada") { actividad = it } }
            item { Campo(aprendizaje, "Qué aprendió") { aprendizaje = it } }
            item { Campo(dudas, "Dudas o dificultades") { dudas = it } }
            item { Campo(fotografias, "Fotografías (nombre o descripción de la evidencia)") { fotografias = it } }
            item { Campo(archivos, "Archivos adjuntos (nombre del archivo)") { archivos = it } }
            item { Campo(enlaces, "Enlaces relacionados") { enlaces = it } }

            if (mensaje.isNotEmpty()) {
                item {
                    Text(
                        text = mensaje,
                        color = if (mensajeEsError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }

            item {
                BotonSecundario("Guardar borrador") {
                    if (fecha.isBlank()) {
                        mensaje = "Indique la fecha de la clase para guardar el borrador"
                        mensajeEsError = true
                    } else {
                        onGuardar(construir("borrador"))
                        limpiar()
                        mensaje = "Borrador guardado. Puedes enviarlo desde tu historial."
                        mensajeEsError = false
                    }
                }
            }
            item {
                BotonAncho("Enviar al docente") {
                    if (fecha.isBlank() || contenido.isBlank() || actividad.isBlank() || aprendizaje.isBlank()) {
                        mensaje = "Complete fecha, contenido, actividad y qué aprendió para enviar"
                        mensajeEsError = true
                    } else {
                        onGuardar(construir("pendiente"))
                        limpiar()
                        mensaje = "Registro enviado. Estado: pendiente de revisión."
                        mensajeEsError = false
                    }
                }
            }
            item { BotonSecundario("Volver / cancelar") { onVolver() } }
        }
    }
}