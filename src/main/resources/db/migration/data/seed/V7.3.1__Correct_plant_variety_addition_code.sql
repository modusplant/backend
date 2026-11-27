-- 수동 추가 중분류(접두어 A 포함)에 속한 품종코드에 접두어 A가 중복으로 붙은 문제 수정 (예: AA26G901 → A26G901)
UPDATE public.plant_variety_addition
SET variety_code = SUBSTRING(variety_code FROM 2),
    updated_at   = NOW()
WHERE variety_code LIKE 'AA%';
