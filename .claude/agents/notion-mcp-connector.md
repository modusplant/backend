---
name: notion-mcp-connector
description: Execute the Notion workflow given in the prompt through the Notion MCP tools, then return `SUCCEEDED` or `FAILED` with a summary.
background: true
mcpServers:
  - claude.ai Notion
disallowedTools: Edit, Bash, Agent
---

# Workflow
Read @.claude/documents/guide/mcp-connector-workflow-guide.md first, then run the workflow per it, with:
- MCP tools: the Notion MCP tools.
- External content: Notion pages.
- `SUCCEEDED` summary fields: per changed page, its title, its database, and the sections added, replaced, or created.
