# Notion API Document Format

This file defines the single canonical structure every page in the 6 target Notion API-specification databases must follow. It exists so the `reflect-api-change-into-notion` skill can know the expected shape of a Notion document without fetching it first.

Section and column names below are literal strings that must appear verbatim in Notion (the workspace's documents are written in Korean); the surrounding explanations are in English.

## Target databases

| Domain                              | Notion database (breadcrumb under `API 명세`) |
|-------------------------------------|---------------------------------------------|
| `member`                            | `회원 API`                                    |
| `member` (admin)                    | `어드민 API`                                   |
| `comment`                           | `소통 댓글 API`                                 |
| `search`                            | `검색 API`                                    |
| `infrastructure.security`           | `인증 인가 API(일반과 소셜에 국한되지 않음)`                |
| `infrastructure.security` (general) | `일반 인증 인가 API`                              |

## Canonical page skeleton

Every endpoint page is a flat sequence of top-level (`H1`) sections in this order. No `예시` wrapper H1 — `요청` and `응답` are themselves top-level.

```
# 요청
## 인증
## 요청 헤더
## 요청 바디
## 요청 파라미터
### 쿼리 파라미터
## 요청 예시

# 응답
## 응답(성공)
## 응답(실패)
## 응답(실패) 종류

# 비고
```

### `# 요청`

- **`## 인증`** — present only when the endpoint has an authentication requirement. Format:
  - A blockquote stating `> **필수(Required)**` or `> **선택(Optional)**`.
  - Prose explaining the requirement, plus a code block example if one is needed (e.g. the `Authorization` header shape).
  - Omit this section entirely for endpoints with no auth requirement — do not write a "N/A" placeholder.
- **`## 요청 헤더`** — only for headers other than the one already covered by `## 인증` (e.g. a custom header). If the only header-related fact is the auth requirement, that belongs in `## 인증`, not here — do not duplicate it as a bullet under `요청 헤더`.
- **`## 요청 바디`** — for endpoints that take a request body: a JSON example code block, followed by a table with columns `키 | 필수 여부 | 설명`. A page that labels this same content (a literal JSON request payload for a POST/PUT/PATCH endpoint) as `쿼리 파라미터`/`Query Parameters` is mislabeled — rename the heading to `요청 바디`; a query-parameter section is reserved for actual URL query strings.
- **`## 요청 파라미터` → `### 쿼리 파라미터`** (query string params) or **`### 경로 변수`** (path variables) — for endpoints with query parameters: a table with columns `이름 | 타입 | 설명 | 필수 여부`. For path parameters, which are inherently always required, the table omits `필수 여부` (columns: `이름 | 타입 | 설명`). The canonical path-parameter heading is `경로 변수` — not `Path 파라미터`, `경로 변수(Path Variable)`, or other translations. The `필수 여부` column header must read exactly `필수 여부` (not `필수` or `Null 허용 여부`/`NOT NULL`/`NULLABLE`); cell values may be a simple `✅`/`❌`/prose or a longer conditional rule (e.g. "첫 조회 시 불필요, 이후 조회 시 필요") when the requirement genuinely depends on context — that conditional text is factual content and must not be simplified away.
- The `필수 여부` column's values, in both `요청 바디` and parameter tables, are always the Korean words `필수`/`선택` (optionally combined with `✅`/`❌` or conditional prose) — never the English `` `required` ``/`` `optional` `` backtick style.
- **`## 요청 예시`** — only when there are multiple distinct usage scenarios worth illustrating separately (e.g. different filter/query combinations). Each scenario is a `### <scenario name>` subheading with its example.
- A page omits any of the above sub-sections that don't apply to it (e.g. a GET endpoint has no `요청 바디`).

### `# 응답`

- **`## 응답(성공)`** — one or more example code blocks. If both response headers and body matter, put them as sequential code blocks under this single heading rather than splitting into separate `응답 헤더(성공)` / `응답 바디(성공)` headings. If the endpoint has multiple success variants (e.g. `200 OK` vs `304 Not Modified`), label each with a bold inline marker (e.g. `**200 OK**`, `**304 Not Modified**`) followed by its own example, still under the single `응답(성공)` heading.
- **`## 응답(실패)`** — one representative failure-response JSON example.
- **`## 응답(실패) 종류`** — a table cataloging every possible failure for the endpoint, columns `원인 | 상태 | 코드 | 메시지`. This is a real `H2` nested under `# 응답` — never a sibling top-level `H1`, never nested deeper as `H3`, and never an unordered/numbered list in place of a table. An endpoint with no possible failures omits this section rather than including an empty table.

### `# 비고`

- Optional trailing section. Present only when there's genuinely something to note beyond the request/response spec.
- Must be a real `H1` heading named exactly `비고` — never a styled/underlined span made to look like a heading, and never named `기타 정보` or `<기타>`.
- Default presentation is plain bullets. Use toggle (`<details><summary>`) blocks only when there are several long, independent notes that benefit from being collapsed.

## Reconciliation rules

When a page's existing content doesn't match this skeleton, apply structural changes only — never alter a factual value (an error code, status, message, example payload, parameter name/type, or prose meaning):

- Merge same-target sections under different names into the canonical name (e.g. an `인증` fact written as a bullet under `요청 헤더`, `응답 예시(성공)`, or a bare `응답(성공)` `H1` all fold into the canonical heading listed above).
- Convert list-based failure catalogs (numbered list + JSON per item) into the `응답(실패) 종류` table — same causes/codes/messages, table form only.
- Rename table columns and values to the canonical names (e.g. `Null 허용 여부` → `필수 여부`; `` `required`/`optional` `` → `필수`/`선택`) without changing which parameters are actually required.
- Do not force genuinely different content into one shape: a body-based endpoint and a query-parameter-based endpoint use different sub-sections (`요청 바디` vs `쿼리 파라미터`) because they describe different things, not because of inconsistent formatting.
- A page with only a single failure example and no distinct list of causes is left as a single `응답(실패)` example with no `응답(실패) 종류` table — do not invent a cause description that isn't already stated verbatim on the page.
