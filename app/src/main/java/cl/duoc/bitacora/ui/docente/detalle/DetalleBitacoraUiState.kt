package cl.duoc.bitacora.ui.docente.detalle

import cl.duoc.bitacora.model.Registro

const val FILTRO_TODOS = "TODOS"
const val FILTRO_PENDIENTES = "PENDIENTES"
const val FILTRO_REVISADOS = "REVISADOS"

data class DetalleBitacoraUiState(
    // Lista ya filtrada que se muestra en pantalla
    val registros: List<Registro> = emptyList(),
    val filtroEstado: String = FILTRO_TODOS,
    val busqueda: String = "",

    // Indicadores de participación
    val totalEstudiantes: Int = 0,
    val participaron: Int = 0,
    val sinParticipacion: List<String> = emptyList(),
    val totalAportes: Int = 0,
    val revisados: Int = 0,
    val pendientes: Int = 0
)
