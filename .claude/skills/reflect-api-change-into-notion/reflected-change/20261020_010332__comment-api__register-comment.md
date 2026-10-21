# Reflected Notion Change — 20261020_010332

Run ID: 20261020_010332
Source Detected-Change File: detected-change/detected_20261020_010332.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 댓글 추가
Notion Page State: Existing (fetch before editing)
Change Summary: Narrow the `empty_comment_path` cause to the comment path only, and state that a blank `replyToPath` is treated like null.

## Notion Content

### `## 요청 바디` table — `replyToPath` row, `설명` cell (replace)

| 키           | 필수 여부 | 설명                                                                 |
|-------------|-------|--------------------------------------------------------------------|
| replyToPath | 선택    | 대댓글에 답글을 작성하는 경우, 답글 대상 대댓글의 경로 (숫자.숫자, 그외의 경우 null이며 빈 문자열은 null과 동일하게 처리) |

### `## 응답(실패) 종류` — `empty_comment_path` row, `원인` cell (replace)

| 원인         | 상태  | 코드                     | 메시지               |
|------------|-----|------------------------|-------------------|
| 댓글 경로의 부재 | 400 | “`empty_comment_path`” | “`댓글의 경로 값이 비었습니다`” |

## Provenance

- `replyToPath` blank-equals-null note ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: `ReplyToCommentPath.create` returns `EmptyReplyToCommentPath` for a null or blank value; a blank `replyToPath` is handled the same as a null one.
- `empty_comment_path` cause narrowed to the comment path ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: `EMPTY_COMMENT_PATH` is now reachable in `register` only through `CommentPath.create(request.path())`.
