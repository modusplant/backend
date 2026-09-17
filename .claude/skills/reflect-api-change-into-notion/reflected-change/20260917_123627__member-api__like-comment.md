# Reflected Notion Change — 20260917_123627

Run ID: 20260917_123627
Source Detected-Change File: detected-change/detected_20260917_123627.md
Target Notion Database: 회원 API
Target Notion Page: 댓글 좋아요 API (best match from `MemberRestController.likeCommunicationComment`'s `@Operation` summary — confirm exact title via `notion-search` before editing)
Notion Page State: Existing (fetch before editing)
Change Summary: add the previously-undocumented `postUlid`/`path` path variables as a canonical `### 경로 변수` table.

## Notion Content

### 경로 변수

| 이름 | 타입 | 설명 |
|---|---|---|
| postUlid | String | 좋아요를 누를 댓글의 게시글 식별자 |
| path | String | 좋아요를 누를 댓글의 경로 |

## Provenance

- `postUlid` row ← detected-change fact: "`likeCommunicationComment` (`PUT .../post/{postUlid}/path/{path}`): two path variables — `postUlid` (`String`, ULID) and `path` (`String`, materialized path, e.g. `1.0.4`)."
- `path` row ← same detected-change fact.
- Description text ← the method's own `@Parameter` descriptions for `postUlid` and `path`.
