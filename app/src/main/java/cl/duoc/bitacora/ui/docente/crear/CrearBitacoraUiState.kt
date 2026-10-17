package cl.duoc.bitacora.ui.docente.crear

data class CrearBitacoraUiState(
    // true = se está editando una bitácora existente (en BORRADOR); false = creando una nueva
    val modoEdicion: Boolean = false,
    val asignacionId: Int = 0,
    val titulo: String = "",
    val proposito: String = "",
    val instrucciones: String = "",
    val preguntas: String = "",
    // Mes y año se eligen en el calendario (no se escriben). Vacío = aún sin elegir.
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
    // Ya existe una bitácora de ese curso, asignatura, mes y año
    val errorDuplicada: Boolean = false,

    // Cuando es true la pantalla vuelve atrás
    val guardada: Boolean = false
)
