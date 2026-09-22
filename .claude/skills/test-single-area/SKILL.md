---
name: test-single-area
description: This file provides strict guidance on creating, modifying, and deleting tests for a single domain or infrastructure area, given as an argument (e.g. `comment`, `member`, `search` for domains; `security`, `jwt`, `config` for infrastructure areas).
arguments: [AREA_NAME]
argument-hint: [area-name]
disable-model-invocation: true
disallowed-tools: Write(/src/main/**) Edit(/src/main/**)
---

# Resolving the Target Area

- Predefined domain names: `account` (including its sub-domains `email`, `identity`, `normal`, `social`), `comment`, `member`, `notification`, `post`, `search`, `term`.
- Predefined infrastructure area names: `advice`, `aop`, `config`, `file`, `jwt`, `monitor`, `security`, `swear`.
- If $AREA_NAME matches a domain name, set $AREA_KIND to `domain`. If it matches an infrastructure area name, set $AREA_KIND to `infra`.
- If $AREA_NAME isn't one predefined domain or infrastructure area name, instantly terminate the skill and give the user what happened.

# Loading the Area Feature

Read @.claude/documents/test-$AREA_KIND-$AREA_NAME-feature.md and use its fields everywhere those terms are referenced below.
If that file does not exist, check if @.claude/rules/$AREA_KIND-$AREA_NAME-details.md file is present.
If the file exists, derive these facts yourself from @.claude/rules/$AREA_KIND-$AREA_NAME-details.md
and the area's actual @src/main / @src/test package layout, applying the same classification rules, then proceed.
If the file doesn't exist, instantly terminate the skill and guide the user to create Rule file first.

# Target Classes

- Primary: classes created, modified, or deleted since the previous session, if it belongs to the $AREA_NAME area (under `domains/$AREA_NAME/` for $AREA_KIND `domain`, or `infrastructure/$AREA_NAME/` for $AREA_KIND `infra`).
- Fallback - 1: If no $AREA_NAME classes were modified in the previous session, run `git status --porcelain | awk '{print $NF}' | grep '\.java$'` and find the $AREA_NAME classes within its output.
- Fallback - 2: if that also yields nothing, run `git diff --name-only HEAD~1 HEAD | grep '\.java$' | awk -F/ '{print $NF}' | sed 's/\.java$//'` and find the $AREA_NAME classes within its output.
- Fallback - 3: if that also yields nothing, run `git diff --name-only HEAD~2 HEAD | grep '\.java$' | awk -F/ '{print $NF}' | sed 's/\.java$//'` and find the $AREA_NAME classes within its output.
- Termination: if no result was found, terminate the skill immediately.

# Excluded Classes

Regardless of the scope above, never generate tests for:
  - Enum classes
  - Exception classes
  - Classes that contain only constructors
  - Any classes listed under $AREA_NAME's `Excluded-classes additions` in its feature

# Test Architecture & Strategy

!`cat ${CLAUDE_PROJECT_DIR}/.claude/rules/test-architecture-details.md`

Apply the Pure Unit Test baseline above, with these $AREA_NAME-specific adjustments from its feature:

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

Follow the TestUtils convention from `test-architecture-details.md` above, applied to $AREA_NAME:

- **Parameter Sources:** reuse constant fields from $AREA_NAME's own `common/constant` path, plus
  every path listed under $AREA_NAME's `TestUtils shared constant paths` in its feature. If missing, create them.
- **Group A (fields) target paths:** the paths listed under $AREA_NAME's `Group A` in its feature.
- **Group B (methods) target paths:** the paths listed under $AREA_NAME's `Group B` in its feature.

Only look up a target path if actually needed for the test at hand.
