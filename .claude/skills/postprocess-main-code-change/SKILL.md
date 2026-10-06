---
name: postprocess-main-code-change
description: Runs the required post-processing workflow after code changes under src/main/ — per-area batched tests, then doc reflection, then Notion API-spec reflection, then Confluence table-definition reflection — strictly in this order.
disable-model-invocation: true
---

# Preconditions

- Run this skill only after code changes under @src/main/ are complete and ready for verification and documentation.
- This skill performs no work itself: it invokes the four skills below via the Skill tool, strictly in the order given, inside the current conversation — never as a forked or backgrounded subagent — so each step's findings (changed classes, test results, detected changes) carry forward into the next.

# Workflow

1. Invoke `reflect-code-change-into-test`.
2. Invoke `reflect-code-change-into-document`.
3. Invoke `reflect-api-change-into-notion`.
4. Invoke `reflect-flyway-change-into-external-source`.

Wait for each invoked skill to finish before starting the next one.

# Termination

- Each invoked skill enforces its own termination rules (see its own `SKILL.md`).
- Skip: if a step terminates because its target set is empty — including a set left empty by
  that skill's own scope filter — treat the step as skipped and continue with the next step.
- Stop: if a step terminates early for any other reason, stop the workflow there — do not force
  the remaining steps to run.
- Report every skipped or stopped step and why, so the user can decide whether to re-run it
  manually once the underlying condition is fixed.

# Hard Constraints

- Always call invoked skills through the Skill tool, so this skill stays a thin, single source of truth for ordering only.