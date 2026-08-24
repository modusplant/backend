---
paths:
  - "src/main/java/kr/modusplant/domains/search/**"
---

> Supplements CLAUDE.md § Architecture — Domain Internal Structure. Assumes familiarity with the four-layer layout and JPA/jOOQ design decision documented there.

# Search Domain Conventions

Applies to `kr.modusplant.domains.search` and its sub-packages.

---

## 1. Package Structure

```
search/
 ├─ domain/
 │   ├─ aggregate/                    # Aggregates
 │   ├─ entity/                       # Entities
 │   ├─ vo/                           # Value Objects; nullobject/ for optional VOs (Empty*)
 │   ├─ enums/                        # Domain status/condition enums
 │   └─ exception/enums/              # Error code enums
 ├─ usecase/
 │   ├─ port/repository/              # Repository port interfaces
 │   ├─ port/mapper/                  # Domain → DTO mapper interfaces
 │   ├─ port/cache/                   # Cache port interfaces
 │   ├─ port/transliterator/          # Transliteration port interfaces
 │   ├─ record/                       # Data-transfer records (REST Controller → adapter)
 │   ├─ response/                     # Response DTOs (Java records)
 │   └─ model/read/                   # Read models for jOOQ query mapping
 ├─ adapter/
 │   ├─ controller/                   # Business orchestration (*Controller)
 │   ├─ mapper/                       # Mapper port implementations
 │   └─ translator/                   # External system abstractions
 └─ framework/
     ├─ inbound/web/rest/             # @RestController (HTTP entry point)
     └─ outbound/
         ├─ jooq/repository/          # DSLContext-based complex queries
         │   └─ mapper/supers/        # Shared base jOOQ mapper interfaces
         ├─ jpa/repository/           # Spring Data JPA interfaces for condition/lookup queries
         ├─ caffeine/                 # In-memory cache implementations of usecase/port/cache/
         ├─ icu4j/                    # Transliteration implementations of usecase/port/transliterator/
         └─ redis/                    # Redis-backed repositories (e.g. search history)
```

---

## 2. domain/ Patterns

**Aggregate** (`domain/aggregate/`):
- `@AllArgsConstructor(AccessLevel.PRIVATE or PROTECTED)` + `static create(...)` factory with internal null checks
- `equals/hashCode` based on the ID VO

**Entity** (`domain/entity/`):
- `protected` constructor + `static create()` factory

**Value Object** (`domain/vo/`):
- `@RequiredArgsConstructor(AccessLevel.PRIVATE)` + `static create()` factory (validates null, regex, length, range)
- Optional VOs (blank/null input allowed): `create()` returns a corresponding `Empty*VO`
  - `Empty*VO`: singleton, `getValue()` returns `null`, uses `@NoArgsConstructor(AccessLevel.PROTECTED, force = true)`, lives under `vo/nullobject/`
- ID VO generation — UUID type: `generate()` · `fromUuid(UUID)` · `fromString(String)`; ULID type: `generate()` · `create(String)`

**Error Codes** (`domain/exception/enums/`):
- `*ErrorCode implements ErrorCode` enum with three fields: `httpStatus(int)`, `code(String)`, `message(String)`

**Enums** (`domain/enums/`):
- Search condition/target/sort values as enums

---

## 3. usecase/ Patterns

**Repository Ports** (`usecase/port/repository/`):
- Parameters and return types use only domain VOs/Aggregates — no JPA entities or jOOQ records

**Cache Ports** (`usecase/port/cache/`):
- Abstracts a cache lookup/store operation over domain VOs; implemented in `framework/outbound/caffeine/`

**Transliterator Ports** (`usecase/port/transliterator/`):
- Abstracts a text-transliteration operation; implemented in `framework/outbound/icu4j/`

**Records** (`usecase/record/`):
- Java records for REST Controller → adapter Controller data transfer; carry raw types (String, Integer, etc.)
- Adapter converts raw types to domain VOs

**Response DTOs** (`usecase/response/`):
- Java records; multi-item responses include a page/relevance-sorted factory where applicable
- Field types: Java primitives, String, UUID, LocalDateTime, JsonNode

**Read Models** (`usecase/model/read/`):
- Java records optimized for specific views; direct targets of jOOQ query mapping

---

## 4. adapter/ Patterns

**Controller** (`adapter/controller/`) — `@Service @Transactional @Slf4j @RequiredArgsConstructor`:
- Receives usecase records, converts to domain VOs, calls ports, returns results

**Mapper** (`adapter/mapper/`) — `@Component`; implements mapper port: domain Aggregate → Response DTO

**Translator** (`adapter/translator/`) — `@Component`; abstracts calls to external systems

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`) — `@RestController @RequestMapping @RequiredArgsConstructor @Validated @Slf4j`:
- HTTP concerns only: request parsing, response serialization, validation
- Wraps request into usecase record and delegates to adapter Controller
- Swagger: `@Tag`, `@Operation`, `@Parameter`, `@Schema`

**jOOQ Repository** (`framework/outbound/jooq/repository/`) — `@Repository`; DSLContext directly for complex joins, relevance-sorted/paginated read models
- `mapper/supers/` holds base jOOQ mapper interfaces shared across repositories in this domain

**JPA Repository** (`framework/outbound/jpa/repository/`) — `@Repository`; Spring Data JPA interface used for condition/lookup queries against a JPA-managed table

**Cache Adapter** (`framework/outbound/caffeine/`) — `@Component`; implements a `usecase/port/cache/` interface with an in-memory Caffeine cache

**Transliterator Adapter** (`framework/outbound/icu4j/`) — `@Component`; implements a `usecase/port/transliterator/` interface using ICU4J

**Redis Repository** (`framework/outbound/redis/`) — `@Repository`; reads/writes Redis directly (e.g. recording and retrieving search history) without an intervening usecase port
