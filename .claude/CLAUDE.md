# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Build & Run
```bash
# Run the application (local profile active by default)
./gradlew bootRun --no-daemon

# Build
./gradlew build --no-daemon

# Run tests + generate JaCoCo coverage report
./gradlew test --no-daemon

# Run tests with coverage task
./gradlew testCoverage --no-daemon
```

### Database & Code Generation

Compilation requires a live DB; its task chain and credentials are in `.claude/rules/db-migration-details.md`.

```bash
# Regenerate jOOQ classes from the current schema
./gradlew jooqCodegen --no-daemon
```

### Single Test
```bash
./gradlew test --tests "kr.modusplant.shared.constant.RegexTest" --no-daemon
```

## Architecture

The project follows **DDD + Clean Architecture**. The `src/main/java/kr/modusplant/` root contains four top-level packages:

| Package            | Role                                                                                                               |
|--------------------|--------------------------------------------------------------------------------------------------------------------|
| `domains`          | Domain modules — each with its own layered sub-structure                                                           |
| `infrastructure`   | Cross-cutting concerns: AOP logging, security, JWT, Swagger, async config                                          |
| `shared`           | Global objects: kernel VOs (`Email`, `Nickname`, `Password`), constants, Spring events, exceptions, ULID generator |
| `shared.framework` | Shared external library wiring (jOOQ, Spring Data, Redis)                                                          |

### Domain Internal Structure

Every domain under `domains/` follows this four-layer layout — dependency flows inward only:

```
[domain]
 ├─ adapter    # Orchestration controllers (inbound use-case calls, NO framework deps;
               #   `shared.framework` belongs to `shared` first, so depending on it is allowed)
 ├─ domain     # Pure business logic: aggregates, entities, VOs, validation
 ├─ usecase    # Ports (interfaces), request/response DTOs, read/write models
 └─ framework  # Technical implementations:
               #   inbound/web/rest  → actual @RestController classes
               #   outbound/jpa      → JPA entities, mappers, Spring Data repos
               #   outbound/jooq     → jOOQ read repositories
               #   outbound/redis    → Redis cache services
```

### Predefined Domain & Infrastructure Area Names

- Domains: `email`, `identity`, `normal-identity`, `social-identity` (all part of the larger `account` domain), `comment`, `member`, `notification`, `post`, `search`, `term`.
- Path mapping for the `account` domain, where the domain name differs from its directory name:
  - `email` → `domains/account/email/`
  - `identity` → `domains/account/identity/`
  - `normal-identity` → `domains/account/normal/`
  - `social-identity` → `domains/account/social/`
- Infrastructure areas: `advice`, `aop`, `config`, `file`, `jwt`, `monitor`, `security`, `transliterate` (all under `infrastructure/<area>/`).

### Technical Design Decisions
- **JPA vs jOOQ**: JPA/Hibernate for simple DML and dirty-checking; jOOQ for bulk reads, aggregations, and complex joins (eliminates N+1).
- **jOOQ generated code**: Auto-generated under `build/generated/sources/jooq/main` in the `kr.modusplant.jooq` package. Never edit manually.
- **MapStruct**: Configured with `unmappedTargetPolicy=ERROR` — every target field must be explicitly mapped or `@Mapping(target=..., ignore=true)` must be set.
- **Redis**: Used for response caching and JWT access-token blacklisting.

### Project Conventions
- **IDs**: Entity identifiers use UUID v4, ULID (via `ulid-creator`), or a DB-managed identity (serial) depending on the entity.
- **Events**: Domain side effects (image removal, abuse-report dashboards, notifications) are decoupled via plain event objects published through Spring's `ApplicationEventPublisher`, defined in each domain's `domain/event/`, `infrastructure/<area>/event/`, and `shared/framework/<library>/event/`.
- **Regex constants**: Regular expressions live in `shared/constant/Regex` — `REGEX_*` for annotations and the precompiled `PATTERN_*` for method bodies. Add new expressions there instead of writing them inline.
  Flyway Java migrations under `db/migration` are the exception and keep their own inline patterns.

### Spring Profiles
- `local` (default): `application-local.yml` + `application-secrets.yml` — debug logging, Swagger enabled
- `dev` / `prod`: deployed environments; secrets injected via AWS SSM Parameter Store
- `secrets` is always included; it supplies sensitive values (DB credentials, keys, etc.) and is not tracked in the repository

## Convention & Skill Docs

Detailed working rules live under `.claude/`:

- `.claude/rules/*.md` — per-area convention specs, auto-attached by path glob.
- `.claude/documents/*.md` — on-demand reference docs: 
  - `test-domain-<domain>-feature.md` / `test-infra-<area>-feature.md` (per-area test features, consumed by the `test-code-synchronizer` agent)
  - `observability-tagging-architecture.md` (backend-to-monitoring-stack identity/correlation tagging)
  - `performance-test-architecture.md` (nGrinder deployment, host log locations, and Groovy script-writing gotchas)
  - `open-authentication-architecture.md` (OAuth 2.0 authorization-code flow across `domains/account/social`, `infrastructure/security`, and `infrastructure/jwt`)
  - `data-deletion-architecture.md` (hard-delete/soft-delete policy, archiving, per-flow related-data handling, and cascade rules)
- `.claude/skills/*/SKILL.md` — task workflows.
- `.claude/skills/<skill>/workflow-diagram.html` — human-only visual view of that skill's `SKILL.md`. Never read it — `SKILL.md` is canonical. Reading it is permitted only to update it to match `SKILL.md`.

## Precaution

- **Accumulated:** During a turn, `allowed-tools` / `disallowed-tools` will be accumulated as the skill invokes other skills, which may cause unexpected permission errors.