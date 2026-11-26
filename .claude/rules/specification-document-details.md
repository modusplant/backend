---
paths:
  - ".claude/specifications/**"
---

# Specification Document Details

The spec documents of one spec are its implementation document, step documents, and reference documents. A fixed
layout keeps every spec consistent, short, and readable.

---

## 1. Spec Document Structure

- One implementation document covers the whole implementation: what it does and why, as the basis that every step
  document builds on.
- One step document covers each implementation step listed in the implementation document: the details that implement
  the step's part of the implementation document.
- A reference document holds supporting material that the implementation and step documents cite (e.g. a taxonomy of
  the cases the implementation handles), only when the implementation needs it; it follows the implementation
  document's abbreviation rule.

---

## 2. Location

Write every document directly under `.claude/specifications/<goal>/`, where `<goal>` is the implementation goal that the
document names start with.

---

## 3. Document Names

- Every name starts with the implementation goal the document serves, in English kebab-case (e.g.
  `prohibited-word-auto-report`).
- Implementation document: `<goal>-implementation.md`.
- Step document: `<goal>-step-<NN>-<detail-goal>.md`, where `<NN>` is the two-digit step number (e.g. `01`) so names
  sort in step order, and `<detail-goal>` is the step's own goal in English kebab-case.
- Reference document: `<goal>-reference-<reference-target>.md`, where `<reference-target>` names the material it holds
  in English kebab-case.

---

## 4. Implementation Document Sections

### 4.1 Layout

1. Purpose
2. Scope
3. Structure Design
   - 3.1 End-to-End Flow, drawn as §6 states
   - 3.2 Package Structure: each package that takes part in the flow of 3.1, with the classes that
     `.claude/rules/claude-document-format-details.md` §3 lists for a document that designs class changes: new classes
     whose role their package does not show, and existing classes whose responsibility the change alters. 3.2 exists
     only when it holds a class, and 3.3 keeps its number without it.
   - 3.3 Implementation-Wide Changes: changes that two or more steps make together, such as a rule that every step's
     changes follow. It holds Storage, External Infrastructure Changes, then Claude Document Changes (changes to files
     under `.claude/documents/`): each only when the implementation needs it, numbered consecutively from 3.3.1 in this
     order, with no other subsections. 3.3 exists only when it holds one of these.
4. Implementation Steps: one table row per step, giving its step number, document name, purpose, and the steps it
   depends on.
5. Constraints
   - Every step's result must build and pass its tests, unless an exception is stated for the step.
6. Reference Documents: one table row per reference document, giving its name and what it holds; only when the
   implementation has one.
7. Glossary: follows the **Glossary** rule of `CLAUDE.local.md`, except that every term that two or more documents of
   the spec use is kept here instead of under `.claude/documents/term/`.

### 4.2 Content Rules

- Leave out everything else `.claude/skills/postprocess-main-code-change/SKILL.md` handles (code quality check, tests,
  Claude document updates outside `.claude/documents/`, Notion API specs, Confluence table definitions), so the
  implementation document stays on the implementation. The Storage subsections of the implementation and step documents
  still hold the table, column, and migration design the implementation needs, and their Claude Document Changes
  subsections hold the `.claude/documents/` changes.
- State what the implementation does and why, and leave how each step does it to the step documents.
- Write a change to a file outside the repository root (e.g. an external monitoring repository) as steps the user
  carries out by hand, because Claude cannot edit files there.
- Write each fact once within the implementation document.
- Write a Scope row or Package Structure item that spans several steps as semicolon-separated clauses that each belong
  to one step, and name no step number in it, so each step document expands only its own clauses.
- Write a technical term in its full name at its first use, with its abbreviation in parentheses, and use only the
  abbreviation after that. When the first use falls inside a code block, give the full name in the prose before the
  block and keep only the abbreviation inside it.
- Name a file type by its extension in inline code (`.sql` migrations) rather than by an abbreviation.

---

## 5. Step Document Sections

### 5.1 Layout

1. Purpose
2. Scope
3. Structure Design
   - 3.1 Component Flow: the relations between the step's implementations (which class calls, implements, or reads
     which) and the workflow among them, drawn as §6 states.
   - 3.2 Package Structure: only when it holds a class, as in the implementation document
   - 3.3 Step-Specific Changes: the step's Storage, External Infrastructure Changes, then Claude Document Changes, each
     only when the step needs it, numbered consecutively from 3.3.1 in this order, with no other subsections. 3.3
     exists only when it holds one of these.
4. Technical Details: subsections as the step needs.
5. Other Implementation Requirements
6. Glossary: only the terms that no other document of the spec uses, and only when the step document has one.

### 5.2 Content Rules

- The line after the title is `Expands <goal>-implementation.md.`, naming the implementation document. A step document
  is read together with it, so it restates none of the implementation document's facts.
- Section 1 states what the step delivers and why it is a separate step.
- Sections 2 and 3.2 expand the implementation document's clauses that belong to the step. Each Scope item names the
  Scope row it expands, and each Package Structure item names the class whose clause it expands, with the `existing`
  marker of an existing class; then each gives the step's details of that clause.
- Section 4 gives the step's technical details in concrete form.
- Section 5 holds what must exist when the step is done and the manual procedures the step requires.
- A detail that several steps need lives only in the document of the earliest step that implements it. In each section
  of a later step document that needs the detail, the step's own content comes first, then a
  `**From step <NN>** (§<section>):` block that names the source section and gives only the gist the step needs.
- When several steps change the same section of a document, each step's Claude Document Changes give only the part of
  the change that its own implementation needs, so each step's changes apply independently of the other steps.
- The implementation document's content rules on left-out content, files outside the repository root, and
  abbreviations apply as well.

---

## 6. Flow Diagrams

The End-to-End Flow and Component Flow code blocks draw each line as one step, so that a reader tells an ordered step
from a branch alternative by its glyphs alone:
- `├→` and `└→` without a label: a step that the line above runs; siblings all run, in order from top to bottom.
- `├[<condition>]→` and `└[<condition>]→`: an alternative of a branch; exactly one sibling runs, the one whose condition
  holds. The conditions of one branch exclude each other, and a branch sits under the step that decides it, never
  among ordered steps as their sibling.
- `→ stop` ends an alternative that ends the path, and `→ continue` stands for an alternative that takes no step of
  its own and goes on to the step after the next `↓`.
- `↓` on its own line leads to the next step of one path, optionally followed by an annotation, such as how the step is
  reached or the condition under which the path continues. After a branch, it continues every alternative that does
  not end with `→ stop`.
- `→` inside a line leads from a call to what it calls, returns, or produces.
- `┐` and `┴→` join several entry lines into one next step.
- A `#` comment explains the step it follows after one space on the same line, or the step right above its own lines.

---

## 7. Document Management

- When code, a `.claude/` document, or a design decision that the documents describe changes (whether committed,
  unpushed, or uncommitted), update every spec document it affects in the same task. When an implementation document
  clause changes, update the step document that expands it; when a step document's detail changes, update the
  `From step` blocks that receive it.
- Include in the documents only what no document of a Claude document hierarchy states.
- When the user asks to delete a spec document, delete it and every mention of it in the other documents as well. When
  the deleted document is a step document, the mentions include all content of its step: the step's design, the
  dependencies on it, the `From step` blocks that receive from it, and the references to the sections removed with it.
  The remaining steps keep their numbers.
- Insert a new step at its place in dependency order, and raise the number of each later step by one, in its document
  name and in every reference to it.
- Report to the user any rule this document lacks or needs to strengthen that writing or revising the documents reveals.
