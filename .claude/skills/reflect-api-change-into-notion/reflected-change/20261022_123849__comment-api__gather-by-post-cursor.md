# Reflected Notion Change — 20261022_123849

Run ID: 20261022_123849
Source Detected-Change File: detected-change/detected_20261022_123849.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 특정 게시글의 댓글 목록 조회 (커서 페이지네이션)
Notion Page State: Existing (fetch before editing)
Change Summary: Add the `replyToIsDeleted` field to the `commentList` item example in `응답(성공)`.
Note: Page title differs from the prior run's (`특정 게시글의 댓글 목록 조회 (무한 스크롤)`); its `URI` property is `/api/v2/communication/comments/post/\{ulid\}`. Edit only the `commentList` item example; add a trailing comma to its existing `replyToNickname` line.

## Notion Content

`## 응답(성공)` — `**200 OK**` example, `commentList` item, after `replyToNickname`:

```
"replyToNickname": "string", // 답글 대상 댓글 작성자의 닉네임 (답글 대상 댓글이 없으면 null)
"replyToIsDeleted": false // 답글 대상 댓글의 삭제 여부 (답글 대상 댓글이 없으면 false)
```

## Provenance

- `commentList` items carry `replyToIsDeleted` ← `kr.modusplant.domains.comment.usecase.response.CommentCursorPageResponse`: `commentList` is `List<CommentOfPostResponse>` bullet.
- `replyToIsDeleted` field, boolean type, position after `replyToNickname` ← `kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse`: new trailing record component bullet.
- Comment text (`false` when there is no reply target) ← `kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse`: `CommentJooqRepository` `Boolean.TRUE.equals(...)` mapping bullet.
