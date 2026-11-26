# Step 14: Data Deletion

## 1. Purpose

Removes auto-report rows wherever the human abuse reports of the same target are removed, so no auto-report outlives
its post or comment. It is separate from storage because it touches the post and member deletion flows and the
data-deletion policy document.

---

## 2. Scope

| Item | Decision |
|---|---|
| Data deletion | Auto-reports are deleted wherever the human abuse reports of the same target are deleted |

---

## 3. Structure Design

### 3.1 Component Flow

```
PostController.deletePost / PostAdminEventListener.deletePost
 └→ PostRepository.deletePostAbuseReportByPostId, deletePostAbuseReportDashboardByPostId, and the auto-report deletion
   └→ PostRepositoryJpaAdapter → member domain's repositories (PostAutoAbuseReportJpaRepository included)
Member withdrawal
 ├→ step 3: ActivitySubjectPostJooqRepository.deleteRelatedRecordsByPostUlids
 │ └→ one dsl.batch: post abuse reports, dashboards, and comm_post_auto_abu_rep
 └→ step 4: ActivitySubjectCommentJooqRepository.deleteRelatedRecordsByMemberIdAndPostUlids
   └→ one dsl.batch: comment abuse reports, dashboards, and comm_comment_auto_abu_rep
```

### 3.2 Package Structure

Packages are relative to `src/main/java/kr/modusplant/`. Classes marked `existing` are changed; every other class is
new.

- `domains/post/adapter/controller/`
  - `PostController` — existing; deletes the auto-report on post deletion
- `domains/post/adapter/listener/`
  - `PostAdminEventListener` — existing; post abuse-report approval also deletes the auto-report
- `domains/post/framework/outbound/jpa/repository/`
  - `PostRepositoryJpaAdapter` — existing; gains the auto-report deletion
- `domains/member/framework/outbound/jooq/repository/`
  - `ActivitySubjectPostJooqRepository` — existing; withdrawal step 3 deletes post auto-reports
  - `ActivitySubjectCommentJooqRepository` — existing; withdrawal step 4 deletes comment auto-reports

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

**Step 14**:

| Document | Section | Change |
|---|---|---|
| `documents/policy/soft-and-hard-data-deletion-policy.md` | §2.2 | The auto-reports are listed with the abuse reports |
| `documents/policy/soft-and-hard-data-deletion-policy.md` | §2.3 | The auto-reports are named in post deletion and withdrawal steps 3 and 4; comment abuse-report approval keeps them |

---

## 4. Technical Details

### 4.1 Deletion Flows

| Flow | Change |
|---|---|
| Post deletion (published) | Delete `comm_post_auto_abu_rep` with the post's abuse reports and dashboard |
| Post abuse-report approval | Same steps as post deletion |
| Comment abuse-report approval | None: `comm_comment_auto_abu_rep` is kept, as `comm_comment_abu_rep` is |
| Member withdrawal step 3 | Add the published posts' auto-reports |
| Member withdrawal step 4 | Add the in-scope comments' auto-reports |

- The new `PostRepository` operation sits next to `deletePostAbuseReportByPostId` and is called at the same sites;
  `PostRepositoryJpaAdapter` implements it with the member domain's Jakarta Persistence API (JPA) repositories, as it
  does for the abuse reports.
- In withdrawal, the auto-report tables join the existing `dsl.batch` deletes with the same conditions as the human
  abuse-report tables, so the rows of other members' targets are kept.
- Draft deletion deletes no auto-report, because drafts are never inspected.
- The auto-report tables have no member column, so withdrawal step 9 (rows keyed by the member) is unaffected.

---

## 5. Other Implementation Requirements

- When the step is done, deleting an auto-reported post and withdrawing a member with auto-reported comments leave no
  auto-report row for those targets locally.
- Every step's result must build and pass its tests, except step 08: its result is a seed migration file, which is
  written only and never compiled, built, or tested, because compilation runs `flywayMigrate` and would apply it to the
  database.

---

## 6. Glossary

- **Auto-report**: A report that the detector writes for admin review while the latest write of its target is
  reportable, stored apart from human abuse reports and never counted in them
- **Published post, draft**: A post visible to every member, or a post visible only to its author
  (`isPublished = false`)
- **Withdrawal step**: One numbered step of the member withdrawal flow in the data-deletion policy
