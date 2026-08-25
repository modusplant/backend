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
 │   ├─ event/                            # Domain events
 │   ├─ exception/                        # Domain-local business exceptions
 │   │   └─ enums/                        # Error code enums
 │   └─ vo/                               # Value Objects
 │       └─ enums/                        # VO-backing status enums
 ├─ usecase/
 │   ├─ model/                            # Read models for jOOQ query mapping
 │   ├─ port/
 │   │   ├─ client/                       # Cross-domain read port
 │   │   ├─ mapper/                       # Domain → DTO mapper interface
 │   │   └─ repository/                   # Read/write repository port interfaces
 │   ├─ request/                          # Inbound REST DTOs (Bean Validation)
 │   └─ response/                         # Outbound REST DTOs
 ├─ adapter/
 │   ├─ controller/                       # Business orchestration (*Controller)
 │   └─ mapper/                           # Mapper port implementation
 └─ framework/
     ├─ inbound/web/rest/                 # @RestController (HTTP entry point)
     ├─ inbound/web/cache/                # ETag / Last-Modified conditional-GET support
     │   └─ model/                        # Cache result carrier record
     └─ outbound/
         ├─ client/                       # Cross-domain read port implementation
         └─ persistence/
             ├─ jooq/                     # DSLContext-based read repository
             └─ jpa/
                 ├─ entity/                # @Entity classes
                 ├─ compositekey/          # Composite PK class for @IdClass entities
                 ├─ mapper/                # Domain ↔ JPA Entity mapping (MapStruct)
                 └─ repository/            # Spring Data JPA interface + write-port adapter
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
- A path-style VO may validate a delimited, hierarchical string format (digit segments separated by a fixed delimiter) with additional structural constraints (no leading-zero segments, 1-based indexing)
- Status VOs wrap a backing enum and expose named convenience factories (e.g. `setAsValid()`, `setAsDeleted()`) alongside the raw `create(String)` factory

**Domain Events** (`domain/event/`):
- `@RequiredArgsConstructor(AccessLevel.PRIVATE)` + `static create(...)` factory with internal validation of required fields
- May derive a classification field (e.g. an action-type string) internally from the shape of another field rather than accepting it as a parameter

**Domain Exceptions** (`domain/exception/`):
- Domain-local exception classes extend the shared business exception base, each with two constructors: `(ErrorCode)` and `(ErrorCode, String message)`
- Distinguish "required value missing" cases from "value present but invalid" cases as separate exception types

**Error Codes** (`domain/exception/enums/`):
- `*ErrorCode implements ErrorCode` enum with three fields: `httpStatus(int)`, `code(String)`, `message(String)`

**Enums** (`domain/vo/enums/`):
- Backing enums for status-style VOs; expose a static helper to check whether a raw string is a recognized value

---

## 3. usecase/ Patterns

**Read Models** (`usecase/model/`):
- Java records optimized for specific views, direct targets of jOOQ query mapping; placed directly under `model/` rather than a further version/purpose subdirectory

**Repository Ports** (`usecase/port/repository/`):
- Split by responsibility into a read-side port and a write-side port (CQRS-style), each implemented by a different framework technology
- Parameters and return types use domain VOs/Aggregates on the read and write ports; a write port may additionally accept a framework-level composite-key type where the aggregate's own identity fields are insufficient to address a single row for update/delete

**Client Port** (`usecase/port/client/`):
- Abstracts a read-only query against another domain's data (existence/state check) needed to enforce a precondition in this domain; implemented in `framework/outbound/client/`

**Mapper Port** (`usecase/port/mapper/`):
- Declares domain construction (raw VOs → Aggregate) and read-model-to-response mapping; implemented by a hand-written (non-generated) adapter class

**Request DTOs** (`usecase/request/`):
- Java records carrying Bean Validation annotations (e.g. `@NotBlank`) and Swagger `@Schema` documentation; deserialized directly from external input

**Response DTOs** (`usecase/response/`):
- Java records; a generic paginated wrapper carries the page/size/total metadata plus a method to convert a zero-based page index to a one-based index for API consumers
- Field types: Java primitives, String, boolean, LocalDateTime

---

## 4. adapter/ Patterns

**Controller** (`adapter/controller/`) — `@Service @Transactional @Slf4j @RequiredArgsConstructor`:
- Receives usecase request DTOs, converts to domain VOs, calls repository/client ports, returns response DTOs
- Enforces cross-aggregate preconditions before a write (e.g. referenced parent resource must be in a publishable state, an addressed record must not already exist) via injected repositories and ports
- For hierarchically-addressed records (a delimited path identifying position in a tree), validates structural placement consistency (that a claimed parent or preceding-sibling position already exists) before allowing an insert at a given path
- Publishes a domain event after a successful write via `ApplicationEventPublisher`

**Mapper** (`adapter/mapper/`) — `@Component @RequiredArgsConstructor`; implements the mapper port: constructs the Aggregate from VOs, and converts a read model to a response DTO (may resolve a stored file key to a full external URL)

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`) — `@RestController @RequestMapping @RequiredArgsConstructor @Validated @Slf4j`:
- HTTP concerns only: request parsing, response serialization, cache headers, validation
- Extracts auth via `@AuthenticationPrincipal` (may be nullable for endpoints with optional authentication); wraps parameters into a usecase call and delegates to the adapter Controller
- Implements conditional GET (`If-None-Match` / `If-Modified-Since`) by delegating cache-state computation to a dedicated cache service, then returning either a 304 (headers only) or a 200 (full body) response
- Swagger: `@Tag`, `@Operation`, `@Parameter`, `@Schema`, `@SecurityRequirement`

**HTTP Cache** (`framework/inbound/web/cache/`) — `@Service @Transactional @Slf4j`:
- Computes an ETag and last-modified timestamp for a resource, comparing against request headers to decide cacheability
- ETag comparison uses a one-way hash/encoder match rather than literal string equality
- May expose multiple overloads keyed by different addressing schemes (e.g. by parent resource vs. by author) for the same conditional-GET pattern
- `model/`: a record carrying the computed `(entityTag, lastModifiedAt, isCacheable)` outcome

**Client Adapter** (`framework/outbound/client/`) — `@Repository @RequiredArgsConstructor`; implements the usecase client port using jOOQ, querying another domain's table directly for a single boolean/state check; returns a safe default when the input identifier or resulting row is absent

**jOOQ Repository** (`framework/outbound/persistence/jooq/`) — `@Repository @RequiredArgsConstructor`; DSLContext directly for joined reads, aggregate counts, and paginated read models; conditionally adjusts joins/computed fields based on whether an optional viewer-identity parameter is present (e.g. to compute a per-viewer "liked" flag)

**JPA Entity** (`framework/outbound/persistence/jpa/entity/`) — `@Entity @Table @EntityListeners(AuditingEntityListener.class) @NoArgsConstructor(AccessLevel.PROTECTED) @Getter`:
- Composite PK via `@IdClass` referencing a dedicated composite-key class in `compositekey/`
- Foreign-key associations may be declared with `foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)` where no DB-level FK constraint is enforced
- Exposes intent-named mutation methods (e.g. increment/decrement a counter floored at zero, mark as deleted, update a text field and its edited-timestamp) rather than public setters
- `@PrePersist` callback defaults nullable numeric/boolean columns before insert
- Auditing: `@CreatedDate` / `@LastModifiedDate` timestamp columns
- Manual static nested builder class alongside the Lombok-generated accessors

**Composite Key** (`framework/outbound/persistence/jpa/compositekey/`) — plain class implementing `Serializable`, paired 1:1 with an `@IdClass` entity; `equals`/`hashCode` over all key fields; manual static nested builder including a copy-from-existing-instance factory

**JPA Mapper** (`framework/outbound/persistence/jpa/mapper/`) — MapStruct `@Mapper(componentModel = "spring")` interface: domain Aggregate (+ related entities resolved by the caller) → JPA Entity, `@BeanMapping(ignoreByDefault = true)` with explicit per-field `@Mapping`s; a `default` method may translate a domain status VO into a persisted boolean flag via a qualified mapping

**JPA Repository** (`framework/outbound/persistence/jpa/repository/`) — `@Repository`; extends Spring Data `JpaRepository` parameterized by the entity and its composite-key class; derived query methods keyed by the natural composite fields (not the JPA-internal key type)

**Repository Adapter** (`framework/outbound/persistence/jpa/repository/`) — `@Repository @RequiredArgsConstructor`; implements the usecase write port: resolves related entities by their business identifiers (raising a not-found/invalid-value error if absent), maps the Aggregate to an Entity via the JPA mapper, checks for a pre-existing row before insert, and delegates soft-delete/update operations to the entity's mutation methods before saving