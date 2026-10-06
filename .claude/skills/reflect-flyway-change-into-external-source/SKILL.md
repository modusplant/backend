---
name: reflect-flyway-change-into-external-source
description: Detects unpushed Flyway migration and JPA entity changes, then reflects their table-column and default-value facts into the Confluence `테이블 정의서` pages.
allowed-tools: Edit(/.claude/skills/reflect-flyway-change-into-external-source/confluence-result/**) Edit(/.claude/skills/reflect-flyway-change-into-external-source/naming-convention.md) Edit(/.claude/skills/reflect-flyway-change-into-external-source/confluence-structure.md)
disallowed-tools: Edit(/.claude/skills/reflect-flyway-change-into-external-source/SKILL.md) Edit(/src/**)
---

# Preconditions

- This skill only reads Flyway migrations, JPA entity classes, their referenced constant classes,
  git metadata, and, only to choose the page for a table absent from the Table Index, the code
  that uses the table's entity (controllers and repositories). It only writes under its own
  `confluence-result/`, to its own `naming-convention.md` and `confluence-structure.md`, and to
  the Confluence pages listed in `confluence-structure.md` via the Atlassian MCP tools.
- @.claude/skills/reflect-flyway-change-into-external-source/confluence-structure.md is the
  canonical page locator, section skeleton, and placement rule set.
- @.claude/skills/reflect-flyway-change-into-external-source/naming-convention.md is the
  canonical Korean-label glossary and rendering rule set.

# Target Sources

- Primary: every file below not yet pushed to the remote, with its git status (`A`, `M`, `D`,
  or `R` with its old path) — the union of:
  - uncommitted changes: `git status --porcelain -- <pathspecs>` (staged, unstaged, and untracked)
  - committed-but-unpushed changes: `git diff --name-status $(git rev-parse --abbrev-ref --symbolic-full-name @{u} 2>/dev/null || echo origin/main)...HEAD -- <pathspecs>`
- `<pathspecs>`:
  - Flyway migrations: `'src/main/resources/db/migration/*.sql'` and `'src/main/java/db/migration/*.java'`
  - JPA entity classes: `'src/main/java/*/jpa/entity/*Entity.java'`
- There is no Affected cascade.
- Termination: if the union is empty, terminate the skill immediately.

# Change Derivation

Derive only the changes each Target Source itself requires, and reflect them as they are. Read
other migrations only to resolve a dropped constraint or index (below) and in Workflow step 5's
diagnosis, never to reconstruct a table's state.
Render every value per `naming-convention.md`'s Rendering Rules.

## Migration Changes

- Read the migration's DDL as it currently is (for a `.java` migration, from its SQL string
  literals). DML (`INSERT`, `UPDATE`, `DELETE`, `SELECT`) requires no change.
- Collapse the file's statements into their net result per table (e.g. a column added and then
  renamed in the same file is one added row under its final name).
- Process multiple migrations in Flyway version order (`V<major>.<minor>.<patch>` and
  `V<major>_<minor>_<patch>` compared numerically per segment), each against the result of the
  earlier ones.
- Statements map to changes of the Main Column Table
  (`한글명 | 영문명 | 타입 | null 허용 | 제약 조건 | 도메인`) and the Defaults Section
  (`## 디폴트 처리되는 컬럼`, `컬럼명 | 디폴트 값`):
  - `CREATE TABLE` → a new section with a row per column in declared order (type, nullability);
    its inline or table-level `PRIMARY KEY` / `UNIQUE` / `REFERENCES` clauses as each named
    column's `제약 조건`, and its `DEFAULT` clauses as Defaults Section rows.
  - `DROP TABLE` → remove the whole section; `RENAME TO` → rename the table in its heading.
  - `ADD COLUMN` → append a row, in statement order, with its constraint clause as `제약 조건`
    and its `DEFAULT` as a Defaults Section row; `DROP COLUMN` → remove its rows;
    `RENAME COLUMN` → rename it in place.
  - `ALTER COLUMN ... TYPE` / `SET NOT NULL` / `DROP NOT NULL` → update that cell.
  - `SET DEFAULT` / `DROP DEFAULT` → add, update, or remove its Defaults Section row.
  - `ADD [CONSTRAINT ...] PRIMARY KEY` / `UNIQUE` / `FOREIGN KEY ... REFERENCES`, and
    `CREATE UNIQUE INDEX` on a single column → set `제약 조건` of the named columns.
  - `DROP CONSTRAINT` / `DROP INDEX` → clear the dropped `PK`, `UNIQUE`, or `FK` from `제약 조건`
    of its columns. Resolve its kind and columns from the statement that created it, in the same
    file or found by `git grep -lw '<name>'` over the migration pathspecs; an unnamed constraint
    has PostgreSQL's default name (`<table>_pkey`, `<table>_<column>_key`, `<table>_<column>_fkey`). 
    A dropped `CHECK` constraint or non-unique index requires no change.
  - Any other statement (e.g. `CHECK` constraints, non-unique `CREATE INDEX`, `COMMENT ON`) requires no change.

## Entity Changes

- The table is the one in `@Table(name = ...)`, resolving constants through
  `src/main/java/kr/modusplant/shared/persistence/constant/TableName.java`.
- An entity default is a field's `@CreatedDate` / `@LastModifiedDate`, its assignment in the
  `@PrePersist` method (literal as written, e.g. `0L`, `USER`), its initializer, or its
  `@Builder.Default` value. Its column is the one in `@Column(name = ...)`, resolving constants
  through `src/main/java/kr/modusplant/shared/persistence/constant/TableColumnName.java`, or the
  snake_case field name.
- The required change is every entity default added, changed, or removed in the file's diff
  against the base used in Target Sources (every entity default for an added file), applied to
  the Defaults Section. An entity default wins over a DB default for the same column.

## Effect Scope

- Never derive `## 세부 사항`, the `도메인` column, or any other section.
- Termination: if every derived change is skipped as already reflected, terminate without
  writing a result file. State which sources were dropped and why.

# Workflow

1. Resolve Target Sources and run the Target Sources Termination check.
2. Run Change Derivation for each Target Source.
3. Locate each table's page through `confluence-structure.md`'s Table Index. For a table absent
   from the index, use its Prefix Map; if no prefix matches, propose a page per the Prefix Map's
   page kinds and ask the user which page to use.
4. Fetch each target page in `html` content format. Skip every derived change already reflected
   in its current content, and run the Effect Scope Termination check.
5. Plan each page's edit per `confluence-structure.md`'s Section Skeleton and Placement Rules,
   keeping every row outside the derived changes as-is. For a change that cannot be applied to
   the current content (e.g. a missing or mismatched row or section), analyze the Flyway history
   of its table (`git grep -lw '<table>'` over the migration pathspecs, in Flyway version order):
   - document error (the page departs from the history) → leave the change out of the edit and
     report it in a separate section of the final report;
   - migration defect (the migration contradicts the history) → apply nothing, write no result
     file, report the defect, and terminate.
   For every column or table without a glossary entry in `naming-convention.md`, draft its Korean
   label from the glossary's tokens and patterns.
6. Show the user the full edit per page — every added, changed, or removed row and section, and
   every drafted Korean label marked as drafted — and get explicit approval via AskUserQuestion.
   Apply nothing that was not approved; on rejection, adjust per the user's answer and ask again.
7. Apply each approved page edit with `updateConfluencePage` in `html` content format, carrying
   over the fetched content for everything outside the edit.
8. Add every approved new Korean label to `naming-convention.md`'s Glossary, and every newly
   placed or removed table to `confluence-structure.md`'s Table Index.
9. Write the result files (see Result File Format).

# Result File Format

Path: `confluence-result/<RUN_ID>__<source-stem>__<page-slug>.md`, an empty file.

- `RUN_ID = YYYYMMDD_HHmmss` (from `date +%Y%m%d_%H%M%S` at the moment the run starts). On a
  same-second collision, append `_2`, `_3`, ... to `RUN_ID`.
- `<source-stem>`: the migration's file name without extension (e.g.
  `V5.2.0__Create_plant_variety_table`), or the entity's simple class name (e.g. `PostEntity`).
- `<page-slug>`: the page's slug from `confluence-structure.md`.
- Write one file per (Target Source, page) pair whose edit was applied successfully. Files
  accumulate; never overwrite or delete a prior run's file.

# Hard Constraints

- Never invoke this skill on your own initiative; only run it when invoked by
  @.claude/skills/postprocess-main-code-change/SKILL.md's workflow.
- Never create, modify, or delete anything in Confluence except what a Target Source implies
  through Change Derivation.
- Preserve each page's existing format: heading style, table shape, column order, row order,
  section order, and separators.
- Never fabricate a fact not present in the actual source files or the current Confluence content.
- English-only in this skill's own files; Korean is permitted only where a literal string must
  round-trip exactly into/out of Confluence.
