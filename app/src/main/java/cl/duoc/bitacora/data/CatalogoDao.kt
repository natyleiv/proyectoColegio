package cl.duoc.bitacora.data

import androidx.room3.Dao

import androidx.room3.Query
import androidx.room3.Upsert
import cl.duoc.bitacora.model.Asignatura
import cl.duoc.bitacora.model.Curso

// Cursos y asignaturas (tablas catálogo)
@Dao
interface CatalogoDao {

    @Upsert
    suspend fun insertarCurso(curso: Curso)

    @Upsert
    suspend fun insertarAsignatura(asignatura: Asignatura)

    // Nombre del curso a partir de su id (ej: 1 -> "5°A")
    @Query("SELECT nombre FROM cursos WHERE id = :id")
    suspend fun nombreCurso(id: Int): String?
}
