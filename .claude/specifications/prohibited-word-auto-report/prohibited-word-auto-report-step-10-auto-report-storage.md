# Step 10: Auto-Report Storage

Expands `prohibited-word-auto-report-implementation.md`.

## 1. Purpose

Creates the persistence for auto-reports and the auto-detection marker on the dashboards, with the member-domain port
that step 12 writes through. It is separate from detection so that the schema, entities, and reflect rules can be tested
without the event flow.

---

## 2. Scope

- **Report decision**, the auto-report following the latest write: the reflect rules (§3.3.1) overwrite the auto-report
  on each reportable detection, delete it on a clean one, and drop a detection older than the stored one.

---

## 3. Structure Design

### 3.1 Component Flow

```
AutoAbuseReportRepository.reflectPostAutoAbuseReport / reflectCommentAutoAbuseReport (target, detection, createdAt)
 └→ AutoAbuseReportRepositoryAdapter (reflect rules, caller's transaction)
   ├→ Post/CommentAutoAbuseReportJpaRepository → Post/CommentAutoAbuseReportEntity
   │ (score, MatchedWordEntityRecord list, detectedAt); deleted on a clean detection
   └→ Post/CommentAbuseReportDashboardEntity → auto marking or unmarking (isAutoDetected, autoDetectedAt);
     an auto-only row deleted on a clean detection
```

### 3.2 Package Structure

- `domains/member/framework/outbound/`
  - `AutoAbuseReportRepositoryAdapter` — implements `AutoAbuseReportRepository` with the auto-report and dashboard
    Jakarta Persistence API (JPA) repositories, in the caller's transaction (§4.2)
  - `ReportDashboardRepositoryAdapter` — existing; `reflectPostAbuseReport` and `reflectCommentAbuseReport` set a null
    `firstReportedAt` (§4.1)

### 3.3 Step-Specific Changes

#### 3.3.1 Storage

`schema/V7.5.8__Create_auto_abuse_report_tables.sql` creates the new tables and their indexes, and
`schema/V7.5.9__Add_auto_detection_columns_to_abuse_report_dashboard.sql` makes the dashboard changes; both take the
next free versions after `V7.5.7`.

New tables (one row per target while its latest write is reportable, overwritten by each reportable detection and
deleted by a clean one, dated by the event's `createdAt`):

| Table | Key | Columns |
|---|---|---|
| `comm_post_auto_abu_rep` | `post_ulid` | `score integer`, `matched_words jsonb`, `detected_at timestamp` |
| `comm_comment_auto_abu_rep` | `post_ulid`, `path` | same as above |

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

---

## 4. Technical Details

### 4.1 Dashboard Entities

- `PostAbuseReportDashboardEntity` and `CommentAbuseReportDashboardEntity` default `reportCount` to 1 and `status` to
  `UNCHECKED` in `prePersist`, so the adapter sets `reportCount = 0` explicitly on an auto-only row.
- `prePersist` defaults `isAutoDetected` to `false`, so the human-report path of
  `ReportDashboardRepository.reflectPostAbuseReport` and `reflectCommentAbuseReport` keeps working unchanged.
- The auto marking and the auto unmarking each set `isAutoDetected` and `autoDetectedAt` together, so `autoDetectedAt`
  is null exactly when `isAutoDetected` is false.
- `ReportDashboardRepositoryAdapter.reflectPostAbuseReport` and `reflectCommentAbuseReport` set `firstReportedAt` on an
  existing row whose `firstReportedAt` is null, next to the existing `report_count` increment and `lastReportedAt`
  update, so the first human report of an auto-only row is dated like that of a new row.
- Both entities carry `@Version`, and `getETagSource()` is built from that version, so an auto marking or unmarking
  changes the entity tag (ETag) that the admin's next dismiss or approve must match, and a deleted auto-only row makes
  them fail as not found.

### 4.2 Auto-Report Entities and Port

- Each auto-report entity overwrites its detection (score, matched words, detection time) in place, so one row per
  target holds the latest reportable detection.
- `CommentAutoAbuseReportEntity` is keyed by `CommentAutoAbuseReportCompositeKey` (`post_ulid`, `path`).
- `AutoAbuseReportRepository` has one reflect operation per target, taking the target identifiers, a reportable or
  clean `ProhibitedWordDetection`, and the detection time; the adapter compares that time with the stored
  `detected_at` to apply the first reflect rule.

**From step 09** (§3.2): `ProhibitedWordDetection` holds the matches and their score, and exposes `isReportable()` and
`isDisabled()`.

---

## 5. Other Implementation Requirements

- When the step is done, `V7.5.8` and `V7.5.9` apply locally, no `is_auto_detected` is null, and `first_reported_at`
  and `last_reported_at` are nullable.
