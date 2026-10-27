---
paths:
  - "src/main/java/**"
---

# Main Architecture Details

Applies to every class under @src/main/java/.

This rule is the baseline for every area. An area rule (`main-<kind>-<area>-details.md`) adds the
area's own variations on top of it.

---

## 1. Layer Patterns

Each pattern applies wherever the area has that layer.

### 1.1 domain/

**Aggregate** (`domain/aggregate/`):
- `@AllArgsConstructor(AccessLevel.PRIVATE or PROTECTED)` + `static create(...)` factory with internal null checks

**Entity** (`domain/entity/`):
- `protected` constructor + `static create()` factory

**Value Object** (`domain/vo/`):
- Private constructor + `static create(...)` factory that validates its input
- Optional VOs (blank/null input allowed): `create()` returns the VO's null-object subclass
  (`vo/nullobject/Empty*`), a singleton whose `getValue()` returns `null`

**Domain Events** (`domain/event/`):
- `static create(...)` factory

### 1.2 usecase/

**Repository Ports** (`usecase/port/repository/`):
- Parameters use domain VOs/Aggregates — no JPA entities or jOOQ records

**Records** (`usecase/record/`):
- Java records for REST Controller → adapter Controller data transfer, carrying raw types; the
  adapter converts them to domain VOs

**Request DTOs** (`usecase/request/`):
- Java records carrying Bean Validation annotations and Swagger `@Schema`; deserialized directly from external input

**Response DTOs** (`usecase/response/`):
- Java records

**Read Models** (`usecase/model/`):
- Java records optimized for specific views; direct targets of jOOQ query mapping

### 1.3 adapter/

**Controller** (`adapter/controller/`) — `@Service`:
- Receives usecase inputs, converts them to domain VOs, calls the repository ports, and returns the results

**Mapper** (`adapter/mapper/`) — `@Component`; implements the mapper port

**Translator** (`adapter/translator/`) — `@Component`; abstracts calls to external systems

### 1.4 framework/

**REST Controller** (`framework/inbound/web/rest/`) — `@RestController @RequiredArgsConstructor @Validated`:
- HTTP concerns only (request parsing, validation, response serialization); delegates to the adapter Controller
- Swagger: `@Tag`, `@Operation`, `@Parameter`, `@Schema`

**HTTP Cache** (`framework/inbound/web/cache/`):
- Evaluates `If-None-Match` / `If-Modified-Since` request headers against the resource's current
  ETag and last-modified timestamp; the outcome is carried in a dedicated record

**jOOQ Repository** (`framework/outbound/jooq/`) — `@Repository`; uses `DSLContext` directly

**JPA Entity** (`framework/outbound/jpa/entity/`) — `@Entity @Table @EntityListeners(AuditingEntityListener.class) @NoArgsConstructor @Getter`:
- Auditing timestamp columns via `@CreatedDate` / `@LastModifiedDate`

**Composite Key** (`framework/outbound/jpa/compositekey/`) — plain class implementing `Serializable`,
paired 1:1 with an `@IdClass` entity

**JPA Repository** (`framework/outbound/jpa/repository/`) — `@Repository`; Spring Data JPA interface

---

## 2. Error Codes

Every `*ErrorCode` enum implements `ErrorCode` with three fields: `httpStatus(int)`, `code(String)`, `message(String)`.

---

## 3. Sensitive-Data Masking

- A class holding a sensitive value (email, password, token, auth code) overrides `toString()` and
  masks every such value via `shared/util/MaskUtils`.
- A log statement printing such a value masks it via the same `MaskUtils` helpers.

---

## 4. Endpoint–`SecurityConfig` Sync

`infrastructure/security/config/SecurityConfig` matches endpoints by hard-coded URL patterns, so it
is updated in the same change as the endpoint:

- **New endpoint:** a permit-all endpoint is registered under its `HttpMethod` in
  `SecurityConfig.PUBLIC_ENDPOINTS` (`LOCAL_PUBLIC_ENDPOINTS` for a `local`-only one); every other
  endpoint carries a method-security annotation (`@PreAuthorize`/`@PostAuthorize`/`@Secured`).
- **Changed URL or HTTP method:** every pattern that matches the endpoint is updated.
- **Removed endpoint:** its public-endpoint entry is removed, unless another endpoint still matches the pattern.

`EndpointAuthorizationCoverageTest` catches only an endpoint with neither an annotation nor a
matching public-endpoint entry; no test enforces the other conditions.

---

## 5. Data-Deletion Consistency

A class that hard-deletes, soft-deletes, or archives persisted data, or deletes the S3 objects or
Redis keys tied to it, is checked against @.claude/documents/policy/data-deletion-policy.md, read
in full; any divergence from the document is a policy violation.