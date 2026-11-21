---
paths:
  - "src/main/java/kr/modusplant/infrastructure/normalization/**"
---

# Normalization Infrastructure Conventions

## 1. Package Structure

- `TextNormalizer` — Spring bean; builds the NFD/NFC forms and the five normalized views
- `TextNormalizedView` (`view/supers/`) — a normalized text and, per code unit, its source ranges (§8); base class of
  the `view/` subclasses, one per `TextNormalizationType` constant
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
- `normalizeWithSyllable()` → returns the syllable normalized view of an `AggressivelyNormalizedView`, given with the
  `LightlyNormalizedView` it was built from, as a `SyllableNormalizedView` (§5).
- `normalizeWithKeystroke()` → returns the keystroke normalized view of an `AggressivelyNormalizedView` as a
  `KeystrokeNormalizedView` (§6).
- `normalizeWithLineInitial()` → returns the line-initial normalized view of a `LightlyNormalizedView` as a
  `LineInitialNormalizedView` (§7).
- Each step of a method builds a plain `TextNormalizedView`, and the method wraps the last step's view in the `view/`
  subclass it returns, so a step shared by several methods (steps 2–4 of §4 and steps 2–4 of §6) serves each of them.

**`TextNormalizedView`** — immutable, non-`final` so that the subclasses can extend it:
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

**`TextNormalizationCharacterMap`** — `final`, `@NoArgsConstructor(access = PRIVATE)`:
- Holds each map as a `public static final Map` constant keyed by code point, with code-point values unless stated
  otherwise.
- Is static, so it takes its `Normalizer2` from `Normalizer2` directly instead of an `Icu4jConfig` bean.
- `CONJOINING_COMPATIBILITY_JAMO_MAP`: every conjoining jamo of the `Hangul_Syllable_Type` L, V, or T → the Hangul
  Compatibility Jamo whose NFKD decomposition is a conjoining jamo with the same Unicode character name once the
  `HANGUL CHOSEONG `, `HANGUL JUNGSEONG `, or `HANGUL JONGSEONG ` prefix is removed from both names (`ᆨ`
  `HANGUL JONGSEONG KIYEOK` → `ㄱ`, whose NFKD decomposition is `HANGUL CHOSEONG KIYEOK`); a conjoining jamo without
  such a Hangul Compatibility Jamo (an archaic jamo, a filler) is left out.
- `SYMBOL_CONFUSABLE_LATIN_MAP`: symbols without a compatibility decomposition that are confusable with Latin letters
  (the regional indicator symbols `🇦`–`🇿` and the negative circled and negative squared Latin capital letters)
  → ASCII lowercase letters.
- `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP`: lowercase Cyrillic and Greek letters confusable with Latin letters (`ѕ`, `і`)
  → ASCII lowercase letters.
- `COMPATIBILITY_CONJOINING_LEADING_CONSONANT_MAP`, `COMPATIBILITY_CONJOINING_VOWEL_MAP`,
  `COMPATIBILITY_CONJOINING_TRAILING_CONSONANT_MAP`: Hangul Compatibility Jamo → the conjoining jamo of the leading
  consonant, the vowel, or the trailing consonant, respectively; only a Hangul Compatibility Jamo that can fill that
  position of a Hangul syllable is included, so an archaic jamo is left out.
- `ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP`: ASCII lowercase letters (`a`–`z`) → the Hangul Compatibility
  Jamo on the same Dubeolsik (KS X 5002) key.
- `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP`: a `String` of the two Hangul Compatibility Jamo typed on
  Dubeolsik keys to form a compound vowel or compound trailing consonant (§9), in typing order → that compound vowel
  or compound trailing consonant.

**`TextNormalizationHelper`** — `final`, `@NoArgsConstructor(access = PRIVATE)`; each predicate is `public static`
and decides a Unicode property by the return value of an ICU4J property query (`UCharacter.hasBinaryProperty` for
`Default_Ignorable_Code_Point`, `UCharacter.getType` for the general categories):
- `isDICP`: the `Default_Ignorable_Code_Point` property.
- `isCombiningMark`: the general categories `Mn`, `Mc`, and `Me`.
- `isNonLetterNoiseCategory`: the general categories `Z*`, `Cc`, `P*`, `S*`, and `N*`.
- `isLineTerminator`: the `Line_Break` values BK, CR, LF, and NL.
- `isAsciiLowerCaseLetter`: the general category `Ll` in the Basic Latin block.
- `isHangulSyllable`: the `Hangul_Syllable_Type` LV or LVT;
  `isHangulSyllableWithoutTrailingConsonant`: LV only; `isHangulSyllableWithTrailingConsonant`: LVT only.
- `isCompatibilityJamo`: a code point in the Hangul Compatibility Jamo block.
- `isConjoiningJamo`: the `Hangul_Syllable_Type` L, V, or T.

---

## 3. `normalizeLightly`

The original text is segmented into extended grapheme clusters with ICU4J `BreakIterator.getCharacterInstance()`. Each
extended grapheme cluster passes these steps in order:

| Order | Step |
|---|---|
| 1 | NFKD, remove code points that `isDICP` or `isCombiningMark` accepts, NFKC |
| 2 | Map conjoining jamo through `CONJOINING_COMPATIBILITY_JAMO_MAP` |
| 3 | Map code points through `SYMBOL_CONFUSABLE_LATIN_MAP` |
| 4 | Lowercase per code point with `UCharacter.toLowerCase` |
| 5 | Map code points through `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP` |

- Lowercasing is per code point, because `String.toLowerCase` can change the length and break the per-code-unit
  ranges.
- Step 5 follows step 4, so an uppercase confusable is mapped once lowercased (`Ѕ` → `ѕ` → `s`).
- Only step 1 removes code points, and it removes combining marks before NFKC, because NFKC would recompose a
  diacritic into its base letter (`é` → `e`); whitespace, punctuation, and an archaic conjoining jamo that
  `CONJOINING_COMPATIBILITY_JAMO_MAP` leaves out are kept.

---

## 4. `normalizeAggressively`

The lightly normalized view passes these steps in order:

| Order | Step |
|---|---|
| 1 | Remove code points that `isNonLetterNoiseCategory` accepts |
| 2 | Collapse consecutive identical jamo into one (rules below) |
| 3 | Compose each run of Hangul Compatibility Jamo into Hangul syllables (rules below) |
| 4 | Merge an elongated vowel or elongated consonant (§9) into the preceding Hangul syllable (rules below) |

- After step 1, only letters (general categories `L*`) remain, along with the rare `Cf`, `Co`, `Cs`, and `Cn` code
  points.
- Step 2 collapses only consecutive identical code points that are Hangul Compatibility Jamo (`isCompatibilityJamo`)
  or conjoining jamo (`isConjoiningJamo`), including Hangul Compatibility Jamo that step 3 later composes into a
  Hangul syllable (`ㅋㅋㅋ` → `ㅋ`, `ㅅㅣㅂㅂㅏㄹ` → `ㅅㅣㅂㅏㄹ` → `시발`). Every other code point, such as a Hangul
  syllable or a Latin letter, is kept however often it repeats (`시발발발`, `fuuuck` stay).
- Step 3 reads a run (§9) left to right as a leading consonant, a vowel, and an optional trailing consonant, and a
  consonant jamo followed by a vowel jamo starts the next Hangul syllable (`ㅂㅕㅇ신` → `병신`). A run that does not
  divide entirely into Hangul syllables is kept as it is, because composing only part of it can break up a sequence
  of Hangul Compatibility Jamo (`ㅅㅂㅠ` stays, instead of becoming `ㅅ뷰`, which no longer contains `ㅅㅂ`). A
  compound vowel or compound trailing consonant composes only when it is a single Hangul Compatibility Jamo
  (`ㅂㅏㄺ` → `밝`): two adjacent Hangul Compatibility Jamo are not joined into one (`ㅂㅏㄹㄱ` stays; step 1 of §5
  joins them), and a compound trailing consonant followed by a vowel jamo is not split into a trailing consonant and
  the next leading consonant (`ㄱㅏㄳㅣ` stays instead of becoming `각시`). Step 3 runs before step 4, so a Hangul
  syllable that step 3 composes can absorb an elongated vowel (`ㅅㅣ이발` → `시이발` in step 3 → `시발` in step 4).
- Step 4, elongated vowel: a Hangul syllable without a trailing consonant absorbs a following elongated vowel, taking
  the trailing consonant of an elongated vowel written as a Hangul syllable (`바알` → `발`), and keeps absorbing while
  it has no trailing consonant.
- Step 4, elongated consonant: the preceding Hangul syllable absorbs it (`존ㄴ맛` → `존맛`, `시ㅂ발` → `시발`).

---

## 5. `normalizeWithSyllable`

The aggressively normalized view passes these steps in order:

| Order | Step |
|---|---|
| 1 | Compose each run of Hangul Compatibility Jamo within one token into as many Hangul syllables as possible (rules below) |
| 2 | Remove every remaining Hangul Compatibility Jamo and ASCII lowercase letter |

- A token is a maximal sequence of consecutive code points of the lightly normalized view's text that are not
  whitespace (ICU4J `isUWhiteSpace`). A code unit belongs to the token holding the start of its lightly normalized
  range, so Hangul Compatibility Jamo written in different tokens never compose (`ㅅ ㅣ` stays apart).
- Step 1 reads a run as step 3 of §4 does, but composes the part that forms Hangul syllables and keeps the rest as
  Hangul Compatibility Jamo (`ㅅㅂㅠ` → `ㅅ뷰`).
  - Two adjacent Hangul Compatibility Jamo that `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP` joins form a
    compound vowel or compound trailing consonant (`ㄷㅜㅔ져` → `뒈져`).
  - A consonant jamo directly after a Hangul syllable without a trailing consonant becomes the trailing consonant of
    that Hangul syllable, because a trailing consonant written apart from its Hangul syllable only hides the word
    (`시바ㄹ놈` → `시발놈`), even when another Hangul syllable follows the consonant jamo (`시ㅈ발` → `싲발`). Two such
    consonant jamo join when they form a compound trailing consonant, and a consonant jamo followed by a vowel jamo
    starts the next Hangul syllable instead (`바ㄹㄱ` → `밝`, `시바ㄹㅏ` → `시바라`).
- Step 2 removes these code points wherever they are, including in a token with no Hangul syllable, because a Hangul
  Compatibility Jamo or ASCII lowercase letter left between Hangul syllables only hides the word (`시ㅠ발`, `시l발`,
  `시 ㅋ 발`, `시 ok 발` → `시발`). Every other code point is kept.

---

## 6. `normalizeWithKeystroke`

The aggressively normalized view passes these steps in order:

| Order | Step |
|---|---|
| 1 | Convert ASCII lowercase letters to Dubeolsik Hangul (rules below) |
| 2–4 | Apply steps 2–4 of §4 again, with the same rules |

- The aggressively normalized view keeps repeated ASCII lowercase letters (step 2 of §4), so a key typed twice stays
  both a trailing consonant and the next leading consonant (`whssk` → `존나`, not `조나`).
- Step 1: each ASCII lowercase letter becomes the Hangul Compatibility Jamo on its key through
  `ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP`, and the converted jamo (§9) of adjacent ASCII lowercase letters
  compose into Hangul syllables as step 3 of §4 reads a run; a converted jamo that joins no Hangul syllable stays a
  Hangul Compatibility Jamo (`tlqkf` → `시발`).
  - Two adjacent ASCII lowercase letters whose converted jamo `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_MAP`
    joins form a compound vowel or compound trailing consonant (`dnjs` → `원`).
  - A consonant jamo followed by a vowel jamo, whether converted jamo or typed jamo (§9), stays a leading consonant
    instead of becoming a trailing consonant (`rkqtk` → `갑사`; `tlqㅏㄹ` → `시ㅂㅏㄹ`).
  - A converted jamo never composes with a typed jamo or a Hangul syllable of the aggressively normalized view
    (`ㅅl발` → `ㅅㅣ발`).
- Steps 2–4 run after step 1, because the repeated jamo, the runs of Hangul Compatibility Jamo, and the elongated
  vowels and consonants typed on an English layout appear only once step 1 converts them (`tlllqkf` → `시ㅣㅣ발` →
  `시ㅣ발` → `시발`); step 3 also composes a converted jamo with a typed jamo (`ㅅl발` → `ㅅㅣ발` → `시발`).

---

## 7. `normalizeWithLineInitial`

- Each contributing line (§9) contributes the first code point that makes it one, so a bullet or indent does not hide
  the letter (`- 시원\n- 발코니` → `시발`).
- A contributed code point in a supplementary plane keeps both its code units, so a surrogate pair is never split.
- Lines are split at the code points that `isLineTerminator` accepts.
- Fewer than 2 contributing lines yield an empty `LineInitialNormalizedView`.

---

## 8. Source Ranges

`TextNormalizedView` holds four parallel `int[]` arrays, `lightlyNormalizedStarts` / `lightlyNormalizedEnds` and
`originalStarts` / `originalEnds`, whose element at index `i` describes the code unit at index `i` of the text: the
bounds of the range `[start, end)` of code-unit indexes that code unit derives from in the lightly normalized view's
text and in the original text, the `String` that `normalizeLightly` takes and every view derives from. These are the
code unit's lightly normalized range and original range.

**`normalizeLightly`** (§3):
- Every code unit an extended grapheme cluster produces gets, as its lightly normalized range, the range of all code
  units the extended grapheme cluster produces in the lightly normalized view's text (`ﬁ` → `fi`: both code units
  `[0, 2)`), and, as its original range, the whole extended grapheme cluster in the original text, so a surrogate pair
  is never split (`𝐬` → `s`: `[0, 2)`).

**Every other method** (§4–§7):
- In each step, a code unit built from several source code units (collapsed, composed, merged, or given a trailing
  consonant) takes the union of their lightly normalized ranges and the union of their original ranges (`ㅅㅣ` → `시`,
  `tl` → `시`: lightly normalized `[0, 2)`).
- Every other code unit keeps both its ranges (`시 발` → `시발`: lightly normalized `[0, 1)` and `[2, 3)`).

---

## 9. Glossary

- **Compound vowel**: a vowel jamo typed on Dubeolsik as two vowel keys (`ㅘ`, `ㅙ`, `ㅚ`, `ㅝ`, `ㅞ`, `ㅟ`, `ㅢ`).
- **Compound trailing consonant**: a consonant jamo of two different consonants that only a trailing consonant takes
  (`ㄳ`, `ㄵ`, `ㄶ`, `ㄺ`, `ㄻ`, `ㄼ`, `ㄽ`, `ㄾ`, `ㄿ`, `ㅀ`, `ㅄ`).
- **Contributing line**: a line of the lightly normalized view's text that holds a code point outside the general
  categories that step 1 of §4 removes.
- **Converted jamo**: a Hangul Compatibility Jamo that step 1 of §6 converts from an ASCII lowercase letter.
- **Elongated consonant**: a consonant jamo written between two Hangul syllables that repeats the trailing consonant
  of the preceding one or the leading consonant of the following one (`존ㄴ맛`, `시ㅂ발`).
- **Elongated vowel**: a vowel written after a Hangul syllable to drag out its sound, as a vowel jamo identical to its
  vowel or as a Hangul syllable with the leading consonant `ㅇ` and the same vowel (`시ㅣ발`, `바알`).
- **Run**: a maximal sequence of consecutive Hangul Compatibility Jamo.
- **Typed jamo**: a Hangul Compatibility Jamo that the aggressively normalized view already holds, as opposed to a
  converted jamo.
