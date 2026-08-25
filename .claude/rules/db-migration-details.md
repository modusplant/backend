---
paths:
  - "src/main/resources/db/migration/**"
  - "src/main/java/db/migration/**"
  - "build.gradle"
  - "src/main/resources/application*.yml"
---

# DB Migration Conventions

Applies to `src/main/resources/db/migration/`, `src/main/java/db/migration/`, and the Flyway/jOOQ configuration in `build.gradle` and `application*.yml`.

---

## 1. Directory Structure & Version Sequencing

```
src/main/resources/db/migration/
 ├─ schema/                 # DDL migrations
 └─ data/seed/              # Small, static seed DML

src/main/java/db/migration/
 └─ data/reference/         # Programmatic/bulk data migrations
```

`schema/`, `data/seed/`, and `data/reference/` (Java) all draw from **one unified version sequence** — Flyway orders every migration across all three locations as a single timeline, not per-folder sequences.

---

## 2. Migration File Naming Convention

Pattern: `V<major>.<minor>.<patch>__Description_With_Underscores.sql`

- Version is a 3-part dotted, semver-style number — never timestamp-based.
- Double underscore (`__`) separates the version from the description.
- The description is `Capitalized_Snake_Case`, starting with a verb: `Create`, `Alter`, `Add`, `Remove`, `Insert`, `Delete`, `Rename`, `Migrate`.
- Examples: `V0.2.0__Migrate_category_uuid_to_serial.sql`, `V0.12.0__Create_site_member_withdraw_table.sql`, `V1.3.1__Insert_common_code.sql`.

**Baseline exception**: `B0.0.0__Create_initial_table.sql` uses Flyway's `B` (baseline) prefix instead of `V`.

---

## 3. Migration Purpose Split

**Schema** (`src/main/resources/db/migration/schema/`):
- DDL only — table/column/constraint/index/function changes.

**Seed Data** (`src/main/resources/db/migration/data/seed/`):
- Small, static, handwritten `INSERT` statements.

**Java Migrations** (`src/main/java/db/migration/`):
- Reserved for programmatic or bulk data work that plain SQL can't express cleanly — e.g. parsing an external file and batch-inserting derived rows.

---

## 4. Java Migration Conventions

- Each class extends `org.flywaydb.core.api.migration.BaseJavaMigration` and overrides `migrate(Context context)`.
- The filename uses underscores in place of dots (`V5_0_1__Insert_plant_table_default_data.java`).

---

## 5. Build & Runtime Execution Surfaces

Flyway runs in two places with deliberately different scopes:

**Gradle plugin** (`build.gradle`, `flyway {}` block) — prepares a live schema for jOOQ codegen at build time:
- `locations = ['classpath:db/migration/data/seed', 'classpath:db/migration/schema']` — **SQL-only**, excludes the Java migration classes

**Spring Boot runtime** (`application.yml`, `spring.flyway`) — the actual source of truth, applied at application startup in every environment:
- `locations: [classpath:db/migration]` — the **whole tree**, SQL and Java together; this is what actually executes Java migrations

Gradle task chain: `flywayMigrate` depends on `processResources`; `jooqCodegen` depends on `flywayMigrate`; `compileJava` and `compileTestJava` depend on `jooqCodegen`.

---

## 6. Environment & Credentials Configuration

Gradle/Flyway/jOOQ build-time credentials are supplied via `JDBC_CONNECTION_URL`, `JDBC_USERNAME`, `JDBC_PASSWORD` (environment variables) or `jdbcConnectionUrl`, `jdbcUsername`, `jdbcPassword` (Gradle properties).

These are distinct from Spring's runtime datasource variables — `DB_CONNECTION_URL`, `DB_USERNAME`, `DB_PASSWORD` (`application.yml`'s `spring.datasource` block).