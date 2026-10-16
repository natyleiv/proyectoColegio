package cl.duoc.bitacora.data

// Clases PLANAS con el resultado de consultas con JOIN.
// No son tablas: Room llena cada propiedad con la columna del SELECT que tenga el mismo nombre.
// Así la pantalla recibe curso/asignatura/nombres ya resueltos aunque en la base estén en tablas aparte.

// Una asignación (docente-curso-asignatura) con los nombres ya resueltos
data class AsignacionDetalle(
    val id: Int,
    val cursoId: Int,
    val curso: String,
    val asignaturaId: Int,
    val asignatura: String
)

// Una bitácora con docente, curso y asignatura resueltos por JOIN
data class BitacoraDetalle(
    val id: Int,
    val asignacionId: Int,
    val titulo: String,
    val proposito: String,
    val instrucciones: String,
    val mes: Int,
    val anio: Int,
    val estado: String,
    val fechaPublicacion: String,
    val fechaCierre: String,
    val docenteId: Int,
    val cursoId: Int,
    val curso: String,
    val asignatura: String
)

// Un registro con título de bitácora, nombre del estudiante y retroalimentación (si existe)
data class RegistroDetalle(
    val id: Int,
    val bitacoraId: Int,
    val estudianteId: Int,
    val fechaClase: String,
    val contenido: String,
    val actividad: String,
    val aprendizaje: String,
    val dudas: String,
    val fotoUri: String,
    val enlace: String,
    val estadoEnvio: String,
    val fechaCreacion: Long,
    val fechaEnvio: Long,
    val bitacoraTitulo: String,
    val estudianteNombre: String,
    // Vienen de la tabla retroalimentaciones (LEFT JOIN): vacío/0 si aún no hay revisión
    val comentarioDocente: String,
    val fechaRetro: Long,
    val estadoRevision: String
)
