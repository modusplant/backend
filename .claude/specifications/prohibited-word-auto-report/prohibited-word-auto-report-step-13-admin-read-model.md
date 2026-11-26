# Step 13: Admin Read Model

Expands `prohibited-word-auto-report-implementation.md`.

## 1. Purpose

Exposes the auto-detection marker on the admin abuse-report dashboards, so an admin can tell auto-detected rows apart
and see when the latest detection ran. It is separate from storage because it changes only the responses of the admin
application programming interface (API), not the stored data.

---

## 2. Scope

- **Admin surface**: the post and comment dashboard read models carry the marker as `isAutoDetected` and
  `autoDetectedAt`, and the admin API keeps its paths, parameters, and filters (§4.1).

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

- `domains/member/framework/outbound/jooq/repository/`
  - `PostAbuseReportDashboardJooqRepository`, `CommentAbuseReportDashboardJooqRepository` — existing;
    `getDashboardFields()` selects the two columns, and `getDashboardReadModelMapper()` maps them after
    `lastReportedAt` (§4.1)

### 3.3 Step-Specific Changes

#### 3.3.1 Claude Document Changes

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
- `autoDetectedAt` is null on a row without auto marking; an auto-only row has `reportCount` 0, null
  `firstReportedAt` and `lastReportedAt`, `isAutoDetected` true, and `autoDetectedAt` set.
- `firstReportedAt` and `lastReportedAt` become nullable in the responses.
- An `isAutoDetected` filter is not part of this design.

**From step 10** (§3.3.1): Both dashboard tables gain `is_auto_detected` and `auto_detected_at`, the latter null
exactly when the former is false, and `first_reported_at` and `last_reported_at` become nullable.

---

## 5. Other Implementation Requirements

- When the step is done, an auto-only row from step 12 is listed locally with `reportCount` 0, null `firstReportedAt`
  and `lastReportedAt`, `isAutoDetected` true, and `autoDetectedAt` set.
