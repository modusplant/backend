# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 소통 댓글 API
Target Notion Page: 특정 게시글의 댓글 목록 조회 (`CommentRestController.gatherByPost`'s `@Operation` summary "게시글 식별자로 컨텐츠 댓글 조회 API" — confirm exact title via `notion-search` before editing)
Notion Page State: Existing (fetch before editing)
Change Summary: add a brand-new `## 요청 파라미터` section (currently absent from the page) containing only a `### 경로 변수` table for `postUlid`. No query parameters exist on this endpoint — do not add a `### 쿼리 파라미터` section.

## Notion Content

## 요청 파라미터

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| postUlid | String | 해당 댓글이 달린 게시글의 식별자 |

## Provenance

- `postUlid` row ← detected-change fact: "`gatherByPost` (`GET /api/v1/communication/comments/post/{ulid}`): path variable bound to method parameter `postUlid` — `@PathVariable(required = false, value = \"ulid\") @NotBlank`, type `String` (ULID). The method has no query parameters."
- Description text ← the method's own Javadoc (`@param postUlid 댓글이 등록된 게시글의 식별자입니다.`) and `@Parameter(description = "해당 댓글이 달린 게시글의 식별자", ...)`.

## Edit Instructions

1. Fetch the existing page for this endpoint inside the `소통 댓글 API` Notion database — search
   by title/route, never assume a cached page URL.
2. Insert a new `## 요청 파라미터` H1... actually H2 heading (per `document-format.md`'s skeleton,
   positioned after `## 요청 바디`/`## 요청 헤더` and before `## 요청 예시`, both absent or present
   as applicable on this page), with `### 경로 변수` beneath it and the table above.
3. Do not add a `### 쿼리 파라미터` subsection — this endpoint has no query parameters.
4. Touch no other section on the page.
