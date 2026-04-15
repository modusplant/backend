-- 1. archived_at 컬럼 생성
ALTER TABLE public.comm_post_archive ADD COLUMN archived_at timestamp without time zone NULL;

-- 2. updated_at 값으로 archived_at 값 갱신(편의상, 엄밀하게는 의미상 맞지 않음)
UPDATE public.comm_post_archive SET archived_at = updated_at;

-- 3. archived_at 컬럼 NOT NULL로 변경
ALTER TABLE public.comm_post_archive ALTER COLUMN archived_at SET NOT NULL;