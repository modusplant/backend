---
paths:
  - "src/main/java/kr/modusplant/domains/search/**"
---

# Search Domain Conventions

## 1. Package Structure

```
search/
├ domain/
│├ aggregate/
│├ entity/
│├ vo/
│├ enums/ # Search condition/target/sort enums
│└ exception/enums/
├ usecase/
│├ port/repository/
│├ port/mapper/
│├ port/cache/ # Cache port interfaces
│├ record/
│├ response/
│└ model/read/
├ adapter/
│├ controller/
│├ mapper/
│└ translator/
└ framework/
 ├ inbound/web/rest/
 └ outbound/
  ├ jooq/
  │├ repository/
  │└ mapper/supers/ # jOOQ record → read model mapper
  ├ jpa/repository/ # Category-condition lookups
  ├ caffeine/ # In-memory cache implementations of usecase/port/cache/
  └ redis/ # Search-history repository
```

---

## 2. domain/ Patterns

**Value Object**:
- Score-like VOs (`SearchKeywordSimilarity`, `SearchPostImportance`) return an internal empty singleton for null input,
  marked by an `empty` flag or an `EMPTY` enum constant instead of a null-object subclass; `isEmpty()` reports it, and
  `getValueIfNotEmpty()` throws `InvalidValueException` with a `SearchErrorCode` on an empty instance

**Enums**:
- Search condition/target/sort values as enums

---

## 3. usecase/ Patterns

**Repository Ports**:
- The post-search port exposes one keyword search per sort order (latest, relevance), each returning read models
- The condition port checks that the requested primary/secondary category IDs exist

**Cache Ports**:
- Serves the transliterated plant names used for plant-name search; implemented in `framework/outbound/caffeine/`

**Response DTOs**:
- A relevance-sorted page response carries its own `of(...)` factory

---

## 4. framework/ Patterns

**jOOQ Repository** — serves keyword searches sorted by latest or by relevance, returning paginated read models

**JPA Repository** — serves the category-condition existence checks against a JPA-managed table

**Cache Adapter** — implements a `usecase/port/cache/` interface with an in-memory Caffeine cache

**Redis Repository** — implements the search-history port, recording and retrieving a searcher's history in Redis
