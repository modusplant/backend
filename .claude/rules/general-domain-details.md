---
paths:
  - "src/main/java/kr/modusplant/domains/member/**"
  - "src/main/java/kr/modusplant/domains/search/**"
---

> Supplements CLAUDE.md § Architecture — Domain Internal Structure. Assumes familiarity with the four-layer layout and JPA/jOOQ design decision documented there.

# Domain Conventions

Applies exclusively to `kr.modusplant.domains.member`, `kr.modusplant.domains.search` and its sub-packages.

---

## 1. Package Structure

```
[member]
 ├─ domain/
 │   ├─ aggregate/          # Aggregates
 │   ├─ entity/             # Entities; nullobject/ for Null Object singletons
 │   ├─ vo/                 # Value Objects; Empty* for optional VOs
 │   ├─ event/              # Domain events
 │   ├─ enums/              # Domain status enums
 │   └─ exception/enums/    # Error code enums
 ├─ usecase/
 │   ├─ port/repository/    # Repository port interfaces
 │   ├─ port/mapper/        # Domain → DTO mapper interfaces
 │   ├─ record/             # Data-transfer records (REST Controller → adapter)
 │   ├─ request/            # External request DTOs (@Valid + Swagger)
 │   ├─ response/           # Response DTOs (Java records)
 │   └─ model/read/         # Read models for jOOQ query mapping
 ├─ adapter/
 │   ├─ controller/         # Business orchestration (*Controller / *AdminController)
 │   ├─ helper/             # Pre-condition checks (*ValidationHelper) and I/O (*IOHelper)
 │   ├─ listener/           # Domain event listeners
 │   ├─ mapper/             # Mapper port implementations
 │   └─ translator/         # External system abstractions
 └─ framework/
     ├─ inbound/web/rest/           # @RestController (HTTP entry point)
     ├─ inbound/web/cache/          # ETag/If-Modified-Since cache validation
     ├─ outbound/                   # Repository port implementations
     ├─ outbound/jpa/entity/        # @Entity classes
     ├─ outbound/jpa/entity/record/ # JPA JSON-column serialization records
     ├─ outbound/jpa/repository/    # Spring Data JPA interfaces
     ├─ outbound/jpa/mapper/        # JPA Entity ↔ Domain mapping
     ├─ outbound/jooq/repository/   # DSLContext-based complex queries
     └─ outbound/jooq/record/       # jOOQ composite parameter records
```

---

## 2. domain/ Patterns

**Aggregate** (`domain/aggregate/`):
- `@AllArgsConstructor(AccessLevel.PRIVATE or PROTECTED)` + `static create(...)` factory with internal null checks
- `equals/hashCode` based on the ID VO

**Entity** (`domain/entity/`):
- `protected` constructor + `static create()` factory
- Optional entities: Null Object pattern → singleton class under `entity/nullobject/`

**Value Object** (`domain/vo/`):
- `@RequiredArgsConstructor(AccessLevel.PRIVATE)` + `static create()` factory (validates null, regex, length, range)
- Optional VOs (blank/null input allowed): `create()` returns a corresponding `Empty*VO`
  - `Empty*VO`: singleton, `getValue()` returns `null`, uses `@NoArgsConstructor(AccessLevel.PROTECTED, force = true)`
- ID VO generation — UUID type: `generate()` · `fromUuid(UUID)` · `fromString(String)`; ULID type: `generate()` · `create(String)`

**Domain Events** (`domain/event/`):
- `static create()` factory; delegate side effects (image deletion, count changes, notifications) outside the domain

**Error Codes** (`domain/exception/enums/`):
- `*ErrorCode implements ErrorCode` enum with three fields: `httpStatus(int)`, `code(String)`, `message(String)`

**Enums** (`domain/enums/`):
- Domain status values as enums; carry a Korean display label (`value: String`)

---

## 3. usecase/ Patterns

**Repository Ports** (`usecase/port/repository/`):
- Parameters and return types use only domain VOs/Aggregates — no JPA entities or jOOQ records

**Records** (`usecase/record/`):
- Java records for REST Controller → adapter Controller data transfer; carry raw types (UUID, String, Integer, MultipartFile)
- Adapter converts raw types to domain VOs; naming: `*GetRecord`, `*OverrideRecord`, `*LikeRecord`, etc.
- Distinct from `framework/outbound/jpa/entity/record/` (JPA JSON) and `framework/outbound/jooq/record/` (jOOQ params)

**Response DTOs** (`usecase/response/`):
- Java records; multi-item responses include `of(List, cursor, hasNext)` factory
- Field types: Java primitives, String, UUID, LocalDateTime, JsonNode

**Read Models** (`usecase/model/read/`):
- Java records optimized for specific views; direct targets of jOOQ query mapping

**Request DTOs** (`usecase/request/`):
- Carry `@Valid` and Swagger Schema annotations; deserialized directly from external input

---

## 4. adapter/ Patterns

**Controller** (`adapter/controller/`) — `@Service @Transactional @Slf4j @RequiredArgsConstructor`:
- Member-facing logic: `*Controller`; admin-only logic: `*AdminController`
- Receives usecase records, converts to domain VOs, calls ports, returns results

**Helper** (`adapter/helper/`) — `@Component`:
- Used only for controller
- `*ValidationHelper`: DB-backed pre-condition checks via repository ports
- `*IOHelper`: external I/O operations (S3 upload/delete, etc.)

**Listener** (`adapter/listener/`) — `@Component`:
- `@EventListener` / `@TransactionalEventListener`; apply Semaphore bulkhead when concurrency control is needed

**Mapper** (`adapter/mapper/`) — `@Component`; implements mapper port: domain Aggregate → Response DTO (may call S3 URL generation)

**Translator** (`adapter/translator/`) — `@Component`;
- Used only for controller
- abstracts calls to external systems

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`) — `@RestController @RequestMapping @RequiredArgsConstructor @Validated @Slf4j`:
- HTTP concerns only: request parsing, response serialization, cache headers, validation
- Extracts auth via `@AuthenticationPrincipal`; wraps into usecase record and delegates to adapter Controller
- Admin endpoints: `@PreAuthorize("hasAuthority('ADMIN')")`
- Swagger: `@Tag`, `@Operation`, `@Parameter`, `@Schema`

**HTTP Cache** (`framework/inbound/web/cache/`):
- ETag + If-Modified-Since conditional request handling; returns `*CacheValidationResult` record

**JPA Entity** (`framework/outbound/jpa/entity/`) — `@Entity @Table @EntityListeners(AuditingEntityListener.class) @NoArgsConstructor @Getter`:
- Composite PK: `@IdClass` + separate CompositeKey class
- JSON column: `@JdbcTypeCode(SqlTypes.JSON)` + dedicated record in `entity/record/`
- Auditing: `createdAt`, `lastModifiedAt`; optimistic locking via `versionNumber`

**JPA Repository** (`framework/outbound/jpa/repository/`) — `@Repository`; extends shared project base interface

**JPA Mapper** (`framework/outbound/jpa/mapper/`) — `@Component`; JPA Entity ↔ Domain Aggregate; may include S3 download

**Repository Adapter** (`framework/outbound/`) — `@Repository`; implements usecase port by combining JPA + jOOQ:
- Use jOOQ for cascade deletes that JPA's cascade cannot express; otherwise use JPA

**jOOQ Repository** (`framework/outbound/jooq/repository/`) — `@Repository`; DSLContext directly for bulk cascades, complex joins, paginated read models; composite parameters via `jooq/record/`