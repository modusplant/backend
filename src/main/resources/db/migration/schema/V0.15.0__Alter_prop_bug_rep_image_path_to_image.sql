-- 1. image_path 컬럼을 image로 재명명
ALTER TABLE public.prop_bug_rep
RENAME COLUMN image_path TO image;

-- 2. image를 형식에 맞춰 JSONB 타입으로 변경
ALTER TABLE public.prop_bug_rep
ALTER COLUMN image TYPE JSONB
USING CASE
    WHEN image IS NULL THEN NULL
    ELSE jsonb_build_array(
        jsonb_build_object(
            'filename', 'image_1.png',
            'src', image
        )
    )
END;