package cl.duoc.bitacora.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import cl.duoc.bitacora.model.Asignacion
import cl.duoc.bitacora.model.Asignatura
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.Curso
import cl.duoc.bitacora.model.PreguntaOrientadora
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.model.Retroalimentacion
import cl.duoc.bitacora.model.Usuario

// La Database representa la base y cada DAO contiene las operaciones sobre una tabla.
// Modelo normalizado (3FN) con 8 tablas relacionadas por claves foráneas:
// cursos, asignaturas, usuarios, asignaciones, bitacoras, preguntas_orientadoras,
// registros y retroalimentaciones.
@Database(
    entities = [
        Curso::class,
        Asignatura::class,
        Usuario::class,
        Asignacion::class,
        Bitacora::class,
        PreguntaOrientadora::class,
        Registro::class,
        Retroalimentacion::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catalogoDao(): CatalogoDao
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
                // Nombre nuevo porque el esquema cambió respecto de la versión anterior
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bitacora_v2.db"
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}
