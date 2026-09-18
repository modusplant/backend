# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 어드민 API
Target Notion Page: 건의 및 버그 제보 제거 API (`MemberAdminRestController.removeProposalOrBugReport`'s `@Operation` summary — this page is known from prior review to have an existing but currently empty `## 요청 파라미터` heading)
Notion Page State: Existing (fetch before editing)
Change Summary: fill the existing empty `## 요청 파라미터` heading with a `### 경로 변수` table documenting `reportUlid`.
Note: the page's `## 요청 파라미터` heading already exists but is empty — insert `### 경로 변수` under it, don't duplicate the heading. Don't touch the separately out-of-scope POST/DELETE label issue.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| reportUlid | String | 삭제할 보고서의 식별자 |

## Provenance

- `reportUlid` row ← detected-change fact: "`removeProposalOrBugReport` (`DELETE /api/admin/v1/report/proposal-or-bug/{reportUlid}`): path variable `reportUlid` — `@PathVariable @NotBlank @Pattern(regexp = REGEX_ULID)`, type `String`."
- Description text ← the method's own `@Parameter(description = "삭제할 보고서의 식별자", ...)`.
