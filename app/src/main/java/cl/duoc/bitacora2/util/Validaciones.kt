package cl.duoc.bitacora2.util

import cl.duoc.bitacora2.data.Asignacion
import cl.duoc.bitacora2.data.DatosPrueba
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ===== CONSTANTES DE LARGOS MÁXIMOS =====

const val MAX_LARGO_TITULO = 100
const val MAX_LARGO_TEXTO_LARGO = 2000
const val MAX_TAMANO_ARCHIVO_BYTES = 5L * 1024L * 1024L // 5 MB

// ===== TEXTOS Y LARGOS =====

// Valida que un campo obligatorio contenga texto y no solo espacios en blanco
fun validarTextoObligatorio(texto: String): Boolean {
    return texto.isNotBlank()
}

// Valida que el texto no exceda el largo máximo permitido
fun validarLargo(texto: String, max: Int): Boolean {
    return texto.length <= max
}

// Valida que el enlace esté vacío (es opcional) o empiece con http:// o https://
fun validarEnlace(enlace: String): Boolean {
    val limpio = enlace.trim()
    return limpio.isEmpty() ||
            limpio.startsWith("http://", ignoreCase = true) ||
            limpio.startsWith("https://", ignoreCase = true)
}

// ===== LOGIN =====

// Valida que el nombre de usuario no esté vacío
fun validarUsuario(usuario: String): Boolean {
    return validarTextoObligatorio(usuario)
}

// Valida que la contraseña de login no esté vacía
fun validarPasswordLogin(password: String): Boolean {
    return validarTextoObligatorio(password)
}

// Valida que las credenciales coincidan con un usuario registrado para el perfil dado
fun credencialesCorrectas(perfil: String, usuario: String, password: String): Boolean {
    return DatosPrueba.buscarUsuario(usuario, password, perfil) != null
}

// ===== MESES Y AÑOS =====

private val PATRON_ANIO = Regex("""^\d{4}$""")

private val MESES_ESPANIOL = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
)

// Obtiene el número de mes (1 a 12) a partir de su nombre en español, o null si no es válido
fun numeroDeMes(mes: String): Int? {
    val indice = MESES_ESPANIOL.indexOf(mes.trim().lowercase(Locale.ROOT))
    return if (indice >= 0) indice + 1 else null
}

// Valida que el mes corresponda a uno de los 12 meses en español (sin distinguir mayúsculas)
fun validarMes(mes: String): Boolean {
    return numeroDeMes(mes) != null
}

// Valida que el año tenga exactamente 4 dígitos y esté dentro del rango del año actual o siguiente
fun validarAnio(
    anio: String,
    anioActual: Int = Calendar.getInstance().get(Calendar.YEAR)
): Boolean {
    val limpio = anio.trim()
    if (!limpio.matches(PATRON_ANIO)) {
        return false
    }
    val anioInt = limpio.toInt()
    return anioInt in anioActual..(anioActual + 1)
}

// ===== FECHAS =====

private val PATRON_FECHA = Regex("""^\d{2}/\d{2}/\d{4}$""")

// Parsea estrictamente una fecha con formato dd/MM/yyyy devolviendo Date o null si no es válida
private fun parsearFechaEstricta(fecha: String): Date? {
    val limpia = fecha.trim()
    if (!limpia.matches(PATRON_FECHA)) {
        return null
    }
    return try {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        formato.isLenient = false
        formato.parse(limpia)
    } catch (e: Exception) {
        null
    }
}

// Establece la hora, minuto, segundo y milisegundo a cero para comparar solo fechas
private fun inicioDelDia(fecha: Date): Calendar {
    return Calendar.getInstance().apply {
        time = fecha
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}

// Valida que la fecha de recordatorio sea válida y no sea anterior a hoy (hoy es válido)
fun validarFechaRecordatorio(
    fecha: String,
    fechaHoy: Date = Date()
): Boolean {
    val fechaParsed = parsearFechaEstricta(fecha) ?: return false
    val calFecha = inicioDelDia(fechaParsed)
    val calHoy = inicioDelDia(fechaHoy)
    return !calFecha.before(calHoy)
}

// Valida que la fecha de clase sea válida, no sea futura, y coincida con el mes y año de la bitácora
fun validarFechaClase(
    fecha: String,
    mes: String,
    anio: String,
    fechaHoy: Date = Date()
): Boolean {
    val fechaParsed = parsearFechaEstricta(fecha) ?: return false
    val mesEsperado = numeroDeMes(mes) ?: return false
    val anioLimpio = anio.trim()
    if (!anioLimpio.matches(PATRON_ANIO)) return false
    val anioEsperado = anioLimpio.toInt()

    val calFecha = inicioDelDia(fechaParsed)
    val calHoy = inicioDelDia(fechaHoy)

    if (calFecha.after(calHoy)) {
        return false
    }

    val mesFecha = calFecha.get(Calendar.MONTH) + 1
    val anioFecha = calFecha.get(Calendar.YEAR)

    return mesFecha == mesEsperado && anioFecha == anioEsperado
}

// ===== BITÁCORA =====

// Valida que se haya seleccionado una asignación y que todos los campos requeridos de la bitácora no estén vacíos
fun validarFormularioBitacora(
    asignacion: Asignacion?,
    mes: String,
    anio: String,
    titulo: String,
    proposito: String,
    instrucciones: String,
    preguntas: String
): Boolean {
    return asignacion != null &&
            validarTextoObligatorio(mes) &&
            validarTextoObligatorio(anio) &&
            validarTextoObligatorio(titulo) &&
            validarTextoObligatorio(proposito) &&
            validarTextoObligatorio(instrucciones) &&
            validarTextoObligatorio(preguntas)
}

// ===== REGISTRO DEL ESTUDIANTE =====

// Valida que los campos obligatorios contenido, actividad y aprendizaje no estén vacíos
fun validarCamposRegistro(
    contenido: String,
    actividad: String,
    aprendizaje: String
): Boolean {
    return validarTextoObligatorio(contenido) &&
            validarTextoObligatorio(actividad) &&
            validarTextoObligatorio(aprendizaje)
}

// ===== RETROALIMENTACIÓN =====

// Valida que el comentario del docente no esté vacío
fun validarComentario(comentario: String): Boolean {
    return validarTextoObligatorio(comentario)
}

// ===== ARCHIVOS =====

private val MIMES_IMAGENES_PERMITIDOS = setOf("image/jpeg", "image/jpg", "image/png")

// Valida que el archivo sea una imagen JPEG o PNG y no supere los 5 MB
fun validarArchivo(tipoMime: String?, tamanoBytes: Long): Boolean {
    if (tipoMime == null) return false
    val mimeNormalizado = tipoMime.trim().lowercase(Locale.ROOT)
    return mimeNormalizado in MIMES_IMAGENES_PERMITIDOS && tamanoBytes in 1..MAX_TAMANO_ARCHIVO_BYTES
}
