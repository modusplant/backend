# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 회원 API
Target Notion Page: 댓글 신고 API (best match from `MemberRestController.reportCommentAbuse`'s `@Operation` summary — confirm exact title via `notion-search` before editing; note there is also a `@Hidden` overload taking only a bare `path` that is not user-facing and must not be confused with this one)
Notion Page State: Existing (fetch before editing)
Change Summary: add the previously-undocumented `postUlid`/`path` path variables as a canonical `### 경로 변수` table.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| postUlid | String | 신고할 댓글이 달린 게시글의 식별자 |
| path | String | 신고할 댓글의 경로 |

## Provenance

- `postUlid`/`path` rows ← detected-change fact: "`reportCommentAbuse` (`POST /api/v1/report/abuse/post/{postUlid}/path/{path}`): two path variables — `postUlid` (`String`, ULID, `@PathVariable @NotBlank`) and `path` (`String`, materialized path, `@PathVariable(required = false) @NotBlank`)."
- Description text ← the method's own `@Parameter` descriptions for `postUlid` and `path`.
