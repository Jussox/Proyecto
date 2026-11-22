package cl.duoc.bitacora2.util

import cl.duoc.bitacora2.data.Asignacion
import cl.duoc.bitacora2.data.DatosPrueba
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// ============================================================================
// 1. CONSTANTES Y VALIDACIONES EN COMÚN (Compartidas entre perfiles y Login)
// ============================================================================

// Constantes de largos máximos para campos de texto y archivos
const val MAX_LARGO_TITULO = 100
const val MAX_LARGO_TEXTO_LARGO = 2000
const val MAX_TAMANO_ARCHIVO_BYTES = 5L * 1024L * 1024L // 5 MB

// Común: Valida que un campo obligatorio contenga texto y no solo espacios en blanco
fun validarTextoObligatorio(texto: String): Boolean {
    return texto.isNotBlank()
}

// Común: Valida que un texto no exceda el límite máximo de caracteres permitido
fun validarLargo(texto: String, max: Int): Boolean {
    return texto.length <= max
}

// Pantalla: LoginScreen
// Valida que el nombre de usuario no esté en blanco
fun validarUsuario(usuario: String): Boolean {
    return validarTextoObligatorio(usuario)
}

// Pantalla: LoginScreen
// Valida que la contraseña de login no esté en blanco
fun validarPasswordLogin(password: String): Boolean {
    return validarTextoObligatorio(password)
}

// Pantalla: LoginScreen
// Valida que tanto usuario como contraseña hayan sido ingresados; devuelve mensaje de error o null
fun validarCamposLogin(usuario: String, password: String): String? {
    if (!validarUsuario(usuario) || !validarPasswordLogin(password)) {
        return "Debe ingresar usuario y contraseña"
    }
    return null
}

// Pantalla: LoginScreen (Modo sin conexión / datos de prueba)
// Valida si las credenciales coinciden con los usuarios precargados en memoria
fun credencialesCorrectas(perfil: String, usuario: String, password: String): Boolean {
    return DatosPrueba.buscarUsuario(usuario, password, perfil) != null
}

// Pantalla: EstudianteRegistroScreen / AdjuntosSection
// Valida que el archivo seleccionado sea una imagen JPG o PNG y que no supere los 5 MB
private val MIMES_IMAGENES_PERMITIDOS = setOf("image/jpeg", "image/jpg", "image/png")

fun validarArchivo(tipoMime: String?, tamanoBytes: Long): Boolean {
    if (tipoMime == null) return false
    val mimeNormalizado = tipoMime.trim().lowercase(Locale.ROOT)
    return mimeNormalizado in MIMES_IMAGENES_PERMITIDOS && tamanoBytes in 1..MAX_TAMANO_ARCHIVO_BYTES
}

// Pantalla: EstudianteRegistroScreen / AdjuntosSection
// Valida el archivo adjunto y devuelve el mensaje de error para el usuario o null si es válido
fun validarArchivoAdjunto(tipoMime: String?, tamanoBytes: Long): String? {
    if (!validarArchivo(tipoMime, tamanoBytes)) {
        return "Solo se permiten fotografías JPG o PNG de hasta 5 MB"
    }
    return null
}

// --- Utilidades compartidas para el manejo y parseo de fechas ---

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

// Establece la hora a 00:00:00 para comparar solo días calendario
private fun inicioDelDia(fecha: Date): Calendar {
    return Calendar.getInstance().apply {
        time = fecha
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
}


// ============================================================================
// 2. VALIDACIONES DE ESTUDIANTE
// ============================================================================

// Pantalla: EstudianteRegistroScreen
// Valida que el enlace sea opcional (puede estar vacío) o que inicie con http:// o https://
fun validarEnlace(enlace: String): Boolean {
    val limpio = enlace.trim()
    return limpio.isEmpty() ||
            limpio.startsWith("http://", ignoreCase = true) ||
            limpio.startsWith("https://", ignoreCase = true)
}

// Pantalla: EstudianteRegistroScreen
// Valida que los tres campos principales de la clase (contenido, actividad, aprendizaje) tengan texto
fun validarCamposRegistro(
    contenido: String,
    actividad: String,
    aprendizaje: String
): Boolean {
    return validarTextoObligatorio(contenido) &&
            validarTextoObligatorio(actividad) &&
            validarTextoObligatorio(aprendizaje)
}

// Pantalla: EstudianteRegistroScreen
// Valida que la fecha de la clase sea válida, no sea futura, y calce con el mes y año de la bitácora
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

// Pantalla: EstudianteRegistroScreen
// Valida todo el formulario de aporte del estudiante antes de guardar o enviar y devuelve el mensaje de error o null
fun validarFormularioRegistroEstudiante(
    bitacoraDisponible: Boolean,
    fecha: String,
    mesBitacora: String,
    anioBitacora: String,
    contenido: String,
    actividad: String,
    aprendizaje: String,
    enlaces: String
): String? {
    if (!bitacoraDisponible) {
        return "La bitácora no está disponible"
    }
    if (!validarFechaClase(fecha, mesBitacora, anioBitacora)) {
        return "Ingrese una fecha válida con formato dd/mm/aaaa, no futura y que corresponda al mes y año de la bitácora ($mesBitacora $anioBitacora)"
    }
    if (!validarCamposRegistro(contenido, actividad, aprendizaje)) {
        return "Complete los campos obligatorios (contenido, actividad y aprendizaje)"
    }
    if (!validarEnlace(enlaces)) {
        return "El enlace debe comenzar con http:// o https://"
    }
    if (!validarLargo(contenido, MAX_LARGO_TEXTO_LARGO)) {
        return "El contenido no puede superar los 2000 caracteres"
    }
    if (!validarLargo(actividad, MAX_LARGO_TEXTO_LARGO)) {
        return "La actividad no puede superar los 2000 caracteres"
    }
    if (!validarLargo(aprendizaje, MAX_LARGO_TEXTO_LARGO)) {
        return "El aprendizaje no puede superar los 2000 caracteres"
    }
    return null
}


// ============================================================================
// 3. VALIDACIONES DE DOCENTE
// ============================================================================

private val PATRON_ANIO = Regex("""^\d{4}$""")

private val MESES_ESPANIOL = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
)

// Pantalla: DocenteCrearBitacoraScreen
// Obtiene el número de mes (1 a 12) a partir del nombre escrito en español
fun numeroDeMes(mes: String): Int? {
    val indice = MESES_ESPANIOL.indexOf(mes.trim().lowercase(Locale.ROOT))
    return if (indice >= 0) indice + 1 else null
}

// Pantalla: DocenteCrearBitacoraScreen
// Valida que el texto del mes corresponda a uno de los 12 meses en español
fun validarMes(mes: String): Boolean {
    return numeroDeMes(mes) != null
}

// Pantalla: DocenteCrearBitacoraScreen
// Valida que el año tenga 4 dígitos numéricos y sea el año actual o el siguiente
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

// Pantalla: DocenteCrearBitacoraScreen
// Valida que se haya seleccionado asignación y todos los campos requeridos estén llenos
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

// Pantalla: DocenteCrearBitacoraScreen
// Valida los textos de la bitácora (obligatorios, mes, año y largos máximos) devolviendo el mensaje de error o null
fun validarCamposTextoBitacora(
    mes: String,
    anio: String,
    titulo: String,
    proposito: String,
    instrucciones: String,
    preguntas: String
): String? {
    return when {
        !validarTextoObligatorio(mes) ||
                !validarTextoObligatorio(anio) ||
                !validarTextoObligatorio(titulo) ||
                !validarTextoObligatorio(proposito) ||
                !validarTextoObligatorio(instrucciones) ||
                !validarTextoObligatorio(preguntas) ->
            "Complete todos los campos obligatorios"
        !validarMes(mes) ->
            "Ingrese un mes válido (por ejemplo: Octubre)"
        !validarAnio(anio) ->
            "El año debe tener 4 dígitos y ser el actual o el siguiente"
        !validarLargo(titulo, MAX_LARGO_TITULO) ->
            "El título no puede superar los 100 caracteres"
        !validarLargo(proposito, MAX_LARGO_TEXTO_LARGO) ->
            "El propósito no puede superar los 2000 caracteres"
        !validarLargo(instrucciones, MAX_LARGO_TEXTO_LARGO) ->
            "Las instrucciones no pueden superar los 2000 caracteres"
        !validarLargo(preguntas, MAX_LARGO_TEXTO_LARGO) ->
            "Las preguntas no pueden superar los 2000 caracteres"
        else -> null
    }
}

// Pantalla: DocenteCrearBitacoraScreen
// Valida el formulario de creación verificando curso/asignatura seleccionada y los textos
fun validarCrearBitacora(
    tieneAsignacion: Boolean,
    mes: String,
    anio: String,
    titulo: String,
    proposito: String,
    instrucciones: String,
    preguntas: String
): String? {
    if (!tieneAsignacion) {
        return "Seleccione un curso y asignatura"
    }
    return validarCamposTextoBitacora(
        mes = mes,
        anio = anio,
        titulo = titulo,
        proposito = proposito,
        instrucciones = instrucciones,
        preguntas = preguntas
    )
}

// Pantalla: DocenteRevisionScreen
// Valida que el texto del comentario no esté vacío ni en blanco
fun validarComentario(comentario: String): Boolean {
    return validarTextoObligatorio(comentario)
}

// Pantalla: DocenteRevisionScreen
// Valida la retroalimentación del docente para guardar borrador o marcar como revisado devolviendo el mensaje de error o null
fun validarComentarioDocente(comentario: String, accion: String = "guardar"): String? {
    if (!validarComentario(comentario)) {
        return if (accion == "revisar") {
            "Escriba un comentario antes de marcar como revisado"
        } else {
            "Escriba un comentario antes de guardar"
        }
    }
    if (!validarLargo(comentario, MAX_LARGO_TEXTO_LARGO)) {
        return "El comentario no puede superar los 2000 caracteres"
    }
    return null
}

// Pantalla: DocenteAportesScreen
// Valida si el texto ingresado en el filtro de fecha tiene un formato parcial o completo válido (solo dígitos y '/')
fun validarCaracteresFiltroFecha(texto: String): Boolean {
    return texto.length <= 10 && texto.all { it.isDigit() || it == '/' }
}

// Pantalla: DocenteAportesScreen
// Evalúa si un registro de estudiante cumple con los criterios activos de búsqueda y estado
fun cumpleFiltrosAporte(
    estudianteUsuario: String,
    estudianteNombre: String,
    fechaClase: String,
    estadoRegistro: String,
    filtroEstudiante: String,
    filtroFecha: String,
    filtroEstado: String
): Boolean {
    val cumpleEstudiante = filtroEstudiante.isBlank() ||
            estudianteUsuario.contains(filtroEstudiante.trim(), ignoreCase = true) ||
            estudianteNombre.contains(filtroEstudiante.trim(), ignoreCase = true)

    val cumpleFecha = filtroFecha.isBlank() ||
            fechaClase.contains(filtroFecha.trim())

    val cumpleEstado = filtroEstado == "todos" ||
            estadoRegistro.equals(filtroEstado, ignoreCase = true)

    return cumpleEstudiante && cumpleFecha && cumpleEstado
}

// Pantalla: DocenteRecordatoriosScreen / DocenteRecordatoriosViewModel
enum class TipoMensajeRecordatorio {
    NINGUNO,
    ERROR,
    CREADO,
    COMPLETADO
}

// Pantalla: DocenteRecordatoriosScreen
// Clasifica el mensaje emitido para aplicar el diseño de banner correspondiente
fun clasificarMensajeRecordatorio(mensaje: String): TipoMensajeRecordatorio {
    if (mensaje.isBlank()) return TipoMensajeRecordatorio.NINGUNO
    val texto = mensaje.lowercase(Locale.ROOT)
    return when {
        texto.contains("ingresa") ||
                texto.contains("indique") ||
                texto.contains("válida") ||
                texto.contains("pasó") ||
                texto.contains("anterior") ||
                texto.contains("error") ||
                texto.contains("seleccione") -> TipoMensajeRecordatorio.ERROR

        texto.contains("completad") -> TipoMensajeRecordatorio.COMPLETADO

        else -> TipoMensajeRecordatorio.CREADO
    }
}

// Pantalla: DocenteRecordatoriosScreen
// Verifica si el recordatorio sigue pendiente
fun esRecordatorioPendiente(estado: String?): Boolean {
    return estado.equals("pendiente", ignoreCase = true)
}

// Pantalla: DocenteRecordatoriosScreen
// Valida que la fecha del recordatorio tenga formato válido y no sea anterior a hoy
fun validarFechaRecordatorio(
    fecha: String,
    fechaHoy: Date = Date()
): Boolean {
    val fechaParsed = parsearFechaEstricta(fecha) ?: return false
    val calFecha = inicioDelDia(fechaParsed)
    val calHoy = inicioDelDia(fechaHoy)
    return !calFecha.before(calHoy)
}

// Pantalla: DocenteRecordatoriosScreen / DocenteRecordatoriosViewModel
// Valida la fecha del recordatorio devolviendo un mensaje claro de error o null si es válida
fun validarFechaRecordatorioConMensaje(fecha: String): String? {
    if (fecha.isBlank()) {
        return "Ingresa una fecha programada en formato dd/mm/aaaa antes de continuar."
    }
    if (!validarFechaRecordatorio(fecha)) {
        return "La fecha ingresada no es válida o ya pasó. Debe tener formato dd/mm/aaaa y ser igual o posterior a hoy."
    }
    return null
}

// Pantalla: DocenteRecordatoriosViewModel
// Genera el mensaje de confirmación al programar un nuevo recordatorio
fun mensajeRecordatorioProgramado(tipo: String, fecha: String): String {
    return "Recordatorio de \"$tipo\" programado para el $fecha. Ya está visible en tu lista."
}

// Pantalla: DocenteRecordatoriosViewModel
// Genera el mensaje de confirmación al marcar un recordatorio como completado
fun mensajeRecordatorioCompletado(tipo: String? = null): String {
    return if (!tipo.isNullOrBlank()) {
        "El recordatorio \"$tipo\" fue marcado como completado."
    } else {
        "El recordatorio fue marcado como completado."
    }
}

// Pantalla: DocenteRevisionScreen
// Verifica si el aporte ya fue marcado como revisado
fun esAporteRevisado(estado: String?): Boolean {
    return estado.equals("revisado", ignoreCase = true)
}

// Pantalla: DocenteRevisionScreen
// Verifica si la ruta o URI de fotografía es válida y no está en blanco
fun tieneFotografiaValida(uriTexto: String?): Boolean {
    return !uriTexto.isNullOrBlank() && uriTexto.trim() != "—"
}

// Pantalla: DocenteRevisionScreen
// Formatea el contador de caracteres del comentario docente
fun textoContadorComentario(comentario: String): String {
    return "${comentario.length} / $MAX_LARGO_TEXTO_LARGO"
}