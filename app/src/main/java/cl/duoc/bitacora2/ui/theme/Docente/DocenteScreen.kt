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
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.ui.theme.comun.nombreEstado

@Composable
fun DocenteScreen(
    nombre: String,
    aportesPendientes: Int,
    recordatoriosPendientes: Int,
    onCursos: () -> Unit,
    onCrear: () -> Unit,
    onBitacoras: () -> Unit,
    onAportes: () -> Unit,
    onPendientes: () -> Unit,
    onParticipacion: () -> Unit,
    onIndicadores: () -> Unit,
    onHistorial: () -> Unit,
    onInforme: () -> Unit,
    onRecordatorios: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    PantallaBase(titulo = "Bitácora") {
        item { Text("Perfil: Docente", style = MaterialTheme.typography.headlineSmall) }
        item { Text("Docente: $nombre") }
        item { Text("Aportes pendientes de revisión: $aportesPendientes", color = MaterialTheme.colorScheme.primary) }
        item { Text("Recordatorios pendientes: $recordatoriosPendientes", color = MaterialTheme.colorScheme.primary) }
        item { Text("Administración de bitácoras", style = MaterialTheme.typography.titleMedium) }
        item { BotonAncho("Mis cursos y asignaturas") { onCursos() } }
        item { BotonAncho("Crear bitácora") { onCrear() } }
        item { BotonAncho("Mis bitácoras mensuales") { onBitacoras() } }
        item { BotonAncho("Historial de bitácoras") { onHistorial() } }
        item { Text("Revisión de aportes", style = MaterialTheme.typography.titleMedium) }
        item { BotonAncho("Aportes enviados por los estudiantes") { onAportes() } }
        item { BotonAncho("Aportes pendientes de revisión") { onPendientes() } }
        item { Text("Seguimiento", style = MaterialTheme.typography.titleMedium) }
        item { BotonAncho("Seguimiento de participación") { onParticipacion() } }
        item { BotonAncho("Indicadores generales") { onIndicadores() } }
        item { BotonAncho("Informe mensual") { onInforme() } }
        item { BotonAncho("Recordatorios internos") { onRecordatorios() } }
        item { BotonSecundario("Cerrar sesión") { onCerrarSesion() } }
    }
}

@Composable
fun DocenteBitacorasScreen(
    titulo: String,
    bitacoras: List<Bitacora>,
    onVolver: () -> Unit,
    onPublicar: (Int) -> Unit,
    onCerrar: (Int) -> Unit,
    onVerAportes: (Int) -> Unit
) {
    PantallaBase(titulo = titulo, onVolver = onVolver) {
        if (bitacoras.isEmpty()) {
            item { Text("No hay bitácoras para mostrar.") }
        }
        items(bitacoras) { b ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(b.titulo, style = MaterialTheme.typography.titleMedium)
                    Text("${b.curso} · ${b.asignatura}")
                    Text("${b.mes} ${b.anio}")
                    Text("Estado: ${nombreEstado(b.estado)}", color = MaterialTheme.colorScheme.primary)
                    Dato("Fecha de publicación", b.fechaPublicacion)
                    Dato("Fecha de cierre", b.fechaCierre)
                    if (b.estado == "guardada") {
                        BotonAncho("Publicar bitácora") { onPublicar(b.id) }
                    }
                    if (b.estado == "publicada") {
                        BotonAncho("Cerrar bitácora") { onCerrar(b.id) }
                    }
                    if (b.estado != "guardada") {
                        BotonSecundario("Ver aportes") { onVerAportes(b.id) }
                    }
                }
            }
        }
    }
}