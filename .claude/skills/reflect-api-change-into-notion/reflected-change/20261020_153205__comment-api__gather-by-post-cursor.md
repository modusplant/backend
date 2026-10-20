# Reflected Notion Change — 20261020_153205

Run ID: 20261020_153205
Source Detected-Change File: detected-change/detected_20261020_153205.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 특정 게시글의 댓글 목록 조회 (무한 스크롤)
Notion Page State: New (create)
Change Summary: Create the page for the cursor-paginated `GET /api/v2/communication/comments/post/{ulid}` endpoint.
Note: Set properties `기능` = `특정 게시글의 댓글 목록 조회 (무한 스크롤)`, `Method` = `GET`, `URI` = `/api/v2/communication/comments/post/\{ulid\}`, `페이지(다중 선택)` = `["소통 컨텐츠 상세 페이지"]`, `진행 상태` = `메인 코드 개발 완료`; leave `MVP`, `우선순위`, `담당자` unset. Each `commentList` item mirrors the item shape on the existing `특정 게시글의 댓글 목록 조회` page.

## Notion Content

```
# 요청
## 인증
> **선택(Optional)**
해당 API는 인증 없이도 호출할 수 있으며, 아래 헤더를 포함하면 회원 기준의 값(예: isLiked)이 반영됩니다.
​```javascript
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
​```
## 요청 헤더
- If-None-Match: 이전 응답의 ETag 값 (선택)
- If-Modified-Since: 이전 응답의 Last-Modified 값 (선택)
## 요청 파라미터
### 쿼리 파라미터
| 이름 | 타입 | 설명 | 필수 여부 |
| lastPath | String | 이전에 조회되었던 마지막 댓글 경로 (예: 3.1) | 선택(최초 조회 시 불필요, 이후 조회 시 필요) |
| size | Integer | 페이지 크기 (1 이상 50 이하, 기본값 20) | 선택 |
### 경로 변수
| 이름 | 타입 | 설명 |
| ulid | String | 해당 댓글이 달린 게시글의 식별자 |
## 요청 예시
### 최초 조회
GET /api/v2/communication/comments/post/01JY3PPG5YJ41H7BPD0DSQW2RD?size=20
### 특정 댓글 이후 조회
GET /api/v2/communication/comments/post/01JY3PPG5YJ41H7BPD0DSQW2RD?lastPath=3.1&size=20

# 응답
응답으로 받은 `lastPath`는 다음 댓글 목록 조회 시 `lastPath` 파라미터 값으로 사용합니다.
## 응답(성공)
**200 OK**
{
  "status": 200,
  "code": "generic_success",
  "message": "",
  "data": {
    "commentList": [ { <existing item shape> }, // ... ],
    "lastPath": "3.1", // 마지막 댓글 경로 (조회된 댓글이 없으면 null)
    "hasNext": true, // 다음 댓글 존재 여부
    "size": 20 // 조회된 댓글 수
  }
}
**304 Not Modified**
응답 바디 없이 헤더(ETag, Last-Modified, Cache-Control)만 반환합니다.
## 응답(실패)
{ "status": 404, "code": "not_found_comment", "message": "댓글이 존재하지 않습니다" }
## 응답(실패) 종류
| 원인 | 상태 | 코드 | 메시지 |
| 게시글 식별자의 부재 | 400 | empty_post_id | 게시글의 식별자 값이 비었습니다 |
| 게시글 식별자의 형식이 올바르지 않음 | 400 | invalid_post_id | 게시글 식별자의 형식이 올바르지 않습니다 |
| 게시글 식별자가 비어 있음 | 400 | constraint_violation | 게시글 식별자가 비어 있습니다. |
| lastPath의 형식이 올바르지 않음 | 400 | constraint_violation | 댓글 경로의 형식이 올바르지 않습니다 |
| size가 1 미만이거나 50 초과 | 400 | constraint_violation | 페이지 크기가 허용된 값을 벗어났습니다. |
| size가 정수가 아님 | 400 | mismatch_input_type | 입력값의 타입이 올바르지 않습니다 |
| lastPath 값의 부재 | 400 | empty_comment_path | 댓글의 경로 값이 비었습니다 |
| lastPath의 형식이 올바르지 않음 | 400 | invalid_comment_path_format | 댓글 경로의 형식이 올바르지 않습니다 |
| lastPath가 1 기반 인덱스를 따르지 않음 | 400 | invalid_comment_path_index | 댓글 경로가 1 기반 인덱스를 따르지 않습니다 |
| lastPath의 깊이가 3 이상 | 400 | invalid_comment_path_depth | 댓글 경로의 깊이가 3 이상입니다 |
| size가 1 미만이거나 50 초과 | 400 | comment_page_size_out_of_range | 댓글 페이지 크기가 올바른 값의 범위를 벗어났습니다 |
| 게시글이 존재하지 않음 | 404 | not_found_post | 게시글이 존재하지 않습니다 |
| lastPath에 해당하는 댓글이 존재하지 않음 | 404 | not_found_comment | 댓글이 존재하지 않습니다 |

# 비고
- 다음 페이지가 있으면 hasNext는 true이고, lastPath는 이번에 반환된 마지막 댓글의 경로입니다.
- 마지막 페이지이면 hasNext는 false이고, 조회된 댓글이 없으면 lastPath는 null입니다.
```

## Provenance

- Endpoint path, `ulid` path variable and description ← `kr.modusplant.domains.comment.framework.inbound.web.rest.CommentRestController`: new `GET /api/v2/communication/comments/post/{ulid}`; path variable `ulid`.
- `## 인증` = `선택(Optional)` ← `kr.modusplant.infrastructure.security.config.SecurityConfig`: `PUBLIC_ENDPOINTS` GET gained `/api/v2/communication/comments/post/**`; also `kr.modusplant.domains.comment.framework.inbound.web.rest.CommentRestController`: optional `@AuthenticationPrincipal` read bullet.
- `If-None-Match` / `If-Modified-Since`, `200 OK` / `304 Not Modified`, cache headers ← `kr.modusplant.domains.comment.framework.inbound.web.rest.CommentRestController`: conditional-GET bullet.
- `lastPath` / `size` parameters, range, default, description ← `kr.modusplant.domains.comment.framework.inbound.web.rest.CommentRestController`: query parameter bullets.
- `constraint_violation` rows and messages, `mismatch_input_type` row ← `kr.modusplant.domains.comment.framework.inbound.web.rest.CommentRestController`: `@NotBlank`/`@Pattern`/`@Min`/`@Max` messages and `GlobalExceptionHandler` bullet.
- `not_found_post` row ← `kr.modusplant.domains.comment.framework.inbound.web.rest.CommentRestController`: `CommentCacheService.getCacheData` bullet; also `kr.modusplant.domains.comment.adapter.controller.CommentController`: `validateIfPostExists`.
- `empty_post_id`, `invalid_post_id`, `empty_comment_path`, `invalid_comment_path_*`, `not_found_comment` causes ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: `PostId`/`CommentPath` construction and `validateIfCommentExists` bullets; status/code/message ← `kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode`: existing-constants bullet.
- `comment_page_size_out_of_range` row ← `kr.modusplant.domains.comment.domain.vo.CommentPageSize`: range check; status/code/message ← `kr.modusplant.domains.comment.domain.exception.enums.CommentErrorCode`: added constant.
- `commentList`, `lastPath`, `hasNext`, `size` fields and meanings ← `kr.modusplant.domains.comment.usecase.response.CommentCursorPageResponse`: record components and `of(...)`.
- `hasNext`/`lastPath` derivation in `# 비고` and the response note ← `kr.modusplant.domains.comment.adapter.controller.CommentController`: `size + 1` fetch and branch bullets.
