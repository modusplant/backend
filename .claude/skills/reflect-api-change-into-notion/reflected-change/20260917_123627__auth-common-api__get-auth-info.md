# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 인증 인가 API(공통)
Target Notion Page: 회원 인증 정보 조회 (`IdentityRestController.getAuthInfo`'s `@Operation` summary "회원의 식별자로 회원의 이메일, 인증 제공자, 가입일을 가져오는 API" — confirm exact title via `notion-search` before editing)
Notion Page State: Existing (fetch before editing)
Change Summary: replace the `id` path variable's current representation (a URL comment inside the request-body code block) with a canonical `### 경로 변수` table.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| id | UUID | 회원의 식별자 |

## Provenance

- `id` row ← detected-change fact: "`getAuthInfo` (`GET /api/v1/members/{id}/auth-info`): path variable `id` bound to method parameter `memberUuid` — `@PathVariable(\"id\") @NotNull`, type `UUID`."
- Description text ← the method's own `@Parameter(schema = @Schema(description = "회원의 식별자", ...))`.

## Edit Instructions

1. Fetch the existing page for this endpoint inside the `인증 인가 API(공통)` Notion database —
   search by title/route, never assume a cached page URL.
2. If `## 요청 파라미터` is absent, insert it; insert `### 경로 변수` under it with the table above.
3. Leave the existing request-body code block's URL comment as-is unless it duplicates the new
   table's information in a way that would violate `document-format.md`'s no-duplication rule — if
   so, remove only the redundant URL comment line, not the rest of the code block.
4. Touch no other section on the page.
