package cl.duoc.bitacora.ui.estudiante.registro

data class NuevoRegistroUiState(
    // Datos de la bitácora (solo para mostrar)
    val bitacoraTitulo: String = "",
    val instrucciones: String = "",
    val preguntas: String = "",
    // Mes de la bitácora en texto (ej: "Octubre 2026"), para el mensaje de error de la fecha
    val mesBitacoraTexto: String = "",
    val cargado: Boolean = false,
    val sinAcceso: Boolean = false,

    // Campos del formulario
    val fechaClase: String = "",
    val contenido: String = "",
    val actividad: String = "",
    val aprendizaje: String = "",
    val dudas: String = "",
    val enlace: String = "",
    val fotoUri: String = "",

    // Estados de error
    val errorFecha: Boolean = false,
    val errorContenido: Boolean = false,
    val errorActividad: Boolean = false,
    val errorAprendizaje: Boolean = false,
    val errorEnlace: Boolean = false,
    val errorFoto: String = "",

    // Cuando es true la pantalla vuelve atrás
    val terminado: Boolean = false
)
