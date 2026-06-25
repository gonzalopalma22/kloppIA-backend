# 📚 Klopp Backend — Apuntesia

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud_Gateway-2025.0-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supabase-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![Gemini](https://img.shields.io/badge/Google_Gemini-AI-8E75B2?style=flat-square&logo=google&logoColor=white)

Backend de **Apuntesia**, una plataforma académica que permite a los estudiantes organizar sus materias, subir apuntes en PDF y obtener resúmenes automáticos generados con inteligencia artificial (Google Gemini).

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

### Microservicios

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

## 📋 Requisitos

- Docker y Docker Compose instalados
- JDK 17+ (solo si se corre sin Docker)
- Maven 3.8+ (incluido en cada servicio como wrapper `mvnw`)

---

## ⚙️ Configuración

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
# Base de datos (PostgreSQL / Supabase)
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>:<puerto>/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=<usuario>
SPRING_DATASOURCE_PASSWORD=<contraseña>

# Seguridad JWT
JWT_SECRET=<clave_secreta_larga_y_segura>

# Google Gemini
GEMINI_API_KEY=<tu_api_key_de_gemini>
```

> ⚠️ **Nunca subas el archivo `.env` al repositorio.** Asegúrate de que esté en `.gitignore`.

Incluye en el repositorio un archivo `.env.example` como referencia para otros desarrolladores:

```env
# Base de datos (PostgreSQL / Supabase)
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

# Seguridad JWT
JWT_SECRET=

# Google Gemini
GEMINI_API_KEY=
```

---

## 🚀 Cómo levantar el proyecto

### Con Docker Compose (recomendado)

```bash
# Clonar el repositorio
git clone <url-del-repo>
cd klopp-backend

# Configurar variables de entorno
cp .env.example .env
# Editar .env con tus credenciales

# Construir y levantar todos los servicios
docker compose up --build
```

El API Gateway quedará disponible en `http://localhost:8080`.

### Sin Docker (desarrollo local)

Levantar cada servicio por separado desde su directorio:

```bash
cd auth-service && ./mvnw spring-boot:run
cd materia-service && ./mvnw spring-boot:run
cd apunte-service && ./mvnw spring-boot:run
cd api-gateway && ./mvnw spring-boot:run
```

---

## 📡 Endpoints de la API

Todos los endpoints pasan por el API Gateway en `http://localhost:8080`.

### Autenticación — `/api/auth`

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `POST` | `/api/auth/registro` | ❌ | Registrar nuevo usuario |
| `POST` | `/api/auth/login` | ❌ | Login, retorna JWT |

**Ejemplo — Registro:**
```json
POST /api/auth/registro
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "email": "juan@ejemplo.com",
  "password": "miContraseña123"
}
```

**Ejemplo — Login:**
```json
POST /api/auth/login
{
  "email": "juan@ejemplo.com",
  "password": "miContraseña123"
}
// Respuesta:
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

### Materias — `/api/materias`

Requieren header `Authorization: Bearer <token>`.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/materias` | Crear una materia |
| `GET` | `/api/materias` | Listar materias del usuario |
| `GET` | `/api/materias/{id}` | Obtener una materia por ID |
| `GET` | `/api/materias/buscar?nombre=X` | Buscar materias por nombre |
| `DELETE` | `/api/materias/{id}` | Eliminar una materia |

---

### Apuntes — `/api/materias/{materiaId}/apuntes`

Requieren header `Authorization: Bearer <token>`. Los apuntes se suben como `multipart/form-data`.

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/api/materias/{materiaId}/apuntes` | Subir un apunte PDF (genera resumen automático con IA) |
| `GET` | `/api/materias/{materiaId}/apuntes` | Listar apuntes de una materia |
| `GET` | `/api/materias/{materiaId}/apuntes/{id}` | Obtener un apunte por ID |
| `GET` | `/api/materias/{materiaId}/apuntes/buscar?titulo=X` | Buscar apuntes por título |
| `DELETE` | `/api/materias/{materiaId}/apuntes/{id}` | Eliminar un apunte |

**Ejemplo — Subir apunte:**
```bash
curl -X POST http://localhost:8080/api/materias/1/apuntes \
  -H "Authorization: Bearer <token>" \
  -F "titulo=Clase 1 - Introducción" \
  -F "archivo=@apunte.pdf"
```

**Respuesta:**
```json
{
  "id": 1,
  "titulo": "Clase 1 - Introducción",
  "resumen": "**Puntos clave:**\n- Concepto A...\n- Concepto B...",
  "nombreArchivo": "apunte.pdf",
  "materiaId": 1,
  "createdAt": "2026-06-25T10:30:00"
}
```

---

## 🔐 Seguridad

El flujo de autenticación funciona así:

1. El cliente obtiene un **JWT** mediante `POST /api/auth/login`.
2. Incluye el token en el header `Authorization: Bearer <token>` en cada request.
3. El **API Gateway** intercepta todas las rutas (excepto `/api/auth/*`), valida el JWT y extrae los claims.
4. Los claims del usuario (`X-User-Id`, `X-User-Email`, `X-User-Rol`) se propagan como headers internos hacia los microservicios.
5. Cada servicio confía en estos headers para identificar al usuario sin revalidar el token.

---

## 🗂️ Estructura del Proyecto

```
klopp-backend/
├── api-gateway/          # Enrutador y validador de JWT
│   └── src/main/java/com/klopp/api_gateway/
│       └── security/JwtFilter.java
├── auth-service/         # Registro, login y JWT
│   └── src/main/java/com/klopp/auth_service/
│       ├── controller/   # AuthController, AdminUserController
│       ├── service/      # AuthService
│       ├── model/        # Usuario, Rol
│       └── security/     # JwtService, SecurityConfig
├── materia-service/      # CRUD de materias
│   └── src/main/java/com/klopp/materia_service/
│       ├── controller/   # MateriaController
│       ├── service/      # MateriaService
│       └── model/        # Materia
├── apunte-service/       # Apuntes PDF + resúmenes IA
│   └── src/main/java/com/klopp/apunte_service/
│       ├── controller/   # ApunteController
│       ├── service/      # ApunteService, GeminiService
│       └── model/        # Apunte
├── docker-compose.yml
└── .env
```

---

## 🤖 Integración con Google Gemini

Cuando se sube un apunte PDF, el `apunte-service` envía el archivo en formato Base64 a la API de Google Gemini con el siguiente prompt:

> *"Eres un asistente académico. Resume el siguiente apunte en puntos clave claros y concisos, organizados por temas principales."*

El resumen generado se almacena junto con los metadatos del apunte en la base de datos.

---

## 🧪 Tests

Cada servicio incluye una clase de test de integración base. Para ejecutarlos:

```bash
cd <servicio>
./mvnw test
```

---

## 📄 Licencia

Este proyecto fue desarrollado como proyecto académico. Todos los derechos reservados.
