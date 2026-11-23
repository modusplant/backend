---
paths:
  - "src/main/java/kr/modusplant/shared/kernel/**"
---

# Shared Kernel Conventions

## 1. Package Structure

- `Email`, `Nickname`, `Password` — kernel value objects shared across domains, following the value object pattern of
  `main-architecture-details.md` §1.1
- `SwearInspector` (`inspector/`) — interface of the swear check that `NicknameValidator` runs
- `NicknameValidator` (`validator/`) — Spring bean that rejects a swear nickname

---

## 2. Class Responsibilities

**`inspector/`** — interfaces that `shared` code calls and code outside `shared` implements (e.g. `SwearHolder`), so
`shared` never depends on another top-level package.

**`validator/`** — Spring beans for the checks that a value object's `create` cannot run, because `create` also runs in
the JPA mappers and has no Spring bean; a write path calls them right after `create`.
`NicknameValidator.validate(Nickname)` runs only the swear check through `SwearInspector` and throws
`InvalidValueException(SWEAR_CONTAINED_NICKNAME)` on any match.
