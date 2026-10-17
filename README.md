# Bitácora Escolar

Aplicación móvil Android (Kotlin + Jetpack Compose) para administrar **bitácoras mensuales de aprendizaje**
por curso y asignatura en un establecimiento educacional público (5.º a 8.º básico).
Proyecto de la **Evaluación Parcial 2** de *DSY1105 – Desarrollo de Aplicaciones Móviles* (Duoc UC).

> Todo el contenido (usuarios, cursos, bitácoras y registros) es **ficticio**. No se usan datos reales de estudiantes.

## Integrantes

- Natalia Millaray Leiva Millaqueo
- Juan Pablo Marambio Zagal

- **Equipo:** 1
- **Planificación en Trello:** _(enlace pendiente)_

## Problema que resuelve

Los estudiantes registran lo aprendido en cuadernos, guías y fotos sueltas, y el docente no tiene un lugar
único para ver los aportes, dar retroalimentación e identificar quién no ha participado.
La app centraliza la **bitácora mensual** y mantiene cada registro privado entre el estudiante y su docente.

## Funcionalidades implementadas

**Docente**
- Inicio de sesión con cuenta ficticia y consulta de sus cursos y asignaturas asignados.
- Crear bitácora mensual (curso, asignatura, mes y año elegidos en un **calendario**, título, propósito, instrucciones y preguntas orientadoras).
- **Editar** una bitácora mientras está en borrador (formulario con los datos cargados).
- Publicar y cerrar una bitácora.
- Ver los aportes enviados, con filtros por estado (todos, pendientes, revisados), búsqueda por nombre del estudiante y filtro por fecha de clase elegida en un **calendario**.
- Al abrir un aporte ya revisado, el botón cambia a **"Actualizar comentario"**.
- Ver indicadores de participación (participaron / total, sin participación, aportes revisados y pendientes).
- Revisar un aporte (texto, foto y enlace), escribir retroalimentación y marcarlo como revisado.

**Estudiante**
- Inicio de sesión con cuenta ficticia y consulta de las bitácoras activas de su curso.
- Crear un registro de clase (fecha elegida en un **calendario** —los días futuros están bloqueados y la fecha debe pertenecer al mes de la bitácora—, contenido, actividad, aprendizaje, dudas y enlace).
- Guardar como **borrador** y seguir editándolo, o **enviar** al docente.
- Adjuntar una foto desde la **galería** (Photo Picker) o con la **cámara**.
- Consultar su historial, el estado de cada aporte (borrador / enviado / pendiente / revisado) y la retroalimentación.

**Mensajes y colores**
- Cada operación muestra un mensaje en un Snackbar: **verde** (éxito), **ámbar** (advertencia) o **rojo** (error de la operación,
  por ejemplo si no se pudo guardar). Los colores están centralizados en `ui/theme/Color.kt` (`ColorExito`, `ColorAdvertencia`, `ColorError`).
- El contenido se adapta a pantallas anchas (tablet / horizontal): queda centrado con un ancho máximo (`PantallaBase`).
- Ícono propio de la aplicación (cuaderno con check) en `res/drawable/ic_launcher_*.xml` y `res/mipmap-*`.

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
| Animaciones funcionales | `AnimatedVisibility`, `Crossfade`, `animateColorAsState`, `animateFloatAsState` (ver `CampoFormulario`, `SelectorEvidencia`, `EtiquetaEstado`, `LoginScreen`, `DetalleBitacoraScreen`) |
| Estructura modular, MVVM y persistencia local | paquetes `model`, `data` (Room), `ui`, `util`; cada pantalla con `Screen`, `ViewModel` y `UiState` |
| Dos recursos nativos | Photo Picker (galería) y cámara con `FileProvider` (`ui/componentes/SelectorEvidencia.kt`, `util/ImagenUtils.kt`) |
| Modelo de datos relacional con claves primarias, foráneas y relaciones | 8 tablas normalizadas (3FN), ver sección siguiente; `model/` y `data/AppDatabase.kt` |
| Mensajes de éxito **y** de error, colores semánticos | `ui/componentes/PantallaBase.kt` (`MensajeSesion`), `Sesion.exito/advertencia/error`, `ui/theme/Color.kt` |
| Editar bitácoras | `ui/docente/crear/` (modo edición) y botón "Editar bitácora" en `DetalleBitacoraScreen` |
| Diseño adaptable a distintos tamaños de pantalla | `PantallaBase` (contenido centrado, ancho máximo 720 dp) |
| GitHub + Trello | Este repositorio y el tablero de Trello del equipo |

## Modelo de datos (normalizado, 3FN)

```
cursos 1───N usuarios (estudiantes: cursoId)
cursos 1───N asignaciones N───1 asignaturas
usuarios(docente) 1───N asignaciones
asignaciones 1───N bitacoras 1───N preguntas_orientadoras
bitacoras 1───N registros N───1 usuarios(estudiante)
registros 1───0..1 retroalimentaciones N───1 usuarios(docente)
```

| Tabla | Clave primaria | Claves foráneas |
|---|---|---|
| `cursos` | id | — |
| `asignaturas` | id | — |
| `usuarios` | id | cursoId → cursos (solo estudiantes) |
| `asignaciones` | id | docenteId → usuarios, cursoId → cursos, asignaturaId → asignaturas |
| `bitacoras` | id | asignacionId → asignaciones |
| `preguntas_orientadoras` | id | bitacoraId → bitacoras |
| `registros` | id | bitacoraId → bitacoras, estudianteId → usuarios |
| `retroalimentaciones` | id | registroId → registros (único), docenteId → usuarios |

Por qué está normalizado: el nombre del curso, de la asignatura, del docente y del estudiante se guardan **una sola vez** y las demás
tablas los referencian por id; las preguntas orientadoras son filas propias (una bitácora tiene muchas); y el estado
*pendiente / revisado* de un registro se deduce de si existe su retroalimentación, así no se guarda dos veces.
Las pantallas reciben los nombres ya resueltos mediante consultas con `JOIN` (`data/Consultas.kt`).

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
├── model/        Entidades Room (Curso, Asignatura, Usuario, Asignacion, Bitacora, PreguntaOrientadora, Registro, Retroalimentacion) y constantes
├── data/         DAO, Repository, consultas con JOIN (Consultas.kt), AppDatabase y datos de prueba
├── ui/
│   ├── login/, docente/..., estudiante/...   Screen + ViewModel + UiState por pantalla
│   ├── componentes/                          Componentes reutilizables
│   ├── navegacion/                           NavHost y control de acceso por perfil
│   └── theme/                                Tema Material 3
└── util/         Validaciones, fechas, sesión e imágenes
```

## Cómo ejecutar

1. Clonar el repositorio y abrirlo en **Android Studio**.
2. Esperar a que termine **Sync Project with Gradle Files**.
3. Crear un dispositivo virtual: **Pixel 8, API 36 (Android 16)**.
4. Ejecutar la app (`Run 'app'`).
5. Ingresar con una cuenta de prueba (se cargan solas en la primera ejecución):

| Perfil | Correo | Contraseña |
|---|---|---|
| Docente (5°A Matemática y Lenguaje) | docente1@escuela.cl | docente123 |
| Docente (6°B Historia) | docente2@escuela.cl | docente123 |
| Estudiante 5°A | alumno1@escuela.cl … alumno4@escuela.cl | alumno123 |
| Estudiante 6°B | alumno5@escuela.cl | alumno123 |

**Importante:** el esquema de la base cambió respecto de versiones anteriores (ahora el archivo es `bitacora_v2.db`). Si ya tenías la app instalada, desinstálala del emulador antes de ejecutar esta versión.

Para empezar de cero (borrar datos de prueba): desinstalar la app del emulador y volver a ejecutarla.
