---
paths:
  - "src/main/java/kr/modusplant/infrastructure/swear/**"
---

# Swear Infrastructure Conventions

The original text is the text that `SwearEngine.match` checks. A view is one of its five normalized forms, each a
`TextNormalizedView` subclass named by a `TextNormalizationType` constant: the lightly normalized view (`LIGHT`) from
`normalizeLightly`, the aggressively normalized view (`AGGRESSIVE`) from `normalizeAggressively`, the syllable
normalized view (`SYLLABLE`) from `normalizeWithSyllable`, the keystroke normalized view (`KEYSTROKE`) from
`normalizeWithKeystroke`, and the line-initial normalized view (`LINE_INITIAL`) from `normalizeWithLineInitial`.
- A view's text is the normalized `String` it holds; the lightly normalized text is the lightly normalized view's
  text, and likewise for the other views. A word (§6) has the same five texts, from the views built with the word as
  the original text.
- Each code unit of a view's text has a lightly normalized range and an original range: the range `[start, end)` of
  code-unit indexes it derives from in the lightly normalized text and in the original text.
- A token is a maximal sequence of consecutive code points of the lightly normalized text that are not whitespace
  (ICU4J `isUWhiteSpace`).

---

## 1. Package Structure

- `SwearWord` — one swear word with its type, severity, and script
- `SwearEngine` — per-view tries over the keywords (§6) of swear words and not-swear words; returns the keyword matches
  that §4 and §5 keep
- `SwearScript` — the script a swear word is written in: `HANGUL`, `LATIN`, `OTHER`
- `SwearMatch` — one match: swear word, view, and original span (§6)

---

## 2. Keywords and Tries

The keyword of a word for a view is not always the word's text in that view:

| View | Keyword |
|---|---|
| Lightly normalized view | The lightly normalized text, for every word |
| Aggressively normalized view | The lightly normalized text, only for a word whose aggressively normalized text equals its lightly normalized text |
| Syllable normalized view | The lightly normalized text, only for a word that has an aggressively normalized view keyword and whose syllable normalized text equals its lightly normalized text |
| Keystroke normalized view | The keystroke normalized text, only for a not-swear word or a `HANGUL` swear word that has an aggressively normalized view keyword, because the keystroke normalized view covers only Korean typed on the Dubeolsik layout |
| Line-initial normalized view | The lightly normalized text, for every word |

- Each view has a swear-word trie and a not-swear-word trie: ICU4J `CharsTrie`s whose keys are the view's keywords of
  the swear words and of the not-swear words, respectively. A keyword match takes its keyword from its range in the
  view's text, which equals the keyword, so the trie values are unused.
- A keyword match of the swear-word trie is a match of every swear word with that keyword, so swear words that share a
  keyword all match the same original span.
- The aggressively normalized and syllable normalized view rows require the word's text in that view to equal its
  lightly normalized text, because a shortened form matches more text than the swear word: `normalizeAggressively`
  shortens `씨8` to `씨`, down to an empty text that no aggressively normalized text contains (`🖕`, general category
  `So`), and `normalizeWithSyllable` removes Hangul Compatibility Jamo and ASCII lowercase letters (`ㅅㅂ`, `fuck`).
- A word whose lightly normalized text is empty, because the word is made up only of default-ignorable code points and
  combining marks, has no keyword and never matches; such a swear word also skips the script check of `create` (§3).

---

## 3. Class Responsibilities

**`SwearWord`** — record `(String word, SwearType type, int severity, SwearScript script)`.

**`SwearEngine`** — `final`, immutable, private constructor; not a Spring bean:
- `static create(TextNormalizer, List<SwearWord> swearWords, List<String> notSwearWords)` normalizes every word into
  its keywords for each view and builds the tries of each view from them once (§2). It fails with
  `ConfigurationErrorCode.INCORRECT_SWEAR_SCRIPT`, listing every swear word whose `script` is not what `SwearScript.of`
  returns for its lightly normalized text, so one application startup reports all mismatches at once.
- `match(String text)` → a `List<SwearMatch>`. It builds the five views of `text`, the original text, searches each
  view's text with that view's tries, and returns a `SwearMatch` for each keyword match of a swear word that §4 and §5
  keep, ordered by view in the §5 order, then by start index in the view's text.
- A search walks a trie from every code-unit index of the view's text, with no case folding, because every view's text
  is already lowercase. Each keyword the walk reaches yields a keyword match, and overlapping keyword matches are all
  kept. Each search creates its own `CharsTrie` from the serialized trie that `create` builds, because a `CharsTrie`
  holds its walk state.

**`SwearScript`** — `static of(String)` derives the script from the swear word's lightly normalized text: any Hangul
code point (ICU4J `UScript.HANGUL`) gives `HANGUL`; otherwise any Latin code point gives `LATIN`; otherwise `OTHER`
(`十八`, `🖕`).

**`SwearMatch`** — record `(SwearWord swearWord, TextNormalizationType textNormalizationType, int originalStart,
int originalEnd)`; `[originalStart, originalEnd)` is the original span of the keyword match.

---

## 4. Validity Rules

The lightly normalized range of a keyword match runs from the `getLightlyNormalizedStart` of its first code unit to
the `getLightlyNormalizedEnd` of its last code unit. `match` keeps a keyword match of a swear word only when every
rule for its view holds:

| Rule | Applies to | Keeps the keyword match when |
|---|---|---|
| Not-swear word | Every view | Its range lies within the range of no keyword match of a not-swear word in the same view (`시발점`) |
| Token alignment | Aggressively normalized, syllable normalized, and keystroke normalized views | Its lightly normalized range contains no whitespace, or starts at the start of a token and ends at the end of a token (`시발` matches in `시 발 놈`, not in `검사 시 발생`) |
| ASCII lowercase run | Keystroke normalized view | For each ASCII lowercase letter in its lightly normalized range, that range covers the whole maximal sequence of consecutive ASCII lowercase letters in the lightly normalized text containing the letter, because English text also converts to Hangul (no `시발` match in `atlqkfb`) |
| Line-initial | Line-initial normalized view | The swear word's lightly normalized text has 2 or more code points |

---

## 5. Deduplication

Among the keyword matches that pass §4, those of the same `SwearWord` whose original spans overlap count once. The
kept one comes from the first view in the order: lightly normalized, aggressively normalized, syllable normalized,
keystroke normalized, line-initial normalized; within one view, it is the one with the smallest start index in the
view's text. Two different swear words whose original spans overlap both stay.

---

## 6. Glossary

- **Keyword**: a key of a view's tries; the text of a word that §2 assigns to that view.
- **Keyword match**: a keyword that a search finds in a view's text, with the range `[start, end)` of code-unit indexes
  it occupies there.
- **Not-swear word**: a word passed to `create` in `notSwearWords` that contains a swear word but is not one
  (`시발점`).
- **Original span**: the range of the original text that a keyword match derives from, from the `getOriginalStart` of
  its first code unit to the `getOriginalEnd` of its last code unit, so it includes characters a view removed between
  them (`시.발` → `[0, 3)`).
- **Word**: a swear word (`SwearWord`) or a not-swear word.
