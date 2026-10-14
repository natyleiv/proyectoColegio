# Plan de trabajo en pareja (Trello + GitHub)

> Este documento es una **guía** para que ustedes registren trabajo REAL. La pauta pide actividad real y distribuida
> entre ambos integrantes; no se puede reemplazar con commits o tareas creados al final. Cada persona debe hacer sus
> propios commits desde su cuenta y mover sus propias tarjetas.

## Tablero de Trello

Listas: **Por hacer · En progreso · En revisión · Hecho**
Etiquetas: `Interfaz`, `Validaciones`, `Datos (Room)`, `Recursos nativos`, `Animaciones`, `Documentación`.
Cada tarjeta lleva un responsable (Integrante A o B) y una fecha. Pueden usar el listado de abajo como tarjetas.

## Reparto sugerido y commits

Formato de commit: `tipo(ámbito): descripción técnica` (feat, fix, refactor, docs, chore).

### Etapa 1 · Base del proyecto
| Tarjeta | Responsable | Commit sugerido |
|---|---|---|
| Crear proyecto y configurar Gradle (Room, KSP, Coil, Navigation) | A | `chore(build): configurar dependencias de Room, KSP, Navigation y Coil` |
| Tema Material 3 y colores de estado | B | `feat(ui): agregar tema Material 3 y colores de estados` |
| Entidades y constantes del modelo | A | `feat(model): crear entidades Usuario, Asignacion, Bitacora y Registro` |

### Etapa 2 · Persistencia (Room)
| Tarjeta | Responsable | Commit sugerido |
|---|---|---|
| DAO de usuarios y asignaciones | B | `feat(data): crear UsuarioDao y AsignacionDao` |
| DAO de bitácoras y registros con filtros por perfil | A | `feat(data): crear BitacoraDao y RegistroDao con consultas filtradas por docente y estudiante` |
| AppDatabase, repositorios y datos de prueba | B | `feat(data): agregar AppDatabase singleton, repositorios y datos ficticios` |

### Etapa 3 · Validaciones y componentes
| Tarjeta | Responsable | Commit sugerido |
|---|---|---|
| Funciones de validación | A | `feat(util): crear validaciones de correo, contraseña, fecha, mes, año y enlace` |
| CampoFormulario con íconos y mensajes de error | B | `feat(ui): crear CampoFormulario con ícono de error y supportingText` |
| PantallaBase y etiquetas de estado | A | `feat(ui): crear PantallaBase (Scaffold y TopAppBar) y etiquetas de estado animadas` |

### Etapa 4 · Pantallas
| Tarjeta | Responsable | Commit sugerido |
|---|---|---|
| Login con ViewModel y UiState | A | `feat(login): implementar login con validaciones y control de credenciales` |
| Navegación y control de acceso por perfil | B | `feat(nav): configurar NavHost y control de acceso por perfil` |
| Inicio y creación de bitácora (docente) | B | `feat(docente): agregar inicio y formulario de nueva bitácora` |
| Detalle de bitácora, publicar/cerrar e indicadores | A | `feat(docente): agregar detalle, publicar, cerrar, filtros e indicadores de participación` |
| Revisión y retroalimentación | B | `feat(docente): agregar revisión de aportes con comentario` |
| Inicio e historial del estudiante | A | `feat(estudiante): agregar inicio e historial de aportes` |
| Formulario de registro con borrador y envío | B | `feat(estudiante): agregar formulario de registro con borrador y envío` |

### Etapa 5 · Recursos nativos y animaciones
| Tarjeta | Responsable | Commit sugerido |
|---|---|---|
| Photo Picker (galería) y límite de 5 MB | A | `feat(recursos): adjuntar foto desde galería con Photo Picker` |
| Cámara con FileProvider | B | `feat(recursos): capturar foto con cámara usando FileProvider` |
| Animaciones (errores, foto, progreso, colores) | A | `feat(ui): agregar animaciones con AnimatedVisibility y animate*AsState` |

### Etapa 6 · Cierre
| Tarjeta | Responsable | Commit sugerido |
|---|---|---|
| README con integrantes y pasos de ejecución | B | `docs: completar README con funcionalidades y pasos de ejecución` |
| Pruebas manuales y correcciones | A y B | `fix(...): corregir [problema real encontrado]` |

## Antes de entregar
- Repositorio **público** y acceso otorgado al docente y a ambos integrantes.
- **No hacer commits después de la fecha de entrega** (cualquier cambio posterior = nota 1.0).
- Subir el código fuente al enlace de AVA grupal (un solo integrante) y activar el AVA individual (nombre, apellido, N.º de equipo y nombre de la app).
- Completar nombres y enlace de Trello en el README.
