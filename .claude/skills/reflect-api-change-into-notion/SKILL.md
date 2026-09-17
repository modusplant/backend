---
name: reflect-api-change-into-notion
description: Detects API-relevant codebase changes in the member/comment/search domains and the security-related auth surface, then produces and applies Notion-ready edit instructions for the corresponding API-specification documents.
disable-model-invocation: true
allowed-tools: Write(.claude/skills/reflect-api-change-into-notion/detected-change/**) Edit(.claude/skills/reflect-api-change-into-notion/detected-change/**) Write(.claude/skills/reflect-api-change-into-notion/reflected-change/**) Edit(.claude/skills/reflect-api-change-into-notion/reflected-change/**)
disallowed-tools: Write(/src/**) Edit(/src/**) Write(.claude/skills/reflect-api-change-into-notion/SKILL.md) Edit(.claude/skills/reflect-api-change-into-notion/SKILL.md) Write(.claude/skills/reflect-api-change-into-notion/document-format.md) Edit(.claude/skills/reflect-api-change-into-notion/document-format.md)
---

# Preconditions

- This skill only reads source under `src/main/java/` and only writes under its own
  `detected-change/` and `reflected-change/` subdirectories, plus Notion pages via the Notion MCP
  tools. It never edits application code.
- `@.claude/skills/reflect-api-change-into-notion/document-format.md` is the finalized, canonical
  Notion page shape. Treat it as read-only — only reference it when composing `## Notion Content` blocks below.

# Target Classes

- Primary: classes created, modified, or deleted since the previous session.
- Fallback - 1: if no such classes exist, run `git status --porcelain | awk '{print $NF}' | grep '\.java$'` and use its output instead.
- Fallback - 2: if that also yields nothing, run `git diff --name-only HEAD~1 HEAD | grep '\.java$' | awk -F/ '{print $NF}' | sed 's/\.java$//'` and use its output instead.
- Fallback - 3: if that also yields nothing, run `git diff --name-only HEAD~2 HEAD | grep '\.java$' | awk -F/ '{print $NF}' | sed 's/\.java$//'` and use its output instead.
- Termination: if no result was found, terminate the skill immediately.

# Watched API Surface

After the Target Classes cascade resolves a list, drop every class that isn't a file listed or
matched by a pattern in the "Codebase → Notion Mapping" table below (e.g. `domains/post/**`,
`domains/notification/**`, and `domains/account/social/**` are all out of scope).

- Termination — Post-Filter: if filtering leaves zero classes, terminate immediately. State which
  classes were dropped and why. Do not create a `detected-change` file for an empty set.

# Codebase → Notion Mapping

| Notion DB (literal Korean breadcrumb under `API 명세`) | db-slug           | `*RestController.java` → `요청 헤더`/`요청 바디`/`요청 파라미터`(`쿼리 파라미터`/`경로 변수`), jointly with `SecurityConfig` → `## 인증`                                                                                                                                                                                                                                                                                                      | `*Response.java` → `응답(성공)`                                                                                                   | Failure-cause sources (adapter `*Controller.java` + `aggregate`/`entity`/`vo`) → `응답(실패) 종류` causes                                                                                                                                                                            | `*ErrorCode.java` → `응답(실패) 종류` `상태`/`코드`/`메시지`                                                                                                                                                                                                                                                                                         |
|------------------------------------------------------|-------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `회원 API`                                             | `member-api`      | `domains/member/framework/inbound/web/rest/MemberRestController.java`                                                                                                                                                                                                                                                                                                                                               | `domains/member/usecase/response/*.java` returned by `MemberController`, `usecase/response/supers/MemberProfileResponse.java` | `domains/member/adapter/controller/MemberController.java`; `domain/aggregate/{Member,MemberProfile,ProposalOrBugReport}.java`; `domain/entity/{MemberProfileImage,ProposalOrBugReportImage}.java`+`nullobject/EmptyMemberProfileImage.java`; `domain/vo/*.java`+`nullobject/*` | `domain/exception/enums/MemberErrorCode.java`                                                                                                                                                                                                                                                                                           |
| `어드민 API`                                            | `admin-api`       | `domains/member/framework/inbound/web/rest/MemberAdminRestController.java`                                                                                                                                                                                                                                                                                                                                          | same response package, filtered to what `MemberAdminController` returns                                                       | `domains/member/adapter/controller/MemberAdminController.java`; same member aggregate/entity/vo set (no admin/member split at domain layer)                                                                                                                                    | `MemberErrorCode.java` (shared with 회원 API); `## 인증` is always **필수(Required)** + `ADMIN` authority (class-level `@PreAuthorize("hasAuthority('ADMIN')")` + `SecurityConfig`'s `/api/admin/**` rule)                                                                                                                                    |
| `소통 댓글 API`                                          | `comment-api`     | `domains/comment/framework/inbound/web/rest/CommentRestController.java`                                                                                                                                                                                                                                                                                                                                             | `domains/comment/usecase/response/{CommentOfPostResponse,CommentPageResponse,CommentResponse}.java`                           | `domains/comment/adapter/controller/CommentController.java`; `domain/aggregate/Comment.java`; `domain/vo/{Author,CommentContent,CommentPath,CommentStatus,PostId}.java`                                                                                                        | `domain/exception/enums/CommentErrorCode.java`                                                                                                                                                                                                                                                                                          |
| `검색 API`                                             | `search-api`      | `domains/search/framework/inbound/web/rest/SearchRestController.java`                                                                                                                                                                                                                                                                                                                                               | `domains/search/usecase/response/{SearchPostRelevanceSortedPageResponse,SearchPostResponse}.java`                             | `domains/search/adapter/controller/{SearchPlantController,SearchPostController}.java`; `domain/aggregate/SearchPost.java`; `domain/entity/SearchPostOption.java`; `domain/vo/*.java`+`nullobject/*`                                                                            | `domain/exception/enums/SearchErrorCode.java`                                                                                                                                                                                                                                                                                           |
| `인증 인가 API(공통)`                                      | `auth-common-api` | `domains/account/identity/framework/inbound/web/rest/IdentityRestController.java` (`GET /api/v1/members/{id}/auth-info`, dummy `POST /api/auth/logout`), `infrastructure/jwt/framework/inbound/web/rest/TokenRestController.java` (`POST /api/auth/token/refresh`)                                                                                                                                                  | `domains/account/identity/usecase/response/IdentityAuthResponse.java`, `infrastructure/jwt/response/TokenResponse.java`       | `domains/account/identity/adapter/controller/IdentityController.java` (Token endpoint has no separate adapter Controller — treat `TokenRestController` itself as the cause source)                                                                                             | `infrastructure/security/enums/SecurityErrorCode.java`, `infrastructure/jwt/exception/enums/AuthTokenErrorCode.java`, `infrastructure/security/exception/*` hierarchy (`BusinessAuthenticationException` root, `AccountStateException`, `BadCredentialException`, `BannedException`, `DisabledByLinkingException`, `InactiveException`) |
| `일반 인증 인가 API`                                       | `auth-normal-api` | `domains/account/normal/framework/inbound/web/rest/NormalIdentityRestController.java` (`POST /api/auth/login` dummy, `POST /api/members/register`, `POST /api/v1/members/{id}/modify/email`, `POST /api/v1/members/{id}/modify/password`), `domains/account/email/framework/inbound/web/rest/EmailIdentityRestController.java` (`POST /api/members/verify-email(/send)`, `POST /api/auth/reset-password-request/*`) | responses returned by `NormalIdentityController`/`EmailIdentityController` (enumerate at implementation time)                 | `domains/account/normal/adapter/controller/NormalIdentityController.java`, `domains/account/email/adapter/controller/EmailIdentityController.java`                                                                                                                             | `domains/account/normal/domain/exception/enums/NormalIdentityErrorCode.java`, `domains/account/email/domain/exception/enums/EmailIdentityErrorCode.java`, `domains/account/shared/exception/enums/AccountErrorCode.java`                                                                                                                |

Cross-cutting (applies to every row above, not owned by a single one):

- `infrastructure/security/config/SecurityConfig.java` — ground truth for every endpoint's
  `## 인증` fact, in all 6 databases. Bean-validation-only failures (a bare `@NotBlank`/`@NotNull`
  with no domain VO behind it) map to `GeneralErrorCode.CONSTRAINT_VIOLATION`/`EMPTY_VALUE` in the
  `응답(실패) 종류` table, not to a domain `*ErrorCode`.

### `## 인증` derivation algorithm

1. If `(HTTP method, path)` matches an entry in `SecurityConfig`'s `PUBLIC_ENDPOINTS` map **and**
   the method still reads an `@AuthenticationPrincipal` for optional personalization (e.g. an
   anonymous-allowed GET that computes a per-viewer field when a token is present) → `## 인증` =
   `> **선택(Optional)**`.
2. If it matches `PUBLIC_ENDPOINTS` with no such optional read → omit `## 인증` entirely.
3. Otherwise (the `anyRequest().authenticated()` default applies) → `## 인증` = `> **필수(Required)**`.
4. If the path additionally falls under `/api/admin/**` → note the extra `ADMIN` authority
   requirement in the `## 인증` prose.

# Detected-Change File Format

Path: `detected-change/detected_<RUN_ID>.md`, where `RUN_ID = YYYYMMDD_HHmmss` (from `date +%Y%m%d_%H%M%S`
at the moment the run starts). Files accumulate — never overwrite a prior run's file. On a
same-second collision, append `_2`, `_3`, ... to `RUN_ID`.

Content is **pure codebase fact** — no Notion database/page names, no target-format vocabulary,
no "what to change in Notion" language. One `##` block per changed class:

```markdown
# Detected Code Change — <RUN_ID>

Run ID: <RUN_ID>
Detected At: <ISO 8601 timestamp>
Target-Class Resolution: <Primary | Fallback-1 | Fallback-2 | Fallback-3>
Target Classes: <comma-separated class simple names>

## <fully-qualified class name>

- Change Kind: <Added | Modified | Deleted | Pre-existing gap (not a code change)>
- Layer: <RestController | Response | Controller | Aggregate | Entity | VO | ErrorCode | SecurityConfig>
- Domain/Package: <e.g. domains.comment>
- Facts:
  - <one bullet per literal fact: annotation, signature change, field add/remove, enum constant, etc.>
- Relevant Code Excerpt:
  ```java
  <short excerpt, only the lines the facts above refer to>
  ```

Necessary-only: omit any class/section with no actual change; never restate unchanged facts.

# Reflected-Change File Format

Path: `reflected-change/<RUN_ID>__<db-slug>__<page-slug>.md` — one file per Notion page, per run.
`<RUN_ID>` **must equal** the `detected-change` file's `RUN_ID` it was derived from — this is the
only linkage between the two directories. Never derive a reflected-change file from any `detected-change` 
file other than the one matching its own `RUN_ID`, and never apply to Notion a reflected-change file 
that isn't the latest for its `<db-slug>__<page-slug>` pair.

```markdown
# Reflected Notion Change — <RUN_ID>

Run ID: <RUN_ID>
Source Detected-Change File: detected-change/detected_<RUN_ID>.md
Target Notion Database: <literal Korean DB name>
Target Notion Page: <page title>
Notion Page State: <Existing (fetch before editing) | New (create)>
Change Summary: <one sentence>
Note: <optional, one line — only when this page's apply procedure deviates from "Notion Update
  Procedure" below, e.g. an already-present-but-empty heading, or existing content to leave
  untouched unless redundant>

## Notion Content

<the literal document-format.md-shaped fragment(s) to add/replace — real heading text, real table
columns in canonical order, `필수`/`선택` not `required`/`optional`>

## Provenance

- <each fact above> ← detected-change fact: <the specific bullet it came from>
```

# Workflow

1. Resolve Target Classes → apply the Watched API Surface filter → run the Termination checks.
2. Write exactly one `detected-change/detected_<RUN_ID>.md` covering every remaining class.
3. For every Notion page touched by those classes (per the mapping table), write one
   `reflected-change/<RUN_ID>__<db-slug>__<page-slug>.md`, derived only from the detected-change
   file just written in step 2.
4. For each affected page, take its latest reflected-change file by `RUN_ID` and apply it per the
   "Notion Update Procedure" below.

# Notion Update Procedure

- Locate each target page by searching inside its named Notion database first — never guess or
  reuse a page URL from a prior run.
- Fetch the page's current content before editing; `document-format.md` never substitutes for the
  actual current content.
- Insert or replace only what `## Notion Content` specifies, positioned per `document-format.md`'s
  skeleton order; touch no other section. Use `document-format.md`'s Reconciliation Rules for any
  structural (non-factual) adjustments needed to fit the existing page into the canonical skeleton.
- Follow any exception in the reflected-change file's `Note:` field, if present.
- Never write a fact into Notion that isn't traceable through the reflected-change file's
  `## Provenance` section back to a `detected-change` bullet.

# Hard Constraints

- English-only in this skill's own files; Korean is permitted only where a literal string must
  round-trip exactly into/out of Notion (headings, column names, `필수`/`선택`, example values).
- Never fabricate a fact not present in the actual source code or the current Notion page content.
- Every file in `detected-change/` follows the exact schema above; every file in `reflected-change/`
  follows its exact schema above — consistency is required within each directory, not across them.
- Every reflected-change file is self-contained: it must be understandable and appliable without
  re-reading the detected-change file, beyond citing it for provenance.
- Scope is limited to the 6 databases and the domains/packages named in the mapping table above —
  never extend to `domains/post`, `domains/notification`, `domains/term`, or
  `domains/account/social/**`.
