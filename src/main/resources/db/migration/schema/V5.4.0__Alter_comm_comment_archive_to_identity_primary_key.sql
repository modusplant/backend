-- 1. (post_ulid, path) 기반 기본 키 제거 (동일한 게시글에서 회원 탈퇴로 아카이빙된 댓글의 경로가 재발급될 수 있기 때문)
ALTER TABLE public.comm_comment_archive DROP CONSTRAINT "PK_COMM_COMMENT_ARCHIVE";

-- 2. 식별자 컬럼 추가 및 기본 키로 지정
ALTER TABLE public.comm_comment_archive ADD COLUMN id BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY;

ALTER TABLE ONLY public.comm_comment_archive
    ADD CONSTRAINT "PK_COMM_COMMENT_ARCHIVE" PRIMARY KEY (id);

-- 3. (post_ulid, path) 조회용 Non-Unique 인덱스 생성
CREATE INDEX idx_comm_comment_archive_post_ulid_path
    ON public.comm_comment_archive (post_ulid, "path");
