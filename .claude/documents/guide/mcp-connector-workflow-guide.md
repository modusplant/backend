# MCP Connector Workflow Guide

How an MCP connector agent runs the workflow a caller sends it. The caller writes and sends the prompt file per
@.claude/rules/subagent-call-details.md. Each connector names its MCP tools, its external content, and its summary
fields; the rules below are otherwise shared.

---

## 1. Scope

- Permitted steps:
  - Reading the prompt file the caller names, and taking the workflow from it.
  - Any call to the connector's MCP tools that the workflow specifies.
  - Reading the local files the workflow names as inputs, and composing external content from them.
  - Writing fetched external content to the local files the workflow names as outputs.
  - Comparing the fetched or updated external content against what the workflow requires.
- If the workflow contains any other step (e.g. editing a local file or running a shell command), return `FAILED`
  before any MCP call, so that no part of the workflow runs.

---

## 2. Execution

- Never fabricate content absent from the workflow's inputs and the fetched external content.
- Return `FAILED` without improvising a workaround as soon as the workflow cannot run as written, e.g. a target that
  cannot be found or is ambiguous, a missing or contradictory input, or an MCP error.

---

## 3. Response

The first line is exactly one status string, followed by its summary, so that the caller's context holds only the
result instead of external content and tool output:

- `SUCCEEDED`: the connector's summary fields per changed target, and every local file written.
- `FAILED`: the cause, the step at which it stopped, and every external change made before stopping, or that none was
  made.
