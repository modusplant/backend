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

`TextNormalizerTest` asserts a code unit's source character kind, first-source and last-source jamo positions, and
syllable end together through its private `assertUnitTags` helper, because these fields are read per code unit like the
ranges.

---

## 3. TestUtils

When built directly, `TextNormalizedView` is built in each test through the package-private
`TextNormalizedView.Builder`, because a TestUtils under `common/util/` sits in another package and cannot reach it.
