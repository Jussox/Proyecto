package cl.duoc.bitacora2.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import cl.duoc.bitacora2.model.Usuario

@Database(entities = [Usuario::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database.db"
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}