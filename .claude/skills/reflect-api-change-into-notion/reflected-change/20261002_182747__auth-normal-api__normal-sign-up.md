# Reflected Notion Change — 20261002_182747

Run ID: 20261002_182747
Source Detected-Change File: detected-change/detected_20261002_182747.md
Target Notion Database: 일반 인증 인가 API
Target Notion Page: 일반 회원가입
Notion Page State: Existing (fetch before editing)
Change Summary: Add the swear-contained-nickname failure (400, `swear_contained_nickname`) to the `응답(실패) 종류` table of `POST /api/members/register`.
Note: Insert the row directly after the existing `중복된 닉네임` row and match that table's existing quote/backtick cell style; leave every other row untouched.

## Notion Content

`## 응답(실패) 종류` — new row:

| 원인 | 상태 | 코드 | 메시지 |
|---|---|---|---|
| 비속어가 포함된 닉네임 | 400 | “`swear_contained_nickname`” | “`닉네임에 비속어가 포함되어 있습니다`” |

## Provenance

- Sign-up fails when the nickname contains a swear word (cause `비속어가 포함된 닉네임`) ← `kr.modusplant.domains.account.normal.adapter.controller.NormalIdentityController`: `registerNormalMember(NormalSignUpRequest)` calls `Nickname.create(request.nickname())`, which throws `InvalidValueException(GeneralErrorCode.SWEAR_CONTAINED_NICKNAME, "nickname")` for a nickname containing a swear word
- Status `400`, code `swear_contained_nickname`, message `닉네임에 비속어가 포함되어 있습니다` ← `kr.modusplant.domains.account.normal.adapter.controller.NormalIdentityController`: `GeneralErrorCode.SWEAR_CONTAINED_NICKNAME(HttpStatus.BAD_REQUEST.value(), "swear_contained_nickname", "닉네임에 비속어가 포함되어 있습니다")`
- Row placed after `중복된 닉네임` ← `kr.modusplant.domains.account.normal.adapter.controller.NormalIdentityController`: the swear check runs before the nickname-existence check (`KernelErrorCode.EXISTS_NICKNAME`)
