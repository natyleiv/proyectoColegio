# Bitácora Escolar

Aplicación móvil Android (Kotlin + Jetpack Compose) para administrar **bitácoras mensuales de aprendizaje**
por curso y asignatura en un establecimiento educacional público (5.º a 8.º básico).
Proyecto de la **Evaluación Parcial 2** de *DSY1105 – Desarrollo de Aplicaciones Móviles* (Duoc UC).

> Todo el contenido (usuarios, cursos, bitácoras y registros) es **ficticio**. No se usan datos reales de estudiantes.

## Integrantes

| Nombre | Rol en el equipo |
|---|---|
| [Nombre y apellido integrante 1] | [por completar] |
| [Nombre y apellido integrante 2] | [por completar] |

- Equipo: [N.º de equipo]
- Planificación en Trello: [pegar enlace del tablero]

## Problema que resuelve

Los estudiantes registran lo aprendido en cuadernos, guías y fotos sueltas, y el docente no tiene un lugar
único para ver los aportes, dar retroalimentación e identificar quién no ha participado.
La app centraliza la **bitácora mensual** y mantiene cada registro privado entre el estudiante y su docente.

## Funcionalidades implementadas

**Docente**
- Inicio de sesión con cuenta ficticia y consulta de sus cursos y asignaturas asignados.
- Crear bitácora mensual (curso, asignatura, mes, año, título, propósito, instrucciones y preguntas orientadoras).
- Publicar y cerrar una bitácora.
- Ver los aportes enviados, con filtros por estado (todos, pendientes, revisados) y búsqueda por estudiante o fecha.
- Ver indicadores de participación (participaron / total, sin participación, aportes revisados y pendientes).
- Revisar un aporte (texto, foto y enlace), escribir retroalimentación y marcarlo como revisado.

**Estudiante**
- Inicio de sesión con cuenta ficticia y consulta de las bitácoras activas de su curso.
- Crear un registro de clase (fecha, contenido, actividad, aprendizaje, dudas y enlace).
- Guardar como **borrador** y seguir editándolo, o **enviar** al docente.
- Adjuntar una foto desde la **galería** (Photo Picker) o con la **cámara**.
- Consultar su historial, el estado de cada aporte (borrador / enviado / pendiente / revisado) y la retroalimentación.

**Seguridad (control de acceso)**
- Cada pantalla valida el perfil (docente / estudiante) antes de mostrarse.
- Las consultas a la base de datos filtran por docente o estudiante: un estudiante solo ve sus registros y un docente solo ve
  las bitácoras de sus cursos asignados.

## Requisitos de la Evaluación Parcial 2 y dónde están

| Requisito | Dónde se cumple |
|---|---|
| Interfaz organizada y navegación clara | `ui/navegacion/Navegacion.kt`, `ui/componentes/PantallaBase.kt` (Scaffold + TopAppBar) |
| Formularios validados con íconos y mensajes | `ui/componentes/CampoFormulario.kt`; formularios: login, nueva bitácora, registro de clase, retroalimentación |
| Validaciones desde la lógica | `util/Validaciones.kt` llamadas desde los `ViewModel` |
| Animaciones funcionales | `AnimatedVisibility`, `animateColorAsState`, `animateFloatAsState` (ver `LoginScreen`, `EtiquetaEstado`, `SelectorEvidencia`, `DetalleBitacoraScreen`) |
| Estructura modular, MVVM y persistencia local | paquetes `model`, `data` (Room), `ui`, `util`; cada pantalla con `Screen`, `ViewModel` y `UiState` |
| Dos recursos nativos | Photo Picker (galería) y cámara con `FileProvider` (`ui/componentes/SelectorEvidencia.kt`, `util/ImagenUtils.kt`) |
| GitHub + Trello | Este repositorio y el tablero de Trello indicado arriba |

## Alcance de este avance (no implementado todavía)

Este es un avance intermedio hacia la Evaluación Final Transversal. Quedan **pendientes**: informe mensual en PDF,
adjuntar archivos (solo se adjuntan fotos), recordatorios internos y el historial de bitácoras anteriores por mes/semestre.

## Tecnologías

Kotlin 2.2.10 · Jetpack Compose (BOM 2026.02.01) · Material 3 · Navigation Compose 2.9.8 · MVVM con `ViewModel`,
`UiState` y `StateFlow` · Room 3.0.3 (KSP) · Coil 3.3.0.
`minSdk 24`, `targetSdk 36`. Dispositivo de prueba: Pixel 8, API 36 (Android 16).

## Estructura del proyecto

```
app/src/main/java/cl/duoc/bitacora/
├── model/        Entidades Room (Usuario, Asignacion, Bitacora, Registro) y constantes de estado
├── data/         DAO, Repository, AppDatabase y datos de prueba
├── ui/
│   ├── login/, docente/..., estudiante/...   Screen + ViewModel + UiState por pantalla
│   ├── componentes/                          Componentes reutilizables
│   ├── navegacion/                           NavHost y control de acceso por perfil
│   └── theme/                                Tema Material 3
└── util/         Validaciones, fechas, sesión e imágenes
```

## Cómo ejecutar

1. Clonar el repositorio y abrirlo en **Android Studio**.
2. Si faltan `gradlew`, `gradlew.bat` y `gradle/wrapper/gradle-wrapper.jar`, copiarlos desde cualquier proyecto
   *Empty Activity* generado por Android Studio y luego hacer **Sync Project with Gradle Files**.
3. Crear un dispositivo virtual: **Pixel 8, API 36 (Android 16)**.
4. Ejecutar la app (`Run 'app'`).
5. Ingresar con una cuenta de prueba (se cargan solas en la primera ejecución):

| Perfil | Correo | Contraseña |
|---|---|---|
| Docente (5°A Matemática y Lenguaje) | docente1@escuela.cl | docente123 |
| Docente (6°B Historia) | docente2@escuela.cl | docente123 |
| Estudiante 5°A | alumno1@escuela.cl … alumno4@escuela.cl | alumno123 |
| Estudiante 6°B | alumno5@escuela.cl | alumno123 |

Para empezar de cero (borrar datos de prueba): desinstalar la app del emulador y volver a ejecutarla.
