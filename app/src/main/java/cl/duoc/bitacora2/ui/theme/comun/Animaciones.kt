package cl.duoc.bitacora2.ui.theme.comun

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cl.duoc.bitacora2.ui.theme.colores.colorFondoOpcion
import cl.duoc.bitacora2.ui.theme.colores.colorMensaje
import cl.duoc.bitacora2.ui.theme.colores.colorTextoOpcion

// ============================================================================
// 1. CONSTANTES Y ANIMACIONES EN COMÚN (Compartidas entre perfiles y Navegación)
// ============================================================================

// Constantes de tiempo (en milisegundos) para uniformar la velocidad de animación en toda la app
const val DURACION_FADE_IN = 250
const val DURACION_EXPAND = 300
const val DURACION_FADE_OUT = 200
const val DURACION_SHRINK = 250
const val DURACION_CROSSFADE = 300
const val DURACION_COLOR_OPCION = 400

// Común: Efecto base de aparición que combina desvanecimiento y expansión vertical
val animacionEntradaExpandir: EnterTransition =
    fadeIn(animationSpec = tween(DURACION_FADE_IN)) +
            expandVertically(animationSpec = tween(DURACION_EXPAND))

// Común: Efecto base de desaparición que combina desvanecimiento y contracción vertical
val animacionSalidaContraer: ExitTransition =
    fadeOut(animationSpec = tween(DURACION_FADE_OUT)) +
            shrinkVertically(animationSpec = tween(DURACION_SHRINK))

// Pantalla: Navegacion (NavHost)
// Anima la entrada de una nueva pantalla deslizándola desde la derecha con desvanecimiento
fun transicionEntradaPantalla(duracion: Int = 300): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { it / 3 },
        animationSpec = tween(duracion)
    ) + fadeIn(
        animationSpec = tween(duracion)
    )

// Pantalla: Navegacion (NavHost)
// Anima la salida de la pantalla actual deslizándola hacia la izquierda al avanzar
fun transicionSalidaPantalla(duracion: Int = 250): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { -it / 3 },
        animationSpec = tween(duracion)
    ) + fadeOut(
        animationSpec = tween(duracion)
    )

// Pantalla: Navegacion (NavHost)
// Anima el regreso a la pantalla anterior deslizándola desde la izquierda al presionar volver
fun transicionPopEntradaPantalla(duracion: Int = 300): EnterTransition =
    slideInHorizontally(
        initialOffsetX = { -it / 3 },
        animationSpec = tween(duracion)
    ) + fadeIn(
        animationSpec = tween(duracion)
    )

// Pantalla: Navegacion (NavHost)
// Anima la salida de la pantalla que se cierra deslizándola hacia la derecha al volver atrás
fun transicionPopSalidaPantalla(duracion: Int = 250): ExitTransition =
    slideOutHorizontally(
        targetOffsetX = { it / 3 },
        animationSpec = tween(duracion)
    ) + fadeOut(
        animationSpec = tween(duracion)
    )

// Común: Contenedor genérico reutilizable que expande o contrae cualquier bloque con animación suave
@Composable
fun ContenidoAnimadoExpandible(
    visible: Boolean,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = animacionEntradaExpandir,
        exit = animacionSalidaContraer,
        modifier = modifier
    ) {
        contenido()
    }
}

// Común: Formularios y pantallas de la app
// Muestra u oculta suavemente un mensaje de texto informativo o de error
@Composable
fun MensajeAnimado(texto: String, esError: Boolean) {
    ContenidoAnimadoExpandible(visible = texto.isNotEmpty()) {
        Text(
            text = texto,
            color = colorMensaje(esError)
        )
    }
}

// Pantalla: LoginScreen
// Muestra una tarjeta roja destacada con ícono y texto que aparece o desaparece si hay un error
@Composable
fun BannerErrorAnimado(
    mensaje: String,
    modifier: Modifier = Modifier
) {
    ContenidoAnimadoExpandible(
        visible = mensaje.isNotEmpty(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = mensaje,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

// Común: BotonOpcion en Componentes.kt, pantallas de filtro y selección de perfil
// Anima fluidamente la transición de color de fondo y de texto al seleccionar o deseleccionar una opción
@Composable
fun animarColoresBotonOpcion(
    seleccionado: Boolean,
    duracionMillis: Int = DURACION_COLOR_OPCION
): Pair<Color, Color> {
    val colorFondo by animateColorAsState(
        targetValue = colorFondoOpcion(seleccionado),
        animationSpec = tween(durationMillis = duracionMillis),
        label = "Color de fondo de la opción"
    )
    val colorTexto by animateColorAsState(
        targetValue = colorTextoOpcion(seleccionado),
        animationSpec = tween(durationMillis = duracionMillis),
        label = "Color del texto de la opción"
    )
    return Pair(colorFondo, colorTexto)
}

// Común: Pantallas de listados y seguimiento
// Cambia suavemente el color y el texto del estado ("Revisado", "Pendiente", etc.) usando Crossfade y animación de color
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


// ============================================================================
// 2. ANIMACIONES DE ESTUDIANTE
// ============================================================================

// Pantalla: EstudianteRegistroScreen / AdjuntosSection
// Muestra u oculta con animación suave la fotografía adjunta seleccionada y su botón de eliminación
@Composable
fun AnimacionFotoAdjunta(
    visible: Boolean,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    ContenidoAnimadoExpandible(
        visible = visible,
        modifier = modifier,
        contenido = contenido
    )
}


// ============================================================================
// 3. ANIMACIONES DE DOCENTE
// ============================================================================

// Pantalla: DocenteScreen
// Despliega o contrae suavemente el submenú de opciones de cada sección tipo acordeón del menú docente
@Composable
fun AnimacionAcordeonDocente(
    expandido: Boolean,
    modifier: Modifier = Modifier,
    contenido: @Composable () -> Unit
) {
    ContenidoAnimadoExpandible(
        visible = expandido,
        modifier = modifier,
        contenido = contenido
    )
}
