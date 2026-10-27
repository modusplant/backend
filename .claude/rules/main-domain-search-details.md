---
paths:
  - "src/main/java/kr/modusplant/domains/search/**"
---

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
         └─ redis/                    # Redis-backed repositories (e.g. search history)
```

---

## 2. domain/ Patterns

**Aggregate** (`domain/aggregate/`):
- `equals/hashCode` based on the ID VO

**Value Object** (`domain/vo/`):
- `@RequiredArgsConstructor(AccessLevel.PRIVATE)`; `create()` validates null, regex, length, range
- `Empty*` null-object VOs use `@NoArgsConstructor(AccessLevel.PROTECTED, force = true)`
- ID VO generation — UUID type: `generate()` · `fromUuid(UUID)` · `fromString(String)`; ULID type: `generate()` · `create(String)`

**Enums** (`domain/enums/`):
- Search condition/target/sort values as enums

---

## 3. usecase/ Patterns

**Repository Ports** (`usecase/port/repository/`):
- Return types use only domain VOs/Aggregates

**Cache Ports** (`usecase/port/cache/`):
- Abstracts a cache lookup/store operation over domain VOs; implemented in `framework/outbound/caffeine/`

**Records** (`usecase/record/`):
- Raw types carried: String, Integer, etc.

**Response DTOs** (`usecase/response/`):
- Multi-item responses include a page/relevance-sorted factory where applicable
- Field types: Java primitives, String, UUID, LocalDateTime, JsonNode

---

## 4. adapter/ Patterns

**Controller** (`adapter/controller/`) — `@Service @Transactional @Slf4j @RequiredArgsConstructor`

**Mapper** (`adapter/mapper/`): domain Aggregate → Response DTO

---

## 5. framework/ Patterns

**REST Controller** (`framework/inbound/web/rest/`) — `@RestController @RequestMapping @RequiredArgsConstructor @Validated @Slf4j`:
- Wraps the request into a usecase record

**jOOQ Repository** (`framework/outbound/jooq/repository/`) — serves complex joins, relevance-sorted/paginated read models
- `mapper/supers/` holds base jOOQ mapper interfaces shared across repositories in this domain

**JPA Repository** (`framework/outbound/jpa/repository/`) — used for condition/lookup queries against a JPA-managed table

**Cache Adapter** (`framework/outbound/caffeine/`) — `@Component`; implements a `usecase/port/cache/` interface with an in-memory Caffeine cache

**Redis Repository** (`framework/outbound/redis/`) — `@Repository`; reads/writes Redis directly (e.g. recording and retrieving search history) without an intervening usecase port
