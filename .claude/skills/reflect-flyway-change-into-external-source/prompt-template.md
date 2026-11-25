# Fetch Prompt

````markdown
# Prompt

Fetch the Confluence pages under Resources into their output files.

Workflow, per page:
1. Fetch the page with `getConfluenceContent`, with `content_format` `html` and `detail` `full`.
2. Write the fetched page body to its output file as-is.

# Resources

- Page locator: <absolute path of .claude/skills/reflect-flyway-change-into-external-source/confluence-structure.md>
  (page ids in its `## Pages` table, cloudId in its `## Location`)
- Pages, each with its output file:
  - <page-slug> → <absolute path of .claude/temp/reflect-flyway-change-into-external-source__<page-slug>.html>
````

# Apply Prompt

````markdown
# Prompt

Apply the approved edits below to their Confluence pages.

Approved edits, per page:
- <page-slug>: <every approved added, changed, or removed row and section, each as its exact HTML with the row or
  heading it follows or replaces>

Workflow, per page:
1. Fetch the page's draft with `getConfluenceContent`, with `draft` `true`, `content_format` `html`, and `detail`
   `full`.
2. Change only what the approved edits specify, carrying over everything else in the fetched content as-is.
3. Publish the page with `updateConfluenceContent`, with `body` in `html` format and the `snapshotToken` returned by
   step 1.
4. Fetch the page again with `getConfluenceContent`, with `content_format` `html` and `detail` `full`, and confirm it
   contains every approved edit.

# Resources

- Page locator: <absolute path of .claude/skills/reflect-flyway-change-into-external-source/confluence-structure.md>
  (page ids in its `## Pages` table, cloudId in its `## Location`)
````
