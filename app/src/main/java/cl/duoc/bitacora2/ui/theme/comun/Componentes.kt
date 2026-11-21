package cl.duoc.bitacora2.ui.theme.comun

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.ui.theme.colores.FondoPantalla

// Estructura común de casi todas las pantallas: barra superior con flecha "Volver" y título, y debajo una lista desplazable con el contenido.
@Composable
fun PantallaBase(
    titulo: String,
    onVolver: (() -> Unit)? = null,
    contenido: LazyListScope.() -> Unit
) {
    Scaffold(
        containerColor = FondoPantalla,
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
fun BotonAncho(texto: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto)
    }
}

// Botón con solo borde y ancho completo, para acciones menos importantes.
@Composable
fun BotonSecundario(texto: String, enabled: Boolean = true, onClick: () -> Unit) {
    OutlinedButton(
        onClick = { onClick() },
        enabled = enabled,
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
    val (colorFondo, colorTexto) = animarColoresBotonOpcion(seleccionado)

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
