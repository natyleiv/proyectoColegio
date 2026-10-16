package cl.duoc.bitacora.data

import cl.duoc.bitacora.model.Asignacion
import cl.duoc.bitacora.model.Asignatura
import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.Curso
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.model.EstadoEnvio
import cl.duoc.bitacora.model.Perfil
import cl.duoc.bitacora.model.PreguntaOrientadora
import cl.duoc.bitacora.model.Registro
import cl.duoc.bitacora.model.Usuario

// Datos FICTICIOS de prueba (el caso exige no usar datos reales).
// Solo se cargan si la base de datos está vacía (primera ejecución).
// El orden importa por las claves foráneas: primero las tablas "padre" (cursos, asignaturas),
// luego usuarios, asignaciones, bitácoras y al final registros.
suspend fun cargarDatosDemo(db: AppDatabase) {
    if (db.usuarioDao().contar() > 0) return

    // 1) Catálogos
    val catalogo = db.catalogoDao()
    catalogo.insertarCurso(Curso(1, "5°A"))
    catalogo.insertarCurso(Curso(2, "6°B"))
    catalogo.insertarAsignatura(Asignatura(1, "Matemática"))
    catalogo.insertarAsignatura(Asignatura(2, "Lenguaje"))
    catalogo.insertarAsignatura(Asignatura(3, "Historia"))

    // 2) Usuarios
    val usuarios = db.usuarioDao()
    // Docentes (contraseña: docente123)
    usuarios.insertar(Usuario(1, "Profesora Ana Rojas", "docente1@escuela.cl", "docente123", Perfil.DOCENTE))
    usuarios.insertar(Usuario(2, "Profesor Luis Soto", "docente2@escuela.cl", "docente123", Perfil.DOCENTE))
    // Estudiantes (contraseña: alumno123); cursoId 1 = 5°A, 2 = 6°B
    usuarios.insertar(Usuario(3, "Estudiante Uno", "alumno1@escuela.cl", "alumno123", Perfil.ESTUDIANTE, 1))
    usuarios.insertar(Usuario(4, "Estudiante Dos", "alumno2@escuela.cl", "alumno123", Perfil.ESTUDIANTE, 1))
    usuarios.insertar(Usuario(5, "Estudiante Tres", "alumno3@escuela.cl", "alumno123", Perfil.ESTUDIANTE, 1))
    usuarios.insertar(Usuario(6, "Estudiante Cuatro", "alumno4@escuela.cl", "alumno123", Perfil.ESTUDIANTE, 1))
    usuarios.insertar(Usuario(7, "Estudiante Cinco", "alumno5@escuela.cl", "alumno123", Perfil.ESTUDIANTE, 2))

    // 3) Asignaciones (docente - curso - asignatura)
    val asignaciones = db.asignacionDao()
    asignaciones.insertar(Asignacion(1, docenteId = 1, cursoId = 1, asignaturaId = 1)) // Ana: 5°A Matemática
    asignaciones.insertar(Asignacion(2, docenteId = 1, cursoId = 1, asignaturaId = 2)) // Ana: 5°A Lenguaje
    asignaciones.insertar(Asignacion(3, docenteId = 2, cursoId = 2, asignaturaId = 3)) // Luis: 6°B Historia

    // 4) Bitácoras y sus preguntas orientadoras
    val bitacoras = db.bitacoraDao()
    bitacoras.insertar(
        Bitacora(
            id = 1,
            asignacionId = 1,
            titulo = "Bitácora de Octubre - Matemática",
            proposito = "Registrar lo aprendido en cada clase de Matemática durante octubre.",
            instrucciones = "Después de cada clase escribe qué trabajaste, qué hiciste y qué aprendiste.",
            mes = 10,
            anio = 2026,
            estado = EstadoBitacora.PUBLICADA,
            fechaPublicacion = "01/10/2026"
        )
    )
    bitacoras.insertarPregunta(PreguntaOrientadora(1, 1, 1, "¿Qué contenido trabajamos hoy?"))
    bitacoras.insertarPregunta(PreguntaOrientadora(2, 1, 2, "¿Qué fue lo más difícil?"))
    bitacoras.insertarPregunta(PreguntaOrientadora(3, 1, 3, "¿Qué te gustaría repasar?"))

    bitacoras.insertar(
        Bitacora(
            id = 2,
            asignacionId = 3,
            titulo = "Bitácora de Octubre - Historia",
            proposito = "Registrar los aprendizajes de Historia del mes.",
            instrucciones = "Escribe un registro por clase y agrega una foto de tu cuaderno si quieres.",
            mes = 10,
            anio = 2026,
            estado = EstadoBitacora.PUBLICADA,
            fechaPublicacion = "01/10/2026"
        )
    )
    bitacoras.insertarPregunta(PreguntaOrientadora(4, 2, 1, "¿Qué hecho histórico vimos?"))
    bitacoras.insertarPregunta(PreguntaOrientadora(5, 2, 2, "¿Por qué es importante?"))

    // 5) Registros: uno ya enviado (pendiente de revisión) y un borrador, para poder probar
    val ahora = System.currentTimeMillis()
    val registros = db.registroDao()
    registros.insertar(
        Registro(
            bitacoraId = 1,
            estudianteId = 3,
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
            estudianteId = 4,
            fechaClase = "06/10/2026",
            contenido = "Suma de fracciones",
            actividad = "",
            aprendizaje = "",
            estadoEnvio = EstadoEnvio.BORRADOR,
            fechaCreacion = ahora
        )
    )
}
