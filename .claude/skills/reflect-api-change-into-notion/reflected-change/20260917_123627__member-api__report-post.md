# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 회원 API
Target Notion Page: 게시글 신고 API (best match from `MemberRestController.reportPostAbuse`'s `@Operation` summary — confirm exact title via `notion-search` before editing; note there is also a `@Hidden` zero-arg overload of the same name that is not user-facing and must not be confused with this one)
Notion Page State: Existing (fetch before editing)
Change Summary: add the previously-undocumented `postUlid` path variable as a canonical `### 경로 변수` table.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| postUlid | String | 신고할 게시글의 식별자 |

## Provenance

- `postUlid` row ← detected-change fact: "`reportPostAbuse` (`POST /api/v1/report/abuse/post/{postUlid}`): path variable `postUlid` — `@PathVariable @NotBlank`, type `String` (ULID)."
- Description text ← the method's own `@Parameter(description = "신고할 게시글의 식별자", ...)`.
