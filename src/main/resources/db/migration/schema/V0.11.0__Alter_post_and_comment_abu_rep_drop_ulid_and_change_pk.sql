BEGIN;

-- 1. comm_post_abu_rep에서 탈퇴한 회원 관련 컬럼 제거
DELETE FROM comm_post_abu_rep
WHERE memb_uuid is null;

-- 2. comm_post_abu_rep에서 중복된 컬럼 제거(첫 번째 레코드만 남김)
DELETE FROM comm_post_abu_rep
WHERE ulid NOT IN (
    SELECT DISTINCT ON (memb_uuid, post_ulid) ulid
    FROM comm_post_abu_rep
    ORDER BY memb_uuid, post_ulid, created_at ASC
);

-- 3. comm_post_abu_rep에서 ulid(PK 컬럼) 제거
ALTER TABLE comm_post_abu_rep DROP COLUMN ulid;

-- 4. comm_post_abu_rep의 새로운 PK로 memb_uuid, post_ulid 지정
ALTER TABLE comm_post_abu_rep ADD PRIMARY KEY (memb_uuid, post_ulid);

-- 5. comm_comment_abu_rep에서 탈퇴한 회원 관련 컬럼 제거
DELETE FROM comm_comment_abu_rep
WHERE memb_uuid is null;

-- 6. comm_comment_abu_rep에서 중복된 컬럼 제거(첫 번째 레코드만 남김)
DELETE FROM comm_comment_abu_rep
WHERE ulid NOT IN (
    SELECT DISTINCT ON (memb_uuid, post_ulid, path) ulid
    FROM comm_comment_abu_rep
    ORDER BY memb_uuid, post_ulid, path, created_at ASC
);

-- 7. comm_comment_abu_rep에서 ulid(PK 컬럼) 제거
ALTER TABLE comm_comment_abu_rep DROP COLUMN ulid;

-- 8. comm_comment_abu_rep의 새로운 PK로 memb_uuid, post_ulid, path 지정
ALTER TABLE comm_comment_abu_rep ADD PRIMARY KEY (memb_uuid, post_ulid, path);

COMMIT;