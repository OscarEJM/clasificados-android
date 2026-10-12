# Clasificados

Aplicación móvil Android de anuncios clasificados. Una persona con cuenta puede consultar anuncios organizados por categorías y publicar, editar o eliminar sus propios anuncios. Los anuncios se comparten entre todos los usuarios a través de servicios en la nube.

Proyecto del curso **Desarrollo de Aplicaciones para Dispositivos Móviles** – Universidad del Valle, sede Tuluá – Tecnología en Desarrollo de Software, 2026.

| | |
|---|---|
| **Estudiante** | Oscar Eduardo Jimenez Morales – 2264419-2724 |
| **Docente** | Miguel Ángel Askar Rodríguez |
| **Entrega final** | 17 de diciembre de 2026 |

## Funcionalidades

- Registro, inicio y cierre de sesión con correo y contraseña. La sesión se conserva al cerrar la aplicación.
- Exploración de 13 categorías en tarjetas de dos por renglón.
- Listado y detalle de anuncios publicados por cualquier usuario.
- Búsqueda por título y filtros por categoría, ciudad, precio y fecha de publicación.
- Publicación de anuncios en cuatro pasos (categoría, detalles, fotos y ubicación y contacto), con validación de cada dato.
- Gestión de mis anuncios: editar y eliminar.
- Tema claro y oscuro, guardado en el dispositivo.

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose (Material 3) |
| Arquitectura | MVVM (Modelo – Vista – ViewModel) |
| Autenticación | Firebase Authentication (correo y contraseña) |
| Datos de los anuncios | Cloud Firestore |
| Fotos de los anuncios | Supabase Storage |
| Preferencia de tema | SharedPreferences |
| Versión mínima | Android 8.0 (API 26) |

## Documentación del proyecto

- **Especificación de Requisitos de Software** según IEEE 830: 34 requisitos funcionales y 19 no funcionales.
- **Historias de usuario**: 8 épicas, 22 historias y plan de sprints.
- **Diseño en Figma**: «Clasificados – Diseño Móvil», en modo claro y oscuro.
- **Tablero Kanban** en ClickUp, organizado por sprints.

## Plan de trabajo

| Sprint | Fechas | Hito |
|---|---|---|
| Sprint 0 | 12 – 18 oct | 15 oct: Entrega 1 (diseño) |
| Sprint 1 | 19 oct – 1 nov | 29 oct: revisión del estado |
| Sprint 2 | 2 – 15 nov | — |
| Sprint 3 | 16 – 29 nov | 19 nov: revisión del estado |
| Sprint 4 | 30 nov – 13 dic | 3 dic: revisión · 10 dic: Segunda entrega |
| Cierre | 14 – 17 dic | 17 dic: Entrega final |

## Cómo ejecutar el proyecto

> El código fuente se agregará a partir del Sprint 1.

1. Clonar el repositorio y abrirlo en Android Studio.
2. Agregar el archivo `google-services.json` del proyecto de Firebase en la carpeta `app/`. No se incluye en el repositorio por seguridad.
3. Agregar en `local.properties` las llaves de Supabase (`SUPABASE_URL` y `SUPABASE_ANON_KEY`). Este archivo tampoco se versiona.
4. Ejecutar en un emulador o dispositivo con Android 8.0 o superior.

## Estado

🟡 En desarrollo – Sprint 0 (diseño y documentación).
