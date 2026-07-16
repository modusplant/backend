---
name: reporting-performance-optimization
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

1. **Flow Analysis:** Recursively trace and analyze all layers connected to the target REST API method(s) to map out the entire logic flow.
2. **Existing Optimizations:** Identify and summarize notable performance optimization elements currently present in each layer that contribute to high-traffic handling (e.g., efficient use of caching layers, leveraging Virtual Threads).
3. **Missing Optimizations:** Identify and list missing or suboptimal patterns in each layer that cause performance bottlenecks under high load (e.g., patterns causing excessive Garbage Collection, redundant or over-extended transaction boundaries, inefficient queries).
4. **Prioritization & Conclusion:** Evaluate each missing optimization point against the following three criteria, each rated High/Medium/Low:
   - **Severity:** Magnitude of performance impact under high-traffic load — effect on p95/p99 latency, throughput ceiling, or resource exhaustion (CPU, memory, DB/connection pool).
   - **Feasibility:** Likelihood the bottleneck is actually triggered given projected traffic — How likely is this problem will occur under high-volume traffic.
   - **Complexity:** Engineering effort and risk to implement the fix — scope of code changes, need for schema/infrastructure changes, and testing burden.

   Score each level High=3/Medium=2/Low=1 and compute `Priority Score = Severity + Feasibility - Complexity`. 
   Rank all missing optimizations by descending Priority Score (ties broken by higher Severity) into a priority matrix, then close with a definitive final conclusion naming the top-priority optimization(s) to implement first.

# Follow-up Actions
- **Output Location:** Save the result under the @.claude/skills/reporting-performance-optimization/ directory (no need to print it to context).
- **File Naming:** The filename is set to `report_[domainName]_[methodName].md`.
- **File Handling:** If the file does not exist, create it. If the file already exists, overwrite it.