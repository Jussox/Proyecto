package cl.duoc.bitacora2.ui.theme.Docente

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import cl.duoc.bitacora2.data.Asignacion
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.data.fechaHoy
import cl.duoc.bitacora2.ui.theme.comun.*

@Composable
fun DocenteCrearBitacoraScreen(
    asignaciones: List<Asignacion>,
    docente: String,
    onVolver: () -> Unit,
    onGuardar: (Bitacora) -> Unit
) {
    var seleccionada by remember { mutableStateOf<Asignacion?>(null) }
    var mes by remember { mutableStateOf("") }
    var anio by remember { mutableStateOf("") }
    var titulo by remember { mutableStateOf("") }
    var proposito by remember { mutableStateOf("") }
    var instrucciones by remember { mutableStateOf("") }
    var preguntas by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var mensajeEsError by remember { mutableStateOf(false) }

    fun guardar(publicar: Boolean) {
        val a = seleccionada
        if (a == null || mes.isBlank() || anio.isBlank() || titulo.isBlank() ||
            proposito.isBlank() || instrucciones.isBlank() || preguntas.isBlank()
        ) {
            mensaje = "Seleccione curso y asignatura y complete todos los campos"
            mensajeEsError = true
        } else {
            onGuardar(
                Bitacora(
                    docente = docente,
                    curso = a.curso,
                    asignatura = a.asignatura,
                    mes = mes,
                    anio = anio,
                    titulo = titulo,
                    proposito = proposito,
                    instrucciones = instrucciones,
                    preguntas = preguntas,
                    estado = if (publicar) "publicada" else "guardada",
                    fechaPublicacion = if (publicar) fechaHoy() else ""
                )
            )
            seleccionada = null
            mes = ""
            anio = ""
            titulo = ""
            proposito = ""
            instrucciones = ""
            preguntas = ""
            mensaje = if (publicar) "Bitácora guardada y publicada."
            else "Bitácora guardada. Puedes publicarla desde «Mis bitácoras mensuales»."
            mensajeEsError = false
        }
    }

    PantallaBase(titulo = "Crear bitácora", onVolver = onVolver) {
        item { Text("Curso y asignatura", style = MaterialTheme.typography.titleMedium) }
        items(asignaciones) { a ->
            BotonOpcion("${a.curso} · ${a.asignatura}", seleccionada == a) { seleccionada = a }
        }
        item { Campo(mes, "Mes (por ejemplo: Octubre)") { mes = it } }
        item { Campo(anio, "Año (por ejemplo: 2026)") { anio = it } }
        item { Campo(titulo, "Título de la bitácora") { titulo = it } }
        item { Campo(proposito, "Propósito general") { proposito = it } }
        item { Campo(instrucciones, "Instrucciones para los estudiantes") { instrucciones = it } }
        item { Campo(preguntas, "Preguntas orientadoras") { preguntas = it } }

        if (mensaje.isNotEmpty()) {
            item {
                Text(
                    text = mensaje,
                    color = if (mensajeEsError) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
                )
            }
        }

        item { BotonSecundario("Guardar") { guardar(false) } }
        item { BotonAncho("Guardar y publicar bitácora") { guardar(true) } }
        item { BotonSecundario("Volver / cancelar") { onVolver() } }
    }
}