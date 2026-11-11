---
paths:
  - "src/test/java/kr/modusplant/infrastructure/swear/**"
---

# Swear Infrastructure Test Conventions

Applies to every test class under `src/test/java/kr/modusplant/infrastructure/swear/`.

---

## 1. Mocking Exceptions

`SwearEngine` is created with a real `TextNormalizer` built from the `Normalizer2` instances of `new Icu4jConfig()`, not
a mock, because its matches are computed from the normalized views.

---

## 2. TestUtils

Paths below are relative to `src/main/java/kr/modusplant/infrastructure/swear/` and mirrored 1:1 under
`src/test/java/kr/modusplant/infrastructure/swear/common/util/`.

- **Group A (fields):** package root (`SwearWord`, `SwearMatch`)
