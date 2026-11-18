-- 모두의기록 — 사용자가 등록한 짧은 식물 영상 기록
CREATE TABLE public.plant_record (
    ulid              VARCHAR(26)                 NOT NULL,
    auth_memb_uuid    uuid                        NOT NULL,
    description       VARCHAR(60),
    original_filename VARCHAR(255)                NOT NULL,
    video_key         VARCHAR(512)                NOT NULL,
    thumbnail_key     VARCHAR(512)                NOT NULL,
    content_type      VARCHAR(100)                NOT NULL,
    size_bytes        BIGINT                      NOT NULL,
    is_published      VARCHAR(20)                 NOT NULL,
    created_at        timestamp without time zone NOT NULL,
    updated_at        timestamp without time zone NOT NULL,
    CONSTRAINT "PK_PLANT_RECORD" PRIMARY KEY (ulid)
);

ALTER TABLE public.plant_record
    ADD CONSTRAINT "CHK_PLANT_RECORD_IS_PUBLISHED"
    CHECK (is_published IN ('PUBLISHED', 'HIDDEN', 'BLOCKED', 'DELETED'));

ALTER TABLE public.plant_record
    ADD CONSTRAINT "CHK_PLANT_RECORD_SIZE_BYTES"
    CHECK (size_bytes > 0);

COMMENT ON TABLE  public.plant_record                   IS '모두의기록 — 사용자가 등록한 짧은 식물 영상 기록';
COMMENT ON COLUMN public.plant_record.ulid              IS '기록 식별자';
COMMENT ON COLUMN public.plant_record.auth_memb_uuid    IS '작성자 회원 UUID. site_member PK 참조';
COMMENT ON COLUMN public.plant_record.description       IS '선택 설명 (최대 60자). 미입력·빈 값은 NULL로 통일';
COMMENT ON COLUMN public.plant_record.original_filename IS '클라이언트가 업로드한 편집 완료 영상의 파일명. 표시·문의 대응용이며 저장 경로 생성에 사용하지 않음';
COMMENT ON COLUMN public.plant_record.video_key         IS '최종 저장 영상의 스토리지 키. 재생 URL은 조회 시 이 키로 생성';
COMMENT ON COLUMN public.plant_record.thumbnail_key     IS '서버가 실제 영상에서 추출·저장한 썸네일의 스토리지 키. 목록 그리드에 사용';
COMMENT ON COLUMN public.plant_record.content_type      IS '실제 검사한 최종 영상 MIME 타입 (예: video/mp4). 클라이언트 요청값을 신뢰하지 않고 검증 결과를 저장';
COMMENT ON COLUMN public.plant_record.size_bytes        IS '최종 영상의 실제 바이트 크기. 썸네일 제외';
COMMENT ON COLUMN public.plant_record.is_published      IS '노출 상태. PUBLISHED: 공개, HIDDEN: 작성자 숨김, BLOCKED: 신고로 인한 비공개, DELETED: 논리 삭제. 공개 목록·상세는 PUBLISHED 조건만 반환. deleted_at 컬럼 없이 이 상태로 삭제 관리';
COMMENT ON COLUMN public.plant_record.created_at        IS '생성일';
COMMENT ON COLUMN public.plant_record.updated_at        IS '수정일';

CREATE INDEX idx_plant_record_latest
    ON public.plant_record (created_at DESC, ulid DESC);

CREATE INDEX idx_plant_record_auth_memb_uuid
    ON public.plant_record (auth_memb_uuid);

CREATE INDEX idx_plant_record_updated
    ON public.plant_record (updated_at DESC, ulid DESC);
