---
paths:
  - "src/main/java/kr/modusplant/infrastructure/normalization/**"
---

# Normalization Infrastructure Conventions

Applies to `kr.modusplant.infrastructure.normalization`.

---

## 1. Package Structure

The package is flat: five classes, no sub-packages.

```
normalization/
├ TextNormalizer # Spring bean; Unicode normalization forms and the light, aggressive, keystroke, and line-initial views
├ TextNormalizedView # A normalized text and, per UTF-16 code unit, its source ranges and keystroke tags
├ TextNormalizationType # The normalization method that produced a TextNormalizedView
├ TextNormalizationCharacterMap # Immutable code-point maps that TextNormalizer reads
└ TextNormalizationHelper # Stateless code-point predicates that TextNormalizer calls
```

---

## 2. Class Responsibilities

**`TextNormalizer`** — `@Component`:
- Receives the NFD, NFC, NFKD, and NFKC `Normalizer2` instances as `Icu4jConfig` beans, selected with
  `@Qualifier(*_NORMALIZER_BEAN_NAME)`, so every normalization form follows the one Unicode version of ICU4J.
- `nfdNormalize(String)` / `nfcNormalize(String)` → the NFD/NFC form of plant-name search.
- `lightlyNormalize(String)` → the lightly normalized view as a `TextNormalizedView` (§3).
- `aggressivelyNormalize(TextNormalizedView)` → the aggressively normalized view of a lightly normalized view as a
  `TextNormalizedView` (§4).
- `decomposeIntoKeystrokes(TextNormalizedView, TextNormalizedView)` → the keystroke view of an aggressively normalized
  view and its lightly normalized view as a `TextNormalizedView` (§5).
- `extractLineInitials(TextNormalizedView)` → the line-initial view of a lightly normalized view as a
  `TextNormalizedView` (§6).

**`TextNormalizedView`** — `final`, immutable:
- Holds the normalized text and four parallel `int[]` arrays indexed by its UTF-16 code units: for each code unit, the
  half-open range `[start, end)` of UTF-16 indexes it derives from in the lightly normalized view and in the original
  text.
- Read through `getText()`, `length()`, `getLightlyNormalizedStart` / `getLightlyNormalizedEnd`, and
  `getOriginalStart` / `getOriginalEnd`, each taking a code-unit index.
- Also holds the keystroke tags of each code unit, read by code-unit index:
  - `getSourceCharacterKind` → the nested enum `SourceCharacterKind`: `HANGUL_SYLLABLE`, `STANDALONE_JAMO`,
    `LATIN_LETTER`, `OTHER`.
  - `getFirstSourceJamoPosition` / `getLastSourceJamoPosition` → the nested enum `JamoPosition` of the code unit's first
    and last source: `INITIAL`, `MEDIAL`, `FINAL`, `STANDALONE`.
  - `getSyllableEnds` → whether the code unit ends its syllable.
- Built only inside the package through the package-private `TextNormalizedView.Builder` (`append` one code unit with
  its four range bounds, then `build`), which copies its arrays to the final size. The `append` overload that also takes
  the keystroke tags serves the keystroke view; a code unit appended without them has `OTHER`, `STANDALONE`, and
  `false`.

**`TextNormalizationType`** — enum of the normalization method that produced a `TextNormalizedView`: `LIGHT`
(`lightlyNormalize`), `AGGRESSIVE` (`aggressivelyNormalize`), `KEYSTROKE` (`decomposeIntoKeystrokes`), `LINE_INITIAL`
(`extractLineInitials`); declared in that order. `TextNormalizedView` does not hold it.

**`TextNormalizationCharacterMap`** — `final`, `@NoArgsConstructor(access = PRIVATE)`:
- Holds the maps as immutable `public static final Map` constants keyed by code point, with code-point values unless
  stated otherwise.
- Is static, so it takes its `Normalizer2` from `Normalizer2` directly instead of an `Icu4jConfig` bean.
- `CONJOINING_COMPATIBILITY_JAMO_MAP`: every conjoining jamo whose `Hangul_Syllable_Type` is L, V, or T → the Hangul
  Compatibility Jamo code point of the same name without its `HANGUL CHOSEONG/JUNGSEONG/JONGSEONG` prefix; a jamo
  without a same-named compatibility jamo (an archaic jamo, a filler) is left out.
- `LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP`: the regional indicator symbols `🇦`–`🇿` and the negative circled and
  negative squared Latin capital letters, which have no compatibility decomposition → Latin lowercase letters.
- `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP`: lowercase Cyrillic and Greek letters confusable with Latin letters (`ѕ`, `і`)
  → Latin lowercase letters.
- `COMPATIBILITY_CONJOINING_CHOSEONG_MAP`, `COMPATIBILITY_CONJOINING_JUNGSEONG_MAP`,
  `COMPATIBILITY_CONJOINING_JONGSEONG_MAP`: Hangul Compatibility Jamo → the conjoining jamo of that syllable position;
  only a jamo that composes into a precomposed syllable is included, so an archaic jamo is left out.
- `LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP`: Latin lowercase letters → the Hangul Compatibility Jamo on the same 2-beolsik
  (KS X 5002) key.
- `COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP`: a compound vowel or compound final Hangul
  Compatibility Jamo → a `String` of its 2-beolsik keystroke jamo in typing order; handwritten, because no Unicode
  property gives this split.
- `KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP`: the inverse of the map above, a `String` key of two
  keystroke jamo → their compound Hangul Compatibility Jamo; derived from that map, so the two cannot drift apart.

**`TextNormalizationHelper`** — package-private, `final`, `@NoArgsConstructor(access = PRIVATE)`:
- Holds the package-private `static` code-point predicates of `TextNormalizer`, which read no `Normalizer2`.
- `isInDICP`: an `IntPredicate` for the `Default_Ignorable_Code_Point` property.
- `isInCombiningMark`: the general categories `Mn`, `Mc`, and `Me`.
- `isNonLetterNoiseCategory`: the general categories that step 2 of §4 removes.
- `isNoiseJamo`: a code point that `Regex.PATTERN_NOISE_JAMO` matches.
- `isLineTerminator`: a code point that `Regex.PATTERN_LINE_TERMINATOR` matches.
- `isHangulSyllable`: the `Hangul_Syllable_Type` LV or LVT; `isHangulSyllableWithoutFinal`: LV only;
  `isHangulSyllableWithFinal`: LVT only.
- `isCompatibilityJamo`: a code point in the Hangul Compatibility Jamo block.

**`Regex.PATTERN_NOISE_JAMO`** in `shared/constant/Regex` matches one of `ㅋ`, `ㅎ`, `ㅠ`, `ㅜ`.

**`Regex.PATTERN_LINE_TERMINATOR`** in `shared/constant/Regex` matches one Unicode line terminator (`\R`).

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
  clusters yields an empty `TextNormalizedView`.

---

## 4. `aggressivelyNormalize`

The lightly normalized view passes these steps in order, each building a new `TextNormalizedView`:

| Order | Step |
|---|---|
| 1 | Map code points through `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP` |
| 2 | Remove code points of the general categories `Z*`, `Cc`, `P*`, `S*`, and `N*` (separators, control characters, punctuation, symbols, numbers) |
| 3 | Remove each run of `PATTERN_NOISE_JAMO` code points that has a Hangul syllable on both sides |
| 4 | Collapse consecutive identical code points into one |
| 5 | Compose each run of Hangul Compatibility Jamo into syllables |
| 6 | Merge an elongated vowel or consonant into its syllable (rules below) |

- Syllable checks are ICU4J property queries (`UProperty.HANGUL_SYLLABLE_TYPE`, `UProperty.BLOCK`).
- Step 4 compares code points, because comparing UTF-16 code units would join two different supplementary characters
  that share a high surrogate.
- Step 5: a run (maximally successive Hangul Compatibility Jamo) is read left to right as an initial, a vowel, and an
  optional final, and a consonant followed by a vowel starts the next syllable (`ㅂㅕㅇ신` → `병신`). A run that does not
  divide entirely into syllables is kept, because composing part of it would turn `ㅅㅂㅠ` into `ㅅ뷰` and lose `ㅅㅂ`. Step 5
  runs before step 6, so a composed syllable can absorb an elongated vowel (`ㅅㅣ이발` → `시발`).
- Step 6, vowel: a Hangul syllable without a final absorbs a following Hangul Compatibility Jamo of the same vowel, or a
  following syllable with the initial `ㅇ` and the same vowel, taking that syllable's final (`바알` → `발`). The merged
  syllable keeps absorbing while it has no final.
- Step 6, consonant: a Hangul Compatibility Jamo between two Hangul syllables is absorbed into the preceding syllable
  when it equals that syllable's final or the following syllable's initial (`존ㄴ나` → `존나`, `시ㅂ발` → `시발`).
- A merged or collapsed code unit takes the union of its source code units' ranges, and every kept code unit keeps its
  own lightly normalized range (`시 발`: `[0, 1)` and `[2, 3)`).

---

## 5. `decomposeIntoKeystrokes`

The aggressively normalized view passes these steps in order, each building a new `TextNormalizedView`:

| Order | Step |
|---|---|
| 1 | Expand each key of `LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP` back into the Latin letters of its lightly normalized range (rules below) |
| 2 | Convert Latin letters to 2-beolsik Hangul (rules below) |
| 3 | Step 5 of §4 |
| 4 | Step 6 of §4 |
| 5 | Decompose each code point into keystroke code units that keep its ranges (table below) |

- Step 1 undoes step 4 of §4 for Latin letters, so a doubled key stays both a final and an initial (`whssk` → `존나`,
  not `조나`).
  - Each letter of the range is mapped through `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP`, the categories of step 2 of §4 are
    skipped (`s s`), and each letter keeps its own ranges.
  - A code point whose lightly normalized range was already expanded is skipped, because the code units of one cluster
    share that range (`ﬁ`).
- Step 2: each key of `LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP` becomes its jamo, and the jamo of adjacent Latin letters
  compose as a 2-beolsik input method composes them: an initial, a vowel, and an optional final (`tlqkf` → `시발`).
  - Two adjacent keys that `KEYSTROKE_COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP` joins form a compound vowel or
    compound final (`dnjs` → `원`), which step 5 of §4 never builds.
  - A consonant followed by a vowel, converted or typed, stays an initial instead of becoming a final (`rkqtk` → `갑사`,
    `tlqㅏㄹ` → `시ㅂㅏㄹ`).
  - A converted jamo never composes with a typed jamo or syllable in this step; steps 3 and 4 join them (`ㅅl발`,
    `시l발`, and `tlllqkf` → `시발`).
- Steps 3 and 4 change only code points next to converted jamo, because §4 already applied them to the rest. Step 4 can
  also absorb a converted jamo into an adjacent Hangul syllable (`나k` → `나`).

| Source code point | `SourceCharacterKind` | Becomes | `JamoPosition` | `syllableEnds` |
|---|---|---|---|---|
| Hangul syllable | By its lightly normalized source (below) | Its keystroke jamo | `INITIAL`, `MEDIAL`, or `FINAL` of the syllable | `true` on its last code unit only |
| Hangul Compatibility Jamo | By its lightly normalized source (below) | Its keystroke jamo | `STANDALONE` | `false` |
| Any other | `OTHER` | Kept | `STANDALONE` | `false` |

- `COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_COMPATIBILITY_JAMO_MAP` splits compound vowels and compound finals into their
  2-beolsik keystrokes (`ㅟ` → `ㅜㅣ`, `ㅄ` → `ㅂㅅ`). A tense consonant stays one code unit, because it is one shifted
  keystroke.
- The `SourceCharacterKind` of a Hangul code point is read from the lightly normalized view at the start of its lightly
  normalized range: a Latin letter, or a letter that `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP` maps to one, gives
  `LATIN_LETTER`; a Hangul Compatibility Jamo gives `STANDALONE_JAMO`; anything else gives `HANGUL_SYLLABLE`.
- A composed syllable keeps the jamo positions of the syllable (`ㅁㅏㅅㅂㅗㄷㅏ` and `tlqkf` have the positions of `맛보다`
  and `시발`).
- Identical keystroke jamo are not merged, so a final and the next initial stay apart (`존나` → `ㅈㅗㄴㄴㅏ`) and `존나`
  does not match `구조나`.

---

## 6. `extractLineInitials`

- Each line of the lightly normalized view contributes its first code point outside the general categories that
  step 2 of §4 removes, so a bullet or indent does not hide the letter (`- 시원\n- 발코니` → `시발`).
- A supplementary-plane first letter keeps both its code units, so a surrogate pair is never split.
- Each code unit keeps its lightly normalized and original ranges.
- Lines are split at the code points that `isLineTerminator` accepts.
- Fewer than 2 contributing lines yield an empty `TextNormalizedView`.
