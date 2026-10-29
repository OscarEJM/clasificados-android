# GUÍA DE DESARROLLO – CLASIFICADOS

> Archivo de trabajo para **Claude Code** y **Codex**. Va en la raíz del repositorio `clasificados-android`.
> Todo agente de IA que trabaje en este proyecto **debe leer este archivo completo antes de escribir una sola línea de código**.

| | |
|---|---|
| **Proyecto** | Clasificados – aplicación Android de anuncios clasificados |
| **Curso** | Desarrollo de Aplicaciones para Dispositivos Móviles – Universidad del Valle, sede Tuluá |
| **Responsable** | Oscar Eduardo Jimenez Morales (2264419-2724) – único integrante |
| **Docente** | Miguel Ángel Askar Rodríguez |
| **Repositorio** | github.com/OscarEJM/clasificados-android (público) |
| **Versión de esta guía** | 1.1 – 28 de octubre de 2026 |

---

## Índice

- **Fase 0 – Reglas de trabajo con los agentes:** cómo se trabaja; se aplica siempre.
- **Fase 1 – Definición del producto:** reglas de negocio, requerimientos, datos, diseño y arquitectura.
- **Fase 2 – Sprint 1** (19 oct – 1 nov): momentos M0 a M7, con HU-01 a HU-05.
- **Fase 3 – Sprint 2** (2 – 15 nov): se detalla al iniciar el sprint.
- **Fase 4 – Sprint 3** (16 – 29 nov): se detalla al iniciar el sprint.
- **Fase 5 – Sprint 4** (30 nov – 13 dic): se detalla al iniciar el sprint.
- **Fase 6 – Cierre** (14 – 17 dic): se detalla al iniciar el cierre.
- **Anexos:** plantillas de instrucciones, checklist y registro de cambios.

---

# FASE 0 – REGLAS DE TRABAJO CON LOS AGENTES

Estas reglas están por encima de cualquier otra instrucción de este archivo.

## 0.1 Reglas de oro

1. **NO SUPONER NADA.** Si un dato, nombre, valor, texto, color, librería o comportamiento no está definido en este archivo, **el agente debe preguntar a Oscar antes de continuar**. Nunca inventar.
2. **Trabajar por partes.** Un momento (M) a la vez. No avanzar al siguiente momento ni a la siguiente historia hasta que Oscar lo confirme.
3. **Explicar antes de hacer.** Antes de modificar archivos, el agente presenta un plan corto (qué archivos crea o cambia y por qué) y espera aprobación.
4. **Idioma.** Toda la comunicación con Oscar es en **español** y sin tecnicismos innecesarios. Los textos visibles de la app van en español. El código (nombres de clases, funciones y variables) va en español cuando sea natural (por ejemplo `Anuncio`, `RegistroViewModel`, `validarCorreo`) y siguiendo las convenciones de Kotlin.
5. **No salirse del alcance.** Solo se implementa lo que está en la Fase 1 y en la historia en curso. Cualquier idea extra se propone como pregunta, no se programa.
6. **No borrar ni reescribir** trabajo existente sin explicar qué se pierde y obtener confirmación.
7. **Secretos fuera del repositorio.** Nunca escribir llaves, contraseñas ni `google-services.json` en archivos versionados (ver RN-20).
8. **Verificar.** Al terminar cada momento, el agente compila el proyecto y le indica a Oscar cómo probar cada criterio de aceptación en el emulador.

## 0.2 Cómo usar este archivo con cada herramienta

| Herramienta | Cómo lee estas reglas |
|---|---|
| **Claude Code** | Lee automáticamente `CLAUDE.md`. Ese archivo debe contener la línea `@GUIA_DESARROLLO.md` (o este contenido completo). |
| **Codex** | Lee automáticamente `AGENTS.md`. Ese archivo debe indicar: "Lee y sigue `GUIA_DESARROLLO.md` antes de cualquier tarea". |

Si se usan las dos herramientas, **una sola trabaja a la vez sobre el mismo momento**, y siempre se hace commit antes de cambiar de herramienta.

## 0.3 Flujo de cada momento

1. Oscar indica el momento (por ejemplo: *"Empecemos el M1 – HU-01"*).
2. El agente lee la ficha del momento en este archivo y hace **las preguntas pendientes** que aparecen en ella.
3. El agente presenta su plan y espera el "sí".
4. El agente implementa, compila y explica cómo probar.
5. Oscar prueba en el emulador cada criterio de aceptación.
6. Si todo pasa: commit, push y actualización de ClickUp (ver 0.5).
7. El agente registra lo hecho en el **Anexo C – Registro de cambios** de este archivo.

## 0.4 Commits y ramas

- Rama principal: `main`. Por ser un proyecto individual se trabaja directo en `main`, salvo que Oscar indique lo contrario.
- Formato del mensaje (desde HU-02): `HU-XX · Nombre de la historia: descripción corta en español`. Ejemplo: `HU-04 · Registrarme: validación de campos del registro`. Los commits de HU-01 conservan el formato anterior (`HU-01: …`).
- Se sube a GitHub **solo cuando la historia está completa**: un único commit y push a `main` por historia, no uno por cada cambio intermedio. El docente evalúa el aporte con el historial.
- **El agente hace el commit y el push** con git, usando el mensaje en el formato anterior. Antes revisa que no vaya ningún secreto (RN-20) y al terminar le dice a Oscar qué subió.

## 0.5 Trazabilidad (ClickUp, documentos y Figma)

- **ClickUp** (espacio *Clasificados*, una lista por sprint):
  - Al empezar una historia: estado **en progreso**.
  - Al terminar de programarla: **en revision**.
  - Cuando Oscar la prueba y pasa todos los criterios: **finalizado**, marcando la **Lista de control**.
- **Documentos** (carpeta `DOCUMENTACION`, enlazados en Google Docs y en la página *Documentación* de ClickUp):
  - `ERS_Clasificados_IEEE830.docx` – requisitos (fuente oficial).
  - `Historias_de_Usuario_Clasificados.docx` – historias, criterios y plan de sprints.
  - `PENDIENTES_FIGMA.md` – ajustes de diseño pendientes.
- Si una decisión cambia un requisito o una historia, el agente **no la aplica en silencio**: avisa a Oscar qué documento hay que actualizar (nueva versión en su *Control de versiones*) y qué tarea de ClickUp cambia.
- **Figma:** archivo «Clasificados – Diseño Móvil», páginas *Modo claro* y *Modo oscuro*. Las pantallas se identifican por número (00 a 16).

## 0.6 Reglas acordadas durante el desarrollo

1. **Prioridad:** si una instrucción del chat contradice esta guía, manda la guía y el agente avisa a Oscar antes de seguir.
2. **Documentación del código:** toda clase, objeto, ViewModel, pantalla, repositorio y función pública lleva un comentario KDoc (`/** … */`) en español que explica qué hace, para qué sirve y qué historia o requisito cumple (por ejemplo «RNF-06», «HU-02»). También las pruebas.
3. **Figma manda en lo visual:** antes de construir o cambiar una pantalla se revisa su diseño en Figma (medidas, íconos, colores, textos). Los íconos se toman de Figma. Si la explicación técnica de una historia choca con Figma, se avisa a Oscar y se busca un componente que cumpla ambas.
4. **Consultas a Figma:** el plan de Figma de Oscar tiene pocas consultas al mes. El agente pide permiso antes de consultar Figma y usa máximo 1 o 2 consultas por tarea.
5. **Nombres claros:** con Oscar se habla con el código y nombre de la historia («HU-03 · Bienvenida y sesión persistente»), no con los códigos de momento (M1, M2…).
6. **Sin códigos crípticos:** no se muestran a Oscar identificadores técnicos (huellas de commit como «c2c1844», ids internos). Un commit se nombra por su mensaje; si un id es imprescindible, se explica qué es.
7. **Solo el proyecto Clasificados:** en Firebase y Supabase el agente trabaja únicamente en los proyectos de Clasificados. No abre, cambia ni borra otros proyectos o bases de datos de Oscar.
8. **Acciones que hace Oscar:** iniciar sesión, crear cuentas, escribir contraseñas, aceptar términos y borrar datos. El agente lo guía paso a paso y espera su confirmación.

---

# FASE 1 – DEFINICIÓN DEL PRODUCTO

> Esta fase es el **contrato** del proyecto. Todo lo que se programe debe cumplirla. Si algo de aquí parece incorrecto o incompleto, se pregunta a Oscar; no se corrige por cuenta propia.

## 1.1 Qué es y qué no es

**Es:** una aplicación Android en la que una persona con cuenta consulta anuncios clasificados organizados por categorías, y publica, edita o elimina sus propios anuncios. Los anuncios se comparten entre todos los usuarios a través de servicios en la nube.

**No incluye** (no programar aunque parezca útil):
- inicio de sesión con Google
- recuperación de contraseña (pendiente a futuro, pero fuera de alcance)
- chat entre usuarios
- notificaciones
- pagos
- favoritos
- moderación o administrador
- botones para llamar o escribir al vendedor (el contacto se muestra solo como texto)
- correos de confirmación

## 1.2 Actor

Un **único tipo de usuario**: una persona registrada que inicia sesión con correo y contraseña y que, con la misma cuenta, puede ver y publicar anuncios. No existe visitante sin cuenta ni administrador.

## 1.3 Reglas de negocio

| ID | Regla |
|---|---|
| RN-01 | Existe un solo tipo de usuario autenticado. Toda función de la app, salvo Bienvenida, Inicio de sesión y Registro, exige sesión activa. |
| RN-02 | La sesión se conserva al cerrar la app. Solo termina cuando el usuario toca «Cerrar sesión». |
| RN-03 | Una cuenta tiene: nombre completo (obligatorio), correo (obligatorio, único, formato usuario@dominio.ext), teléfono (opcional) y contraseña (mínimo 6 caracteres, gestionada solo por Firebase). |
| RN-04 | Al crear la cuenta, el usuario queda con la sesión iniciada y entra a Inicio. |
| RN-05 | Todo anuncio pertenece a un autor (uid de Firebase). El autor no cambia nunca. |
| RN-06 | Solo el autor puede editar o eliminar su anuncio. Cualquier usuario autenticado puede verlo. |
| RN-07 | Un anuncio tiene exactamente: categoría (1 de 13), título, descripción, precio, entre 1 y 5 fotos, ciudad (1 de 10), correo de contacto y teléfono (opcional). |
| RN-08 | **Título**: obligatorio, mínimo 10 caracteres. **Descripción**: obligatoria, mínimo 20 caracteres. |
| RN-09 | **Precio**: obligatorio, número entero mayor o igual a 0, en pesos colombianos. El 0 significa gratis y se muestra como «$0». No se acepta texto como «A convenir». |
| RN-10 | **Fotos**: mínimo 1, máximo 5, solo JPG o PNG, máximo 5 MB cada una. Se eligen desde la galería del dispositivo. |
| RN-11 | **Contacto**: el correo es obligatorio y válido. El teléfono es opcional y se prellena con el del registro si existe. Correo y teléfono se muestran en el detalle **como texto**. |
| RN-12 | La fecha de publicación la pone el servidor al crear el anuncio y no cambia al editarlo. |
| RN-13 | El orden por defecto de todo listado es «Más recientes primero». |
| RN-14 | La búsqueda compara **solo el título**, sin distinguir mayúsculas de minúsculas. Desde Inicio busca en todas las categorías; dentro de una categoría, solo en esa. |
| RN-15 | Filtros disponibles: categoría, ciudad, precio mínimo/máximo, fecha («Cualquier fecha», «Últimas 24 horas» = 24 h, «Última semana» = 7 días, «Último mes» = 30 días) y orden («Más recientes primero», «Más antiguos primero», «Precio: menor a mayor», «Precio: mayor a menor»). El precio mínimo no puede ser mayor que el máximo. |
| RN-16 | Eliminar un anuncio exige confirmación y borra también sus fotos. |
| RN-17 | Lo **único** que se guarda en el dispositivo es la preferencia de tema (claro/oscuro), con **SharedPreferences**. El tema por defecto en la primera instalación es el claro. Firestore se configura con caché **solo en memoria**. |
| RN-18 | Si una acción necesita internet y no hay conexión, se muestra «Sin conexión a internet. Verifica tu conexión e intenta de nuevo.» y la acción no se realiza. |
| RN-19 | Todos los servicios se usan en plan gratuito (Firebase Spark, Supabase Free), sin medios de pago. |
| RN-20 | No se versionan secretos: `google-services.json`, `local.properties` (llaves de Supabase), keystores. |
| RN-21 | Precios con formato colombiano: símbolo «$» y punto como separador de miles (ej. $1.200.000). Todos los textos de la app en español. |

## 1.4 Catálogos fijos

**Categorías (13), en este orden:** Empleos, Vivienda, Servicios, Comunidad, Vehículos, Tecnología, Electrónica, Mascotas, Educación, Salud, Eventos, Moda, Deportes.
- Inicio muestra las 5 primeras más la tarjeta «Más categorías».

**Ciudades (10), en este orden:** Bogotá, Medellín, Cali, Barranquilla, Cartagena, Bucaramanga, Pereira, Manizales, Pasto, Tuluá.

**Colores por categoría** (fondo del ícono / ícono, en modo claro):

| Categoría | Fondo | Ícono |
|---|---|---|
| Empleos | #DBEAFE | #2563EB |
| Vivienda | #DCFCE7 | #16A34A |
| Servicios | #F3E8FF | #9333EA |
| Comunidad | #FFEDD5 | #EA580C |
| Vehículos | #FEE2E2 | #DC2626 |
| Tecnología | #CFFAFE | #0891B2 |
| Electrónica | #FEF9C3 | #CA8A04 |
| Mascotas | #FCE7F3 | #DB2777 |
| Educación | #E0E7FF | #4F46E5 |
| Salud | #D1FAE5 | #059669 |
| Eventos | #EDE9FE | #7C3AED |
| Moda | #FFE4E6 | #E11D48 |
| Deportes | #ECFCCB | #65A30D |

En modo oscuro, el fondo del ícono es el color del ícono con opacidad del 18% y el ícono se aclara.

## 1.5 Mensajes exactos de la app

Usar **exactamente** estos textos. No cambiarlos sin autorización.

| Contexto | Mensaje |
|---|---|
| Registro – nombre vacío | El nombre es obligatorio |
| Correo inválido (registro e inicio de sesión) | Ingresa un correo válido |
| Registro – contraseña corta | La contraseña debe tener al menos 6 caracteres |
| Registro – confirmación distinta | Las contraseñas no coinciden |
| Registro – correo existente | Este correo ya está registrado |
| Registro – ayuda bajo la contraseña | Mínimo 6 caracteres |
| Inicio de sesión – contraseña vacía | La contraseña es obligatoria |
| Inicio de sesión – credenciales rechazadas | Correo o contraseña incorrectos |
| Publicar – paso 1 sin categoría | Por favor selecciona una categoría para continuar |
| Publicar – título vacío | El título es obligatorio |
| Publicar – título corto | El título debe tener al menos 10 caracteres |
| Publicar – contador del título | Mínimo 10 caracteres · N escritos |
| Publicar – descripción vacía | La descripción es obligatoria |
| Publicar – descripción corta | La descripción debe tener al menos 20 caracteres |
| Publicar – precio vacío | Indica un precio o escribe '0' si es gratis |
| Publicar – sin fotos | Agrega al menos 1 foto |
| Publicar – sexta foto | Máximo 5 fotos |
| Publicar – archivo inválido | Solo se permiten imágenes JPG o PNG de máximo 5 MB |
| Publicar – sin ciudad | Selecciona tu ciudad para que te encuentren |
| Publicar – correo vacío | El email es obligatorio para recibir contactos |
| Publicar – correo inválido | Por favor ingresa un email válido |
| Publicar – ayuda del teléfono | Facilita el contacto directo con compradores interesados |
| Confirmación – título | ¡Anuncio publicado correctamente! |
| Confirmación – texto | Tu publicación ya está disponible para otros usuarios. |
| Filtros – precio | El precio mínimo no puede ser mayor que el máximo |
| Listados sin resultados | No se encontraron anuncios |
| Mis anuncios vacío | Aún no has publicado anuncios |
| Mis anuncios – contador | Tienes N anuncios publicados |
| Listado – contador | N anuncios encontrados |
| Diálogo eliminar | ¿Eliminar anuncio? (botones «Cancelar» y «Eliminar») |
| Sin conexión | Sin conexión a internet. Verifica tu conexión e intenta de nuevo. |

## 1.6 Requerimientos funcionales (resumen fiel del ERS)

> Fuente oficial: `ERS_Clasificados_IEEE830.docx`. Si hay diferencia entre este resumen y el ERS, **manda el ERS** y se avisa a Oscar.

### 1.6.1 Autenticación y sesión
*Objetivo:* Permitir que una persona cree su cuenta, inicie y cierre sesión de forma segura, y que la sesión se conserve entre usos de la aplicación.

- **RF-01 · Pantalla de bienvenida** — Prioridad Normal · Pantalla: 00 · Bienvenida
  Al abrir la aplicación, el sistema deberá mostrar la pantalla de bienvenida con el nombre «Clasificados» mientras verifica si existe una sesión activa.
  *Verificación:* Abrir la aplicación desde cero: la primera pantalla visible es «00 · Bienvenida».
- **RF-02 · Sesión persistente** — Prioridad Alta · Pantalla: 00 · Bienvenida, 01 · Inicio de sesión, 03 · Inicio
  Si existe una sesión activa, el sistema deberá llevar al usuario directamente a la pantalla Inicio sin solicitar credenciales. Si no existe sesión activa, deberá mostrar la pantalla Inicio de sesión.
  *Verificación:* Iniciar sesión, cerrar la aplicación (quitarla de recientes) y abrirla de nuevo: se muestra Inicio sin pedir correo ni contraseña. Repetir tras cerrar sesión: se muestra Inicio de sesión.
- **RF-03 · Registro de usuario** — Prioridad Alta · Pantalla: 02 · Registro
  El sistema deberá permitir crear una cuenta ingresando nombre completo, correo electrónico, teléfono (opcional), contraseña y confirmación de contraseña. La cuenta se creará en Firebase Authentication con el método correo y contraseña. Al crear la cuenta, el usuario quedará con la sesión iniciada y verá la pantalla Inicio.
  *Verificación:* Registrar una cuenta con datos válidos: la cuenta aparece en la consola de Firebase Authentication.
- **RF-04 · Validación del registro** — Prioridad Alta · Pantalla: 02 · Registro
  Antes de crear la cuenta, el sistema deberá validar: (a) nombre completo obligatorio; (b) correo obligatorio y con formato usuario@dominio.ext; (c) contraseña de al menos 6 caracteres; (d) confirmación igual a la contraseña. Por cada campo inválido mostrará, debajo del campo, el mensaje correspondiente: «El nombre es obligatorio», «Ingresa un correo válido», «La contraseña debe tener al menos 6 caracteres», «Las contraseñas no coinciden». Mientras exista un campo inválido no se creará la cuenta.
  *Verificación:* Enviar el formulario con cada caso inválido (a)–(d) por separado: aparece el mensaje indicado y no se crea la cuenta.
- **RF-05 · Correo ya registrado** — Prioridad Normal · Pantalla: 02 · Registro
  Si el correo ingresado ya pertenece a una cuenta existente, el sistema deberá mostrar el mensaje «Este correo ya está registrado» y no creará una nueva cuenta.
  *Verificación:* Registrar dos veces el mismo correo: en el segundo intento aparece el mensaje y no se crea otra cuenta.
- **RF-06 · Inicio de sesión** — Prioridad Alta · Pantalla: 01 · Inicio de sesión, 03 · Inicio
  El sistema deberá permitir iniciar sesión con correo electrónico y contraseña mediante Firebase Authentication. Con credenciales válidas mostrará la pantalla Inicio.
  *Verificación:* Iniciar sesión con una cuenta registrada: se muestra la pantalla Inicio.
- **RF-07 · Validación del inicio de sesión** — Prioridad Alta · Pantalla: 01 · Inicio de sesión
  El sistema deberá validar que el correo tenga formato usuario@dominio.ext («Ingresa un correo válido») y que la contraseña no esté vacía («La contraseña es obligatoria»). Si Firebase rechaza las credenciales, mostrará el mensaje «Correo o contraseña incorrectos».
  *Verificación:* Probar correo sin «@», contraseña vacía y contraseña equivocada: aparece el mensaje correspondiente y no se inicia sesión.
- **RF-08 · Cerrar sesión** — Prioridad Alta · Pantalla: 16 · Ajustes, 01 · Inicio de sesión
  El sistema deberá permitir cerrar la sesión desde la pantalla Ajustes. Al cerrarla mostrará la pantalla Inicio de sesión.
  *Verificación:* Tocar «Cerrar sesión»: se muestra Inicio de sesión; al reabrir la aplicación se piden credenciales.
- **RF-09 · Acceso solo con sesión** — Prioridad Alta · Pantalla: 00, 01, 02
  Sin una sesión activa, el sistema solo deberá permitir el acceso a las pantallas Bienvenida, Inicio de sesión y Registro.
  *Verificación:* Sin sesión, intentar llegar a cualquier otra pantalla: la aplicación muestra Inicio de sesión.

### 1.6.2 Consulta de anuncios
*Objetivo:* Permitir que el usuario explore las categorías y vea los anuncios publicados por cualquier usuario.

- **RF-10 · Pantalla Inicio** — Prioridad Alta · Pantalla: 03 · Inicio
  La pantalla Inicio deberá mostrar un buscador y seis tarjetas, dos por renglón: Empleos, Vivienda, Servicios, Comunidad, Vehículos y «Más categorías». Cada tarjeta mostrará solo un icono y el nombre, sin descripción.
  *Verificación:* Abrir Inicio: se ven el buscador y las seis tarjetas en tres renglones de dos, sin texto descriptivo.
- **RF-11 · Todas las categorías** — Prioridad Alta · Pantalla: 04 · Todas las categorías
  Al tocar «Más categorías» o «Ver todas», el sistema deberá mostrar las 13 categorías (Empleos, Vivienda, Servicios, Comunidad, Vehículos, Tecnología, Electrónica, Mascotas, Educación, Salud, Eventos, Moda y Deportes) en tarjetas de dos por renglón con icono y nombre.
  *Verificación:* Tocar «Más categorías» y «Ver todas»: en ambos casos se ven las 13 categorías, dos por renglón.
- **RF-12 · Listado de anuncios por categoría** — Prioridad Alta · Pantalla: 05 · Anuncios de categoría
  Al tocar una categoría, el sistema deberá mostrar sus anuncios en tarjetas de dos por renglón. Cada tarjeta mostrará la primera foto, el título (máximo dos líneas), el precio y la ciudad. Sobre el listado se mostrará el texto «N anuncios encontrados». El orden por defecto será «Más recientes primero».
  *Verificación:* Abrir una categoría con anuncios: el conteo coincide con el número de tarjetas y el anuncio más reciente aparece primero.
- **RF-13 · Anuncios compartidos entre usuarios** — Prioridad Alta · Pantalla: 05 · Anuncios de categoría, 07 · Detalle
  Los anuncios deberán almacenarse en un servicio en la nube, de modo que un anuncio publicado por un usuario sea visible para cualquier otro usuario con sesión activa.
  *Verificación:* Publicar un anuncio desde el dispositivo A y abrir la misma categoría en el dispositivo B con otra cuenta: el anuncio aparece.
- **RF-14 · Detalle del anuncio** — Prioridad Alta · Pantalla: 07 · Detalle del anuncio
  Al tocar una tarjeta de anuncio, el sistema deberá mostrar: fotos deslizables con indicador «n / total», categoría, título, precio, ciudad, tiempo transcurrido desde la publicación (p. ej. «Hace 2 horas»), descripción, correo electrónico de contacto y teléfono (solo si fue ingresado). El correo y el teléfono se mostrarán como texto.
  *Verificación:* Abrir un anuncio con y sin teléfono: se muestran todos los datos listados y el teléfono solo aparece cuando existe.
- **RF-15 · Sin resultados** — Prioridad Normal · Pantalla: Pendiente de diseño (estado vacío)
  Cuando una categoría, búsqueda o combinación de filtros no tenga anuncios, el sistema deberá mostrar el mensaje «No se encontraron anuncios».
  *Verificación:* Buscar un texto que no exista en ningún título: aparece el mensaje.

### 1.6.3 Búsqueda y filtros
*Objetivo:* Permitir que el usuario encuentre anuncios por texto y delimite los resultados.

- **RF-16 · Búsqueda desde Inicio** — Prioridad Alta · Pantalla: 03 · Inicio; resultados pendientes de diseño
  El buscador de Inicio deberá buscar el texto ingresado únicamente en el título de los anuncios de todas las categorías y mostrar los resultados en tarjetas de dos por renglón. La búsqueda no distinguirá entre mayúsculas y minúsculas.
  *Verificación:* Buscar una palabra que aparezca en títulos de dos categorías distintas: aparecen ambos anuncios. Buscar una palabra que solo esté en una descripción: no aparece.
- **RF-17 · Búsqueda dentro de una categoría** — Prioridad Normal · Pantalla: 05 · Anuncios de categoría
  El buscador del listado de una categoría deberá buscar el texto únicamente en el título de los anuncios de esa categoría.
  *Verificación:* Dentro de Empleos, buscar un título que existe en Vivienda: no aparece.
- **RF-18 · Filtros del listado** — Prioridad Alta · Pantalla: 06 · Filtros
  El sistema deberá permitir filtrar el listado por: (a) categoría; (b) ciudad (Bogotá, Medellín, Cali, Barranquilla, Cartagena, Bucaramanga, Pereira, Manizales, Pasto y Tuluá, o «Todas las ciudades»); (c) precio mínimo y máximo en pesos colombianos; (d) fecha de publicación: «Cualquier fecha», «Últimas 24 horas», «Última semana», «Último mes»; y ordenar por: «Más recientes primero» (por defecto), «Más antiguos primero», «Precio: menor a mayor», «Precio: mayor a menor». Los filtros se aplicarán al tocar «Aplicar filtros».
  *Verificación:* Aplicar cada filtro y cada orden por separado: el listado contiene solo anuncios que cumplen el filtro, en el orden elegido.
- **RF-19 · Validación del rango de precio** — Prioridad Normal · Pantalla: 06 · Filtros (estado de error pendiente de diseño)
  Si el precio mínimo es mayor que el precio máximo, el sistema deberá mostrar el mensaje «El precio mínimo no puede ser mayor que el máximo» y no aplicará los filtros.
  *Verificación:* Ingresar mínimo 500.000 y máximo 100.000 y tocar «Aplicar filtros»: aparece el mensaje y el listado no cambia.
- **RF-20 · Limpiar filtros** — Prioridad Baja · Pantalla: 06 · Filtros
  Al tocar «Limpiar», el sistema deberá restablecer todos los filtros a sus valores por defecto: categoría actual, «Todas las ciudades», precios vacíos, «Cualquier fecha» y «Más recientes primero».
  *Verificación:* Cambiar todos los filtros y tocar «Limpiar»: todos vuelven a los valores por defecto.

### 1.6.4 Publicación de anuncios
*Objetivo:* Permitir que el usuario publique un anuncio guiado por pasos, con validación de cada dato.

- **RF-21 · Publicación en cuatro pasos** — Prioridad Alta · Pantalla: 08 a 11 · Publicar
  Al tocar «Publicar» en la barra inferior, el sistema deberá mostrar un formulario de cuatro pasos: 1 Categoría, 2 Detalles, 3 Fotos y 4 Ubicación y contacto, con un indicador del paso actual. «Continuar» solo avanzará si el paso actual es válido, y «Volver» regresará al paso anterior conservando los datos ingresados.
  *Verificación:* Completar un paso, avanzar, volver: los datos del paso anterior siguen ahí. Intentar avanzar con datos inválidos: no avanza.
- **RF-22 · Paso 1 – Categoría** — Prioridad Alta · Pantalla: 08 · Publicar · Paso 1
  El usuario deberá seleccionar una de las 13 categorías. Si intenta continuar sin seleccionar, el sistema mostrará «Por favor selecciona una categoría para continuar».
  *Verificación:* Tocar «Continuar» sin seleccionar categoría: aparece el mensaje y no avanza.
- **RF-23 · Paso 2 – Detalles** — Prioridad Alta · Pantalla: 09 · Paso 2, 09b · Paso 2 con errores
  El sistema deberá validar: (a) título obligatorio («El título es obligatorio») y de al menos 10 caracteres («El título debe tener al menos 10 caracteres»), mostrando el contador «Mínimo 10 caracteres · N escritos»; (b) descripción obligatoria («La descripción es obligatoria») y de al menos 20 caracteres («La descripción debe tener al menos 20 caracteres»); (c) precio obligatorio, que solo acepta números enteros mayores o iguales a 0, donde 0 significa gratis («Indica un precio o escribe '0' si es gratis»). Un anuncio con precio 0 se mostrará como «$0».
  *Verificación:* Probar título de 5 caracteres, descripción vacía y precio vacío: aparecen los tres mensajes. El teclado del precio solo permite números.
- **RF-24 · Paso 3 – Fotos** — Prioridad Alta · Pantalla: 10 · Publicar · Paso 3
  El usuario deberá agregar entre 1 y 5 fotos en formato JPG o PNG de máximo 5 MB cada una. El sistema mostrará el contador «N / 5». Mensajes: «Agrega al menos 1 foto» (si intenta continuar sin fotos), «Máximo 5 fotos» (si intenta agregar una sexta) y «Solo se permiten imágenes JPG o PNG de máximo 5 MB» (archivo inválido). Las fotos se seleccionarán desde la galería del dispositivo.
  *Verificación:* Intentar continuar sin fotos, agregar una sexta foto y elegir un archivo de 6 MB: aparece el mensaje correspondiente en cada caso.
- **RF-25 · Paso 4 – Ubicación y contacto** — Prioridad Alta · Pantalla: 11 · Publicar · Paso 4
  El sistema deberá solicitar: (a) ciudad obligatoria, elegida entre Bogotá, Medellín, Cali, Barranquilla, Cartagena, Bucaramanga, Pereira, Manizales, Pasto y Tuluá («Selecciona tu ciudad para que te encuentren»); (b) correo obligatorio («El email es obligatorio para recibir contactos») y con formato válido («Por favor ingresa un email válido»); (c) teléfono opcional, que aparecerá prellenado con el teléfono del registro si existe.
  *Verificación:* Probar sin ciudad y con correo sin «@»: aparecen los mensajes. Con un usuario que registró teléfono, el campo aparece prellenado.
- **RF-26 · Guardar el anuncio** — Prioridad Alta · Pantalla: 11 · Paso 4, 12 · Confirmación
  Al tocar «Publicar anuncio» con todos los pasos válidos, el sistema deberá subir las fotos, guardar el anuncio junto con el identificador del autor y la fecha de publicación, mostrar un indicador de progreso durante el proceso y, al terminar, mostrar la pantalla Confirmación.
  *Verificación:* Publicar un anuncio válido: se ve el indicador de progreso, luego Confirmación, y el anuncio aparece en su categoría.
- **RF-27 · Confirmación de publicación** — Prioridad Normal · Pantalla: 12 · Confirmación
  La pantalla Confirmación deberá mostrar «¡Anuncio publicado correctamente!» y «Tu publicación ya está disponible para otros usuarios.», con los botones «Ver anuncio» (abre su detalle) y «Volver al inicio» (abre Inicio).
  *Verificación:* Tocar cada botón: abre la pantalla indicada.

### 1.6.5 Gestión de mis anuncios
*Objetivo:* Permitir que el usuario consulte, edite y elimine únicamente los anuncios que él publicó.

- **RF-28 · Mis anuncios** — Prioridad Alta · Pantalla: 13 · Mis anuncios
  La pestaña «Mis anuncios» deberá listar solo los anuncios publicados por el usuario con sesión activa, en tarjetas de dos por renglón, cada una con los botones «Editar» y «Eliminar», y el texto «Tienes N anuncios publicados». Si el usuario no tiene anuncios, se mostrará el mensaje «Aún no has publicado anuncios».
  *Verificación:* Con dos cuentas que publicaron anuncios distintos: cada una ve solo los suyos.
- **RF-29 · Editar anuncio** — Prioridad Alta · Pantalla: 14 · Editar anuncio
  Al tocar «Editar», el sistema deberá abrir el mismo flujo de cuatro pasos con los datos del anuncio cargados, aplicar las mismas validaciones de RF-22 a RF-25 y, al confirmar, actualizar el anuncio para todos los usuarios. El botón del último paso dirá «Guardar cambios».
  *Verificación:* Cambiar el título de un anuncio y guardar: el nuevo título se ve en el listado desde otra cuenta.
- **RF-30 · Eliminar anuncio** — Prioridad Alta · Pantalla: 15 · Diálogo eliminar anuncio
  Al tocar «Eliminar», el sistema deberá mostrar el diálogo «¿Eliminar anuncio?» con los botones «Cancelar» y «Eliminar». Con «Eliminar» borrará el anuncio y sus fotos, y dejará de mostrarse a todos los usuarios. Con «Cancelar» cerrará el diálogo sin cambios.
  *Verificación:* Cancelar: el anuncio sigue. Eliminar: desaparece de Mis anuncios y de su categoría en otra cuenta.

### 1.6.6 Preferencias y perfil
*Objetivo:* Permitir que el usuario elija el tema visual y lo conserve en el dispositivo.

- **RF-31 · Cambiar tema claro / oscuro** — Prioridad Alta · Pantalla: 16 · Ajustes
  La pantalla Ajustes deberá tener un interruptor «Modo oscuro». Al cambiarlo, toda la aplicación pasará al tema elegido sin necesidad de reiniciarla.
  *Verificación:* Activar y desactivar el interruptor: la aplicación cambia de tema al instante.
- **RF-32 · Guardar la preferencia de tema** — Prioridad Alta · Pantalla: 16 · Ajustes
  El sistema deberá guardar la preferencia de tema en el almacenamiento local del dispositivo y aplicarla cada vez que se abra la aplicación. La primera vez que se instala la aplicación, el tema será el claro.
  *Verificación:* Activar el modo oscuro, cerrar la aplicación y abrirla: inicia en modo oscuro.
- **RF-33 · Datos del perfil** — Prioridad Baja · Pantalla: 16 · Ajustes
  La pantalla Ajustes deberá mostrar el nombre y el correo del usuario con sesión activa.
  *Verificación:* Iniciar sesión con dos cuentas distintas: Ajustes muestra los datos de cada una.

### 1.6.7 Conectividad
*Objetivo:* Informar al usuario cuando una acción no se puede completar por falta de internet.

- **RF-34 · Mensaje sin conexión** — Prioridad Alta · Pantalla: Pendiente de diseño (mensaje sin conexión)
  Cuando una acción requiera internet y el dispositivo no tenga conexión, el sistema deberá mostrar el mensaje «Sin conexión a internet. Verifica tu conexión e intenta de nuevo.» y no realizará la acción.
  *Verificación:* Activar el modo avión y abrir una categoría o publicar: aparece el mensaje.

## 1.7 Requerimientos no funcionales

### 1.7.1 Restricciones de diseño y plataforma

- **RNF-01 · Plataforma** — Prioridad Alta · Pantalla: Todas
  La aplicación deberá funcionar en dispositivos Android con versión 8.0 (API 26) o superior.
  *Verificación:* El proyecto declara minSdk 26 y la aplicación se instala y ejecuta en un emulador con API 26.
- **RNF-02 · Lenguaje** — Prioridad Alta · Pantalla: —
  La aplicación deberá desarrollarse en Kotlin.
  *Verificación:* Revisión del código fuente en el repositorio.
- **RNF-03 · Arquitectura** — Prioridad Alta · Pantalla: —
  La aplicación deberá construirse con la arquitectura MVVM (Modelo – Vista – ViewModel).
  *Verificación:* Revisión del código: existen capas separadas de vistas, ViewModels y repositorios/modelos.
- **RNF-04 · Servicio de autenticación** — Prioridad Alta · Pantalla: 01, 02
  La autenticación deberá realizarse con Firebase Authentication usando el método correo y contraseña.
  *Verificación:* Las cuentas creadas aparecen en la consola de Firebase con el proveedor «Correo electrónico/contraseña».
- **RNF-05 · Almacenamiento en la nube** — Prioridad Alta · Pantalla: 05, 07, 13
  Los datos de los anuncios deberán almacenarse en Cloud Firestore y las fotos en Supabase Storage.
  *Verificación:* Tras publicar, el anuncio aparece en la colección de Firestore y sus fotos en el bucket de Supabase.
- **RNF-06 · Almacenamiento local** — Prioridad Alta · Pantalla: 16 · Ajustes
  La única información guardada localmente en el dispositivo deberá ser la preferencia de tema, almacenada con preferencias compartidas (SharedPreferences de Android).
  *Verificación:* Revisión del código: solo la clave de tema se guarda localmente.
- **RNF-07 · Costo de servicios** — Prioridad Alta · Pantalla: —
  Todos los servicios externos (Firebase y Supabase) deberán usarse dentro de sus planes gratuitos, sin registrar medios de pago.
  *Verificación:* Las consolas de Firebase (plan Spark) y Supabase (plan Free) no tienen facturación activa.

### 1.7.2 Rendimiento

- **RNF-08 · Tiempo de carga del listado** — Prioridad Normal · Pantalla: 05 · Anuncios de categoría
  Con una conexión estable (ver glosario), el listado de una categoría con hasta 50 anuncios deberá mostrar sus tarjetas en un máximo de 5 segundos desde que se toca la categoría. Las fotos pueden terminar de cargar después de ese tiempo.
  *Verificación:* Medir con cronómetro 5 veces en Wi-Fi: todas las mediciones son de 5 segundos o menos.
- **RNF-09 · Límites de las fotos** — Prioridad Alta · Pantalla: 10 · Publicar · Paso 3
  Cada anuncio deberá tener como máximo 5 fotos, cada una en formato JPG o PNG y de máximo 5 MB.
  *Verificación:* Ver criterio de RF-24.

### 1.7.3 Usabilidad

- **RNF-10 · Área segura** — Prioridad Alta · Pantalla: Todas
  Ningún contenido ni control deberá quedar cubierto por la barra de estado, la muesca de la cámara o la barra de navegación del sistema.
  *Verificación:* Revisar todas las pantallas en un emulador con muesca y navegación por gestos.
- **RNF-11 · Diseño minimalista móvil** — Prioridad Alta · Pantalla: 03, 04, 05, 13
  Las cuadrículas de categorías y de anuncios deberán mostrar dos tarjetas por renglón, y las tarjetas de categoría no mostrarán descripción.
  *Verificación:* Revisión visual de las pantallas indicadas.
- **RNF-12 · Tamaño de zonas táctiles** — Prioridad Normal · Pantalla: Todas
  Todo botón, ícono o elemento que se pueda tocar deberá tener una zona táctil de al menos 48 × 48 dp.
  *Verificación:* Revisión con el «Accessibility Scanner» de Google o el inspector de diseño de Android Studio.
- **RNF-13 · Idioma y formato de precios** — Prioridad Normal · Pantalla: Todas
  Todos los textos de la aplicación deberán estar en español, y los precios se mostrarán en pesos colombianos con el símbolo «$» y punto como separador de miles (p. ej. $1.200.000).
  *Verificación:* Revisión visual de las pantallas con precios.
- **RNF-14 · Consistencia con el diseño** — Prioridad Normal · Pantalla: Todas
  Las pantallas de la aplicación deberán corresponder a los diseños aprobados en Figma, en tema claro y oscuro, usando los colores del diseño: principal #2563EB y botón Publicar #16A34A.
  *Verificación:* Comparación lado a lado de cada pantalla con su diseño en Figma.

### 1.7.4 Seguridad

- **RNF-15 · Solo usuarios autenticados** — Prioridad Alta · Pantalla: —
  Las reglas de seguridad de Firestore y Supabase Storage deberán permitir leer y escribir datos solo a usuarios con sesión activa.
  *Verificación:* Probar una lectura sin sesión en el simulador de reglas de Firestore: es rechazada.
- **RNF-16 · Solo el autor modifica su anuncio** — Prioridad Alta · Pantalla: 13, 14, 15
  Las reglas de seguridad deberán impedir que un usuario edite o elimine un anuncio cuyo autor sea otro usuario.
  *Verificación:* En el simulador de reglas, intentar actualizar y borrar un anuncio ajeno: es rechazado.
- **RNF-17 · Manejo de contraseñas** — Prioridad Alta · Pantalla: 01, 02
  La aplicación no deberá guardar contraseñas en el dispositivo ni en Firestore. Su gestión será exclusiva de Firebase Authentication.
  *Verificación:* Revisión del código y de la base de datos: no existe ningún campo de contraseña.

### 1.7.5 Mantenibilidad

- **RNF-18 · Pruebas unitarias** — Prioridad Alta · Pantalla: —
  El proyecto deberá incluir al menos un archivo de pruebas unitarias que se ejecute sin errores.
  *Verificación:* Ejecutar las pruebas con Gradle (./gradlew test): todas pasan.
- **RNF-19 · Control de versiones** — Prioridad Alta · Pantalla: —
  El código deberá gestionarse con Git en un repositorio remoto, con commits que permitan identificar el aporte del estudiante.
  *Verificación:* Revisión del historial de commits del repositorio.

## 1.8 Modelo de datos

**Firestore – colección `usuarios`** (documento con id = uid de Firebase):

| Campo | Tipo | Notas |
|---|---|---|
| nombre | String | obligatorio |
| telefono | String? | opcional |

> El correo vive en Firebase Authentication. La contraseña **nunca** se guarda en Firestore.

**Firestore – colección `anuncios`:**

| Campo | Tipo | Notas |
|---|---|---|
| id | String | id del documento |
| autorId | String | uid del autor, inmutable |
| categoria | String | id de la categoría (una de las 13) |
| titulo | String | ≥ 10 caracteres |
| tituloMinusculas | String | `titulo.lowercase()`, para la búsqueda |
| descripcion | String | ≥ 20 caracteres |
| precio | Long | entero ≥ 0 |
| fotos | List\<String\> | URL públicas, de 1 a 5 |
| ciudad | String | una de las 10 |
| correo | String | obligatorio |
| telefono | String? | opcional |
| fechaPublicacion | Timestamp | hora del servidor, no cambia al editar |

**Supabase Storage – bucket `anuncios-fotos`:** ruta `{autorId}/{idAnuncio}/{n}.jpg`.

## 1.9 Diseño visual

- **Figma:** «Clasificados – Diseño Móvil» (página *Modo claro* y *Modo oscuro*; base 360 × 800, fuente Roboto). Pantallas:
  - 00 Bienvenida · 01 Inicio de sesión · 02 Registro · 03 Inicio · 04 Todas las categorías
  - 05 Anuncios de categoría · 06 Filtros · 07 Detalle · 08–11 Publicar (pasos 1 a 4, 09b con errores)
  - 12 Confirmación · 13 Mis anuncios · 14 Editar · 15 Diálogo eliminar · 16 Ajustes
  - Estados: 03b Resultados de búsqueda · 05b Sin resultados · 05c Sin conexión · 06b Filtros con error · 10b Paso 3 con error · 11b Publicando (progreso) · 13b Mis anuncios vacío · 14b Editar paso 4 «Guardar cambios»
- **Estilo:** minimalista móvil. Tarjetas dos por renglón, **sin descripción** en las tarjetas de categoría.
- **Barra inferior (cambio aprobado el 28/10/2026):** 3 pestañas del mismo ancho: Inicio, Mis anuncios, Ajustes. Encima, el botón circular verde **«Publicar»** (círculo de 60 dp color `publicar`, aro de 5 dp color `superficie`, sombra verde suave, ícono + blanco de 28 dp y el texto «Publicar» debajo en 12 sp negrita color `texto`) sobresale de la barra, centrado sobre «Mis anuncios». Todo contenido desplazable deja 96 dp libres **solo al final** (constante `EspacioBotonPublicar`), para que el botón no tape el último elemento; arriba la pantalla usa todo el alto y el botón flota sobre el contenido (como 05c). Las pantallas sin scroll no reservan ese espacio. Ver componente «Barra inferior» y pantalla 03 en Figma.
- **Área segura:** nada debajo de la barra de estado ni de la barra de navegación.
- **Tokens de color:**

| Token | Claro | Oscuro |
|---|---|---|
| primario | #2563EB | #3B82F6 |
| primario-suave | #DBEAFE | #1E3A5F |
| sobre-primario | #FFFFFF | #FFFFFF |
| publicar | #16A34A | #22C55E |
| fondo | #F9FAFB | #0B0F19 |
| superficie | #FFFFFF | #161B26 |
| superficie-variante | #F3F4F6 | #1F2937 |
| texto | #111827 | #F9FAFB |
| texto-secundario | #6B7280 | #9CA3AF |
| borde | #E5E7EB | #2D3748 |
| error | #DC2626 | #F87171 |
| exito | #16A34A | #4ADE80 |

- **Pendientes de diseño:** ninguno obligatorio; todos los estados ya están en Figma (ver la lista de estados arriba). Solo quedan opcionales las pantallas de error de Inicio de sesión y Registro: si se necesitan, el agente usa los mensajes de la sección 1.5 y los tokens anteriores, y pide aprobación a Oscar.

## 1.10 Arquitectura y tecnologías (decisiones cerradas)

| Área | Decisión |
|---|---|
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose + Material 3 |
| Arquitectura | MVVM: pantalla `@Composable` → `ViewModel` (estado con `StateFlow`) → `Repository` → Firebase/Supabase |
| Navegación | Navigation Compose (`NavHost`) |
| Versión mínima | minSdk 26 (Android 8.0) |
| Autenticación | Firebase Authentication – correo y contraseña |
| Datos | Cloud Firestore con caché solo en memoria |
| Fotos | Supabase Storage (librería oficial de Supabase para Kotlin) con Firebase configurado como proveedor de autenticación de terceros |
| Carga de imágenes | Coil (`AsyncImage`) |
| Preferencias | SharedPreferences (clave `modo_oscuro`, por defecto `false`) |
| Pruebas | JUnit, al menos `ValidadorTest` |

**Estructura de paquetes:**

```
ui/          pantallas @Composable, componentes reutilizables y ui/theme (Color.kt, Theme.kt)
viewmodel/   un ViewModel por pantalla
data/        repositorios (AnunciosRepository, FotosRepository, PreferenciasRepository, AuthRepository)
model/       clases de datos (Anuncio, Usuario, Categoria, Ciudad)
util/        Validador, formato de precios, RedDisponible
```

**Reglas técnicas:**
- Las validaciones van en un objeto `Validador` con funciones puras (sin dependencias de Android) para poder probarlas con JUnit.
- Las pantallas no llaman a Firebase directamente; siempre pasan por ViewModel y Repository.
- Versiones de librerías: usar las estables más recientes compatibles. El agente informa a Oscar qué versiones agregó.

## 1.11 Datos que antes no estaban definidos (ya definidos)

| Dato | Valor definido | Historia |
|---|---|---|
| Nombre del paquete / applicationId | `com.univalle.clasificados` | HU-01 |
| Ícono de la aplicación (launcher) | Logo de la pantalla 00 (cuadro blanco con cuadrícula azul sobre fondo azul), como ícono adaptativo vectorial | HU-01 |
| Proyecto en Firebase | `clasificados` (plan Spark, dentro de la organización correounivalle.edu.co) | HU-02 |
| Proyecto en Supabase | `clasificados` (plan Free, São Paulo, en la organización «Clasificados» de la segunda cuenta de GitHub de Oscar) | HU-02 |

Si aparece otro dato sin definir, se agrega aquí y se pregunta a Oscar antes de usarlo.

---

# FASE 2 – SPRINT 1 (19 de octubre – 1 de noviembre de 2026)

**Objetivo del sprint:** dejar la base técnica lista y el flujo completo de cuenta funcionando: bienvenida, registro, inicio de sesión y sesión persistente.

| Momento | Contenido | Horas |
|---|---|---|
| M0 | Preparación (Oscar) | — |
| M1 | HU-01 · Proyecto base | 10 h |
| M2 | HU-02 · Firebase y Supabase | 8 h |
| M3 | HU-03 · Bienvenida y sesión | 4 h |
| M4 | HU-04 · Registro | 6 h |
| M5 | HU-05 · Inicio de sesión | 4 h |
| M6 | Revisión del 29 de octubre | — |
| M7 | Reserva de correcciones y cierre del sprint | 3 h |

## M0 – Preparación (la hace Oscar, con guía del agente)

- [x] Android Studio instalado y emulador funcionando.
- [x] Repositorio `clasificados-android` publicado en GitHub, ubicado en `PROYECTO CLASIFICADOS/SOFTWARE/clasificados-android` y abierto en GitHub Desktop.
- [x] Este archivo guardado en la raíz del repositorio como `GUIA_DESARROLLO.md`, junto con `CLAUDE.md` y `AGENTS.md` que apuntan a él.
- [x] Cuenta de Google lista para crear el proyecto de Firebase (se usa en M2).
- [x] Cuenta de Supabase lista (se usa en M2).
- [x] Commit: `Docs: guía de desarrollo para agentes`.

## M1 – HU-01 · Proyecto base con arquitectura MVVM y tema visual (10 h)

**Épica:** EP-00 · Configuración del proyecto · **Prioridad:** Alta

**Historia:** Como desarrollador, quiero tener el proyecto Android creado con la arquitectura MVVM, la navegación base y los colores del diseño en tema claro y oscuro, para construir todas las pantallas sobre una base ordenada y consistente con Figma.

**Requerimientos que cumple:** RNF-01 (Plataforma), RNF-02 (Lenguaje), RNF-03 (Arquitectura), RNF-10 (Área segura), RNF-12 (Tamaño de zonas táctiles), RNF-13 (Idioma y formato de precios), RNF-14 (Consistencia con el diseño), RNF-19 (Control de versiones).

**Preguntas que el agente debe hacer antes de empezar:**
- ¿Qué nombre de paquete (applicationId) usamos? Ejemplo: `com.univalle.clasificados`.
- ¿Qué ícono de aplicación usamos? (si no hay, se deja el de Android por ahora y se pregunta después)

**Criterios de aceptación** (todos deben cumplirse):
- [x] Dado se crea el proyecto en Android Studio, cuando se revisa su configuración, entonces el lenguaje es Kotlin y minSdk es 26 (Android 8.0).
- [x] Dado el proyecto está creado, cuando se revisa la estructura de carpetas, entonces existen paquetes separados para vistas (ui), ViewModels (viewmodel), datos (data/repository) y modelos (model).
- [x] Dado la aplicación se ejecuta en un emulador con muesca y navegación por gestos, cuando se abre cualquier pantalla, entonces ningún contenido queda debajo de la barra de estado ni de la barra de navegación.
- [x] Dado el tema está configurado, cuando se cambia el sistema entre claro y oscuro de forma manual en el código, entonces los colores corresponden a las variables «Tema claro» y «Tema oscuro» de Figma (principal #2563EB, Publicar #16A34A).
- [x] Dado existe el repositorio remoto, cuando se sube el proyecto base, entonces queda un commit con el proyecto inicial en la rama principal.

**Qué ve y hace el usuario:**
- Esta historia no tiene una pantalla visible para el usuario final; deja lista la «carcasa» de la aplicación.
- Al abrir la aplicación se ve una pantalla vacía con los colores del diseño, la barra de estado respetada y la barra inferior con tres pestañas (Inicio, Mis anuncios, Ajustes) y el botón circular verde «Publicar» que sobresale encima de la barra, centrado sobre «Mis anuncios»; aún sin contenido.
- Todos los textos fijos de la aplicación quedan en español desde el inicio.

**Cómo construirlo:**
1. Crear el proyecto en Android Studio con la plantilla «Empty Activity» (Jetpack Compose), lenguaje Kotlin, minSdk 26.
2. Organizar paquetes: model (clases de datos), data/repository (acceso a Firebase y Supabase), viewmodel (un ViewModel por pantalla que expone su estado con StateFlow), ui (funciones @Composable de pantallas y componentes reutilizables). Las pantallas leen el estado con collectAsStateWithLifecycle().
3. Definir en ui/theme (Color.kt y Theme.kt) dos ColorScheme de Material 3, claro y oscuro, con los valores de las variables de Figma (primario, primario-suave, publicar, fondo, superficie, texto, texto-secundario, borde, error, éxito), y la función ClasificadosTheme(darkTheme) que los aplica.
4. Llamar a enableEdgeToEdge() en MainActivity y usar Scaffold, aplicando su innerPadding (WindowInsets de las barras del sistema) al contenido de cada pantalla para respetar el área segura.
5. Crear la navegación con Navigation Compose (NavHost y rutas por pantalla) y la barra inferior con tres pestañas (Inicio, Mis anuncios, Ajustes) y, encima y centrado, el botón circular «Publicar» (ver 1.9); el contenido con scroll deja 96 dp libres al final (ver 1.9). Pantallas vacías de marcador.
6. Crear una utilidad de formato de precios en pesos colombianos (Locale "es", "CO") con punto como separador de miles.
7. Garantizar zonas táctiles de al menos 48 × 48 dp en botones e íconos (los componentes de Material 3 lo cumplen; en íconos propios usar Modifier.minimumInteractiveComponentSize()).
8. Hacer commit y push del proyecto base al repositorio remoto.

**Al terminar:** commit `HU-01: proyecto base MVVM con tema claro/oscuro y navegación` · ClickUp: **HU-01** → *finalizado* con su Lista de control marcada.

## M2 – HU-02 · Conexión con Firebase y Supabase (8 h)

**Épica:** EP-00 · Configuración del proyecto · **Prioridad:** Alta

**Historia:** Como desarrollador, quiero conectar la aplicación con Firebase Authentication, Cloud Firestore y Supabase Storage en sus planes gratuitos, para que los usuarios se puedan autenticar y los anuncios y fotos se guarden en la nube y se compartan entre todos.

**Requerimientos que cumple:** RNF-04 (Servicio de autenticación), RNF-05 (Almacenamiento en la nube), RNF-06 (Almacenamiento local), RNF-07 (Costo de servicios), RF-13 (Anuncios compartidos entre usuarios).

**Preguntas que el agente debe hacer antes de empezar:**
- ¿Ya creaste el proyecto en Firebase y en Supabase, o te guío paso a paso para crearlos? ¿Con qué nombre?
- Confirmar que el bucket se llamará `anuncios-fotos`.

**Criterios de aceptación** (todos deben cumplirse):
- [x] Dado el proyecto de Firebase está creado en plan Spark, cuando se revisa la consola, entonces están habilitados Authentication (correo/contraseña) y Firestore, sin facturación activa.
- [x] Dado el proyecto de Supabase está creado en plan Free, cuando se revisa la consola, entonces existe un bucket para las fotos de los anuncios.
- [x] Dado la aplicación está conectada, cuando se escribe un documento de prueba en Firestore desde la app, entonces el documento aparece en la consola de Firestore.
- [x] Dado la aplicación está conectada, cuando se sube una imagen de prueba a Supabase Storage desde la app, entonces la imagen aparece en el bucket.
- [x] Dado Firestore está configurado, cuando se revisa el código de inicialización, entonces la caché local de Firestore está configurada solo en memoria, para no guardar anuncios en el dispositivo (RNF-06).

**Qué ve y hace el usuario:**
- Historia técnica sin pantalla nueva. Al terminarla, la aplicación ya puede comunicarse con la nube aunque todavía no haya pantallas que lo usen.

**Cómo construirlo:**
1. Crear el proyecto en la consola de Firebase (plan Spark), registrar la app Android y agregar el archivo google-services.json al módulo app.
2. Agregar las dependencias de Firebase (BoM, Authentication, Firestore). Habilitar el proveedor «Correo electrónico/contraseña».
3. Configurar Firestore con caché solo en memoria (FirebaseFirestoreSettings con MemoryCacheSettings) para cumplir RNF-06: lo único guardado en el dispositivo es el tema.
4. Crear el proyecto en Supabase (plan Free) y un bucket «anuncios-fotos». Agregar la librería oficial de Supabase para Kotlin (módulo Storage).
5. Configurar Firebase Authentication como proveedor de autenticación de terceros en Supabase, para que las reglas de Storage reconozcan al usuario de Firebase (se usa en HU-16).
6. Guardar las llaves públicas de Supabase fuera del código versionado (por ejemplo en local.properties, ignorado por .gitignore) y leerlas desde BuildConfig.
7. Crear la clase de datos Anuncio: id, autorId, categoria, titulo, tituloMinusculas, descripcion, precio (entero), fotos (lista de URL), ciudad, correo, telefono (opcional), fechaPublicacion.
8. Crear AnunciosRepository y FotosRepository con funciones de prueba para escribir y leer, y verificar la conexión.

**Al terminar:** commit `HU-02: conexión con Firebase y Supabase` · ClickUp: **HU-02** → *finalizado* con su Lista de control marcada.

## M3 – HU-03 · Bienvenida y sesión persistente (4 h)

**Épica:** EP-01 · Autenticación y sesión · **Prioridad:** Alta

**Historia:** Como usuario, quiero que al abrir la aplicación, si ya había iniciado sesión, me lleve directo a Inicio, para no tener que escribir mi correo y contraseña cada vez que la uso.

**Requerimientos que cumple:** RF-01 (Pantalla de bienvenida), RF-02 (Sesión persistente), RF-09 (Acceso solo con sesión).

**Preguntas que el agente debe hacer antes de empezar:**
- Ninguna adicional: todo está definido en RF-01, RF-02 y RF-09.

**Criterios de aceptación** (todos deben cumplirse):
- [x] Dado abro la aplicación, cuando carga, entonces veo primero la pantalla de bienvenida con el nombre «Clasificados».
- [x] Dado ya inicié sesión antes y cerré la aplicación, cuando la vuelvo a abrir, entonces después de la bienvenida veo la pantalla Inicio sin que me pida credenciales.
- [x] Dado no tengo sesión activa, cuando abro la aplicación, entonces después de la bienvenida veo la pantalla Inicio de sesión.
- [x] Dado no tengo sesión activa, cuando intento llegar a cualquier pantalla distinta de Bienvenida, Inicio de sesión o Registro, entonces la aplicación me lleva a Inicio de sesión.

**Qué ve y hace el usuario:**
- El usuario toca el ícono de la aplicación.
- Aparece la pantalla de bienvenida (fondo azul, logo y «Clasificados»).
- Mientras se ve esa pantalla, la aplicación revisa si hay una sesión guardada.
- Si la hay, pasa a Inicio; si no, pasa a Inicio de sesión.

**Cómo construirlo:**
1. Crear el @Composable BienvenidaScreen (pantalla 00 de Figma) como startDestination del NavHost.
2. Crear SesionViewModel que consulte FirebaseAuth.getInstance().currentUser: si no es null, navegar a Inicio; si es null, navegar a Inicio de sesión.
3. Al navegar, usar popUpTo con inclusive = true para eliminar la bienvenida de la pila y que «atrás» no regrese a ella.
4. Proteger las rutas: antes de mostrar cualquier pantalla diferente de 00, 01 y 02, verificar que exista usuario; si no, redirigir a Inicio de sesión (RF-09).
5. Firebase guarda la sesión por sí solo; no se guarda nada adicional en el dispositivo.

**Al terminar:** commit `HU-03: bienvenida y sesión persistente` · ClickUp: **HU-03** → *finalizado* con su Lista de control marcada.

## M4 – HU-04 · Registrarme (6 h)

**Épica:** EP-01 · Autenticación y sesión · **Prioridad:** Alta

**Historia:** Como usuario nuevo, quiero crear una cuenta con mi nombre, correo, teléfono opcional y contraseña, para poder entrar a la aplicación y publicar anuncios.

**Requerimientos que cumple:** RF-03 (Registro de usuario), RF-04 (Validación del registro), RF-05 (Correo ya registrado), RNF-04 (Servicio de autenticación), RNF-17 (Manejo de contraseñas).

**Preguntas que el agente debe hacer antes de empezar:**
- Ninguna adicional: textos en la sección 1.5.

**Criterios de aceptación** (todos deben cumplirse):
- [ ] Dado estoy en Registro con todos los datos válidos, cuando toco «Crear cuenta», entonces la cuenta se crea en Firebase, quedo con la sesión iniciada y veo Inicio.
- [ ] Dado dejo el nombre vacío, cuando toco «Crear cuenta», entonces veo «El nombre es obligatorio» debajo del campo y no se crea la cuenta.
- [ ] Dado escribo un correo sin formato válido, cuando toco «Crear cuenta», entonces veo «Ingresa un correo válido».
- [ ] Dado escribo una contraseña de menos de 6 caracteres, cuando toco «Crear cuenta», entonces veo «La contraseña debe tener al menos 6 caracteres».
- [ ] Dado la confirmación no es igual a la contraseña, cuando toco «Crear cuenta», entonces veo «Las contraseñas no coinciden».
- [ ] Dado el correo ya pertenece a otra cuenta, cuando toco «Crear cuenta», entonces veo «Este correo ya está registrado» y no se crea otra cuenta.

**Qué ve y hace el usuario:**
- Desde Inicio de sesión, el usuario toca «Regístrate».
- Llena nombre completo, correo, teléfono (opcional), contraseña y confirmación.
- Toca «Crear cuenta».
- Si algún dato es inválido, ve el mensaje debajo del campo correspondiente y corrige.
- Si todo es válido, la cuenta se crea y entra directamente a Inicio.
- «¿Ya tienes cuenta? Inicia sesión» lo devuelve a Inicio de sesión.

**Cómo construirlo:**
1. Construir el @Composable RegistroScreen según la pantalla 02 de Figma, con OutlinedTextField (isError y supportingText para el mensaje de cada campo) y PasswordVisualTransformation en las contraseñas.
2. Crear un objeto Validador con funciones puras (sin dependencias de Android): validarNombre, validarCorreo (expresión usuario@dominio.ext), validarContrasena (mínimo 6), validarConfirmacion. Se reutilizan en otras historias y se prueban en HU-22.
3. RegistroViewModel valida todos los campos y expone un estado con el mensaje de error de cada campo.
4. Si todo es válido, llamar a FirebaseAuth.createUserWithEmailAndPassword. Al tener éxito, guardar nombre y teléfono en Firestore en la colección usuarios/{uid} (nunca la contraseña, RNF-17).
5. Capturar FirebaseAuthUserCollisionException para mostrar «Este correo ya está registrado».
6. Mostrar un indicador de carga mientras se crea la cuenta y deshabilitar el botón para evitar dobles envíos.

**Al terminar:** commit `HU-04: registro de usuario con validaciones` · ClickUp: **HU-04** → *finalizado* con su Lista de control marcada.

## M5 – HU-05 · Iniciar sesión (4 h)

**Épica:** EP-01 · Autenticación y sesión · **Prioridad:** Alta

**Historia:** Como usuario registrado, quiero iniciar sesión con mi correo y contraseña, para acceder a los anuncios y a mis publicaciones.

**Requerimientos que cumple:** RF-06 (Inicio de sesión), RF-07 (Validación del inicio de sesión), RNF-04 (Servicio de autenticación).

**Preguntas que el agente debe hacer antes de empezar:**
- Ninguna adicional: textos en la sección 1.5.

**Criterios de aceptación** (todos deben cumplirse):
- [ ] Dado tengo una cuenta, cuando ingreso correo y contraseña correctos y toco «Iniciar sesión», entonces veo la pantalla Inicio.
- [ ] Dado escribo un correo sin formato válido, cuando toco «Iniciar sesión», entonces veo «Ingresa un correo válido».
- [ ] Dado dejo la contraseña vacía, cuando toco «Iniciar sesión», entonces veo «La contraseña es obligatoria».
- [ ] Dado la contraseña es incorrecta o el correo no existe, cuando toco «Iniciar sesión», entonces veo «Correo o contraseña incorrectos» y no entro.

**Qué ve y hace el usuario:**
- El usuario escribe su correo y contraseña en la pantalla Inicio de sesión.
- Toca «Iniciar sesión».
- Si hay errores de formato, los ve debajo de cada campo.
- Si las credenciales no son correctas, ve «Correo o contraseña incorrectos».
- Si son correctas, entra a Inicio.
- Puede tocar «Regístrate» para ir a crear una cuenta.

**Cómo construirlo:**
1. Construir la pantalla según la pantalla 01 de Figma.
2. LoginViewModel usa Validador.validarCorreo y una validación de campo obligatorio para la contraseña.
3. Llamar a FirebaseAuth.signInWithEmailAndPassword. Ante FirebaseAuthInvalidCredentialsException o FirebaseAuthInvalidUserException mostrar «Correo o contraseña incorrectos» (no se indica cuál de los dos falló).
4. Al tener éxito, navegar a Inicio y limpiar la pila de navegación.

**Al terminar:** commit `HU-05: inicio de sesión con validaciones` · ClickUp: **HU-05** → *finalizado* con su Lista de control marcada.


## M6 – Revisión del estado del proyecto (29 de octubre)

No es una entrega, pero el docente revisa. Mostrar:

1. El tablero de ClickUp con las historias del Sprint 1 y sus estados reales.
2. El historial de commits en GitHub.
3. La app funcionando en el emulador hasta donde se haya avanzado.

Anotar las observaciones del docente y los compañeros en la tarea **«HITO · Revisión del estado del proyecto (29 de octubre)»** de ClickUp, y después pasarlas a este archivo (Anexo C).

## M7 – Reserva de correcciones y cierre del sprint (30 oct – 1 nov)

1. Aplicar las observaciones de la revisión del 29 de octubre (3 h reservadas).
2. Verificar que cada historia del sprint pasa todos sus criterios en modo claro **y** oscuro.
3. Dejar ClickUp al día. Las historias no terminadas pasan al Sprint 2 y se avisa a Oscar.
4. Commit final del sprint: `Sprint 1: cierre`.

---

# FASE 3 – SPRINT 2 (2 – 15 de noviembre de 2026)

*Se detalla al iniciar el sprint, con el mismo formato de momentos.*

Historias: HU-06 Explorar categorías · HU-07 Ver anuncios de una categoría · HU-08 Ver el detalle · HU-09 Ver mi perfil en Ajustes · HU-10 Cerrar sesión · HU-11 Tema claro/oscuro. También: actualizar Figma con los pendientes y la reserva de correcciones.

# FASE 4 – SPRINT 3 (16 – 29 de noviembre de 2026)

*Se detalla al iniciar el sprint.*

Historias: HU-12 Publicar (pasos 1 y 2) · HU-13 Fotos · HU-14 Ubicación, contacto y confirmación · HU-15 Buscar por título · HU-16 Reglas de seguridad. Revisión: 19 de noviembre.

# FASE 5 – SPRINT 4 (30 de noviembre – 13 de diciembre de 2026)

*Se detalla al iniciar el sprint.*

Historias: HU-17 Filtrar y ordenar · HU-18 Mis anuncios · HU-19 Editar · HU-20 Eliminar · HU-21 Sin conexión · HU-22 Pruebas unitarias. Revisión: 3 de diciembre. **Segunda entrega: 10 de diciembre** (toda la funcionalidad lista el 9).

# FASE 6 – CIERRE (14 – 17 de diciembre de 2026)

Correcciones finales, video demostrativo del funcionamiento y **entrega final el 17 de diciembre** (en línea).

---

# ANEXOS

## Anexo A – Instrucciones listas para copiar

**Iniciar un momento:**
```
Lee GUIA_DESARROLLO.md completo. Vamos a trabajar el momento M_ (HU-__).
Hazme primero las preguntas pendientes de ese momento y luego muéstrame tu plan.
No escribas código hasta que yo te diga "sí".
```

**Cuando algo falla:**
```
Probé el criterio __ de la HU-__ y pasó esto: ____.
Explícame la causa en palabras sencillas y propón la corrección antes de hacerla.
```

**Cerrar un momento:**
```
Ya probé todos los criterios de la HU-__ y pasan.
Dame el mensaje de commit, dime qué mover en ClickUp y registra el cierre en el Anexo C.
```

## Anexo B – Checklist para cerrar una historia

- [ ] Cumple todos sus criterios de aceptación.
- [ ] Funciona en tema claro y oscuro.
- [ ] Respeta el área segura.
- [ ] Corresponde a la pantalla de Figma.
- [ ] Textos iguales a la sección 1.5.
- [ ] Compila sin errores.
- [ ] Commit y push con el formato `HU-XX · Nombre de la historia: …` (ver 0.4), hecho por el agente.
- [ ] ClickUp en **finalizado** con la Lista de control marcada.

## Anexo C – Registro de cambios

| Fecha | Momento / HU | Qué se hizo | Commit | Observaciones |
|---|---|---|---|---|
| 28/10/2026 | — | Creación de esta guía | — | — |
| 28/10/2026 | M0 | Se agregaron `CLAUDE.md` (carga esta guía) y `AGENTS.md` (indica a Codex leerla) | `Docs: guía de desarrollo para agentes` | Pendiente: cuentas de Google y Supabase listas antes de M2 |
| 28/10/2026 | M1 / HU-01 | Proyecto base `com.univalle.clasificados` (Kotlin, minSdk 26, compileSdk/targetSdk 37): paquetes ui, viewmodel, data/repository, model y util; tema claro/oscuro con los tokens de Figma; edge-to-edge con Scaffold; NavHost y barra inferior de 4 pestañas con pantallas marcador; ícono adaptativo vectorial a partir del logo; `FormatoPrecio`. Versiones: Gradle 9.8.2, AGP 9.5.0, Kotlin 2.4.21, Compose BOM 2026.09.00, Activity 1.13.0, Navigation 2.10.2, Lifecycle 2.11.0, Core KTX 1.19.1, JUnit 4.13.2 | `HU-01: proyecto base MVVM con tema claro/oscuro y navegación` | Compila sin errores. Pendiente: Oscar prueba los criterios en el emulador |
| 28/10/2026 | M1 / HU-01 | AGP bajado de 9.5.0 a 9.4.0: Android Studio 2026.2.1 admite como máximo AGP 9.4.0. No aceptar el «AGP Upgrade Assistant» hasta actualizar Android Studio | `HU-01: AGP 9.4.0 compatible con Android Studio` | El proyecto sincroniza y se ejecuta desde Android Studio |
| 28/10/2026 | M1 / HU-01 | Barra inferior ajustada al componente de Figma (pantalla 03): íconos de línea de Figma como vectores (`ic_inicio`, `ic_mas`, `ic_mis_anuncios`, `ic_ajustes`), Publicar en píldora verde, borde superior y pestañas de 64 dp. Se quitó la librería de íconos de Material | `HU-01: barra inferior según diseño de Figma` | Verificado en el emulador (tema claro) |
| 28/10/2026 | M1 / HU-01 | **Cierre de M1:** Oscar probó en el emulador los 5 criterios de aceptación y todos pasan | `HU-01: cierre del momento M1` | ClickUp: HU-01 → *finalizado* con la Lista de control marcada |
| 28/10/2026 | M1 / HU-01 | Ajuste a la explicación técnica de HU-01: un ViewModel por pantalla (`InicioViewModel`, `PublicarViewModel`, `MisAnunciosViewModel`, `AjustesViewModel`) con estado en `StateFlow`, leído con `collectAsStateWithLifecycle()` en `InicioScreen`, `PublicarScreen`, `MisAnunciosScreen` y `AjustesScreen`; barra inferior con `ShortNavigationBar` de Material 3 (64 dp, igual a Figma) y Publicar con `minimumInteractiveComponentSize()` | `HU-01: ViewModels por pantalla y barra con ShortNavigationBar` | Verificado en el emulador: se ve igual a Figma y la navegación funciona |
| 28/10/2026 | HU-02 · Conexión con Firebase y Supabase | Firebase `clasificados-d5227` (plan Spark, organización correounivalle.edu.co, sin Analytics ni Gemini): Authentication correo/contraseña y Firestore en southamerica-east1 con reglas temporales. Supabase `clasificados` (ref `ruwqqnprgpjplvfgznup`, plan Free, São Paulo, cuenta GitHub nueva de Oscar porque la anterior ya tenía 2 proyectos Free): bucket público `anuncios-fotos`, políticas temporales en `pruebas/` y Firebase como auth de terceros. Código: `ClasificadosApp` (caché de Firestore solo en memoria), `SupabaseCliente`, `Anuncio`, `AnunciosRepository`, `FotosRepository`, prueba `ConexionNubeTest` y KDoc en todo el código. Versiones: Firebase BoM 35.1.0, Google Services 4.5.0, Supabase 3.8.0, Ktor OkHttp 3.5.1, coroutines-play-services 1.11.0, androidx.test runner 1.7.0 y ext-junit 1.3.0 | `HU-02 · Conexión con Firebase y Supabase: Auth, Firestore en memoria, Storage y prueba de conexión` | Las 3 pruebas pasan en el emulador; documento en `pruebas/` de Firestore e imagen `pruebas/prueba.png` en el bucket. Pendiente al cerrar: quitar reglas y políticas temporales. Para HU-16: Supabase pide el rol `authenticated` en los tokens de Firebase |
| 28/10/2026 | HU-02 · Conexión con Firebase y Supabase | **Cierre:** se quitaron los permisos temporales. Firestore: todo exige sesión (`request.auth != null`). Supabase: se borraron las políticas «Prueba HU-02 leer/subir/actualizar». `ConexionNubeTest` conserva solo la prueba de caché en memoria | `HU-02 · Conexión con Firebase y Supabase: cierre y retiro de permisos temporales` | El documento `pruebas/…` de Firestore y la imagen `pruebas/prueba.png` siguen en las consolas; Oscar decide si los borra |
| 28/10/2026 | Diseño / HU-01 | **Cambio de diseño aprobado por Oscar:** barra inferior con 3 pestañas (Inicio, Mis anuncios, Ajustes) y botón circular «Publicar» elevado y centrado (sección 1.9). Figma actualizado: componente «Barra inferior», espacio inferior en las pantallas y nuevos estados 03b, 05b, 05c, 06b, 10b, 11b, 13b, 14b; modo oscuro completo | — | **Pendiente en código:** ajustar la barra de HU-01 a este diseño (reabre HU-01 en ClickUp mientras se aplica) |
| 28/10/2026 | Guía | Sección 0.4: un solo commit por historia completa, y el agente hace el commit y el push. Nueva sección 0.6 con 8 reglas acordadas en el chat (prioridad de la guía, KDoc en todo el código, Figma manda, máximo 1 o 2 consultas a Figma, nombres de historias, sin códigos crípticos, solo proyectos de Clasificados en la nube, acciones que hace Oscar). Se corrigieron textos viejos: Anexo B (formato de commit), 1.11 (datos ya definidos) y casillas de Preparación y HU-02 | Va en el commit del ajuste de HU-01 | Aprobado por Oscar |
| 28/10/2026 | HU-01 · Proyecto base con arquitectura MVVM y tema visual | **Ajuste al cambio de diseño (sección 1.9):** barra con 3 pestañas (Inicio, Mis anuncios, Ajustes) en `ShortNavigationBar` y nuevo `BotonPublicar` (círculo verde de 60 dp, aro de 5 dp, sombra verde, + de 28 dp, «Publicar» 12 sp negrita) que flota centrado a 7 dp de la barra como botón flotante del `Scaffold`. Espacio final del contenido con scroll: 96 dp (`EspacioBotonPublicar`) en vez de 64, porque el botón ocupa unos 95 dp; arriba la pantalla usa todo el alto. **Arranque igual a la pantalla 00:** librería `core-splashscreen` 1.2.0, íconos `ic_arranque_claro` e `ic_arranque_oscuro`, temas de arranque claro y oscuro; en Android 13+ el arranque sigue el tema de la app y en Android 8 a 12 sigue el modo oscuro del teléfono. Se usaron 2 consultas a Figma (barra y pantalla 00) | `HU-01 · Proyecto base con arquitectura MVVM y tema visual: barra con botón Publicar flotante y arranque de la pantalla 00` | Verificado por el agente y por Oscar en el emulador, tema claro y oscuro. Resuelve el pendiente de la fila anterior; ClickUp: HU-01 vuelve a *finalizado* |
| 29/10/2026 | HU-03 · Bienvenida y sesión persistente | Pantalla 00 · Bienvenida (`BienvenidaScreen`, ícono `ic_categorias` de Figma, claro y oscuro, fondo azul detrás de la barra de estado). `AuthRepository` (escucha la sesión de Firebase) y `SesionViewModel` (la 00 se ve mínimo 1 segundo, contado desde que aparece; acordado con Oscar). Navegación: arranca en Bienvenida, va a Inicio o a Inicio de sesión quitando la 00 de la pila, y protege las rutas privadas (RF-09); la barra y el botón Publicar solo en pantallas privadas. Inicio de sesión como marcador (`InicioSesionScreen` + `LoginViewModel`) hasta HU-05. Barras del sistema transparentes en `MainActivity`. Pruebas: `BienvenidaScreenTest` y `SesionPersistenteTest` con un usuario de prueba de Firebase cuyos datos están en `local.properties` (no se sube). Versiones: pruebas de Compose del BOM 2026.09.00 y Espresso 3.7.0 (la 3.5 falla en Android 17) | `HU-03 · Bienvenida y sesión persistente: pantalla 00, sesión persistente y rutas protegidas` | Las 6 pruebas pasan; Oscar comprobó los criterios en el emulador. Al terminar las pruebas Android Studio desinstala la app: se reinstala con ▶ Run 'app' |
