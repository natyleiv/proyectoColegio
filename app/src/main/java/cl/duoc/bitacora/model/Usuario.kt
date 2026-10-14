package cl.duoc.bitacora.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Tabla "usuarios": docentes y estudiantes FICTICIOS (el caso prohíbe datos reales).
// "curso" solo se usa en los estudiantes (ej: "5°A"); en el docente queda vacío
// porque sus cursos vienen de la tabla "asignaciones".
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val password: String,
    val perfil: String,
    val curso: String = ""
)
