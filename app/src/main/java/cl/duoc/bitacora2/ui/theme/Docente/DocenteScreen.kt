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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Bitacora
import cl.duoc.bitacora2.ui.theme.comun.BotonAncho
import cl.duoc.bitacora2.ui.theme.comun.BotonSecundario
import cl.duoc.bitacora2.ui.theme.comun.Dato
import cl.duoc.bitacora2.ui.theme.comun.PantallaBase
import cl.duoc.bitacora2.ui.theme.comun.nombreEstado


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row

import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

//para el boton de cerrar sesion
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.Color

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
    var administracionExpandida by remember {
        mutableStateOf(false)
    }

    var revisionExpandida by remember {
        mutableStateOf(false)
    }

    var seguimientoExpandido by remember {
        mutableStateOf(false)
    }

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
                            administracionExpandida =
                                !administracionExpandida
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
                            text = if (administracionExpandida) "▲" else "▼",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                // Las opciones aparecen y desaparecen suavemente.
                AnimatedVisibility(
                    visible = administracionExpandida,
                    enter = fadeIn(
                        animationSpec = tween(250)
                    ) + expandVertically(
                        animationSpec = tween(300)
                    ),
                    exit = fadeOut(
                        animationSpec = tween(200)
                    ) + shrinkVertically(
                        animationSpec = tween(250)
                    )
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
                            revisionExpandida = !revisionExpandida
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
                            text = if (revisionExpandida) "▲" else "▼",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                AnimatedVisibility(
                    visible = revisionExpandida,
                    enter = fadeIn(animationSpec = tween(250)) +
                            expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(200)) +
                            shrinkVertically(animationSpec = tween(250))
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
                            seguimientoExpandido = !seguimientoExpandido
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
                            text = if (seguimientoExpandido) "▲" else "▼",
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                AnimatedVisibility(
                    visible = seguimientoExpandido,
                    enter = fadeIn(animationSpec = tween(250)) +
                            expandVertically(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(200)) +
                            shrinkVertically(animationSpec = tween(250))
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
                    containerColor = Color(0xFFAD1515),
                    contentColor = Color.White
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