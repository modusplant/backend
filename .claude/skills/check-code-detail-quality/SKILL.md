---
name: check-code-detail-quality
description: Checks the unpushed changes under src/main/ against .claude/rules/main-architecture-details.md and each document under .claude/documents/policy/, with one Haiku subagent per document, then returns either a pass or a rejection with the aggregated reasons.
---

# Preconditions

- This skill is read-only: it reads source under `src/main/java/`, the Standard Documents, and git metadata, and writes
  nothing.

# Target Classes

- Every `.java` class under `src/main/java/` not yet pushed to the remote — the union of:
  - uncommitted changes: `git status --porcelain -- 'src/main/java/*.java'` (staged, unstaged, and untracked)
  - committed-but-unpushed changes:
    `git diff --name-only $(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null || echo origin/main)...HEAD -- 'src/main/java/*.java'`
- Termination: if the union is empty, terminate the skill immediately with an empty target set.

# Standard Documents

- `.claude/rules/main-architecture-details.md`
- Every `.md` file directly under `.claude/documents/policy/`

# Workflow

1. Resolve the Target Classes.
2. Resolve the Standard Documents.
3. Launch one subagent per Standard Document with the Agent tool, all in a single message so they run in parallel
   (`subagent_type: Explore`, `model: haiku`; no `fork`, no `isolation`). Each prompt carries:
   - the Target Class list and the two git commands above, so the subagent reads the actual diff
   - the instruction to read its one Standard Document in full, then judge the changes against each rule in it that the
     changes fall under, treating a rule no change falls under as satisfied
   - the reply format of step 4
4. Each subagent replies with exactly one of:
   - `PASS` — the changes satisfy every rule. Nothing else is sent.
   - `REJECT` — followed by every violation grouped under the name of the section it fails, each with its
     `file_path:line`, the requirement it fails, and what is missing or wrong, as specifically as possible.
5. Return:
   - `QUALITY CHECK PASSED` alone, if every reply is `PASS`.
   - `QUALITY CHECK REJECTED`, followed by the violations of every `REJECT` reply grouped by Standard Document, then by
     section, otherwise.
