---
name: adapt-to-code-change
description: This skill applies the follow-up actions required in response to code changes.
disable-model-invocation: true
disallowed-tools: Write(./src/**) Edit(./src/**)
---

# Detailed Action Items

- Update the documentation corresponding to each changed class.

# Target Classes

- Primary: classes created, modified, or deleted since the previous session.
- Fallback: if no such classes exist, run `git status --porcelain | awk '{print $NF}' | grep '\.java$'` and use its output instead.
- If that also yields nothing, terminate the skill immediately.

# Document Mapping per Class

Classification follows `CLAUDE.md`; determine which section each class belongs to before checking anything.

- Domain submodules under `domains` (e.g. `member`, `search`): @.claude/CLAUDE.md, plus any file under @.claude/rules/ relevant to that domain.
- Other top-level architecture (e.g. `infrastructure`, `shared`): @.claude/CLAUDE.md only.

# Update Strategy

- Necessary-only: update strictly what's needed; leave no bloat in the document (no excessive examples, no content duplicated within the same document or across documents).
- Token optimization: do not look up any class other than the ones being updated.