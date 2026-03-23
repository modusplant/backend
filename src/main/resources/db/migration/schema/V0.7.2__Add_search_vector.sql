-- 1. pg_trgm 확장 활성화 (RDS 지원)
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 2. search_vector 컬럼 추가
ALTER TABLE comm_post
    ADD COLUMN IF NOT EXISTS search_vector tsvector;

-- 3. 기존 데이터 search_vector 일괄 업데이트
UPDATE comm_post
SET search_vector =
        setweight(to_tsvector('simple', coalesce(title, '')), 'A') ||
        setweight(
                to_tsvector('simple',
                            coalesce(
                                    (SELECT string_agg(elem ->> 'data', ' ')
                                     FROM jsonb_array_elements(content) AS elem
                                     WHERE elem ->> 'type' = 'text'),
                                    ''
                            )
                ),
                'B'
        );

-- 4. GIN 인덱스 (tsvector 검색용)
CREATE INDEX IF NOT EXISTS idx_comm_post_search_vector
    ON comm_post USING GIN (search_vector);

-- 5. GIN 인덱스 (pg_trgm 부분일치용)
CREATE INDEX IF NOT EXISTS idx_comm_post_title_trgm
    ON comm_post USING GIN (title gin_trgm_ops);

-- 6. 자동 업데이트 트리거 함수
CREATE OR REPLACE FUNCTION fn_update_comm_post_search_vector()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.search_vector :=
            setweight(to_tsvector('simple', coalesce(NEW.title, '')), 'A') ||
            setweight(
                    to_tsvector('simple',
                                coalesce(
                                        (SELECT string_agg(elem ->> 'data', ' ')
                                         FROM jsonb_array_elements(NEW.content) AS elem
                                         WHERE elem ->> 'type' = 'text'),
                                        ''
                                )
                    ),
                    'B'
            );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 7. 트리거 등록
CREATE OR REPLACE TRIGGER trg_comm_post_search_vector
    BEFORE INSERT OR UPDATE OF title, content
    ON comm_post
    FOR EACH ROW
EXECUTE FUNCTION fn_update_comm_post_search_vector();
