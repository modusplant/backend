---
name: fetch-community-webpage-url
description: Provides url/result-url.txt, a list of representative internet community website URLs (global, Korean, and ModusPlant-direction-matched), fetching candidates from fixed sources and filtering them when needed.
arguments: [needsRefetching, fetchCount]
allowed-tools: Bash(python3 .claude/skills/fetch-community-webpage-url/scripts/pipeline_and_return_url.py*), Bash(TZ=Asia/Seoul date*), mcp__claude_ai_Atlassian_Rovo__searchConfluenceUsingCql, mcp__claude_ai_Atlassian_Rovo__getConfluencePage, WebSearch, mcp__playwright__browser_navigate, mcp__playwright__browser_evaluate, mcp__playwright__browser_close, Edit(.claude/skills/fetch-community-webpage-url/preparation/**)
disallowed-tools: Edit(/.claude/skills/fetch-community-webpage-url/url/custom-url.txt)
---

# Workflow
- `fetchCount` defaults to `50` when not given.
- Run every script command from the project root as `python3 .claude/skills/fetch-community-webpage-url/scripts/pipeline_and_return_url.py <command>`.
- **Exit code 1 (`ERROR`) from any script command:** report the error line as-is and stop.

## 1. Prepare
1. **Short-circuit:** If `$needsRefetching` is not `true` and `url/result-url.txt` exists, provide its content and stop.
2. **Planning direction:** If `preparation/planning-direction.md` is missing, the direction is changed. Otherwise, on cloud ID `modusplant-project.atlassian.net`, run:
   1. CQL `ancestor = 24248322 AND type = page`. If the returned page-ID set differs from `source-pages` in the header, the direction is changed.
   2. CQL `ancestor = 24248322 AND type = page AND lastmodified >= "<checked-at>"`, with `checked-at` from the header. If any page is returned, the direction is changed.
   - **If direction changed:**
     1. Run `TZ=Asia/Seoul date '+%Y/%m/%d %H:%M'` and keep the output as the new `checked-at`.
     2. Read every page from the CQL `ancestor = 24248322 AND type = page` results with `getConfluencePage` (markdown).
     3. Rewrite `preparation/planning-direction.md` in the **Planning Direction Format** below.
   - **Otherwise:** reuse the existing file unchanged.
3. **Topic keywords:** If the direction changed or `preparation/topic-keyword.md` is missing, derive keywords from `preparation/planning-direction.md` and rewrite `preparation/topic-keyword.md` in the **Topic Keyword Format** below. Otherwise, reuse it unchanged.

## 2. Fetch
Fetch candidates from each source in **Sources** at its `Related Step`.
1. **Web search (`web-search`):** Build one query per keyword in `preparation/topic-keyword.md`.
   - **`## Global`:** Query `<keyword> community forum`. Run them all with WebSearch in `standard` mode in a single turn.
   - **`## Korea`:** Query `<keyword> 커뮤니티` on Naver. For each query, `browser_navigate` to `https://search.naver.com/search.naver?query=<URL-encoded query>`, 
     then `browser_evaluate` with the content of `query/extract-naver-search-link.js`. Run `browser_close` after the last query. If the Playwright MCP tools are unavailable, report that and stop.
   - From each query's results, take the site URL of up to `fetchCount` results whose page is about plants or gardening (a plant or gardening site, or a plant-specific board, group, or sub-community of a general platform).
2. **Script sources (`global-wiki`, `korean-wiki`):** Run `fetch --count <fetchCount> --web-search <site URL>...` with every site URL from step 1. It fetches `global-wiki` and `korean-wiki`, then normalizes and deduplicates them together with the step 1 URLs into `url/candidate-url.json`.

### Sources
| Source      | Producer | Related Step | Cap                                 | Origin                                                                    |
|-------------|----------|--------------|-------------------------------------|---------------------------------------------------------------------------|
| web-search  | Claude   | 1            | `fetchCount` per query              | WebSearch for `## Global`; Naver via Playwright MCP for `## Korea`        |
| global-wiki | script   | 2            | top `fetchCount` items by sitelinks | `query/select-url-from-global-wiki.sparql` (the script appends `LIMIT`)   |
| korean-wiki | script   | 2            | first `fetchCount` category members | `분류:대한민국의_인터넷_커뮤니티` members, resolved to Wikidata P856 or the infobox URL |
| custom      | user     |              | none                                | `url/custom-url.txt`, one URL per line; bypasses the filter chain         |

- **custom:** No fetch step reads it. `write-result` adds it to the result in **Return**.
- **korean-wiki unresolved pages:** Pages that resolve to no website are appended to `scripts/unresolved-korean-wiki-url.txt` and skipped on later runs. Delete a line to make the script retry that page.

## 3. Filter Chain
Apply the filters in order. Each filter only looks at candidates that are not yet dropped, and `url/custom-url.txt` URLs skip the chain.

| Order | Filter       | Performed by                         | Drops a candidate when                         |
|-------|--------------|--------------------------------------|------------------------------------------------|
| 1     | normalize    | `fetch`                              | it is not a valid URL, or it duplicates one    |
| 2     | reachability | `filter-reachability`                | it is unreachable or redirects to another host |
| 3     | suitability  | Claude, then `write-result --reject` | it fails the **Selection Criteria**            |

- **Blocked:** A candidate that answers status `400`, `401`, `403`, or `429` is not dropped by the reachability filter. `filter-reachability` records it in `blockedBy` instead, and it stays a suitability target.
- **Suitability:** After `filter-reachability`, read `url/candidate-url.json` and judge every candidate whose `droppedBy` is `null`, blocked or not, against the **Selection Criteria** by the site as a whole. Collect the failing URLs for step 4.

### Selection Criteria
- **Included:** registered users publish posts and reply to or react to each other.
- **Excluded:** news or blog articles, listicles, community directories or trackers, mirrors of another site, app-store pages, shops, encyclopedias or image libraries, organization news or event pages, 
  single posts or threads, and sites whose established, dominant identity is crime, hate, condemnation of others, obscenity, or a similar purpose, regardless of the purpose they were founded for.

## 4. Return
1. Run `write-result --reject <URL>...` with the URLs that failed the suitability filter. It writes `url/result-url.txt` as the sorted union of the remaining unblocked candidates and `url/custom-url.txt`, and `url/blocked-url.txt` as the remaining blocked candidates only.
2. Provide only the following, so that the returned URLs need no further judgment:
   - the content of `url/result-url.txt`;
   - the `added` and `removed` counts of `url/blocked-url.txt` from the `SUMMARY: blocked` line;
   - every `WARNING: custom:` line from step 1, which names a `url/custom-url.txt` line skipped as an invalid URL.

# File Formats

## Planning Direction Format
- `checked-at` is in KST at minute precision (`yyyy/MM/dd HH:mm`). Prose is English.

```markdown
<!-- source-pages: <id> | <id> | ... ; checked-at: <yyyy/MM/dd HH:mm> -->
# Planning Direction
## Target Users
## Core Value
```

## Topic Keyword Format
- `## Global` lists English keywords, and `## Korea` lists Korean keywords, one per bullet.

```markdown
# Topic Keywords
## Global
## Korea
```

## Candidate URL Format
- `sources` lists every source that produced the URL. `blockedBy` is `null` or `status-<code>`. `droppedBy` is `null` for a surviving candidate, `reachability:<reason>`, or `suitability`.

```json
[{"url": "https://<host>/", "sources": ["global-wiki", "web-search"], "blockedBy": null, "droppedBy": null}]
```

# Constraint
- **Single Writer:** Only `scripts/pipeline_and_return_url.py` writes `scripts/unresolved-korean-wiki-url.txt` and the files under `url/`, except `url/custom-url.txt`. The script normalizes every URL to `https://<host>/`.
- **Output Purity:** `url/result-url.txt`, `url/blocked-url.txt`, `url/custom-url.txt`, and `scripts/unresolved-korean-wiki-url.txt` contain one URL per line and nothing else.
