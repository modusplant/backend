---
paths:
  - "src/main/java/kr/modusplant/domains/member/**"
---

# Member Domain Conventions

Applies to `kr.modusplant.domains.member` and its sub-packages.

---

## 1. Package Structure

```
member/
 ├─ domain/
 │   ├─ aggregate/
 │   ├─ entity/                    # nullobject/ for Null Object singletons
 │   ├─ vo/                        # nullobject/ for optional VOs (Empty*)
 │   ├─ event/
 │   ├─ enums/                     # Domain status enums
 │   └─ exception/enums/
 ├─ usecase/
 │   ├─ port/repository/
 │   ├─ port/mapper/
 │   ├─ record/
 │   ├─ request/
 │   ├─ response/
 │   │   └─ supers/                # Marker interfaces for multi-version polymorphic responses
 │   └─ model/read/
 ├─ adapter/
 │   ├─ controller/                # *Controller / *AdminController
 │   ├─ helper/                    # Pre-condition checks (*ValidationHelper) and I/O (*IOHelper)
 │   ├─ listener/                  # Domain event listeners
 │   ├─ mapper/
 │   └─ translator/
 └─ framework/
     ├─ inbound/web/rest/
     ├─ inbound/web/cache/
     │   ├─ record/                # *CacheValidationResult — ETag/If-Modified-Since result carriers
     │   └─ service/               # *CacheValidationService — conditional-request evaluation logic
     ├─ outbound/                  # Repository port implementations (combine JPA + jOOQ)
     ├─ outbound/jpa/entity/
     │   └─ record/                # JPA JSON-column serialization records
     ├─ outbound/jpa/repository/
     │   └─ supers/                # Shared base repository interfaces
     ├─ outbound/jpa/mapper/
     │   └─ supers/                # Shared base mapper interfaces
     ├─ outbound/jpa/adapter/      # *RepositoryJpaAdapter — mediates between the outbound port
     │                             #   implementation and the raw Spring Data JPA repository
     ├─ outbound/jpa/compositekey/ # Composite PK classes for join/report tables
     └─ outbound/jooq/repository/
```

---

## 2. domain/ Patterns

**Aggregate** (`domain/aggregate/`):
- May expose intent-named instance methods that reassign a field to a new valid state for in-place transitions outside the `create` factory (e.g. `MemberProfile.clearImage()` resetting the profile image field to its Empty entity)

**Entity** (`domain/entity/`):
- Optional entities: Null Object pattern → singleton class under `entity/nullobject/`
- May expose an additional overloaded `create(...)` factory that derives an omitted field internally from an already-validated field (e.g. parsing a filename VO out of a path VO's value)

**Value Object** (`domain/vo/`):
- Path-like VOs may expose an additional overloaded `create(...)` factory composing other VOs (e.g. an ID VO + a filename VO), alongside the raw-string `create(String)` factory

**Domain Events** (`domain/event/`):
- Delegate side effects (image deletion, count changes, notifications) outside the domain

**Enums** (`domain/enums/`):
- Report-status enums carry a Korean display label (`value: String`)

---

## 3. usecase/ Patterns

**Records** (`usecase/record/`):
- When an operation has multiple coexisting API versions, the record is suffixed accordingly (e.g. `*FooRecord_V1`, `*FooRecord_V2`) instead of one record serving every version
- Distinct from `framework/outbound/jpa/entity/record/` (JPA JSON)

**Response DTOs** (`usecase/response/`):
- When a mapper method returns a different concrete response record depending on a parameter, the candidate records implement a shared empty marker interface placed under `response/supers/`; the mapper declares that interface as its return type, and each call site casts the result back to the concrete type it expects

**Read Models** (`usecase/model/read/`):
- Serve views such as abuse-report/proposal dashboards

---

## 4. adapter/ Patterns

**Controller** (`adapter/controller/`):
- Member-facing logic: `*Controller`; admin-only logic: `*AdminController`

**Helper** (`adapter/helper/`):
- `*ValidationHelper`: DB-backed pre-condition checks via repository ports
- `*IOHelper`: external I/O operations (S3 upload/delete, etc.); a presigned-URL issuance method also registers the returned file key as pending via the global `PendingFileService`
- `*IOHelper` may also expose an S3-backed validation (e.g. `validateIfImageExistsInStorage`), complementing the DB-backed checks in `*ValidationHelper`

**Listener** (`adapter/listener/`):
- `@EventListener` / `@TransactionalEventListener`; apply Semaphore bulkhead when concurrency control is needed

**Mapper** (`adapter/mapper/`): domain Aggregate or VO → Response DTO (may call S3 URL generation)

**Translator** (`adapter/translator/`):
- External systems include social-login providers

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`):
- Naming mirrors the adapter Controller it delegates to, with a `*RestController` suffix (e.g. `MemberRestController`, `MemberAdminRestController`)
- Admin endpoints: `@PreAuthorize("hasAuthority('ADMIN')")`
- Multiple API versions of one operation coexist as separate methods on the same controller: the class-level `@RequestMapping` carries no version, each `@GetMapping`/`@PostMapping`/etc. embeds its own `/v{n}/` path segment, and the method name takes a matching `_v{n}` suffix (e.g. `fooBar_v1`, `fooBar_v2`)
- `consumes = MediaType.MULTIPART_FORM_DATA_VALUE` is declared only when the method binds an actual `MultipartFile`/`List<MultipartFile>` via `@RequestPart`; endpoints taking only `String`/`List<String>` params use plain `@RequestParam` with no `consumes` override

**HTTP Cache** (`framework/inbound/web/cache/`):
- `record/`: `*CacheValidationResult` carries the outcome of a validation check
- `service/`: `*CacheValidationService` evaluates request headers against current resource state to produce a result

**JPA Entity** (`framework/outbound/jpa/entity/`):
- JSON column: `@JdbcTypeCode(SqlTypes.JSON)` + dedicated record in `entity/record/`

**JPA Repository** (`framework/outbound/jpa/repository/`):
- `supers/` holds base repository interfaces shared across multiple JPA repositories in this domain

**JPA Mapper** (`framework/outbound/jpa/mapper/`) — JPA Entity ↔ Domain Aggregate; may include S3 download
- `supers/` holds base mapper interfaces shared across multiple JPA mappers in this domain

**JPA Adapter** (`framework/outbound/jpa/adapter/`) — `*RepositoryJpaAdapter` classes wrap a Spring Data JPA repository to present a narrower, domain-shaped interface to the outbound repository implementation
- Exception: `MemberProfileRepositoryJpaAdapter` implements `MemberProfileRepository` directly (no separate `framework/outbound/`-level adapter)

**Composite Key** (`framework/outbound/jpa/compositekey/`) — used by join/report tables (e.g. like, abuse report)

**Repository Adapter** (`framework/outbound/`) — implements usecase port by combining JPA + jOOQ:
- Use jOOQ for cascade deletes that JPA's cascade cannot express; otherwise use JPA

**jOOQ Repository** (`framework/outbound/jooq/repository/`) — serves bulk cascades, complex joins, paginated read models
