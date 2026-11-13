---
paths:
  - "src/main/java/kr/modusplant/infrastructure/swear/**"
---

# Swear Infrastructure Conventions

Applies to `kr.modusplant.infrastructure.swear`.

---

## 1. Package Structure

The package is flat: four classes, no sub-packages.

```
swear/
├ SwearWord # One swear word with its type, severity, and script
├ SwearEngine # Per-view tries over swear words and not-swear words; returns valid, deduplicated matches
├ SwearScript # The script a swear word is written in: HANGUL, LATIN, OTHER
└ SwearMatch # One match: swear word, view, and original span
```

---

## 2. Keywords and Tries

A word is a swear word or a not-swear word. A keyword is the normalized text of a word that the tries of one view hold
as a key, a term kept apart from a view's text because the tries search the views of the original text:

| View | Keyword |
|---|---|
| `LIGHT` | `normalizeLightly` text of every word |
| `AGGRESSIVE` | The same text, only for a word whose `normalizeAggressively` text equals it |
| `KEYSTROKE` | `normalizeWithKeystroke` text, only for the words of `AGGRESSIVE` whose keystroke view's lightly normalized ranges cover the whole lightly normalized text |
| `LINE_INITIAL` | `normalizeLightly` text of every swear word |

- Each view has a swear-word trie and, except `LINE_INITIAL`, a not-swear-word trie. Both are ICU4J `CharsTrie`s whose
  keys are the view's keywords of their words. The trie value of each key, the `int` that `CharsTrie.getValue`
  returns, is the index of that keyword in a keyword list kept beside the trie, because a `CharsTrie` value can only be
  an `int`.
- A keyword match of the swear-word trie is a match of every swear word with that keyword, so swear words that share a
  keyword all match the same span of the original text.
- A swear word that the aggressive view changes stays in `LIGHT` and `LINE_INITIAL` only, because a shortened form
  matches more text than the swear word (`씨8` → `씨`, `ass` → `as`). A swear word that the aggressive view removes
  entirely stays there too, because no aggressively normalized text contains it (`🖕`, general category `So`).
- A swear word of `AGGRESSIVE` whose keystroke view leaves part of its lightly normalized text uncovered gets no
  `KEYSTROKE` keyword, for the same reason: the keystroke view removed one of its code points (`개ㅅ끼` → `ㄱㅐㄲㅣ`).
- A word whose lightly normalized text is empty, because it is made up only of default-ignorable code points and
  combining marks, gets no keyword and never matches; such a swear word also skips the script check of `create` (§3).
- A swear word's stored `script` decides its keystroke rules (§4); a swear word whose `script` is `OTHER` has none.

---

## 3. Class Responsibilities

**`SwearWord`** — record `(String word, SwearType type, int severity, SwearScript script)`; one swear word.

**`SwearEngine`** — `final`, immutable, private constructor; not a Spring bean:
- `static create(TextNormalizer, List<SwearWord> swearWords, List<String> notSwearWords)` normalizes every word into
  the keywords of each view and builds the tries of each view from them once (§2). It fails with
  `ConfigurationErrorCode.INCORRECT_SWEAR_SCRIPT`, listing every swear word whose `script` is not what `SwearScript.of`
  returns for its lightly normalized text, so the startup reports all mismatches at once.
- `match(String text)` → a `List<SwearMatch>`. It builds the four views of `text`, the original text, with
  `normalizeLightly`, `normalizeAggressively`, `normalizeWithKeystroke`, and `normalizeWithLineInitial`, searches each
  view's text with that view's tries, and returns the matches that §4 and §5 keep, ordered by view in the §5 order,
  then by start index in the view's text.
- A search walks a trie from every code-unit index of the view's text, with no case folding, because the views are
  already lowercase. Each keyword the walk reaches yields a keyword match: the keyword and the range `[start, end)` it
  occupies in the view's text. Overlapping keyword matches are all kept, because scoring and masking need each of them.
  Each search creates its own `CharsTrie` from the serialized trie that `create` builds, because a `CharsTrie` holds
  its walk state.

**`SwearScript`** — enum `HANGUL`, `LATIN`, `OTHER`; the script of a swear word. `static of(String)` derives it from
the swear word's lightly normalized text: any Hangul code point (ICU4J `UScript.HANGUL`) gives `HANGUL`; otherwise any
Latin code point gives `LATIN`; otherwise `OTHER` (`十八`, `🖕`).

**`SwearMatch`** — record `(SwearWord swearWord, TextNormalizationType textNormalizationType, int originalStart,
int originalEnd)`. The original span is the range `[originalStart, originalEnd)` of the original text, from the
minimum `getOriginalStart` to the maximum `getOriginalEnd` of the matched code units. It covers exactly the evading
characters (`시.발` → `[0, 3)`).

---

## 4. Validity Rules

`match` keeps a keyword match of a swear word only when every rule for its view holds:

| Rule | Applies to | Keeps the match when |
|---|---|---|
| Not-swear word | Light, aggressive, keystroke | It is not inside a keyword match of a not-swear word in the same view (`시발점`) |
| Token alignment | Aggressive, keystroke | A span whose lightly normalized range contains whitespace (ICU4J `isUWhiteSpace`) starts and ends at token boundaries of the light view (`시발` matches in `시 발 놈`, not in `검사 시 발생`) |
| Jamo role | Keystroke, `HANGUL` swear word | It starts at a `CHOSEONG` or `STANDALONE` jamo and ends at a syllable end or `STANDALONE` jamo (no `ㅅㅂ` match in `맛보다`) |
| Keyboard run | Keystroke, `HANGUL` swear word | For each of its `ASCII_LOWER_CASE_LETTER` units, the match's lightly normalized range covers the whole run that the unit comes from, a run being a maximal sequence of light-view code points giving that source kind, because English text also converts to Hangul (no `시발` match in `atlqkfb`) |
| Latin swear word | Keystroke, `LATIN` swear word | Every unit has the source kind `STANDALONE_JAMO`, so only English written out as its Dubeolsik keystroke jamo matches, because the syllables a Dubeolsik input method composes from the same keys cannot be told from Korean text (`dick` matches in `ㅇㅑㅊㅏ`, not in `야차`) |
| Line-initial | Line-initial | The swear word's lightly normalized text has 2 or more code points |

---

## 5. Deduplication

Among the matches that pass §4, those of the same `SwearWord` whose original spans overlap count once. The kept match
comes from the first view in the order `LIGHT`, `AGGRESSIVE`, `KEYSTROKE`, `LINE_INITIAL`. Two different swear words
whose original spans overlap both stay.
