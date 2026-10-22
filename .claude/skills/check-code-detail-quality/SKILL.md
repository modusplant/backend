---
name: check-code-detail-quality
description: Checks the unpushed changes under src/main/ against each code quality check item with one Haiku subagent per item, then returns either a pass or a rejection with the aggregated reasons.
---

# Preconditions

- This skill is read-only: it reads source under `src/main/java/` and git metadata, and writes nothing.

# Target Classes

- Every `.java` class under `src/main/java/` not yet pushed to the remote — the union of:
  - uncommitted changes: `git status --porcelain -- 'src/main/java/*.java'` (staged, unstaged, and untracked)
  - committed-but-unpushed changes: `git diff --name-only $(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null || echo origin/main)...HEAD -- 'src/main/java/*.java'`
- Termination: if the union is empty, terminate the skill immediately with an empty target set.

# Code Quality Check Items

Each item names what it applies to and the conditions the changes must meet to pass.

## Sensitive-data masking

- **Applies to:** a changed class holding a sensitive value (email, password, token, auth code), and a changed log statement printing such a value.
- **Passes when:**
  - the class overrides `toString()` and masks every sensitive value in it via `shared/util/MaskUtils`.
  - the log statement masks the value via the same `MaskUtils` helpers.

## Endpoint–`SecurityConfig` sync

- **Applies to:** a changed endpoint, with `infrastructure/security/config/SecurityConfig`, which matches endpoints by hard-coded URL patterns.
- **Passes when** `SecurityConfig` is updated in the same change as the endpoint:
  - **New endpoint:** a permit-all endpoint is registered under its `HttpMethod` in `SecurityConfig.PUBLIC_ENDPOINTS` (`LOCAL_PUBLIC_ENDPOINTS` for a `local`-only one); every other endpoint carries a method-security annotation (`@PreAuthorize`/`@PostAuthorize`/`@Secured`).
  - **Changed URL or HTTP method:** every pattern that matches the endpoint is updated.
  - **Removed endpoint:** its public-endpoint entry is removed, unless another endpoint still matches the pattern.
- **Scope:** `EndpointAuthorizationCoverageTest` fails only for an endpoint with neither an annotation nor a matching public-endpoint entry; every other condition above is checked only here.

# Workflow

1. Resolve the Target Classes.
2. In a single message, launch one subagent per Code Quality Check Item with the Agent tool (`subagent_type: Explore`, `model: haiku`; no `fork`, no `isolation`). Each prompt carries:
   - the item's full text, verbatim
   - the Target Class list and the two git commands above, so the subagent reads the actual diff
   - the instruction to judge only the changes the item applies to, treating a change set the item does not apply to as satisfying it
   - the reply format of step 3
3. Each subagent replies with exactly one of:
   - `PASS` — the changes satisfy the item. Nothing else is sent.
   - `REJECT` — followed by every violation, each with its `file_path:line`, the requirement it fails, and what is missing or wrong, as specifically as possible.
4. Wait until every subagent has replied, then return:
   - `QUALITY CHECK PASSED` alone, if no reply is `REJECT`.
   - `QUALITY CHECK REJECTED`, followed by the violations grouped under each rejecting item's name, if any reply is `REJECT`.
