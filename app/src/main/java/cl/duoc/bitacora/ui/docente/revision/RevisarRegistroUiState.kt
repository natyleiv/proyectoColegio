package cl.duoc.bitacora.ui.docente.revision

import cl.duoc.bitacora.data.RegistroDetalle

data class RevisarRegistroUiState(
    val registro: RegistroDetalle? = null,
    val cargado: Boolean = false,
    val comentario: String = "",
    val errorComentario: Boolean = false,
    val guardado: Boolean = false
)
