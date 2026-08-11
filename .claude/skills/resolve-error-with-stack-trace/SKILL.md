---
name: resolve-error-with-stack-trace
description: Diagnose the precise root cause of an error from its stack trace and resolve it.
disable-model-invocation: true
arguments: [errorStackTrace]
---

# Hypothesize the Root Cause
- Strip the stack trace down to its essential frames to avoid unnecessary noise and complexity in later steps. (Capture this step's output as `$STACK_TRACE_SUMMARY`.)
- If the stack trace alone does not sufficiently narrow down the cause, stop the skill immediately and ask the user for a more specific stack trace.
- Classify the root cause as either code-based or environment-based. (Capture this classification as `$ROOT_CAUSE_CLASSIFICATION`.)

# Resolve the Error
- **Prerequisite:** Each attempt must be conducted independently and must not affect the environment or conditions of the others in any way.
- **Code-based hypothesis:**
  - Parallel: attempt resolution in parallel using `git worktree`.
  - Search scope: before attempting resolution, build an optimal set of starting paths, then search outward from them.
  - Access scope: access is allowed across most paths, except `@src/test/`, which is off-limits.
- **Environment-based hypothesis:**
  - Sequential: attempt environment-related commands sequentially within a single agent.
- Track every hypothesis attempted, in order, regardless of outcome — classification, description, how it was verified, and whether it was rejected or resolved the error. (Capture this trail as `$HYPOTHESES_CONSIDERED`.)
- Once resolution succeeds, stop all remaining resolution attempts immediately.
  - Remove any worktree used during resolution with `git worktree remove`.
  - For the hypothesis that resolved the error, capture the full account of hypothesis formation → verification → resolution as `$RESOLUTION_DETAIL`.

# Report the Result
1. Summary of the stack trace. (`$STACK_TRACE_SUMMARY`)
2. Every hypothesis considered. (`$HYPOTHESES_CONSIDERED`)
3. For the hypothesis that resolved the error, a detailed account of the full process: hypothesis formation → verification → resolution. (`$RESOLUTION_DETAIL`)
4. A definitive closing conclusion naming the confirmed root cause and the fix applied. (Capture this step's output as `$CONCLUSION`.)

# Follow-up Actions

- **Rendering Target:** The report is delivered as a single static HTML file rendered from @.claude/skills/resolve-error-with-stack-trace/template.html.
- **Output Location:** Save the rendered result under @.claude/skills/resolve-error-with-stack-trace/result/ (no need to print it to context).
- **File Naming:** The filename is set to `resolve_[errorSlug].html`, where `[errorSlug]` is a concise snake_case slug (2-5 words) summarizing the resolved root-cause exception, derived from the primary exception/cause (e.g. a `ConnectException: Connection refused` surfaced via `SdkClientException` → `connection_refused_error`).
- **File Handling:** If the file does not exist, create it by copying `template.html` into the output location. If it already exists, overwrite it.
- **Placeholder Substitution:** Fill the copied template by replacing each `{{TOKEN}}` with the corresponding captured variable, rendered as an HTML fragment (table rows, list items, or plain text):
  - `{{ERROR_TYPE}}` → the primary/top-level exception class name from the stack trace, `{{ROOT_CAUSE_CLASSIFICATION}}` → `$ROOT_CAUSE_CLASSIFICATION` ("Code-based" or "Environment-based"), `{{REPORT_DATE}}` → current date, `{{TARGET_DESCRIPTION}}` → a one-line resolved-status summary (what failed, where, and its current state).
  - `{{STACK_TRACE_SUMMARY_CONTENT}}` → `$STACK_TRACE_SUMMARY`, wrapped as plain text inside the template's `.flow-trace` block.
  - `{{HYPOTHESES_CONTENT}}` → `$HYPOTHESES_CONSIDERED` as `<tr>` rows (# / Hypothesis / Classification / Verification / Outcome columns). Classification renders as `<span class="pill pill-code">Code-based</span>` or `<span class="pill pill-env">Environment-based</span>`; Outcome renders as `<span class="pill pill-resolved">Resolved</span>` or `<span class="pill pill-rejected">Rejected</span>`.
  - `{{RESOLUTION_PROCESS_CONTENT}}` → `$RESOLUTION_DETAIL` as three `.finding-group` blocks titled "Hypothesis Formation", "Verification", and "Resolution", each using the template's `.finding-group-title` / `.finding-list` / `.finding-item` markup (or plain paragraphs where a step has no discrete sub-items, e.g. command output shown in a nested `.flow-trace` block).
  - `{{CONCLUSION_CONTENT}}` → `$CONCLUSION`.
- **Minimal substitution example** (single pass, no external templating library needed):
  ```python
  import pathlib

  values = {
      "{{ERROR_TYPE}}": error_type,
      "{{ROOT_CAUSE_CLASSIFICATION}}": root_cause_classification,
      "{{REPORT_DATE}}": report_date,
      "{{TARGET_DESCRIPTION}}": target_description,
      "{{STACK_TRACE_SUMMARY_CONTENT}}": stack_trace_summary_html,
      "{{HYPOTHESES_CONTENT}}": hypotheses_html,
      "{{RESOLUTION_PROCESS_CONTENT}}": resolution_process_html,
      "{{CONCLUSION_CONTENT}}": conclusion_html,
  }

  html = pathlib.Path("template.html").read_text()
  for token, content in values.items():
      html = html.replace(token, content)
  pathlib.Path(f"result/resolve_{error_slug}.html").write_text(html)
  ```
