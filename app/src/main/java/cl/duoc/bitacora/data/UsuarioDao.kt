package cl.duoc.bitacora.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import cl.duoc.bitacora.model.Usuario
import kotlinx.coroutines.flow.Flow

// Configuramos el QUÉ VAMOS A HACER con la tabla usuarios
@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Usuario?

    // Estudiantes del curso de una bitácora, SOLO si la bitácora pertenece al docente indicado.
    // Así un docente no puede ver estudiantes de cursos que no tiene asignados.
    @Query(
        """
        SELECT u.* FROM usuarios u
        INNER JOIN bitacoras b ON u.curso = b.curso
        WHERE b.id = :bitacoraId AND b.docenteId = :docenteId AND u.perfil = 'ESTUDIANTE'
        ORDER BY u.nombre
        """
    )
    fun obtenerEstudiantesDeBitacora(bitacoraId: Int, docenteId: Int): Flow<List<Usuario>>
}
