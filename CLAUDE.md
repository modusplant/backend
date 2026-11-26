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

The project follows **DDD + Clean Architecture**. The `src/main/java/kr/modusplant/` root contains four top-level
packages:

| Package | Role |
|---|---|
| `domains` | Domain modules — each with its own layered sub-structure |
| `infrastructure` | Cross-cutting concerns: AOP logging, security, JWT, Swagger, async config |
| `shared` | Global objects: kernel VOs (`Email`, `Nickname`, `Password`), constants, Spring events, exceptions, ULID generator |
| `shared.framework` | Shared external library wiring (jOOQ, Spring Data, Redis) |

### Domain Internal Structure

Every domain under `domains/` follows this four-layer layout — dependency flows inward only:

```
[domain]/
├ adapter/ # Orchestration controllers (inbound use-case calls, NO framework deps;
│# `shared.framework` belongs to `shared` first, so depending on it is allowed),
│# plus mapper port implementations, precondition helpers, event listeners, translators
├ domain/ # Pure business logic: aggregates, entities, VOs, events, validation
├ usecase/ # Ports (interfaces), request/response DTOs, records, read models
└ framework/ # Technical implementations
 ├ inbound/
 │└ web/
 │ ├ rest/ # Actual @RestController classes
 │ └ cache/ # Conditional-GET (ETag / Last-Modified) evaluation
 └ outbound/
  ├ jpa/ # JPA entities, mappers, Spring Data repos
  ├ jooq/ # jOOQ read repositories
  ├ persistence/ # jpa/ and jooq/ grouped under one package (account domains)
  ├ redis/ # Redis-backed repositories
  └ scheduler/ # @Scheduled jobs
```

### Predefined Domain & Infrastructure Area Names

- Domains: `email-identity`, `identity`, `normal-identity`, `social-identity` (all part of the larger `account` domain),
  `comment`, `member`, `notification`, `post`, `search`, `term`.
- Path mapping for the `account` domain, where the domain name differs from its directory name:
  - `identity` → `domains/account/identity/`
  - `email-identity` → `domains/account/email/`
  - `normal-identity` → `domains/account/normal/`
  - `social-identity` → `domains/account/social/`
- Infrastructure areas: `advice`, `aop`, `config`, `file`, `jwt`, `monitor`, `normalization`, `security`,
  `prohibitedword` (all under `infrastructure/<area>/`).

### Technical Design Decisions
- **JPA vs jOOQ**: JPA/Hibernate for simple DML and dirty-checking; jOOQ for bulk reads, aggregations, and complex joins
  (eliminates N+1).
- **jOOQ generated code**: Auto-generated under `build/generated/sources/jooq/main` in the `kr.modusplant.jooq` package.
  Never edit manually.
- **Redis**: Used for multi-purpose caching (e.g. concurrency control) and JWT access-token blacklisting.

### Project Conventions
- **IDs**: Entity identifiers use UUID v4, ULID (via `ulid-creator`), or a DB-managed identity (serial) depending on the
  entity.
- **Events**: Domain side effects (image removal, abuse-report dashboards, notifications) are decoupled via plain event
  objects published through Spring's `ApplicationEventPublisher`, defined in each domain's `domain/event/`,
  `infrastructure/<area>/event/`, and `shared/framework/<library>/event/`.
- **Regex constants**: Regular expressions live in `shared/constant/Regex` — `REGEX_*` for annotations and the
  precompiled `PATTERN_*` for method bodies. Add new expressions there instead of writing them inline. Flyway Java
  migrations under `db/migration` are the exception and keep their own inline patterns.

### Spring Profiles
- `local` (default): `application-local.yml` + `application-secrets.yml` — debug logging, Swagger enabled
- `dev` / `prod`: deployed environments; secrets injected via AWS SSM Parameter Store
- `secrets` is always included; it supplies sensitive values (DB credentials, keys, etc.) and is not tracked in the
  repository

## Convention & Skill Docs

Detailed working rules live under `.claude/`:

- `.claude/rules/*.md` — convention specs, auto-attached by path glob.
- `.claude/documents/<type>/*.md` — on-demand reference docs, grouped by directory. Before reading one, decide whether
  it is needed from its document name (`<type>/<file-name>`, e.g. `policy/soft-and-hard-data-deletion-policy.md`):
  - `architecture/` — explanations of specific concerns that span the backend architecture
  - `guide/` — guides addressed to AI for carrying out specific purposes
  - `policy/` — policies currently applied to this project
  - `tech-stack/` — how this project implements specific technologies
  - `term/` — glossaries of terms that several documents use
- `.claude/skills/*/SKILL.md` — task workflows.

## Claude Document Hierarchy

A Claude document hierarchy is a parent document and the child documents under it, among `CLAUDE.md` and the
documents under `.claude/` outside `.claude/temp/`. A child may depend on its parent; a parent does not depend on its
children, so it states every fact it needs itself, even one taken from a child, which then drops that fact. Each level
is the parent of each document of a lower level that falls under it:

1. `CLAUDE.md`, `CLAUDE.local.md` — every other document.
2. A term document under `.claude/documents/term/` — each document that uses its terms.
3. A policy document under `.claude/documents/policy/` or a tech-stack document under `.claude/documents/tech-stack/` —
   each document on the area its `Applies to` statement names, where the policy applies or the technology is
   implemented.
4. Every other document; among the rules, a baseline rule is the parent of the per-area rules under it, all
   auto-attached by path glob (e.g. `main-architecture-details.md` over `main-<kind>-<area>-details.md`).

A child whose content depends on a parent names that parent in a `Parent Document` section, one
`` - `<path>` — <what it depends on> `` item per parent, where `<path>` is the parent's path from the repository root
(e.g. `.claude/documents/policy/prohibited-word-filtering-policy.md`). The section is the document's first section,
right after its title and any introduction, unnumbered and at the level of its other sections. A parent that always
loads with the child is not named: `CLAUDE.md`, and a rule whose path glob covers the child's own path glob. A parent
names none of its children, and a document with no parent left to name has no such section.

## Precaution

- **Accumulated:** During a turn, `allowed-tools` / `disallowed-tools` will be accumulated as the skill invokes other
  skills, which may cause unexpected permission errors.
