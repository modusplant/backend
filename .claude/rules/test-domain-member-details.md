---
paths:
  - "src/test/java/kr/modusplant/domains/member/**"
---

# Member Domain Test Conventions

## 1. Exception Assertions

Exception assertions check `getErrorCode()` against `MemberErrorCode`, or against `EntityErrorCode` when the absent
row is a post or comment.

---

## 2. Excluded Classes

- jOOQ repository classes (see the jOOQ repository policy below).

---

## 3. Pure Unit Test Path Exceptions

- `framework/outbound/jpa/entity`: only if absolutely necessary to save data directly during testing.
- `framework/outbound/jpa/repository`: necessary to verify DB interaction.

---

## 4. Repository Test Policy

- **jOOQ repository** (`framework/outbound/jooq/repository`): `excluded`. Evidence: no test file exists under
  `framework/outbound/jooq/`.
- **Redis repository** (`framework/outbound/redis`): `unit-test (mocked StringRedisTemplate)`, also mocking the JPA
  fallback repository and asserting whether a cache miss reads the JPA lookup and populates the cache.

---

## 5. TestUtils

- **Shared constant paths:** `domains/member/common/constant`, `domains/post/common/constant`,
  `domains/comment/common/constant`
- **Group A (fields):** `domain/event`, `domain/vo` (incl. `vo/nullobject`), `framework/outbound/jpa/compositekey`,
  `framework/outbound/jpa/entity/record` (records only), `usecase/model/read`, `usecase/record`, `usecase/request`,
  `usecase/response`
- **Group B (methods):** `domain/aggregate`, `domain/entity` (incl. `entity/nullobject`),
  `framework/outbound/jpa/entity` (excluding `entity/record`)
