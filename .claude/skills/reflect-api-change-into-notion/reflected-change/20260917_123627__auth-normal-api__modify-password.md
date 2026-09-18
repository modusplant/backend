# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 일반 인증 인가 API
Target Notion Page: 일반 계정 비밀번호 수정 (`NormalIdentityRestController.modifyPassword`'s `@Operation` summary "일반 회원의 비밀번호 수정 API" — confirm exact title via `notion-search` before editing)
Notion Page State: Existing (fetch before editing)
Change Summary: replace the `id` path variable's current representation (a URL comment inside the request-body code block) with a canonical `### 경로 변수` table.
Note: leave the existing request-body URL comment as-is unless it duplicates the new table — remove only the redundant comment line if so.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| id | UUID | 회원의 식별자 |

## Provenance

- `id` row ← detected-change fact: "`modifyPassword` (`POST /api/v1/members/{id}/modify/password`): path variable `id` bound to method parameter `memberUuid` — `@PathVariable(\"id\") @NotNull`, type `UUID`."
- Description text ← the method's own `@Parameter(schema = @Schema(description = "회원의 식별자", ...))`.
