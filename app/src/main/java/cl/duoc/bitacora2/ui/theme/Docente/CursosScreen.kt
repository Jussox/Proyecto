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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Asignacion
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase


@Composable
fun CursosScreen(
    titulo: String,
    asignaciones: List<Asignacion>,
    textoBoton: String,
    onVolver: () -> Unit,
    onElegir: (Asignacion) -> Unit
) {
    PantallaBase(titulo = titulo, onVolver = onVolver) {
        items(asignaciones) { a ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(a.curso, style = MaterialTheme.typography.titleMedium)
                    Text("Asignatura: ${a.asignatura}")
                    BotonAncho(textoBoton) { onElegir(a) }
                }
            }
        }
    }
}