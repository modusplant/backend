# Swear Filtering Policy

Policy that defines which user input is checked against the swear dictionary, how a match is handled, and at which point
of each flow the check runs.

Applies to every flow that reaches `SwearHolder` (`shared/framework/jdbc/holder/SwearHolder`), either through its
`filterSwear` method or through the `Nickname` kernel VO (`shared/kernel/Nickname`).

---

## 1. Filtering Targets

| Target | Handling | Applied by |
|---|---|---|
| comment content (register, content update) | masked | `CommentController` |
| member profile introduction (profile override, V3 and V4) | masked | `MemberController` |
| member profile introduction (social sign-up completion) | masked | `SocialIdentityController` |
| nickname | rejected with `SWEAR_CONTAINED_NICKNAME` (HTTP 400) | `Nickname.create`, wherever the VO is built |

Every other user input is stored as submitted, including post titles and contents.

---

## 2. Filtering Algorithm

### 2.1 Dictionary

- Loaded once at application startup (`@PostConstruct`) from the `word` column of the `swear` table and kept as an
  immutable, duplicate-free list sorted in the application order of section 2.2; a change to the table takes effect
  only after a restart.
- Every two-character word also gets three variants with one character inserted between its two characters: a space,
  `1`, or `2` (e.g. `ab` → `a b`, `a1b`, `a2b`).
- Code outside Spring beans reads the same list through the static `getStaticSwearWords()`, which throws
  `NotInitializedException` (`HOLDER_NOT_INITIALIZED`) before the holder is initialized; only `Nickname` uses it.

### 2.2 Masking (`filterSwear`)

- Null or blank text is returned unchanged.
- For each dictionary word the text contains, every occurrence is replaced with as many `*` as the word has characters,
  so the text length is preserved.
- Matching is a case-sensitive exact substring match; the input is not normalized.
- Words are applied longest first, ties in natural string order, so a word containing a shorter word is masked whole
  before the shorter word can split it (e.g. `abc` is masked before `ab`).
- Masking never rejects the request.

### 2.3 Rejection (`Nickname.create`)

Checks run in this order, each throwing on failure:
1. Null or blank → `EmptyValueException` (`EMPTY_NICKNAME`).
2. Mismatch with `PATTERN_NICKNAME` → `InvalidValueException` (`INVALID_NICKNAME_FORMAT`).
3. Contains any dictionary word as a substring, matched as in section 2.2 → `InvalidValueException`
   (`SWEAR_CONTAINED_NICKNAME`).

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
1. `Nickname.create` runs on the submitted nickname, so a swear nickname is rejected before any other check.
2. The pre-override validation (`validateBeforeOverrideProfile`) runs.
3. The stored profile is loaded and its current image is deleted.
4. The introduction is masked.
5. The profile is rebuilt with the masked introduction and saved.

**Nickname check**: `Nickname.create` runs before the existence lookup, so a swear nickname is rejected instead of
receiving an existence result.

### 3.3 Normal Identity (`NormalIdentityController`)

**Sign-up**:
1. An existing email is rejected with a conflict error code chosen by its auth provider.
2. `Nickname.create` runs on the submitted nickname, then a duplicate nickname is rejected.
3. The sign-up data is built (building `Nickname` again) and saved.

### 3.4 Social Identity (`SocialIdentityController`)

**Sign-up completion**:
1. An already signed-up email is rejected with `ALREADY_SIGNED_UP`.
2. `Nickname.create` runs while the social member profile is built.
3. The agreed terms are built from the submitted term versions.
4. The introduction is masked.
5. The member, its auth record, its profile carrying the masked introduction, and its agreed terms are saved.

### 3.5 Stored Nicknames

The JPA mappers that rebuild a member's `Nickname` from a stored value call `Nickname.create`, so the rejection check
also runs on reads: `MemberJpaMapperImpl`, `MemberProfileJpaMapperImpl`, `MemberProfileRepositoryJpaAdapter`, and
`SocialIdentityJpaMapperImpl`. A stored nickname that contains a word added to the dictionary later fails to load with
`SWEAR_CONTAINED_NICKNAME`.
