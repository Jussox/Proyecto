package cl.duoc.bitacora2.ui.theme.comun

import androidx.compose.ui.graphics.Color
import cl.duoc.bitacora2.ui.theme.colores.EstadoBorrador
import cl.duoc.bitacora2.ui.theme.colores.EstadoCerrada
import cl.duoc.bitacora2.ui.theme.colores.EstadoGuardada
import cl.duoc.bitacora2.ui.theme.colores.EstadoPendiente
import cl.duoc.bitacora2.ui.theme.colores.EstadoPorDefecto
import cl.duoc.bitacora2.ui.theme.colores.EstadoPublicada
import cl.duoc.bitacora2.ui.theme.colores.EstadoRevisado

// Estados posibles de los aportes (borrador, pendiente, revisado) y de las bitácoras (guardada, publicada, cerrada).
// La base de datos sigue guardando el texto de "valor"; "texto" es lo que ve el usuario y "color" el que se muestra en pantalla.
// Los colores NO se escriben aquí: se toman de la carpeta colores.
enum class Estado(val valor: String, val texto: String, val color: Color) {
    BORRADOR("borrador", "Borrador", EstadoBorrador),
    PENDIENTE("pendiente", "Pendiente de revisión", EstadoPendiente),
    REVISADO("revisado", "Revisado", EstadoRevisado),
    GUARDADA("guardada", "Guardada (sin publicar)", EstadoGuardada),
    PUBLICADA("publicada", "Publicada (activa)", EstadoPublicada),
    CERRADA("cerrada", "Cerrada", EstadoCerrada);

    companion object {
        // Busca el estado a partir del texto guardado; devuelve null si no existe.
        fun desde(valor: String): Estado? = entries.firstOrNull { it.valor == valor }

        // Texto que ve el usuario; si el estado no existe, devuelve el mismo texto recibido.
        fun textoDe(valor: String): String = desde(valor)?.texto ?: valor

        // Color del estado; si no existe, usa el color por defecto.
        fun colorDe(valor: String): Color = desde(valor)?.color ?: EstadoPorDefecto
    }
}
