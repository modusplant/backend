---
name: postprocess-main-code-change
description: Runs the required post-processing workflow after code changes under src/main/ — single-domain tests, then doc reflection, then Notion API-spec reflection — strictly in this order.
arguments: [AREA_NAME]
argument-hint: [area-name]
disable-model-invocation: true
disallowed-tools: Write(/src/**) Edit(/src/**)
---

# Preconditions

- Run this skill only after code changes under @src/main/ are complete and ready for verification and documentation.
- This skill performs no work itself: it invokes the three skills below via the Skill tool, strictly in the order given, inside the current conversation — never as a forked or backgrounded subagent — so each step's findings (changed classes, test results, detected changes) carry forward into the next.

# Workflow

1. Invoke `test-single-area` with `$AREA_NAME`.
2. Invoke `reflect-code-change-into-document`.
3. Invoke `reflect-api-change-into-notion`.

Wait for each invoked skill to finish before starting the next one.

# Termination

- Each invoked skill enforces its own termination rules (see its own `SKILL.md`). If any step terminates early, stop the workflow there — do not force the remaining steps to run. Report which step stopped and why, so the user can decide whether to re-run it manually once the underlying condition is fixed.

# Hard Constraints

- Always call invoked skills through the Skill tool, so this skill stays a thin, single source of truth for ordering only.