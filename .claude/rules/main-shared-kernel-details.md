---
paths:
  - "src/main/java/kr/modusplant/shared/kernel/**"
---

# Shared Kernel Conventions

## Parent Document

- `.claude/documents/policy/prohibited-word-filtering-policy.md` — the nickname rejection that `NicknameValidator` runs
  (§2.3), and the stored nicknames it skips (§3.5)

---

## 1. Package Structure

- `Email`, `Nickname`, `Password` — kernel value objects shared across domains
- `ProhibitedWordInspector` (`inspector/`) — interface of the prohibited word check that `NicknameValidator` runs
- `NicknameValidator` (`validator/`) — Spring bean that rejects a nickname containing a prohibited word

---

## 2. Class Responsibilities

**`inspector/`** — interfaces that `shared` code calls and code outside `shared` implements (e.g.
`ProhibitedWordHolder`), so `shared` never depends on another top-level package.

**`validator/`** — Spring beans for the checks that a value object's `create` cannot run, because `create` also runs in
the JPA mappers and has no Spring bean; a write path calls them right after `create`.
