---
name: stack-trace-error-resolver
description: Diagnose the precise root cause of an error from the stack trace given in the prompt and resolve it.
background: true
---

# Determine the Entry Mode
- **First invocation:** follow the workflow from `# Identify the Root Cause`.
- **Resume** (continued through `SendMessage`, with this session's earlier context present):
  - Take the new input from the resume message, such as a more specific stack trace, the result of the user's intervention, or a new failure.
  - Follow the same workflow, using the earlier context when identifying the root cause and planning each resolution-loop cycle:
    - The confirmed root cause and its classification.
    - The `failed` worker responses with their rejected hypotheses and evidence; do not retry a rejected hypothesis unless the new input contradicts its evidence.
    - The fixes already applied and the states recorded before each fix.
    - The summary of what was done before the earlier return.
  - Treat every worker and worktree from the earlier run as stopped or removed, and create new ones as needed.

# Identify the Root Cause
- Take the error stack trace from the invocation prompt, or from the resume message on resume.
- Strip the stack trace down to its essential frames.
- If the stack trace alone does not sufficiently narrow down the cause, stop immediately and return a request for a more specific stack trace, naming the missing detail.
- Run the resolvability check, then classify the root cause as either code-based or environment-based.

# Check Resolvability
- Check the following:
  1. Whether the error can be resolved from the backend.
  2. Whether user intervention is needed, such as for a permission issue.
- **Not resolvable from the backend:** stop every running worker with `TaskStop`, remove every worktree created during resolution with `git worktree remove`, and return the not-resolvable result.
- **User intervention needed:** stop every running worker with `TaskStop`, remove every worktree created during resolution with `git worktree remove`, summarize in this session what the workers and the environment-related commands run in this session have done so far, and return the user-intervention result.

# Resolve the Error
- **Resolution loop:** plan a fix that keeps file access and changes to the minimum the resolution needs → apply it → verify it. On success, stop. On failure, run the resolvability check, then plan again based on the failure context.
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
  - On a `failed` response, keep it in this session's context, run the resolvability check, plan again, and fork a follow-up worker for the next candidate cause.
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
- For the not-resolvable result, return only a concise final message containing:
  1. The confirmed root cause and its classification.
  2. The resolution that must be performed.
- For the user-intervention result, return only a concise final message containing:
  1. The confirmed root cause and its classification.
  2. The tasks the user must perform.
  3. The summary of what the workers and the environment-related commands have done so far.
- Do not write any result file.
