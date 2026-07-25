---
name: resolve-error-with-stack-trace
description: Diagnose the precise root cause of an error from its stack trace and resolve it.
disable-model-invocation: true
arguments: [errorStackTrace]
---

# Hypothesize the Root Cause
- Strip the stack trace down to its essential frames to avoid unnecessary noise and complexity in later steps.
- If the stack trace alone does not sufficiently narrow down the cause, stop the skill immediately and ask the user for a more specific stack trace.
- Classify the root cause as either code-based or environment-based.

# Resolve the Error
- **Prerequisite:** Each attempt must be conducted independently and must not affect the environment or conditions of the others in any way.
- **Code-based hypothesis:**
  - Parallel: attempt resolution in parallel using `git worktree`.
  - Search scope: before attempting resolution, build an optimal set of starting paths, then search outward from them.
  - Access scope: access is allowed across most paths, except `@src/test/`, which is off-limits.
- **Environment-based hypothesis:**
  - Sequential: attempt environment-related commands sequentially within a single agent.
- Once resolution succeeds, stop all remaining resolution attempts immediately.
  - Remove any worktree used during resolution with `git worktree remove`.

# Report the Result
1. Summary of the stack trace.
2. Every hypothesis considered.
3. For the hypothesis that resolved the error, a detailed account of the full process: hypothesis formation → verification → resolution.