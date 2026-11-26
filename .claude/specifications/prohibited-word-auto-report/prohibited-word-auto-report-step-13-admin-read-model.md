# Step 13: Admin Read Model

## 1. Purpose

Exposes the auto-detection marker on the admin abuse-report dashboards, so an admin can tell auto-detected rows apart
and see when the latest detection ran. It is separate from storage because it changes only the responses of the admin
application programming interface (API), not the stored data.

---

## 2. Scope

| Item | Decision |
|---|---|
| Admin surface | The existing post/comment abuse-report dashboards, with an auto-detection marker |

---

## 3. Structure Design

### 3.1 Component Flow

```
MemberAdminRestController (dashboard list, dismiss, approve)
 └→ Post/CommentAbuseReportDashboardJooqRepository
   ├→ getDashboardFields() # selects is_auto_detected, auto_detected_at
   └→ getDashboardReadModelMapper() → Post/CommentAbuseReportDashboardReadModel (isAutoDetected, autoDetectedAt)
     └→ Post/CommentAbuseReportDashboardResponse (list) or the single read model
```

### 3.2 Package Structure

Packages are relative to `src/main/java/kr/modusplant/`. Classes marked `existing` are changed; every other class is
new.

- `domains/member/framework/outbound/jooq/repository/`
  - `PostAbuseReportDashboardJooqRepository`, `CommentAbuseReportDashboardJooqRepository` — existing; select and map
    the two new dashboard columns

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

**Step 13**:

| Document | Section | Change |
|---|---|---|
| `documents/policy/prohibited-word-filtering-policy.md` | §2.4 | The post and comment abuse-report dashboard responses carry `isAutoDetected` and `autoDetectedAt`, the latter null on a row the detector has not marked; `firstReportedAt` and `lastReportedAt` are null on an auto-only row |

---

## 4. Technical Details

### 4.1 Read Models

- Both read models are records without `@Schema`, built only by the two `*AbuseReportDashboardJooqRepository` classes;
  they gain `isAutoDetected` and `autoDetectedAt` after `lastReportedAt`.
- `PostAbuseReportDashboardResponse` and `CommentAbuseReportDashboardResponse` carry the read models as a list, and the
  dismiss and approve endpoints of `MemberAdminRestController` return a single read model, so every response gains the
  fields without a change of its own.
- `autoDetectedAt` is null on a row that is not marked; an auto-only row has `reportCount` 0, null `firstReportedAt`
  and `lastReportedAt`, `isAutoDetected` true, and `autoDetectedAt` set.
- `firstReportedAt` and `lastReportedAt` become nullable in the responses.
- The admin API keeps its paths, parameters, and filters; an `isAutoDetected` filter is not part of this design.

---

## 5. Other Implementation Requirements

- When the step is done, an auto-only row from step 12 is listed locally with `reportCount` 0, null `firstReportedAt`
  and `lastReportedAt`, `isAutoDetected` true, and `autoDetectedAt` set.
- Every step's result must build and pass its tests.

---

## 6. Glossary

- **Dashboard row**: One row of `comm_post_abuse_report_dashboard` or `comm_comment_abuse_report_dashboard`, which the
  admin reviews per target
- **Auto-only row**: A dashboard row without human reports: `report_count = 0`, null `first_reported_at` and
  `last_reported_at`
- **Marked row**: A dashboard row whose latest detection is reportable
