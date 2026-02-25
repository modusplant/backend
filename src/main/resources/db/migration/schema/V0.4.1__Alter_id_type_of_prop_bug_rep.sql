-- 있던 prop_bug_rep 레코드는 깔끔하게 제거
TRUNCATE TABLE public.prop_bug_rep;

-- PK 필드명 변경
ALTER TABLE public.prop_bug_rep
    RENAME COLUMN uuid TO ulid;

-- PK 필드 타입 변경
ALTER TABLE public.prop_bug_rep
    ALTER COLUMN ulid TYPE character varying(26)
    USING ulid::text;

-- PK 제약 조건 추가
ALTER TABLE ONLY public.prop_bug_rep
    ADD CONSTRAINT "PK_PROP_BUG_REP" PRIMARY KEY (ulid);