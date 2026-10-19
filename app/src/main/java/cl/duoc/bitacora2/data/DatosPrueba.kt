package cl.duoc.bitacora2.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// DATOS FICTICIOS DE PRUEBA (no son datos reales de estudiantes ni docentes)

data class UsuarioPrueba(
    val usuario: String,
    val password: String,
    val perfil: String,
    val nombre: String,
    val curso: String
)

data class Asignacion(
    val docente: String,
    val curso: String,
    val asignatura: String
)

object DatosPrueba {

    val usuarios = listOf(
        UsuarioPrueba("estudiante1", "clave1234", "estudiante", "Angie", "7.º Básico A"),
        UsuarioPrueba("estudiante2", "clave1234", "estudiante", "Leo", "7.º Básico A"),
        UsuarioPrueba("estudiante3", "clave1234", "estudiante", "Estudiante Prueba 3", "7.º Básico A"),
        UsuarioPrueba("estudiante4", "clave1234", "estudiante", "Estudiante Prueba 4", "8.º Básico B"),
        UsuarioPrueba("docente1", "clave1234", "docente", "Docente Prueba 1", ""),
        UsuarioPrueba("docente2", "clave1234", "docente", "Docente Prueba 2", "")
    )

    val asignaciones = listOf(
        Asignacion("docente1", "7.º Básico A", "Lenguaje"),
        Asignacion("docente1", "7.º Básico A", "Matemática"),
        Asignacion("docente2", "8.º Básico B", "Ciencias")
    )

    fun buscarUsuario(usuario: String, password: String, perfil: String): UsuarioPrueba? {
        return usuarios.firstOrNull {
            it.usuario == usuario && it.password == password && it.perfil == perfil
        }
    }

    fun nombreDe(usuario: String): String {
        return usuarios.firstOrNull { it.usuario == usuario }?.nombre ?: usuario
    }

    fun cursoDe(usuario: String): String {
        return usuarios.firstOrNull { it.usuario == usuario }?.curso ?: ""
    }

    fun asignacionesDeDocente(docente: String): List<Asignacion> {
        return asignaciones.filter { it.docente == docente }
    }

    fun asignacionesDeEstudiante(estudiante: String): List<Asignacion> {
        val curso = cursoDe(estudiante)
        return asignaciones.filter { it.curso == curso }
    }

    fun estudiantesDeCurso(curso: String): List<UsuarioPrueba> {
        return usuarios.filter { it.perfil == "estudiante" && it.curso == curso }
    }
}

fun fechaHoy(): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
}