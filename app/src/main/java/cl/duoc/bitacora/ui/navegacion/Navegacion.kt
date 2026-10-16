package cl.duoc.bitacora.ui.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.bitacora.model.Perfil
import cl.duoc.bitacora.ui.docente.crear.CrearBitacoraScreen
import cl.duoc.bitacora.ui.docente.detalle.DetalleBitacoraScreen
import cl.duoc.bitacora.ui.docente.inicio.DocenteInicioScreen
import cl.duoc.bitacora.ui.docente.revision.RevisarRegistroScreen
import cl.duoc.bitacora.ui.estudiante.detalle.DetalleRegistroScreen
import cl.duoc.bitacora.ui.estudiante.historial.HistorialScreen
import cl.duoc.bitacora.ui.estudiante.inicio.EstudianteInicioScreen
import cl.duoc.bitacora.ui.estudiante.registro.NuevoRegistroScreen
import cl.duoc.bitacora.ui.login.LoginScreen
import cl.duoc.bitacora.util.Sesion

// Rutas de la aplicación (clase 03)
private const val LOGIN = "login"
private const val DOCENTE_INICIO = "docente_inicio"
private const val DOCENTE_CREAR = "docente_crear"
private const val DOCENTE_EDITAR = "docente_editar"
private const val DOCENTE_DETALLE = "docente_detalle"
private const val DOCENTE_REVISAR = "docente_revisar"
private const val ESTUDIANTE_INICIO = "estudiante_inicio"
private const val ESTUDIANTE_REGISTRO = "estudiante_registro"
private const val ESTUDIANTE_HISTORIAL = "estudiante_historial"
private const val ESTUDIANTE_DETALLE = "estudiante_detalle"

// CONTROL DE ACCESO POR PERFIL: si el usuario no tiene el perfil que exige la pantalla,
// no se muestra el contenido y se envía al login.
@Composable
private fun AccesoPorPerfil(
    perfil: String,
    onSinAcceso: () -> Unit,
    contenido: @Composable () -> Unit
) {
    if (Sesion.usuario?.perfil == perfil) {
        contenido()
    } else {
        LaunchedEffect(Unit) {
            onSinAcceso()
        }
    }
}

@Composable
fun Navegacion() {
    val navController = rememberNavController()

    // Cierra la sesión y vuelve al login borrando el historial de pantallas
    val cerrarSesion = {
        Sesion.cerrar()
        navController.navigate(LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    NavHost(
        navController = navController,
        startDestination = LOGIN
    ) {
        composable(LOGIN) {
            LoginScreen(
                onLoginDocente = {
                    navController.navigate(DOCENTE_INICIO) {
                        popUpTo(LOGIN) { inclusive = true }
                    }
                },
                onLoginEstudiante = {
                    navController.navigate(ESTUDIANTE_INICIO) {
                        popUpTo(LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ---------------- DOCENTE ----------------
        composable(DOCENTE_INICIO) {
            AccesoPorPerfil(Perfil.DOCENTE, onSinAcceso = { cerrarSesion() }) {
                DocenteInicioScreen(
                    onCrearBitacora = { navController.navigate(DOCENTE_CREAR) },
                    onAbrirBitacora = { id ->
                        Sesion.bitacoraId = id
                        navController.navigate(DOCENTE_DETALLE)
                    },
                    onCerrarSesion = { cerrarSesion() }
                )
            }
        }
        composable(DOCENTE_CREAR) {
            AccesoPorPerfil(Perfil.DOCENTE, onSinAcceso = { cerrarSesion() }) {
                // Modo "crear": formulario vacío
                Sesion.modoEdicion = false
                CrearBitacoraScreen(onVolver = { navController.popBackStack() })
            }
        }
        composable(DOCENTE_EDITAR) {
            AccesoPorPerfil(Perfil.DOCENTE, onSinAcceso = { cerrarSesion() }) {
                // Mismo formulario, pero cargado con la bitácora Sesion.bitacoraId
                Sesion.modoEdicion = true
                CrearBitacoraScreen(onVolver = { navController.popBackStack() })
            }
        }
        composable(DOCENTE_DETALLE) {
            AccesoPorPerfil(Perfil.DOCENTE, onSinAcceso = { cerrarSesion() }) {
                DetalleBitacoraScreen(
                    onVolver = { navController.popBackStack() },
                    onAbrirRegistro = { navController.navigate(DOCENTE_REVISAR) },
                    onEditar = { navController.navigate(DOCENTE_EDITAR) }
                )
            }
        }
        composable(DOCENTE_REVISAR) {
            AccesoPorPerfil(Perfil.DOCENTE, onSinAcceso = { cerrarSesion() }) {
                RevisarRegistroScreen(onVolver = { navController.popBackStack() })
            }
        }

        // ---------------- ESTUDIANTE ----------------
        composable(ESTUDIANTE_INICIO) {
            AccesoPorPerfil(Perfil.ESTUDIANTE, onSinAcceso = { cerrarSesion() }) {
                EstudianteInicioScreen(
                    onNuevoRegistro = { bitacoraId ->
                        Sesion.bitacoraId = bitacoraId
                        Sesion.registroId = 0          // 0 = registro nuevo
                        navController.navigate(ESTUDIANTE_REGISTRO)
                    },
                    onVerHistorial = { navController.navigate(ESTUDIANTE_HISTORIAL) },
                    onCerrarSesion = { cerrarSesion() }
                )
            }
        }
        composable(ESTUDIANTE_REGISTRO) {
            AccesoPorPerfil(Perfil.ESTUDIANTE, onSinAcceso = { cerrarSesion() }) {
                NuevoRegistroScreen(onVolver = { navController.popBackStack() })
            }
        }
        composable(ESTUDIANTE_HISTORIAL) {
            AccesoPorPerfil(Perfil.ESTUDIANTE, onSinAcceso = { cerrarSesion() }) {
                HistorialScreen(
                    onVolver = { navController.popBackStack() },
                    onEditarBorrador = { navController.navigate(ESTUDIANTE_REGISTRO) },
                    onVerRegistro = { navController.navigate(ESTUDIANTE_DETALLE) }
                )
            }
        }
        composable(ESTUDIANTE_DETALLE) {
            AccesoPorPerfil(Perfil.ESTUDIANTE, onSinAcceso = { cerrarSesion() }) {
                DetalleRegistroScreen(onVolver = { navController.popBackStack() })
            }
        }
    }
}
