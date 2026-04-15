-- 1. content_text 컬럼 생성
ALTER TABLE public.comm_post_archive ADD COLUMN content_text TEXT NULL;

-- 2. fn_update_comm_post_content_text 함수로 content_text 값 갱신
UPDATE public.comm_post_archive SET content_text = fn_update_comm_post_content_text(content);

-- 3. content_text 컬럼 NOT NULL로 변경
ALTER TABLE public.comm_post_archive ALTER COLUMN content_text SET NOT NULL;

-- 4. content 컬럼 삭제
ALTER TABLE public.comm_post_archive DROP COLUMN content;