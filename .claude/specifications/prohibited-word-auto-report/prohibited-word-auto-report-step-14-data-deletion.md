# Step 14: Data Deletion

Expands `prohibited-word-auto-report-implementation.md`.

## 1. Purpose

Removes auto-report rows in every flow that hard-deletes their post or comment, so no auto-report outlives its target.
It is separate from storage because it touches the post and member deletion flows and the data-deletion policy
document.

---

## 2. Scope

- **Data deletion**: the auto-report tables join the deletes of post deletion, post abuse-report approval, and
  withdrawal steps 3 and 4, and comment abuse-report approval keeps them, because it only soft-deletes the comment
  (§4.1).

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

### 3.3 Step-Specific Changes

#### 3.3.1 Claude Document Changes

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
| Comment abuse-report approval | None: `comm_comment_auto_abu_rep` is kept, because the comment is only soft-deleted |
| Member withdrawal step 3 | Add the published posts' auto-reports |
| Member withdrawal step 4 | Add the in-scope comments' auto-reports |

- The new `PostRepository` operation sits next to `deletePostAbuseReportByPostId` and is called at the same sites;
  `PostRepositoryJpaAdapter` implements it with the member domain's Jakarta Persistence API (JPA) repositories, as it
  does for the abuse reports.
- In withdrawal, the auto-report tables join the existing `dsl.batch` deletes of
  `ActivitySubjectPostJooqRepository.deleteRelatedRecordsByPostUlids` and
  `ActivitySubjectCommentJooqRepository.deleteRelatedRecordsByMemberIdAndPostUlids`, with the same conditions as the
  human abuse-report tables, so the rows of other members' targets are kept.
- Draft deletion deletes no auto-report, because drafts are never inspected.
- Withdrawal step 9 (rows keyed by the member) is unaffected, because the auto-report tables have no member column.

**From step 10** (§3.3.1): `comm_post_auto_abu_rep` is keyed by `post_ulid`, and `comm_comment_auto_abu_rep` by
`post_ulid` and `path`.

---

## 5. Other Implementation Requirements

- When the step is done, deleting an auto-reported post and withdrawing a member with auto-reported comments leave no
  auto-report row for those targets locally.
