package cl.duoc.bitacora2.ui.theme.comun

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.data.Asignacion

import androidx.compose.ui.Alignment



@Composable
fun PantallaBase(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    contenido: LazyListScope.() -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onVolver != null) {
                    IconButton(onClick = { onVolver() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }

                Text(
                    text = titulo,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = contenido
        )
    }
}


@Composable
fun BotonAncho(texto: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto)
    }
}

@Composable
fun BotonSecundario(texto: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto)
    }
}

@Composable
fun BotonOpcion(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit
) {
    val colorFondo by animateColorAsState(
        targetValue = if (seleccionado) Color(0xFF6750A4) else Color(0xFFE8DEF8),
        animationSpec = tween(durationMillis = 400),
        label = "Color de fondo de la opción"
    )
    val colorTexto by animateColorAsState(
        targetValue = if (seleccionado) Color.White else Color(0xFF4A4458),
        animationSpec = tween(durationMillis = 400),
        label = "Color del texto de la opción"
    )

    Button(
        onClick = { onClick() },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorFondo,
            contentColor = colorTexto
        )
    ) {
        Text(texto)
    }
}

@Composable
fun Campo(valor: String, etiqueta: String, onCambio: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = {
            onCambio(it)
        },
        label = {
            Text(etiqueta)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun Dato(etiqueta: String, valor: String) {
    Text("$etiqueta: ${if (valor.isBlank()) "—" else valor}")
}

fun nombreEstado(estado: String): String {
    if (estado == "borrador") return "Borrador"
    if (estado == "pendiente") return "Pendiente de revisión"
    if (estado == "revisado") return "Revisado"
    if (estado == "guardada") return "Guardada (sin publicar)"
    if (estado == "publicada") return "Publicada (activa)"
    if (estado == "cerrada") return "Cerrada"
    return estado
}

// Un color distinto para cada estado (de aportes y de bitácoras)
fun colorEstado(estado: String): Color {
    if (estado == "borrador") return Color(0xFF607D8B)
    if (estado == "pendiente") return Color(0xFFEF6C00)
    if (estado == "revisado") return Color(0xFF2E7D32)
    if (estado == "guardada") return Color(0xFF607D8B)
    if (estado == "publicada") return Color(0xFF00897B)
    if (estado == "cerrada") return Color(0xFFFD0000)
    return Color(0xFF6750A4)
}


@Composable
fun EstadoTexto(estado: String) {
    val color by animateColorAsState(
        targetValue = colorEstado(estado),
        animationSpec = tween(durationMillis = 600),
        label = "Color del estado"
    )

    Crossfade(
        targetState = estado,
        label = "Texto del estado"
    ) { estadoActual ->
        Text(
            text = "Estado: ${nombreEstado(estadoActual)}",
            color = color
        )
    }
}

// Un mensaje que aparece y desaparece con animación.
// Si el texto está vacío, no se ve nada.
@Composable
fun MensajeAnimado(texto: String, esError: Boolean) {
    AnimatedVisibility(
        visible = texto.isNotEmpty(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Text(
            text = texto,
            color = if (esError) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.primary
        )
    }
}

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