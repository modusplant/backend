---
name: check-code-detail-quality
description: Checks the unpushed changes under src/main/ against .claude/rules/main-architecture-details.md with one Haiku subagent, then returns either a pass or a rejection with the aggregated reasons.
---

# Preconditions

- This skill is read-only: it reads source under `src/main/java/`, `.claude/rules/main-architecture-details.md`, and git metadata, and writes nothing.

# Target Classes

- Every `.java` class under `src/main/java/` not yet pushed to the remote — the union of:
  - uncommitted changes: `git status --porcelain -- 'src/main/java/*.java'` (staged, unstaged, and untracked)
  - committed-but-unpushed changes: `git diff --name-only $(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null || echo origin/main)...HEAD -- 'src/main/java/*.java'`
- Termination: if the union is empty, terminate the skill immediately with an empty target set.

# Workflow

1. Resolve the Target Classes.
2. Launch one subagent with the Agent tool (`subagent_type: Explore`, `model: haiku`; no `fork`, no `isolation`). Its prompt carries:
   - the Target Class list and the two git commands above, so the subagent reads the actual diff
   - the instruction to read `.claude/rules/main-architecture-details.md` in full, and every document it requires, then judge the changes against each rule the changes fall under, treating a rule no change falls under as satisfied
   - the reply format of step 3
3. The subagent replies with exactly one of:
   - `PASS` — the changes satisfy every rule. Nothing else is sent.
   - `REJECT` — followed by every violation grouped under the name of the rule section it fails, each with its `file_path:line`, the requirement it fails, and what is missing or wrong, as specifically as possible.
4. Return:
   - `QUALITY CHECK PASSED` alone, if the reply is `PASS`.
   - `QUALITY CHECK REJECTED`, followed by the grouped violations, if the reply is `REJECT`.
