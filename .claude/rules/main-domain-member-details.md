---
paths:
  - "src/main/java/kr/modusplant/domains/member/**"
---

# Member Domain Conventions

## Parent Document

- `.claude/documents/policy/swear-filtering-policy.md` — the introduction masking and nickname checks of profile
  override and nickname check, and their order (§3.2)
- `.claude/documents/policy/soft-and-hard-data-deletion-policy.md` — the member withdrawal and post abuse-report
  approval steps (§2.3)
- `.claude/documents/tech-stack/open-authentication-implementation.md` — the social login provider calls of
  `MemberSocialTranslator`

---

## 1. Package Structure

- `Member`, `MemberProfile`, `ProposalOrBugReport` — aggregate roots: a member, its profile, and a proposal or bug
  report
- `MemberController`, `MemberAdminController` — adapter controllers for member use cases and for the admin report
  dashboards
- `MemberValidationHelper` — precondition checks
- `MemberImageIOHelper` — storage I/O of profile and report images (upload URL issuing, deletion, existence checks)
- `MemberEventListener` — transactional event listener removing chosen-comment caches and reflecting abuse reports into
  the report dashboards
- `MemberSocialTranslator` — calls the social login providers (access token, account deletability)
- `MemberCacheValidationService` — conditional-request evaluation, returning a `*CacheValidationResult` record
- `MemberRepositoryAdapter`, `ReportRepositoryAdapter`, `ReportDashboardRepositoryAdapter` (`framework/outbound/`) —
  repository port implementations combining JPA and jOOQ
- `ActivitySubjectPostCacheRedisRepository` — Redis cache of the chosen comment path per post
- `MemberProfileResponse` — marker interface for multi-version polymorphic profile responses
- `MemberUuidPrimaryKeyJpaRepository` — base interface of the JPA repositories keyed by a member UUID

---

## 2. domain/ Patterns

**Aggregate**:
- May expose intent-named instance methods that reassign a field to a new valid state for in-place transitions outside
  the `create` factory (e.g. `MemberProfile.clearImage()` resetting the profile image field to its Empty entity)

**Entity**:
- Optional entities: Null Object pattern → singleton class under `entity/nullobject/`
- May expose an additional overloaded `create(...)` factory that derives an omitted field internally from an
  already-validated field (e.g. parsing a filename VO out of a path VO's value)

**Value Object**:
- Path-like VOs may expose an additional overloaded `create(...)` factory composing other VOs (e.g. an ID VO + a
  filename VO), alongside the raw-string `create(String)` factory

**Domain Events**:
- Delegate side effects (image deletion, count changes, notifications) outside the domain

**Enums**:
- Report-status enums carry a Korean display label (`value: String`)

---

## 3. usecase/ Patterns

**Records**:
- When an operation has multiple coexisting API versions, the record is suffixed accordingly (e.g. `*FooRecord_V1`,
  `*FooRecord_V2`) instead of one record serving every version
- Distinct from `framework/outbound/jpa/entity/record/` (JPA JSON)

**Response DTOs**:
- When a mapper method returns a different concrete response record depending on a parameter, the candidate records
  implement a shared empty marker interface placed under `response/supers/`; the mapper declares that interface as its
  return type, and each call site casts the result back to the concrete type it expects

**Read Models**:
- Serve views such as abuse-report/proposal dashboards

---

## 4. adapter/ Patterns

**Controller**:
- Member-facing logic: `*Controller`; admin-only logic: `*AdminController`

**Helper**:
- `*ValidationHelper`: DB-backed pre-condition checks via repository ports
- `*IOHelper`: external I/O operations (S3 upload/delete, etc.); a presigned-URL issuance method also registers the
  returned file key as pending via the global `PendingFileService`
- `*IOHelper` may also expose an S3-backed validation (e.g. `validateIfImageExistsInStorage`), complementing the
  DB-backed checks in `*ValidationHelper`

**Listener**:
- `@EventListener` / `@TransactionalEventListener`; apply Semaphore bulkhead when concurrency control is needed

**Mapper**: domain Aggregate or VO → Response DTO (may call S3 URL generation)

**Translator**:
- External systems include social-login providers

---

## 5. framework/ Patterns

**REST Controller**:
- Naming mirrors the adapter Controller it delegates to, with a `*RestController` suffix (e.g. `MemberRestController`,
  `MemberAdminRestController`)
- Admin endpoints: `@PreAuthorize("hasAuthority('ADMIN')")`
- Multiple API versions of one operation coexist as separate methods on the same controller: the class-level
  `@RequestMapping` carries no version, each `@GetMapping`/`@PostMapping`/etc. embeds its own `/v{n}/` path segment, and
  the method name takes a matching `_v{n}` suffix (e.g. `fooBar_v1`, `fooBar_v2`)
- `consumes = MediaType.MULTIPART_FORM_DATA_VALUE` is declared only when the method binds an actual
  `MultipartFile`/`List<MultipartFile>` via `@RequestPart`; endpoints taking only `String`/`List<String>` params use
  plain `@RequestParam` with no `consumes` override

**HTTP Cache**:
- `record/`: `*CacheValidationResult` carries the outcome of a validation check
- `service/`: `*CacheValidationService` evaluates request headers against current resource state to produce a result

**JPA Entity**:
- JSON column: `@JdbcTypeCode(SqlTypes.JSON)` + dedicated record in `entity/record/`

**JPA Repository**:
- `supers/` holds base repository interfaces shared across multiple JPA repositories in this domain

**JPA Mapper** — JPA Entity ↔ Domain Aggregate; may include S3 download
- `supers/` holds base mapper interfaces shared across multiple JPA mappers in this domain

**JPA Repository Adapter** — `*RepositoryJpaAdapter` classes live in `framework/outbound/jpa/adapter/`

**Composite Key** — used by join/report tables (e.g. like, abuse report)

**jOOQ Repository** — serves bulk cascade deletes that JPA's cascade cannot express, complex joins, paginated read
models

**Redis Repository** — falls back to a JPA lookup and populates the cache on a miss
