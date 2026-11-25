---
paths:
  - "src/test/java/kr/modusplant/infrastructure/prohibitedword/**"
  - "src/test/java/kr/modusplant/infrastructure/config/prohibitedword/**"
---

# Prohibited Word Infrastructure Test Conventions

## Parent Document

- `.claude/documents/term/text-normalization-term.md` — the view term (§1)

---

## 1. Mocking Exceptions

`ProhibitedWordEngine` is created with a real `TextNormalizer` built from the `Normalizer2` instances of
`new Icu4jConfig()`, not a mock, because its matches are computed from the normalized views. `ProhibitedWordConfigTest`
mocks `JdbcTemplate` with rows of both tables and builds the engine with the same real `TextNormalizer`.

---

## 2. TestUtils

- **Group A (fields):** package root (`ProhibitedWord`, `ProhibitedWordMatch`)
- `ProhibitedWordHolderTestUtils` creates `ProhibitedWordHolder` with a `ProhibitedWordEngine` from
  `ProhibitedWordEngine.create` and the real `TextNormalizer`, so a test that needs real matching gets a working holder
  with no DB.
