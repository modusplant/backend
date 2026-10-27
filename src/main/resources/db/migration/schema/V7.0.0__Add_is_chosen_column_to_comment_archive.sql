-- 1. 디폴트 값 없이 nullable 컬럼으로 추가
ALTER TABLE public.comm_comment_archive
ADD COLUMN is_chosen boolean;

-- 2. 기존 레코드의 채택 여부를 false로 설정
UPDATE public.comm_comment_archive
SET is_chosen = false;

-- 3. NOT NULL 제약 조건 추가
ALTER TABLE public.comm_comment_archive
ALTER COLUMN is_chosen SET NOT NULL;