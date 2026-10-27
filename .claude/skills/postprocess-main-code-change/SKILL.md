---
name: postprocess-main-code-change
description: Runs the required post-processing workflow after code changes under src/main/ — a code quality gate, then per-area batched tests, then doc reflection, then Notion API-spec reflection, then Confluence table-definition reflection — strictly in this order.
disable-model-invocation: true
---

# Preconditions

- Run this skill only after code changes under @src/main/ are complete and ready for verification and documentation.
- This skill performs no work itself: it invokes the five skills below via the Skill tool, strictly in the order given,
  inside the current conversation — never as a forked or backgrounded subagent.

# Workflow

1. Invoke `check-code-detail-quality`.
2. Invoke `reflect-code-change-into-test`.
3. Invoke `reflect-code-change-into-document`.
4. Invoke `reflect-api-change-into-notion`.
5. Invoke `reflect-flyway-change-into-external-source`.

Wait for each invoked skill to finish before starting the next one.

# Termination

- Each invoked skill enforces its own termination rules (see its own `SKILL.md`).
- Quality gate: if `check-code-detail-quality` returns `QUALITY CHECK PASSED`, continue with the next step. If it
  returns `QUALITY CHECK REJECTED`, report that response as-is and stop the workflow there.
- Test gate: if `reflect-code-change-into-test` returns `TEST SYNC PASSED`, continue with the next step. If it returns
  `TEST SYNC FAILED`, report that response as-is and stop the workflow there.
- Precedence: when a step returns a gate verdict, that verdict alone decides whether the workflow continues. Skip and
  Stop below apply only to a step that ends without a gate verdict.
- Skip: if a step terminates because its target set is empty — including a set left empty by that skill's own scope
  filter — treat the step as skipped and continue with the next step.
- Stop: if a step terminates early for any other reason, stop the workflow there — do not force the remaining steps to
  run.
- Report every skipped or stopped step and why, so the user can decide whether to re-run it manually once the underlying
  condition is fixed.