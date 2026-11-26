# Step 12: Detection Listener

## 1. Purpose

Connects the step 11 events to detection (step 09) and storage (step 10). It is the first step that changes stored data
in response to user content, so it is kept apart from the pieces it joins.

---

## 2. Scope

| Item | Decision |
|---|---|
| Targets | Published post title and body (create and update); comment content (register and update) |
| Action | Flag for admin review only; nothing is blocked or given Hidden processing automatically |
| Report decision | A text is auto-reported when the severity-weighted score of its matches reaches a configurable threshold; the auto-report follows the latest write, so a re-edit below the threshold removes it |

---

## 3. Structure Design

### 3.1 Component Flow

```
PostAutoAbuseInspectionEvent / CommentAutoAbuseInspectionEvent (raw text, createdAt)
 ↓ @Async("memberDomainExecutor") @TransactionalEventListener(AFTER_COMMIT)
MemberEventListener.inspectPost / inspectComment, inside acquireAndProcess
 ↓
ProhibitedWordHolder.detectProhibitedWord(title, contentText) / detectProhibitedWord(content)
 ↓
ProhibitedWordDetection.isDisabled() true → stop
 ↓ reportable or clean: in one transaction, if the target still exists
AutoAbuseReportRepository.reflectPostAutoAbuseReport / reflectCommentAutoAbuseReport (target, detection, createdAt)
```

### 3.2 Package Structure

Packages are relative to `src/main/java/kr/modusplant/`. Classes marked `existing` are changed; every other class is
new.

- `domains/member/adapter/listener/`
  - `MemberEventListener` — existing; runs detection for both events and reflects results that are not disabled

### 3.3 Implementation-Wide Changes

#### 3.3.1 Claude Document Changes

Every step's Claude document changes follow these rules:
- Paths are relative to `.claude/`.
- `documents/policy/prohibited-word-filtering-policy.md` holds the prohibited-word policy across domains, and states
  every implementation fact of `infrastructure/prohibitedword/` it needs in its own sentences, without naming the rule
  that covers it, because a parent document does not depend on its children.
- The policy uses a term this implementation coins only after its Glossary section defines it, because a coined term
  needs its own definition in every document that uses it.

### 3.4 Step-Specific Changes

#### 3.4.1 Claude Document Changes

**Step 12**:

| Document | Section | Change |
|---|---|---|
| `documents/policy/prohibited-word-filtering-policy.md` | Header | `detectProhibitedWord` joins `maskProhibitedWord` and `NicknameValidator` as an entry point |
| `documents/policy/prohibited-word-filtering-policy.md` | §1 | Post title and body (published create and update) and comment content (register and update) are auto-reported by `MemberEventListener` |
| `documents/policy/prohibited-word-filtering-policy.md` | New §2.4 Auto-Report | A target is auto-reported when its score reaches the threshold, with the score and properties stated in the policy's own sentences; the kill switch stops auto-reporting only, writing and deleting nothing, while masking and nickname rejection continue; the auto-report reflect rules, under which a clean re-edit removes the auto-report and auto-reports never count as human reports; nothing is blocked or given Hidden processing |
| `documents/policy/prohibited-word-filtering-policy.md` | New §3.7 Auto-Report (`MemberEventListener`) | After-commit asynchronous handling in the `adminSemaphore` bulkhead, and a timed-out detection dropped without retry |

---

## 4. Technical Details

### 4.1 Handlers

- Detection runs after the write commits, so the write path gains no latency and a detection failure never rolls back a
  user's post or comment.
- The handlers are added to `MemberEventListener` and run inside its private `acquireAndProcess` bulkhead, so
  auto-reporting shares the `adminSemaphore` and its timeout; a separate listener class could not reach that method.
  A detection skipped by a bulkhead timeout is dropped with a warning log and not retried, the same as every other task
  behind that bulkhead.
- `MemberEventListener` declares its constructor explicitly, so the new dependencies (`ProhibitedWordHolder`,
  `AutoAbuseReportRepository`, the post and comment existence checks) are added to it by hand.
- The `adminSemaphore` timeout of `acquireAndProcess` is `app.semaphore.datasource.bulkhead.admin.timeout-ms`, and
  `acquireAndProcess` logs a task failure.
- Detection runs before any transaction, so a disabled detection stops without opening one.
- A reportable or clean detection is dated by the event's creation time, so a late-handled event keeps the time of its
  write and the storage reflect rules can drop it as stale. A clean detection is reflected too, so that a re-edit
  without prohibited words removes the earlier auto-report. A missing target writes nothing and is logged.

---

## 5. Other Implementation Requirements

- When the step is done, a published post with a severe prohibited word creates an auto-detected dashboard row, and a
  post with a single severity-2 prohibited word creates none; re-editing the first post without prohibited words
  deletes its auto-only row.
- Every step's result must build and pass its tests, except step 08: its result is a seed migration file, which is
  written only and never compiled, built, or tested, because compilation runs `flywayMigrate` and would apply it to the
  database.

---

## 6. Glossary

- **Inspection event**: `PostAutoAbuseInspectionEvent` or `CommentAutoAbuseInspectionEvent`, which hands a written text
  to detection
- **Detection**: A `ProhibitedWordDetection`: the matches of one inspection, their score, and the auto-report decision
- **Reportable detection, clean detection**: A detection whose score reaches the auto-report threshold, or one that is
  neither reportable nor disabled
- **Auto-report**: A report that the detector writes for admin review while the latest write of its target is
  reportable, stored apart from human abuse reports and never counted in them
- **Dashboard row**: One row of `comm_post_abuse_report_dashboard` or `comm_comment_abuse_report_dashboard`, which the
  admin reviews per target
- **Auto-only row**: A dashboard row without human reports: `report_count = 0`, null `first_reported_at` and
  `last_reported_at`
- **Bulkhead**: A semaphore that caps how many tasks of one kind run at once, so they cannot exhaust the shared
  connection pool
- **Hidden processing**: Making a whole post or comment invisible to other members, as distinct from masking, which
  covers only the matched characters
