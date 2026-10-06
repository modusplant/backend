# Naming Convention — `테이블 정의서`

The ubiquitous language of the Confluence `테이블 정의서` pages, used to draft Korean labels and to
render derived facts. A label found in the Glossary is used as-is; a label built from the Token
and Pattern tables is a draft and needs user approval.

## Glossary — Tables

| Table                                 | Korean label     |
|---------------------------------------|------------------|
| `site_member`                         | 사이트 회원           |
| `site_member_auth`                    | 사이트 회원 인증        |
| `site_member_term`                    | 사이트 회원 약관        |
| `site_member_prof`                    | 사이트 회원 프로필       |
| `site_member_withdraw`                | 사이트 회원 탈퇴        |
| `comm_pri_cate`                       | 소통 1차 항목         |
| `comm_seco_cate`                      | 소통 2차 항목         |
| `comm_post`                           | 소통 게시글           |
| `comm_post_archive`                   | 소통 게시글 아카이브      |
| `comm_post_like`                      | 소통 게시글 좋아요       |
| `comm_post_bookmark`                  | 소통 게시글 북마크       |
| `comm_post_abu_rep`                   | 소통 게시글 신고        |
| `comm_post_abuse_report_dashboard`    | 게시글 신고 대시보드      |
| `comm_comment_abuse_report_dashboard` | 댓글 신고 대시보드       |
| `plant`                               | 식물               |
| `plant_space`                         | 식물 공간 환경 정보      |
| `plant_info`                          | 식물 정보            |
| `plant_info_photo`                    | 식물 정보 사진         |
| `term`                                | 약관               |
| `refresh_token`                       | 리프레시 토큰          |
| `fcm_token`                           | FCM 토큰           |
| `comm_comment`                        | 소통 댓글            |
| `comm_comment_like`                   | 소통 댓글 좋아요        |
| `comm_comment_abu_rep`                | 소통 댓글 신고         |
| `swear`                               | 비속어              |
| `prop_bug_rep`                        | 건의 및 버그 제보       |
| `prop_bug_rep_archive`                | 건의 및 버그 제보 아카이브  |
| `comm_notification`                   | 소통 컨텐츠 알림        |
| `common_code_group`                   | 공통 코드 그룹         |
| `common_code`                         | 공통 코드            |
| `pending_file`                        | 지연 중 파일          |

## Glossary — Columns

A column with a `Table` value uses that label only in that table; otherwise the label applies to
every table.

| Column                    | Table                  | Korean label        |
|---------------------------|------------------------|---------------------|
| `id`, `uuid`, `ulid`      |                        | 식별자                 |
| `post_ulid`               | `comm_notification`    | 연관 게시글 식별자          |
| `post_ulid`               |                        | 게시글 식별자             |
| `memb_uuid`               |                        | 회원 식별자              |
| `member_uuid`             |                        | 회원 식별자              |
| `auth_memb_uuid`          |                        | 인가 회원 식별자           |
| `pri_cate_id`             |                        | 1차 항목 식별자           |
| `pri_cate_uuid`           |                        | 1차 항목 식별자           |
| `seco_cate_id`            |                        | 2차 항목 식별자           |
| `path`                    |                        | 구체화된 경로             |
| `created_at`              |                        | 생성 시점               |
| `updated_at`              |                        | 마지막 수정 시점           |
| `last_modified_at`        |                        | 마지막 변경 시점           |
| `edited_at`               |                        | 마지막 편집 시점           |
| `published_at`            |                        | 발행 시점               |
| `archived_at`             |                        | 아카이빙 시점             |
| `checked_at`              |                        | 확인 시점               |
| `withdrawn_at`            |                        | 탈퇴 시점               |
| `expired_at`              |                        | 만료 시점               |
| `logged_in_at`            |                        | 마지막 로그인 시점          |
| `first_reported_at`       |                        | 최초 신고 시점            |
| `last_reported_at`        |                        | 마지막 신고 시점           |
| `ver_num`                 |                        | 버전 번호               |
| `ver`                     |                        | 버전                  |
| `like_count`              |                        | 좋아요 수               |
| `view_count`              |                        | 조회 수                |
| `report_count`            |                        | 신고 횟수               |
| `title`                   |                        | 제목                  |
| `content`                 |                        | 본문                  |
| `content_text`            |                        | 본문 텍스트              |
| `content_preview`         |                        | 컨텐츠 미리보기            |
| `thumbnail_path`          |                        | 대표사진 경로             |
| `image_path`              |                        | 이미지 경로              |
| `image`                   |                        | 이미지                 |
| `image_number`            |                        | 이미지 개수              |
| `file_path`               |                        | 파일 경로               |
| `file_key`                |                        | 파일 키                |
| `is_published`            |                        | 발행 여부               |
| `is_active`               |                        | 활성화 여부              |
| `is_banned`               |                        | 밴 여부                |
| `is_deleted`              |                        | 삭제 여부               |
| `is_default`              | `plant_space`          | 기본 공간 여부            |
| `is_plant_light`          |                        | 식물등 사용 여부           |
| `category`                |                        | 항목                  |
| `order`                   |                        | 순서                  |
| `sort_order`              |                        | 정렬 순서               |
| `status`                  | `comm_notification`    | 알림 상태               |
| `status`                  |                        | 상태                  |
| `nickname`                |                        | 닉네임                 |
| `role`                    |                        | 역할                  |
| `email`                   |                        | 이메일                 |
| `pw`                      |                        | 비밀번호                |
| `provider`                |                        | 제공자                 |
| `provider_id`             |                        | 제공자 아이디             |
| `agreed_tou_ver`          |                        | 동의한 서비스 이용약관 버전     |
| `agreed_priv_poli_ver`    |                        | 동의한 개인정보 수집 및 이용 버전 |
| `agreed_comm_poli_ver`    |                        | 동의한 커뮤니티 운영정책 버전    |
| `intro`                   |                        | 소개                  |
| `reason`                  | `site_member_withdraw` | 탈퇴 사유               |
| `opinion`                 |                        | 의견                  |
| `name`                    |                        | 이름                  |
| `refresh_token`           |                        | 리프레시 토큰             |
| `token`                   |                        | 토큰                  |
| `platform`                |                        | 플랫폼                 |
| `word`                    |                        | 단어                  |
| `type`                    | `swear`                | 종류                  |
| `actor_id`                |                        | 알림 행위자 식별자          |
| `recipient_id`            |                        | 알림 수신자 식별자          |
| `comment_path`            |                        | 연관 댓글 경로            |
| `actor_nickname`          |                        | 연관 행위자 닉네임          |
| `action`                  |                        | 알림 활동               |
| `group_code`              |                        | 그룹 코드               |
| `group_name`              |                        | 그룹 이름               |
| `description`             | `common_code_group`    | 그룹 서술               |
| `description`             | `plant_info`           | 식물 서술               |
| `code`                    |                        | 코드                  |
| `label`                   |                        | 라벨                  |
| `domain`                  |                        | 도메인                 |
| `scientific_name_id`      |                        | 학명 식별자              |
| `scientific_name`         |                        | 학명                  |
| `korean_name`             |                        | 국명                  |
| `family_scientific_name`  |                        | 과 학명                |
| `family_korean_name`      |                        | 과 국명                |
| `genus_scientific_name`   |                        | 속 학명                |
| `genus_korean_name`       |                        | 속 국명                |
| `species_name`            |                        | 종소명                 |
| `subspecies_name`         |                        | 종하명                 |
| `resource_type`           |                        | 자원 유형               |
| `space_location`          |                        | 공간 위치 코드            |
| `space_location_custom`   |                        | 공간 위치 직접 입력값        |
| `light_intensity`         |                        | 빛의 세기               |
| `light_direction`         |                        | 빛의 방향 코드            |
| `light_direction_custom`  |                        | 빛의 방향 직접 입력값        |
| `ventilation_type`        |                        | 환기 방식 코드            |
| `ventilation_type_custom` |                        | 환기 방식 직접 입력값        |
| `humidity`                |                        | 습도                  |
| `plant_name`              |                        | 식물 이름               |
| `plant_id`                |                        | 식물 식별자              |
| `space_id`                |                        | 공간 식별자              |
| `plant_info_id`           |                        | 식물 정보 식별자           |
| `watering_cycle_days`     |                        | 물주기 일수              |
| `grown_since`             |                        | 식물 키우기 일자           |

## Tokens

Name fragments and their Korean equivalents, for drafting labels not in the Glossary.

| Token                        | Korean      |
|------------------------------|-------------|
| `site_member`                | 사이트 회원      |
| `memb`, `member`             | 회원          |
| `comm`                       | 소통          |
| `post`                       | 게시글         |
| `comment`                    | 댓글          |
| `pri_cate`                   | 1차 항목       |
| `seco_cate`                  | 2차 항목       |
| `archive`                    | 아카이브        |
| `like`                       | 좋아요         |
| `bookmark`                   | 북마크         |
| `abu_rep`, `abuse_report`    | 신고          |
| `dashboard`                  | 대시보드        |
| `prop_bug_rep`               | 건의 및 버그 제보  |
| `prof`                       | 프로필         |
| `withdraw`                   | 탈퇴          |
| `term`                       | 약관          |
| `notification`               | 알림          |
| `plant`                      | 식물          |
| `info`                       | 정보          |
| `photo`                      | 사진          |
| `space`                      | 공간          |
| `code`                       | 코드          |
| `group`                      | 그룹          |
| `file`                       | 파일          |
| `token`                      | 토큰          |
| `auth` (table name)          | 인증          |
| `auth` (column name)         | 인가          |

## Patterns

| Column pattern                   | Korean pattern |
|----------------------------------|----------------|
| `<x>_id`, `<x>_uuid`, `<x>_ulid` | `<x> 식별자`      |
| `is_<x>`                         | `<x> 여부`       |
| `<x>_count`                      | `<x> 수`        |
| `<x>_at`                         | `<x> 시점`       |
| `<x>_custom`                     | `<x> 직접 입력값`   |
| `<x>_code`                       | `<x> 코드`       |
| `<x>_path`                       | `<x> 경로`       |

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