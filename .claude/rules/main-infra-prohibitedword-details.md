---
paths:
  - "src/main/java/kr/modusplant/infrastructure/prohibitedword/**"
  - "src/main/java/kr/modusplant/infrastructure/config/prohibitedword/**"
---

# Prohibited Word Infrastructure Conventions

The original text is the text that `ProhibitedWordEngine.match` checks. A word (§6) has the five view texts of the views
built with the word as the original text.

---

## Parent Document

- `.claude/documents/term/text-normalization-term.md` — the original text, view, and token terms (§1)
- `.claude/documents/policy/prohibited-word-filtering-policy.md` — the dictionary (§2.1) and the masking behavior (§2.2)
  that `ProhibitedWordEngine` and `maskProhibitedWord` implement

---

## 1. Package Structure

- `ProhibitedWord` — one prohibited word with its type, severity, and script
- `ProhibitedWordEngine` — per-view tries over the keywords (§6) of prohibited words and allowed words; returns the
  keyword matches that §4 and §5 keep
- `ProhibitedWordScript` — the script a prohibited word is written in: `HANGUL`, `LATIN`, `OTHER`
- `ProhibitedWordMatch` — one match: prohibited word, view, and original span (§6)
- `ProhibitedWordHolder` — Spring entry point of the engine for masking and the nickname prohibited word check

---

## 2. Keywords and Tries

The keyword of a word for a view is not always the word's text in that view:

| View | Keyword |
|---|---|
| Lightly normalized view | The lightly normalized text, for every word |
| Aggressively normalized view | The lightly normalized text, only for a word whose aggressively normalized text equals its lightly normalized text |
| Syllable normalized view | The lightly normalized text, only for a word that has an aggressively normalized view keyword and whose syllable normalized text equals its lightly normalized text |
| Keystroke normalized view | The keystroke normalized text, only for an allowed word or a `HANGUL` prohibited word that has an aggressively normalized view keyword, because the keystroke normalized view covers only Korean typed on the Dubeolsik layout |
| Line-initial normalized view | The lightly normalized text, for every word |

- Each view has a prohibited-word trie and an allowed-word trie: ICU4J `CharsTrie`s whose keys are the view's keywords
  of the prohibited words and of the allowed words, respectively. A keyword match takes its keyword from its range in
  the view's text, which equals the keyword, so the trie values are unused.
- A keyword match of the prohibited-word trie is a match of every prohibited word with that keyword, so prohibited words
  that share a keyword all match the same original span.
- The aggressively normalized and syllable normalized view rows require the word's text in that view to equal its
  lightly normalized text, because a shortened form matches more text than the prohibited word: `normalizeAggressively`
  shortens `씨8` to `씨`, down to an empty text that no aggressively normalized text contains (`🖕`, general category
  `So`), and `normalizeWithSyllable` removes Hangul Compatibility Jamo and ASCII lowercase letters (`ㅅㅂ`, `fuck`).
- A word whose lightly normalized text is empty, because the word is made up only of default-ignorable code points and
  combining marks, has no keyword and never matches; such a prohibited word also skips the script check of `create`
  (§3).

---

## 3. Class Responsibilities

**`ProhibitedWord`** — record `(String word, ProhibitedWordType type, int severity, ProhibitedWordScript script)`.

**`ProhibitedWordEngine`** — `final`, immutable, private constructor; a Spring bean that
`config/prohibitedword/ProhibitedWordConfig` builds once at application startup from `prohibited_word` (word, type,
severity, script) and `allowed_word` (word), loaded through `JdbcTemplate`:
- `static create(TextNormalizer, List<ProhibitedWord> prohibitedWords, List<String> allowedWords)` normalizes every word
  into its keywords for each view and builds the tries of each view from them once (§2). It fails with
  `ConfigurationErrorCode.INCORRECT_PROHIBITED_WORD_SCRIPT`, listing every prohibited word whose `script` is not what
  `ProhibitedWordScript.of` returns for its lightly normalized text, so one application startup reports all mismatches
  at once.
- `match(String text)` → a `List<ProhibitedWordMatch>`. It builds the five views of `text`, the original text, searches
  each view's text with that view's tries, and returns a `ProhibitedWordMatch` for each keyword match of a prohibited
  word that §4 and §5 keep.
- A search walks a trie from every code-unit index of the view's text, with no case folding, because every view's text
  is already lowercase. Each keyword the walk reaches yields a keyword match, and overlapping keyword matches are all
  kept. Each search creates its own `CharsTrie` from the serialized trie that `create` builds, because a `CharsTrie`
  holds its walk state.

**`ProhibitedWordScript`** — `static of(String)` derives the script from the prohibited word's lightly normalized text:
any Hangul code point (ICU4J `UScript.HANGUL`) gives `HANGUL`; otherwise any Latin code point gives `LATIN`; otherwise
`OTHER` (`十八`, `🖕`).

**`ProhibitedWordMatch`** — record
`(ProhibitedWord prohibitedWord, TextNormalizationType textNormalizationType, int originalStart, int originalEnd)`;
`[originalStart, originalEnd)` is the original span of the keyword match.

**`ProhibitedWordHolder`** — Spring bean that injects `ProhibitedWordEngine` and implements
`shared/kernel/inspector/ProhibitedWordInspector`:
- `maskProhibitedWord(String text)` merges the overlapping or adjacent original spans of the `ProhibitedWordMatch`es not
  from `LINE_INITIAL` and replaces each merged span with `***`.
- `containsProhibitedWord(String text)`, the `ProhibitedWordInspector` method, returns whether `match` returns any
  `ProhibitedWordMatch`.

---

## 4. Validity Rules

`match` keeps a keyword match of a prohibited word only when every rule for its view holds:

| Rule | Applies to | Keeps the keyword match when |
|---|---|---|
| Allowed word | Every view | Its range lies within the range of no keyword match of an allowed word in the same view (`시발점`) |
| Token alignment | Aggressively normalized, syllable normalized, and keystroke normalized views | Its lightly normalized span contains no whitespace, or starts at the start of a token and ends at the end of a token (`시발` matches in `시 발 놈`, not in `검사 시 발생`) |
| ASCII lowercase run | Keystroke normalized view | Its lightly normalized span covers each ASCII lowercase run of the lightly normalized text that it overlaps, because the keystroke normalized view converts every ASCII lowercase letter to Hangul, so part of a longer run, such as an English word, can convert to a keyword and give a false match (no `시발` match in `atlqkfb`) |
| Line-initial | Line-initial normalized view | The prohibited word's lightly normalized text has 2 or more code points |

---

## 5. Deduplication

Among the keyword matches that pass §4, those of the same `ProhibitedWord` whose original spans overlap count once. The
kept one comes from the first view in the order: lightly normalized, aggressively normalized, syllable normalized,
keystroke normalized, line-initial normalized; within one view, it is the one with the smallest start index in the
view's text. Two different prohibited words whose original spans overlap both stay.

---

## 6. Glossary

- **Keyword**: a text of a word that a view's tries hold as a key and that a search looks for in the view's text; the
  §2 table gives, per view, which of the word's texts it is and for which words.
- **Keyword match**: a keyword that a search finds in a view's text, with the range `[start, end)` of code-unit indexes
  it occupies there.
- **Lightly normalized span** and **original span**: the range of the lightly normalized text and of the original text
  that a keyword match derives from, from the `getLightlyNormalizedStart` or `getOriginalStart` of its first code unit
  to the `getLightlyNormalizedEnd` or `getOriginalEnd` of its last code unit, so it includes characters a view removed
  between them (`시.발` → `[0, 3)`).
- **Allowed word**: a word passed to `create` in `allowedWords`.
- **Word**: a prohibited word (`ProhibitedWord`) or an allowed word.
