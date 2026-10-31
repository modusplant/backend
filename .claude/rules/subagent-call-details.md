---
paths:
  - ".claude/skills/reflect-api-change-into-notion/**"
  - ".claude/skills/reflect-flyway-change-into-external-source/**"
---

# Subagent Call Details

Applies only when a skill sends a prompt to a subagent under @.claude/agents/ in a format defined in a file other than
its `SKILL.md`.

---

## 1. Prompt Template

- The calling skill owns its prompt format as `prompt-template.md` in its own skill directory, read-only to the skill
  itself.
- Placeholders are written as `<...>`; everything else is sent verbatim.
- A skill with one prompt writes it as the whole file. A skill with more than one puts each in a four-backtick
  `markdown` fenced block under its own `# <Kind> Prompt` heading, where `<kind>` is its lowercase `<Kind>` (e.g.
  `# Fetch Prompt` → `fetch`).

---

## 2. Prompt File

Path: `.claude/temp/<skill-name>-prompt-to-<subagent>-<yy-mm-dd-hh-mm-ss>[-<kind>].md`

- `<skill-name>`: the calling skill's `name`; `<subagent>`: the called agent's `name`.
- `<yy-mm-dd-hh-mm-ss>`: two-digit year through second, taken when the file is written.
- `-<kind>`: only for a skill with more than one prompt, so that its prompts in one run never share a name.

Content is the template's prompt with every placeholder filled in, in this shape:

````markdown
# Prompt

<task sentence>

Workflow:
1. <step>

# Resources

- <role>: <absolute path>
````

- `# Prompt` holds the task and its ordered workflow; `# Resources` holds every local file the workflow reads or
  writes, by absolute path, so the subagent needs no context beyond the file.

---

## 3. Call Procedure

1. Write the prompt file.
2. Call the Agent tool with the subagent's `subagent_type` and only this prompt:
   `Read <absolute path of the prompt file> and carry out its prompt exactly as written.`
3. Wait for the subagent's completion notification, then handle `SUCCEEDED` or `FAILED` as the skill defines.