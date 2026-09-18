# Reflected Notion Change — 20260917_162213

Run ID: 20260917_162213
Source Detected-Change File: detected-change/detected_20260917_162213.md
Target Notion Database: 회원 API
Target Notion Page: 회원 프로필 덮어쓰기 - v4
Notion Page State: New (create)
Change Summary: Create a new page documenting `PUT /api/v4/members/profile`, the S3-URL-returning
sibling of the already-published `회원 프로필 덮어쓰기 - v3` page.

Note: Title omits the word "API" to match this database's existing naming convention (siblings are
titled "회원 프로필 덮어쓰기 - v1" and "회원 프로필 덮어쓰기 - v3", not "... API - v1/v3"). This
database also carries page properties (MVP, Method, URI, 우선순위, 진행 상태, 페이지(다중 선택),
담당자) outside `document-format.md`'s body skeleton; see "## Properties" below — these are set by
precedent from the `회원 프로필 덮어쓰기 - v3` sibling page (same feature area, immediately adjacent
version), not derived from source code, except `URI`/`Method`/`기능` which are literal code facts.
Two apparent code/doc conflicts were checked directly against current source before drafting this file
(`NOT_FOUND_MEMBER_ID` HTTP status, `EXISTS_NICKNAME` code string) and found already resolved — both
the current code and the current `v3` page agree, so this page mirrors those same values with no
open discrepancy. One further note: the `v3` page documents `fileKey`/`introduction`/`nickname` under
`## 요청 바디` as a JSON payload, but `MemberRestController.java` binds all three as implicit query
parameters (`@RequestParam`, no `@RequestBody`) for both v3 and v4 — this page documents v4 correctly
as `## 요청 파라미터` → `### 쿼리 파라미터` per the actual code, which leaves `v3`'s page mislabeled
(out of scope for this run; flagged to the user separately).

## Properties

| Property | Value | Source |
|---|---|---|
| 기능 (title) | 회원 프로필 덮어쓰기 - v4 | naming convention of sibling pages |
| Method | PUT | `MemberRestController.java` `@PutMapping` |
| URI | /api/v4/members/profile | `MemberRestController.java` route |
| MVP | 3차 | mirrored from `회원 프로필 덮어쓰기 - v3` (adjacent version, same feature wave) |
| 우선순위 | 높음 | mirrored from v3 sibling |
| 진행 상태 | 완료 | mirrored from v3 sibling — code already exists and is in production shape |
| 페이지(다중 선택) | 마이페이지_내 정보_프로필 설정 페이지 | mirrored from v3 sibling |
| 담당자 | (left unset) | no source evidence for who implemented v4; not fabricated |

## Notion Content

```markdown
# 요청
## 인증
> **필수(Required)**
해당 API는 인증된 사용자만 접근할 수 있습니다.
요청 시 반드시 아래 헤더를 포함해야합니다.
```javascript
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```
## 요청 파라미터
### 쿼리 파라미터
| 이름 | 타입 | 설명 | 필수 여부 |
|---|---|---|---|
| fileKey | String | 갱신할 회원의 프로필 이미지 파일 키(코드상에서의 이미지 경로) | 선택 |
| introduction | String | 갱신할 회원의 프로필 소개 | 선택 |
| nickname | String | 갱신할 회원의 닉네임 | 필수 |

# 응답
## 응답(성공)
```plain text
cache-control: no-store,must-revalidate,private
```
```json
{
  "status": 200,                                      // integer
  "code": "generic_success",                          // String
  "message": "",                                      // String
  "data": {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",     // String(UUID)
    "imageUrl": "이미지로의 URL",                       // String
    "introduction": "프로필 소개",                      // String
    "nickname": "ModusPlantPlayer"                     // String
  }
}
```
## 응답(실패)
```json
{
  "status": 404,                                // integer
  "code": "not_found_member_id",                // String
  "message": "회원 아이디를 찾을 수 없습니다. "    // String
}
```
## 응답(실패) 종류
| 원인 | 상태 | 코드 | 메시지 |
|---|---|---|---|
| 존재하지 않는 회원 ID | 404 | `"not_found_member_id"` | `"회원 아이디를 찾을 수 없습니다. "` |
| 비속어가 포함된 닉네임 | 400 | `"swear_contained"` | `"값에 비속어가 포함되어 있습니다. "` |
| 중복된 닉네임 | 400 | `"exists_nickname"` | `"닉네임이 이미 존재합니다"` |
| 저장되어 있지 않은 S3 이미지 파일 | 404 | `"not_found_file_key_on_s3"` | `"Amazon S3에서 파일 키를 찾을 수 없습니다. "` |
| 프로필 이미지 파일 키의 형식이 올바르지 않음 | 400 | `"invalid_member_profile_image_path"` | `"회원 프로필 이미지 경로의 서식이 올바르지 않습니다. "` |
| 프로필 소개가 허용되는 길이(60자)를 초과함 | 400 | `"member_profile_introduction_over_length"` | `"회원 프로필 소개가 허용되는 길이를 초과하였습니다. "` |

# 비고
- v3(`PUT /api/v3/members/profile`) 대비 차이점: 응답 데이터의 `imagePath`(저장 경로) 대신, S3에서 생성한 `imageUrl`을 반환합니다. 요청 형식과 실패 케이스는 v3와 동일합니다.
```

## Provenance

- Route `PUT /api/v4/members/profile`, `@PreAuthorize("isAuthenticated()")` ← detected-change fact: MemberRestController `@PutMapping(value = "/v4/members/profile")` / `@PreAuthorize("isAuthenticated()")`
- `## 인증` = 필수(Required) ← detected-change fact: SecurityConfig `PUBLIC_ENDPOINTS` has no `PUT` entry matching this path; default `anyRequest().authenticated()` applies
- 쿼리 파라미터 table rows (`fileKey`/`introduction` 선택, `nickname` 필수) ← detected-change fact: MemberRestController parameter annotations (`@RequestParam(required = false)` ×2, `@NotBlank`+`@Pattern` on `nickname`)
- 응답(성공) body shape (`id`, `imageUrl`, `introduction`, `nickname`) ← detected-change fact: `MemberProfileResponseWithImageUrl` record fields
- 응답(실패) example / `not_found_member_id` row (404) ← detected-change fact: `MemberErrorCode.NOT_FOUND_MEMBER_ID(HttpStatus.NOT_FOUND.value(), ...)`
- `swear_contained` row (400) ← detected-change fact: `SwearErrorCode.SWEAR_CONTAINED`, thrown via `MemberController.validateBeforeOverrideProfile`
- `exists_nickname` row (400) ← detected-change fact: `KernelErrorCode.EXISTS_NICKNAME`, thrown via `ExistsEntityException(KernelErrorCode.EXISTS_NICKNAME, "nickname")` in `MemberController.validateBeforeOverrideProfile`
- `not_found_file_key_on_s3` row (404) ← detected-change fact: `AWSErrorCode.NOT_FOUND_FILE_KEY_ON_S3`, thrown via `memberImageIOHelper.validateIfImageExistsInStorage`
- `invalid_member_profile_image_path` row (400) ← detected-change fact: `MemberErrorCode.INVALID_MEMBER_PROFILE_IMAGE_PATH`, thrown via `MemberProfileImagePath.create(record.fileKey())`
- `member_profile_introduction_over_length` row (400) ← detected-change fact: `MemberErrorCode.MEMBER_PROFILE_INTRODUCTION_OVER_LENGTH`, thrown via `MemberProfileIntroduction.create(...)`
- 비고 note (imageUrl vs imagePath vs. v3) ← detected-change fact: "Only difference from the sibling `overrideMemberProfile_v3` ... v3 returns `MemberProfileResponseWithImagePath`, v4 returns `MemberProfileResponseWithImageUrl`"
