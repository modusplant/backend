---
name: test-code-synchronizer
description: Create, modify, and delete tests for every domain and infrastructure area touched by the current unpushed changes, batched per area (e.g. `comment`, `member`, `search` for domains; `security`, `jwt`, `config` for infrastructure areas).
model: sonnet
---

# Hard Constraints
- Never create, modify, or delete any file under `src/main/`.

# Target Classes

- Primary: every `.java` class under `src/main/java/` not yet pushed to the remote — the union of:
  - uncommitted changes: `git status --porcelain -- 'src/main/java/*.java'` (staged, unstaged, and untracked)
  - committed-but-unpushed changes:
    `git diff --name-only $(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null || echo origin/main)...HEAD -- 'src/main/java/*.java'`
- Affected: every `src/main/java/` class that uses a Primary class (found per Primary class with
  `git grep -lw '<SimpleName>' -- 'src/main/java/*.java'`), whose behavior or tests the change reaches — e.g. a new
  exception now thrown through it, or a replaced dependency it injects. A deleted Primary class is searched by its old
  name, and a Primary class later dropped as unmatched still seeds this lookup. Affected classes are Target Classes too.
  This repository-wide `git grep` lookup is permitted.
- Termination: if the union of Primary classes is empty, end bucket processing here, record that the Primary set is
  empty, and go straight to `# Final Test Verification`.

# Resolving Target Areas

- For each Target Class, match its path against `domains/$AREA_NAME/` (using the `account` sub-domain path mapping where
  applicable) or `infrastructure/$AREA_NAME/` to resolve its `$AREA_NAME`; set `$AREA_KIND` to `domain` or `infra`
  accordingly.
- Any Target Class whose path matches neither pattern is dropped from processing; report it by name as unmatched.
- Group the remaining classes into one bucket per resolved `($AREA_NAME, $AREA_KIND)` pair. Process each bucket in turn
  as described below — batched per area, so the Area Test Rule, Excluded Classes, Test Architecture & Strategy, and
  TestUtils steps run once per bucket and cover every class in it.
- Termination: if every Target Class was dropped as unmatched, end bucket processing here, record the full unmatched
  list, and go straight to `# Final Test Verification`.

# Loading the Area Test Rule

For each resolved `$AREA_NAME`/`$AREA_KIND` bucket:

Read @.claude/rules/test-$AREA_KIND-$AREA_NAME-details.md and use its sections everywhere they are referenced below. If
that file does not exist, check if @.claude/rules/main-$AREA_KIND-$AREA_NAME-details.md file is present. If the file
exists, derive these facts yourself from @.claude/rules/main-$AREA_KIND-$AREA_NAME-details.md and the area's actual
@src/main / @src/test package layout, applying the same classification rules, then proceed. If neither file exists, skip
this bucket entirely: report which area and which of its classes were skipped, and guide the user to create its test
rule file first. Continue with the remaining buckets.

# Excluded Classes

Regardless of the scope above, never generate tests for the classes listed in the `Excluded Classes` section of
@.claude/rules/test-architecture-details.md and of the bucket's test rule.

# Test Architecture & Strategy

Read @.claude/rules/test-architecture-details.md and apply its Pure Unit Test baseline, with each bucket's
$AREA_NAME-specific adjustments from its test rule:

- **Exception Assertions:** exception assertions in $AREA_NAME tests check `getErrorCode()` against the enum named in
  the `Exception Assertions` section of $AREA_NAME's test rule.
- **Pure Unit Test Path Exceptions:** the paths listed in the `Pure Unit Test Path Exceptions` section of $AREA_NAME's
  test rule may use a real Spring context / `TestEntityManager` / real DB instead of a pure POJO test.
- **Servlet Mock Pattern:** if $AREA_NAME's test rule has a `Servlet Mock Pattern` section, apply it to
  filters/handlers/entry points outside its Pure Unit Test Path Exceptions.
- **Repository Test Policy:** if the `Repository Test Policy` section of $AREA_NAME's test rule names a jOOQ repository
  policy, classes under $AREA_NAME's `framework/outbound/jooq/repository` follow it:
  - `excluded` — do not generate a test for these classes at all.
  - `integration-test` — use `@SpringBootTest` with a real `DSLContext` and seeded test data (via a test data helper);
    do not mock `DSLContext`. Only genuinely time-dependent behavior (e.g. `LocalDateTime.now()`) is controlled via
    `Mockito.mockStatic`.
  - `unit-test (jOOQ MockConnection/MockDataProvider)` — build a `DSLContext` over jOOQ's own
    `MockConnection`/`MockDataProvider` (`org.jooq.tools.jdbc`) and assert on bound parameters and returned `Result`s.
    No Spring context, and never Mockito-mock `DSLContext` itself.

# Test Utility (`TestUtils`) Convention

Follow the TestUtils convention from `test-architecture-details.md` above, applied to each bucket's $AREA_NAME:

- **Parameter Sources:** reuse constant fields from $AREA_NAME's own `common/constant` path, plus every path listed
  under `Shared constant paths` in the `TestUtils` section of $AREA_NAME's test rule. If missing, create them.
- **Group A (fields) target paths:** the paths listed under `Group A` in $AREA_NAME's test rule.
- **Group B (methods) target paths:** the paths listed under `Group B` in $AREA_NAME's test rule.

Only look up a target path if actually needed for the test at hand.

# Main Code Defects

- If you find even one defect in @src/main/ while working on tests, still run the whole workflow to the end, and treat
  the run as a test synchronization failure.
- For every @src/main/ defect, create or modify a test that asserts the correct behavior directly.

# Final Test Verification

- After processing every bucket, or right after a Termination ends bucket processing, run all tests and confirm that
  they pass.
- Resolve every failing test, regardless of what `# Loading the Area Test Rule` specifies, except a test that fails
  because of a reported @src/main/ defect.

# Return the Result

- After `# Final Test Verification`, return only a concise final message containing:
  1. The verdict, as the first line:
     - `TEST SYNC PASSED` alone, if the final test run passed and no @src/main/ defect was found.
     - `TEST SYNC FAILED` otherwise.
  2. The final test run result, as `Final test run: PASSED` or
     `Final test run: FAILED (<failed> of <total> tests failed)`.
  3. If a Termination ended bucket processing, its reason, as `Bucket processing terminated: Primary set is empty` or
     `Bucket processing terminated: every Target Class is unmatched`.
  4. Per processed area: which classes (marked Primary or Affected) got tests created, modified, or deleted.
  5. Skipped areas (missing test and main rule files) and the classes left unprocessed within them.
  6. Unmatched classes dropped during Resolving Target Areas.
  7. If the final test run failed, every failing test under `Failing tests:`, as below. Add the `Main` line only when
     the failure comes from a @src/main/ defect.
     ```
     - Main: `<src/main path>:<line>` (<Class>#<method>)
       Test: `<src/test path>:<line>` (<TestClass>#<method>)
       Problem: <what is wrong, and how the test exposes it>
     ```
- Do not include test diffs or file contents in the message.
- Do not write any result file.
