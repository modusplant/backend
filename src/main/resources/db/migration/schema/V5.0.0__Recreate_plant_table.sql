-- 1. 기존 plant 테이블 제거
DROP TABLE public.plant;

-- 2. 새로 plant 테이블 생성
CREATE TABLE public.plant (
    id integer PRIMARY KEY,
    scientific_name_id integer NOT NULL,
    scientific_name VARCHAR(255) NOT NULL,
    korean_name VARCHAR(64) NOT NULL,
    family_scientific_name VARCHAR(64) NOT NULL,
    family_korean_name VARCHAR(64) NOT NULL,
    genus_scientific_name VARCHAR(64) NOT NULL,
    genus_korean_name VARCHAR(64),
    species_name VARCHAR(64),
    subspecies_name VARCHAR(64),
    resource_type VARCHAR(6) NOT NULL
);