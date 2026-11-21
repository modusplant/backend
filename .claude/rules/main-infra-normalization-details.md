---
paths:
  - "src/main/java/kr/modusplant/infrastructure/normalization/**"
---

# Normalization Infrastructure Conventions

## 1. Package Structure

- `TextNormalizer` — Spring bean; builds the NFD/NFC forms and the lightly normalized, aggressively normalized,
  syllable, keystroke, and line-initial views
- `TextNormalizedView` (`view/supers/`) — a normalized text and, per code unit, its source ranges; base class of the
  `view/` subclasses, one per `TextNormalizationType` constant
- `TextNormalizationType` — the normalization method that produced a view
- `TextNormalizationCharacterMap` — immutable code-point maps that `TextNormalizer` reads
- `TextNormalizationHelper` — stateless code-point predicates that `TextNormalizer` calls

---

## 2. Class Responsibilities

**`TextNormalizer`** — `@Component`:
- Receives the NFD, NFC, NFKD, and NFKC `Normalizer2` instances as `Icu4jConfig` beans, selected with
  `@Qualifier(*_NORMALIZER_BEAN_NAME)`, so every normalization form follows the one Unicode version of ICU4J.
- `nfdNormalize()` / `nfcNormalize()` → returns the NFD/NFC form of a `String`.
- `normalizeLightly()` → returns the lightly normalized view of an original text as a `LightlyNormalizedView` (§3).
- `normalizeAggressively()` → returns the aggressively normalized view of a `LightlyNormalizedView` as an
  `AggressivelyNormalizedView` (§4).
- `normalizeWithSyllable()` → returns the syllable view of an `AggressivelyNormalizedView` and its
  `LightlyNormalizedView` as a `SyllableNormalizedView` (§5).
- `normalizeWithKeystroke()` → returns the keystroke view of an `AggressivelyNormalizedView` as a
  `KeystrokeNormalizedView` (§6).
- `normalizeWithLineInitial()` → returns the line-initial view of a `LightlyNormalizedView` as a
  `LineInitialNormalizedView` (§7).
- Each step of a method builds a plain `TextNormalizedView`, and the method wraps the last step's view in its subclass,
  so a step shared by several methods (steps 2–4 of §4 and steps 2–4 of §6) serves each of them.

**`TextNormalizedView`** — immutable, non-`final` so that the subclasses can extend it:
- Holds its text, the normalized `String` that `getText()` returns, and the four parallel `int[]` range arrays of §8.
- Read through `getText()`, `length()`, `getLightlyNormalizedStart` / `getLightlyNormalizedEnd`, and
  `getOriginalStart` / `getOriginalEnd`, each taking a code-unit index.
- Built through the public `TextNormalizedView.Builder` (`append` one code unit with its four range bounds, then
  `build`), which copies its arrays to the final size.

**`view/` subclasses** — `final`; each type tells which normalization method produced the view, so a method parameter
accepts only the view it needs:
- `LightlyNormalizedView` (`LIGHT`), `AggressivelyNormalizedView` (`AGGRESSIVE`), `SyllableNormalizedView`
  (`SYLLABLE`), `KeystrokeNormalizedView` (`KEYSTROKE`), `LineInitialNormalizedView` (`LINE_INITIAL`).
- Each is built through its public constructor from a `TextNormalizedView`, whose protected copy constructor shares the
  text and the immutable arrays without copying them.

**`TextNormalizationType`** — enum: `LIGHT` (`normalizeLightly`),
`AGGRESSIVE` (`normalizeAggressively`), `SYLLABLE` (`normalizeWithSyllable`), `KEYSTROKE` (`normalizeWithKeystroke`),
`LINE_INITIAL` (`normalizeWithLineInitial`).

**`TextNormalizationCharacterMap`** — `final`, `@NoArgsConstructor(access = PRIVATE)`:
- Holds each map as a `public static final Map` constant keyed by code point, with code-point values unless stated
  otherwise.
- Is static, so it takes its `Normalizer2` from `Normalizer2` directly instead of an `Icu4jConfig` bean.
- `CONJOINING_COMPATIBILITY_JAMO_MAP`: every conjoining jamo whose `Hangul_Syllable_Type` is L, V, or T → the Hangul
  Compatibility Jamo whose NFKD decomposition is a conjoining jamo of the same name once the
  `HANGUL CHOSEONG/JUNGSEONG/JONGSEONG` prefix is removed from both names; a conjoining jamo without such a 
  compatibility jamo (an archaic jamo, a filler) is left out.
- `LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP`: the regional indicator symbols `🇦`–`🇿` and the negative circled and
  negative squared Latin capital letters, which have no compatibility decomposition → ASCII lowercase letters.
- `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP`: lowercase Cyrillic and Greek letters confusable with Latin letters (`ѕ`, `і`)
  → ASCII lowercase letters.
- `COMPATIBILITY_CONJOINING_CHOSEONG_MAP`, `COMPATIBILITY_CONJOINING_JUNGSEONG_MAP`,
  `COMPATIBILITY_CONJOINING_JONGSEONG_MAP`: Hangul Compatibility Jamo → the conjoining jamo of that syllable position;
  only a jamo that composes into a precomposed syllable is included, so an archaic jamo is left out.
- `ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP`: ASCII lowercase letters (`a`–`z`) → the Hangul Compatibility
  Jamo on the same Dubeolsik (KS X 5002) key.
- `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP`: a `String` of two Dubeolsik keystroke jamo in typing
  order → the compound vowel or compound final Hangul Compatibility Jamo they form.

**`TextNormalizationHelper`** — `final`, `@NoArgsConstructor(access = PRIVATE)`; each predicate is `public static`:
- `isDICP`: the `Default_Ignorable_Code_Point` property.
- `isCombiningMark`: the general categories `Mn`, `Mc`, and `Me`.
- `isNonLetterNoiseCategory`: the general categories `Z*`, `Cc`, `P*`, `S*`, and `N*`.
- `isLineTerminator`: the `Line_Break` values BK, CR, LF, and NL.
- `isAsciiLowerCaseLetter`: the general category `Ll` in the Basic Latin block.
- `isHangulSyllable`: the `Hangul_Syllable_Type` LV or LVT;
  `isHangulSyllableWithoutFinal`: LV only; `isHangulSyllableWithFinal`: LVT only.
- `isCompatibilityJamo`: a code point in the Hangul Compatibility Jamo block.
- `isConjoiningJamo`: the `Hangul_Syllable_Type` L, V, or T.

---

## 3. `normalizeLightly`

The original text is segmented into extended grapheme clusters with ICU4J `BreakIterator.getCharacterInstance()`. Each
cluster passes these steps in order:

| Order | Step |
|---|---|
| 1 | NFKD, remove code points with the `Default_Ignorable_Code_Point` property and combining marks (general categories `Mn`, `Mc`, `Me`), NFKC |
| 2 | Map conjoining jamo through `CONJOINING_COMPATIBILITY_JAMO_MAP` |
| 3 | Map code points through `LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP` |
| 4 | Lowercase per code point with `UCharacter.toLowerCase` |
| 5 | Map code points through `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP` |

- Whether a code point belongs to a character class defined by a Unicode property is decided by the return value of an
  ICU4J property query (`UCharacter.hasBinaryProperty` for `Default_Ignorable_Code_Point`, `UCharacter.getType` for the
  general categories).
- Lowercasing is per code point, because `String.toLowerCase` can change the length and break the per-code-unit
  ranges.
- Step 5 follows step 4, because `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP` is keyed by lowercase letters, so an uppercase
  confusable is mapped once lowercased (`Ѕ` → `ѕ` → `s`).
- Only step 1 removes code points, and it removes combining marks before NFKC, because NFKC would recompose a
  diacritic into its base letter (`é` → `e`); whitespace, punctuation, and an archaic jamo without a compatibility form
  are kept.
- A cluster made up only of default-ignorable code points and combining marks produces no code unit, so an
  original text of such clusters yields an empty `TextNormalizedView`.

---

## 4. `normalizeAggressively`

The lightly normalized view passes these steps in order, each building a new `TextNormalizedView`:

| Order | Step |
|---|---|
| 1 | Remove code points of the general categories `Z*`, `Cc`, `P*`, `S*`, and `N*` |
| 2 | Collapse consecutive identical jamo into one (rules below) |
| 3 | Compose each run of Hangul Compatibility Jamo into syllables |
| 4 | Merge an elongated vowel or consonant into its syllable (rules below) |

- After step 1, only letters (general categories `L*`) remain, along with the rare `Cf`, `Co`, `Cs`, and `Cn` code
  points.
- Step 2 collapses only a jamo that is standalone at that step, a Hangul Compatibility Jamo (`isCompatibilityJamo`) or
  a conjoining jamo (`isConjoiningJamo`), including one that step 3 later composes into a syllable (`ㅋㅋㅋ` → `ㅋ`,
  `ㅅㅣㅂㅂㅏㄹ` → `ㅅㅣㅂㅏㄹ` → `시발`). Every other code point, such as a Hangul syllable or a Latin letter, is kept
  however often it repeats (`시발발발`, `fuuuck` stay).
- Step 3: a run (maximally successive Hangul Compatibility Jamo) is read left to right as an initial, a vowel, and an
  optional final, and a consonant followed by a vowel starts the next syllable (`ㅂㅕㅇ신` → `병신`). A run that does not
  divide entirely into syllables is kept as it is, because composing only part of it can break up a jamo sequence
  (`ㅅㅂㅠ` stays, instead of becoming `ㅅ뷰`, which no longer contains `ㅅㅂ`). A compound vowel or compound final
  composes only when it is a single jamo (`ㅂㅏㄺ` → `밝`): two adjacent jamo are not joined (`ㅂㅏㄹㄱ` stays; step 1 of 
  §5 joins them), and a compound final followed by a vowel is not split into a final and the next initial (`ㄱㅏㄳㅣ` 
  stays instead of becoming `각시`). Step 3 runs before step 4, so a composed syllable can absorb an elongated vowel
  (`ㅅㅣ이발` → `시이발` in step 3 → `시발` in step 4).
- Step 4, vowel: a Hangul syllable without a final absorbs a following Hangul Compatibility Jamo of the same vowel, or a
  following syllable with the initial `ㅇ` and the same vowel, taking that syllable's final (`바알` → `발`). The merged
  syllable keeps absorbing while it has no final.
- Step 4, consonant: a Hangul Compatibility Jamo between two Hangul syllables is absorbed into the preceding syllable
  when it equals that syllable's final or the following syllable's initial (`존ㄴ맛` → `존맛`, `시ㅂ발` → `시발`).

---

## 5. `normalizeWithSyllable`

The aggressively normalized view passes these steps in order, each building a new `TextNormalizedView`:

| Order | Step |
|---|---|
| 1 | Compose each run of Hangul Compatibility Jamo within one token into as many syllables as possible (rules below) |
| 2 | Remove every remaining Hangul Compatibility Jamo and ASCII lowercase letter |

- A token is a maximal run of lightly normalized code points other than whitespace (ICU4J `isUWhiteSpace`); a code unit
  belongs to the token holding the start of its lightly normalized range, so jamo typed in different tokens never
  compose (`ㅅ ㅣ` stays apart).
- Step 1 reads a run left to right as an initial, a vowel, and an optional final, and a consonant followed by a vowel
  starts the next syllable, like step 3 of §4, but composes the part that forms syllables and keeps the rest as jamo
  (`ㅅㅂㅠ` → `ㅅ뷰`).
  - Two adjacent jamo that `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP` joins form a compound vowel
    or compound final (`ㄷㅜㅔ져` → `뒈져`).
  - A consonant jamo directly after a Hangul syllable without a final becomes that syllable's final, because a final
    typed apart from its syllable only hides the word (`시바ㄹ놈` → `시발놈`); an inserted consonant becomes a final
    too (`시ㅈ발` → `싲발`). Two such consonants join when they form a compound final, and a consonant followed by a
    vowel jamo starts the next syllable instead (`바ㄹㄱ` → `밝`, `시바ㄹㅏ` → `시바라`).
- Step 2 removes code points wherever they are, including a token with no Hangul syllable, because a jamo or Latin
  letter left between syllables only hides the word (`시ㅠ발`, `시l발`, `시 ㅋ 발`, `시 ok 발` → `시발`). Every other code
  point is kept.

---

## 6. `normalizeWithKeystroke`

The aggressively normalized view passes these steps in order, each building a new `TextNormalizedView`:

| Order | Step |
|---|---|
| 1 | Convert ASCII lowercase letters to Dubeolsik Hangul (rules below) |
| 2–4 | Apply steps 2–4 of §4 again, with the same rules |

- Step 2 of §4 keeps repeated ASCII lowercase letters, so a key typed twice stays both a final and an initial
  (`whssk` → `존나`, not `조나`).
- Step 1: each ASCII lowercase letter becomes the Hangul Compatibility Jamo on its key through
  `ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP`, and the Hangul Compatibility Jamo converted from adjacent ASCII
  lowercase letters compose into Hangul syllables, each read left to right as an initial, a vowel, and an optional
  final; a converted jamo that joins no syllable stays a Hangul Compatibility Jamo (`tlqkf` → `시발`).
  - Two adjacent ASCII lowercase letters whose jamo `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP` joins
    form a compound vowel or compound final (`dnjs` → `원`).
  - A consonant followed by a vowel, converted or typed, stays an initial instead of becoming a final (`rkqtk` → `갑사`;
    `tlqㅏㄹ` → `시ㅂㅏㄹ`).
  - A converted jamo never composes with a typed jamo or syllable (`ㅅl발` → `ㅅㅣ발`).
- Steps 2–4 run after step 1, because only the converted Hangul shows the repeats, jamo runs, and elongations typed on
  an English layout (`tlllqkf` → `시ㅣㅣ발` → `시ㅣ발` → `시발`); step 3 also composes a converted jamo with a typed
  jamo (`ㅅl발` → `ㅅㅣ발` → `시발`). A syllable converted from a repeated syllable is kept as step 2 of §4 keeps it
  (`tltl` → `시시`).

---

## 7. `normalizeWithLineInitial`

- Each line of the lightly normalized view contributes its first code point outside the general categories that
  step 1 of §4 removes, so a bullet or indent does not hide the letter (`- 시원\n- 발코니` → `시발`).
- A supplementary-plane first letter keeps both its code units, so a surrogate pair is never split.
- Lines are split at the code points that `isLineTerminator` accepts.
- Fewer than 2 contributing lines yield an empty `TextNormalizedView`.

---

## 8. Source Ranges

`TextNormalizedView` holds four parallel `int[]` arrays, `lightlyNormalizedStarts` / `lightlyNormalizedEnds` and
`originalStarts` / `originalEnds`, whose element at index `i` describes the code unit at index `i` of the text: the
bounds of the range `[start, end)` of code-unit indexes that code unit derives from in the lightly normalized view and
in the original text, the `String` that `normalizeLightly` takes and every view derives from.

**`normalizeLightly`** (§3):
- Every code unit a cluster produces gets, as its lightly normalized range, the range of all code units the
  cluster produces in the lightly normalized view (`ﬁ` → `fi`: both code units `[0, 2)`), and, as its original range,
  the whole cluster in the original text, so a surrogate pair is never split (`𝐬` → `s`: `[0, 2)`).

**`normalizeAggressively`** (§4):
- Steps 2–4: a code unit built from several source code units (collapsed, composed, or merged) takes the union of
  their lightly normalized ranges and the union of their original ranges (`ㅅㅣ` → `시`: lightly normalized `[0, 2)`).
- Steps 1–4: every other code unit keeps both its ranges (`시 발` → `시발`: lightly normalized `[0, 1)` and `[2, 3)`).

**`normalizeWithSyllable`** (§5):
- Step 1: a composed syllable, or a syllable that takes a final, takes the union of the ranges of its jamo; every other
  code unit keeps both its ranges.
- Step 2: every kept code unit keeps both its ranges.

**`normalizeWithKeystroke`** (§6):
- Step 1: a syllable composed from several ASCII lowercase letters takes the union of their ranges (`tl` → `시`:
  lightly normalized `[0, 2)`); every other code unit keeps both its ranges.
- Steps 2–4: the rules of steps 2–4 of `normalizeAggressively` apply.

**`normalizeWithLineInitial`** (§7):
- Each code unit keeps its lightly normalized and original ranges.
