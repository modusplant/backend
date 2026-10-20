# Reflected Notion Change — 20261020_112540

Run ID: 20261020_112540
Source Detected-Change File: detected-change/detected_20261020_112540.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 댓글 추가
Notion Page State: Existing (fetch before editing)
Change Summary: Add the `deleted_reply_to_comment` failure for a soft-deleted reply target and reflect it in the `replyToPath` note.
Note: Insert the new row after the `empty_comment_content` row so the 400 rows stay ahead of the 404 rows; in `# 비고`, edit only the `답글 대상 댓글 (replyToPath)` toggle.

## Notion Content

### `## 응답(실패) 종류` — new row (insert after the `empty_comment_content` row)

| 원인                | 상태  | 코드                           | 메시지                   |
|-------------------|-----|------------------------------|-----------------------|
| 답글 대상 댓글이 삭제되어 있음 | 400 | “`deleted_reply_to_comment`” | “`답글 대상 댓글이 삭제되었습니다`” |

### `# 비고` — `답글 대상 댓글 (replyToPath)` toggle

- Replace `✅ path “1.5”, replyToPath “1.4” → 경로가 “1.4”인 댓글 존재 → 허용` with
  `✅ path “1.5”, replyToPath “1.4” → 경로가 “1.4”인 댓글이 존재하며 삭제되지 않음 → 허용`
- Append as the last bullet:
  `❌ path “1.5”, replyToPath “1.4” → 경로가 “1.4”인 댓글이 삭제됨 → 불허 (deleted_reply_to_comment)`

## Provenance

- `deleted_reply_to_comment` row cause (soft-deleted reply target) ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: `validateIfCommentIsNotDeleted` throws `DeletedReplyToCommentException` (error code `DELETED_REPLY_TO_COMMENT`) when the reply-target comment is soft-deleted; nothing is saved.
- `deleted_reply_to_comment` row status/code/message ← `kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode`: New constant `DELETED_REPLY_TO_COMMENT`: status `400`, code `deleted_reply_to_comment`, message `답글 대상 댓글이 삭제되었습니다`.
- `replyToPath` note — allowed only when the target exists and is not deleted; rejected when deleted ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: the deleted check runs after the depth/same-root check and the existence check.
