package cl.duoc.bitacora2.ui.theme.Docente

import android.R
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.ui.theme.comun.animacionEntradaExpandir
import cl.duoc.bitacora2.ui.theme.comun.animacionSalidaContraer
import cl.duoc.bitacora2.ui.theme.comun.Estado


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row

import androidx.compose.material3.CardDefaults

//para el boton de cerrar sesion
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import cl.duoc.bitacora2.ui.theme.colores.Blanco
import cl.duoc.bitacora2.ui.theme.colores.CerrarSesionFondo

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

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
    onCerrarSesion: () -> Unit,
    viewModel: DocenteViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PantallaBase(titulo = "Bitácora") {

        item {
            Text(
                text = "Perfil: Docente",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            Text("Docente: $nombre")
        }

        item {
            Text(
                text = "Recordatorios pendientes: $recordatoriosPendientes",
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Encabezado desplegable de administración.
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.toggleAdministracion()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement =
                            Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Administración de bitácoras",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Text(
                            text = if (uiState.administracionExpandida) "▲" else "▼",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Las opciones aparecen y desaparecen suavemente.
                AnimatedVisibility(
                    visible = uiState.administracionExpandida,
                    enter = animacionEntradaExpandir,
                    exit = animacionSalidaContraer
                ) {
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {
                        BotonAncho("Mis cursos y asignaturas") {
                            onCursos()
                        }

                        BotonAncho("Crear bitácora") {
                            onCrear()
                        }

                        BotonAncho("Mis bitácoras mensuales") {
                            onBitacoras()
                        }

                        BotonAncho("Historial de bitácoras") {
                            onHistorial()
                        }
                    }
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.toggleRevision()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Revisión de aportes",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Text(
                            text = if (uiState.revisionExpandida) "▲" else "▼",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                AnimatedVisibility(
                    visible = uiState.revisionExpandida,
                    enter = animacionEntradaExpandir,
                    exit = animacionSalidaContraer
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BotonAncho("Aportes enviados por los estudiantes") {
                            onAportes()
                        }

                        BotonAncho("Aportes pendientes: $aportesPendientes") {
                            onPendientes()
                        }
                    }
                }
            }
        }

        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.toggleSeguimiento()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Seguimiento",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Text(
                            text = if (uiState.seguimientoExpandido) "▲" else "▼",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                AnimatedVisibility(
                    visible = uiState.seguimientoExpandido,
                    enter = animacionEntradaExpandir,
                    exit = animacionSalidaContraer
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BotonAncho("Seguimiento de participación") {
                            onParticipacion()
                        }

                        BotonAncho("Indicadores generales") {
                            onIndicadores()
                        }

                        BotonAncho("Informe mensual") {
                            onInforme()
                        }

                        BotonAncho("Recordatorios internos") {
                            onRecordatorios()
                        }
                    }
                }
            }
        }

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
                    containerColor = CerrarSesionFondo,
                    contentColor = Blanco
                )
            ) {
                Text("Cerrar sesión")
            }
        }
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
                    Text("Estado: ${Estado.textoDe(b.estado)}", color = MaterialTheme.colorScheme.primary)
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
