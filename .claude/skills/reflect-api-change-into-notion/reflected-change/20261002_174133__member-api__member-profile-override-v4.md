# Reflected Notion Change — 20261002_174133

Run ID: 20261002_174133
Source Detected-Change File: detected-change/detected_20261002_174133.md
Target Notion Database: 회원 API
Target Notion Page: 회원 프로필 덮어쓰기 - v4
Notion Page State: Existing (fetch before editing)
Change Summary: Replace the `swear_contained` failure row with `swear_contained_nickname` in `응답(실패) 종류`.
Note: Replace only the code and message cells of the existing `비속어가 포함된 닉네임` row; leave its cause text and every other row untouched.

## Notion Content

```markdown
## 응답(실패) 종류
| 원인 | 상태 | 코드 | 메시지 |
|---|---|---|---|
| 비속어가 포함된 닉네임 | 400 | `"swear_contained_nickname"` | `"닉네임에 비속어가 포함되어 있습니다"` |
```

## Provenance

- Removal of the `swear_contained` row ← `kr.modusplant.domains.member.adapter.controller.MemberController`: "`validateBeforeOverrideProfile` no longer throws `SwearContainedException` (`SwearErrorCode.SWEAR_CONTAINED`, `"swear_contained"`)"
- `swear_contained_nickname` row (400, message) ← `kr.modusplant.domains.member.adapter.controller.MemberController`: "`overrideProfile(MemberProfileOverrideRecord_V3)` and `overrideProfile(MemberProfileOverrideRecord_V4)` call `Nickname.create(record.nickname())`, which now throws `InvalidValueException(GeneralErrorCode.SWEAR_CONTAINED_NICKNAME, "nickname")`" and "`GeneralErrorCode.SWEAR_CONTAINED_NICKNAME(HttpStatus.BAD_REQUEST.value(), "swear_contained_nickname", "닉네임에 비속어가 포함되어 있습니다")`"
