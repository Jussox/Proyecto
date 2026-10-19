package cl.duoc.bitacora2.ui.theme.Estudiante

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cl.duoc.bitacora2.data.Registro
import cl.duoc.bitacora2.data.fechaHoy
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.comun.*

import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EstudianteRegistroScreen(
    bitacora: Bitacora?,
    estudiante: String,
    registroExistente: Registro? = null,
    onVolver: () -> Unit,
    onGuardar: (Registro) -> Unit
) {
    var fecha by remember(registroExistente?.id) {
        mutableStateOf(registroExistente?.fechaClase ?: "")
    }
    var contenido by remember(registroExistente?.id) {
        mutableStateOf(registroExistente?.contenido ?: "")
    }
    var actividad by remember(registroExistente?.id) {
        mutableStateOf(registroExistente?.actividad ?: "")
    }
    var aprendizaje by remember(registroExistente?.id) {
        mutableStateOf(registroExistente?.aprendizaje ?: "")
    }
    var dudas by remember(registroExistente?.id) {
        mutableStateOf(registroExistente?.dudas ?: "")
    }
    var fotografias by rememberSaveable(registroExistente?.id) {
        mutableStateOf(registroExistente?.fotografias ?: "")
    }
    var archivos by remember(registroExistente?.id) {
        mutableStateOf(registroExistente?.archivos ?: "")
    }
    var enlaces by remember(registroExistente?.id) {
        mutableStateOf(registroExistente?.enlaces ?: "")
    }
    var mensaje by remember {
        mutableStateOf("")
    }
    var mensajeEsError by remember {
        mutableStateOf(false)
    }



    fun fechaValida(fecha: String): Boolean {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formato.isLenient = false
            formato.parse(fecha) != null
        } catch (e: Exception) {
            false
        }
    }

    fun construir(estado: String): Registro {
        return Registro(
            id = registroExistente?.id ?: 0,
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
            comentario = registroExistente?.comentario ?: "",
            fechaCreacion = registroExistente?.fechaCreacion ?: fechaHoy(),
            fechaEnvio = if (estado == "pendiente") {
                fechaHoy()
            } else {
                registroExistente?.fechaEnvio ?: ""
            },
            fechaRetroalimentacion =
                registroExistente?.fechaRetroalimentacion ?: "",
            docenteRevisor = registroExistente?.docenteRevisor ?: ""
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

            item {
                Text("Adjuntos", style = MaterialTheme.typography.titleMedium)
            }
            item {
                AdjuntosSection(
                    uriFoto = fotografias,
                    enlace = enlaces,
                    onFotoCambio = { fotografias = it },
                    onEnlaceCambio = { enlaces = it }
                )
            }

            item { Campo(archivos, "Archivos adjuntos (nombre del archivo)") { archivos = it } }

            if (mensaje.isNotEmpty()) {
                item {
                    Text(
                        text = mensaje,
                        color = if (mensajeEsError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }

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
                    //respeta formato de la fecha
                    if (!fechaValida(fecha)) {
                        mensaje = "Ingrese una fecha válida con formato dd/MM/aaaa (ejemplo: 16/10/2026)"
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
                    if (!fechaValida(fecha) ||
                        contenido.isBlank() ||
                        actividad.isBlank() ||
                        aprendizaje.isBlank()
                    ) {
                        mensaje = "Ingrese una fecha válida (dd/MM/aaaa) y complete contenido, actividad y aprendizaje"
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