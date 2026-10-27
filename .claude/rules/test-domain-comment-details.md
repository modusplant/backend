---
paths:
  - "src/test/java/kr/modusplant/domains/comment/**"
---

# Comment Domain Test Conventions

Applies to every test class under `src/test/java/kr/modusplant/domains/comment/`.

---

## 1. Exception Assertions

Exception assertions check `getErrorCode()` against `CommentErrorCode`.

---

## 2. Pure Unit Test Path Exceptions

- `framework/outbound/jpa/entity`: uses `@RepositoryOnlyContext` + `TestEntityManager` to verify
  `@PrePersist` defaulting and JPA-managed behavior.
- `framework/outbound/jpa/repository`: uses `@RepositoryOnlyContext` to verify DB interaction.

---

## 3. Repository Test Policy

- **jOOQ repository** (`framework/outbound/jooq/repository`): `unit-test (jOOQ MockConnection/MockDataProvider)`.
  Evidence: `src/test/java/kr/modusplant/domains/comment/framework/outbound/jooq/repository/CommentJooqRepositoryTest.java`
- **Redis repository** (`framework/outbound/redis`): `unit-test (mocked StringRedisTemplate)` — a pure
  unit test that Mockito-mocks `StringRedisTemplate` and the `ValueOperations` returned by
  `opsForValue()`, stubs `setIfAbsent` / `increment` / `expire`, and asserts on the composed key
  strings, the counter set value, and the TTL. Bulk eviction stubs `scan` with an iterator-backed
  mocked `Cursor` per key pattern and records each `delete` batch as a copy at call time. No Spring context.

---

## 4. TestUtils

Paths below are relative to `src/main/java/kr/modusplant/domains/comment/` and mirrored 1:1 under
`src/test/java/kr/modusplant/domains/comment/common/util/`.

- **Shared constant paths:** `domains/comment/common/constant`, `domains/post/common/constant`, `domains/member/common/constant`, `domains/notification/common/constant`
- **Group A (fields):** `domain/vo`, `domain/event`, `domain/aggregate`, `usecase/model`, `usecase/request`, `usecase/response`, `framework/inbound/web/cache/model`, `framework/outbound/jpa/compositekey`
- **Group B (methods):** `framework/outbound/jpa/entity`
