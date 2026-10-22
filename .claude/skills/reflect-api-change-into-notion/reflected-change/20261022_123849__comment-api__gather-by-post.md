# Reflected Notion Change — 20261022_123849

Run ID: 20261022_123849
Source Detected-Change File: detected-change/detected_20261022_123849.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 특정 게시글의 댓글 목록 조회 (오프셋 페이지네이션)
Notion Page State: Existing (fetch before editing)
Change Summary: Add the `replyToIsDeleted` field to each `data` item in `응답(성공)` and describe it in `# 비고`.
Note: Page title differs from the prior run's (`특정 게시글의 댓글 목록 조회`); its `URI` property is `/api/v1/communication/comments/post/\{ulid\}`. Edit only the two item examples and the `replyToPath, replyToNickname` bullet group; add a trailing comma to each item's existing `replyToNickname` line.

## Notion Content

`## 응답(성공)` — first item (reply target present), after `replyToNickname`:

```
"replyToNickname": "string", // 답글 대상 댓글 작성자의 닉네임 (답글 대상 댓글이 없으면 null)
"replyToIsDeleted": false // 답글 대상 댓글의 삭제 여부 (답글 대상 댓글이 없으면 false)
```

`## 응답(성공)` — second item (no reply target), after `replyToNickname`:

```
"replyToNickname": null,
"replyToIsDeleted": false
```

`# 비고` — new bullet group after the `replyToPath, replyToNickname` group:

```
- replyToIsDeleted
  - 답글 대상 댓글이 삭제된 경우 true입니다.
  - 답글 대상 댓글이 삭제되지 않았거나, 답글 대상 댓글이 없는 댓글의 값은 false입니다.
```

## Provenance

- `replyToIsDeleted` field, boolean type, position after `replyToNickname` ← `kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse`: new trailing record component bullet.
- `true` only when the reply target is soft-deleted; `false` when not deleted or when there is no reply target ← `kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse`: `CommentJooqRepository` `Boolean.TRUE.equals(...)` mapping bullet.
- Applies to `GET /api/v1/communication/comments/post/{ulid}` list items ← `kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse`: v1 endpoint bullet.
