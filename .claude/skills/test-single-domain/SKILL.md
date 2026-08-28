---
name: test-single-domain
description: This file provides strict guidance on creating, modifying, and deleting tests for a single domain, given as an argument (e.g. `comment`, `member`, `search`).
arguments: [DOMAIN_NAME]
argument-hint: [domain-name]
disable-model-invocation: true
disallowed-tools: Write(/src/main/**) Edit(/src/main/**)
---

# Resolving the Target Domain

- If the $DOMAIN_NAME isn't one predefined domain name, instantly terminate the skill and give the user what happened.

# Loading the Domain Profile

Read @.claude/documents/test-domain-profiles-$DOMAIN_NAME.md and use its fields everywhere those terms are referenced below.
If that file does not exist, check if @.claude/rules/domain-$DOMAIN_NAME-details.md file is present.
If the file exists, derive these facts yourself from @.claude/rules/domain-$DOMAIN_NAME-details.md 
and the domain's actual @src/main / @src/test package layout, applying the same classification rules, then proceed.
If the file doesn't exist, instantly terminate the skill and guide the user to create Rule file first.

# Target Classes

- Primary: classes created, modified, or deleted since the previous session, if it belongs to the $DOMAIN_NAME domain.
- Fallback - 1: If no $DOMAIN_NAME-domain classes were modified in the previous session, run `git status --porcelain | awk '{print $NF}' | grep '\.java$'` and find the $DOMAIN_NAME-domain classes within its output.
- Fallback - 2: if that also yields nothing, run `git diff --name-only HEAD~1 HEAD | grep '\.java$' | awk -F/ '{print $NF}' | sed 's/\.java$//'` and find the $DOMAIN_NAME-domain classes within its output.
- Fallback - 3: if that also yields nothing, run `git diff --name-only HEAD~2 HEAD | grep '\.java$' | awk -F/ '{print $NF}' | sed 's/\.java$//'` and find the $DOMAIN_NAME-domain classes within its output.
- Termination: if no result was found, terminate the skill immediately.

# Excluded Classes

Regardless of the scope above, never generate tests for:
  - Enum classes
  - Exception classes
  - Classes that contain only constructors
  - Any classes listed under $DOMAIN_NAME's `Excluded-classes additions` in its profile

# Test Architecture & Strategy

!`cat ${CLAUDE_PROJECT_DIR}/.claude/documents/test-architecture-convention.md`

Apply the Pure Unit Test baseline above, with these $DOMAIN_NAME-specific adjustments from its profile:

- **Pure-Unit-Test path exceptions:** the paths listed under $DOMAIN_NAME's `Pure-Unit-Test path
  exceptions` may use a real Spring context / `TestEntityManager` / real DB instead of a pure POJO test.
- **jOOQ repository test policy:** classes under $DOMAIN_NAME's `framework/outbound/jooq/repository`
  follow whichever policy $DOMAIN_NAME's profile names:
    - `excluded` — do not generate a test for these classes at all.
    - `integration-test` — use `@SpringBootTest` with a real `DSLContext` and seeded test data (via
      a test data helper); do not mock `DSLContext`. Only genuinely time-dependent behavior (e.g.
      `LocalDateTime.now()`) is controlled via `Mockito.mockStatic`.
    - `unit-test (jOOQ MockConnection/MockDataProvider)` — build a `DSLContext` over jOOQ's own
      `MockConnection`/`MockDataProvider` (`org.jooq.tools.jdbc`) and assert on bound parameters
      and returned `Result`s. No Spring context, and never Mockito-mock `DSLContext` itself.

# Test Utility (`TestUtils`) Convention

Follow the TestUtils convention from `test-architecture-convention.md` above, applied to $DOMAIN_NAME:

- **Parameter Sources:** reuse constant fields from $DOMAIN_NAME's own `common/constant` path, plus
  every path listed under $DOMAIN_NAME's `TestUtils shared constant paths` in its profile. If missing, create them.
- **Group A (fields) target paths:** the paths listed under $DOMAIN_NAME's `Group A` in its profile.
- **Group B (methods) target paths:** the paths listed under $DOMAIN_NAME's `Group B` in its profile.

Only look up a target path if actually needed for the test at hand.