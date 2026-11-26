# Prohibited Word Auto-Report Implementation

---

## 1. Purpose

Automatically report prohibited words in posts and comments to admins through the existing abuse-report dashboards,
built on the existing `ProhibitedWordEngine`. The same engine also masks published post titles and bodies.

---

## 2. Scope

| Item | Decision |
|---|---|
| Targets | Published post title and body (create and update); comment content (register and update) |
| Action | Flag for admin review only; nothing is blocked or given Hidden processing automatically |
| Report decision | A text is auto-reported when the severity-weighted score of its matches reaches a configurable threshold; the auto-report follows the latest write, so a re-edit below the threshold removes it |
| Masking | Published post title and body (create and update) are masked |
| Admin surface | The existing post/comment abuse-report dashboards, with an auto-detection marker |
| Data deletion | Auto-reports are deleted wherever the human abuse reports of the same target are deleted |
| Cost | No new container, no external application programming interface (API), no machine learning (ML) runtime, and no new library |

Non-goals: spam detection, image/video moderation, profile introduction auto-reporting, admin notifications.

---

## 3. Structure Design

### 3.1 End-to-End Flow

From a published write to an auto-detected dashboard row:

```
PostController.createPost/updatePost (published) ┐
CommentController.register/updateContent ────────┴→ PostAutoAbuseInspectionEvent.create / CommentAutoAbuseInspectionEvent.create
 │ (raw text before masking, createdAt)
 ↓ @Async("memberDomainExecutor") @TransactionalEventListener(AFTER_COMMIT)
MemberEventListener.inspectPost / inspectComment, inside acquireAndProcess
 ↓
ProhibitedWordHolder.detectProhibitedWord(title, contentText) / detectProhibitedWord(content)
 └→ ProhibitedWordEngine.match per text → ProhibitedWordDetection.of(matches, ProhibitedWordProperties)
   # score; disabled() when disabled
 ↓
ProhibitedWordDetection.isDisabled() true → stop
 ↓ reportable or clean: in one transaction, if the target still exists
AutoAbuseReportRepository.reflectPostAutoAbuseReport / reflectCommentAutoAbuseReport (target, detection, createdAt)
 └→ AutoAbuseReportRepositoryAdapter (reflect rules)
   ├→ reportable: insert or overwrite Post/CommentAutoAbuseReportEntity; clean: delete it
   └→ Post/CommentAbuseReportDashboardEntity: reportable → auto marking (insert an auto-only row if none);
     clean → delete an auto-only row, or auto unmarking (human report fields never touched)
```

- Detection runs after the write commits, so the write path gains no latency and a detection failure never rolls back a
  user's post or comment.
- The event carries the raw text, because published posts and comments are masked by `maskProhibitedWord` before they
  are saved, and a masked text can no longer be scored.
- The handlers are added to `MemberEventListener` and run inside its private `acquireAndProcess` bulkhead, so
  auto-reporting shares the `adminSemaphore` and its timeout; a separate listener class could not reach that method.
  A detection skipped by a bulkhead timeout is dropped with a warning log and not retried, the same as every other task
  behind that bulkhead.

### 3.2 Package Structure

Packages are relative to `src/main/java/kr/modusplant/`. Classes marked `existing` are changed; every other class is
new.

- `infrastructure/prohibitedword/`
  - `ProhibitedWordHolder` — existing; gains `detectProhibitedWord`
  - `ProhibitedWordProperties` — `app.moderation.prohibited-word` binding: kill switch, threshold, per-word cap
  - `ProhibitedWordDetection` — The matches of one inspection, their score, and the auto-report decision
- `domains/post/adapter/controller/`
  - `PostController` — existing; masks a published post's title and body and publishes `PostAutoAbuseInspectionEvent`
    after a published create or update; deletes the auto-report on post deletion
- `domains/post/adapter/listener/`
  - `PostAdminEventListener` — existing; post abuse-report approval also deletes the auto-report
- `domains/post/framework/outbound/jpa/repository/`
  - `PostRepositoryJpaAdapter` — existing; `save` returns `PostId`; gains the auto-report deletion
- `domains/comment/adapter/controller/`
  - `CommentController` — existing; publishes `CommentAutoAbuseInspectionEvent` after register and update
- `domains/member/adapter/listener/`
  - `MemberEventListener` — existing; runs detection for both events and reflects results that are not disabled
- `domains/member/framework/outbound/`
  - `AutoAbuseReportRepositoryAdapter` — Applies the auto-report reflect rules
  - `ReportDashboardRepositoryAdapter` — existing; a human report on an auto-only row sets `first_reported_at`
- `domains/member/framework/outbound/jooq/repository/`
  - `PostAbuseReportDashboardJooqRepository`, `CommentAbuseReportDashboardJooqRepository` — existing; select and map
    the two new dashboard columns
  - `ActivitySubjectPostJooqRepository` — existing; withdrawal step 3 deletes post auto-reports
  - `ActivitySubjectCommentJooqRepository` — existing; withdrawal step 4 deletes comment auto-reports

- The member domain owns abuse reports and dashboards today, so it also owns auto-reports. The post and comment domains
  only publish events, as they already do for `CommentRegisterEvent`.

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

#### 3.4.1 Storage

**Step 10**: `schema/V7.5.8__Create_auto_abuse_report_tables.sql` creates the new tables and their indexes, and
`schema/V7.5.9__Add_auto_detection_columns_to_abuse_report_dashboard.sql` makes the dashboard changes; both take the
next free versions after `V7.5.7`.

New tables (one row per target while its latest write is reportable, overwritten by each reportable detection and
deleted by a clean one, dated by the event's `createdAt`):

| Table | Key | Columns |
|---|---|---|
| `comm_post_auto_abu_rep` | `post_ulid` | `reason varchar(20)`, `score integer`, `matched_words jsonb`, `detected_at timestamp` |
| `comm_comment_auto_abu_rep` | `post_ulid`, `path` | same as above |

- `reason` holds an `AutoAbuseReportReason`, `PROHIBITED_WORD` now, and leaves room for `SPAM` and `MEDIA` later
  without a schema change.
- `matched_words` stores one `MatchedWordEntityRecord` per matched prohibited word (normalized form, `type`, count),
  never the user's raw text.

Dashboard changes (`comm_post_abuse_report_dashboard`, `comm_comment_abuse_report_dashboard`):

| Column | Purpose |
|---|---|
| `is_auto_detected boolean` | Whether the latest detection of the target is reportable |
| `auto_detected_at timestamp` (nullable) | Time of the latest reportable detection, null exactly when `is_auto_detected` is false |
| `first_reported_at`, `last_reported_at` (existing) | Become nullable; human report times only, null on an auto-only row |

`is_auto_detected` is added as nullable, backfilled with `UPDATE ... SET is_auto_detected = false`, then set to
`NOT NULL`, with no `DEFAULT`. `first_reported_at` and `last_reported_at` drop `NOT NULL`, so the detector never writes
a human report field.

Auto-reports are kept apart from human reports: the detector writes only the auto-report table, `is_auto_detected`,
and `auto_detected_at`, and never changes `report_count`, `first_reported_at`, or `last_reported_at`. The dashboard row
and its status stay shared, so an admin reviews one row per target.

Reflect rules of one detection that is not disabled, run in the caller's transaction:

| Detection | Stored state | Write |
|---|---|---|
| Any | Auto-report row detected later than the incoming detection | Nothing, because the handlers finish out of order and an older edit must not overwrite a newer one |
| Reportable | Any other auto-report state | The auto-report row is inserted or overwritten |
| Reportable | No dashboard row | Inserted with `report_count = 0`, `UNCHECKED`, null `first_reported_at` / `last_reported_at`, `is_auto_detected = true`, and `auto_detected_at` at the detection time; a later human report increments `report_count` as it does today and also sets `first_reported_at` |
| Reportable | Dashboard row in `UNCHECKED` | Marked auto-detected; the human report fields unchanged |
| Reportable | Dashboard row in `DISMISSED` or `BLINDED` | Marked auto-detected; the status stays, because no policy defines re-reporting edited content, so the admin's decision stands |
| Clean | No auto-report row | Nothing |
| Clean | Auto-report row | Deleted |
| Clean | Dashboard row with `report_count = 0` | Deleted, in any status, because nothing is left to review |
| Clean | Dashboard row with `report_count > 0` | Unmarked: `is_auto_detected = false`, `auto_detected_at = null`; the status and the human report fields unchanged |

- A clean detection deletes the auto-report row, so an older reportable detection that finishes after it finds no row
  to compare with and is reflected; this gap is accepted with the other concurrency gaps of the flow.

#### 3.4.2 Claude Document Changes

**Step 11**:

| Document | Section | Change |
|---|---|---|
| `documents/policy/prohibited-word-filtering-policy.md` | §1 | Post title and body (published create and update) join the table as masked by `PostController`; the closing sentence states that draft post titles and contents are stored as submitted |
| `documents/policy/prohibited-word-filtering-policy.md` | §3.1 | Register and content update publish `CommentAutoAbuseInspectionEvent` with the raw content |
| `documents/policy/prohibited-word-filtering-policy.md` | New §3.5 Post (`PostController`), before Stored Nicknames, which becomes §3.6 | Published create and update mask the title and body with `maskProhibitedWord` before the save and publish `PostAutoAbuseInspectionEvent` with the raw title and body after the save; drafts are not masked and publish nothing |

**Step 12**:

| Document | Section | Change |
|---|---|---|
| `documents/policy/prohibited-word-filtering-policy.md` | Header | `detectProhibitedWord` joins `maskProhibitedWord` and `NicknameValidator` as an entry point |
| `documents/policy/prohibited-word-filtering-policy.md` | §1 | Post title and body (published create and update) and comment content (register and update) are auto-reported by `MemberEventListener` |
| `documents/policy/prohibited-word-filtering-policy.md` | New §2.4 Auto-Report | A target is auto-reported when its score reaches the threshold, with the score and properties stated in the policy's own sentences; the kill switch stops auto-reporting only, writing and deleting nothing, while masking and nickname rejection continue; the auto-report reflect rules, under which a clean re-edit removes the auto-report and auto-reports never count as human reports; nothing is blocked or given Hidden processing |
| `documents/policy/prohibited-word-filtering-policy.md` | New §3.7 Auto-Report (`MemberEventListener`) | After-commit asynchronous handling in the `adminSemaphore` bulkhead, and a timed-out detection dropped without retry |

**Step 13**:

| Document | Section | Change |
|---|---|---|
| `documents/policy/prohibited-word-filtering-policy.md` | §2.4 | The post and comment abuse-report dashboard responses carry `isAutoDetected` and `autoDetectedAt`, the latter null on a row the detector has not marked; `firstReportedAt` and `lastReportedAt` are null on an auto-only row |

**Step 14**:

| Document | Section | Change |
|---|---|---|
| `documents/policy/soft-and-hard-data-deletion-policy.md` | §2.2 | The auto-reports are listed with the abuse reports |
| `documents/policy/soft-and-hard-data-deletion-policy.md` | §2.3 | The auto-reports are named in post deletion and withdrawal steps 3 and 4; comment abuse-report approval keeps them |

---

## 4. Technical Details

### 4.2 Scoring

```
score = Σ over distinct matched words ( severity × min(occurrences, maxOccurrencesPerWord) )
auto-report when score ≥ threshold
```

| Property | Initial value | Meaning |
|---|---|---|
| `app.moderation.prohibited-word.auto-report-threshold` | `3` | Minimum score to auto-report |
| `app.moderation.prohibited-word.max-occurrences-per-word` | `3` | Repetitions of one prohibited word counted at most this many times |
| `app.moderation.prohibited-word.enabled` | `true` | Kill switch for detection (masking stays on) |

- The values are bound with `@ConfigurationProperties`. The initial values follow the common pattern for a 1–3 severity
  scale: one severe word, a general word with any second occurrence, or three mild-word occurrences reports; a single
  general word or at most two mild-word occurrences only masks. They are pending review.
- Occurrences are counted after the engine's deduplication. A post's title and body are scanned separately and scored
  together, because joining them would create matches across the seam.

### 4.3 Event Publish Sites

| Site | Change |
|---|---|
| `PostController.createPost` | When `isPublished`, mask `title` and `contentText` with `maskProhibitedWord` before building the post, and publish `PostAutoAbuseInspectionEvent` with the raw `title` and `contentText` from the request after `postRepository.save`, which returns the saved `PostId` because the universally unique lexicographically sortable identifier (ULID) is generated on persist |
| `PostController.updatePost` | Same, when `isPublished` |
| `CommentController.register` | Publish `CommentAutoAbuseInspectionEvent` with `request.content()` (raw) next to `CommentRegisterEvent` |
| `CommentController.updateContent` | Publish `CommentAutoAbuseInspectionEvent` with the raw content |

`PostAutoAbuseInspectionEvent` carries `postUlid`, `title`, `contentText`, and `createdAt`, and
`CommentAutoAbuseInspectionEvent` carries `postUlid`, `path`, the raw content, and `createdAt`. Their text fields may be
blank (a title-only post), so their `create(...)` validates only identifiers and the creation time.

Drafts are neither masked nor inspected: they are visible only to their author. A published post cannot return to
draft, so an inspected post never leaves the inspected scope.

### 4.4 Data Deletion Flows

The auto-report tables are deleted wherever the human abuse reports of the same target are deleted:

| Flow | Change |
|---|---|
| Post deletion (published) | Delete `comm_post_auto_abu_rep` with the post's abuse reports and dashboard |
| Post abuse-report approval | Same steps as post deletion |
| Comment abuse-report approval | None: `comm_comment_auto_abu_rep` is kept, as `comm_comment_abu_rep` is |
| Member withdrawal step 3 | Add the published posts' auto-reports |
| Member withdrawal step 4 | Add the in-scope comments' auto-reports |

The repository methods next to `PostRepository.deletePostAbuseReportByPostId` gain auto-report counterparts.

---

## 5. Implementation Steps

| Step | Document | Purpose | Depends on |
|---|---|---|---|
| 09 | `prohibited-word-auto-report-step-09-scoring-and-properties.md` | Score, threshold, and kill switch | — |
| 10 | `prohibited-word-auto-report-step-10-auto-report-storage.md` | Auto-report tables, dashboard columns, entities, repository port and adapter | 09 |
| 11 | `prohibited-word-auto-report-step-11-event-publish-sites.md` | Published post masking, inspection events, and their publish sites | — |
| 12 | `prohibited-word-auto-report-step-12-detection-listener.md` | Handlers that detect and write | 09, 10, 11 |
| 13 | `prohibited-word-auto-report-step-13-admin-read-model.md` | Dashboard read models | 10, 12 |
| 14 | `prohibited-word-auto-report-step-14-data-deletion.md` | Auto-report rows in every deletion flow | 10, 12 |

---

## 6. Constraints

- Every step's result must build and pass its tests.

---

## 7. Reference Documents

| Document | Content |
|---|---|
| `prohibited-word-auto-report-reference-obfuscation-coverage.md` | Taxonomy of obfuscation (OB) techniques (OB-01–OB-44) and how the engine handles each |

---

## 8. Glossary

- **Engine**: `ProhibitedWordEngine`, which matches an original text against the dictionary
- **Prohibited word**: A word that is masked, rejected in a nickname, and scored, stored in `prohibited_word`: a
  profanity, a sexual term, or a non-verbal string such as an emoji
- **Seed migration**: A Flyway migration under `data/seed/` that writes dictionary rows
- **Published post, draft**: A post visible to every member, or a post visible only to its author
  (`isPublished = false`)
- **Raw content**: The text as the request carries it, before `maskProhibitedWord` masks it
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
- **Auto marking, auto unmarking**: Setting `isAutoDetected` to true and `autoDetectedAt` to the detection time on a
  dashboard row, or `isAutoDetected` to false and `autoDetectedAt` to null
- **Bulkhead**: A semaphore that caps how many tasks of one kind run at once, so they cannot exhaust the shared
  connection pool
- **Hidden processing**: Making a whole post or comment invisible to other members, as distinct from masking, which
  covers only the matched characters
- **Withdrawal step**: One numbered step of the member withdrawal flow in the data-deletion policy
- **OB ID**: The ID of an evasion technique in `prohibited-word-auto-report-reference-obfuscation-coverage.md`
