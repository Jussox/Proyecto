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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Recordatorio
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonOpcion
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Campo
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase

@Composable
fun DocenteRecordatoriosScreen(
    recordatorios: List<Recordatorio>,
    onVolver: () -> Unit,
    onCrear: (String, String) -> Unit,
    onCompletar: (Int) -> Unit
) {
    var tipo by remember { mutableStateOf("Cierre mensual") }
    var fecha by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    val tipos = listOf("Cierre mensual", "Creación de la siguiente bitácora", "Inicio de semestre")

    PantallaBase(titulo = "Recordatorios internos", onVolver = onVolver) {
        item { Text("Configurar recordatorio", style = MaterialTheme.typography.titleMedium) }
        items(tipos) { t ->
            BotonOpcion(t, tipo == t) { tipo = t }
        }
        item { Campo(fecha, "Fecha programada (dd/mm/aaaa)") { fecha = it } }
        item {
            BotonAncho("Configurar recordatorio") {
                if (fecha.isBlank()) {
                    mensaje = "Indique la fecha programada"
                } else {
                    onCrear(tipo, fecha)
                    fecha = ""
                    mensaje = "Recordatorio configurado"
                }
            }
        }
        if (mensaje.isNotEmpty()) {
            item { Text(mensaje, color = MaterialTheme.colorScheme.primary) }
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
                        BotonSecundario("Marcar como completado") { onCompletar(r.id) }
                    }
                }
            }
        }
        item { Text("Los recordatorios se muestran solo dentro de la aplicación; no envían notificaciones.") }
    }
}