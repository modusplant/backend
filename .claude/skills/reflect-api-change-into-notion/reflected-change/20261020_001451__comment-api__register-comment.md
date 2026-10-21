# Reflected Notion Change — 20261020_001451

Run ID: 20261020_001451
Source Detected-Change File: detected-change/detected_20261020_001451.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 댓글 추가
Notion Page State: Existing (fetch before editing)
Change Summary: Restore the parent-comment failure row and note bullets, and remove the `exist_comment` failure row.

## Notion Content

### `## 응답(실패) 종류` (remove / add rows)

- Remove the row `exist_comment`.
- Add the row below right before the `not_found_comment` row:

| 원인                         | 상태  | 코드                             | 메시지                         |
|----------------------------|-----|--------------------------------|-----------------------------|
| 댓글의 부모 댓글이 없는데 대댓글을 저장하려 함 | 404 | “`not_exist_parent_comment`”   | “`댓글의 부모 댓글의 데이터가 없습니다`”    |

### `# 비고` — `부모 댓글` toggle (insert after the `의미` bullet)

```
	- 부모 댓글이 있어야 댓글 추가가 가능합니다.
	- ✅ 예시: 경로가 “1.4”인 댓글은 경로가 “1”인 댓글이 있어야 등록 가능합니다.
```

## Provenance

- `not_exist_parent_comment` row cause and the two parent-required note bullets ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: `register` calls `validateIfParentCommentExists`, which throws `NOT_EXIST_PARENT_COMMENT` when the nested path's parent does not exist.
- `not_exist_parent_comment` status/code/message ← `kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode`: `NOT_EXIST_PARENT_COMMENT(404, "not_exist_parent_comment", "댓글의 부모 댓글의 데이터가 없습니다")` unchanged.
- Removal of the `exist_comment` row ← `kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode`: `EXIST_COMMENT` removed.
