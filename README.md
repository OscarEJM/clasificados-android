# Clasificados

Aplicación móvil Android de anuncios clasificados. Una persona con cuenta puede consultar anuncios organizados por categorías y publicar, editar o eliminar sus propios anuncios. Los anuncios se comparten entre todos los usuarios a través de servicios en la nube.

Proyecto del curso **Desarrollo de Aplicaciones para Dispositivos Móviles** – Universidad del Valle, sede Tuluá – Tecnología en Desarrollo de Software, 2026.

| | |
|---|---|
| **Estudiante** | Oscar Eduardo Jimenez Morales – 2264419-2724 |
| **Docente** | Miguel Ángel Askar Rodríguez |
| **Entrega final** | 17 de diciembre de 2026 |

## Estado

🟢 **Sprint 3 terminado – versión estable v3.0.** Funcionan la cuenta, la consulta de anuncios, la búsqueda, la publicación completa y las reglas de seguridad. En el Sprint 4 siguen los filtros, Mis anuncios (editar y eliminar), el aviso sin conexión y las pruebas unitarias finales.

## Funcionalidades

| Funcionalidad | Historias | Estado |
|---|---|---|
| Registro, inicio y cierre de sesión con correo y contraseña; la sesión se conserva al cerrar la app | HU-03, HU-04, HU-05, HU-10 | ✅ Lista |
| Exploración de las 13 categorías en tarjetas de dos por renglón | HU-06 | ✅ Lista |
| Listado de los anuncios de una categoría, del más reciente al más antiguo | HU-07 | ✅ Lista |
| Detalle del anuncio: fotos deslizables, precio, ciudad, tiempo transcurrido y contacto como texto | HU-08 | ✅ Lista |
| Perfil en Ajustes (nombre, correo e iniciales) | HU-09 | ✅ Lista |
| Tema claro y oscuro, guardado en el teléfono | HU-11 | ✅ Lista |
| Publicar un anuncio en cuatro pasos (categoría, detalles, fotos y ubicación y contacto) con validación de cada dato y pantalla de confirmación | HU-12, HU-13, HU-14 | ✅ Lista |
| Búsqueda por título desde Inicio y dentro de cada categoría, sin distinguir mayúsculas | HU-15 | ✅ Lista |
| Reglas de seguridad: solo usuarios con sesión ven los datos y solo el autor modifica su anuncio | HU-16 | ✅ Lista |
| Filtros por categoría, ciudad, precio y fecha, y orden del listado | HU-17 | ⏳ Sprint 4 |
| Mis anuncios, editar y eliminar | HU-18, HU-19, HU-20 | ⏳ Sprint 4 |
| Aviso sin conexión a internet | HU-21 | ⏳ Sprint 4 |
| Pruebas unitarias finales | HU-22 | ⏳ Sprint 4 |

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin 2.4 |
| Interfaz | Jetpack Compose con Material 3 |
| Arquitectura | MVVM: pantalla → ViewModel (estado con `StateFlow`) → repositorio → Firebase / Supabase |
| Navegación | Navigation Compose |
| Autenticación | Firebase Authentication (correo y contraseña) |
| Datos de los anuncios | Cloud Firestore, con caché solo en memoria |
| Fotos de los anuncios | Supabase Storage (bucket privado; Firebase como proveedor de autenticación) |
| Carga de imágenes | Coil 3 |
| Preferencia de tema | SharedPreferences (lo único que se guarda en el teléfono) |
| Pruebas | JUnit y pruebas de Compose en el emulador |
| Versión mínima | Android 8.0 (API 26); compila para Android 17 (API 37) |

Todos los servicios se usan en sus planes gratuitos (Firebase Spark y Supabase Free).

## Estructura del código

```
app/src/main/java/com/univalle/clasificados/
├── ui/          pantallas (@Composable), componentes reutilizables, navegación y tema
├── viewmodel/   un ViewModel por pantalla
├── data/        repositorios (anuncios, fotos, sesión, galería y preferencias) y clientes de la nube
├── model/       clases de datos: Anuncio, Usuario, Categoria y Ciudad
└── util/        validaciones, búsqueda, formato de precios y tiempo transcurrido
```

- `app/src/test/`: pruebas unitarias (sin emulador).
- `app/src/androidTest/`: pruebas de cada historia en el emulador.
- `reglas/`: copia de las reglas de seguridad de Firestore y de Supabase Storage, con capturas del simulador de reglas como evidencia.

## Cómo ejecutar el proyecto

1. Clonar el repositorio y abrirlo en Android Studio.
2. Agregar el archivo `google-services.json` del proyecto de Firebase en la carpeta `app/`. No se incluye en el repositorio por seguridad.
3. Agregar en `local.properties` las llaves de Supabase: `SUPABASE_URL` y `SUPABASE_KEY`. Este archivo tampoco se versiona.
4. Ejecutar en un emulador o dispositivo con Android 8.0 o superior.

## Pruebas

- **Sin emulador:** `./gradlew testDebugUnitTest`
- **En el emulador:** `./gradlew connectedDebugAndroidTest`. Usan cuentas de prueba que se escriben en `local.properties` (`PRUEBA_CORREO`, `PRUEBA_CONTRASENA`, `PRUEBA2_CORREO` y `PRUEBA2_CONTRASENA`); si no están, esas pruebas se saltan.

## Forma de trabajo

- Una rama por historia de usuario, unida a `desarrollo` con un pull request cuando todos sus criterios de aceptación pasan.
- Al cerrar cada sprint, `desarrollo` se une a `main` y se marca una versión estable: **v1.0** (Sprint 1), **v2.0** (Sprint 2) y **v3.0** (Sprint 3).
- Reglas para los agentes de IA que apoyan el desarrollo: [`GUIA_DESARROLLO.md`](GUIA_DESARROLLO.md).

## Documentación del proyecto

- **Especificación de Requisitos de Software** según IEEE 830: 34 requisitos funcionales y 19 no funcionales.
- **Historias de usuario**: 8 épicas, 22 historias y plan de sprints.
- **Diseño en Figma**: «Clasificados – Diseño Móvil», en modo claro y oscuro.
- **Tablero Kanban** en ClickUp, organizado por sprints.

## Plan de trabajo

| Sprint | Fechas | Hito | Estado |
|---|---|---|---|
| Sprint 0 | 12 – 18 oct | 15 oct: Entrega 1 (diseño) | ✅ Terminado |
| Sprint 1 | 19 oct – 1 nov | 29 oct: revisión del estado | ✅ Terminado · v1.0 |
| Sprint 2 | 2 – 15 nov | — | ✅ Terminado · v2.0 |
| Sprint 3 | 16 – 29 nov | 19 nov: revisión del estado | ✅ Terminado · v3.0 |
| Sprint 4 | 30 nov – 13 dic | 3 dic: revisión · 10 dic: Segunda entrega | ⏳ Pendiente |
| Cierre | 14 – 17 dic | 17 dic: Entrega final | ⏳ Pendiente |
