---
paths:
  - ".claude/**"
---

# Claude Document Format Conventions

Applies to the handwritten Markdown documents under `.claude/`. Generated output and data files (e.g.
`detected-change/`, `reflected-change/`, `cache/`, logs, URL lists) are exempt.

---

## 1. Purpose

Depth and structure are shown with minimal whitespace, because whitespace used only for layout costs tokens on every
read.

---

## 2. Tables

- No cell padding: `| a | b |`.
- Separator rows are `|---|---|`, keeping any `:` alignment markers (`|:---|---:|`).

---

## 3. Directory Trees

- Inside a code block, each depth level takes one glyph column: `│` under an ancestor with later siblings, a space under
  one without, then `├` or `└` and one space before the entry. No `─`.
- A `#` comment follows its entry after one space, with single spaces inside it.
- A comment continued onto the next line starts with that line's depth prefix, followed by `#`.
- Code blocks drawing flow diagrams (branching with `┬` or arrows) are exempt.

```
search/
├ domain/
│├ aggregate/
│└ enums/ # Search condition/target/sort enums
└ framework/
 └ outbound/ # Repository port implementations
  # (combine JPA + jOOQ)
```

---

## 4. Prose and Lists

- Wrap lines at 120 characters, breaking at a space outside inline code; a single longer token stays on one line.
- Continuation lines and nested lists indent to the parent marker's content column: 2 spaces under `-`, 3 under `1.`.
- Exempt: YAML frontmatter, headings, table rows, non-tree code blocks (their indentation is part of the code), and line
  breaks in Korean text.

---

## 5. Spacing

- One space between tokens, no trailing whitespace, and no runs of blank lines.
