# Reflected Notion Change — 20261007_112317

Run ID: 20261007_112317
Source Detected-Change File: detected-change/detected_20261007_112317.md
Target Notion Database: 검색 API
Target Notion Page: 키워드로 식물 국명 목록 조회
Notion Page State: Existing (fetch before editing)
Change Summary: Rename the searched subject from "식물 국명" to "식물명" in the page title, the `기능` property, and the `size` query-parameter description.
Note: Page title and the `기능` property become `키워드로 식물명 목록 조회`; the response example keeps the `koreanName` key unchanged.

## Notion Content

Page title / `기능` property: `키워드로 식물명 목록 조회`

### 쿼리 파라미터 (row `size` only)

| 이름 | 타입 | 설명 | 필수 여부 |
|------|------|------|-----------|
| size | Integer | 요청할 식물명 수 | ✅ |

## Provenance

- Page title / `기능` `키워드로 식물명 목록 조회` ← `kr.modusplant.domains.search.framework.inbound.web.rest.SearchRestController`: `@Operation` summary is `키워드를 통한 식물명 목록 검색 API`
- `size` description `요청할 식물명 수` ← `kr.modusplant.domains.search.framework.inbound.web.rest.SearchRestController`: `size` query parameter `@Schema` description is `조회할 식물명의 수`
- `koreanName` key unchanged ← `kr.modusplant.domains.search.framework.inbound.web.rest.SearchRestController`: `plantName` serialized as `koreanName` via `@JsonProperty("koreanName")`
