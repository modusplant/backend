---
paths:
  - "src/main/java/kr/modusplant/infrastructure/swear/**"
---

# Swear Infrastructure Conventions

The original text is the text that `SwearEngine.match` checks. A view is one of its five normalized forms, named by
`TextNormalizationType`, and a view's text is that form: `LIGHT` from `normalizeLightly`, `AGGRESSIVE` from
`normalizeAggressively`, `SYLLABLE` from `normalizeWithSyllable`, `KEYSTROKE` from `normalizeWithKeystroke`, and
`LINE_INITIAL` from `normalizeWithLineInitial`.

---

## 1. Package Structure

- `SwearWord` — one swear word with its type, severity, and script
- `SwearEngine` — per-view tries over swear words and not-swear words; returns valid, deduplicated matches
- `SwearScript` — the script a swear word is written in: `HANGUL`, `LATIN`, `OTHER`
- `SwearMatch` — one match: swear word, view, and original span

---

## 2. Keywords and Tries

A word is a swear word or a not-swear word. A keyword is the normalized text of a word that the tries of one view hold
as a key. It is a separate term from a view's text, which the tries search, because a view's keyword is not always the
word normalized for that view:

| View | Keyword |
|---|---|
| `LIGHT` | `normalizeLightly` text of every word |
| `AGGRESSIVE` | The same text, only for a word whose `normalizeAggressively` text equals it |
| `SYLLABLE` | The same text, only for the words of `AGGRESSIVE` whose `normalizeWithSyllable` text equals it |
| `KEYSTROKE` | `normalizeWithKeystroke` text, only for the not-swear words and `HANGUL` swear words of `AGGRESSIVE` |
| `LINE_INITIAL` | `normalizeLightly` text of every word |

- Each view has a swear-word trie and a not-swear-word trie. Both are ICU4J `CharsTrie`s whose keys are the view's
  keywords of their words. A keyword match takes its keyword from the matched range of the view's
  text, which equals the keyword, so the trie values are unused.
- A keyword match of the swear-word trie is a match of every swear word with that keyword, so swear words that share a
  keyword all match the same span of the original text.
- A swear word that the aggressive view changes stays in `LIGHT` and `LINE_INITIAL` only, because a shortened form
  matches more text than the swear word (`씨8` → `씨`). A swear word that the aggressive view removes
  entirely stays there too, because no aggressively normalized text contains it (`🖕`, general category `So`).
- A word of `AGGRESSIVE` that the syllable view changes gets no `SYLLABLE` keyword, for the same reason: the syllable
  view removes its jamo or Latin letters (`ㅅㅂ`, `fuck`).
- A word whose lightly normalized text is empty, because it is made up only of default-ignorable code points and
  combining marks, gets no keyword and never matches; such a swear word also skips the script check of `create` (§3).
- Only a `HANGUL` swear word gets a `KEYSTROKE` keyword, because the keystroke view covers only Korean typed on the
  Dubeolsik layout.

---

## 3. Class Responsibilities

**`SwearWord`** — record `(String word, SwearType type, int severity, SwearScript script)`; one swear word.

**`SwearEngine`** — `final`, immutable, private constructor; not a Spring bean:
- `static create(TextNormalizer, List<SwearWord> swearWords, List<String> notSwearWords)` normalizes every word into
  the keywords of each view and builds the tries of each view from them once (§2). It fails with
  `ConfigurationErrorCode.INCORRECT_SWEAR_SCRIPT`, listing every swear word whose `script` is not what `SwearScript.of`
  returns for its lightly normalized text, so the startup reports all mismatches at once.
- `match(String text)` → a `List<SwearMatch>`. It builds the five views of `text`, the original text, searches each
  view's text with that view's tries, and returns the matches that §4 and §5 keep, ordered by view in the §5 order, then
  by start index in the view's text.
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
| Not-swear word | All views | It is not inside a keyword match of a not-swear word in the same view (`시발점`) |
| Token alignment | Aggressive, syllable, keystroke | A span whose lightly normalized range contains whitespace (ICU4J `isUWhiteSpace`) starts and ends at token boundaries of the light view (`시발` matches in `시 발 놈`, not in `검사 시 발생`) |
| ASCII lowercase run | Keystroke | For each ASCII lowercase letter in its lightly normalized range, that range covers the whole maximal run of ASCII lowercase letters in the light view containing it, because English text also converts to Hangul (no `시발` match in `atlqkfb`) |
| Line-initial | Line-initial | The swear word's lightly normalized text has 2 or more code points |

---

## 5. Deduplication

Among the matches that pass §4, those of the same `SwearWord` whose original spans overlap count once. The kept match
comes from the first view in the order `LIGHT`, `AGGRESSIVE`, `SYLLABLE`, `KEYSTROKE`, `LINE_INITIAL`. Two different
swear words whose original spans overlap both stay.
