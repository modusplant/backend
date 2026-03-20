-- 1. thumbnail 컬럼 추가
ALTER TABLE comm_post ADD COLUMN IF NOT EXISTS thumbnail_path VARCHAR(500);

-- 2. 기존 데이터 마이그레이션
--    content JSONB 배열에서 type = 'image'인 첫 번째 요소의 src를 추출
UPDATE comm_post
SET thumbnail_path = (
    SELECT elem ->> 'src'
    FROM jsonb_array_elements(content) AS elem
    WHERE elem ->> 'type' = 'image'
      AND elem ->> 'src' IS NOT NULL
    ORDER BY (elem ->> 'order')::int ASC
    LIMIT 1
)
WHERE content IS NOT NULL
  AND jsonb_typeof(content) = 'array';