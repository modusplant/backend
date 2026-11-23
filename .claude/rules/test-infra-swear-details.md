---
paths:
  - "src/test/java/kr/modusplant/infrastructure/swear/**"
  - "src/test/java/kr/modusplant/infrastructure/config/swear/**"
---

# Swear Infrastructure Test Conventions

## 1. Mocking Exceptions

`SwearEngine` is created with a real `TextNormalizer` built from the `Normalizer2` instances of `new Icu4jConfig()`, not
a mock, because its matches are computed from the normalized views. `SwearConfigTest` mocks `JdbcTemplate` with rows of
both tables and builds the engine with the same real `TextNormalizer`.

---

## 2. TestUtils

- **Group A (fields):** package root (`SwearWord`, `SwearMatch`)
- `SwearHolderTestUtils` creates `SwearHolder` with a `SwearEngine` from `SwearEngine.create` and the real
  `TextNormalizer`, so a test that needs real matching gets a working holder with no DB.
