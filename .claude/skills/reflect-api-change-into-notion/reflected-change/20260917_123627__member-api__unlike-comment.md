# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 회원 API
Target Notion Page: 댓글 좋아요 취소 API (best match from `MemberRestController.unlikeCommunicationComment`'s `@Operation` summary — confirm exact title via `notion-search` before editing)
Notion Page State: Existing (fetch before editing)
Change Summary: add the previously-undocumented `postUlid`/`path` path variables as a canonical `### 경로 변수` table.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| postUlid | String | 좋아요를 취소할 댓글의 게시글 식별자 |
| path | String | 좋아요를 취소할 댓글의 경로 |

## Provenance

- `postUlid`/`path` rows ← detected-change fact: "`unlikeCommunicationComment` (`DELETE .../post/{postUlid}/path/{path}`): same two path variables as above [likeCommunicationComment]."
- Description text ← the method's own `@Parameter` descriptions for `postUlid` and `path`.

## Edit Instructions

1. Fetch the existing page for this endpoint inside the `회원 API` Notion database — search by
   title/route, never assume a cached page URL.
2. If `## 요청 파라미터` is absent, insert it; insert `### 경로 변수` under it with the table above.
3. Touch no other section on the page.
