package cl.duoc.bitacora2.ui.theme.comun

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.ui.theme.colores.colorFondoOpcion
import cl.duoc.bitacora2.ui.theme.colores.colorMensaje
import cl.duoc.bitacora2.ui.theme.colores.colorTextoOpcion
import cl.duoc.bitacora2.data.Asignacion

import androidx.compose.ui.Alignment



// Estructura común de casi todas las pantallas: barra superior con flecha "Volver" y título, y debajo una lista desplazable con el contenido.
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


// Botón principal de ancho completo; es el botón de acción normal de la app.
@Composable
fun BotonAncho(texto: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto)
    }
}

// Botón con solo borde y ancho completo, para acciones menos importantes.
@Composable
fun BotonSecundario(texto: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto)
    }
}

// Botón para elegir una opción (por ejemplo perfil o filtro); el elegido cambia de color con animación.
@Composable
fun BotonOpcion(
    texto: String,
    seleccionado: Boolean,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onClick: () -> Unit
) {
    val colorFondo by animateColorAsState(
        targetValue = colorFondoOpcion(seleccionado),
        animationSpec = tween(durationMillis = 400),
        label = "Color de fondo de la opción"
    )
    val colorTexto by animateColorAsState(
        targetValue = colorTextoOpcion(seleccionado),
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

// Campo de texto con etiqueta para escribir datos en los formularios.
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

// Muestra una línea "Etiqueta: valor"; si el valor está vacío muestra "—".
@Composable
fun Dato(etiqueta: String, valor: String) {
    Text("$etiqueta: ${if (valor.isBlank()) "—" else valor}")
}

// Muestra "Estado: ..." con el color del estado; al cambiar de estado, el texto y el color cambian con animación.
@Composable
fun EstadoTexto(estado: String) {
    val color by animateColorAsState(
        targetValue = Estado.colorDe(estado),
        animationSpec = tween(durationMillis = 600),
        label = "Color del estado"
    )

    Crossfade(
        targetState = estado,
        animationSpec = tween(DURACION_CROSSFADE),
        label = "Texto del estado"
    ) { estadoActual ->
        Text(
            text = "Estado: ${Estado.textoDe(estadoActual)}",
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
        enter = animacionEntradaExpandir,
        exit = animacionSalidaContraer
    ) {
        Text(
            text = texto,
            color = colorMensaje(esError)
        )
    }
}
