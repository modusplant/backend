ALTER TABLE public.comm_post ADD COLUMN edited_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE public.comm_post_archive ADD COLUMN edited_at TIMESTAMP WITHOUT TIME ZONE;

-- 마이그레이션
UPDATE public.comm_post
SET edited_at = CASE
                    WHEN published_at IS NULL THEN updated_at
                    WHEN DATE_TRUNC('second', published_at) = DATE_TRUNC('second', updated_at) THEN published_at
                    ELSE updated_at
                END;
UPDATE public.comm_post_archive
SET edited_at = CASE
                    WHEN published_at IS NULL THEN updated_at
                    WHEN DATE_TRUNC('second', published_at) = DATE_TRUNC('second', updated_at) THEN published_at
                    ELSE updated_at
                END;

-- NOT NULL 제약 추가
ALTER TABLE public.comm_post ALTER COLUMN edited_at SET NOT NULL;
ALTER TABLE public.comm_post_archive ALTER COLUMN edited_at SET NOT NULL;