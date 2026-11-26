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
   ├→ reportable: auto-report row written, dashboard row given auto marking
   └→ clean: auto-report row deleted, dashboard row given auto unmarking or deleted if auto-only
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

---

## 4. Implementation Steps

| Step | Document | Purpose | Depends on |
|---|---|---|---|
| 09 | `prohibited-word-auto-report-step-09-scoring-and-properties.md` | Score, threshold, and kill switch | — |
| 10 | `prohibited-word-auto-report-step-10-auto-report-storage.md` | Auto-report tables, dashboard columns, entities, repository port and adapter | 09 |
| 11 | `prohibited-word-auto-report-step-11-event-publish-sites.md` | Published post masking, inspection events, and their publish sites | — |
| 12 | `prohibited-word-auto-report-step-12-detection-listener.md` | Handlers that detect and write | 09, 10, 11 |
| 13 | `prohibited-word-auto-report-step-13-admin-read-model.md` | Dashboard read models | 10, 12 |
| 14 | `prohibited-word-auto-report-step-14-data-deletion.md` | Auto-report rows in every deletion flow | 10, 12 |

---

## 5. Constraints

- Every step's result must build and pass its tests.

---

## 6. Reference Documents

| Document | Content |
|---|---|
| `prohibited-word-auto-report-reference-obfuscation-coverage.md` | Taxonomy of obfuscation (OB) techniques (OB-01–OB-44) and how the engine handles each |

---

## 7. Glossary

- **Engine**: `ProhibitedWordEngine`, which matches an original text against the dictionary
- **Prohibited word**: A word that is masked, rejected in a nickname, and scored, stored in `prohibited_word`: a
  profanity, a sexual term, or a non-verbal string such as an emoji
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
