# Swear Filtering Policy

Policy that defines which user input is checked against the swear dictionary, how a match is handled, and at which point
of each flow the check runs.

Applies to every flow that reaches swear handling, either through the `maskSwear` method of `SwearHolder`
(`infrastructure/swear/SwearHolder`) or through `NicknameValidator` (`shared/kernel/validator/NicknameValidator`).

---

## 1. Filtering Targets

| Target | Handling | Applied by |
|---|---|---|
| comment content (register, content update) | masked | `CommentController` |
| member profile introduction (profile override, V3 and V4) | masked | `MemberController` |
| member profile introduction (social sign-up completion) | masked | `SocialIdentityController` |
| nickname | rejected with `SWEAR_CONTAINED_NICKNAME` (HTTP 400) | `NicknameValidator`, called by `NormalIdentityController` (sign-up), `SocialIdentityController` (sign-up completion), and `MemberController` (nickname check, profile override V3 and V4) |

Every other user input is stored as submitted, including post titles and contents.

---

## 2. Filtering Algorithm

### 2.1 Dictionary

- One dictionary, the swear words of the `swear` table and the not-swear words of the `not_swear` table, and one engine
  serve both masking and nickname rejection; a not-swear word contains a swear word but is not one (`시발점`).
- A dictionary change takes effect at the next application startup.
- Only Flyway `data/seed` migrations write `swear` and `not_swear`, and an applied seed migration is never edited.

### 2.2 Masking (`maskSwear`)

- Null or blank text is returned unchanged.
- An obfuscated spelling of a swear word is masked as the word it spells, and each run of overlapping or adjacent
  matches, whitespace inside it included, becomes one `***` (`시.발` → `***`, `시 발` → `***`).
- A match formed by the first characters of lines, and a not-swear word, are not masked.
- Masking never rejects the request.

### 2.3 Rejection (Nickname Write Paths)

Checks run in this order, each throwing on failure:
1. Null or blank, in `Nickname.create` → `EmptyValueException` (`EMPTY_NICKNAME`).
2. Mismatch with `PATTERN_NICKNAME`, in `Nickname.create` → `InvalidValueException` (`INVALID_NICKNAME_FORMAT`).
3. Any engine match through `SwearInspector.containsSwear`, regardless of the swear word's severity, in
   `NicknameValidator.validate`, which the write path's controller injects and calls right after `Nickname.create` →
   `InvalidValueException` (`SWEAR_CONTAINED_NICKNAME`); a not-swear word is accepted.

---

## 3. Per-Domain Timing and Order

### 3.1 Comment (`CommentController`)

**Register**:
1. The request values are converted to VOs.
2. The author's write permission within the post, the parent comment's existence (for a nested path), and the reply
   target (when given) are validated.
3. The content is masked.
4. The comment path is reserved in Redis; a duplicate request ends here.
5. The comment is saved with the masked content.
6. `CommentRegisterEvent` is published carrying the masked content.

**Content update**: the request values are converted to VOs, the caller is verified as the comment's author, the
content is masked, then the stored content is updated.

### 3.2 Member (`MemberController`)

**Profile override** (V3 and V4):
1. `Nickname.create` and `NicknameValidator.validate` run on the submitted nickname, so a swear nickname is rejected
   before any other check.
2. The pre-override validation (`validateBeforeOverrideProfile`) runs.
3. The stored profile is loaded and its current image is deleted.
4. The introduction is masked.
5. The profile is rebuilt with the masked introduction and saved.

**Nickname check**: `Nickname.create` and `NicknameValidator.validate` run before the existence lookup, so a swear
nickname is rejected instead of receiving an existence result.

### 3.3 Normal Identity (`NormalIdentityController`)

**Sign-up**:
1. An existing email is rejected with a conflict error code chosen by its auth provider.
2. `Nickname.create` and `NicknameValidator.validate` run on the submitted nickname, then a duplicate nickname is
   rejected.
3. The sign-up data is built (building `Nickname` again) and saved.

### 3.4 Social Identity (`SocialIdentityController`)

**Sign-up completion**:
1. An already signed-up email is rejected with `ALREADY_SIGNED_UP`.
2. `Nickname.create` and `NicknameValidator.validate` run on the submitted nickname, then the social member profile is
   built with it.
3. The agreed terms are built from the submitted term versions.
4. The introduction is masked.
5. The member, its auth record, its profile carrying the masked introduction, and its agreed terms are saved.

### 3.5 Stored Nicknames

The JPA mappers that rebuild a member's `Nickname` from a stored value (`MemberJpaMapperImpl`,
`MemberProfileJpaMapperImpl`, `MemberProfileRepositoryJpaAdapter`, and `SocialIdentityJpaMapperImpl`) call only
`Nickname.create`, which runs no swear check, so a stored nickname never fails to load because of the dictionary.
