---
paths:
  - "src/main/resources/csv/**"
  - "src/main/java/db/migration/data/reference/V5_0_1__Insert_plant_table_default_data.java"
  - "src/main/java/db/migration/data/reference/V5_2_1__Insert_plant_variety_table_default_data.java"
  - "src/main/resources/db/migration/schema/V0.17.0__Create_plant_table.sql"
  - "src/main/resources/db/migration/schema/V4.0.0__Alter_kor_name_to_korean_name.sql"
  - "src/main/resources/db/migration/schema/V5.0.0__Recreate_plant_table.sql"
  - "src/main/resources/db/migration/schema/V5.0.2__Create_index_on_plant_korean_name.sql"
  - "src/main/resources/db/migration/schema/V5.0.3__Alter_add_created_at_and_updated_at_to_plant.sql"
  - "src/main/resources/db/migration/schema/V5.2.0__Create_plant_variety_table.sql"
  - "src/main/resources/db/migration/schema/V5.2.2__Alter_add_created_at_and_updated_at_to_plant_variety.sql"
  - "src/main/resources/db/migration/schema/V7.1.0__Create_plant_variety_category_tables.sql"
  - "src/main/resources/db/migration/schema/V7.2.0__Create_plant_variety_addition_table.sql"
  - "src/main/resources/db/migration/data/seed/V7.1.1__Refine_plant_name.sql"
  - "src/main/resources/db/migration/data/seed/V7.1.2__Correct_mismatched_plant_name.sql"
  - "src/main/resources/db/migration/data/seed/V7.1.3__Unify_plant_name_spelling.sql"
  - "src/main/resources/db/migration/data/seed/V7.1.4__Delete_duplicate_plant.sql"
  - "src/main/resources/db/migration/data/seed/V7.2.1__Insert_plant_variety_addition.sql"
  - "src/main/resources/db/migration/data/seed/V7.2.2__Correct_plant_variety_addition_code.sql"
  - "src/main/java/kr/modusplant/infrastructure/config/cache/CacheConfig.java"
---

# Plant Dictionary Data Conventions

Policy that defines where the plant-name dictionary comes from, how its data is refined, how its tables relate, and how
new plant names and corrections are added.

Applies to the `plant`, `plant_variety`, `plant_variety_middle_category`, `plant_variety_major_category`, and
`plant_variety_addition` tables, the CSVs under `src/main/resources/csv/`, and every Flyway migration that writes to
them.

---

## 1. Source Data

| CSV | Contents (header) | Loaded into | Loader |
|---|---|---|---|
| `plant_classification_system_name_001.csv` | scientific-name ID, scientific name, Korean name, family/genus scientific and Korean names, species, subspecies, resource type (e.g. `자생종`) | `plant` | `V5_0_1__Insert_plant_table_default_data` |
| `standard_item_code_001.csv` | standard item codes: `대분류명`, `중분류코드`, `중분류명(품목명)`, `소분류코드`, `소분류명(품종명)` | `plant_variety` | `V5_2_1__Insert_plant_variety_table_default_data` |

- Both loaders are Java migrations under `src/main/java/db/migration/data/reference/`; they run only at application
  startup, not in the Gradle Flyway run that precedes jOOQ codegen.
- `plant_variety_addition` has no CSV source; its rows are handwritten in `data/seed/` SQL migrations.

---

## 2. Refinement

### 2.1 CSV Pre-processing

Already applied to `standard_item_code_001.csv` itself; redo all of it on any replacement CSV:
- Delete every record whose major category is `농림가공` or `농산물종자류`.
- Remove every `(수입)` from variety names.
- Correct middle category `2347`'s name `암` to `얌`.
- Delete every record of middle category `해바라기씨` (`1631`), since it is a seed.

Every other CSV value stays as the original; code normalization happens at load time.

### 2.2 Load-Time Refinement of `plant_variety` (V5.2.1)

1. Restore codes broken by Excel: scientific notation (e.g. `2.40E+06` → `2403E3`) and lost leading zeros (middle codes
   to 4 characters, variety codes to 6).
2. Fix a variety code whose first 4 characters differ from its middle code, keeping whichever side is valid.
3. Strip parenthesized text from variety names (not from middle category names).
4. Exclude records that cannot work as a plant name: catch-alls (`기타…`, `…혼합…`), non-plants (feed, `캔들…`, `…부작…`),
   processed goods (dried, `…말랭이…`, `…수액`, `…종자`, listed processed names and middle categories), and names with no
   Hangul or a single syllable that is neither the middle category name nor a listed real plant name.
5. Make middle code and middle name one-to-one:
   - One name with several codes: keep the code with the most records and drop the others' records.
   - One code with several names: unify names for the same plant, give a different plant an independent code (the next
     unused number in its major category's code band, also replacing the variety code's prefix), and split the catch-all
     `기타` into `기타(<major category name>)`.
6. For duplicate variety names, keep the record with the lexicographically smallest variety code.

`plant` is loaded from its CSV without refinement; each loaded row's `id` is its 0-based CSV row index, and rows
inserted later take the next `id` after the current maximum.

### 2.3 Category Tables (V7.1.0)

Middle and major categories live in their own tables, derived from the refined `plant_variety`: middle categories keep
its codes, and major categories, which had none, have two-digit codes in category-name order. `plant_variety` holds only
the two codes.

### 2.4 Name Refinement for Search Accuracy (V7.1.1–V7.1.4)

- Delete rows whose name is invalid as a plant name: broken HTML entities, names unrelated to their scientific name, and
  timber trade names, misspellings, or fragments that cannot be matched to a plant.
- Remove quotes, `®`, `?`, `&nbsp;`, surrounding whitespace, and the source markers `(중)`, `(북)`, `(븍)`; keep
  parentheses that tell same-named plants apart.
- Split a comma-joined Korean name into one row per name.
- Correct a Korean name attached to the wrong scientific name: move it onto the matching scientific name's taxonomy, or
  delete the row when no single matching scientific name exists.
- Unify transliterated spellings: the 표준국어대사전 headword first, then the genus Korean name's spelling, then the
  spelling used most across the records. When unification changes a record's most-used spelling, add a row with the old
  spelling so it stays searchable.
- Delete duplicate `plant` rows identical in every column except `id` and timestamps, keeping the smallest `id`.

---

## 3. Relationships and Conventions

- `plant` stands alone and references no other table; one `scientific_name_id` can carry several Korean names, one row
  each.
- `plant_variety` and `plant_variety_addition` share one schema and reference their middle and major categories by
  code, without foreign-key constraints.
- Codes:

| Code | Shape |
|---|---|
| major category (derived) | two digits |
| middle category (CSV) | four characters, each major category using its own band (e.g. `10xx`) |
| variety (CSV) | middle category code + two-digit serial |
| manually added major category | `A` + two digits (e.g. `A00`) |
| manually added middle category | `A` + four characters shaped as a CSV middle category code (e.g. `A0000`) |
| manually added variety | under a CSV middle category: `A` + middle category code + two-digit serial; under an added middle category: its code (already prefixed with `A`) + two-digit serial |

- Variety names are unique within `plant_variety` and within `plant_variety_addition`, and category names within their
  table; `plant.korean_name` is not unique and may also appear in `plant_variety`.
- Category `order` values are unique, and a new category takes the next value after the current maximum.
- Plant-name search reads the distinct union of `plant.korean_name`, `plant_variety.variety_name`, and
  `plant_variety_addition.variety_name`, loaded on first use into a single-entry Caffeine cache by
  `infrastructure/config/cache/CacheConfig.java`.

---

## 4. Data Addition Guide

- Write every change as a new migration under `src/main/resources/db/migration/data/seed/`; never edit an applied
  migration or the CSVs to change data.
- Add the exact path of every new migration or source file that touches these tables to this document's `paths`
  frontmatter, since the frontmatter lists files individually instead of using broad globs.
- Add a new plant name to `plant_variety_addition`, not to `plant` or `plant_variety`, only when it duplicates no name
  in the three name columns; filter duplicates in the `INSERT` itself.
- Resolve a variety's middle and major categories by name; a name matching no category drops the row without an error.
  Add a middle category only when no existing name matches exactly:
  - Name: the genus Korean name in 국가표준식물목록 (https://www.nature.go.kr/kpni) without `속`.
  - Code: `A` + the next number its major category's band does not yet use, among both CSV and added codes.
- Number a new variety's serial after the largest serial already used in its middle category.
- Correct an existing `plant` or `plant_variety` row in a new migration that:
  - targets rows by name, adding the scientific name for `plant`;
  - sets `updated_at`;
  - states each correction's reason in a Korean comment.
- Seed SQL must also succeed when the plant tables are empty, because the Gradle Flyway run executes it without the
  Java loaders.
- New names reach search after the application restarts and reloads the cache. The whole distinct name list is one
  cache entry weighted by its size, so raise the cache's `maximumWeight` whenever the distinct name count grows
  toward it.