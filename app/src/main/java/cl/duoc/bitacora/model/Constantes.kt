package cl.duoc.bitacora.model

// Valores de texto que se repiten en la app. Los centralizamos para no equivocarnos al escribirlos.
// OJO: las consultas SQL de los DAO usan estos mismos textos ('PUBLICADA', 'ENVIADO', etc.)

object Perfil {
    const val DOCENTE = "DOCENTE"
    const val ESTUDIANTE = "ESTUDIANTE"
}

object EstadoBitacora {
    const val BORRADOR = "BORRADOR"
    const val PUBLICADA = "PUBLICADA"
    const val CERRADA = "CERRADA"
}

object EstadoEnvio {
    const val BORRADOR = "BORRADOR"
    const val ENVIADO = "ENVIADO"
}

object EstadoRevision {
    const val PENDIENTE = "PENDIENTE"
    const val REVISADO = "REVISADO"
}
