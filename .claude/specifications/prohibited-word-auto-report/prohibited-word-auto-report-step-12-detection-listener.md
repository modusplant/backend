# Step 12: Detection Listener

Expands `prohibited-word-auto-report-implementation.md`.

## 1. Purpose

Connects the step 11 events to detection (step 09) and storage (step 10). It is the first step that changes stored data
in response to user content, so it is kept apart from the pieces it joins.

---

## 2. Scope

- **Action**: `MemberEventListener` writes only through `AutoAbuseReportRepository`, so a detection changes the
  auto-report and the auto-detection marker of the dashboard row, and never the post, the comment, or their visibility.

---

## 3. Structure Design

### 3.1 Component Flow

```
Post/CommentProhibitedWordMatchInspectedEvent (matches, possibly empty; createdAt)
 ↓ @Async("memberDomainExecutor") @TransactionalEventListener(AFTER_COMMIT)
MemberEventListener.reflectAutoAbuseReport(event), one overload per event, inside acquireAndProcess
 ↓
ProhibitedWordHolder.detectProhibitedWord(event.matches())
 ├[isDisabled()]→ stop
 └[reportable or clean]→ continue
 ↓ in one transaction, if the target still exists
AutoAbuseReportRepository.reflectPostAutoAbuseReport / reflectCommentAutoAbuseReport (target, detection, createdAt)
```

### 3.2 Package Structure

- `domains/member/adapter/listener/`
  - `MemberEventListener` — existing; the two `reflectAutoAbuseReport` overloads reflect a reportable or clean
    detection with the event's creation time, in one transaction that first checks the target still exists (§4.1)

### 3.3 Step-Specific Changes

#### 3.3.1 Claude Document Changes

| Document | Section | Change |
|---|---|---|
| `documents/policy/prohibited-word-filtering-policy.md` | Header | `detectProhibitedWord`, which scores the matches that `inspectProhibitedWord` returns, joins the entry points |
| `documents/policy/prohibited-word-filtering-policy.md` | §1 | Post title and body (published create and update) and comment content (register and update) are auto-reported by `MemberEventListener` |
| `documents/policy/prohibited-word-filtering-policy.md` | New §2.4 Auto-Report | A target is auto-reported when its score reaches the threshold, with the score and properties stated in the policy's own sentences; the kill switch stops auto-reporting only, writing and deleting nothing, while masking and nickname rejection continue; the auto-report reflect rules, under which a clean re-edit removes the auto-report and auto-reports never count as human reports; nothing is blocked or given Hidden processing |
| `documents/policy/prohibited-word-filtering-policy.md` | New §3.7 Auto-Report (`MemberEventListener`) | After-commit asynchronous handling in the `adminSemaphore` bulkhead, and a timed-out detection dropped without retry |

---

## 4. Technical Details

### 4.1 Handlers

- `MemberEventListener` declares its constructor explicitly, so the new dependencies (`ProhibitedWordHolder`,
  `AutoAbuseReportRepository`, the post and comment existence checks) are added to it by hand.
- The `adminSemaphore` timeout of `acquireAndProcess` is `app.semaphore.datasource.bulkhead.admin.timeout-ms`, and
  `acquireAndProcess` logs a task failure.
- The overloads follow the existing `reflectReportDashboard(PostAbuseReportEvent)` and
  `reflectReportDashboard(CommentAbuseReportEvent)`, which share one name across event types.
- An event with empty matches is detected like any other rather than reflected as clean directly, so the kill switch
  stops it too.
- Detection runs before any transaction, so a disabled detection stops without opening one.
- A reportable or clean detection is dated by the event's creation time, so a late-handled event keeps the time of its
  write. A clean detection is reflected too, so that a re-edit below the threshold or without matches removes
  the earlier auto-report. A missing target writes nothing and is logged.

**From step 09** (§4.3): `detectProhibitedWord` scores the matches it gets, and returns
`ProhibitedWordDetection.disabled()` while the kill switch is off.

**From step 10** (§3.3.1): The reflect rules drop a detection older than the stored auto-report, and a clean detection
deletes the auto-report and an auto-only row.

**From step 11** (§4.1): Each event carries the creation time taken at its publish site and `matches`, pooling a
post's title and body, which is empty only for an update without matches.

---

## 5. Other Implementation Requirements

- When the step is done, a published post with a severe prohibited word creates an auto-detected dashboard row, and a
  post with a single severity-2 prohibited word creates none; re-editing the first post without prohibited words
  deletes its auto-only row.
