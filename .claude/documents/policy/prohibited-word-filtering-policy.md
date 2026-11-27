# Prohibited Word Filtering Policy

Policy that defines which user input is checked against the prohibited word dictionary, how a match is handled, and at
which point of each flow the check runs.

Applies to every flow that reaches prohibited word handling, either through the `maskProhibitedWord` method of
`ProhibitedWordHolder` (`infrastructure/prohibitedword/ProhibitedWordHolder`) or through `NicknameValidator`
(`shared/kernel/validator/NicknameValidator`).

---

## Parent Document

- `.claude/documents/term/text-normalization-term.md` — the view, lightly normalized text, and token terms (§2.1)

---

## 1. Filtering Targets

| Target | Handling | Applied by |
|---|---|---|
| comment content (register, content update) | masked | `CommentController` |
| member profile introduction (profile override, V3 and V4) | masked | `MemberController` |
| member profile introduction (social sign-up completion) | masked | `SocialIdentityController` |
| nickname | rejected with `PROHIBITED_WORD_CONTAINED_NICKNAME` (HTTP 400) | `NicknameValidator`, called by `NormalIdentityController` (sign-up), `SocialIdentityController` (sign-up completion), and `MemberController` (nickname check, profile override V3 and V4) |

Every other user input is stored as submitted, including post titles and contents.

---

## 2. Filtering Algorithm

### 2.1 Dictionary

- One dictionary, the prohibited words of the `prohibited_word` table and the allowed words of the `allowed_word`
  table, and one engine serve both masking and nickname rejection.
- A dictionary change takes effect at the next application startup.
- Only Flyway `data/seed` migrations write `prohibited_word` and `allowed_word`, and an applied seed migration is never
  edited.
- Every row a seed migration inserts meets these entry rules:
  - A prohibited word is entered only when its false-positive likelihood is 1 or less, counting every match the engine
    makes in normal text: as a substring of a normal word (`개년` in `5개년`), as a bare form (`18` in ages and dates),
    and as a match across spaced tokens (`십 알`). Allowed words for the few normal words it matches can bring its
    likelihood to 1 or less (`시발` with `시발점`).
  - A lexical variant is entered as a prohibited word, not derived by an engine rule, because a general rule for it
    matches normal words (folding the tense `ㅆ` into `ㅅ` turns `씨바` into `시바`, as in `시바견`).
  - A lexical variant takes the `type` and `severity` of its base word, so it counts the same as the spelling it evades.
  - A prohibited word's `script` is the script of its own characters (`씨x` → `HANGUL`, `sibal` → `LATIN`, `🖕` →
    `OTHER`), because the application fails to start when a `script` differs from the one its lightly normalized text
    gives.
  - A word is entered as its lightly normalized text.
  - A candidate is not entered when, in some view built from its text, the engine matches another word of the same table
    over the whole text (`시 발`, `시이발`, `시바알` for `시발`). Among candidates that match one another this way, only
    the one the others reduce to is entered (`시발`, not `시바알`). The reason is that the engine compares a stored word
    with each view's text as it is, so a stored word that some view would rewrite matches only in the views that leave
    it as it is. A spelling that no view matches to another word is entered even when it means the same (`씨발` beside
    `시발`).
  - An existing word is not entered again, and a word fits its column (`varchar(10)` for `prohibited_word`,
    `varchar(20)` for `allowed_word`).
  - An allowed word contains a match of a prohibited word, because one without a match suppresses nothing.

### 2.2 Masking (`maskProhibitedWord`)

- Null or blank text is returned unchanged.
- An obfuscated spelling of a prohibited word is masked as the word it spells, and each run of overlapping or adjacent
  matches, whitespace inside it included, becomes one `***` (`시.발` → `***`, `시 발` → `***`).
- A match formed by the first characters of lines, and an allowed word, are not masked.
- Masking never rejects the request.

### 2.3 Rejection (Nickname Write Paths)

Checks run in this order, each throwing on failure:
1. Null or blank, in `Nickname.create` → `EmptyValueException` (`EMPTY_NICKNAME`).
2. Mismatch with `PATTERN_NICKNAME`, in `Nickname.create` → `InvalidValueException` (`INVALID_NICKNAME_FORMAT`).
3. Any engine match through `ProhibitedWordInspector.containsProhibitedWord`, regardless of the prohibited word's
   severity, in `NicknameValidator.validate`, which the write path's controller injects and calls right after
   `Nickname.create` → `InvalidValueException` (`PROHIBITED_WORD_CONTAINED_NICKNAME`); an allowed word is accepted.

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
1. `Nickname.create` and `NicknameValidator.validate` run on the submitted nickname, so a nickname containing a
   prohibited word is rejected before any other check.
2. The pre-override validation (`validateBeforeOverrideProfile`) runs.
3. The stored profile is loaded and its current image is deleted.
4. The introduction is masked.
5. The profile is rebuilt with the masked introduction and saved.

**Nickname check**: `Nickname.create` and `NicknameValidator.validate` run before the existence lookup, so a nickname
containing a prohibited word is rejected instead of receiving an existence result.

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
`Nickname.create`, which runs no prohibited word check, so a stored nickname never fails to load because of the
dictionary.

---

## 4. Glossary

- **Prohibited word**: A word that is masked and rejected in a nickname, stored in `prohibited_word`: a profanity, a
  sexual term, or a non-verbal string such as an emoji
- **Allowed word**: A normal word that contains a prohibited word, stored in `allowed_word` (`시발점`)
- **False-positive likelihood**: How likely a prohibited word is to match normal text, rated 0 to 5:
  - 0: It matches no normal text
  - 1: It matches only normal expressions that plant posts rarely use
  - 2: It matches a few normal words that plant posts sometimes use
  - 3: It matches normal words that plant posts commonly use
  - 4: It matches many normal words or phrases
  - 5: It matches almost any normal text
- **Lexical variant**: A word-specific spelling of a prohibited word that no view derives (`쓰벌` for `씨벌`)
