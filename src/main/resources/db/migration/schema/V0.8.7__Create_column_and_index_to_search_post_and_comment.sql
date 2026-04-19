-- 1. comm_post에 content 데이터 중 text data만 따로 보관하기 위한 content_text 갱신용 함수 생성
CREATE OR REPLACE FUNCTION fn_update_comm_post_content_text(jsonb_data jsonb)
RETURNS TEXT AS $$
    SELECT string_agg(elem->>'data', ' ')
    FROM jsonb_array_elements(jsonb_data) AS elem
    WHERE elem->>'type' = 'text';
$$ LANGUAGE sql IMMUTABLE;

-- 2. Generated Column으로 content_text 컬럼 생성
ALTER TABLE public.comm_post
    ADD COLUMN content_text TEXT
    GENERATED ALWAYS AS (fn_update_comm_post_content_text(content)) STORED;

-- 3. content_text에 인덱스 부여
CREATE INDEX idx_comm_post_content_text_trgm
    ON public.comm_post
    USING GIN (content_text gin_trgm_ops);

-- 4. comm_comment의 content에 인덱스 부여
CREATE INDEX idx_comm_comment_content_trgm
    ON public.comm_comment
    USING GIN (content gin_trgm_ops);