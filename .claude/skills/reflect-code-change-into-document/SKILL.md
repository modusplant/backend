---
name: reflect-code-change-into-document
description: Updates the Claude Code documentation to match unpushed `.java` changes under `src/main/` and `src/test/`, plus the Docker, CI/CD, and observability docs for their matching files.
disallowed-tools: Edit(/src/**)
---

# Detailed Action Items

- Update the documentation corresponding to each Target Class.

# Target Classes

Resolved per @.claude/documents/guide/git-command-usage-guide.md:

- Primary: section 1, with pathspec `'*.java'` and `--name-only`, covering both `src/main/` and `src/test/`.
- Affected: section 2, seeded by the Primary `src/main/` classes only, with pathspec `'src/main/java/*.java'`, counting
  documented behavior. Affected classes are Target Classes too.
- Termination: if the union of Primary classes is empty, terminate the skill immediately.

# Document Mapping per Class

Determine which section each class belongs to before checking anything.

- Domain submodules under `domains` (e.g. `member`, `search`): @.claude/CLAUDE.md, plus any file under
  @.claude/documents/, @.claude/rules/ or @.claude/skills/ relevant to that domain.
- Other top-level architecture (e.g. `infrastructure`, `shared`): @.claude/CLAUDE.md, plus any file under
  @.claude/documents/, @.claude/rules/ or @.claude/skills/ relevant to that area.
- Not associated with any domain:
  - Local Docker-related (e.g. @docker-compose.yml): @.claude/rules/local-docker-environment-details.md only.
  - CI/CD-related (e.g. @.github/workflows/modusplant-backend-ci-cd.yml):
    @.claude/rules/prod-dev-ci-cd-pipeline-details.md only.
  - Grafana-related (e.g. @src/main/resources/logback-spring.xml):
    @.claude/documents/architecture/observability-tagging-architecture.md only.

# Update Strategy

- Necessary-only: update strictly what's needed; leave no bloat in the document (no excessive examples).