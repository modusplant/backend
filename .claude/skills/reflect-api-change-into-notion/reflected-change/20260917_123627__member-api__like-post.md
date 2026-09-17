# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 회원 API
Target Notion Page: 게시글 좋아요 API (best match from `MemberRestController.likeCommunicationPost`'s `@Operation` summary — confirm exact title via `notion-search` before editing)
Notion Page State: Existing (fetch before editing)
Change Summary: add the previously-undocumented `postUlid` path variable as a canonical `### 경로 변수` table.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| postUlid | String | 좋아요를 누를 게시글의 식별자 |

## Provenance

- `postUlid` row ← detected-change fact: "`likeCommunicationPost` (`PUT /api/v1/members/like/communication/post/{postUlid}`): path variable `postUlid` — `@PathVariable(required = false) @NotBlank`, type `String` (ULID format)."
- Description text ← the method's own `@Parameter(description = "좋아요를 누를 게시글의 식별자", ...)`.
