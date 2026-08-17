-- 1. 기존 데이터의 image_number 값을 image 배열 길이에 맞춰 재계산
UPDATE public.prop_bug_rep
SET image_number = CASE
    WHEN image IS NULL THEN NULL
    ELSE jsonb_array_length(image)
END;

-- 2. image 변경 시 image_number를 자동으로 갱신하는 트리거 함수
CREATE OR REPLACE FUNCTION fn_update_prop_bug_rep_image_number()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.image_number := CASE
        WHEN NEW.image IS NULL THEN NULL
        ELSE jsonb_array_length(NEW.image)
    END;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. 트리거 등록
CREATE OR REPLACE TRIGGER trg_prop_bug_rep_image_number
    BEFORE INSERT OR UPDATE OF image
    ON public.prop_bug_rep
    FOR EACH ROW
EXECUTE FUNCTION fn_update_prop_bug_rep_image_number();
