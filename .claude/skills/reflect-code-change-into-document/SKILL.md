---
name: reflect-code-change-into-document
description: Updates the Claude Code documentation to match unpushed `.java` changes under `src/main/` and `src/test/`, plus the Docker, CI/CD, and observability docs for their matching files.
disallowed-tools: Edit(/src/**)
---

# Detailed Action Items

- Update the documentation corresponding to each Target Class.

# Target Classes

- Primary: every `.java` class not yet pushed to the remote, under both `src/main/` and `src/test/` — the union of:
  - uncommitted changes: `git status --porcelain -- '*.java'` (staged, unstaged, and untracked)
  - committed-but-unpushed changes:
    `git diff --name-only $(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null || echo origin/main)...HEAD -- '*.java'`
- Affected: every `src/main/java/` class that uses a Primary `src/main/` class (found per class with
  `git grep -lw '<SimpleName>' -- 'src/main/java/*.java'`), whose documented behavior the change reaches — e.g. a new
  exception now thrown through it, or a replaced dependency it injects. A deleted Primary class is searched by its old
  name. Affected classes are Target Classes too. This repository-wide `git grep` lookup is permitted.
- Termination: if the union of Primary classes is empty, terminate the skill immediately.

# Document Mapping per Class

Classification follows `CLAUDE.md`; determine which section each class belongs to before checking anything.

- Domain submodules under `domains` (e.g. `member`, `search`): @.claude/CLAUDE.md, plus any file under
  @.claude/documents/, @.claude/rules/ or @.claude/skills/ relevant to that domain.
- Other top-level architecture (e.g. `infrastructure`, `shared`): @.claude/CLAUDE.md, plus any file under
  @.claude/documents/ or @.claude/rules/ relevant to that area.
- Not associated with any domain:
  - Local Docker-related (e.g. @docker-compose.yml): @.claude/rules/local-docker-environment-details.md only.
  - CI/CD-related (e.g. @.github/workflows/modusplant-backend-ci-cd.yml):
    @.claude/rules/prod-dev-ci-cd-pipeline-details.md only.
  - Grafana-related (e.g. @src/main/resources/logback-spring.xml):
    @.claude/documents/architecture/observability-tagging-architecture.md only.

# Update Strategy

- Necessary-only: update strictly what's needed; leave no bloat in the document (no excessive examples, no content
  duplicated within the same document or across documents).