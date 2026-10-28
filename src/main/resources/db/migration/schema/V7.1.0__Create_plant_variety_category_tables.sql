-- plant_variety의 중분류와 대분류를 별도 테이블로 분리
CREATE TABLE public.plant_variety_middle_category (
    category_code VARCHAR(12) PRIMARY KEY,
    category_name VARCHAR(24) NOT NULL UNIQUE,
    "order"       INTEGER     NOT NULL UNIQUE,
    created_at    timestamp without time zone NOT NULL
);

CREATE TABLE public.plant_variety_major_category (
    category_code VARCHAR(12) PRIMARY KEY,
    category_name VARCHAR(24) NOT NULL UNIQUE,
    "order"       INTEGER     NOT NULL UNIQUE,
    created_at    timestamp without time zone NOT NULL
);

-- 중분류: 기존 중분류코드와 중분류명을 그대로 추출
INSERT INTO public.plant_variety_middle_category (category_code, category_name, "order", created_at)
SELECT middle_category_code,
       middle_category_name,
       ROW_NUMBER() OVER (ORDER BY middle_category_code),
       NOW()
FROM (SELECT DISTINCT middle_category_code, middle_category_name FROM public.plant_variety) AS middle_category;

-- 대분류: 기존 대분류코드가 없으므로 대분류명 순으로 두 자리 대분류코드를 부여
INSERT INTO public.plant_variety_major_category (category_code, category_name, "order", created_at)
SELECT LPAD(ROW_NUMBER() OVER (ORDER BY major_category_name)::TEXT, 2, '0'),
       major_category_name,
       ROW_NUMBER() OVER (ORDER BY major_category_name),
       NOW()
FROM (SELECT DISTINCT major_category_name FROM public.plant_variety) AS major_category;

-- plant_variety: 대분류명을 대분류코드로 대체하고 중분류명 제거
ALTER TABLE public.plant_variety ADD COLUMN major_category_code VARCHAR(12);

UPDATE public.plant_variety variety
SET major_category_code = major_category.category_code
FROM public.plant_variety_major_category major_category
WHERE variety.major_category_name = major_category.category_name;

ALTER TABLE public.plant_variety ALTER COLUMN major_category_code SET NOT NULL;

ALTER TABLE public.plant_variety
DROP COLUMN major_category_name,
DROP COLUMN middle_category_name;
