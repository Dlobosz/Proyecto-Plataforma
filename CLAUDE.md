# Plataforma de Aprendizaje Progresivo de Programación

Contexto y plan de acción del proyecto. Este documento resume todas las decisiones tomadas
hasta ahora para que puedas continuar la construcción sin tener que volver a discutirlas.

## 1. Visión general

Herramienta web **personal** (uso propio de Diego, no portafolio ni producto para terceros)
para aprender lenguajes de programación mediante desafíos progresivos. Combina:

- **Contenido/referencia** del lenguaje (teoría breve).
- **Motor de desafíos** con dificultad creciente, organizados en unidades por lenguaje.
- **IDE embebido en el navegador** (editor de código + ejecución real + validación automática).

## 2. Objetivo y alcance del MVP

- **Objetivo:** que Diego lo use para reforzar su propio aprendizaje de Python y SQL
  (lenguajes que ya está incorporando en su plan de autoestudio).
- **Lenguajes del MVP:** Python y SQL únicamente.
- **Alcance del roadmap:** solo unidades/desafíos que se pueden validar ejecutando código
  contra Judge0 (comparación de output). Temas puramente teóricos sin desafío ejecutable
  (POO general, conexión a bases de datos, despliegue en la nube) quedan **fuera del MVP**,
  como visión futura.
- **Sin autenticación:** es una herramienta de un solo usuario, no hay login ni multi-tenancy.
- **Sin gamificación en el MVP:** nivel, XP, racha, medallas y puntos por pistas quedan
  explícitamente para la etapa de iteración continua, no para la primera versión funcional.
- **Deploy:** por ahora solo en local (máquina de Diego). La decisión de dónde alojarlo a
  largo plazo (cuenta AWS personal vs. Railway/Render/Vercel) queda pendiente hasta tener
  el MVP validado.

## 3. Stack tecnológico

| Capa | Tecnología | Notas |
|---|---|---|
| Backend | Spring Boot (Java 21) + Spring Data JPA | Reutiliza el stack que Diego ya domina de proyectos anteriores (Innovatech, examen de viajes, sistema hospitalario) |
| Base de datos | MySQL 8 | Local por ahora |
| Cliente HTTP a Judge0 | Spring WebFlux `WebClient` | Mismo patrón que usó en el microservicio de Reviews de su examen Full Stack |
| Ejecución de código | Judge0 CE vía RapidAPI (plan gratuito) | Ejecución **síncrona** con `wait=true` para evitar implementar polling |
| Frontend | React + Vite | Mismo framework que usó en Innovatech |
| Editor embebido | `@monaco-editor/react` | El mismo editor que usa VS Code |
| Ruteo frontend | `react-router-dom` | 3 rutas: dashboard, contenido, desafío |
| Cliente HTTP frontend | `axios` | |

**Paquete base del backend:** `com.diegolobos.plataforma` (placeholder — ajustar si el
group id real de Spring Initializr fue distinto, con find-and-replace en todo el proyecto).

## 4. Modelo de datos

Entidades JPA (paquete `model`), sin tabla de usuario (herramienta de un solo usuario):

- **`Language`**: `id`, `nombre`, `judge0LanguageId`
- **`Unit`**: `id`, `language` (FK), `titulo`, `orden`, `contenidoTeorico`, `estado`
  (enum `EstadoUnidad`: BLOQUEADA / DESBLOQUEADA / COMPLETADA)
- **`Challenge`**: `id`, `unit` (FK), `titulo`, `enunciado`, `dificultad` (enum `Dificultad`:
  FACIL / MEDIO / DIFICIL), `tipoValidacion` (enum `TipoValidacion`: OUTPUT / RESULTSET),
  `codigoInicial`, `pistas` (texto, sin costo de puntos por ahora)
- **`ValidationCase`**: `id`, `challenge` (FK), `tipo`, `payload` (columna `JSON` — ver
  formato abajo)
- **`Submission`**: `id`, `challenge` (FK), `codigoEnviado`, `resultado` (enum
  `ResultadoSubmission`: PASSED / FAILED / ERROR), `fecha`
- **`Note`** (apuntes personales): `id`, `unit` (FK), `contenido`, `fecha` — **el modelo y
  repositorio existen, pero todavía no tiene controller ni UI** (pendiente, ver backlog)

Relaciones: `Language` 1→N `Unit` → 1→N `Challenge` → 1→N `ValidationCase` y 1→N `Submission`.

Todas las relaciones `@ManyToOne` (hijo → padre) tienen `@JsonIgnore` para evitar recursión
infinita al serializar a JSON. Las entidades se devuelven directamente en los endpoints
(sin capa de DTOs para lecturas) — simplificación consciente para el MVP, marcada como
posible mejora futura.

### Formato del `payload` de `ValidationCase`

- **Tipo `OUTPUT` (Python):** `{"input": "...", "expectedOutput": "..."}`
- **Tipo `RESULTSET` (SQL):** `{"schemaSql": "...", "seedDataSql": "...", "expectedResult": "..."}`

## 5. Arquitectura del motor de ejecución (importante)

**Descubrimiento clave:** Judge0 no separa "schema" de "query" para SQL — solo ejecuta un
script completo con `sqlite3` y devuelve su stdout. Por lo tanto:

- Para desafíos **Python** (`OUTPUT`): el `source_code` enviado a Judge0 es el código del
  usuario tal cual; el `stdin` viene de `payload.input`; se compara el stdout (trimmed)
  contra `payload.expectedOutput` (trimmed).
- Para desafíos **SQL** (`RESULTSET`): el `source_code` enviado a Judge0 es la concatenación
  `schemaSql + seedDataSql + queryDelUsuario`; no hay stdin; se compara el stdout (trimmed)
  contra `payload.expectedResult` (trimmed). El formato de `expectedResult` debe coincidir
  con el output por defecto de `sqlite3` (una fila por línea, columnas separadas por `|`,
  sin headers).

Esto significa que **ambos tipos terminan comparando texto contra texto** — lo único que
cambia es cómo se arma el `source_code` antes de enviarlo. Implementado con el patrón
`SubmissionAssembler` (interfaz) + `PythonSubmissionAssembler` + `SqlSubmissionAssembler`.

El orquestador es `ExecutionService`: por cada `ValidationCase` del `Challenge`, arma el
código, llama a `Judge0Client.ejecutar(...)`, compara resultados, y al final guarda un
`Submission` con el resultado global (`PASSED` si todos los casos pasan, `FAILED` si alguno
falla sin error, `ERROR` si Judge0 devuelve un status distinto de "Accepted" (`status.id != 3`)
o no responde).

## 6. Endpoints REST

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/languages` | Lista de lenguajes |
| GET | `/api/languages/{languageId}/units` | Unidades de un lenguaje, ordenadas |
| GET | `/api/units/{id}` | Detalle de una unidad |
| GET | `/api/units/{unitId}/challenges` | Desafíos de una unidad |
| GET | `/api/challenges/{id}` | Detalle de un desafío |
| POST | `/api/challenges/{id}/submit` | Envía `{"codigo": "..."}`, devuelve resultado + detalle por caso |

CORS habilitado para `http://localhost:5173` (puerto por defecto de Vite) vía `WebConfig`.

## 7. Diseño de UI (3 pantallas)

1. **Dashboard / Panel de Progreso** (`/`): ruta de unidades por lenguaje, con íconos según
   `estado` (🔒 bloqueada, 🔄 en curso, ✅ completada). *Sin gamificación en el MVP* (no hay
   nivel, racha ni medallas todavía, aunque el boceto original de Diego las incluía).
2. **Vista de Contenido** (`/units/:unitId`): teoría de la unidad + lista de sus desafíos.
   Textos teóricos cortos, priorizando ejemplos de código.
3. **Vista de Desafío** (`/challenges/:challengeId`): split-screen con panel de contexto
   (instrucciones, dificultad, pistas colapsables) a la izquierda y editor Monaco + panel
   de resultados a la derecha. El divisor entre ambos paneles es arrastrable (implementado
   con mouse events nativos, sin librería extra).

## 8. Estado actual — qué ya está construido

**Backend** (`backend/src/main/java/com/diegolobos/plataforma/`):
- `model/`: las 6 entidades + 4 enums, con `@JsonIgnore` en las referencias inversas
- `repository/`: los 6 repositorios de Spring Data
- `config/`: `Judge0Config` (WebClient) y `WebConfig` (CORS)
- `dto/`: DTOs de request/response para submissions, y `dto/judge0/` para los DTOs de la
  API de Judge0
- `service/`: `Judge0Client`, `SubmissionAssembler` + sus 2 implementaciones, `ExecutionService`
- `controller/`: `LanguageController`, `UnitController`, `ChallengeController`, `SubmissionController`
- `seed/DataSeeder`: carga 1 unidad + 1 desafío de ejemplo por lenguaje al arrancar (si la
  tabla `languages` está vacía)
- `resources/application.properties`: config de MySQL y Judge0 (con placeholders a completar)

**Frontend** (`frontend/src/`):
- `api/client.js`: cliente axios centralizado con todas las llamadas al backend
- `pages/Dashboard.jsx`, `pages/ContentView.jsx`, `pages/ChallengeView.jsx`
- `App.jsx`: ruteo con react-router-dom

**Repositorio y entorno:** ya creados (Etapas 1 y 2 del plan completadas), estructura
monorepo con carpetas `backend/` y `frontend/`.

## 9. Advertencias técnicas a verificar

- Los `judge0LanguageId` usados en el seeder (**71 para Python, 82 para SQL/SQLite**) son
  los típicos de Judge0 CE, pero **hay que confirmarlos** contra `GET /languages` de la
  suscripción real de Judge0 en RapidAPI antes de usarlos en desafíos reales.
- El paquete `com.diegolobos.plataforma` es un placeholder — verificar que coincide con el
  group id real generado en Spring Initializr.
- `application.properties` tiene placeholders para la contraseña de MySQL y la API key de
  RapidAPI — deben completarse con los valores reales antes de correr el proyecto.

## 10. Backlog / iteración continua (después del MVP funcional)

- [ ] Cargar contenido real (más unidades y desafíos de Python y SQL) — Diego lo irá
      agregando con ayuda de la IA
- [ ] Lógica de desbloqueo automático: cambiar `estado` de una `Unit` de BLOQUEADA a
      DESBLOQUEADA cuando se completa la anterior (hoy el seeder las deja desbloqueadas a mano)
- [ ] Controller + UI para `Note` (apuntes personales) — el modelo ya existe
- [ ] Gamificación: nivel, XP, racha de días, medallas, descuento de puntos por pistas
- [ ] Capa de DTOs para las respuestas de lectura (hoy se devuelven entidades directamente)
- [ ] Definir y ejecutar el deploy final (cuenta AWS personal vs. Railway/Render/Vercel)

## 11. Cómo correr el proyecto en local

1. MySQL corriendo localmente, con la base de datos y credenciales configuradas en
   `application.properties`.
2. Backend: `mvn spring-boot:run` desde `backend/`.
3. Frontend: `npm install` (primera vez) y luego `npm run dev` desde `frontend/`.
4. Abrir `http://localhost:5173`.
