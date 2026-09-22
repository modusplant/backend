---
name: reflect-code-change-into-test
description: This file provides strict guidance on creating, modifying, and deleting tests for every domain and infrastructure area touched by the current unpushed changes, batched per area (e.g. `comment`, `member`, `search` for domains; `security`, `jwt`, `config` for infrastructure areas).
disallowed-tools: Write(/src/main/**) Edit(/src/main/**)
---

# Target Classes

- Primary: every `.java` class not yet pushed to the remote — the union of:
  - uncommitted changes: `git status --porcelain -- '*.java'` (staged, unstaged, and untracked)
  - committed-but-unpushed changes: `git diff --name-only $(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null || echo origin/main)...HEAD -- '*.java'`
- Termination: if the union is empty, terminate the skill immediately.

# Resolving Target Areas

- For each Target Class, match its path against `domains/$AREA_NAME/` (using the `account`
  sub-domain path mapping where applicable) or `infrastructure/$AREA_NAME/` to resolve its
  `$AREA_NAME`; set `$AREA_KIND` to `domain` or `infra` accordingly.
- Any Target Class whose path matches neither pattern is dropped from processing; report it by
  name as unmatched.
- Group the remaining classes into one bucket per resolved `($AREA_NAME, $AREA_KIND)` pair.
  Process each bucket in turn as described below — batched per area, so the Area Feature,
  Excluded Classes, Test Architecture & Strategy, and TestUtils steps run once per bucket and
  cover every class in it.
- Termination: if every Target Class was dropped as unmatched, terminate the skill immediately
  and report the full unmatched list.

# Loading the Area Feature

For each resolved `$AREA_NAME`/`$AREA_KIND` bucket:

Read @.claude/documents/test-$AREA_KIND-$AREA_NAME-feature.md and use its fields everywhere those terms are referenced below.
If that file does not exist, check if @.claude/rules/$AREA_KIND-$AREA_NAME-details.md file is present.
If the file exists, derive these facts yourself from @.claude/rules/$AREA_KIND-$AREA_NAME-details.md
and the area's actual @src/main / @src/test package layout, applying the same classification rules, then proceed.
If neither file exists, skip this bucket entirely: report which area and which of its classes were
skipped, and guide the user to create its Rule file first. Continue with the remaining buckets.

# Excluded Classes

Regardless of the scope above, never generate tests for:
  - Enum classes
  - Exception classes
  - Classes that contain only constructors
  - Any classes listed under the bucket's $AREA_NAME's `Excluded-classes additions` in its feature

# Test Architecture & Strategy

!`cat ${CLAUDE_PROJECT_DIR}/.claude/rules/test-architecture-details.md`

Apply the Pure Unit Test baseline above, with each bucket's $AREA_NAME-specific adjustments from its feature:

- **ErrorCode class:** exception assertions in $AREA_NAME tests check `getErrorCode()` against
  the enum named under $AREA_NAME's `ErrorCode class` in its feature.
- **Pure-Unit-Test path exceptions:** the paths listed under $AREA_NAME's `Pure-Unit-Test path
  exceptions` may use a real Spring context / `TestEntityManager` / real DB instead of a pure POJO test.
- **Servlet mock pattern:** if $AREA_NAME's feature defines a `Servlet mock pattern`, apply it to
  filters/handlers/entry points outside its Pure-Unit-Test path exceptions.
- **jOOQ repository test policy:** if $AREA_NAME's feature names a policy, classes under
  $AREA_NAME's `framework/outbound/jooq/repository` follow it:
    - `excluded` — do not generate a test for these classes at all.
    - `integration-test` — use `@SpringBootTest` with a real `DSLContext` and seeded test data (via
      a test data helper); do not mock `DSLContext`. Only genuinely time-dependent behavior (e.g.
      `LocalDateTime.now()`) is controlled via `Mockito.mockStatic`.
    - `unit-test (jOOQ MockConnection/MockDataProvider)` — build a `DSLContext` over jOOQ's own
      `MockConnection`/`MockDataProvider` (`org.jooq.tools.jdbc`) and assert on bound parameters
      and returned `Result`s. No Spring context, and never Mockito-mock `DSLContext` itself.

# Test Utility (`TestUtils`) Convention

Follow the TestUtils convention from `test-architecture-details.md` above, applied to each bucket's $AREA_NAME:

- **Parameter Sources:** reuse constant fields from $AREA_NAME's own `common/constant` path, plus
  every path listed under $AREA_NAME's `TestUtils shared constant paths` in its feature. If missing, create them.
- **Group A (fields) target paths:** the paths listed under $AREA_NAME's `Group A` in its feature.
- **Group B (methods) target paths:** the paths listed under $AREA_NAME's `Group B` in its feature.

Only look up a target path if actually needed for the test at hand.

# Summary

After processing every bucket, report:
  - Per processed area: which classes got tests created, modified, or deleted.
  - Skipped areas (missing feature/rule file) and the classes left unprocessed within them.
  - Unmatched classes dropped during Resolving Target Areas.

# Hard Constraints

- Never invoke this skill on your own initiative; only run it when invoked by
  @.claude/skills/postprocess-main-code-change/SKILL.md's workflow.
