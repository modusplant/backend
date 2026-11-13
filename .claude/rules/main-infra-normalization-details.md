---
paths:
  - "src/main/java/kr/modusplant/infrastructure/normalization/**"
---

# Normalization Infrastructure Conventions

Applies to `kr.modusplant.infrastructure.normalization`.

---

## 1. Package Structure

The package is flat: six classes, no sub-packages.

```
normalization/
├ TextNormalizer # Spring bean; builds the NFD/NFC forms and the lightly normalized, aggressively normalized,
│ # keystroke, and line-initial views
├ TextNormalizedView # A normalized text and, per code unit, its source ranges
├ TextNormalizedKeystrokeView # The keystroke view: a TextNormalizedView and, per code unit, its source kind,
│ # first and last source jamo positions, and syllable end
├ TextNormalizationType # The normalization method that produced a view
├ TextNormalizationCharacterMap # Immutable code-point maps that TextNormalizer reads
└ TextNormalizationHelper # Stateless code-point predicates that TextNormalizer calls
```

---

## 2. Class Responsibilities

**`TextNormalizer`** — `@Component`:
- Receives the NFD, NFC, NFKD, and NFKC `Normalizer2` instances as `Icu4jConfig` beans, selected with
  `@Qualifier(*_NORMALIZER_BEAN_NAME)`, so every normalization form follows the one Unicode version of ICU4J.
- `nfdNormalize()` / `nfcNormalize()` → returns the NFD/NFC form of a `String`.
- `normalizeLightly()` → returns the lightly normalized view of an original text as a `TextNormalizedView` (§3).
- `normalizeAggressively()` → returns the aggressively normalized view of a lightly normalized view as a
  `TextNormalizedView` (§4).
- `normalizeWithKeystroke()` → returns the keystroke view of an aggressively normalized view and its lightly
  normalized view as a `TextNormalizedKeystrokeView` (§5).
- `normalizeWithLineInitial()` → returns the line-initial view of a lightly normalized view as a
  `TextNormalizedView` (§6).

**`TextNormalizedView`** — `final`, immutable:
- Holds its text, the normalized `String` that `getText()` returns, and four parallel `int[]` arrays whose element at
  index `i` describes the code unit at index `i` of the text: the bounds of the range `[start, end)` of code-unit
  indexes that code unit derives from in the lightly normalized view and in the original text, the `String` that
  `normalizeLightly` takes and every view derives from.
- Read through `getText()`, `length()`, `getLightlyNormalizedStart` / `getLightlyNormalizedEnd`, and
  `getOriginalStart` / `getOriginalEnd`, each taking a code-unit index.
- Built only inside the package through the package-private `TextNormalizedView.Builder` (`append` one code unit with
  its four range bounds, then `build`), which copies its arrays to the final size.

**`TextNormalizedKeystrokeView`** — `final`, immutable; the keystroke view only:
- Holds a `TextNormalizedView`, read through `getTextNormalizedView()`, for the keystroke text and its ranges.
- Holds four more parallel arrays for the code units of that text, read by code-unit index:
  - `getSourceKind` → returns the nested enum `SourceKind`: `HANGUL_SYLLABLE`, `STANDALONE_JAMO`,
    `ASCII_LOWER_CASE_LETTER`, `OTHER`.
  - `getFirstSourceJamoPosition` / `getLastSourceJamoPosition` → returns the nested enum `JamoPosition` of the code
    unit's first and last source: `CHOSEONG`, `JUNGSEONG`, `JONGSEONG`, `STANDALONE`.
  - `isSyllableEnd` → returns whether the code unit ends its syllable.
- Built only inside the package through the package-private `TextNormalizedKeystrokeView.Builder`, which wraps a
  `TextNormalizedView.Builder`: `append` takes one code unit with its four range bounds, source kind, first and last
  source jamo positions, and syllable end; `build` copies its arrays to the final size.

**`TextNormalizationType`** — enum of the normalization method that produced a view: `LIGHT` (`normalizeLightly`),
`AGGRESSIVE` (`normalizeAggressively`), `KEYSTROKE` (`normalizeWithKeystroke`), `LINE_INITIAL`
(`normalizeWithLineInitial`).

**`TextNormalizationCharacterMap`** — `final`, `@NoArgsConstructor(access = PRIVATE)`:
- Holds the maps as immutable `public static final Map` constants keyed by code point, with code-point values unless
  stated otherwise.
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
- `COMPOUND_COMPATIBILITY_JAMO_COMPATIBILITY_JAMO_KEYSTROKE_MAP`: a compound vowel or compound final Hangul
  Compatibility Jamo → a `String` of its two Dubeolsik keystroke jamo in typing order.
- `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_MAP`: the inverse of the map above, a `String` key of the
  two Dubeolsik keystroke jamo → their compound Hangul Compatibility Jamo; derived from that map, so the two cannot
  drift apart.

**`TextNormalizationHelper`** — `final`, `@NoArgsConstructor(access = PRIVATE)`:
- Holds the `public static` code-point predicates of `TextNormalizer`.
- `isInDICP`: an `IntPredicate` for the `Default_Ignorable_Code_Point` property.
- `isInCombiningMark`: the general categories `Mn`, `Mc`, and `Me`.
- `isNonLetterNoiseCategory`: the general categories `Z*`, `Cc`, `P*`, `S*`, and `N*`.
- `isLineTerminator`: a code point that `Regex.PATTERN_LINE_TERMINATOR` matches.
- `isAsciiLowerCaseLetter`: a code point from `a` to `z`.
- `isHangulSyllable`: the `Hangul_Syllable_Type` LV or LVT;
  `isHangulSyllableWithoutFinal`: LV only; `isHangulSyllableWithFinal`: LVT only.
- `isCompatibilityJamo`: a code point in the Hangul Compatibility Jamo block.

---

## 3. `normalizeLightly`

The original text is segmented into extended grapheme clusters with ICU4J `BreakIterator.getCharacterInstance()`. Each
cluster passes these steps in order:

| Order | Step |
|---|---|
| 1 | Remove code points with the `Default_Ignorable_Code_Point` property |
| 2 | NFKD, remove combining marks (general categories `Mn`, `Mc`, `Me`), NFKC |
| 3 | Map conjoining jamo through `CONJOINING_COMPATIBILITY_JAMO_MAP` |
| 4 | Map code points through `LETTER_LIKE_SYMBOL_LOWER_CASE_LATIN_MAP` |
| 5 | Lowercase per code point with `UCharacter.toLowerCase` |

- Every code unit a cluster produces gets, as its lightly normalized range, the range of all code units the
  cluster produces in the lightly normalized view (`ﬁ` → `fi`: both code units `[0, 2)`), and, as its original range,
  the whole cluster in the original text, so a surrogate pair is never split (`𝐬` → `s`: `[0, 2)`).
- Whether a code point belongs to a character class defined by a Unicode property is decided by the return value of an
  ICU4J property query (`UCharacter.hasBinaryProperty` for `Default_Ignorable_Code_Point`, `UCharacter.getType` for the
  general categories).
- Lowercasing is per code point, because `String.toLowerCase` can change the length and break the per-code-unit
  ranges.
- Only steps 1 and 2 remove code points, so whitespace, punctuation, and an archaic jamo without a compatibility form
  are kept.
- A cluster made up only of default-ignorable code points and combining marks produces no code unit, so an
  original text of such clusters yields an empty `TextNormalizedView`.

---

## 4. `normalizeAggressively`

The lightly normalized view passes these steps in order, each building a new `TextNormalizedView`:

| Order | Step |
|---|---|
| 1 | Map code points through `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP` |
| 2 | Remove code points of the general categories `Z*`, `Cc`, `P*`, `S*`, and `N*` |
| 3 | Collapse consecutive identical code points into one |
| 4 | Compose each run of Hangul Compatibility Jamo into syllables |
| 5 | Merge an elongated vowel or consonant into its syllable (rules below) |

- After step 2, only letters (general categories `L*`) remain, along with the rare `Cf`, `Co`, `Cs`, and `Cn` code
  points.
- Step 3 compares code points, because comparing code units would join two different supplementary characters
  that share a high surrogate.
- Step 4: a run (maximally successive Hangul Compatibility Jamo) is read left to right as an initial, a vowel, and an
  optional final, and a consonant followed by a vowel starts the next syllable (`ㅂㅕㅇ신` → `병신`). A run that does not
  divide entirely into syllables is kept as it is, because composing only part of it can break up a jamo sequence
  (`ㅅㅂㅠ` stays, instead of becoming `ㅅ뷰`, which no longer contains `ㅅㅂ`). Step 4 runs before step 5, so a composed
  syllable can absorb an elongated vowel (`ㅅㅣ이발` → `시이발` in step 4 → `시발` in step 5).
- Step 5, vowel: a Hangul syllable without a final absorbs a following Hangul Compatibility Jamo of the same vowel, or a
  following syllable with the initial `ㅇ` and the same vowel, taking that syllable's final (`바알` → `발`). The merged
  syllable keeps absorbing while it has no final.
- Step 5, consonant: a Hangul Compatibility Jamo between two Hangul syllables is absorbed into the preceding syllable
  when it equals that syllable's final or the following syllable's initial (`존ㄴ맛` → `존맛`, `시ㅂ발` → `시발`).
- Steps 3–5: a code unit built from several source code units (collapsed, composed, or merged) takes the union of
  their lightly normalized ranges and the union of their original ranges (`ㅅㅣ` → `시`: lightly normalized `[0, 2)`).
- Steps 1–5: every other code unit keeps both its ranges (`시 발` → `시발`: lightly normalized `[0, 1)` and `[2, 3)`).

---

## 5. `normalizeWithKeystroke`

The aggressively normalized view passes these steps in order; steps 1–5 each build a new `TextNormalizedView`, and
step 6 builds the `TextNormalizedKeystrokeView`:

| Order | Step |
|---|---|
| 1 | Undo step 3 of §4 for ASCII lowercase letters, expanding each into those of its lightly normalized range (rules below) |
| 2 | Convert ASCII lowercase letters to Dubeolsik Hangul (rules below) |
| 3 | Step 4 of §4 |
| 4 | Step 5 of §4 |
| 5 | Remove a single non-syllable code point between two Hangul syllables (rules below) |
| 6 | Decompose each Hangul code point into its Dubeolsik keystroke jamo, the Hangul Compatibility Jamo of each keystroke, and keep every other code point; each resulting code unit keeps the code point's ranges and takes the attributes of the table below |

- Step 3 of §4 joins every run of identical code points, including a run separated only by code points that step 2 of
  §4 removes (`ss`, `s s`); step 1 undoes it for ASCII lowercase letters, so a key typed twice stays both a final and an
  initial (`whssk` → `존나`, not `조나`).
  - Expanding an ASCII lowercase letter reads each code point of its lightly normalized range and maps it through
    `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP`, because step 1 of §4 mapped a confusable source letter to that letter (`ѕ` →
    `s`).
  - A code point that does not then become an ASCII lowercase letter, such as one of the categories that step 2 of §4
    removes, is left out of the expansion (`s s` → `ss`).
  - Each ASCII lowercase letter produced takes both the lightly normalized range and the original range of its code
    point in the lightly normalized view, not the union that step 3 of §4 gave the expanded letter.
  - An ASCII lowercase letter of the aggressively normalized view expands only the part of its lightly normalized range
    past the end of every range expanded before it, because §3 gives every code unit of one extended grapheme cluster
    the whole range of that cluster, so expanding one of them already yields the others (`ﬁ` → `fi`: expanding `f`
    yields `fi`, and `i` yields nothing; `ﬁi` → `fii`: `i`, joined with the `i` of `ﬁ`, yields only the last `i`).
- Step 2: each ASCII lowercase letter becomes the Hangul Compatibility Jamo on its key through
  `ASCII_LOWER_CASE_COMPATIBILITY_JAMO_KEYSTROKE_MAP`, and the Hangul Compatibility Jamo converted from adjacent ASCII
  lowercase letters compose into Hangul syllables, each read left to right as an initial, a vowel, and an optional
  final; a converted jamo that joins no syllable stays a Hangul Compatibility Jamo (`tlqkf` → `시발`).
  - Two adjacent ASCII lowercase letters whose jamo `COMPATIBILITY_JAMO_COMPOUND_COMPATIBILITY_JAMO_KEYSTROKE_MAP` joins
    form a compound vowel or compound final (`dnjs` → `원`), which step 3 (step 4 of §4) never builds.
  - A consonant followed by a vowel, converted or typed, stays an initial instead of becoming a final (`rkqtk` → `갑사`;
    `tlqㅏㄹ` → `시ㅂㅏㄹ` in step 2 → `시발` in step 3).
  - A converted jamo never composes with a typed jamo or syllable in this step; steps 3 and 4 join them (`ㅅl발` →
    `ㅅㅣ발` in step 2 → `시발` in step 3; `시l발` → `시ㅣ발` in step 2 → `시발` in step 4; `tlllqkf` → `시ㅣㅣ발` in
    step 2 → `시발` in step 4).
- Steps 3 and 4 change only code points next to converted jamo, because §4 already applied them to the rest. Step 4 can
  also absorb a converted jamo into an adjacent Hangul syllable (`나k` → `나ㅏ` in step 2 → `나` in step 4).
- Step 5: a run of code points that are not Hangul syllables, between two Hangul syllables, is removed when it is one
  code point long, because one inserted letter or jamo hides the word (`시x발` → `시ㅌ발` in step 2 → `시발` in step 5;
  `시ㅈ발` → `시발`). A longer run is kept, because it can be a word written in jamo (`시ㅅㅂ발` keeps `ㅅㅂ`). Every other
  code unit keeps its ranges.
  - An inserted ASCII consonant that step 2 makes the final of the preceding syllable is not removed (`tlxqkf` →
    `싵발`).
  - An ASCII lowercase letter typed alone between two syllables is removed even when its jamo alone forms a word
    (`아h아` → `아ㅗ아` in step 2 → `아아` in step 5).

Step 6 handles each source code point as follows:

| Source code point | `SourceKind` | Becomes | `JamoPosition` | `syllableEnd` |
|---|---|---|---|---|
| Hangul syllable | By its lightly normalized source (below) | The Dubeolsik keystroke jamo (Hangul Compatibility Jamo) of its initial, vowel, and optional final | `CHOSEONG`, `JUNGSEONG`, or `JONGSEONG` of the syllable | `true` on its last code unit only |
| Hangul Compatibility Jamo | By its lightly normalized source (below) | Its Dubeolsik keystroke jamo (Hangul Compatibility Jamo) | `STANDALONE` | `false` |
| Any other | `OTHER` | Kept | `STANDALONE` | `false` |

- `COMPOUND_COMPATIBILITY_JAMO_COMPATIBILITY_JAMO_KEYSTROKE_MAP` splits compound vowels and compound finals into their
  Dubeolsik keystroke jamo (`ㅟ` → `ㅜㅣ`, `ㅄ` → `ㅂㅅ`). A tense consonant stays one code unit, because it is one
  shifted keystroke.
- The `SourceKind` of a Hangul code point is read from the lightly normalized view at the start of its lightly
  normalized range: an ASCII lowercase letter, or a letter that `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP` maps to one, gives
  `ASCII_LOWER_CASE_LETTER`; a Hangul Compatibility Jamo gives `STANDALONE_JAMO`; anything else gives
  `HANGUL_SYLLABLE`.
- A composed syllable keeps the jamo positions of the syllable (`ㅁㅏㅅㅂㅗㄷㅏ` and `tlqkf` have the positions of `맛보다`
  and `시발`).
- Identical Dubeolsik keystroke jamo are not merged, so a final and the next initial stay apart (`존나` → `ㅈㅗㄴㄴㅏ`).

---

## 6. `normalizeWithLineInitial`

- Each line of the lightly normalized view contributes its first code point outside the general categories that
  step 2 of §4 removes, so a bullet or indent does not hide the letter (`- 시원\n- 발코니` → `시발`).
- A supplementary-plane first letter keeps both its code units, so a surrogate pair is never split.
- Each code unit keeps its lightly normalized and original ranges.
- Lines are split at the code points that `isLineTerminator` accepts.
- Fewer than 2 contributing lines yield an empty `TextNormalizedView`.
