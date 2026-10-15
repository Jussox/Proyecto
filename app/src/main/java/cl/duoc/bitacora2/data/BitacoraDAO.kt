package cl.duoc.bitacora2.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BitacoraDao {

    @Insert
    suspend fun insertarBitacora(bitacora: Bitacora)

    @Query("SELECT * FROM bitacoras")
    fun obtenerBitacoras(): Flow<List<Bitacora>>

    @Query("UPDATE bitacoras SET estado = 'publicada', fechaPublicacion = :fecha WHERE id = :id")
    suspend fun publicarBitacora(id: Int, fecha: String)

    @Query("UPDATE bitacoras SET estado = 'cerrada', fechaCierre = :fecha WHERE id = :id")
    suspend fun cerrarBitacora(id: Int, fecha: String)

    @Insert
    suspend fun insertarRegistro(registro: Registro)

    @Query("SELECT * FROM registros")
    fun obtenerRegistros(): Flow<List<Registro>>

    @Query("UPDATE registros SET estado = 'pendiente', fechaEnvio = :fecha WHERE id = :id")
    suspend fun enviarRegistro(id: Int, fecha: String)

    @Query("UPDATE registros SET comentario = :comentario, fechaRetroalimentacion = :fecha, docenteRevisor = :docente WHERE id = :id")
    suspend fun guardarComentario(id: Int, comentario: String, fecha: String, docente: String)

    @Query("UPDATE registros SET estado = 'revisado' WHERE id = :id")
    suspend fun marcarRevisado(id: Int)

    @Insert
    suspend fun insertarRecordatorio(recordatorio: Recordatorio)

    @Query("SELECT * FROM recordatorios")
    fun obtenerRecordatorios(): Flow<List<Recordatorio>>

    @Query("UPDATE recordatorios SET estado = 'completado' WHERE id = :id")
    suspend fun completarRecordatorio(id: Int)
}