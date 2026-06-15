-- 현재 시각을 기준으로 created_at과 updated_at 컬럼 데이터에 디폴트 값 입력
-- V5.0.1 Flyway 마이그레이션 파일이 무사히 동작하기 위해서는 디폴트 값을 제거해서는 안 된다.
ALTER TABLE public.plant
ADD COLUMN created_at timestamp without time zone NOT NULL DEFAULT NOW(),
ADD COLUMN updated_at timestamp without time zone NOT NULL DEFAULT NOW();