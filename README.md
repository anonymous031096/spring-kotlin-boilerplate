# Spring Kotlin Boilerplate

A production-ready **Spring Boot** API boilerplate in **Kotlin**, with JWT auth, RBAC, Redis session cache, rate limiting, and modular architecture. Use it as a starting point for your next backend or share it with the community.

---

## Features

| Feature | Description |
|--------|-------------|
| **Kotlin + Spring Boot 4** | Kotlin 2.2, Spring Boot 4, Java 21 |
| **JWT (access + refresh)** | Separate key pairs; refresh tokens cannot call APIs |
| **IAM (RBAC)** | Users, roles, permissions; signup gets default `USER` role |
| **Session & revocation** | User sessions with Redis cache for fast revocation |
| **Rate limiting** | IP-based rate limiting (Bucket4j, optional Redis backend) |
| **API docs** | Swagger UI / OpenAPI 3 |
| **Observability** | Actuator, health, optional metrics |
| **Database** | PostgreSQL + Flyway migrations, JPA with auditing |
| **Docker** | `docker-compose` for Postgres and Redis |

---

## Quick start

### Prerequisites

- **Java 21**
- **Docker** (for Postgres and Redis, or install them locally)

### 1. Clone and configure

```bash
git clone https://github.com/anonymous031096/spring-kotlin-boilerplate.git
cd spring-kotlin-boilerplate
cp .env.example .env
# Edit .env if needed (DB, Redis, port, Swagger)
```

### 2. Start dependencies (Docker)

```bash
docker-compose up -d
```

### 3. JWT keys (first run)

The app expects PEM key pairs under `keys/`:

- `keys/access-private.pem`, `keys/access-public.pem` — access token
- `keys/refresh-private.pem`, `keys/refresh-public.pem` — refresh token

Generate (example with OpenSSL):

```bash
mkdir -p keys
# Access token key pair
openssl genrsa -out keys/access-private.pem 2048
openssl rsa -in keys/access-private.pem -pubout -out keys/access-public.pem
# Refresh token key pair
openssl genrsa -out keys/refresh-private.pem 2048
openssl rsa -in keys/refresh-private.pem -pubout -out keys/refresh-public.pem
```

Paths and expiry are configurable in `application.yaml` under `jwt.*`.

### 4. Run the application

```bash
./gradlew bootRun
```

- **API base:** `http://localhost:8080` (or `SERVER_PORT` from `.env`)
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **Health:** `http://localhost:8080/actuator/health`

### 5. Try the API

- **Sign up:** `POST /auth/signup` with `username`, `password`, `name`
- **Sign in:** `POST /auth/signin` with `username`, `password` → returns `accessToken`, `refreshToken`
- **Refresh:** `POST /auth/refresh` with `refreshToken`, `deviceId`
- Use `Authorization: Bearer <accessToken>` for protected endpoints (e.g. `/users/me`, `/roles`)

---

## Configuration

| Variable | Description | Default |
|----------|-------------|--------|
| `SERVER_PORT` | HTTP port | `8080` |
| `DB_HOST`, `DB_PORT`, `DB_NAME` | PostgreSQL | `localhost`, `5432`, `boilerplate` |
| `DB_USERNAME`, `DB_PASSWORD` | DB credentials | `postgres`, `postgres` |
| `REDIS_HOST`, `REDIS_PORT` | Redis | `localhost`, `6379` |
| `SWAGGER_ENABLED` | Enable Swagger UI / api-docs | `true` |

Copy `.env.example` to `.env` and override as needed. The app uses these for datasource and Redis URLs.

---

## API overview

| Area | Endpoints |
|------|-----------|
| **Auth** | `POST /auth/signup`, `POST /auth/signin`, `POST /auth/refresh`, `POST /auth/logout` |
| **Users** | `GET /users`, `GET /users/me`, `GET /users/{id}`, `PUT /users/{id}`, `PUT /users/me/password` |
| **Roles** | `GET /roles`, `GET /roles/{id}`, `POST /roles`, `PUT /roles/{id}`, `DELETE /roles/{id}` |
| **Permissions** | `GET /permissions` |

Protected endpoints require `Authorization: Bearer <accessToken>`. Role/permission checks use `@PreAuthorize` (e.g. `hasAuthority('role:read')`).

---

## Project structure

```
com.example.boilerplate
├── BoilerplateApplication.kt
├── modules/
│   └── iam/             # IAM: auth, users, roles, permissions
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       ├── dto/
│       ├── security/     # JWT filter, session cache, JwtService
│       ├── config/       # JWT properties
│       └── mapper/
└── shared/              # Shared DTOs, base entity, filters, audit
    ├── dto/
    ├── entity/
    ├── security/
    ├── filter/
    ├── ratelimit/
    ├── config/          # Security, OpenAPI, JPA audit, etc.
    ├── exception/
    └── audit/
```

---

## Tests

```bash
./gradlew test
```

---

## Tech stack (summary)

- **Runtime:** Java 21, Kotlin 2.2
- **Framework:** Spring Boot 4, Spring Security, Spring Data JPA
- **DB:** PostgreSQL, Flyway
- **Cache/sessions:** Redis (optional for session cache)
- **Auth:** JWT (JJWT), access + refresh with separate key pairs
- **API:** Spring MVC, validation, OpenAPI 3 (Springdoc)
- **Rate limiting:** Bucket4j
- **Mapping:** MapStruct (kapt)

---

## License

This project is licensed under the [MIT License](LICENSE).

---

## Contributing

1. Fork the repo.
2. Create a branch, make your changes, run `./gradlew test`.
3. Open a Pull Request with a short description of the change.

---

**Enjoy building with Spring and Kotlin.**
