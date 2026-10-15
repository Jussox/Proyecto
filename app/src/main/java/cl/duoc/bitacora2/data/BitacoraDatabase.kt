package cl.duoc.bitacora2.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(
    entities = [Bitacora::class, Registro::class, Recordatorio::class],
    version = 1
)
abstract class BitacoraDatabase : RoomDatabase() {

    abstract fun bitacoraDao(): BitacoraDao

    companion object {

        @Volatile
        private var INSTANCE: BitacoraDatabase? = null

        fun getInstance(context: Context): BitacoraDatabase {

            return INSTANCE ?: synchronized(this) {

                Room.databaseBuilder(
                    context.applicationContext,
                    BitacoraDatabase::class.java,
                    "bitacora.db"
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}