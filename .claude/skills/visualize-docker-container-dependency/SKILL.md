---
name: visualize-docker-container-dependency
description: Builds a fresh depends_on tree of every Docker container defined by the root docker-compose.yml, docker-compose.override.yml, and their include targets, and renders it with Graphviz as a single static HTML file.
disable-model-invocation: true
allowed-tools: Bash(python3 .claude/skills/visualize-docker-container-dependency/scripts/build_graph.py*)
---

# Workflow

1. From the project root, run `python3 .claude/skills/visualize-docker-container-dependency/scripts/build_graph.py`.
2. **Exit code 2 (`MISSING_TOOL`):** Relay the listed tools and install commands to the user, and ask whether to install them. Once approved and installed, run step 1 again.
3. **Exit code 1 (`ERROR`):** Report the error line as-is and stop.
4. **Exit code 0:** Report the `OUTPUT` path, the `SUMMARY` line, and every `UNDEFINED_TARGETS` / `WARNING` line.

# Output

- **Location:** `result/docker_dependency_<YYYYMMDDHHmm>.html` under this skill's directory (no need to print its content to context).
- **Content:** One inline-SVG graph that opens directly in Google Chrome.
  - **Nodes:** One per service, labeled with the service name and its `container_name`, clustered by Compose file group.
  - **Edges:** Dependent → dependency, labeled with the `depends_on` condition; styles are listed in the page legend.

# Constraint

- **Single Reader:** @.claude/skills/visualize-docker-container-dependency/scripts/build_graph.py is the only component that reads files, and it opens Compose files only.
- **Always Fresh:** Do not read, reuse, or compare against previous files under @.claude/skills/visualize-docker-container-dependency/result/.