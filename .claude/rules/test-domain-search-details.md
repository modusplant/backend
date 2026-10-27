---
paths:
  - "src/test/java/kr/modusplant/domains/search/**"
---

# Search Domain Test Conventions

Applies to every test class under `src/test/java/kr/modusplant/domains/search/`.

---

## 1. Exception Assertions

Exception assertions check `getErrorCode()` against `SearchErrorCode`.

---

## 2. Pure Unit Test Path Exceptions

- `framework/outbound/jpa/repository`: necessary to verify DB interaction.

---

## 3. Repository Test Policy

- **jOOQ repository** (`framework/outbound/jooq/repository`): `integration-test`.
  Evidence: `src/test/java/kr/modusplant/domains/search/framework/outbound/jooq/repository/SearchPostRepositoryJooqAdapterIntegrationTest.java`

---

## 4. TestUtils

Paths below are relative to `src/main/java/kr/modusplant/domains/search/` and mirrored 1:1 under
`src/test/java/kr/modusplant/domains/search/common/util/`.

- **Shared constant paths:** `domains/search/common/constant`, `domains/post/common/constant`, `domains/comment/common/constant`
- **Group A (fields):** `domain/vo` (incl. `vo/nullobject`), `usecase/model/read`, `usecase/record`, `usecase/response`
- **Group B (methods):** `domain/aggregate`, `domain/entity`
