-- CSV 기반 plant_variety와 구분하여, 국내에서 널리 유통되나 CSV에 없는 품종명을 수동으로 관리하기 위한 테이블
-- plant_variety와 완전히 같은 스키마로 생성 (V5.2.0, V5.2.2, V7.1.0 반영 후 기준)
CREATE TABLE public.plant_variety_addition (
    variety_code         VARCHAR(12) PRIMARY KEY,
    variety_name         VARCHAR(24) NOT NULL UNIQUE,
    middle_category_code VARCHAR(12) NOT NULL,
    created_at           timestamp without time zone NOT NULL DEFAULT NOW(),
    updated_at           timestamp without time zone NOT NULL DEFAULT NOW(),
    major_category_code  VARCHAR(12) NOT NULL
);
