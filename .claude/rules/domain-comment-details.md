---
paths:
  - "src/main/java/kr/modusplant/domains/comment/**"
---

# Comment Domain Conventions

Applies to `kr.modusplant.domains.comment` and its sub-packages.

---

## 1. Package Structure

```
comment/
 ├─ domain/
 │   ├─ aggregate/                        # Aggregate root
 │   ├─ enums/                            # Status-style backing enums
 │   ├─ event/                            # Domain events
 │   ├─ exception/
 │   │   └─ enums/                        # Error code enums (no domain-local exception classes)
 │   └─ vo/                               # Value Objects
 ├─ usecase/
 │   ├─ model/                            # Read models for jOOQ query mapping
 │   ├─ port/
 │   │   ├─ mapper/                       # Domain → DTO mapper interface
 │   │   └─ repository/                   # Query/Command/Cache repository port interfaces
 │   ├─ request/                          # Inbound REST DTOs (Bean Validation)
 │   └─ response/                         # Outbound REST DTOs
 ├─ adapter/
 │   ├─ controller/                       # Business orchestration
 │   ├─ helper/                           # Cross-aggregate write-precondition checks
 │   └─ mapper/                           # Mapper port implementation
 └─ framework/
     ├─ inbound/web/rest/                 # HTTP entry point
     ├─ inbound/web/cache/                # ETag / Last-Modified conditional-GET support
     │   └─ model/                        # Cache result carrier record
     └─ outbound/
         ├─ jooq/
         │   └─ repository/               # DSLContext-based read repository
         ├─ redis/                        # Redis-backed idempotency + sibling-order allocation
         └─ jpa/
             ├─ entity/                    # @Entity classes
             ├─ compositekey/              # Composite PK class for @IdClass entities
             ├─ mapper/                    # Domain ↔ JPA Entity mapping (MapStruct)
             └─ repository/                # Spring Data JPA interface + write-port adapter
```

---

## 2. domain/ Patterns

**Aggregate** (`domain/aggregate/`):
- `@AllArgsConstructor(AccessLevel.PRIVATE)` + `static create(...)` factory validating that every constituent VO is non-null
- May expose an overloaded `create(...)` factory that accepts an explicit optional field alongside the primary factory that derives it internally with a default
- `equals`/`hashCode` based on the composite identity fields (the referenced post and the comment's own positional identifier), via Apache Commons `EqualsBuilder`/`HashCodeBuilder`

**Value Object** (`domain/vo/`):
- `@AllArgsConstructor(AccessLevel.PRIVATE)` + `static create()` factory (validates null, blank, regex, or length)
- May expose an additional `createNullable(...)` factory that skips validation for contexts where the value is genuinely optional (e.g. an unauthenticated viewer)
- ID-style VOs validate against a fixed-format identifier pattern (length + regex) rather than parsing structured components
- A path-style VO may validate a delimited, hierarchical string format (digit segments separated by a fixed delimiter) with additional structural constraints (no leading-zero segments, 1-based indexing, a capped maximum nesting depth)
- Status VOs wrap a backing enum and expose named convenience factories (e.g. `setAsValid()`, `setAsDeleted()`) alongside the raw `create(String)` factory

**Domain Events** (`domain/event/`):
- `@RequiredArgsConstructor(AccessLevel.PRIVATE)` + `static create(...)` factory with internal validation of required fields
- May derive a classification field (e.g. an action-type string) internally from the shape of another field rather than accepting it as a parameter

**Domain Exceptions** (`domain/exception/`):
- This domain defines no domain-local exception subclasses; validation failures throw the shared kernel exceptions directly, parameterized with a `domain/exception/enums` error code

**Error Codes** (`domain/exception/enums/`):
- `*ErrorCode implements ErrorCode` enum with three fields: `httpStatus(int)`, `code(String)`, `message(String)`

**Enums** (`domain/enums/`):
- Backing enums for status-style VOs; expose a static helper to check whether a raw string is a recognized value

---

## 3. usecase/ Patterns

**Read Models** (`usecase/model/`):
- Java records optimized for specific views, direct targets of jOOQ query mapping; placed directly under `model/`; named with a `*ReadModel` suffix

**Repository Ports** (`usecase/port/repository/`):
- Split by responsibility into a query-side port (`*QueryRepository`), a command-side port (`*CommandRepository`) (CQRS-style), and a cache-side port (`*CacheRepository`) implemented over a key-value store, each implemented by a different framework technology
- Parameters and return types use domain VOs/Aggregates on every port;
- The query port may also declare a cross-domain read-only precondition check (e.g. whether a referenced parent resource is in a publishable state);
- The query port may expose a lookup for the highest existing child ordinal beneath a given parent path, used to set an external counter

**Cache Port** (`usecase/port/repository/`):
- Declares a single reservation operation that returns an `Optional` which is empty when the same request was already processed (idempotency)
- Accepts a lazily-evaluated fallback supplier so the implementation reads the database only when its own state is cold

**Mapper Port** (`usecase/port/mapper/`):
- Declares domain construction (raw VOs → Aggregate) and read-model-to-response mapping; implemented by a handwritten (non-generated) adapter class

**Request DTOs** (`usecase/request/`):
- Java records carrying Bean Validation annotations (e.g. `@NotBlank`) and Swagger `@Schema` documentation; deserialized directly from external input

**Response DTOs** (`usecase/response/`):
- Java records; a generic paginated wrapper carries the page/size/total metadata plus a method to convert a zero-based page index to a one-based index for API consumers
- Field types: Java primitives, String, boolean, LocalDateTime

---

## 4. adapter/ Patterns

**Controller** (`adapter/controller/`) — `@Service @Transactional @Slf4j @RequiredArgsConstructor`:
- Receives usecase request DTOs, converts to domain VOs, calls repository ports, returns response DTOs
- Delegates cross-aggregate precondition checks to a dedicated validation helper; operations that modify an existing record additionally require the caller to be its author
- For hierarchically-addressed records (a delimited path identifying position in a tree), verifies the parent position exists before inserting a nested record; the record's final positional index is assigned server-side rather than taken from the request
- Reserves the server-authoritative path (idempotency marker plus next-sibling ordinal) through the cache port before constructing the aggregate
- Publishes a domain event after a successful write via `ApplicationEventPublisher`

**Validation Helper** (`adapter/helper/`) — `@Component @RequiredArgsConstructor`:
- Exposes single-purpose precondition checks
- Exposes composite methods that bundle the set of checks a given operation needs (create-new vs. modify-existing)
- Throws the shared kernel exceptions parameterized with a `domain/exception/enums` error code; the ownership failure maps to HTTP 403

**Mapper** (`adapter/mapper/`) — `@Component @RequiredArgsConstructor`; implements the mapper port

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`) — `@RestController @RequestMapping @RequiredArgsConstructor @Validated @Slf4j`:
- HTTP concerns only: request parsing, response serialization, cache headers, validation
- Extracts auth via `@AuthenticationPrincipal` (may nullable for endpoints with optional authentication); wraps parameters into a usecase call and delegates to the adapter Controller
- Endpoints that mutate an existing record forward the authenticated principal for a downstream ownership check
- Implements conditional GET (`If-None-Match` / `If-Modified-Since`) by delegating cache-state computation to a dedicated cache service, then returning either a 304 (headers only) or a 200 (full body) response
- Swagger: `@Tag`, `@Operation`, `@Parameter`, `@Schema`, `@SecurityRequirement`

**HTTP Cache** (`framework/inbound/web/cache/`) — `@Service @Transactional @Slf4j`:
- Computes an ETag and last-modified timestamp for a resource, comparing against request headers to decide cacheability
- ETag comparison uses a one-way hash/encoder match rather than literal string equality
- May expose multiple overloads keyed by different addressing schemes (e.g. by parent resource vs. by author) for the same conditional-GET pattern
- `model/`: a record carrying the computed `(entityTag, lastModifiedAt, isCacheable)` outcome

**jOOQ Repository** (`framework/outbound/jooq/repository/`) — `@Repository @RequiredArgsConstructor`:
- DSLContext directly for joined reads, aggregate counts, and paginated read models
- Conditionally adjusts joins/computed fields based on whether an optional viewer-identity parameter is present
- May also implement a cross-domain read-only state check
- May expose a highest-child-ordinal lookup beneath a given parent path (root-level vs. nested distinguished by different pattern matches)

**Redis Repository** (`framework/outbound/redis/`) — `@Repository @RequiredArgsConstructor`; implements the cache port over a `StringRedisTemplate`:
- Key strings are built from unified format-string constants, one per purpose (an idempotency marker keyed by the write's identifying tuple; a per-parent ordinal counter using a literal sentinel for the root parent), all under a bounded, slidingly-refreshed TTL
- The reservation runs a set-if-absent idempotency guard (a lost race yields an empty `Optional`), then draws the next ordinal from an atomic increment counter that is set from the caller-supplied database lower bound before its first increment, so a cold counter cannot collide with existing rows
- Null replies and data-access errors are rethrown as a shared connection-failure exception

**JPA Entity** (`framework/outbound/jpa/entity/`) — `@Entity @Table @EntityListeners(AuditingEntityListener.class) @NoArgsConstructor(AccessLevel.PROTECTED) @Getter`:
- Composite PK via `@IdClass` referencing a dedicated composite-key class in `compositekey/`
- Foreign-key associations may be declared with `foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)` where no DB-level FK constraint is enforced
- Exposes intent-named mutation methods (e.g. increment/decrement a counter floored at zero, mark as deleted) rather than public setters
- `@PrePersist` callback defaults nullable numeric/boolean columns before insert
- Auditing: `@CreatedDate` / `@LastModifiedDate` timestamp columns
- Manual static nested builder class alongside the Lombok-generated accessors

**Composite Key** (`framework/outbound/jpa/compositekey/`) — plain class implementing `Serializable`, paired 1:1 with an `@IdClass` entity; `equals`/`hashCode` over all key fields; manual static nested builder including a copy-from-existing-instance factory

**JPA Mapper** (`framework/outbound/jpa/mapper/`) — MapStruct `@Mapper(componentModel = "spring")` interface: domain Aggregate (+ related entities resolved by the caller) → JPA Entity, `@BeanMapping(ignoreByDefault = true)` with explicit per-field `@Mapping`s; a `default` method may translate a domain status VO into a persisted boolean flag via a qualified mapping

**JPA Repository** (`framework/outbound/jpa/repository/`) — `@Repository`; extends Spring Data `JpaRepository` parameterized by the entity and its composite-key class; derived query methods keyed by the natural composite fields (not the JPA-internal key type)

**Repository Adapter** (`framework/outbound/jpa/repository/`) — `@Repository @RequiredArgsConstructor`; implements the usecase write port: resolves related entities by their business identifiers, maps the Aggregate to an Entity via the JPA mapper, checks for a pre-existing row before insert, and delegates soft-delete/update operations to the entity's mutation methods before saving