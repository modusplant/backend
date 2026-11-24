---
paths:
  - "src/test/java/kr/modusplant/infrastructure/normalization/**"
---

# Normalization Infrastructure Test Conventions

## 1. Mocking Exceptions

`TextNormalizer` is built with the real `Normalizer2` instances of `new Icu4jConfig()`, not mocks, because its output is
the ICU4J normalization result itself.

---

## 2. Range Assertions

`TextNormalizerTest` asserts a code unit's lightly normalized and original ranges together through its private
`assertRanges` helper.

---

## 3. TestUtils

A test takes a `TextNormalizedView` it needs as test data from `TextNormalizedViewTestUtils`, the per-class TestUtils.
`TextNormalizedViewTest` is the exception and calls `TextNormalizedView.Builder` directly, because the `Builder` is
what it tests.
