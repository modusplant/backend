# Naming Convention — `테이블 정의서`

The ubiquitous language of the Confluence `테이블 정의서` pages, used to draft Korean labels and to
render derived facts. A label found in the glossary is used as-is; a label drafted from its tokens
and patterns needs user approval.

## Glossary

The glossary is machine-readable: UTF-8 TSV files under `glossary/`, each with one header line and
one entry per line.

| File          | Fields                     | Meaning                                                                                                                    |
|---------------|----------------------------|----------------------------------------------------------------------------------------------------------------------------|
| `table.tsv`   | `table`, `label`           | Korean label of a table                                                                                                    |
| `column.tsv`  | `column`, `table`, `label` | Korean label of a column; an empty `table` applies to every table, a non-empty one only to that table and takes precedence |
| `token.tsv`   | `token`, `scope`, `korean` | Korean equivalent of a name fragment; `scope` is `table`, `column`, or empty for both                                      |
| `pattern.tsv` | `pattern`, `korean`        | Column-name pattern and its Korean pattern, with `<x>` as the stem                                                         |

Access the glossary through
`python3 .claude/skills/reflect-flyway-change-into-external-source/scripts/glossary.py`, 
run from the project root.

| Command                                    | Output per input                                                                                                       |
|--------------------------------------------|------------------------------------------------------------------------------------------------------------------------|
| `lookup table <table>...`                  | `FOUND<TAB><table><TAB><label>` or `MISSING<TAB><table>`                                                               |
| `lookup column <table> <column>...`        | `FOUND<TAB><table><TAB><column><TAB><label>` or `MISSING<TAB><table><TAB><column>`                                     |
| `draft table <table>...`                   | `FOUND` as above, or `DRAFT<TAB><table><TAB><candidate><TAB>unresolved=<fragments>`                                    |
| `draft column <table> <column>...`         | `FOUND` as above, or `DRAFT<TAB><table><TAB><column><TAB><candidate><TAB>pattern=<pattern><TAB>unresolved=<fragments>` |
| `add table <table> <label>`                | `ADDED<TAB>...`, or `EXISTS<TAB>...` with the current label (never overwrites)                                         |
| `add column <column> <table or -> <label>` | Same as `add table`; `-` registers a label for every table                                                             |

- A draft candidate marks each fragment without a token as `?<fragment>`; replace it before proposing the label.
- Exit code `0` on success (including `MISSING` and `EXISTS`); exit code `1` with an
  `ERROR<TAB><message>` line on invalid arguments, an invalid identifier or label, or a malformed file.

## Rendering Rules

- 타입:
  - The type as written in the migration, without `without time zone` (e.g. `timestamp`, `serial`,
    `int4`, `decimal(5, 2)`).
  - Letter case follows the existing rows of the same section; for a new section, the section
    immediately before it.
- null 허용: `NOT NULL` or `NULL`; a primary-key column is `NOT NULL`.
- 제약 조건: `PK` (on every column of a composite key), `UNIQUE`, or `FK (<table>.<column>)`; empty
  when none. If a column carries more than one, ask the user how to render it.
- 도메인: always empty.
- 디폴트 값:
  - `NOW()`, `CURRENT_TIMESTAMP`, `@CreatedDate`, `@LastModifiedDate` → `현재 시각`.
  - Booleans → `true` / `false`.
  - String literals → without quotes (e.g. `'v1.0.0'` → `v1.0.0`).
  - Numbers and enum constants → as written (e.g. `0`, `0L`, `USER`, `UNCHECKED`).