package cl.duoc.bitacora2.ui.theme.colores

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ARCHIVO ÚNICO DE COLORES DE LA APP.
// Cada color se escribe una sola vez aquí; el resto del código usa estos nombres.

// ============================================================================
// COLOR DE FONDO DE LAS PANTALLAS (Fondo global de toda la app)
// ============================================================================
// Pantalla: Todas las pantallas de la aplicación (Theme.kt, PantallaBase y LoginScreen)
// Cambia este color para modificar el fondo de todas las pantallas de la aplicación.
val FondoPantalla = Color(0xFFFFFBFE)
val FondoClaro = FondoPantalla // Alias para compatibilidad

// ============================================================================
// 1. CONSTANTES Y COLORES EN COMÚN (Compartidos y Tema Base)
// ============================================================================

// Común: Color blanco para texto sobre botones oscuros y fondos destacados
val Blanco = Color.White

// Común: Morado principal corporativo de la aplicación
val MoradoApp = Color(0xFF6750A4)

// --- TEMA BASE DE LA APLICACIÓN (utilizados por Theme.kt para Material 3) ---
// Modo claro
val Purple40 = Color(0xFF6650a4)      // Color primario: botones y elementos destacados
val PurpleGrey40 = Color(0xFF625b71)  // Color secundario
val Pink40 = Color(0xFF7D5260)        // Color terciario para acentos
// Modo oscuro
val Purple80 = Color(0xFFD0BCFF)      // Color primario en modo oscuro
val PurpleGrey80 = Color(0xFFCCC2DC)  // Color secundario en modo oscuro
val Pink80 = Color(0xFFEFB8C8)        // Color terciario en modo oscuro
// Textos
val TextoSobreFondo = Color(0xFF1C1B1F)  // Color de texto sobre el fondo claro

// --- BOTONES DE OPCIÓN (Pantallas: LoginScreen, filtros de aportes y recordatorios) ---
// Pantalla: Componentes.kt (BotonOpcion)
val OpcionSeleccionadaFondo = MoradoApp                      // Fondo de la opción elegida
val OpcionNoSeleccionadaFondo = Color(0xFFE8DEF8)            // Fondo de las opciones no elegidas
val OpcionNoSeleccionadaTexto = Color(0xFF4A4458)            // Texto de las opciones no elegidas

// Devuelve el color de fondo del botón de opción según si está seleccionado o no
fun colorFondoOpcion(seleccionado: Boolean): Color =
    if (seleccionado) OpcionSeleccionadaFondo else OpcionNoSeleccionadaFondo

// Devuelve el color del texto del botón de opción según si está seleccionado o no
fun colorTextoOpcion(seleccionado: Boolean): Color =
    if (seleccionado) Blanco else OpcionNoSeleccionadaTexto

// --- ESTADOS COMPARTIDOS DE BITÁCORAS Y REGISTROS ---
// Usados tanto por docentes como por estudiantes en listados, tarjetas y detalles
val EstadoBorrador = Color(0xFF607D8B)       // Estado borrador (gris azulado)
val EstadoPendiente = Color(0xFFEF6C00)      // Estado pendiente de revisión (naranja)
val EstadoRevisado = Color(0xFF2E7D32)       // Estado revisado / aprobado (verde)
val EstadoGuardada = EstadoBorrador          // Bitácora guardada sin publicar (mismo gris)
val EstadoPublicada = Color(0xFF00897B)      // Bitácora publicada y activa (verde azulado)
val EstadoCerrada = Color(0xFFFD0000)        // Bitácora finalizada / cerrada (rojo)
val EstadoPorDefecto = MoradoApp             // Color de respaldo para cualquier estado no listado

// Pantallas: DocenteBitacorasScreen y EstudianteBitacorasScreen
// Devuelve el color representativo según el estado de una bitácora mensual
fun colorEstadoBitacora(estado: String?): Color = when (estado) {
    "publicada" -> EstadoPublicada
    "cerrada" -> EstadoCerrada
    "guardada", "borrador" -> EstadoBorrador
    else -> EstadoBorrador
}

// Común: Formularios y pantallas de la app
// Devuelve color rojo para advertencias de error o color primario para mensajes estándar
@Composable
fun colorMensaje(esError: Boolean): Color =
    if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

// Común: Insignias e indicadores en listas de ambos perfiles
// Genera un fondo suave y translúcido a partir del color base indicado
fun colorFondoInsignia(colorBase: Color, alpha: Float = 0.12f): Color =
    colorBase.copy(alpha = alpha)


// ============================================================================
// 2. COLORES DE ESTUDIANTE
// ============================================================================

// Pantallas: EstudianteScreen (Tarjetas del menú) y EstudianteRetroalimentacionScreen (Badge Revisado)
val MenuVerdeIcono = Color(0xFF1E8E3E)       // Verde oscuro: ícono de "Bitácoras mensuales publicadas" y badge "Revisado"
val MenuVerdeFondo = Color(0xFFE6F4EA)       // Verde claro: fondo de "Bitácoras mensuales publicadas" y badge "Revisado"

// Pantalla: EstudianteScreen (Tarjeta del menú principal)
val MenuNaranjaIcono = Color(0xFFE37400)     // Naranja oscuro: ícono de "Estado e historial"
val MenuNaranjaFondo = Color(0xFFFEF7E0)     // Amarillo claro: fondo de "Estado e historial"

// Pantalla: EstudianteScreen (Tarjeta del menú principal)
val MenuMoradoIcono = Color(0xFF9333EA)      // Morado vivo: ícono de "Retroalimentación del docente"
val MenuMoradoFondo = Color(0xFFF3E8FF)      // Morado claro: fondo de "Retroalimentación del docente"

// Pantalla: EstudianteHistorialScreen y DocenteAportesScreen
// Devuelve el color del aporte del estudiante (Revisado, Pendiente de revisión o Borrador)
fun colorEstadoRegistro(estado: String?): Color = when (estado) {
    "revisado" -> EstadoRevisado
    "pendiente" -> EstadoPendiente
    "borrador" -> EstadoBorrador
    else -> EstadoBorrador
}


// ============================================================================
// 3. COLORES DE DOCENTE
// ============================================================================

// Pantallas: DocenteScreen, DocenteParticipacionScreen y DocenteEstudiantesScreen (Tarjetas, acordeones y avatares de estudiantes)
val ParticipacionFondo = Color(0xFFEAF3FC)   // Azul suave: fondo de tarjeta de seguimiento de participación y avatar de estudiante
val ParticipacionIcono = Color(0xFF3976B9)   // Azul medio: ícono de seguimiento de participación y avatar de estudiante
val ParticipacionTarjeta = Color(0xFFF3F7FC) // Azul tenue: fondo de tarjeta destacada cuando el acordeón está expandido

// Pantalla: DocenteScreen
val CerrarSesionFondo = Color(0xFFAD1515)    // Rojo fuerte: fondo del botón "Cerrar sesión" del docente

// Pantalla: DocenteParticipacionScreen
// Devuelve verde si el estudiante entregó su aporte o naranja si está pendiente de entrega
fun colorParticipacion(participo: Boolean): Color =
    if (participo) EstadoRevisado else EstadoPendiente

// Pantalla: DocenteParticipacionScreen
// Devuelve el color de fondo suave (verde claro o naranja claro) para el badge de participación
fun colorFondoParticipacion(participo: Boolean): Color =
    if (participo) MenuVerdeFondo else MenuNaranjaFondo
