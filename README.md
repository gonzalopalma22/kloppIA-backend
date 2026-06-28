# 📚 Klopp Backend — ApuntesIA

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud_Gateway-2025.0-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Gemini](https://img.shields.io/badge/Google_Gemini-AI-8E75B2?style=flat-square&logo=google&logoColor=white)

Backend de **Apuntesia**, una plataforma académica que permite a estudiantes organizar sus materias, subir apuntes en PDF y obtener resúmenes automáticos generados con inteligencia artificial (Google Gemini).

Construido con arquitectura de microservicios en **Spring Boot**, desplegable con Docker Compose.

---

## 🏗️ Arquitectura

```
Cliente
   │
   ▼
┌─────────────────┐         Puerto 8080
│   API Gateway   │  ◄──── Único punto de entrada
│  (Spring Cloud) │         Valida JWT y enruta
└────────┬────────┘
         │
   ┌─────┼──────────┐
   ▼     ▼          ▼
┌──────┐ ┌────────┐ ┌────────────┐
│ Auth │ │Materia │ │  Apunte    │
│:8081 │ │ :8082  │ │   :8083    │
└──────┘ └────────┘ └──────┬─────┘
                            │
                     ┌──────▼──────┐
                     │ Google      │
                     │ Gemini API  │
                     └─────────────┘

        Todos los servicios ──► PostgreSQL (Supabase)
```

| Servicio | Puerto interno | Responsabilidad |
|---|---|---|
| `api-gateway` | 8080 | Enrutamiento, validación JWT, propagación de headers de usuario |
| `auth-service` | 8081 | Registro, login y generación de tokens JWT |
| `materia-service` | 8082 | CRUD de materias por usuario |
| `apunte-service` | 8083 | Subida de PDFs, generación de resúmenes con Gemini |

---

## 🛠️ Stack Tecnológico

- **Java 17** + **Spring Boot 3.5**
- **Spring Cloud Gateway** — API Gateway reactivo
- **Spring Security** — autenticación y autorización
- **Spring Data JPA** — persistencia con Hibernate
- **JWT (jjwt 0.11.5)** — autenticación stateless
- **PostgreSQL** alojado en **Supabase**
- **Google Gemini API** — resúmenes de PDFs con IA
- **Lombok** — reducción de código boilerplate
- **Docker + Docker Compose** — orquestación de servicios

---

## 📋 Prerrequisitos

| Herramienta | Versión mínima | Necesario para |
|---|---|---|
| Docker | 24+ | Levantar con Compose |
| Docker Compose | 2.20+ | Orquestar servicios |
| JDK | 17+ | Ejecución local sin Docker |
| Maven | 3.8+ | Build local (incluido como `mvnw` en cada servicio) |

> **Nota:** En macOS con Apple Silicon (M1/M2/M3), asegúrate de tener Docker Desktop configurado para arquitectura `linux/arm64` o activa la emulación Rosetta.

---

## ⚙️ Configuración del entorno

Crea un archivo `.env` en la **raíz del proyecto** con las siguientes variables:

```env
# Base de datos (PostgreSQL / Supabase)
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<puerto>/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=<usuario>
SPRING_DATASOURCE_PASSWORD=<contraseña>

# Seguridad JWT
JWT_SECRET=<clave_secreta_larga_y_segura_minimo_32_caracteres>

# Google Gemini
GEMINI_API_KEY=<tu_api_key_de_gemini>
```

> ⚠️ **Nunca subas el archivo `.env` al repositorio.** Está incluido en `.gitignore`.

### Obtener credenciales

- **Supabase:** Crea un proyecto en [supabase.com](https://supabase.com), ve a *Settings → Database* y copia la Connection String (modo `Transaction pooler`).
- **Gemini API Key:** Genera una clave en [Google AI Studio](https://aistudio.google.com/app/apikey).
- **JWT Secret:** Puedes generar una clave segura con:
  ```bash
  openssl rand -base64 48
  ```

---

## 🚀 Instalación y ejecución

### Opción A — Docker Compose (recomendado)

```bash
# 1. Clonar el repositorio
git clone <url-del-repo>
cd klopp-backend

# 2. Configurar variables de entorno
cp .env.example .env
# Editar .env con tus credenciales reales

# 3. Construir imágenes y levantar todos los servicios
docker compose up --build
```

Para levantar en segundo plano:

```bash
docker compose up --build -d
```

Para detener los servicios:

```bash
docker compose down
```

Una vez levantado, el **API Gateway** estará disponible en:

```
http://localhost:8080
```

Verifica que todos los contenedores estén corriendo:

```bash
docker compose ps
```

Deberías ver cuatro servicios en estado `running`: `api-gateway`, `auth-service`, `materia-service` y `apunte-service`.

---

### Opción B — Ejecución local sin Docker

Cada servicio se puede levantar de forma independiente. Asegúrate de que las variables del `.env` estén disponibles en tu entorno o configúralas en `src/main/resources/application.properties` de cada servicio.

Levanta los servicios en este orden (el gateway debe ir último):

```bash
# Terminal 1 — Auth Service
cd auth-service
./mvnw spring-boot:run

# Terminal 2 — Materia Service
cd materia-service
./mvnw spring-boot:run

# Terminal 3 — Apunte Service
cd apunte-service
./mvnw spring-boot:run

# Terminal 4 — API Gateway
cd api-gateway
./mvnw spring-boot:run
```

En Windows, reemplaza `./mvnw` por `mvnw.cmd`.

---

## 📡 Endpoints de la API

Todos los endpoints se acceden a través del **API Gateway** en `http://localhost:8080`.

---

### 🔑 Auth Service — `/api/auth`

Rutas **públicas** (no requieren token).

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/auth/registro` | Registrar nuevo usuario |
| `POST` | `/api/auth/login` | Login, retorna JWT |

**Registro:**
```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan",
    "apellido": "Pérez",
    "email": "juan@ejemplo.com",
    "password": "miContraseña123"
  }'
# Respuesta: "Usuario registrado correctamente"
```

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "juan@ejemplo.com",
    "password": "miContraseña123"
  }'
# Respuesta:
# { "token": "eyJhbGciOiJIUzI1NiJ9...", "nombre": "Juan", "rol": "ROLE_USER" }
```

Guarda el `token` de la respuesta — lo necesitarás en todos los endpoints siguientes.

---

### 📚 Materia Service — `/api/materias`

Todos los endpoints requieren el header `Authorization: Bearer <token>`.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/materias` | Crear una materia |
| `GET` | `/api/materias` | Listar materias del usuario autenticado |
| `GET` | `/api/materias/{id}` | Obtener una materia por ID |
| `GET` | `/api/materias/buscar?nombre=X` | Buscar materias por nombre |
| `PUT` | `/api/materias/{id}` | Editar nombre/descripción de una materia |
| `DELETE` | `/api/materias/{id}` | Eliminar una materia |

**Crear materia:**
```bash
curl -X POST http://localhost:8080/api/materias \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Cálculo I", "descripcion": "Límites, derivadas e integrales"}'
```

**Listar materias:**
```bash
curl http://localhost:8080/api/materias \
  -H "Authorization: Bearer <token>"
```

**Buscar por nombre:**
```bash
curl "http://localhost:8080/api/materias/buscar?nombre=Cálculo" \
  -H "Authorization: Bearer <token>"
```

**Eliminar materia:**
```bash
curl -X DELETE http://localhost:8080/api/materias/1 \
  -H "Authorization: Bearer <token>"
```

---

### 📄 Apunte Service — `/api/materias/{materiaId}/apuntes`

Todos los endpoints requieren `Authorization: Bearer <token>`. Los apuntes se suben como `multipart/form-data`.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/materias/{materiaId}/apuntes` | Subir PDF (genera resumen con IA automáticamente) |
| `GET` | `/api/materias/{materiaId}/apuntes` | Listar apuntes de una materia |
| `GET` | `/api/materias/{materiaId}/apuntes/{id}` | Obtener un apunte por ID |
| `GET` | `/api/materias/{materiaId}/apuntes/buscar?titulo=X` | Buscar apuntes por título |
| `PUT` | `/api/materias/{materiaId}/apuntes/{id}` | Editar título de un apunte |
| `DELETE` | `/api/materias/{materiaId}/apuntes/{id}` | Eliminar un apunte |

**Subir apunte PDF:**
```bash
curl -X POST http://localhost:8080/api/materias/1/apuntes \
  -H "Authorization: Bearer <token>" \
  -F "titulo=Clase 1 - Introducción" \
  -F "archivo=@/ruta/local/apunte.pdf"
```

Respuesta esperada:
```json
{
  "id": 1,
  "titulo": "Clase 1 - Introducción",
  "resumen": "**Puntos clave:**\n- Concepto A...\n- Concepto B...",
  "nombreArchivo": "apunte.pdf",
  "materiaId": 1,
  "createdAt": "2026-06-28T10:30:00"
}
```

**Listar apuntes de una materia:**
```bash
curl http://localhost:8080/api/materias/1/apuntes \
  -H "Authorization: Bearer <token>"
```

---

## 🔐 Flujo de seguridad

1. El cliente obtiene un **JWT** via `POST /api/auth/login`.
2. Incluye el token en el header `Authorization: Bearer <token>` en cada request.
3. El **API Gateway** intercepta todas las rutas excepto `/api/auth/*`, valida el JWT y extrae los claims.
4. Los claims (`X-User-Id`, `X-User-Email`, `X-User-Rol`) se propagan como headers internos hacia los microservicios.
5. Cada microservicio confía en estos headers para identificar al usuario sin revalidar el token.

---

## 🧪 Tests

Cada microservicio incluye tests unitarios con **JUnit 5** y **Mockito** que cubren los casos de éxito y error del servicio de negocio.

### Ejecutar tests de un servicio

```bash
# Auth Service
cd auth-service
./mvnw test

# Materia Service
cd materia-service
./mvnw test

# Apunte Service
cd apunte-service
./mvnw test
```

### Ejecutar todos los tests desde la raíz

```bash
for service in auth-service materia-service apunte-service; do
  echo "=== Tests: $service ==="
  (cd $service && ./mvnw test)
done
```

### Cobertura de tests por servicio

#### `auth-service` — `AuthServiceTest`

| Test | Descripción |
|---|---|
| `registro_exitoso` | Verifica que un usuario nuevo se guarde correctamente en la BD |
| `registro_emailDuplicado_lanzaExcepcion` | Verifica que no se registre un email ya existente |
| `login_exitoso` | Verifica que login retorne token y datos del usuario |
| `login_usuarioNoEncontrado_lanzaExcepcion` | Verifica error cuando el email no existe |
| `login_contrasenaIncorrecta_lanzaExcepcion` | Verifica error cuando la contraseña no coincide |

#### `materia-service` — `MateriaServiceTest`

| Test | Descripción |
|---|---|
| `crear_exitoso` | Verifica creación de una materia y retorno del DTO |
| `listarPorUsuario_retornaLista` | Verifica listado de materias del usuario autenticado |
| `obtenerPorId_exitoso` | Verifica obtención de una materia por ID |
| `obtenerPorId_noEncontrado_lanzaExcepcion` | Verifica error cuando la materia no existe |
| `obtenerPorId_sinPermiso_lanzaExcepcion` | Verifica que un usuario no acceda a materias ajenas |
| `eliminar_exitoso` | Verifica eliminación de una materia propia |
| `eliminar_sinPermiso_lanzaExcepcion` | Verifica que no se eliminen materias de otro usuario |

#### `apunte-service` — `ApunteServiceTest`

| Test | Descripción |
|---|---|
| `crear_exitoso` | Verifica subida de PDF, llamada a Gemini y guardado del apunte |
| `listarPorMateria_retornaLista` | Verifica listado de apuntes de una materia |
| `obtenerPorId_exitoso` | Verifica obtención de un apunte por ID |
| `obtenerPorId_noEncontrado_lanzaExcepcion` | Verifica error cuando el apunte no existe |
| `obtenerPorId_sinPermiso_lanzaExcepcion` | Verifica que un usuario no acceda a apuntes ajenos |
| `eliminar_exitoso` | Verifica eliminación de un apunte propio |
| `eliminar_sinPermiso_lanzaExcepcion` | Verifica que no se eliminen apuntes de otro usuario |

---

## 🤖 Integración con Google Gemini

Cuando se sube un apunte PDF, el `apunte-service` convierte el archivo a Base64 y lo envía a la API de Google Gemini con el siguiente prompt:

> *"Eres un asistente académico. Resume el siguiente apunte en puntos clave claros y concisos, organizados por temas principales."*

El resumen generado se almacena junto con los metadatos del apunte en PostgreSQL.

---

## 🗂️ Estructura del proyecto

```
klopp-backend/
├── .env                          # Variables de entorno (no subir al repo)
├── docker-compose.yml            # Orquestación de todos los servicios
├── api-gateway/                  # Enrutador y validador de JWT
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/klopp/api_gateway/
│       ├── config/CorsConfig.java
│       └── security/JwtFilter.java
├── auth-service/                 # Registro, login y JWT
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/klopp/auth_service/
│       │   ├── controller/       # AuthController, AdminUserController
│       │   ├── dto/              # LoginDTO, RegistroDTO, JwtResponseDTO
│       │   ├── model/            # Usuario, Rol
│       │   ├── repository/       # UsuarioRepository
│       │   ├── security/         # JwtService, SecurityConfig, GatewayHeaderFilter
│       │   └── service/          # AuthService
│       └── test/java/com/klopp/auth_service/
│           └── service/AuthServiceTest.java
├── materia-service/              # CRUD de materias
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/klopp/materia_service/
│       │   ├── controller/       # MateriaController
│       │   ├── dto/              # MateriaDTO, MateriaResponseDTO
│       │   ├── model/            # Materia
│       │   ├── repository/       # MateriaRepository
│       │   ├── security/         # SecurityConfig, GatewayHeaderFilter
│       │   └── service/          # MateriaService
│       └── test/java/com/klopp/materia_service/
│           └── service/MateriaServiceTest.java
└── apunte-service/               # Apuntes PDF + resúmenes IA
    ├── Dockerfile
    ├── pom.xml
    └── src/
        ├── main/java/com/klopp/apunte_service/
        │   ├── controller/       # ApunteController
        │   ├── dto/              # ApunteDTO, ApunteResponseDTO
        │   ├── model/            # Apunte
        │   ├── repository/       # ApunteRepository
        │   ├── security/         # SecurityConfig, GatewayHeaderFilter, AppConfig
        │   └── service/          # ApunteService, GeminiService
        └── test/java/com/klopp/apunte_service/
            └── service/ApunteServiceTest.java
```

---

## 🐛 Solución de problemas

**Los contenedores no levantan:**
Verifica que el archivo `.env` esté en la raíz del proyecto y que todas las variables estén definidas.

**Error de conexión a la base de datos:**
Confirma que la URL de Supabase sea la del modo `Transaction pooler` (puerto 5432) y no la del `Session pooler`. La URL debe incluir `?sslmode=require`.

**`apunte-service` retorna error al generar resumen:**
Verifica que `GEMINI_API_KEY` sea válida y que el archivo subido sea un PDF con contenido de texto (los PDFs escaneados solo como imagen pueden no extraerse correctamente).

**Puerto 8080 ya en uso:**
Detén el proceso que ocupa ese puerto o cambia el puerto del gateway en `docker-compose.yml`:
```yaml
ports:
  - "9090:8080"   # cambia 9090 por el puerto libre que prefieras
```

---

## 📄 Licencia

© 2026 [gonzaloPalma22](https://github.com/gonzaloPalma22). Todos los derechos reservados.