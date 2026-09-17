# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 어드민 API
Target Notion Page: 건의 및 버그 제보 제거 API (`MemberAdminRestController.removeProposalOrBugReport`'s `@Operation` summary — this page is known from prior review to have an existing but currently empty `## 요청 파라미터` heading)
Notion Page State: Existing (fetch before editing)
Change Summary: fill the existing empty `## 요청 파라미터` heading with a `### 경로 변수` table documenting `reportUlid`.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| reportUlid | String | 삭제할 보고서의 식별자 |

## Provenance

- `reportUlid` row ← detected-change fact: "`removeProposalOrBugReport` (`DELETE /api/admin/v1/report/proposal-or-bug/{reportUlid}`): path variable `reportUlid` — `@PathVariable @NotBlank @Pattern(regexp = REGEX_ULID)`, type `String`."
- Description text ← the method's own `@Parameter(description = "삭제할 보고서의 식별자", ...)`.

## Edit Instructions

1. Fetch the existing page for this endpoint inside the `어드민 API` Notion database — search by
   title/route, never assume a cached page URL.
2. The page already has a `## 요청 파라미터` heading but it is empty — insert `### 경로 변수` under
   it with the table above; do not create a duplicate `## 요청 파라미터` heading.
3. Touch no other section on the page (in particular, do not touch the separately-flagged,
   out-of-scope POST/DELETE method-label bug on this same page's request example — that is not
   part of this change).
