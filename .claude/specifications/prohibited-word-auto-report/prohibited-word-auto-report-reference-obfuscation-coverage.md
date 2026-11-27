# Prohibited Word Auto-Report Reference: Obfuscation Coverage

Taxonomy of the text-obfuscation techniques used to evade a profanity dictionary, and how the prohibited-word engine
handles each one. "Implementation §" refers to `prohibited-word-auto-report-implementation.md`, "step 09 §" to
`prohibited-word-auto-report-step-09-scoring-and-properties.md`, "Normalization §" to
`.claude/rules/main-infra-normalization-details.md`, which defines the views, and "prohibited-word §" to
`.claude/rules/main-infra-prohibited-word-details.md`, which defines the match rules, and "policy §" to
`.claude/documents/policy/prohibited-word-filtering-policy.md`, which defines the dictionary entry rules. Each technique
has an obfuscation (OB) ID.

---

## 1. Handling Principle

| Mechanism | Used for | Reason |
|---|---|---|
| Engine view or match rule | Transformations that apply to every word the same way | One rule covers the whole dictionary, including words added later |
| Dictionary entry (lexical) | Variants specific to one word | Policy §2.1 |
| Not handled | Techniques that need state, context, or non-text input | Out of reach for a stateless dictionary engine within the cost limits of implementation §2 |

---

## 2. Unicode-Level Techniques

| ID | Technique | Example | Handling |
|---|---|---|---|
| OB-01 | Full-width and compatibility forms | `시１발`, `ｓｉｂａｌ` | Normalization §3 step 1 |
| OB-02 | Mathematical and enclosed alphanumerics | `𝐬𝐢𝐛𝐚𝐥`, `ⓢⓘⓑⓐⓛ` | Normalization §3 step 1 |
| OB-03 | Latin-confusable symbols without a compatibility decomposition | `🇸🇮🇧🇦🇱`, `🅢🅘🅑🅐🅛` | Normalization §3 step 3 |
| OB-04 | Halfwidth and enclosed Hangul | `ﾵﾲ`, `㉦㉥`, `㉮` | Normalization §3 steps 1 and 2 |
| OB-05 | Conjoining-jamo input | `시발` typed as U+1109 U+1175 U+1107 U+1161 U+11AF | Normalization §3 steps 1 and 2 |
| OB-06 | Invisible characters | `시​발` (U+200B), bidirectional controls, tag characters, variation selectors, soft hyphen, word joiner, combining grapheme joiner (CGJ) | Normalization §3 step 1 |
| OB-07 | Hangul filler characters | `시ㅤ발` (U+3164), U+115F, U+1160, U+FFA0 | Normalization §3 step 1 |
| OB-08 | Combining marks | `시̈발`, `s̈ibal` | Normalization §3 step 1 |
| OB-09 | Case variation | `SiBaL`, `QUDTLS` | Normalization §3 step 4 |

---

## 3. Non-Letter Noise and Insertion Techniques

| ID | Technique | Example | Handling |
|---|---|---|---|
| OB-10 | Whitespace and line-break insertion | `시 발`, `시\n발`, `f u c k` | Normalization §4 step 1; Token alignment of prohibited-word §4 |
| OB-11 | Punctuation, symbol, and markup insertion | `시.발`, `시*발`, `**시**발`, `시~~발` | Normalization §4 step 1 |
| OB-12 | Digit insertion | `씨1발`, `시2발` | Normalization §4 step 1 |
| OB-13 | Emoji insertion | `시🌱발` | Normalization §4 step 1 |
| OB-14 | Noise-jamo insertion | `시ㅠ발`, `시ㅋ발` | Normalization §5 step 2; a consonant jamo after a Hangul syllable without a trailing consonant becomes its trailing consonant by §5 step 1, so `시ㅋ발` (`싴발`) is not caught |

---

## 4. Substitution and Repetition Techniques

| ID | Technique | Example | Handling |
|---|---|---|---|
| OB-16 | Cross-script confusables | `ѕіbаl` (Cyrillic) | Normalization §3 step 5 |
| OB-18 | Jamo and Hangul syllable repetition | `ㅅㅣㅂㅂㅂㅏㄹ`, `시발발발` | Normalization §4 step 2 for jamo; a repeated Hangul syllable is kept, and the lightly normalized view still contains the word (`시발발발`) |
| OB-19 | Vowel and consonant elongation | `시이이발`, `씨이바알`, `씨ㅣ발`, `존ㄴ나` | Normalization §4 step 4 |

---

## 5. Hangul-Structure and Layout Techniques

| ID | Technique | Example | Handling |
|---|---|---|---|
| OB-20 | Full jamo decomposition | `ㅅㅣㅂㅏㄹ` | Normalization §4 step 3 |
| OB-21 | Mixed Hangul syllables and jamo | `시바ㄹ`, `씨ㅂㅏㄹ`, `ㅂㅕㅇ신`, `ㅅㅣ발` | Normalization §4 step 3; `시바ㄹ` by §5 step 1 |
| OB-22 | Split compound jamo | `ㄷㅜㅣㅈㅕ` (`뒤져`) | Normalization §5 step 1 |
| OB-23 | Korean typed on an English layout | `tlqkf`, `qudtls`, `tl발`, `ㅅl발`, `시l발` | Normalization §6 step 1, and steps 2–4 for repetition, jamo runs, and elongation (`tlllqkf`, `ㅅl발`); `시l발` by §5 step 2; a Shift-typed tense consonant reads as its plain consonant (`Tlqkf` → `시발`), because the dictionary holds the plain form and a tense consonant only intensifies the leading consonant |
| OB-25 | Vertical (acrostic) writing | lines starting with `시` and `발` | Normalization §7; Line-initial of prohibited-word §4; never masked (`maskProhibitedWord` of prohibited-word §3) |

---

## 6. Lexical Techniques

Handled by dictionary entries (policy §2.1), except the forms a note excludes; each set of entries arrives as a new
seed migration.

| ID | Technique | Example | Note |
|---|---|---|---|
| OB-26 | Homophone spellings | `쒸발`, `싀발`, `쓰벌` | Entered per word |
| OB-27 | Tense or aspirated consonant swaps | `시팔`, `씨빨` | Entered per word |
| OB-28 | Liaison and gemination spellings | `씹할` | Entered per word; `십알` and `십발` are excluded, because their spaced forms (`십 알`, `십 발`) are normal text (policy §2.1) |
| OB-29 | Leading-consonant abbreviations | `ㅅㅂ`, `ㅂㅅ`, `ㅈㄹ`, `ㅁㅊ` | Entered per abbreviation |
| OB-30 | Romanization | `sibal`, `ssibal`, `shibal` | Entered per spelling; a transliteration view is ambiguous |
| OB-31 | Digit readings | `씨8`, `개18` | Entered per word; bare `18` and `10새` are excluded, because ages, dates, and years use them (`2010새해`) (policy §2.1) |
| OB-32 | Hanja readings | `十八` | Excluded, because it is also the plain number in Chinese and Japanese text (policy §2.1) |
| OB-33 | Self-masked forms | `씨x`, `ㅅ*ㅂ` | `씨x` is entered; `ㅅ*ㅂ` reduces to OB-29 through OB-11 |
| OB-34 | Emoji used as profanity | `🖕` | Entered; matched in the lightly normalized view only (prohibited-word §2 and §4) |
| OB-35 | Neologisms | new slang | Entered by a new seed migration (policy §2.1) |

---

## 7. Not Handled

| ID | Technique | Example | Reason |
|---|---|---|---|
| OB-15 | Hangul look-alikes | `ㅂㅕo신`, `人l발`, `^ㅣ발` | Reading a digit, symbol, or other letter as a jamo depends on context and misreads normal text; a form that spreads is entered as OB-35 |
| OB-17 | Latin leetspeak | `s1bal`, `$ibal`, `5hit` | Reading a digit or symbol as a Latin letter depends on context and misreads normal text (`as5`, `mp3`); a form that spreads is entered as OB-35 |
| OB-24 | English written as its Dubeolsik keystroke jamo | `ㅇㅑㅊㅏ` | The keystroke normalized view covers only Korean typed on the Dubeolsik layout (prohibited-word §2) |
| OB-36 | Splitting across messages | comment 1 `시`, comment 2 `발` | Needs per-member history; the detector is stateless |
| OB-37 | Splitting between title and body | title ends `시`, body starts `발` | Title and body are scanned separately (step 09 §4.3) |
| OB-38 | Reversed or scrambled order | `발시` | Rare, and a reversed view matches normal words; a form that spreads is entered as OB-35 |
| OB-39 | Mixed romanization and Hangul | `시bal` | Combinations multiply per word; neither a view nor entries cover them at a reasonable cost |
| OB-40 | Semantic insults | euphemism, sarcasm, insults without a profanity | Needs a context-aware classifier, which the cost limit of implementation §2 excludes by allowing no machine learning runtime |
| OB-41 | Character references and encodings | `&#49884;&#48156;`, `%EC%8B%9C` | Post and comment text is assumed to be rendered as plain text, so the encoded form is shown as typed |
| OB-42 | Non-text content | text in images, American Standard Code for Information Interchange (ASCII) art | Only text is inspected (implementation §2, Inspection targets); ASCII art has no character-level form |
| OB-43 | English typed on a Korean layout | `야차` | The Hangul syllables a Dubeolsik input method composes from the same keys cannot be told from Korean text |
| OB-44 | Latin letter repetition | `ssiiibal`, `fuuuck` | Collapsing a repeated Latin letter changes normal English words (`book` → `bok`) |

---

## 8. Glossary

- **Seed migration**: A Flyway migration under `data/seed/` that writes dictionary rows
