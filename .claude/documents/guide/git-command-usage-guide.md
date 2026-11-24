# Git Command Usage Guide

The git commands that skills and agents run to resolve their target files.

---

## 1. Unpushed Changes

The caller supplies `<pathspecs>` and `<output-form>`; the commands are otherwise fixed. The target files are the files
not yet pushed to the remote, the union of:

- uncommitted changes: `git status --porcelain -- <pathspecs>` (staged, unstaged, and untracked)
- committed-but-unpushed changes:
  `git diff <output-form> $(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null || echo origin/main)...HEAD -- <pathspecs>`

`<output-form>` is `--name-only`, or `--name-status` when the caller needs each file's git status (`A`, `M`, `D`, or
`R` with its old path).

---

## 2. Usage Lookup

- A file that uses an identifier is found with `git grep -lw '<identifier>' -- <pathspecs>`.
- An Affected class lookup runs this once per Primary class, with the class's simple name as `<identifier>`; a deleted
  Primary class is searched by its old name. The caller supplies `<pathspecs>` and the kind of behavior that counts
  (e.g. documented behavior).
- These lookups may run over the whole repository.

---

## 3. Glossary

- **Primary class**: a class found by section 1.
- **Affected class**: a class found by section 2 that uses a Primary class and whose behavior, of the kind the caller
  counts, the change reaches (e.g. a new exception now thrown through it, or a replaced dependency it injects).
