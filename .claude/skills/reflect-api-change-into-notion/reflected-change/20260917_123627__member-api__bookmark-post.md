# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 회원 API
Target Notion Page: 게시글 북마크 API (best match from `MemberRestController.bookmarkCommunicationPost`'s `@Operation` summary — confirm exact title via `notion-search` before editing)
Notion Page State: Existing (fetch before editing)
Change Summary: add the previously-undocumented `postUlid` path variable as a canonical `### 경로 변수` table.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| postUlid | String | 북마크를 누를 게시글의 식별자 |

## Provenance

- `postUlid` row ← detected-change fact: "`bookmarkCommunicationPost` (`PUT /api/v1/members/bookmark/communication/post/{postUlid}`): path variable `postUlid` — same shape as above [likeCommunicationPost]."
- Description text ← the method's own `@Parameter(description = "북마크를 누를 게시글의 식별자", ...)`.

## Edit Instructions

1. Fetch the existing page for this endpoint inside the `회원 API` Notion database — search by
   title/route, never assume a cached page URL.
2. If `## 요청 파라미터` is absent, insert it; insert `### 경로 변수` under it with the table above.
3. Touch no other section on the page.
