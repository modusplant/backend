# Reflected Notion Change — 20261019_235317

Run ID: 20261019_235317
Source Detected-Change File: detected-change/detected_20261019_235317.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 특정 게시글의 댓글 목록 조회
Notion Page State: Existing (fetch before editing)
Change Summary: Add `replyToPath` and `replyToNickname` to each item of the success response and note when they are null.

## Notion Content

### `## 응답(성공)` — append after `"isDeleted"` in each item

First item:

```
			"isDeleted": false,
			"replyToPath": "string", // 숫자.숫자 (답글 대상 댓글이 없으면 null)
			"replyToNickname": "string" // 답글 대상 댓글 작성자의 닉네임 (답글 대상 댓글이 없으면 null)
```

Second item:

```
			"isDeleted": true,
			"replyToPath": null,
			"replyToNickname": null
```

### `# 비고` — append bullet

```
- replyToPath, replyToNickname
	- 대댓글에 대한 답글인 경우, 답글 대상 댓글의 경로와 그 댓글 작성자의 닉네임입니다.
	- 답글 대상 댓글이 없는 댓글의 두 값은 null입니다.
```

## Provenance

- `replyToPath`, `replyToNickname` fields, their meaning, and null when absent ← `kr.modusplant.domains.comment.usecase.response.CommentOfPostResponse`: added trailing components populated from `comm_comment.reply_to_path` and the reply target's author nickname via left joins (null without a reply target).
- `숫자.숫자` format of `replyToPath` ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: `replyToPath` must have exactly two segments.
