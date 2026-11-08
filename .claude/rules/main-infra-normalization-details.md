---
paths:
  - "src/main/java/kr/modusplant/infrastructure/normalization/**"
---

# Normalization Infrastructure Conventions

Applies to `kr.modusplant.infrastructure.normalization`.

---

## 1. Package Structure

The package is flat: three classes, no sub-packages.

```
normalization/
├ TextNormalizer # Spring bean; Unicode normalization forms and lightly and aggressively normalized text
├ NormalizedText # A normalized text and, per UTF-16 code unit, its source ranges
└ NormalizationCharacterMap # Immutable code-point maps that TextNormalizer reads
```

---

## 2. Class Responsibilities

**`TextNormalizer`** — `@Component`:
- Receives the NFD, NFC, NFKD, and NFKC `Normalizer2` instances as `Icu4jConfig` beans, selected with
  `@Qualifier(*_NORMALIZER_BEAN_NAME)`, so every normalization form follows the one Unicode version of ICU4J.
- `nfdNormalize(String)` / `nfcNormalize(String)` → the NFD/NFC form of plant-name search.
- `lightlyNormalize(String)` → the lightly normalized text as a `NormalizedText` (§3).
- `aggressivelyNormalize(NormalizedText)` → the aggressively normalized text of a lightly normalized text as a
  `NormalizedText` (§4).

**`NormalizedText`** — `final`, immutable:
- Holds the normalized text and four parallel `int[]` arrays indexed by its UTF-16 code units: for each code unit, the
  half-open range `[start, end)` of UTF-16 indexes it derives from in the lightly normalized text and in the original
  text.
- Read through `getText()`, `length()`, `getLightlyNormalizedStart` / `getLightlyNormalizedEnd`, and
  `getOriginalStart` / `getOriginalEnd`, each taking a code-unit index.
- Built only inside the package through the package-private `NormalizedText.Builder` (`append` one code unit with its
  four range bounds, then `build`), which copies its arrays to the final size.

**`NormalizationCharacterMap`** — `final`, `@NoArgsConstructor(access = PRIVATE)`:
- Holds the maps as immutable `public static final Map<Integer, Integer>` constants keyed by code point.
- Is static, so it takes its `Normalizer2` from `Normalizer2` directly instead of an `Icu4jConfig` bean.
- `CONJOINING_COMPATIBILITY_JAMO_MAP`: every conjoining jamo whose `Hangul_Syllable_Type` is L, V, or T → the Hangul
  Compatibility Jamo code point of the same name without its `HANGUL CHOSEONG/JUNGSEONG/JONGSEONG` prefix; a jamo
  without a same-named compatibility jamo (an archaic jamo, a filler) is left out.
- `LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP`: the regional indicator symbols `🇦`–`🇿` and the negative circled and
  negative squared Latin capital letters, which have no compatibility decomposition → Latin lowercase letters.
- `CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP`: lowercase Cyrillic and Greek letters confusable with Latin letters (`ѕ`, `і`)
  → Latin lowercase letters.
- `HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP`: digits, symbols, and letters that stand for a jamo next to Hangul (`1`, `l`,
  `|`, `0`, `o`, `^`, `人`) → Hangul Compatibility Jamo.
- `LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP`: digits and symbols that stand for a letter next to Latin letters (`1`,
  `0`, `$`, and others) → Latin lowercase letters; `!` is left out, because a sentence can end with it.

**`Regex.PATTERN_FILLER_JAMO`** in `shared/constant/Regex` matches one of `ㅋ`, `ㅎ`, `ㅠ`, `ㅜ`.

---

## 3. `lightlyNormalize`

The original text is segmented into extended grapheme clusters with ICU4J `BreakIterator.getCharacterInstance()`. Each
cluster passes these steps in order:

| Order | Step |
|---|---|
| 1 | Remove code points with the `Default_Ignorable_Code_Point` property (including the Hangul fillers) |
| 2 | NFKD, remove combining marks (general categories `Mn`, `Mc`, `Me`), NFKC |
| 3 | Map conjoining jamo through `CONJOINING_COMPATIBILITY_JAMO_MAP` |
| 4 | Map code points through `LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP` |
| 5 | Lowercase per code point with `UCharacter.toLowerCase` |

- Every UTF-16 code unit a cluster produces gets the whole output range of the cluster as its lightly normalized range
  (`ﬁ` → `fi`: both code units `[0, 2)`) and the whole cluster as its original range, so a surrogate pair is never split
  (`𝐬` → `s`: `[0, 2)`).
- Whether a code point belongs to a character class defined by a Unicode property is decided by the return value of an
  ICU4J property query (`UCharacter.hasBinaryProperty` for `Default_Ignorable_Code_Point`, `UCharacter.getType` for the
  general categories), not by a handwritten code-point range list.
- Lowercasing is per code point, because `String.toLowerCase` can change the length and break the per-code-unit
  ranges.
- Whitespace and punctuation are kept (`시 발.` stays `시 발.`).
- An archaic jamo without a compatibility form is kept, so no letter is lost.
- A cluster made up only of default-ignorable code points and combining marks produces no code unit, so a text of such
  clusters yields an empty `NormalizedText`.

---

## 4. `aggressivelyNormalize`

The lightly normalized text passes these steps in order, each building a new `NormalizedText`:

| Order | Step |
|---|---|
| 1 | Map code points through `CROSS_SCRIPT_HOMOGLYPH_LATIN_MAP` |
| 2 | Decide each key of `HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP` and `LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP` by its nearest neighbors (table below) |
| 3 | Remove code points of the general categories `Z*`, `Cc`, `P*`, `S*`, and `N*` (separators, control characters, punctuation, symbols, numbers) |
| 4 | Remove each consecutive run of `PATTERN_FILLER_JAMO` code points that has a Hangul syllable on both sides |
| 5 | Collapse consecutive identical code points into one |
| 6 | Merge an elongated vowel into its syllable (rules below) |

Step 2 reads the nearest code point on each side that is neither in the step 3 general categories nor a key of either
map, so a chain of keys is decided together (`人l발` → `ㅅㅣ발`):

| Neighbors | Result |
|---|---|
| A Hangul syllable on both sides | Removed |
| Hangul on one or both sides, no Latin | `HANGUL_CONTEXT_LOOK_ALIKE_JAMO_MAP` value; unchanged without an entry |
| Latin on one or both sides, no Hangul | `LATIN_CONTEXT_LOOK_ALIKE_LEET_LETTER_MAP` value; unchanged without an entry |
| Hangul and Latin, or neither | Unchanged, so `tl발` keeps its Latin letters |

- Step 2 runs before step 3, because step 3 removes the digits and symbols that step 2 reads.
- Script and syllable checks are ICU4J property queries (`UScript.getScript`, `UProperty.HANGUL_SYLLABLE_TYPE`,
  `UProperty.BLOCK`).
- Step 5 compares code points, because comparing UTF-16 code units would join two different supplementary characters
  that share a high surrogate.
- Step 6: a Hangul syllable without a final absorbs a following Hangul Compatibility Jamo of the same vowel, or a
  following syllable with the initial `ㅇ` and the same vowel, taking that syllable's final (`바알` → `발`). The merged
  syllable keeps absorbing while it has no final.
- A merged or collapsed code unit takes the union of its source code units' ranges, and every kept code unit keeps its
  own lightly normalized range (`시 발`: `[0, 1)` and `[2, 3)`).
