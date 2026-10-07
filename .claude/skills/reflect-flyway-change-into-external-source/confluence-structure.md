# Confluence Structure — `테이블 정의서`

## Location

- Site: `https://modusplant-project.atlassian.net`
- cloudId: `a9962cac-b27a-410d-9cee-0268a2bae694`
- Space key: `MODUSPLANT`
- Folder: `테이블 정의서` (folder id `819470`; a folder, not a page — list its pages with CQL
  `ancestor = 819470`, not with `getConfluencePageDescendants`)

## Pages

| page-slug               | Page title         | Page id    | URL                                                                      |
|-------------------------|--------------------|------------|--------------------------------------------------------------------------|
| `member`                | `테이블 정의서(회원)`      | `819373`   | `https://modusplant-project.atlassian.net/wiki/spaces/mp/pages/819373`   |
| `post`                  | `테이블 정의서(게시글)`     | `819806`   | `https://modusplant-project.atlassian.net/wiki/spaces/mp/pages/819806`   |
| `admin-activity`        | `테이블 정의서(관리자 활동)`  | `819471`   | `https://modusplant-project.atlassian.net/wiki/spaces/mp/pages/819471`   |
| `environment-and-plant` | `테이블 정의서(환경 및 식물)` | `819650`   | `https://modusplant-project.atlassian.net/wiki/spaces/mp/pages/819650`   |
| `user-activity`         | `테이블 정의서(유저 활동)`   | `24870913` | `https://modusplant-project.atlassian.net/wiki/spaces/mp/pages/24870913` |
| `server-function`       | `테이블 정의서(서버 기능)`   | `819225`   | `https://modusplant-project.atlassian.net/wiki/spaces/mp/pages/819225`   |
| `policy-implementation` | `테이블 정의서(정책 구현)`   | `24051715` | `https://modusplant-project.atlassian.net/wiki/spaces/mp/pages/24051715` |

If a page id stops resolving, re-locate the pages with CQL `ancestor = 819470` and report the
mismatch instead of guessing.

## Table Index

Tables in page order, as they currently appear on each page.

| page-slug               | Tables                                                                                                                           |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `member`                | `site_member`, `site_member_auth`, `site_member_term`, `site_member_prof`, `site_member_withdraw`                                |
| `post`                  | `comm_pri_cate`, `comm_seco_cate`, `comm_post`, `comm_post_archive`, `comm_post_like`, `comm_post_bookmark`, `comm_post_abu_rep` |
| `admin-activity`        | `comm_post_abuse_report_dashboard`, `comm_comment_abuse_report_dashboard`                                                        |
| `environment-and-plant` | `plant`, `plant_variety`, `plant_space`, `plant_info`, `plant_info_photo`                                                        |
| `user-activity`         | `comm_comment`, `comm_comment_like`, `comm_comment_abu_rep`, `prop_bug_rep`                                                      |
| `server-function`       | `refresh_token`, `fcm_token`, `comm_notification`, `pending_file`                                                                |
| `policy-implementation` | `term`, `swear`, `prop_bug_rep_archive`, `common_code_group`, `common_code`                                                      |

## Prefix Map

For a table absent from the Table Index. Evaluate top to bottom; the first match wins. If no row
matches, ask the user.

Pages fall into two kinds, which the rows and any page proposed to the user follow:

- Domain pages (`member`, `post`, `environment-and-plant`): a domain's tables belong here, including
  auxiliary tables such as archives (e.g. `comm_post_archive`), unless the table serves a role page.
  Judge domain membership qualitatively, chiefly by table naming, not by which code references the
  table: other domains may reference these domains' entities and repositories.
- Role pages (`admin-activity`, `user-activity`, `server-function`, `policy-implementation`): a
  table belongs to the page matching its own role, so tables of one domain may sit on different
  pages (e.g. `prop_bug_rep` on `user-activity`, `prop_bug_rep_archive` on `policy-implementation`).
  A role page takes precedence over a domain page. `admin-activity` holds tables used only by
  admin-only features (e.g. `comm_post_abuse_report_dashboard`); identify them in the code by
  endpoint URLs containing `admin` and by entity and repository usage confined to that feature.

| Table name pattern                                                    | page-slug               |
|-----------------------------------------------------------------------|-------------------------|
| `*_dashboard`                                                         | `admin-activity`        |
| `site_member*`                                                        | `member`                |
| `plant*`                                                              | `environment-and-plant` |
| `comm_post*`, `comm_pri_cate*`, `comm_seco_cate*`                     | `post`                  |
| `prop_bug_rep_archive*`, `term*`, `swear*`, `common_code*`            | `policy-implementation` |
| `comm_comment*`, `prop_bug_rep*`                                      | `user-activity`         |
| `comm_notification*`, `refresh_token*`, `fcm_token*`, `pending_file*` | `server-function`       |

## Section Skeleton

One section per table, in this order (markdown view of the page; edit through the `html` view):

```markdown
# <Korean table label>(<table_name>)

|  |  |  |  |  |  |
| --- | --- | --- | --- | --- | --- |
| 한글명 | 영문명 | 타입 | null 허용 | 제약 조건 | 도메인 |
| <한글명> | <column> | <type> | <NOT NULL or NULL> | <constraint or empty> |  |

## 디폴트 처리되는 컬럼

| 컬럼명 | 디폴트 값 |
| --- | --- |
| <column> | <default> |

## 세부 사항

| 한글명 | 영문명 | 설명 |
| --- | --- | --- |

---
```

- The main table's header row is empty; the `한글명 | 영문명 | ...` label row is its first body row.
- Column rows follow the derived column order.
- `## 디폴트 처리되는 컬럼` is present only when at least one column has a default; its rows follow
  the main table's column order.
- `## 세부 사항` is never created or edited by this skill.
- Each section ends with a horizontal rule, except where the page's existing last section has none;
  keep that page's ending as it is.

## Placement Rules

- A new table's section goes right after the section of its related table on the same page:
  1. the table on that page whose name is the longest prefix of the new table's name
     (e.g. `plant_variety` → `plant`);
  2. otherwise, the table its foreign key references, if that table is on the same page.
- With no related table, append the section at the end of the page.
- Headings (`#` and `##`) and table header cells are plain text, never bold.
