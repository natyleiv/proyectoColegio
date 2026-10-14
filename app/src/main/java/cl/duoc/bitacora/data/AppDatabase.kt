package cl.duoc.bitacora.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import cl.duoc.bitacora.model.Asignacion
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.model.Usuario

// La Database representa la base y cada DAO contiene las operaciones sobre una tabla.
@Database(
    entities = [Usuario::class, Asignacion::class, Bitacora::class, Registro::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun asignacionDao(): AsignacionDao
    abstract fun bitacoraDao(): BitacoraDao
    abstract fun registroDao(): RegistroDao

    companion object {
        // Solo queremos UNA instancia de la base de datos (Singleton)
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
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
