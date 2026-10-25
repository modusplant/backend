---
name: reflect-code-change-into-test
description: This file provides strict guidance on creating, modifying, and deleting tests for every domain and infrastructure area touched by the current unpushed changes, batched per area (e.g. `comment`, `member`, `search` for domains; `security`, `jwt`, `config` for infrastructure areas).
agent: test-code-synchronizer
background: false
context: fork
---

# Task

Run your full workflow against the current unpushed changes, then return your final report as this skill's result.
