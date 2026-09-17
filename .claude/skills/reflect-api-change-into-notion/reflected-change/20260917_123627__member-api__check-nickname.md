# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 회원 API
Target Notion Page: 회원 닉네임 중복 확인 API (best match from `MemberRestController.checkExistedMemberNickname`'s `@Operation` summary — confirm exact title via `notion-search` before editing)
Notion Page State: Existing (fetch before editing)
Change Summary: add the previously-undocumented `nickname` path variable as a canonical `### 경로 변수` table.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| nickname | String | 중복을 확인하려는 회원의 닉네임 |

## Provenance

- `nickname` row ← detected-change fact: "`checkExistedMemberNickname` (`GET /api/v1/members/check/nickname/{nickname}`): path variable `nickname` — `@PathVariable(required = false) @NotBlank @Pattern(regexp = REGEX_NICKNAME)`, type `String`."
- Description text ← the method's own `@Parameter(description = "중복을 확인하려는 회원의 닉네임", ...)`.

## Edit Instructions

1. Fetch the existing page for this endpoint inside the `회원 API` Notion database — search by
   title/route, never assume a cached page URL.
2. If `## 요청 파라미터` is absent, insert it; insert `### 경로 변수` under it with the table above.
3. Touch no other section on the page.
