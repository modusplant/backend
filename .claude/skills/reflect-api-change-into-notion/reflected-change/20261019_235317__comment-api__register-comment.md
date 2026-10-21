# Reflected Notion Change — 20261019_235317

Run ID: 20261019_235317
Source Detected-Change File: detected-change/detected_20261019_235317.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 댓글 추가
Notion Page State: Existing (fetch before editing)
Change Summary: Add the optional `replyToPath` request field, limit `path` to depth 2, and align the failure catalog and notes with the reply-target validation that replaced the parent-comment check.
Note: The `exist_comment` row is left untouched (its source, `CommentRepositoryJpaAdapter`, is outside the watched surface). The missing `## 인증` section is a pre-existing gap and is reported, not added.

## Notion Content

### `## 요청 바디` — JSON example (replace)

```json
{
  "replyToPath": "string", // 숫자.숫자 (대댓글에 답글을 작성하는 경우에만, 그외의 경우 null)
  "postId": "string", // ulid
  "path": "string", // 숫자 또는 숫자.숫자
  "content": "string" // 댓글 내용
}
```

### `## 요청 바디` — table (insert a row before `postId`; replace the `path` description)

| 키           | 필수 여부 | 설명                                                    |
|-------------|-------|-------------------------------------------------------|
| replyToPath | 선택    | 대댓글에 답글을 작성하는 경우, 답글 대상 대댓글의 경로 (숫자.숫자, 그외의 경우 null) |
| path        | 필수    | 숫자 또는 숫자.숫자                                           |

### `## 응답(실패) 종류` (replace / add / remove rows)

- Replace the cause of `empty_comment_path`: `댓글 경로 또는 답글 대상 댓글 경로의 부재`
- Add rows:

| 원인                                                       | 상태  | 코드                                | 메시지                             |
|----------------------------------------------------------|-----|-----------------------------------|---------------------------------|
| 댓글 경로 또는 답글 대상 댓글 경로의 형식이 올바르지 않음                         | 400 | “`invalid_comment_path_format`”   | “`댓글 경로의 형식이 올바르지 않습니다`”        |
| 댓글 경로 또는 답글 대상 댓글 경로가 1 기반 인덱스를 따르지 않음                    | 400 | “`invalid_comment_path_index`”    | “`댓글 경로가 1 기반 인덱스를 따르지 않습니다`”   |
| 댓글 경로 또는 답글 대상 댓글 경로의 깊이가 3 이상                            | 400 | “`invalid_comment_path_depth`”    | “`댓글 경로의 깊이가 3 이상입니다`”          |
| 답글 대상 댓글 경로와 댓글 경로가 모두 깊이 2가 아니거나, 서로 다른 댓글 아래에 있음        | 400 | “`invalid_reply_to_comment_path`” | “`답글 대상 댓글의 경로가 올바르지 않습니다`”    |
| 답글 대상 댓글이 존재하지 않음                                        | 404 | “`not_found_comment`”             | “`댓글이 존재하지 않습니다`”              |

- Remove rows: `exist_post_comment`, `not_exist_parent_comment`, `not_exist_sibling_comment`.

### `# 비고`

- `부모 댓글` toggle: remove the bullets `부모 댓글이 있어야 댓글 추가가 가능합니다.` and `✅ 예시: 경로가 “1.4”인 댓글은 경로가 “1”인 댓글이 있어야 등록 가능합니다.`
- Replace the `댓글 등록 시나리오 예시 (부모 및 형제 댓글)` toggle with:

```
<details>
<summary>답글 대상 댓글 (replyToPath)</summary>
	- 의미: 대댓글에 답글을 작성할 때, 답글의 대상이 되는 대댓글의 경로입니다.
	- replyToPath와 path는 모두 “숫자.숫자” 형식(깊이 2)이고, 첫 번째 숫자가 같아야 합니다.
	- ✅ path “1.5”, replyToPath “1.4” → 경로가 “1.4”인 댓글 존재 → 허용
	- ❌ path “1.5”, replyToPath “2.1” → 서로 다른 댓글 아래에 있음 → 불허 (invalid_reply_to_comment_path)
	- ❌ path “1.5”, replyToPath “1.9” → 경로가 “1.9”인 댓글 없음 → 불허 (not_found_comment)
</details>
```

## Provenance

- `replyToPath` key, `선택`, description, `숫자.숫자` format ← `kr.modusplant.domains.comment.framework.inbound.web.rest.CommentRestController`: new `replyToPath` component, no Bean Validation, `@Schema(nullable = true, pattern = "^\\d+\\.\\d+$")`, description text.
- `path` described as `숫자 또는 숫자.숫자`; removal of the depth-3 scenario `4.2.1` ← `kr.modusplant.domains.comment.framework.inbound.web.rest.CommentRestController`: `path` pattern changed to `^\d+(?:\.\d+)?$`.
- `empty_comment_path` cause covers the reply target; `invalid_comment_path_format`/`_index`/`_depth` rows ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: non-null `replyToPath` converted via `CommentPath.create(...)`; pre-existing conversion of `path` through the same factory.
- `invalid_comment_path_*` and `invalid_reply_to_comment_path` status/code/message ← `kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode`: added `INVALID_REPLY_TO_COMMENT_PATH`; retained constants unchanged.
- `invalid_reply_to_comment_path` cause; `not_found_comment` row (404, `댓글이 존재하지 않습니다`); reply-target note toggle ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: `validateIfReplyToCommentPathIsValid` two-segment/same-root check, then `validateIfCommentExists` throwing `NOT_FOUND_COMMENT`.
- Removal of `not_exist_parent_comment` row and the parent-required note bullets ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: parent-existence check `validateIfParentCommentExists` removed.
- Removal of `exist_post_comment` and `not_exist_sibling_comment` rows ← `kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode`: constants removed.
