package cl.duoc.bitacora.data

import cl.duoc.bitacora.model.Asignacion
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.model.EstadoEnvio
import cl.duoc.bitacora.model.Perfil
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.model.Usuario

// Datos FICTICIOS de prueba (el caso exige no usar datos reales).
// Solo se cargan si la base de datos está vacía (primera ejecución).
suspend fun cargarDatosDemo(db: AppDatabase) {
    if (db.usuarioDao().contar() > 0) return

    val usuarios = db.usuarioDao()
    // Docentes (contraseña: docente123)
    usuarios.insertar(Usuario(1, "Profesora Ana Rojas", "docente1@escuela.cl", "docente123", Perfil.DOCENTE))
    usuarios.insertar(Usuario(2, "Profesor Luis Soto", "docente2@escuela.cl", "docente123", Perfil.DOCENTE))
    // Estudiantes (contraseña: alumno123)
    usuarios.insertar(Usuario(3, "Estudiante Uno", "alumno1@escuela.cl", "alumno123", Perfil.ESTUDIANTE, "5°A"))
    usuarios.insertar(Usuario(4, "Estudiante Dos", "alumno2@escuela.cl", "alumno123", Perfil.ESTUDIANTE, "5°A"))
    usuarios.insertar(Usuario(5, "Estudiante Tres", "alumno3@escuela.cl", "alumno123", Perfil.ESTUDIANTE, "5°A"))
    usuarios.insertar(Usuario(6, "Estudiante Cuatro", "alumno4@escuela.cl", "alumno123", Perfil.ESTUDIANTE, "5°A"))
    usuarios.insertar(Usuario(7, "Estudiante Cinco", "alumno5@escuela.cl", "alumno123", Perfil.ESTUDIANTE, "6°B"))

    val asignaciones = db.asignacionDao()
    asignaciones.insertar(Asignacion(1, 1, "5°A", "Matemática"))
    asignaciones.insertar(Asignacion(2, 1, "5°A", "Lenguaje"))
    asignaciones.insertar(Asignacion(3, 2, "6°B", "Historia"))

    val bitacoras = db.bitacoraDao()
    bitacoras.insertar(
        Bitacora(
            id = 1,
            titulo = "Bitácora de Octubre - Matemática",
            proposito = "Registrar lo aprendido en cada clase de Matemática durante octubre.",
            instrucciones = "Después de cada clase escribe qué trabajaste, qué hiciste y qué aprendiste.",
            preguntas = "¿Qué contenido trabajamos hoy?\n¿Qué fue lo más difícil?\n¿Qué te gustaría repasar?",
            curso = "5°A",
            asignatura = "Matemática",
            mes = 10,
            anio = 2026,
            docenteId = 1,
            estado = EstadoBitacora.PUBLICADA,
            fechaPublicacion = "01/10/2026"
        )
    )
    bitacoras.insertar(
        Bitacora(
            id = 2,
            titulo = "Bitácora de Octubre - Historia",
            proposito = "Registrar los aprendizajes de Historia del mes.",
            instrucciones = "Escribe un registro por clase y agrega una foto de tu cuaderno si quieres.",
            preguntas = "¿Qué hecho histórico vimos?\n¿Por qué es importante?",
            curso = "6°B",
            asignatura = "Historia",
            mes = 10,
            anio = 2026,
            docenteId = 2,
            estado = EstadoBitacora.PUBLICADA,
            fechaPublicacion = "01/10/2026"
        )
    )

    val ahora = System.currentTimeMillis()
    val registros = db.registroDao()
    // Un aporte ya enviado (pendiente de revisión) y un borrador, para poder probar la app
    registros.insertar(
        Registro(
            bitacoraId = 1,
            bitacoraTitulo = "Bitácora de Octubre - Matemática",
            estudianteId = 3,
            estudianteNombre = "Estudiante Uno",
            fechaClase = "05/10/2026",
            contenido = "Fracciones equivalentes",
            actividad = "Resolvimos ejercicios en el cuaderno y en parejas.",
            aprendizaje = "Aprendí a simplificar fracciones dividiendo por el mismo número.",
            dudas = "No entendí cuando el denominador es muy grande.",
            estadoEnvio = EstadoEnvio.ENVIADO,
            fechaCreacion = ahora - 86_400_000L,
            fechaEnvio = ahora - 86_400_000L
        )
    )
    registros.insertar(
        Registro(
            bitacoraId = 1,
            bitacoraTitulo = "Bitácora de Octubre - Matemática",
            estudianteId = 4,
            estudianteNombre = "Estudiante Dos",
            fechaClase = "06/10/2026",
            contenido = "Suma de fracciones",
            actividad = "",
            aprendizaje = "",
            estadoEnvio = EstadoEnvio.BORRADOR,
            fechaCreacion = ahora
        )
    )
}
