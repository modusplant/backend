---
paths:
  - "src/main/java/kr/modusplant/infrastructure/swear/**"
---

# Swear Infrastructure Conventions

Applies to `kr.modusplant.infrastructure.swear`.

---

## 1. Package Structure

The package is flat: five classes, no sub-packages.

```
swear/
├ SwearEngine # Per-view tries over dictionary and allowlist; returns valid, deduplicated matches
├ SwearWord # One dictionary word with its type, severity, and script
├ SwearScript # The script a dictionary word is written in: HANGUL, LATIN, OTHER
└ SwearMatch # One match: word, view, and original span
```

---

## 2. Class Responsibilities

**`SwearEngine`** — `final`, immutable, private constructor; not a Spring bean:
- `static create(TextNormalizer, List<SwearWord> swearWords, List<String> allowlist)` compiles the lists once (§3).
  It takes the `TextNormalizer` as an argument, so its caller passes the Spring bean and a test passes one built from
  `new Icu4jConfig()`. It throws one `IllegalArgumentException` that lists every word whose `script` differs from
  `SwearScript.of` its light form, so a wrong dictionary row fails the startup once with all mismatches.
- `match(String text)` → a `List<SwearMatch>`. It builds the four `TextNormalizer` views of the text, runs each view
  against its tries, and keeps the matches that pass §4 and §5, in view order. A `null` or empty text returns an
  empty list.
- Uses ICU4J `CharsTrieBuilder` / `CharsTrie` with no case folding (the views are already lowercase). Each keyword's
  value is its index in the trie's distinct keyword list. A trie walks from every UTF-16 start position of the view
  until `BytesTrie.Result.hasNext()` is false and records every result with a value, so overlapping matches are all
  returned as half-open spans of the view. A view with no keywords has no trie, because `CharsTrieBuilder` cannot
  build an empty one. The serialized trie is kept and a new `CharsTrie` is created per call, because a `CharsTrie`
  holds its walk state.

**`SwearWord`** — record `(String word, SwearType type, int severity, SwearScript script)`.

**`SwearScript`** — enum `HANGUL`, `LATIN`, `OTHER`; the script of a word's characters. `static of(String)` derives it
from a light form: any Hangul code point (ICU4J `UScript.HANGUL`) gives `HANGUL`; otherwise any Latin code point gives
`LATIN`; otherwise `OTHER` (`十八`, `🖕`).

**`SwearMatch`** — record `(SwearWord swearWord, TextNormalizationType textNormalizationType, int originalStart,
int originalEnd)`. The original span is the half-open UTF-16 range `[originalStart, originalEnd)` of the original text,
from the minimum `getOriginalStart` to the maximum `getOriginalEnd` of the matched code units. It covers exactly the
evading characters (`시.발` → `[0, 3)`).

---

## 3. Compilation

| View | Word and allowlist keywords |
|---|---|
| `LIGHT` | `lightlyNormalize` text of every entry |
| `AGGRESSIVE` | The same text, only for an entry whose `aggressivelyNormalize` text equals it |
| `KEYSTROKE` | `decomposeIntoKeystrokes` text, only for the entries of `AGGRESSIVE` |
| `LINE_INITIAL` | `lightlyNormalize` text of every word; no allowlist |

- An entry that the aggressive view changes stays in `LIGHT` (and `LINE_INITIAL`) only, because a shortened form
  matches more text than the word (`씨8` → `씨`, `ass` → `as`). `🖕` stays in `LIGHT` only for the same reason.
- An entry whose form is empty is skipped. Several words sharing a keyword are all kept, and each is validated on its
  own.
- A word's stored `script` decides its keystroke rules (§4); a word whose `script` is `OTHER` has none.

---

## 4. Validity Rules

A match is kept only when every rule for its view holds:

| Rule | Applies to | Keeps the match when |
|---|---|---|
| Allowlist | Light, aggressive, keystroke | It is not inside an allowlist match of the same view (`시발점`) |
| Token alignment | Aggressive, keystroke | A span whose lightly normalized range contains whitespace (ICU4J `isUWhiteSpace`) starts and ends at token boundaries of the light view (`검사 시 발생` rejected, `시 발 놈` kept) |
| Jamo role | Keystroke, `HANGUL` word | It starts at an `INITIAL` or `STANDALONE` jamo and ends at a syllable end or `STANDALONE` jamo (`맛보다` rejected for `ㅅㅂ`) |
| Keyboard run | Keystroke, `HANGUL` word, with any `LATIN_LETTER` unit | Its lightly normalized range covers the whole run of keyboard letters (keys of `LATIN_LOWER_CASE_TWO_BEOLSIK_JAMO_MAP` or `CROSS_SCRIPT_CONFUSABLE_LATIN_MAP`) in the light view around each such unit, because English text also converts to Hangul (`atlqkfb` rejected) |
| Latin word | Keystroke, `LATIN` word | Every unit has the source kind `STANDALONE_JAMO` (`려차` rejected for `fuck`, `ㄹㅕㅊㅏ` kept) |
| Line-initial | Line-initial | The word's light form has 2 or more code points |

---

## 5. Deduplication

Matches of the same `SwearWord` whose original spans overlap count once. The kept match comes from the first view in
the order `LIGHT`, `AGGRESSIVE`, `KEYSTROKE`, `LINE_INITIAL`. Two different words that overlap in the text both stay.
