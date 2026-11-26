# Step 09: Scoring and Properties

Expands `prohibited-word-auto-report-implementation.md`.

## 1. Purpose

Turns the engine's matches into a score and an auto-report decision, and binds the threshold, cap, and kill switch. It
is a separate step because masking and nickname rejection use matches without a score, while only content
auto-reporting uses the score. No flow calls it until step 12.

---

## 2. Scope

- **Report decision**, the score against a configurable threshold: `ProhibitedWordDetection` computes the score and the
  decision (§4.1), and `ProhibitedWordProperties` binds the threshold, the per-word cap, and the kill switch (§4.2).

---

## 3. Structure Design

### 3.1 Component Flow

```
ProhibitedWordProperties ← app.moderation.prohibited-word (application.yml), validated at startup
ProhibitedWordHolder.detectProhibitedWord(texts...)
 ├→ enabled = false → ProhibitedWordDetection.disabled() (isDisabled() true: neither reportable nor clean)
 ├→ ProhibitedWordEngine.match per text (texts never joined)
 └→ ProhibitedWordDetection.of(pooled matches, ProhibitedWordProperties) → score, isReportable()
```

### 3.2 Package Structure

- `infrastructure/prohibitedword/`
  - `ProhibitedWordHolder` — existing; `detectProhibitedWord(texts...)` scans each text with the engine and returns
    one detection for the pooled matches (§4.3)
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

### 4.3 `ProhibitedWordHolder.detectProhibitedWord`

- Each text is scanned separately and the pooled matches are scored once, so a post's title and body share the
  per-word cap, and a prohibited word split across the end of one text and the start of the next is not matched,
  because joining them would create matches across the seam.
- With `enabled = false` it returns `ProhibitedWordDetection.disabled()`, whose `isDisabled()` is true, so the step 12
  listener writes and deletes nothing instead of treating the text as clean; masking and nickname checks keep using the
  engine.

---

## 5. Other Implementation Requirements

- When the step is done, the application starts with the new properties, and nothing calls `detectProhibitedWord` yet.
