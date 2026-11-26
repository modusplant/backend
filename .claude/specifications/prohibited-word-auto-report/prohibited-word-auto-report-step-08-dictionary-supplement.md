# Step 08: Dictionary Supplement

## 1. Purpose

Writes the seed migration files that rebuild the whole dictionary at a false-positive likelihood of 1 or less, with
every prohibited word's severity graded by its intensity: they empty both dictionary tables, then insert the existing
prohibited words whose likelihood is 1 or less, the prohibited words the dictionary misses, and the allowed words that
bring others to 1 or less. The existing seed holds only Hangul words, so no Latin profanity is detected, several of its
words match normal words, and its severity is fixed per `type`. The files are the result of the step; the user applies
them. It is a separate step because its result is seed migration files that are never built or tested.

---

## 2. Scope

| Item | Decision |
|---|---|
| Dictionary | Missing prohibited words are added, prohibited words with a high false-positive likelihood are removed, and every prohibited word's severity is graded from 1 to 3 by intensity |

---

## 3. Structure Design

### 3.1 Component Flow

```
prohibited_word / allowed_word (after V7.5.2)        Korean sources, crawled with Playwright
 ↓ each prohibited word rated                         ↓ candidates rated
 └────────────── V7.5.3–V7.5.7 seed files ────────────┘
   (emptied tables, kept and new prohibited words per type, allowed words)
 ↓ applied by the user through Flyway
ProhibitedWordEngine bean at the next application startup
```

### 3.2 Package Structure

None.

### 3.3 Implementation-Wide Changes

#### 3.3.1 Storage

**Migrations** (next free versions after `V7.5.2`), allocated across the steps that write them:

| File | Step |
|---|---|
| `data/seed/V7.5.3__Truncate_prohibited_word_and_allowed_word.sql` | 08 |
| `data/seed/V7.5.4__Insert_general_prohibited_words.sql` | 08 |
| `data/seed/V7.5.5__Insert_family_prohibited_words.sql` | 08 |
| `data/seed/V7.5.6__Insert_sexual_prohibited_words.sql` | 08 |
| `data/seed/V7.5.7__Insert_allowed_words.sql` | 08 |

### 3.4 Step-Specific Changes

#### 3.4.1 Storage

**Step 08**: `V7.5.3` empties `prohibited_word` and `allowed_word` with `TRUNCATE ... RESTART IDENTITY`, which no
foreign key blocks; `V7.5.4`, `V7.5.5`, and `V7.5.6` insert the `GENERAL`, `FAMILY`, and `SEXUAL` prohibited words, in
every script; `V7.5.7` inserts the allowed words.

---

## 4. Technical Details

### 4.1 Review of Existing Prohibited Words

Every row of `prohibited_word` after `V7.5.2` is rated by its false-positive likelihood, counted as the entry rules of
`.claude/documents/policy/prohibited-word-filtering-policy.md` §2.1 count it. One of these follows:

| Rating | Action | Examples |
|---|---|---|
| 1 or less | Kept | `씨발` |
| Above 1, from a few identifiable normal words | Kept, with those words entered as allowed words | `개년` with `5개년`-style words, if the rating then reaches 1 or less |
| Above 1, from open-ended normal text | Not reinserted | A word that normal words or phrases contain too widely for allowed words to cover |

- The review includes these words, whose false positives are already known: `개년` (`5개년`), `자위` (`자위대`), `소추`
  (`탄핵소추`), `등신` (`등신대`), and `떡치` (`떡치기`).
- `이새끼`, `저새끼`, and `니새끼` are not reinserted, because the token alignment rule also matches them across a space
  that splits whole tokens (`이 새끼 떼어`) and inside an unspaced token (`이새끼를`), while plant posts use `새끼` for
  an offshoot. Bare `새끼` and its abbreviation `ㅅㄲ` are not
  entered for the same reason.
- `개새끼`, `씹새끼`, and `미친새끼`, and the sexual terms `강간`, `콘돔`, `유륜`, `성욕`, `포르노`, and `야동`, are
  kept with a rating of 1 or less, because plant posts rarely use them in a normal sense.
- An allowed word that no reinserted prohibited word match is left in is not reinserted either, because it suppresses
  nothing.

### 4.2 Missing Prohibited Words

- Candidates come from Korean-language sources, such as Korean encyclopedia articles on Korean profanity and public
  Korean profanity filter lists, crawled with Playwright instead of a web search or fetch tool.
- They cover Latin profanity, as `LATIN` words, the common Korean profanity that the dictionary lacks, demeaning words
  and slurs, including `-충` words (`무뇌충`), and sexual terms: lust, sex positions and acts, sex-industry places and
  jobs, adult content and adult sites in Hangul and Latin spelling (`폰허브`, `xvideos`), and Japanese sexual terms
  transliterated into Hangul (`아헤가오`).
- A candidate that contains a space is entered unspaced, because the aggressively normalized view removes the space.
- Each candidate is rated like an existing word and entered only at a false-positive likelihood of 1 or less, including
  substring matches in English words, because the lightly normalized view matches a Latin word inside a longer word
  (`ass` in `grass`; `shit` in `shiitake`).
- Every prohibited word, existing or new, takes its `type` from `ProhibitedWordType` and its `severity` from its
  intensity, regardless of `type`:

  | Severity | Intensity | Examples |
  |---|---|---|
  | 1 | Suggestive or mild | `19금`, `음탕`, `존나` |
  | 2 | Common profanity or explicit sexual term | `씨발`, `정상위` |
  | 3 | Family insult, hate slur, or prostitution or sex-crime term | `느금마`, `깜둥이`, `매춘`, `강간` |

### 4.3 Seed File Content

- Each file holds one role (§3.4.1). Every insert file starts with the same warning header comment as
  `data/seed/V5.1.2__Reinsert_swear.sql`.
- Every row they insert meets the entry rules of `.claude/documents/policy/prohibited-word-filtering-policy.md` §2.1,
  and another candidate of the files counts as another word of the same table.
- The views a rule depends on are derived by hand from the normalization steps of
  `rules/main-infra-normalization-details.md` and the keyword and validity rules of
  `rules/main-infra-prohibited-word-details.md`; the engine is not run on the file.

---

## 5. Other Implementation Requirements

- The step ends when `V7.5.3`–`V7.5.7` are written. Nothing compiles, builds, or tests them; the user applies them on
  the local database.
- Every step's result must build and pass its tests, except step 08: its result is a seed migration file, which is
  written only and never compiled, built, or tested, because compilation runs `flywayMigrate` and would apply it to the
  database.

---

## 6. Glossary

Allowed word and false-positive likelihood are defined in the Glossary of
`.claude/documents/policy/prohibited-word-filtering-policy.md`.

- **Engine**: `ProhibitedWordEngine`, which matches an original text against the dictionary
- **Prohibited word**: A word that is masked, rejected in a nickname, and scored, stored in `prohibited_word`: a
  profanity, a sexual term, or a non-verbal string such as an emoji
- **Seed migration**: A Flyway migration under `data/seed/` that writes dictionary rows
