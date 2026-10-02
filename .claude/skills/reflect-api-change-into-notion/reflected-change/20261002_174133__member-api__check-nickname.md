# Reflected Notion Change — 20261002_174133

Run ID: 20261002_174133
Source Detected-Change File: detected-change/detected_20261002_174133.md
Target Notion Database: 회원 API
Target Notion Page: 회원 닉네임 중복 확인
Notion Page State: Existing (fetch before editing)
Change Summary: Append a `swear_contained_nickname` failure row to `응답(실패) 종류`.
Note: Append the row after the existing `닉네임의 형식이 유효하지 않음` row, matching this page's existing un-backticked cell style.

## Notion Content

```markdown
## 응답(실패) 종류
| 원인 | 상태 | 코드 | 메시지 |
|---|---|---|---|
| 비속어가 포함된 닉네임 | 400 | swear_contained_nickname | 닉네임에 비속어가 포함되어 있습니다 |
```

## Provenance

- `swear_contained_nickname` row (400, message) ← `kr.modusplant.domains.member.adapter.controller.MemberController`: "`checkExistedNickname(MemberNicknameCheckRecord)` calls `Nickname.create(record.nickname())` and therefore reaches the same `SWEAR_CONTAINED_NICKNAME` failure" and "`GeneralErrorCode.SWEAR_CONTAINED_NICKNAME(HttpStatus.BAD_REQUEST.value(), "swear_contained_nickname", "닉네임에 비속어가 포함되어 있습니다")`"
