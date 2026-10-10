package cl.duoc.bitacora2.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.bitacora2.model.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios")
    fun obtenerUsuarios(): Flow<List<Usuario>>
}