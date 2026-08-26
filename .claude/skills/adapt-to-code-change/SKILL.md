---
name: adapt-to-code-change
description: This skill applies the follow-up actions required in response to code changes.
disable-model-invocation: true
disallowed-tools: Write(/src/**) Edit(/src/**)
---

# Detailed Action Items

- Update the documentation corresponding to each changed class.

# Target Classes

- Primary: classes created, modified, or deleted since the previous session.
- Fallback - 1: if no such classes exist, run `git status --porcelain | awk '{print $NF}' | grep '\.java$'` and use its output instead.
- Fallback - 2: if that also yields nothing, run `git diff --name-only HEAD~1 HEAD | grep '\.java$' | awk -F/ '{print $NF}' | sed 's/\.java$//'` and use its output instead.
- Fallback - 3: if that also yields nothing, run `git diff --name-only HEAD~2 HEAD | grep '\.java$' | awk -F/ '{print $NF}' | sed 's/\.java$//'` and use its output instead.
- Termination: if no result was found, terminate the skill immediately.

# Document Mapping per Class

Classification follows `CLAUDE.md`; determine which section each class belongs to before checking anything.

- Domain submodules under `domains` (e.g. `member`, `search`): @.claude/CLAUDE.md, plus any file under @.claude/documents/, @.claude/rules/ or @.claude/skills/ relevant to that domain.
- Other top-level architecture (e.g. `infrastructure`, `shared`): @.claude/CLAUDE.md, plus any file under @.claude/rules/.
- Not associated with any domain:
  - Docker-related (e.g. @docker-compose.yml): @.claude/skills/configure-docker-environment/SKILL.md only.

# Update Strategy

- Necessary-only: update strictly what's needed; leave no bloat in the document (no excessive examples, no content duplicated within the same document or across documents).