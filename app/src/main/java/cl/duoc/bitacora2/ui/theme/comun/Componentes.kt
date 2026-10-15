package cl.duoc.bitacora2.ui.theme.comun

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onVolver != null) {
                    IconButton(
                        onClick = { onVolver() }
                    ) {
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
    if (seleccionado) {
        Button(
            onClick = { onClick() },
            modifier = modifier
        ) {
            Text(texto)
        }
    } else {
        OutlinedButton(
            onClick = { onClick() },
            modifier = modifier
        ) {
            Text(texto)
        }
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