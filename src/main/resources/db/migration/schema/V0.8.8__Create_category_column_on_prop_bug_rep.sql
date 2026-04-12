-- 1. NULL로 category 컬럼 생성
ALTER TABLE public.prop_bug_rep
    ADD COLUMN category varchar(10);

-- 2. 기존 prop_bug_rep 행의 category를 PROPOSAL로 일괄 변경
UPDATE public.prop_bug_rep
    SET category = 'PROPOSAL';

-- 2. category를 NOT NULL로 변경
ALTER TABLE public.prop_bug_rep
    ALTER COLUMN category SET NOT NULL;