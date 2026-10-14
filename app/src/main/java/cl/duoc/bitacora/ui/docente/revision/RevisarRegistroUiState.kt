package cl.duoc.bitacora.ui.docente.revision

import cl.duoc.bitacora.model.Registro

data class RevisarRegistroUiState(
    val registro: Registro? = null,
    val cargado: Boolean = false,
    val comentario: String = "",
    val errorComentario: Boolean = false,
    val guardado: Boolean = false
)
