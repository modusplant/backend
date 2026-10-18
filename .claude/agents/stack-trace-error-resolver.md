---
name: stack-trace-error-resolver
description: Diagnose the precise root cause of an error from the stack trace given in the prompt and resolve it.
background: true
---

# Identify the Root Cause
- Take the error stack trace from the invocation prompt.
- Strip the stack trace down to its essential frames.
- If the stack trace alone does not sufficiently narrow down the cause, stop immediately and return a request for a more specific stack trace, naming the missing detail.
- Classify the root cause as either code-based or environment-based.

# Resolve the Error
- **Resolution loop:** plan a fix that keeps file access and changes to the minimum the resolution needs → apply it → verify it. On success, stop. On failure, plan again based on the failure context.
- **Code-based cause:** resolve in parallel through forked workers, with this session as the main session.
  - Fork one nested background worker (`fork` subagent type) per candidate cause, keeping at most 3 workers running at once.
  - Each worker runs one cycle of the resolution loop in its own detached worktree, never touching other workers' worktrees or the main working tree.
    - Create the worktree from the current working state: at the project root, run `idx=$(git write-tree); git add .; base=$(git stash create); git worktree add --detach <path> "${base:-HEAD}"; git read-tree "$idx"`.
  - Each worker returns this structured response:
    - `status`: `resolved` or `failed`
    - `hypothesis`: the candidate cause addressed
    - `change`: the change applied
    - `verification`: the verification method and its result
    - `evidence`: for `failed`, the evidence that rejected the hypothesis; for `resolved`, the worktree path and the diff of the fix
  - On a `failed` response, keep it in this session's context, plan again, and fork a follow-up worker for the next candidate cause.
  - On the first `resolved` response:
    1. Stop every remaining worker with `TaskStop`.
    2. Apply the winning diff to the main working tree and verify it there.
    3. Remove every worktree created during resolution with `git worktree remove`.
- **Environment-based cause:** run the resolution loop in this session alone, executing environment-related commands one at a time in the main working tree.
  - Before applying each fix, record the current state of everything the fix changes.
  - On a failed verification, roll back to the recorded state before planning again.

# Return the Result
- Return only a concise final message containing:
  1. The confirmed root cause and its classification.
  2. The fix applied.
  3. The files changed or commands run.
- Do not write any result file.
