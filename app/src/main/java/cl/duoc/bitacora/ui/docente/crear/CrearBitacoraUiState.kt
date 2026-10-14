package cl.duoc.bitacora.ui.docente.crear

data class CrearBitacoraUiState(
    val asignacionId: Int = 0,
    val titulo: String = "",
    val proposito: String = "",
    val instrucciones: String = "",
    val preguntas: String = "",
    val mes: String = "",
    val anio: String = "",

    // Estados de error
    val errorAsignacion: Boolean = false,
    val errorTitulo: Boolean = false,
    val errorProposito: Boolean = false,
    val errorInstrucciones: Boolean = false,
    val errorPreguntas: Boolean = false,
    val errorMes: Boolean = false,
    val errorAnio: Boolean = false,

    // Cuando es true la pantalla vuelve atrás
    val guardada: Boolean = false
)
