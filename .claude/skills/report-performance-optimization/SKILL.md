---
name: report-performance-optimization
description: Performs a technical analysis of a single REST API method to identify missing optimizations and derive new architectural or code-level optimizations for high-traffic conditions.
disable-model-invocation: true
---

# Argument
- **$0:** The domain name to target, passed as the first argument to the prompt.
- **$1:** The REST API method name to target, passed as the second argument to the prompt.

# Precondition
- **Domain Name Resolution:** `[domainName]` and `[DomainName]` are dynamically resolved from $0.
  - **[domainName]:** lowercase/camelCase form of $0 (e.g., $0 = `member` → `member`; $0 = `search` → `search`).
  - **[DomainName]:** Capitalized/PascalCase form of $0 (e.g., $0 = `member` → `Member`; $0 = `search` → `Search`).
- **Method Name Resolution:** `[methodName]` is dynamically resolved from $1 (e.g., $1 = `searchPlantKoreanNameByKeyword` → `searchPlantKoreanNameByKeyword`).
- **Target Controller:** @src/main/java/kr/modusplant/domains/[domainName]/framework/inbound/web/rest/[DomainName]RestController.java
- **Multiple Method Matches:** If [methodName] matches multiple methods within the target REST Controller, all matching methods must be targeted for analysis.

# Report

Produce a report with the following sequential sections:

1. **Flow Analysis:** Recursively trace and analyze all layers connected to the target REST API method(s) to map out the entire logic flow. (Capture this step's output as `$FLOW_ANALYSIS`.)
2. **Existing Optimizations:** Identify and summarize notable performance optimization elements currently present in each layer that contribute to high-traffic handling (e.g., efficient use of caching layers, leveraging Virtual Threads). (Capture this step's output as `$EXISTING_OPTIMIZATIONS`.)
3. **Missing Optimizations:** Identify and list missing or suboptimal patterns in each layer that cause performance bottlenecks under high load (e.g., patterns causing excessive Garbage Collection, redundant or over-extended transaction boundaries, inefficient queries). (Capture this step's output as `$MISSING_OPTIMIZATIONS`.)
4. **Prioritization:** Evaluate each missing optimization point against the following three criteria, each rated High/Medium/Low:
   - **Severity:** Magnitude of performance impact under high-traffic load — effect on p95/p99 latency, throughput ceiling, or resource exhaustion (CPU, memory, DB/connection pool).
   - **Feasibility:** Likelihood the bottleneck is actually triggered given projected traffic — How likely is this problem will occur under high-volume traffic.
   - **Complexity:** Engineering effort and risk to implement the fix — scope of code changes, need for schema/infrastructure changes, and testing burden.

   Score each level High=3/Medium=2/Low=1 and compute `Priority Score = Severity + Feasibility - Complexity`.
   Rank all missing optimizations by descending Priority Score (ties broken by higher Severity) into a priority matrix. (Capture this step's output as `$PRIORITY_MATRIX`.)
5. **Conclusion:** Close with a definitive final conclusion naming the top-priority optimization(s) to implement first, based on the priority matrix from step 4. (Capture this step's output as `$CONCLUSION`.)

# Follow-up Actions
- **Rendering Target:** The report is delivered as a single static HTML file rendered from @.claude/skills/report-performance-optimization/template.html.
- **Output Location:** Save the rendered result under @.claude/skills/report-performance-optimization/result/ (no need to print it to context).
- **File Naming:** The filename is set to `report_[domainName]_[methodName].html`.
- **File Handling:** If the file does not exist, create it by copying `template.html` into the output location. If it already exists, overwrite it.
- **Placeholder Substitution:** Fill the copied template by replacing each `{{TOKEN}}` with the corresponding captured variable, rendered as an HTML fragment (table rows, list items, or plain text):
  - `{{DOMAIN_NAME}}` → `[domainName]`, `{{METHOD_NAME}}` → `[methodName]`, `{{REPORT_DATE}}` → current date, `{{TARGET_DESCRIPTION}}` → the resolved target controller/method(s) from the Precondition section.
  - `{{FLOW_ANALYSIS_CONTENT}}` → `$FLOW_ANALYSIS`, wrapped as plain text inside the template's `.flow-trace` block.
  - `{{EXISTING_OPTIMIZATIONS_CONTENT}}` → `$EXISTING_OPTIMIZATIONS` as `<tr><td>...</td></tr>` rows (Layer / Optimization / Effect columns).
  - `{{MISSING_OPTIMIZATIONS_CONTENT}}` → `$MISSING_OPTIMIZATIONS` as grouped blocks using the template's `.finding-group` / `.finding-item` / `.finding-id` markup.
  - `{{PRIORITY_MATRIX_CONTENT}}` → `$PRIORITY_MATRIX` as `<tr>` rows, each including a `.score-bar-fill` sized via inline `style="width: X%"` where `X = score / max_score * 100`.
  - `{{CONCLUSION_CONTENT}}` → `$CONCLUSION`.
- **Minimal substitution example** (single pass, no external templating library needed):
  ```python
  import pathlib

  values = {
      "{{DOMAIN_NAME}}": domain_name,
      "{{METHOD_NAME}}": method_name,
      "{{REPORT_DATE}}": report_date,
      "{{TARGET_DESCRIPTION}}": target_description,
      "{{FLOW_ANALYSIS_CONTENT}}": flow_analysis_html,
      "{{EXISTING_OPTIMIZATIONS_CONTENT}}": existing_optimizations_html,
      "{{MISSING_OPTIMIZATIONS_CONTENT}}": missing_optimizations_html,
      "{{PRIORITY_MATRIX_CONTENT}}": priority_matrix_html,
      "{{CONCLUSION_CONTENT}}": conclusion_html,
  }

  html = pathlib.Path("template.html").read_text()
  for token, content in values.items():
      html = html.replace(token, content)
  pathlib.Path(f"result/report_{domain_name}_{method_name}.html").write_text(html)
  ```