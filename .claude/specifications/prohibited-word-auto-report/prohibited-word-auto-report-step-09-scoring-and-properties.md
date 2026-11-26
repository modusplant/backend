# Step 09: Scoring and Properties

Expands `prohibited-word-auto-report-implementation.md`.

## 1. Purpose

Makes masking return the engine's matches, turns matches into a score and an auto-report decision, and binds the
threshold, cap, and kill switch. It is a separate step because masking and nickname rejection use matches without a
score, while only content auto-reporting uses the score. No flow calls the new methods until steps 11 and 12.

---

## 2. Scope

- **Report decision**, the score against a configurable threshold: `ProhibitedWordHolder.inspectProhibitedWord`
  returns the matches that masking finds (§4.3), `ProhibitedWordDetection` computes the score and the decision from
  them (§4.1), and `ProhibitedWordProperties` binds the threshold, the per-word cap, and the kill switch (§4.2).

---

## 3. Structure Design

### 3.1 Component Flow

```
ProhibitedWordProperties ← app.moderation.prohibited-word (application.yml), validated at startup
ProhibitedWordHolder.inspectProhibitedWord(text)
 └→ ProhibitedWordEngine.match(text) once → ProhibitedWordInspection (masked text, matches)
ProhibitedWordHolder.maskProhibitedWord(text) → inspectProhibitedWord(text).maskedText()
ProhibitedWordHolder.detectProhibitedWord(matches)
 ├[enabled = false]→ ProhibitedWordDetection.disabled() (isDisabled() true: neither reportable nor clean)
 └[enabled = true]→ ProhibitedWordDetection.of(matches, ProhibitedWordProperties) → score, isReportable()
```

### 3.2 Package Structure

- `infrastructure/prohibitedword/`
  - `ProhibitedWordHolder` — existing; `inspectProhibitedWord(text)` masks a text and returns its matches from the
    same engine scan, `maskProhibitedWord` delegates to it, and `detectProhibitedWord(matches)` returns one detection
    for the given matches (§4.3)
  - `ProhibitedWordInspection` — record of the masked text and the matches of one text (§4.3)
  - `ProhibitedWordProperties` — a record validated at startup and registered explicitly (§4.2)
  - `ProhibitedWordDetection` — `of(matches, properties)` scores the matches and exposes `isReportable()`;
    `disabled()` is the detection of a disabled kill switch, whose `isDisabled()` is true (§4.1, §4.3)

---

## 4. Technical Details

### 4.1 Scoring

`ProhibitedWordDetection.of` computes:

```
score = Σ over distinct matched words ( severity × min(occurrences, maxOccurrencesPerWord) )
isReportable() = score ≥ autoReportThreshold
```

| Property | Initial value | Meaning |
|---|---|---|
| `app.moderation.prohibited-word.auto-report-threshold` | `3` | Minimum score to auto-report |
| `app.moderation.prohibited-word.max-occurrences-per-word` | `3` | Repetitions of one prohibited word counted at most this many times |
| `app.moderation.prohibited-word.enabled` | `true` | Kill switch for detection (masking stays on) |

- The initial values follow the common pattern for a 1–3 severity scale: one severe word, a general word with any second
  occurrence, or three mild-word occurrences reports; a single general word or at most two mild-word occurrences only
  masks. They are pending review.
- Occurrences are counted after the engine's deduplication.

### 4.2 Properties

- `ProhibitedWordProperties` binds the properties above with `@ConfigurationProperties` and is a record validated at
  startup, because configuration files are external input; a zero `maxOccurrencesPerWord` or threshold fails startup.
- No class is bound with a class-level `@ConfigurationProperties` yet (`ThrottledDataSourceConfig` binds one on a
  `@Bean` method), and no properties scan is configured, so the record is registered explicitly.
- `application.yml` gets the initial values next to the existing `app.semaphore` block.

### 4.3 `ProhibitedWordHolder.inspectProhibitedWord` and `detectProhibitedWord`

- `inspectProhibitedWord` masks as `maskProhibitedWord` does, skipping `LINE_INITIAL` matches, and returns every match,
  `LINE_INITIAL` ones included, so the score counts what the masking leaves visible as well. A null or blank text
  returns the text unchanged with no matches.
- `detectProhibitedWord` scores the matches it gets once, so a caller that pools the matches of several texts (a post's
  title and body) makes them share the per-word cap. Each text is still scanned by its own `inspectProhibitedWord`
  call, so a prohibited word split across the end of one text and the start of the next is not matched.
- With `enabled = false` it returns `ProhibitedWordDetection.disabled()`, whose `isDisabled()` is true, so the step 12
  listener writes and deletes nothing instead of treating the text as clean; masking and nickname checks keep using the
  engine.

---

## 5. Other Implementation Requirements

- When the step is done, the application starts with the new properties, `maskProhibitedWord` masks as before, and
  nothing calls `inspectProhibitedWord` or `detectProhibitedWord` yet.
