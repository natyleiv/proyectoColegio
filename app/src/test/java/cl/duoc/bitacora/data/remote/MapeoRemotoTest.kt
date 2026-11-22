package cl.duoc.bitacora.data.remote

import cl.duoc.bitacora.model.Bitacora
import cl.duoc.bitacora.model.EstadoBitacora
import cl.duoc.bitacora.model.EstadoEnvio
import cl.duoc.bitacora.model.Registro
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// Pruebas de las conversiones entre el JSON del backend y los modelos de la app.
// Cada conversión se prueba con todos los campos en null (el backend no mandó el dato)
// y con datos completos, para recorrer los dos caminos de cada "?:".
class MapeoRemotoTest {

    // ---------- Bitácora ----------
    @Test
    fun aBitacora_camposNull_usaValoresPorDefecto() {
        // ARRANGE: lo que llegaría si el backend mandara todo en null
        val remota = BitacoraRemota()
        // ACT
        val bitacora = MapeoRemoto.aBitacora(remota)
        // ASSERT
        assertEquals(0, bitacora.id)
        assertEquals(0, bitacora.asignacionId)
        assertEquals("", bitacora.titulo)
        assertEquals("", bitacora.proposito)
        assertEquals("", bitacora.instrucciones)
        assertEquals(1, bitacora.mes)
        assertEquals(2026, bitacora.anio)
        assertEquals(EstadoBitacora.BORRADOR, bitacora.estado)
        assertEquals("", bitacora.fechaPublicacion)
        assertEquals("", bitacora.fechaCierre)
    }

    @Test
    fun aBitacora_conDatos_copiaTodosLosCampos() {
        val remota = BitacoraRemota(
            id = 3, asignacionId = 2, titulo = "Bitácora de Noviembre - Lenguaje",
            proposito = "Registrar lo aprendido", instrucciones = "Escribe después de cada clase",
            mes = 11, anio = 2026, estado = EstadoBitacora.PUBLICADA,
            fechaPublicacion = "21/11/2026", fechaCierre = "30/11/2026"
        )

        val bitacora = MapeoRemoto.aBitacora(remota)

        assertEquals(3, bitacora.id)
        assertEquals(2, bitacora.asignacionId)
        assertEquals("Bitácora de Noviembre - Lenguaje", bitacora.titulo)
        assertEquals("Registrar lo aprendido", bitacora.proposito)
        assertEquals("Escribe después de cada clase", bitacora.instrucciones)
        assertEquals(11, bitacora.mes)
        assertEquals(2026, bitacora.anio)
        assertEquals(EstadoBitacora.PUBLICADA, bitacora.estado)
        assertEquals("21/11/2026", bitacora.fechaPublicacion)
        assertEquals("30/11/2026", bitacora.fechaCierre)
    }

    @Test
    fun aBitacoraRemota_idCero_enviaIdNullParaQueLoAsigneElBackend() {
        val nueva = Bitacora(
            asignacionId = 1, titulo = "Bitácora nueva", proposito = "p",
            instrucciones = "i", mes = 12, anio = 2026
        )

        val remota = MapeoRemoto.aBitacoraRemota(nueva)

        assertNull(remota.id)
        assertEquals(1, remota.asignacionId)
        assertEquals(12, remota.mes)
    }

    @Test
    fun aBitacoraRemota_conId_enviaElMismoId() {
        val existente = Bitacora(
            id = 7, asignacionId = 1, titulo = "Bitácora existente", proposito = "p",
            instrucciones = "i", mes = 10, anio = 2026
        )

        assertEquals(7, MapeoRemoto.aBitacoraRemota(existente).id)
    }

    // ---------- Pregunta orientadora ----------
    @Test
    fun aPregunta_camposNull_usaValoresPorDefecto() {
        val pregunta = MapeoRemoto.aPregunta(PreguntaRemota())

        assertEquals(0, pregunta.id)
        assertEquals(0, pregunta.bitacoraId)
        assertEquals(0, pregunta.orden)
        assertEquals("", pregunta.texto)
    }

    @Test
    fun aPregunta_conDatos_copiaTodosLosCampos() {
        val pregunta = MapeoRemoto.aPregunta(
            PreguntaRemota(id = 4, bitacoraId = 2, orden = 1, texto = "¿Qué hecho histórico vimos?")
        )

        assertEquals(4, pregunta.id)
        assertEquals(2, pregunta.bitacoraId)
        assertEquals(1, pregunta.orden)
        assertEquals("¿Qué hecho histórico vimos?", pregunta.texto)
    }

    // ---------- Registro ----------
    @Test
    fun aRegistro_camposNull_usaValoresPorDefecto() {
        val registro = MapeoRemoto.aRegistro(RegistroRemoto())

        assertEquals(0, registro.id)
        assertEquals(0, registro.bitacoraId)
        assertEquals(0, registro.estudianteId)
        assertEquals("", registro.fechaClase)
        assertEquals("", registro.contenido)
        assertEquals("", registro.actividad)
        assertEquals("", registro.aprendizaje)
        assertEquals("", registro.dudas)
        assertEquals("", registro.fotoUri)
        assertEquals("", registro.enlace)
        assertEquals(EstadoEnvio.BORRADOR, registro.estadoEnvio)
        assertEquals(0L, registro.fechaCreacion)
        assertEquals(0L, registro.fechaEnvio)
    }

    @Test
    fun aRegistro_conDatos_copiaTodosLosCampos() {
        val remoto = RegistroRemoto(
            id = 1, bitacoraId = 1, estudianteId = 3, fechaClase = "05/10/2026",
            contenido = "Fracciones equivalentes", actividad = "Ejercicios en parejas",
            aprendizaje = "Fracciones equivalentes", dudas = "Simplificar",
            fotoUri = "content://foto", enlace = "https://www.duoc.cl",
            estadoEnvio = EstadoEnvio.ENVIADO, fechaCreacion = 100L, fechaEnvio = 200L
        )

        val registro = MapeoRemoto.aRegistro(remoto)

        assertEquals(1, registro.id)
        assertEquals(1, registro.bitacoraId)
        assertEquals(3, registro.estudianteId)
        assertEquals("05/10/2026", registro.fechaClase)
        assertEquals("Fracciones equivalentes", registro.contenido)
        assertEquals("Ejercicios en parejas", registro.actividad)
        assertEquals("Fracciones equivalentes", registro.aprendizaje)
        assertEquals("Simplificar", registro.dudas)
        assertEquals("content://foto", registro.fotoUri)
        assertEquals("https://www.duoc.cl", registro.enlace)
        assertEquals(EstadoEnvio.ENVIADO, registro.estadoEnvio)
        assertEquals(100L, registro.fechaCreacion)
        assertEquals(200L, registro.fechaEnvio)
    }

    @Test
    fun aRegistroRemoto_idCero_enviaIdNull() {
        val nuevo = Registro(
            bitacoraId = 1, estudianteId = 3, fechaClase = "20/10/2026",
            contenido = "Comparación de fracciones", actividad = "", aprendizaje = "",
            fechaCreacion = 0L
        )

        val remoto = MapeoRemoto.aRegistroRemoto(nuevo)

        assertNull(remoto.id)
        assertEquals(3, remoto.estudianteId)
        assertEquals(EstadoEnvio.BORRADOR, remoto.estadoEnvio)
    }

    @Test
    fun aRegistroRemoto_conId_enviaElMismoId() {
        val existente = Registro(
            id = 3, bitacoraId = 1, estudianteId = 3, fechaClase = "20/10/2026",
            contenido = "Comparación de fracciones", actividad = "a", aprendizaje = "b",
            fechaCreacion = 0L
        )

        assertEquals(3, MapeoRemoto.aRegistroRemoto(existente).id)
    }

    // ---------- Retroalimentación ----------
    @Test
    fun aRetroalimentacion_camposNull_usaValoresPorDefecto() {
        val retro = MapeoRemoto.aRetroalimentacion(RetroalimentacionRemota())

        assertEquals(0, retro.id)
        assertEquals(0, retro.registroId)
        assertEquals(0, retro.docenteId)
        assertEquals("", retro.comentario)
        assertEquals(0L, retro.fecha)
    }

    @Test
    fun aRetroalimentacion_conDatos_copiaTodosLosCampos() {
        val retro = MapeoRemoto.aRetroalimentacion(
            RetroalimentacionRemota(id = 2, registroId = 3, docenteId = 1, comentario = "Buen avance", fecha = 500L)
        )

        assertEquals(2, retro.id)
        assertEquals(3, retro.registroId)
        assertEquals(1, retro.docenteId)
        assertEquals("Buen avance", retro.comentario)
        assertEquals(500L, retro.fecha)
    }

    // ---------- ms-usuarios ----------
    @Test
    fun aUsuario_camposNull_usaValoresPorDefectoYSinContrasena() {
        val usuario = MapeoRemoto.aUsuario(UsuarioRemoto())

        assertEquals(0, usuario.id)
        assertEquals("", usuario.nombre)
        assertEquals("", usuario.correo)
        assertEquals("", usuario.password)   // el backend nunca manda la contraseña
        assertEquals("", usuario.perfil)
        assertNull(usuario.cursoId)          // los docentes no tienen curso
    }

    @Test
    fun aUsuario_estudianteConCurso_copiaTodosLosCampos() {
        val usuario = MapeoRemoto.aUsuario(
            UsuarioRemoto(id = 3, nombre = "Estudiante Uno", correo = "alumno1@escuela.cl",
                perfil = "ESTUDIANTE", cursoId = 1)
        )

        assertEquals(3, usuario.id)
        assertEquals("Estudiante Uno", usuario.nombre)
        assertEquals("alumno1@escuela.cl", usuario.correo)
        assertEquals("ESTUDIANTE", usuario.perfil)
        assertEquals(1, usuario.cursoId)
    }

    @Test
    fun aCurso_y_aAsignatura_camposNull_usanValoresPorDefecto() {
        val curso = MapeoRemoto.aCurso(CursoRemoto())
        val asignatura = MapeoRemoto.aAsignatura(AsignaturaRemota())

        assertEquals(0, curso.id)
        assertEquals("", curso.nombre)
        assertEquals(0, asignatura.id)
        assertEquals("", asignatura.nombre)
    }

    @Test
    fun aCurso_y_aAsignatura_conDatos_copianLosCampos() {
        val curso = MapeoRemoto.aCurso(CursoRemoto(id = 1, nombre = "5°A"))
        val asignatura = MapeoRemoto.aAsignatura(AsignaturaRemota(id = 3, nombre = "Historia"))

        assertEquals(1, curso.id)
        assertEquals("5°A", curso.nombre)
        assertEquals(3, asignatura.id)
        assertEquals("Historia", asignatura.nombre)
    }

    @Test
    fun aAsignacion_camposNull_usaValoresPorDefecto() {
        val asignacion = MapeoRemoto.aAsignacion(AsignacionRemota())

        assertEquals(0, asignacion.id)
        assertEquals(0, asignacion.docenteId)
        assertEquals(0, asignacion.cursoId)
        assertEquals(0, asignacion.asignaturaId)
    }

    @Test
    fun aAsignacion_conDatos_copiaTodosLosCampos() {
        val asignacion = MapeoRemoto.aAsignacion(
            AsignacionRemota(id = 3, docenteId = 2, cursoId = 2, asignaturaId = 3)
        )

        assertEquals(3, asignacion.id)
        assertEquals(2, asignacion.docenteId)
        assertEquals(2, asignacion.cursoId)
        assertEquals(3, asignacion.asignaturaId)
    }
}