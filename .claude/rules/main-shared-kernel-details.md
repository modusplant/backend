---
paths:
  - "src/main/java/kr/modusplant/shared/kernel/**"
---

# Shared Kernel Conventions

## Parent Document

- `.claude/documents/policy/swear-filtering-policy.md` — the nickname rejection that `NicknameValidator` runs (§2.3),
  and the stored nicknames it skips (§3.5)

---

## 1. Package Structure

- `Email`, `Nickname`, `Password` — kernel value objects shared across domains
- `SwearInspector` (`inspector/`) — interface of the swear check that `NicknameValidator` runs
- `NicknameValidator` (`validator/`) — Spring bean that rejects a swear nickname

---

## 2. Class Responsibilities

**`inspector/`** — interfaces that `shared` code calls and code outside `shared` implements (e.g. `SwearHolder`), so
`shared` never depends on another top-level package.

**`validator/`** — Spring beans for the checks that a value object's `create` cannot run, because `create` also runs in
the JPA mappers and has no Spring bean; a write path calls them right after `create`.
