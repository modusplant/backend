---
paths:
  - "src/test/java/kr/modusplant/infrastructure/normalization/**"
---

# Normalization Infrastructure Test Conventions

Applies to every test class under `src/test/java/kr/modusplant/infrastructure/normalization/`.

---

## 1. Mocking Exceptions

`TextNormalizer` is built with the real `Normalizer2` instances of `new Icu4jConfig()`, not mocks, because its output is
the ICU4J normalization result itself.

---

## 2. Range Assertions

`TextNormalizerTest` asserts a code unit's lightly normalized and original ranges together through its private
`assertSpans` helper.

---

## 3. TestUtils

`NormalizedText` is built in each test through the package-private `NormalizedText.Builder`, because a TestUtils under
`common/util/` sits in another package and cannot reach it.
