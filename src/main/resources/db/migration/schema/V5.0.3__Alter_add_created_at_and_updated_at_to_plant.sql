-- 현재 시각을 기준으로 created_at과 updated_at 컬럼 데이터에 디폴트 값 입력
ALTER TABLE public.plant
ADD COLUMN created_at timestamp without time zone NOT NULL DEFAULT NOW(),
ADD COLUMN updated_at timestamp without time zone NOT NULL DEFAULT NOW();

-- 디폴트 설정 삭제
ALTER TABLE public.plant ALTER COLUMN created_at DROP DEFAULT;
ALTER TABLE public.plant ALTER COLUMN updated_at DROP DEFAULT;