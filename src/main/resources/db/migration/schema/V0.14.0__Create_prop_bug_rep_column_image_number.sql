-- 1. image_number 컬럼 추가
ALTER TABLE public.prop_bug_rep
ADD COLUMN image_number INTEGER;

-- 2. image_path 값에 따라 image_number 컬럼 업데이트
UPDATE public.prop_bug_rep
SET image_number = CASE
    WHEN image_path IS NULL THEN NULL
    ELSE 1
END;