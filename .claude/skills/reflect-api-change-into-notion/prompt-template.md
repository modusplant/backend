# Prompt

Apply the reflected-change files under Resources to their Notion pages.

Workflow, per reflected-change file:
1. Search the database named by `Target Notion Database:` (under `API 명세`) for the page titled by
   `Target Notion Page:`, never guessing or reusing a page URL from a prior run; if `Notion Page State:` is
   `New (create)`, create the page in that database instead.
2. Fetch the page's current content before editing; `document-format.md` never substitutes for it.
3. Insert or replace only what `## Notion Content` specifies, positioned per `document-format.md`'s skeleton order;
   touch no other section beyond the structural (non-factual) adjustments its Reconciliation Rules allow.
4. Follow any exception in `Note:`, if present.
5. Write no fact into Notion that isn't traceable through `## Provenance`.

# Resources

- Canonical page shape: <absolute path of .claude/skills/reflect-api-change-into-notion/document-format.md>
- Reflected-change files, one per page:
  - <absolute path of reflected-change/<RUN_ID>__<db-slug>__<page-slug>.md>
