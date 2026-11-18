package cl.duoc.bitacora2.ui.theme.colores

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ARCHIVO ÚNICO DE COLORES DE LA APP.
// Cada color se escribe una sola vez aquí; el resto del código usa estos nombres.

// ===== COLORES BASE =====
// Blanco: texto sobre botones y opciones de color fuerte.
val Blanco = Color.White
// Morado principal de la app: opción seleccionada y color de estado por defecto.
val MoradoApp = Color(0xFF6750A4)

// ===== TEMA DE LA APP (los usa Theme.kt) =====
// Modo claro
val Purple40 = Color(0xFF6650a4)      // Color primario: botones y elementos destacados.
val PurpleGrey40 = Color(0xFF625b71)  // Color secundario.
val Pink40 = Color(0xFF7D5260)        // Color terciario (acentos).
// Modo oscuro
val Purple80 = Color(0xFFD0BCFF)      // Color primario: botones y elementos destacados.
val PurpleGrey80 = Color(0xFFCCC2DC)  // Color secundario.
val Pink80 = Color(0xFFEFB8C8)        // Color terciario (acentos).
// Fondos y textos del modo claro
val FondoClaro = Color(0xFFFFFBFE)       // Fondo de las pantallas (background y surface).
val TextoSobreFondo = Color(0xFF1C1B1F)  // Texto sobre el fondo claro.

// ===== BOTONES DE OPCIÓN (BotonOpcion en Componentes.kt) =====
val OpcionSeleccionadaFondo = MoradoApp                      // Fondo de la opción elegida.
val OpcionNoSeleccionadaFondo = Color(0xFFE8DEF8)            // Fondo de las opciones no elegidas.
val OpcionNoSeleccionadaTexto = Color(0xFF4A4458)            // Texto de las opciones no elegidas (la elegida usa Blanco).

// ===== ESTADOS (aportes y bitácoras) =====
val EstadoBorrador = Color(0xFF607D8B)       // Registro del estudiante guardado sin enviar.
val EstadoPendiente = Color(0xFFEF6C00)      // Registro enviado, esperando revisión del docente.
val EstadoRevisado = Color(0xFF2E7D32)       // Registro ya revisado por el docente.
val EstadoGuardada = EstadoBorrador          // Bitácora guardada sin publicar (mismo gris que borrador).
val EstadoPublicada = Color(0xFF00897B)      // Bitácora publicada y activa para los estudiantes.
val EstadoCerrada = Color(0xFFFD0000)        // Bitácora cerrada.
val EstadoPorDefecto = MoradoApp             // Cualquier estado que no sea uno de los anteriores.

// ===== BOTÓN "CERRAR SESIÓN" DEL DOCENTE =====
val CerrarSesionFondo = Color(0xFFAD1515)    // Fondo rojo del botón; su texto usa Blanco.

// ===== MENÚ DEL ESTUDIANTE (tarjetas de acceso, EstudianteScreen.kt) =====
val MenuVerdeIcono = Color(0xFF1E8E3E)       // Ícono de "Bitácoras mensuales publicadas".
val MenuVerdeFondo = Color(0xFFE6F4EA)       // Fondo de "Bitácoras mensuales publicadas".
val MenuNaranjaIcono = Color(0xFFE37400)     // Ícono de "Estado e historial".
val MenuNaranjaFondo = Color(0xFFFEF7E0)     // Fondo de "Estado e historial".
val MenuMoradoIcono = Color(0xFF9333EA)      // Ícono de "Retroalimentación del docente".
val MenuMoradoFondo = Color(0xFFF3E8FF)      // Fondo de "Retroalimentación del docente".

// Fondo del botón de opción según esté seleccionado o no.
fun colorFondoOpcion(seleccionado: Boolean): Color =
    if (seleccionado) OpcionSeleccionadaFondo else OpcionNoSeleccionadaFondo

// Color del texto del botón de opción según esté seleccionado o no.
fun colorTextoOpcion(seleccionado: Boolean): Color =
    if (seleccionado) Blanco else OpcionNoSeleccionadaTexto

// Color de un mensaje de aviso: rojo si es error, color primario si no lo es.
@Composable
fun colorMensaje(esError: Boolean): Color =
    if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
