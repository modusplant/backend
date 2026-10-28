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
├ domain/
│├ aggregate/
│├ enums/ # Status-style backing enums
│├ event/
│├ exception/ # Domain-local exception classes
││└ enums/
│└ vo/
│ └ nullobject/
├ usecase/
│├ model/
│├ port/
││├ mapper/
││└ repository/ # Query/Command/Cache/lookup repository port interfaces
│├ request/
│└ response/
├ adapter/
│├ controller/
│├ helper/ # Cross-aggregate write-precondition checks
│├ listener/ # Synchronous listeners for other domains' events
│└ mapper/
└ framework/
 ├ inbound/web/rest/
 ├ inbound/web/cache/ # ETag / Last-Modified conditional-GET support
 │└ model/ # Cache result carrier record
 ├ inbound/flyway/ # Flyway callbacks reacting to applied migrations
 └ outbound/
  ├ jooq/
  │└ repository/
  ├ redis/ # Redis-backed idempotency + sibling-order allocation
  └ jpa/
   ├ entity/
   ├ compositekey/
   ├ mapper/ # Domain ↔ JPA Entity mapping implementation
   │└ supers/ # Domain ↔ JPA Entity mapping interface
   └ repository/ # Spring Data JPA interface + write-port adapter
```

---

## 2. domain/ Patterns

**Aggregate** (`domain/aggregate/`):
- An optional reference VO (e.g. the reply target's path) is passed to `create(...)` as its null-object subclass rather
  than null
- May expose an overloaded `create(...)` factory that accepts an explicit optional field alongside the primary factory
  that derives it internally with a default
- Equality is based on the composite identity fields (the referenced post and the comment's own positional identifier)

**Value Object** (`domain/vo/`):
- An identity VO wrapping a UUID offers a nullable-tolerant `fromNullableUuid(...)` that returns its null-object
  subclass for null input in contexts where the value is genuinely optional (e.g. an unauthenticated viewer)
- ID-style VOs validate against a fixed-format identifier pattern (length + regex) rather than parsing structured
  components
- A path-style VO may validate a delimited, hierarchical string format (digit segments separated by a fixed delimiter)
  with additional structural constraints (no leading-zero segments, 1-based indexing, a capped maximum nesting depth)
- A bounded numeric VO (e.g. a page size) rejects null and values outside a fixed inclusive range
- Status VOs wrap a backing enum and expose named convenience factories (e.g. `active()`, `deleted()`) alongside the raw
  `create(String)` factory

**Domain Events** (`domain/event/`):
- May derive a classification field (e.g. an action-type string) internally from the shape of another field rather than
  accepting it as a parameter

**Domain Exceptions** (`domain/exception/`):
- Validation failures throw the shared kernel exceptions directly, parameterized with a `domain/exception/enums` error
  code
- A failure with no matching shared kernel exception (e.g. a soft-deleted reply target) has a domain-local exception
  extending the shared `BusinessException`, whose no-argument constructor fixes its error code

**Enums** (`domain/enums/`):
- Backing enums for status-style VOs; expose a static helper to check whether a raw string is a recognized value

---

## 3. usecase/ Patterns

**Repository Ports** (`usecase/port/repository/`):
- Split by responsibility into a query-side port (`*QueryRepository`), a command-side port (`*CommandRepository`)
  (CQRS-style), and a cache-side port (`*CacheRepository`) implemented over a key-value store, each implemented by a
  different framework technology; separate read-only lookup ports, each named after the referenced object (the author,
  the immediate parent post), cover cross-aggregate references
- The query port may also declare an ownership check (whether a comment was written by a given author)
- The query port may expose a lookup for the highest existing child ordinal beneath a given parent path, used to set an
  external counter

**Cache Port** (`usecase/port/repository/`):
- Declares a reservation operation that returns an `Optional` which is empty when the same request was already processed
  (idempotency)
- Accepts a lazily-evaluated fallback supplier so the implementation reads the database only when its own state is cold
- Declares a bulk eviction operation that expires every path-bearing cache entry and returns the evicted count

**Mapper Port** (`usecase/port/mapper/`):
- Declares domain construction (raw VOs → Aggregate) and read-model-to-response mapping, including converting a
  paginated read model's zero-based page index to a one-based index for API consumers; implemented by a handwritten
  (non-generated) adapter class

**Response DTOs** (`usecase/response/`):
- A generic offset-paginated wrapper carries the page/size/total metadata, and a cursor-paginated wrapper carries the
  items, the last item's path as the next cursor, a has-next flag, and the item count

---

## 4. adapter/ Patterns

**Controller** (`adapter/controller/`):
- Delegates cross-aggregate precondition checks to a dedicated validation helper; `updateContent` and `delete`
  additionally require the caller to be the comment's author via the helper's ownership check
- For hierarchically-addressed records (a delimited path identifying position in a tree), verifies the parent position
  exists before inserting a nested record; the record's final positional index is assigned server-side rather than taken
  from the request
- When an optional reply target path is given, validates that both it and the new record's path are depth-2 paths under
  the same root, then verifies through the helper that the reply target exists and is not soft-deleted
- For cursor pagination, verifies the cursor record exists, requests one row beyond the page size, and derives the
  has-next flag and next cursor from that extra row
- Reserves the server-authoritative path (idempotency marker plus next-sibling ordinal) through the cache port before
  constructing the aggregate
- Publishes a domain event after a successful write via `ApplicationEventPublisher`

**Validation Helper** (`adapter/helper/`):
- Exposes single-purpose precondition checks
- Exposes composite methods that bundle the set of checks a given operation needs (create-new vs. modify-existing)
- Throws the shared kernel exceptions parameterized with a `domain/exception/enums` error code, or a domain-local
  exception; the ownership failure maps to HTTP 403

**Event Listener** (`adapter/listener/`) — `@EventListener` methods run in the publisher's transaction:
- On another domain's abuse-report approval event, requires the event's member to hold the admin role (otherwise throws
  `NotAccessibleException` with `GeneralErrorCode.ADMIN_ROLE_REQUIRED_TO_ACCESS`), runs the helper's parent-post and
  comment existence checks, then soft-deletes the comment without an author-ownership check

**Mapper** (`adapter/mapper/`):
- Exposes a reply target as deleted with a null nickname when it is soft-deleted, or when its row is absent while the
  reply-target path is set; the reply-target path itself is returned as stored

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`):
- Extracts auth via `@AuthenticationPrincipal` (nullable for endpoints with optional authentication); wraps parameters,
  including the caller's UUID for `register`, `updateContent`, and `delete`, into a usecase call and delegates to the
  adapter Controller
- Implements conditional GET (`If-None-Match` / `If-Modified-Since`) by delegating cache-state computation to a
  dedicated cache service, then returning either a 304 (headers only) or a 200 (full body) response

**HTTP Cache** (`framework/inbound/web/cache/`):
- ETag comparison uses a one-way hash/encoder match rather than literal string equality
- May expose multiple overloads keyed by different addressing schemes (e.g. by parent resource vs. by author) for the
  same conditional-GET pattern
- `model/`: a record carrying the computed `(entityTag, lastModifiedAt, isCacheable)` outcome

**Flyway Callback** (`framework/inbound/flyway/`) — implements Flyway's `Callback` and is auto-registered by Spring
Boot's Flyway auto-configuration:
- Records on `AFTER_EACH_MIGRATE` whether a specific migration version was applied in the current run
- Acts on `AFTER_MIGRATE` / `AFTER_MIGRATE_ERROR` only when that version was applied, delegating the side effect (e.g.
  cache eviction) to a usecase port

**jOOQ Repository** (`framework/outbound/jooq/repository/`):
- Serves joined reads, aggregate counts, and paginated read models
- Cursor-paginated reads use keyset seek on the shared sort key, first reading the cursor row's sort-key values (an
  empty list when it is absent)
- Conditionally adjusts joins/computed fields based on whether an optional viewer-identity parameter is present
- Resolves an optional same-table self-reference (the reply target addressed by path within the same post) and its
  author through aliased left joins
- May implement the ownership check as an existence query matching the comment's identity and its author
- May expose a highest-child-ordinal lookup beneath a given parent path as a `MAX` aggregate over per-depth ordinal
  columns (root-level: the first-depth column among rows without a second-depth value; nested: the second-depth column
  among rows sharing the root's first-depth value), returning 0 when no sibling exists

**Redis Repository** (`framework/outbound/redis/`) — implements the cache port over a `StringRedisTemplate`:
- Key strings are built from unified format-string constants, one per purpose (an idempotency marker keyed by the
  write's identifying tuple; a per-parent ordinal counter using a literal sentinel for the root parent), all under a
  bounded, slidingly-refreshed TTL
- The reservation runs a set-if-absent idempotency guard (a lost race yields an empty `Optional`), then draws the next
  ordinal from an atomic increment counter that is set from the caller-supplied database lower bound before its first
  increment, so a cold counter cannot collide with existing rows
- Bulk eviction walks each key family with cursor-based `SCAN` (never `KEYS`) and deletes the matches in fixed-size
  batches
- Null replies and data-access errors are rethrown as a shared connection-failure exception

**JPA Entity** (`framework/outbound/jpa/entity/`):
- Mutation methods include incrementing/decrementing a counter floored at zero and marking the comment as deleted or
  chosen
- `@PrePersist` callback defaults nullable numeric/boolean columns before insert

**JPA Mapper** (`framework/outbound/jpa/mapper/`) — `*Impl` class implementing an interface in `mapper/supers/`: domain
Aggregate (+ related entities resolved by the caller) → JPA Entity via the entity's builder, setting only the fields
derived from its inputs; a `private` method translates a domain status VO into a persisted boolean flag

**JPA Repository** (`framework/outbound/jpa/repository/`) — extends Spring Data `JpaRepository` parameterized by the
entity and its composite-key class; derived query methods keyed by the natural composite fields (not the JPA-internal
key type)

**Repository Adapter** (`framework/outbound/jpa/repository/`) — implements the usecase write port: resolves related
entities by their business identifiers, maps the Aggregate to an Entity via the JPA mapper, and delegates
soft-delete/update operations to the entity's mutation methods before saving

**Lookup Repository Adapter** (`framework/outbound/jpa/repository/`) — implements a read-only lookup port over the
referenced domain's Spring Data repository; a lookup that must return a value throws a not-found exception parameterized
with a `domain/exception/enums` error code when the referenced row is absent, and a state check returns false for an
absent row
