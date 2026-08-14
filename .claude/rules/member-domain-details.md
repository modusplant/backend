---
paths:
  - "src/main/java/kr/modusplant/domains/member/**"
---

> Supplements CLAUDE.md § Architecture — Domain Internal Structure. Assumes familiarity with the four-layer layout and JPA/jOOQ design decision documented there.

# Member Domain Conventions

Applies to `kr.modusplant.domains.member` and its sub-packages.

---

## 1. Package Structure

```
member/
 ├─ domain/
 │   ├─ aggregate/                 # Aggregates
 │   ├─ entity/                    # Entities; nullobject/ for Null Object singletons
 │   ├─ vo/                        # Value Objects; nullobject/ for optional VOs (Empty*)
 │   ├─ event/                     # Domain events
 │   ├─ enums/                     # Domain status enums
 │   └─ exception/enums/           # Error code enums
 ├─ usecase/
 │   ├─ port/repository/           # Repository port interfaces
 │   ├─ port/mapper/               # Domain → DTO mapper interfaces
 │   ├─ record/                    # Data-transfer records (REST Controller → adapter)
 │   ├─ request/                   # External request DTOs (@Valid + Swagger)
 │   ├─ response/                  # Response DTOs (Java records)
 │   │   └─ supers/                # Marker interfaces for multi-version polymorphic responses
 │   └─ model/read/                # Read models for jOOQ query mapping
 ├─ adapter/
 │   ├─ controller/                # Business orchestration (*Controller / *AdminController)
 │   ├─ helper/                    # Pre-condition checks (*ValidationHelper) and I/O (*IOHelper)
 │   ├─ listener/                  # Domain event listeners
 │   ├─ mapper/                    # Mapper port implementations
 │   └─ translator/                # External system abstractions
 └─ framework/
     ├─ inbound/web/rest/          # @RestController (HTTP entry point)
     ├─ inbound/web/cache/
     │   ├─ record/                # *CacheValidationResult — ETag/If-Modified-Since result carriers
     │   └─ service/               # *CacheValidationService — conditional-request evaluation logic
     ├─ outbound/                  # Repository port implementations (combine JPA + jOOQ)
     ├─ outbound/jpa/entity/       # @Entity classes
     │   └─ record/                # JPA JSON-column serialization records
     ├─ outbound/jpa/repository/   # Spring Data JPA interfaces
     │   └─ supers/                # Shared base repository interfaces
     ├─ outbound/jpa/mapper/       # JPA Entity ↔ Domain mapping implementations
     │   └─ supers/                # Shared base mapper interfaces
     ├─ outbound/jpa/adapter/      # *RepositoryJpaAdapter — mediates between the outbound port
     │                             #   implementation and the raw Spring Data JPA repository
     ├─ outbound/jpa/compositekey/ # Composite PK classes for @IdClass entities (join/report tables)
     ├─ outbound/jooq/repository/  # DSLContext-based complex queries
     └─ outbound/jooq/record/      # jOOQ composite parameter records
```

---

## 2. domain/ Patterns

**Aggregate** (`domain/aggregate/`):
- `@AllArgsConstructor(AccessLevel.PRIVATE or PROTECTED)` + `static create(...)` factory with internal null checks
- `equals/hashCode` based on the ID VO
- May expose intent-named instance methods that reassign a field to a new valid state for in-place transitions outside the `create` factory (e.g. `MemberProfile.clearImage()` resetting the profile image field to its Empty entity)

**Entity** (`domain/entity/`):
- `protected` constructor + `static create()` factory
- Optional entities: Null Object pattern → singleton class under `entity/nullobject/`
- May expose an additional overloaded `create(...)` factory that derives an omitted field internally from an already-validated field (e.g. parsing a filename VO out of a path VO's value) — mirrors the VO overload pattern above, avoiding duplicated parsing logic across callers

**Value Object** (`domain/vo/`):
- `@RequiredArgsConstructor(AccessLevel.PRIVATE)` + `static create()` factory (validates null, regex, length, range)
- Optional VOs (blank/null input allowed): `create()` returns a corresponding `Empty*VO`
  - `Empty*VO`: singleton, `getValue()` returns `null`, uses `@NoArgsConstructor(AccessLevel.PROTECTED, force = true)`
- ID VO generation — UUID type: `generate()` · `fromUuid(UUID)` · `fromString(String)`; ULID type: `generate()` · `create(String)`
- Path-like VOs may expose an additional overloaded `create(...)` factory composing other VOs (e.g. an ID VO + a filename VO), alongside the raw-string `create(String)` factory

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
- Exception: a method may accept a trailing primitive `int version` parameter to select version-conditional behavior in its adapter implementation

**Records** (`usecase/record/`):
- Java records for REST Controller → adapter Controller data transfer; carry raw types (UUID, String, Integer, List<String>, MultipartFile)
- Adapter converts raw types to domain VOs; naming: `*GetRecord`, `*OverrideRecord`, `*LikeRecord`, etc.
- When an operation has multiple coexisting API versions, the record is suffixed accordingly (e.g. `*OverrideRecord_V1`, `*OverrideRecord_V2`) instead of one record serving every version
- Distinct from `framework/outbound/jpa/entity/record/` (JPA JSON) and `framework/outbound/jooq/record/` (jOOQ params)

**Response DTOs** (`usecase/response/`):
- Java records; multi-item responses commonly include a static `of(...)` factory (e.g. paginated lists: `of(List, cursor, hasNext)`; an id plus its related list: `of(id, List)`)
- Field types: Java primitives, String, UUID, LocalDateTime, JsonNode
- When a mapper method returns a different concrete response record depending on version, the candidate records implement a shared empty marker interface placed under `response/supers/`; the mapper declares that interface as its return type, and each call site casts the result back to the concrete type it expects

**Read Models** (`usecase/model/read/`):
- Java records optimized for specific views (e.g. abuse-report/proposal dashboards); direct targets of jOOQ query mapping

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
- `*IOHelper`: external I/O operations (S3 upload/delete, etc.); a presigned-URL issuance method also registers the returned file key as pending via the global `PendingFileService`, so an S3 upload never confirmed by a DB write can later be cleaned up as an orphan

**Listener** (`adapter/listener/`) — `@Component`:
- `@EventListener` / `@TransactionalEventListener`; apply Semaphore bulkhead when concurrency control is needed

**Mapper** (`adapter/mapper/`) — `@Component`; implements mapper port: domain Aggregate or VO → Response DTO (may call S3 URL generation)

**Translator** (`adapter/translator/`) — `@Component`;
- Used only for controller
- Abstracts calls to external systems (e.g. social-login providers)

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`) — `@RestController @RequestMapping @RequiredArgsConstructor @Validated @Slf4j`:
- HTTP concerns only: request parsing, response serialization, cache headers, validation
- Extracts auth via `@AuthenticationPrincipal`; wraps into usecase record and delegates to adapter Controller
- Naming mirrors the adapter Controller it delegates to, with a `*RestController` suffix (e.g. `MemberRestController`, `MemberAdminRestController`)
- Admin endpoints: `@PreAuthorize("hasAuthority('ADMIN')")`
- Swagger: `@Tag`, `@Operation`, `@Parameter`, `@Schema`
- Multiple API versions of one operation coexist as separate methods on the same controller: the class-level `@RequestMapping` carries no version, each `@GetMapping`/`@PostMapping`/etc. embeds its own `/v{n}/` path segment, and the method name takes a matching `_v{n}` suffix (e.g. `overrideMemberProfile_v1`, `overrideMemberProfile_v2`)
- `consumes = MediaType.MULTIPART_FORM_DATA_VALUE` is declared only when the method binds an actual `MultipartFile`/`List<MultipartFile>` via `@RequestPart`; endpoints taking only `String`/`List<String>` params use plain `@RequestParam` with no `consumes` override

**HTTP Cache** (`framework/inbound/web/cache/`):
- ETag + If-Modified-Since conditional request handling
- `record/`: `*CacheValidationResult` carries the outcome of a validation check
- `service/`: `*CacheValidationService` evaluates request headers against current resource state to produce a result

**JPA Entity** (`framework/outbound/jpa/entity/`) — `@Entity @Table @EntityListeners(AuditingEntityListener.class) @NoArgsConstructor @Getter`:
- Composite PK: `@IdClass` + a dedicated composite-key class under `outbound/jpa/compositekey/`
- JSON column: `@JdbcTypeCode(SqlTypes.JSON)` + dedicated record in `entity/record/`
- Auditing: `createdAt`, `lastModifiedAt`; optimistic locking via `versionNumber`

**JPA Repository** (`framework/outbound/jpa/repository/`) — `@Repository`; extends shared project base interface
- `supers/` holds base repository interfaces shared across multiple JPA repositories in this domain

**JPA Mapper** (`framework/outbound/jpa/mapper/`) — `@Component`; JPA Entity ↔ Domain Aggregate; may include S3 download
- `supers/` holds base mapper interfaces shared across multiple JPA mappers in this domain

**JPA Adapter** (`framework/outbound/jpa/adapter/`) — `@Component`; `*RepositoryJpaAdapter` classes wrap a Spring Data JPA repository to present a narrower, domain-shaped interface to the outbound repository implementation
- Exception: `MemberProfileRepositoryJpaAdapter` implements `MemberProfileRepository` directly (no separate `framework/outbound/`-level adapter)

**Composite Key** (`framework/outbound/jpa/compositekey/`) — plain classes implementing `Serializable`, paired 1:1 with an `@IdClass` entity (e.g. like/abuse-report join tables)

**Repository Adapter** (`framework/outbound/`) — `@Repository`; implements usecase port by combining JPA + jOOQ:
- Use jOOQ for cascade deletes that JPA's cascade cannot express; otherwise use JPA

**jOOQ Repository** (`framework/outbound/jooq/repository/`) — `@Repository`; DSLContext directly for bulk cascades, complex joins, paginated read models; composite parameters via `jooq/record/`
