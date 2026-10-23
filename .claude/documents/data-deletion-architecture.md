# Data Deletion Architecture

Architecture reference for how persisted data is deleted across domains; consumed on demand.

Applies to every application flow that hard-deletes, soft-deletes, or archives PostgreSQL rows, to the Amazon S3 objects
and Redis keys removed in the same flow, and to the cascade rules declared at the database and JPA levels.

---

## 1. Delete Types

| Data                         | Delete type | Effect                                     |
|------------------------------|-------------|--------------------------------------------|
| comment (`comm_comment`)     | soft-delete | `is_deleted` set to `true`; the row stays  |
| every other table            | hard-delete | the row is removed                         |

Member withdrawal (`MemberController.withdraw()` → `MemberRepository.withdraw()`, implemented by
`MemberRepositoryAdapter.withdraw()`) hard-deletes all member-related data in one transaction, including the member's
comments regardless of their soft-delete state.

---

## 2. Hard-Delete Policy

### 2.1 Archived Data

Archiving copies the row into its archive table, stamped with `archived_at`, before the row is hard-deleted.

| Source         | Archive                | Archived by                                                                                                                            |
|----------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| `comm_post`    | `comm_post_archive`    | post deletion (`PostController.deletePost`) and member withdrawal; published posts only                                                |
| `comm_comment` | `comm_comment_archive` | member withdrawal only; the member's comments and every comment in the member's published posts, with `is_deleted` copied as it stands |
| `prop_bug_rep` | `prop_bug_rep_archive` | proposal/bug report removal (`MemberAdminController.removeProposalOrBug`); checked reports only                                        |

On member withdrawal, the member identifier is set to `null` in `comm_post_archive`, `comm_comment_archive`,
`prop_bug_rep_archive`, and the live `prop_bug_rep`; those rows are kept.

### 2.2 Non-Archived Data

Every other hard-deleted row is removed without a copy, including:

- draft posts
- post and comment likes, post bookmarks
- post and comment abuse reports and their dashboards
- notifications
- member profile, member terms, member auth, and member
- refresh tokens and FCM tokens (`refresh_token`, `fcm_token`)
- plant spaces, plant information, and plant information photos
- pending-file records (`pending_file`)

### 2.3 Related-Data Handling per Flow

**Post deletion** (`PostController.deletePost`):
- Published post: archived, then its likes, bookmarks, and Redis recently-viewed entries are deleted.
- Draft and published post: its content's S3 files are deleted synchronously (`ContentDataProcessorPort.deleteFiles`), then the post row is deleted.

**Member withdrawal** (`MemberRepositoryAdapter.withdraw()`), in this order, where "comments in scope" means the
member's comments plus every comment in the member's published posts:
1. S3 objects (every post image, plant information photo, profile image) and Redis keys (other members' recently-viewed entries and view counts of the published posts; the member's recently-viewed entries and search history) are removed by events handled after the transaction commits.
2. Notifications whose actor or recipient is the member, that target one of the member's published posts, or that target one of the member's comments are deleted; this runs before posts and comments are deleted, because the comment match is a subquery on `comm_comment`.
3. Likes, bookmarks, abuse reports, and dashboards of the published posts are deleted, then the published posts are archived.
4. Likes, abuse reports, and dashboards of the comments in scope are deleted, then those comments are archived.
5. The comments in scope are deleted, then every post (published and draft) of the member.
6. `like_count` is decreased on every post and comment the member liked.
7. The withdrawal reason and opinion are saved to `site_member_withdraw`.
8. Archive and report member identifiers are set to `null` (2.1).
9. The remaining member rows are deleted: `refresh_token`, `comm_post_like`, `comm_comment_like`, `comm_post_bookmark`, `comm_post_abu_rep`, `comm_comment_abu_rep`, `site_member_prof`, `site_member_term`, `site_member_auth`, `site_member`, `fcm_token`, `plant_space`, `plant_info` (`plant_info_photo` by cascade, section 4).

**Proposal/bug report removal** (`ReportRepositoryAdapter.removeProposalOrBugReport`): image S3 objects are removed by an
event handled after the transaction commits; the report is archived, then deleted.

**Like cancellation** (post or comment): the like row is deleted and the target's `like_count` is decreased, floored at zero for comments.

**Notification retention**: a scheduler deletes notifications older than 30 days, and saving a notification deletes the
recipient's oldest notifications beyond the per-recipient maximum.

**Token and file cleanup**: schedulers delete expired refresh tokens and pending files older than 10 minutes (S3 object
first, then the `pending_file` row); logout deletes the refresh token; FCM delivery deletes a token the provider reports
as unregistered or invalid.

---

## 3. Soft-Delete Policy

Comment deletion (`CommentController.delete` → `CommentRepositoryJpaAdapter.deleteComment`) only sets
`comm_comment.is_deleted` to `true`. The row, its content, likes, `like_count`, abuse reports, dashboards, and
notifications are left as they are, and the post's comment list keeps returning it with `isDeleted` set.

Comment counts are computed at read time; no counter column is stored:

| Count / query                                                    | Soft-deleted comments |
|------------------------------------------------------------------|-----------------------|
| comment count of a post (post lists, member post lists, search)  | included              |
| comments written by a member (list and total)                    | excluded              |
| post search matching on comment content                          | excluded              |

Reply-target handling of soft-deleted comments is a comment-domain rule, defined in `.claude/rules/domain-comment-details.md`.

---

## 4. Cascade

- **Database**: the only foreign key is `plant_info_photo.plant_info_id → plant_info(id) ON DELETE CASCADE`
  (`V1.2.0__Create_plant_info_table.sql`); deleting a `plant_info` row deletes its photos.
- **JPA**: no association declares `CascadeType.REMOVE`, `CascadeType.ALL`, `orphanRemoval`, or `@OnDelete`; associations
  cascade `PERSIST` and `MERGE` only.

Every other related row is removed only by an explicit delete in the flow that owns it (section 2.3).
