# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Build & Run
```bash
# Run the application (local profile active by default)
./gradlew bootRun

# Build (skips jOOQ codegen if DB is unavailable)
./gradlew build

# Run tests + generate JaCoCo coverage report
./gradlew test

# Run tests with coverage task
./gradlew testCoverage
```

### Database & Code Generation
The build chain requires a live DB: `processResources` → `flywayMigrate` → `jooqCodegen` → `compileJava`

```bash
# Apply Flyway migrations
./gradlew flywayMigrate

# Regenerate jOOQ classes from the current schema
./gradlew jooqCodegen
```

DB credentials must be supplied via environment variables or `gradle.properties`:
```
JDBC_CONNECTION_URL / jdbcConnectionUrl
JDBC_USERNAME       / jdbcUsername
JDBC_PASSWORD       / jdbcPassword
```

### Single Test
```bash
./gradlew test --tests "kr.modusplant.shared.constant.RegexTest"
```

## Architecture

The project follows **DDD + Clean Architecture**. The `src/main/java/kr/modusplant/` root contains four top-level packages:

| Package          | Role                                                                                                               |
|------------------|--------------------------------------------------------------------------------------------------------------------|
| `domains`        | Domain modules — each with its own layered sub-structure                                                           |
| `framework`      | Shared external library wiring (jOOQ, Spring Data, Redis)                                                          |
| `infrastructure` | Cross-cutting concerns: AOP logging, security, JWT, Swagger, async config                                          |
| `shared`         | Global objects: kernel VOs (`Email`, `Nickname`, `Password`), constants, Spring events, exceptions, ULID generator |

### Domain Internal Structure
Every domain under `domains/` follows this four-layer layout — dependency flows inward only:

```
[domain]
 ├─ adapter    # Orchestration controllers (inbound use-case calls, NO framework deps)
 ├─ domain     # Pure business logic: aggregates, entities, VOs, validation
 ├─ usecase    # Ports (interfaces), request/response DTOs, read/write models
 └─ framework  # Technical implementations:
               #   inbound/web/rest  → actual @RestController classes
               #   outbound/jpa      → JPA entities, mappers, Spring Data repos
               #   outbound/jooq     → jOOQ read repositories
               #   outbound/redis    → Redis cache services
```

Domains: `account` (sub-domains: `email`, `identity`, `normal`, `social`), `comment`, `member`, `notification`, `post`, `search`, `term`.

### Key Design Decisions
- **JPA vs jOOQ**: JPA/Hibernate for simple DML and dirty-checking; jOOQ for bulk reads, aggregations, and complex joins (eliminates N+1).
- **jOOQ generated code**: Auto-generated under `build/generated/sources/jooq/main` in the `kr.modusplant.jooq` package. Never edit manually — run `./gradlew jooqCodegen` to regenerate after schema changes.
- **MapStruct**: Configured with `unmappedTargetPolicy=ERROR` — every target field must be explicitly mapped or `@Mapping(target=..., ignore=true)` must be set.
- **IDs**: Entity identifiers use UUID v4, ULID (via `ulid-creator`), or a DB-managed identity (serial) depending on the entity.
- **Redis**: Used for response caching and JWT refresh-token blacklisting.
- **Async**: Virtual threads (Java 21) power asynchronous push notifications via Firebase FCM.
- **Events**: Domain side effects (image removal, abuse-report dashboards, notifications) are decoupled via Spring `ApplicationEvent`s defined in `shared/event/`.

### Spring Profiles
- `local` (default): `application-local.yml` + `application-secrets.yml` — debug logging, Swagger enabled, `flyway.clean-disabled=false`
- `dev` / `prod`: deployed environments; secrets injected via AWS SSM Parameter Store
- `secrets` is always included; it supplies sensitive values (DB credentials, keys, etc.) and is not tracked in the repository

### Flyway Migrations
- Schema DDL: `src/main/resources/db/migration/schema/` (prefix `V`)
- Seed data: `src/main/resources/db/migration/data/seed/` (prefix `V`)
- Java-based migrations: `src/main/java/db/migration/` (prefix `V`)

## Commit Convention

Format: `MP-{ticket} :{gitmoji}: {Type}: {description in Korean}`

| Gitmoji              | Type     |
|----------------------|----------|
| `:sparkles:`         | Feat     |
| `:bug:`              | Fix      |
| `:recycle:`          | Refactor |
| `:white_check_mark:` | Test     |
| `:memo:`             | Docs     |
| `:wrench:`           | Chore    |
| `:fire:`             | Remove   |
| `:ambulance:`        | HOTFIX!  |

Example: `MP-289 :bug: Fix: 게시글 여러 번 수정 시 발생하는 낙관적 락 에러 해결`
