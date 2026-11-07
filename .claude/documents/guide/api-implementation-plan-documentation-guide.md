# API Implementation Plan Documentation Guide

How to write the planning documents for a new API implementation: their hierarchy, location, names, and sections. A
fixed layout keeps every plan consistent, short, and readable.

---

## 1. Document Hierarchy

- One implementation spec covers the whole implementation.
- One step document covers each implementation step listed in the spec.
- A reference document holds supporting material that the spec and step documents cite (e.g. a taxonomy of the cases
  the implementation handles), only when the implementation needs it; it follows the spec's abbreviation rule.

---

## 2. Location

Write every document directly under `.claude/temp/`.

---

## 3. Document Names

- Every name starts with the implementation goal the document serves, in English kebab-case (e.g. `swear-auto-report`).
- Implementation spec: `<goal>-implementation-spec.md`.
- Step document: `<goal>-step-<NN>-<detail-goal>.md`, where `<NN>` is the two-digit step number (e.g. `01`) so names
  sort in step order, and `<detail-goal>` is the step's own goal in English kebab-case.
- Reference document: `<goal>-reference-<reference-target>.md`, where `<reference-target>` names the material it holds
  in English kebab-case.

---

## 4. Implementation Spec Sections

### 4.1 Layout

1. Purpose
2. Scope
3. Structure Design
   - 3.1 End-to-End Flow
   - 3.2 Package Structure: each package that takes part in the flow of 3.1, and each class in it, with its role.
   - Storage, External Infrastructure Changes, then Claude Document Changes (changes to files under
     `.claude/documents/`): each only when the implementation needs it, numbered consecutively from 3.3 in this order.
     No other subsections.
4. Technical Details: subsections as the implementation needs, the first of which defines each technical term used in
   the document that is not in general or common use (e.g. a term the implementation coins or narrows).
5. Implementation Steps: one table row per step, giving its step number, document name, purpose, and the steps it
   depends on.
6. Constraints
   - Every step's result must build and pass its tests.
   - Class, method, and variable names must state their purpose clearly.
7. Reference Documents: one table row per reference document, giving its name and what it holds; only when the
   implementation has one.

### 4.2 Content Rules

- Leave out everything else `.claude/skills/postprocess-main-code-change/SKILL.md` handles (code quality check, tests,
  Claude document updates outside `.claude/documents/`, Notion API specs, Confluence table definitions), so the spec
  stays on the implementation. Storage still holds the table, column, and migration design the implementation needs.
  Claude Document Changes holds the `.claude/documents/` changes.
- Write a change to a file outside the repository root (e.g. an external monitoring repository) as steps the user
  carries out by hand, because Claude cannot edit files there.
- Write each fact once within the spec.
- Write a technical term in its full name at its first use, with its abbreviation in parentheses, and use only the
  abbreviation after that. When the first use falls inside a code block, give the full name in the prose before the 
  block and keep only the abbreviation inside it.
- Section 2 and section 3 from 3.2 on refer to no other section, because step documents copy them word for word and a
  copied reference would point outside the step document.
- The format inside each section (prose, list, table, code block) is free. Name a file type by its extension in inline
  code (`.sql` migrations) rather than by an abbreviation.

---

## 5. Step Document Sections

### 5.1 Layout

1. Purpose
2. Scope
3. Structure Design: the same subsections as the spec, except that 3.1 is named `Component Flow`. It covers the
   relations between the step's implementations (which class calls, implements, or reads which) and the workflow
   among them.
4. Technical Details: subsections as the step needs.
5. Other Implementation Requirements

### 5.2 Content Rules

- Section 1 and 3.1 are written for the step. Section 1 states what the step delivers and why it is a separate step.
- Section 2 and section 3 from 3.2 on copy, word for word, only the spec text that applies to the step. Do not link
  or redirect to the spec, so the step document reads on its own.
- Section 4 does not copy the spec; rewrite its technical details in concrete form for the step.
- Section 5 holds what must exist when the step is done and the manual procedures the step requires, then repeats the
  spec's section 6 constraints.
- The spec's content rules on left-out content, files outside the repository root, abbreviations, and section format
  apply as well.
