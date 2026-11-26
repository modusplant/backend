---
paths:
  - ".claude/specifications/**"
---

# Specification Document Details

The spec documents of one spec are its implementation document, step documents, and reference documents. A fixed
layout keeps every spec consistent, short, and readable.

---

## 1. Spec Document Structure

- One implementation document covers the whole implementation.
- One step document covers each implementation step listed in the implementation document.
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
   - 3.1 End-to-End Flow
   - 3.2 Package Structure: each package that takes part in the flow of 3.1, listing only the classes whose role
     neither their package nor `.claude/rules/main-architecture-details.md` shows. An existing class gives only its
     change, and a new class only the feature or role it adds.
   - 3.3 Implementation-Wide Changes: changes that two or more steps make together, such as a rule that every step's
     changes follow.
   - 3.4 Step-Specific Changes: changes that one step makes, each naming the number of the step it belongs to, even
     when later steps use the result.
   - 3.3 and 3.4 each hold Storage, External Infrastructure Changes, then Claude Document Changes (changes to files
     under `.claude/documents/`): each only when the implementation needs it, numbered consecutively from `.1` in this
     order. Each of 3.3 and 3.4 exists only when it holds one of these, numbered consecutively from 3.3 in this order.
     No other subsections.
4. Technical Details: subsections as the implementation needs, in the order of the steps they belong to.
5. Implementation Steps: one table row per step, giving its step number, document name, purpose, and the steps it
   depends on.
6. Constraints
   - Every step's result must build and pass its tests, unless an exception is stated for the step.
7. Reference Documents: one table row per reference document, giving its name and what it holds; only when the
   implementation has one.
8. Glossary: as the **Glossary** rule of `CLAUDE.local.md` requires.

### 4.2 Content Rules

- Leave out everything else `.claude/skills/postprocess-main-code-change/SKILL.md` handles (code quality check, tests,
  Claude document updates outside `.claude/documents/`, Notion API specs, Confluence table definitions), so the
  implementation document stays on the implementation. The Storage subsections still hold the table, column, and
  migration design the implementation needs, and the Claude Document Changes subsections hold the `.claude/documents/`
  changes.
- Write a change to a file outside the repository root (e.g. an external monitoring repository) as steps the user
  carries out by hand, because Claude cannot edit files there.
- Write each fact once within the implementation document.
- Write a Scope row or Package Structure item that spans several steps as semicolon-separated clauses that each belong
  to one step, and name no step number in it, so each step document copies only its own clauses.
- Write a technical term in its full name at its first use, with its abbreviation in parentheses, and use only the
  abbreviation after that. When the first use falls inside a code block, give the full name in the prose before the
  block and keep only the abbreviation inside it.
- Section 2 and section 3 from 3.2 on refer to no other section, because step documents copy them word for word and a
  copied reference would point outside the step document.
- Name a file type by its extension in inline code (`.sql` migrations) rather than by an abbreviation.

---

## 5. Step Document Sections

### 5.1 Layout

1. Purpose
2. Scope
3. Structure Design: the same subsections as the implementation document, except that 3.1 is named `Component Flow`.
   It covers the relations between the step's implementations (which class calls, implements, or reads which) and the
   workflow among them.
4. Technical Details: subsections as the step needs.
5. Other Implementation Requirements
6. Glossary: as the **Glossary** rule of `CLAUDE.local.md` requires.

### 5.2 Content Rules

- Section 1 and 3.1 are written for the step. Section 1 states what the step delivers and why it is a separate step.
- Section 2 and section 3 from 3.2 on copy, word for word, only the implementation document text (rows, items, or
  clauses) that the step needs. A Package Structure item copies its class name and `existing` marker, then only the
  clauses the step needs. Do not link or redirect to the implementation document, so the step document reads on its
  own.
- Section 4 does not copy the implementation document; rewrite its technical details in concrete form for the step.
- Section 5 holds what must exist when the step is done and the manual procedures the step requires, then repeats the
  implementation document's section 6 constraints.
- The implementation document's content rules on left-out content, files outside the repository root, and
  abbreviations apply as well.

---

## 6. Document Management

- When code, a `.claude/` document, or a design decision that the documents describe changes (whether committed,
  unpushed, or uncommitted), update every spec document it affects in the same task. Text that a step document copies
  from the implementation document stays identical to it.
- Include in the documents only what no document of a Claude document hierarchy states.
- When the user asks to delete a spec document, delete it and every mention of it in the other documents as well. When
  the deleted document is a step document, the mentions include all content of its step: the step's design, the
  dependencies on it, and the references to the sections removed with it. The remaining steps and the implementation
  document's section 4 subsections keep their numbers.
- Insert a new step at its place in dependency order, and raise the number of each later step by one, in its document
  name and in every reference to it.
- Report to the user any rule this document lacks or needs to strengthen that writing or revising the documents reveals.
