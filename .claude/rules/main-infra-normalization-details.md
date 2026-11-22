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
- `TextNormalizationCharacterMap` — immutable code-point maps
- `TextNormalizationHelper` — stateless code-point predicates

---

## 2. Class Responsibilities

**`TextNormalizer`** — `@Component`:
- Receives the NFD, NFC, NFKD, and NFKC `Normalizer2` instances as `Icu4jConfig` beans, selected with
  `@Qualifier(*_NORMALIZER_BEAN_NAME)`, so every normalization form follows the one Unicode version of ICU4J.
- `nfdNormalize()` / `nfcNormalize()` → returns the NFD/NFC form of a `String`.
- `normalizeLightly()` → returns the lightly normalized view of the original text, the `String` that every view derives
  from, as a `LightlyNormalizedView` (§3).
- `normalizeAggressively()` → returns the aggressively normalized view of a `LightlyNormalizedView` as an
  `AggressivelyNormalizedView` (§4).
- `normalizeWithSyllable()` → returns the syllable normalized view of an `AggressivelyNormalizedView`, given with the
  `LightlyNormalizedView` it was built from, as a `SyllableNormalizedView` (§5).
- `normalizeWithKeystroke()` → returns the keystroke normalized view of an `AggressivelyNormalizedView` as a
  `KeystrokeNormalizedView` (§6).
- `normalizeWithLineInitial()` → returns the line-initial normalized view of a `LightlyNormalizedView` as a
  `LineInitialNormalizedView` (§7).
- Each step of a method takes and returns a plain `TextNormalizedView`, so a step shared by several methods (steps 2–4
  of §4 and steps 2–4 of §6) serves each of them; the method wraps the last step's view in the `view/` subclass it
  returns.

**`TextNormalizedView`** — immutable, non-`final` so that the subclasses can extend it:
- Read through `getText()`, `length()`, `getLightlyNormalizedStart` / `getLightlyNormalizedEnd`, and
  `getOriginalStart` / `getOriginalEnd`, each taking a code-unit index.
- Built through the public `TextNormalizedView.Builder` (`append` one code unit with its four range bounds, then
  `build`), which copies its arrays to the final size.

**`view/` subclasses** — `final`; each type tells which normalization method produced the view:
- `LightlyNormalizedView` (`LIGHT`), `AggressivelyNormalizedView` (`AGGRESSIVE`), `SyllableNormalizedView`
  (`SYLLABLE`), `KeystrokeNormalizedView` (`KEYSTROKE`), `LineInitialNormalizedView` (`LINE_INITIAL`).
- Each is built through its public constructor from a `TextNormalizedView`, whose protected copy constructor shares the
  text and the immutable arrays without copying them.

**`TextNormalizationCharacterMap`** — `final`, `@NoArgsConstructor(access = PRIVATE)`:
- Holds each map as a constant keyed by code point, with code-point values unless stated otherwise.
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
  Jamo on the same Dubeolsik key.
- `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_MAP`: a `String` of the two Hangul Compatibility Jamo typed
  on Dubeolsik keys to form a compound vowel or compound trailing consonant (§9), in typing order → that compound vowel
  or compound trailing consonant.

**`TextNormalizationHelper`** — `final`, `@NoArgsConstructor(access = PRIVATE)`; each predicate is `public static`
and decides a Unicode property by the return value of an ICU4J property query:
- `isDICP`: the `Default_Ignorable_Code_Point` property.
- `isCombiningMark`: the general categories `Mn`, `Mc`, and `Me`.
- `isLetter`: the general categories `L*`.
- `isLineTerminator`: the `Line_Break` values BK, CR, LF, and NL.
- `isAsciiLowerCaseLetter`: the general category `Ll` in the Basic Latin block.
- `isHangulSyllable`: the `Hangul_Syllable_Type` LV or LVT;
  `isHangulSyllableWithoutTrailingConsonant`: LV only; `isHangulSyllableWithTrailingConsonant`: LVT only.
- `isCompatibilityJamo`: a code point in the Hangul Compatibility Jamo block.

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

- Lowercasing is per code point, because `String.toLowerCase` can change the length of code point (unit).
- Step 5 follows step 4, so an uppercase confusable is mapped once lowercased (`Ѕ` → `ѕ` → `s`).
- Only step 1 removes code points, and it removes combining marks before NFKC, because NFKC would recompose a
  diacritic into its base letter (`e` + U+0301 → `é`); whitespace, punctuation, and an archaic conjoining jamo that
  `CONJOINING_COMPATIBILITY_JAMO_MAP` leaves out are kept.

---

## 4. `normalizeAggressively`

The lightly normalized view passes these steps in order:

| Order | Step |
|---|---|
| 1 | Keep only code points that `isLetter` accepts |
| 2 | Collapse consecutive identical Hangul Compatibility Jamo into one (rules below) |
| 3 | Compose each run of Hangul Compatibility Jamo into Hangul syllables (rules below) |
| 4 | Merge an elongated vowel or elongated consonant (§9) into the preceding Hangul syllable (rules below) |

- Step 2 collapses only Hangul Compatibility Jamo (`ㅋㅋㅋ` → `ㅋ`). Every other code point, such as a Hangul syllable,
  a Latin letter, or an archaic conjoining jamo, is kept however often it repeats (`시발발발`, `fuuuck` stay).
- Step 3 reads a run (§9) as a leading consonant, a vowel, and an optional trailing consonant, and a consonant jamo
  followed by a vowel jamo starts the next Hangul syllable (`ㅂㅕㅇ신` → `병신`), unless it is a compound trailing
  consonant, which cannot be a leading consonant (`ㄱㅏㄳㅣ` reads as `갃` and `ㅣ`). A run that does not divide entirely
  into Hangul syllables is kept as it is, because composing only part of it can break up a sequence of Hangul
  Compatibility Jamo (`ㅅㅂㅠ` stays, instead of becoming `ㅅ뷰`, which no longer contains `ㅅㅂ`). A compound vowel or
  compound trailing consonant composes only when it is a single Hangul Compatibility Jamo (`ㅂㅏㄺ` → `밝`): two
  adjacent Hangul Compatibility Jamo are not joined into one (`ㅂㅏㄹㄱ` stays; step 1 of §5 joins them). Step 3 runs
  after step 2, so a consonant jamo typed twice composes once (`ㅅㅣㅂㅂㅏㄹ` → `ㅅㅣㅂㅏㄹ` in step 2 → `시발` in step 3),
  and before step 4, so a Hangul syllable that step 3 composes can absorb an elongated vowel (`ㅅㅣ이발` → `시이발` in
  step 3 → `시발` in step 4).
- Step 4, elongated vowel: a Hangul syllable without a trailing consonant absorbs a following elongated vowel, taking
  the trailing consonant of an elongated vowel written as a Hangul syllable (`바알` → `발`), and keeps absorbing while
  it has no trailing consonant.
- Step 4, elongated consonant: the preceding Hangul syllable absorbs it, also when it repeats the leading consonant of
  the following one (`존ㄴ맛` → `존맛`, `시ㅂ발` → `시발`).

---

## 5. `normalizeWithSyllable`

The aggressively normalized view passes these steps in order:

| Order | Step |
|---|---|
| 1 | Compose each run of Hangul Compatibility Jamo within one token into as many Hangul syllables as possible (rules below) |
| 2 | Remove every remaining Hangul Compatibility Jamo and ASCII lowercase letter |

- A token is a sequence of consecutive code points of the lightly normalized view's text that holds no whitespace
  (ICU4J `isUWhiteSpace`) and is bounded on each side by whitespace or the start or end of the text. A code unit
  belongs to the token holding the start of its lightly normalized range. The aggressively normalized view holds no
  whitespace, so a run can span several tokens; step 1 ends a run at a token boundary and attaches no consonant jamo
  across one as a trailing consonant, so nothing composes across tokens (`ㅂ ㅠㅅㅂ` keeps `ㅂㅠㅅㅂ`, `시바 ㄹ놈` keeps
  `시바ㄹ놈`).
- Step 1 reads a run as step 3 of §4 does, but composes the part that forms Hangul syllables and keeps the rest as
  Hangul Compatibility Jamo (`ㅅㅂㅠ` → `ㅅ뷰`, `ㄱㅏㄳㅣ` → `갃ㅣ`).
  - Two adjacent Hangul Compatibility Jamo that `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_MAP` joins
    form a compound vowel or compound trailing consonant (`ㄷㅜㅔ져` → `뒈져`).
  - A consonant jamo directly after a Hangul syllable without a trailing consonant becomes the trailing consonant of
    that Hangul syllable (`시바ㄹ놈` → `시발놈`), also when another Hangul syllable follows it, so step 2 does not
    remove it (`시ㅈ발` → `싲발`). Two such consonant jamo join when they form a compound trailing consonant, and a
    consonant jamo followed by a vowel jamo starts the next Hangul syllable instead, unless it is a compound trailing
    consonant (`바ㄹㄱ` → `밝`, `시바ㄹㅏ` → `시바라`, `가ㄳㅣ` → `갃ㅣ`).
- Step 2 removes these code points wherever they are (`시ㅠ발`, `시l발`, `시 ㅋ 발`, `시 ok 발` → `시발`). Every other
  code point is kept.

---

## 6. `normalizeWithKeystroke`

The aggressively normalized view passes these steps in order:

| Order | Step |
|---|---|
| 1 | Convert ASCII lowercase letters to Dubeolsik Hangul (rules below) |
| 2–4 | Apply steps 2–4 of §4 again, with the same rules |

- The aggressively normalized view keeps repeated ASCII lowercase letters (step 2 of §4), so a key typed twice stays
  both a trailing consonant and the next leading consonant (`whssk` → `존나`).
- Step 1: each ASCII lowercase letter becomes the Hangul Compatibility Jamo on its key through
  `ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP`, and the converted jamo (§9) of adjacent ASCII lowercase letters
  compose into Hangul syllables as step 3 of §4 reads a run; a converted jamo that joins no Hangul syllable stays a
  Hangul Compatibility Jamo (`tlqkf` → `시발`).
  - Two adjacent ASCII lowercase letters whose converted jamo
    `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_MAP` joins form a compound vowel or compound trailing
    consonant (`dnjs` → `원`).
  - A converted jamo never composes with a typed jamo (§9) or a Hangul syllable of the aggressively normalized view
    (`ㅅl발` → `ㅅㅣ발`, `바f` → `바ㄹ`).
  - A converted consonant jamo followed by a typed vowel jamo stays a Hangul Compatibility Jamo instead of becoming the
    trailing consonant of the preceding Hangul syllable (`tlqㅏㄹ` → `시ㅂㅏㄹ`).
- Steps 2–4 run after step 1, because the repeated jamo, the runs of Hangul Compatibility Jamo, and the elongated
  vowels and consonants typed on an English layout appear only once step 1 converts them (`tlllqkf` → `시ㅣㅣ발` →
  `시ㅣ발` → `시발`); step 3 also composes a converted jamo with a typed jamo (`ㅅl발` → `ㅅㅣ발` → `시발`).

---

## 7. `normalizeWithLineInitial`

- Each contributing line (§9) contributes the first code point that makes it one, so a bullet or indent does not hide
  the letter (`- 시원\n- 발코니` → `시발`).
- Lines are split at the code points that `isLineTerminator` accepts.
- Fewer than 2 contributing lines yield an empty `LineInitialNormalizedView`.

---

## 8. Source Ranges

`TextNormalizedView` holds four parallel `int[]` arrays, whose elements at index `i` give the code unit at index `i` of
the text two ranges `[start, end)` of code-unit indexes:
- Lightly normalized range (`lightlyNormalizedStarts` / `lightlyNormalizedEnds`): the code units of the lightly
  normalized view's text that the code unit derives from.
- Original range (`originalStarts` / `originalEnds`): the code units of the original text that the code unit derives
  from.

**`normalizeLightly`** (§3):
- Every code unit an extended grapheme cluster produces gets, as its lightly normalized range, the range of all code
  units the extended grapheme cluster produces in the lightly normalized view's text (`ﬁ` → `fi`: both code units
  `[0, 2)`), and, as its original range, the whole extended grapheme cluster in the original text (`𝐬` → `s`:
  `[0, 2)`).

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
- **Contributing line**: a line of the lightly normalized view's text that holds a code point that `isLetter` accepts.
- **Converted jamo**: a Hangul Compatibility Jamo that step 1 of §6 converts from an ASCII lowercase letter.
- **Elongated consonant**: a consonant jamo written between two Hangul syllables that repeats the trailing consonant
  of the preceding one or the leading consonant of the following one (`존ㄴ맛`, `시ㅂ발`).
- **Elongated vowel**: a vowel written after a Hangul syllable to drag out its sound, as a vowel jamo identical to its
  vowel or as a Hangul syllable with the leading consonant `ㅇ` and the same vowel (`시ㅣ발`, `바알`).
- **Run**: a maximal sequence of consecutive Hangul Compatibility Jamo.
- **Typed jamo**: a Hangul Compatibility Jamo that the aggressively normalized view already holds, as opposed to a
  converted jamo.
