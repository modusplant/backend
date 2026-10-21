# Reflected Notion Change — 20261020_153205

Run ID: 20261020_153205
Source Detected-Change File: detected-change/detected_20261020_153205.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 댓글 삭제
Notion Page State: Existing (fetch before editing)
Change Summary: Add the failure row for a malformed `path` path variable rejected by bean validation.
Note: Append the new row after the existing `데이터의 제약조건 위배` row; leave every other section untouched.

## Notion Content

### `## 응답(실패) 종류` — append row

| 원인 | 상태 | 코드 | 메시지 |
| 댓글 경로의 형식이 올바르지 않음 | 400 | “`constraint_violation`” | “`댓글 경로의 형식이 올바르지 않습니다`” |

## Provenance

- Row cause, status, code, and message ← `kr.modusplant.domains.comment.framework.inbound.web.rest.CommentRestController`: `path` gained `@Pattern(..., message = "댓글 경로의 형식이 올바르지 않습니다")`; constraint violations return 400 `constraint_violation` with the first violation's message.
