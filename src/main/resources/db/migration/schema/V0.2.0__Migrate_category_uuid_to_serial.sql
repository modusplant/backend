-- 1) 백업
-- 모든 관련 테이블 백업
CREATE TABLE comm_pri_cate_backup AS SELECT * FROM comm_pri_cate;
CREATE TABLE comm_seco_cate_backup AS SELECT * FROM comm_seco_cate;
CREATE TABLE comm_post_backup AS SELECT * FROM comm_post;
CREATE TABLE comm_post_archive_backup AS SELECT * FROM comm_post_archive;


-- 2) 마이그레이션
BEGIN;

-- UUID -> ID 임시 매핑테이블 생성
CREATE TEMP TABLE pri_cate_mapping AS
       SELECT
           uuid as old_uuid,
           ROW_NUMBER() OVER (ORDER BY "order") as new_id
       FROM comm_pri_cate;

CREATE TEMP TABLE seco_cate_mapping AS
       SELECT
           s.uuid as old_uuid,
           ROW_NUMBER() OVER (ORDER BY p."order", s."order") as new_id
       FROM comm_seco_cate s
           JOIN comm_pri_cate p ON s.pri_cate_uuid = p.uuid;

-- comm_pri_cate 변경
ALTER TABLE comm_pri_cate ADD COLUMN id SERIAL;
UPDATE comm_pri_cate c SET id = m.new_id FROM pri_cate_mapping m WHERE c.uuid = m.old_uuid;

ALTER TABLE comm_pri_cate DROP CONSTRAINT PK_COMM_PRI_CATE;
ALTER TABLE comm_pri_cate ADD PRIMARY KEY (id);
ALTER TABLE comm_pri_cate ALTER COLUMN id SET NOT NULL;

-- comm_seco_cate 번경
ALTER TABLE comm_seco_cate ADD COLUMN id SERIAL;
ALTER TABLE comm_seco_cate ADD COLUMN pri_cate_id INTEGER;
UPDATE comm_seco_cate s SET id = m.new_id FROM seco_cate_mapping m WHERE s.uuid = m.old_uuid;
UPDATE comm_seco_cate s SET pri_cate_id = m.new_id FROM pri_cate_mapping m WHERE s.pri_cate_uuid = m.old_uuid;

ALTER TABLE comm_seco_cate DROP CONSTRAINT PK_COMM_SECO_CATE;
ALTER TABLE comm_seco_cate ADD PRIMARY KEY (id);
ALTER TABLE comm_seco_cate ALTER COLUMN id SET NOT NULL;
ALTER TABLE comm_seco_cate ALTER COLUMN pri_cate_id SET NOT NULL;

-- comm_post 변경
ALTER TABLE comm_post ADD COLUMN pri_cate_id INTEGER;
ALTER TABLE comm_post ADD COLUMN seco_cate_id INTEGER;
UPDATE comm_post p SET pri_cate_id = m.new_id FROM pri_cate_mapping m WHERE p.pri_cate_uuid = m.old_uuid;
UPDATE comm_post p SET seco_cate_id = m.new_id FROM seco_cate_mapping m WHERE p.seco_cate_uuid = m.old_uuid;

ALTER TABLE comm_post ALTER COLUMN pri_cate_id SET NOT NULL;
ALTER TABLE comm_post ALTER COLUMN seco_cate_id SET NOT NULL;

-- comm_post_archive 변경
ALTER TABLE comm_post_archive ADD COLUMN pri_cate_id INTEGER;
ALTER TABLE comm_post_archive ADD COLUMN seco_cate_id INTEGER;
UPDATE comm_post_archive p SET pri_cate_id = m.new_id FROM pri_cate_mapping m WHERE p.pri_cate_uuid = m.old_uuid;
UPDATE comm_post_archive p SET seco_cate_id = m.new_id FROM seco_cate_mapping m WHERE p.seco_cate_uuid = m.old_uuid;

ALTER TABLE comm_post_archive ALTER COLUMN pri_cate_id SET NOT NULL;
ALTER TABLE comm_post_archive ALTER COLUMN seco_cate_id SET NOT NULL;

-- 기존 UUID 컬럼 제거
ALTER TABLE comm_post DROP COLUMN pri_cate_uuid;
ALTER TABLE comm_post DROP COLUMN seco_cate_uuid;
ALTER TABLE comm_post_archive DROP COLUMN pri_cate_uuid;
ALTER TABLE comm_post_archive DROP COLUMN seco_cate_uuid;
ALTER TABLE comm_seco_cate DROP COLUMN pri_cate_uuid;
ALTER TABLE comm_seco_cate DROP COLUMN uuid;
ALTER TABLE comm_pri_cate DROP COLUMN uuid;

COMMIT;


-- 3) 백업 테이블 삭제
DROP TABLE comm_pri_cate_backup;
DROP TABLE comm_seco_cate_backup;
DROP TABLE comm_post_backup;
DROP TABLE comm_post_archive_backup;