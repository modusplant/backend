---
name: reflect-code-change-into-document
description: This skill applies the follow-up actions required in response to code changes.
disallowed-tools: Edit(/src/**)
---

# Detailed Action Items

- Update the documentation corresponding to each Target Class.

# Target Classes

- Primary: every `.java` class not yet pushed to the remote, under both `src/main/` and `src/test/` — the union of:
  - uncommitted changes: `git status --porcelain -- '*.java'` (staged, unstaged, and untracked)
  - committed-but-unpushed changes: `git diff --name-only $(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null || echo origin/main)...HEAD -- '*.java'`
- Affected: every `src/main/java/` class that uses a Primary `src/main/` class (found per class with
  `git grep -lw '<SimpleName>' -- 'src/main/java/*.java'`), whose documented behavior the change
  reaches — e.g. a new exception now thrown through it, or a replaced dependency it injects. A
  deleted Primary class is searched by its old name. Affected classes are Target Classes too.
- Termination: if the union of Primary classes is empty, terminate the skill immediately.

# Document Mapping per Class

Classification follows `CLAUDE.md`; determine which section each class belongs to before checking anything.

- Domain submodules under `domains` (e.g. `member`, `search`): @.claude/CLAUDE.md, plus any file under @.claude/documents/, @.claude/rules/ or @.claude/skills/ relevant to that domain.
- Other top-level architecture (e.g. `infrastructure`, `shared`): @.claude/CLAUDE.md, plus any file under @.claude/documents/ or @.claude/rules/ relevant to that area.
- Not associated with any domain:
  - Docker-related (e.g. @docker-compose.yml): @.claude/skills/guide-docker-environment-configuration/SKILL.md only.
  - Grafana-related (e.g. @src/main/resources/logback-spring.xml): @.claude/documents/observability-tagging-architecture.md only.

# Update Strategy

- Necessary-only: update strictly what's needed; leave no bloat in the document (no excessive examples, no content duplicated within the same document or across documents).

# Hard Constraints

- Never invoke this skill on your own initiative; only run it when invoked by
  @.claude/skills/postprocess-main-code-change/SKILL.md's workflow.