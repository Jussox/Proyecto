package cl.duoc.bitacora2.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "bitacoras")
data class Bitacora(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val docente: String,
    val curso: String,
    val asignatura: String,
    val mes: String,
    val anio: String,
    val titulo: String,
    val proposito: String,
    val instrucciones: String,
    val preguntas: String,
    val estado: String,
    val fechaPublicacion: String = "",
    val fechaCierre: String = ""
)

@Entity(tableName = "registros")
data class Registro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val bitacoraId: Int,
    val estudiante: String,
    val fechaClase: String,
    val contenido: String,
    val actividad: String,
    val aprendizaje: String,
    val dudas: String,
    val fotografias: String,
    val archivos: String,
    val enlaces: String,
    val estado: String,
    val comentario: String = "",
    val fechaCreacion: String,
    val fechaEnvio: String = "",
    val fechaRetroalimentacion: String = "",
    val docenteRevisor: String = ""
)

@Entity(tableName = "recordatorios")
data class Recordatorio(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val docente: String,
    val tipo: String,
    val fechaProgramada: String,
    val estado: String
)